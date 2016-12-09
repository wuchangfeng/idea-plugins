import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.EverythingGlobalScope;

import java.util.Iterator;
import java.util.List;


public class LayoutCreator extends WriteCommandAction.Simple {

    protected PsiFile mFile;
    protected Project mProject;
    protected PsiClass mClass;
    protected List<Element> mElements;
    // https://github.com/JetBrains/intellij-community/blob/master/java/java-psi-api/src/com/intellij/psi/PsiElementFactory.java
    protected PsiElementFactory mFactory;

    // PsiFile just a file in plugins dev,PsiClass just a class
    public LayoutCreator(PsiFile file, PsiClass clazz, String command, List<Element> elements) {
        super(clazz.getProject(), command);
        mFile = file;
        mProject = clazz.getProject();
        mClass = clazz;
        mElements = elements;
        mFactory = JavaPsiFacade.getElementFactory(mProject);
    }


    @Override
    public void run() throws Throwable {
        generateFields();
        generateFindViewById();
        // reformat class
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        new ReformatCodeProcessor(mProject, mClass.getContainingFile(), null, false).runWithoutProgress();
    }


    /**
     *   generate string like : private TextView mtv;
     */
    protected void generateFields() {
        for (Iterator<Element> iterator = mElements.iterator(); iterator.hasNext(); ) {
            Element element = iterator.next();

            if (!element.used) {
                iterator.remove();
                continue;
            }
            // remove duplicate field
            PsiField[] fields = mClass.getFields();
            boolean duplicateField = false;

            for (PsiField field : fields) {
                String name = field.getName();
                if (name != null && name.equals(element.getFieldName())) {
                    duplicateField = true;
                    break;
                }
            }

            // 同上，删除重复声明的
            if (duplicateField) {
                iterator.remove();
                continue;
            }

            // private TextView aa_bb_cc;
            mClass.add(mFactory.createFieldFromText("/ private " + element.name + " " + element.getFieldName() + ";", mClass));
        }
    }

    /**
     *  generate s string like that after in initView():
     *  tv_name = (TextView)findViewById(R.id.tv_name)
     */
    protected void generateFindViewById() {
        PsiClass activityClass = JavaPsiFacade.getInstance(mProject).findClass(
                "android.app.Activity", new EverythingGlobalScope(mProject));
        PsiClass compatActivityClass = JavaPsiFacade.getInstance(mProject).findClass(
                "android.support.v7.app.AppCompatActivity", new EverythingGlobalScope(mProject));

        // Check for Activity class
        if ((activityClass != null && mClass.isInheritor(activityClass, true))
                || (compatActivityClass != null && mClass.isInheritor(compatActivityClass, true))
                || mClass.getName().contains("Activity")) {

            // 未找到 oncreate() 方法
            if (mClass.findMethodsByName("onCreate", false).length == 0) {
                // Add an empty stub of onCreate()
                StringBuilder method = new StringBuilder();
                method.append("@Override protected void onCreate(android.os.Bundle savedInstanceState) {\n");
                method.append("super.onCreate(savedInstanceState);\n");
                method.append("\t// TODO: add setContentView(...) and run LayoutCreator again\n");
                method.append("}");
                mClass.add(mFactory.createMethodFromText(method.toString(), mClass));
            } else {
                PsiStatement setContentViewStatement = null;
                boolean hasInitViewStatement = false;
                PsiMethod onCreate = mClass.findMethodsByName("onCreate", false)[0];
                for (PsiStatement statement : onCreate.getBody().getStatements()) {
                    // 查找到 setContentView() 方法
                    if (statement.getFirstChild() instanceof PsiMethodCallExpression) {
                        PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) statement.getFirstChild()).getMethodExpression();
                        if (methodExpression.getText().equals("setContentView")) {
                            setContentViewStatement = statement;
                        } else if (methodExpression.getText().equals("initView")) {
                            hasInitViewStatement = true;
                        }
                    }
                }
                // 如果有 setContentView() 方法声明，但是还没有 initView() 方法声明的话
                if(!hasInitViewStatement && setContentViewStatement != null) {

                    onCreate.getBody().addAfter(mFactory.createStatementFromText("initView();", mClass), setContentViewStatement);
                }
                // 看下面
                generatorLayoutCode();
            }
        }
    }


    // 在 initView() 中填写 findviewbyid 的 code
    private void generatorLayoutCode() {
        StringBuilder initView = new StringBuilder();
            initView.append("private void initView() {\n");

        for (Element element : mElements) {
            initView.append(element.getFieldName() + " = (" + element.name + ")findViewById(" + element.getFullID() + ");\n");
        }

        initView.append("}\n");
        // 插入生成的代码
        mClass.add(mFactory.createMethodFromText(initView.toString(), mClass));
    }
}