import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.awt.RelativePoint;

import java.util.List;


public class Utils {

    private static String value = null;
    private static String name = null;

    /**
     * show notification
     *
     * @param project
     * @param type
     * @param text
     */
    public static void showNotification(Project project, MessageType type, String text) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)// show time length
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }


    /**
     * Get PsiClass with editor and PsiFile ,PsiClass just a class in Pugins dev
     *
     * @param editor
     * @param file
     * @return
     */
    public static PsiClass getTargetClass(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if(element == null) {
            return null;
        } else {
            PsiClass target = (PsiClass) PsiTreeUtil.getParentOfType(element, PsiClass.class);
            return target instanceof SyntheticElement ?null:target;
        }
    }


    /**
     * Get layout name from XML like :layout="@layout/sublayout2" />
     *
     * @param layout
     * @return
     */
    public static String getLayoutName(String layout) {
        if (layout == null || !layout.startsWith("@") || !layout.contains("/")) {
            return null;
        }
        String[] parts = layout.split("/");
        if (parts.length != 2) {
            return null;
        }
        return parts[1];
    }


    /**
     *
     * @param file 布局文件 activity_main
     * @param elements 解析出的标签元素
     * @return
     */
    public static List<Element> getIDsFromLayout(final PsiFile file, final List<Element> elements) {
        file.accept(new XmlRecursiveElementVisitor() {
            @Override
            public void visitElement(final PsiElement element) {
                super.visitElement(element);

                // 解析XML标签 <TextView /> 这样就是一个 tag 总之含有 <> 之类就可以认为是一个 tag
                if (element instanceof XmlTag) {
                    // 解析出所有的 tag
                    XmlTag tag = (XmlTag) element;
                    // xml 文件包含了 include 的情况下，迭代解析进去
                    if (tag.getName().equalsIgnoreCase("include")) {
                        XmlAttribute layout = tag.getAttribute("layout", null);
                        if (layout != null) {
                            Project project = file.getProject();
                            PsiFile include = null;
                            PsiFile[] mPsiFiles = FilenameIndex.getFilesByName(project, getLayoutName(layout.getValue())+".xml", GlobalSearchScope.allScope(project));

                            if (mPsiFiles.length>0){
                                include = mPsiFiles[0];
                            }
                            if (include != null) {
                                // 递归调用本来方法进行递归 xml 解析
                                getIDsFromLayout(include, elements);
                                return;
                            }
                        }
                    }

                    // 直接解析，不包含 include 情况下。 获得 对应 element 的 id
                    XmlAttribute id = tag.getAttribute("android:id", null);

                    if (id != null) {
                        value = id.getValue();
                        name = tag.getName();
                    }else{
                        return;
                    }


                    // 检查是否有自定义类 <com.allenwu.ExpendTextView/>
                    XmlAttribute clazz = tag.getAttribute("class", null);
                    if (clazz != null) {
                        name = clazz.getValue();
                    }

                    // name: TextView 、 id: tv_name 、 tag: <TextView .../>
                    try {
                        Element e = new Element(name, value, tag);
                        elements.add(e);
                    } catch (IllegalArgumentException e) {
                        // TODO log
                    }
                }
            }
        });
        return elements;
    }
}
