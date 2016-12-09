import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.psi.xml.XmlFile;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allen on 2016/12/4.
 */
public class FindView extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前 project
        Project project = e.getProject();
        // 获取 editor
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        // 获取选中模式
        SelectionModel model = editor.getSelectionModel();
        // 获取选中内容
        final String selectedText = model.getSelectedText();

        if (TextUtils.isEmpty(selectedText)) {
            Utils.showNotification(project, MessageType.ERROR,"请选中生成内容");
            return;
        }

        // 通过 getFilesByName 来获取布局文件，获取 PsiFile，是这种语法情况下的 FIle 文件
        PsiFile[] mPsiFiles = FilenameIndex.getFilesByName(project, selectedText+".xml", GlobalSearchScope.allScope(project));
        if (mPsiFiles.length<=0){
            Utils.showNotification(project,MessageType.INFO,"所输入的布局文件没有找到!");
            return;
        }

        // 强转型至 XmlFile , 现在这个文件即布局文件
        XmlFile xmlFile =  (XmlFile) mPsiFiles[0];
        // 解析布局文件 Element 实体类，主要弄清楚其包含的字段
        List<Element> elements = new ArrayList<>();
        // 拿到带有 id 属性的字段
        Utils.getIDsFromLayout(xmlFile,elements);

        if (!elements.isEmpty()) {
            startWork(project, editor, elements);
        } else {
            Utils.showNotification(project, MessageType.ERROR, "在layout中没有ID被找到");
        }
    }

    private void startWork(Project project, Editor editor, List<Element> elements) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        new LayoutCreator(file, Utils.getTargetClass(editor, file), "Generate Injections", elements).execute();
        Utils.showNotification(project, MessageType.INFO, "文件生成成功");
    }
}
