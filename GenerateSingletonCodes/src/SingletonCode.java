import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.io.IOException;

/**
 * Created by allen on 2016/12/18.
 */
public class SingletonCode extends AnAction {

    private VirtualFile folder;
    private Project project;
    private String name;
    private GenerateType type;
    private static final String  SUFFIX=".java";
    private boolean isSourceFolder;
    private String packageName;

    @Override
    public void actionPerformed(AnActionEvent e) {

        folder=e.getData(PlatformDataKeys.VIRTUAL_FILE);
        project=e.getProject();
        // 显示对话框
        GenerateSingletonDialog sd=new GenerateSingletonDialog();
        sd.setSize(400, 150);
        sd.setLocationRelativeTo(null);
        sd.addCallback(callback);
        sd.setVisible(true);

    }
    private GenerateSingletonDialog.Callback callback=new GenerateSingletonDialog.Callback() {
        @Override
        public void showDialogResult(String name, GenerateType type) {
            SingletonCode.this.name=name;
            SingletonCode.this.type=type;
            ApplicationManager.getApplication().runWriteAction(getRunnableWrapper(runnable));
        }
    };
    protected Runnable getRunnableWrapper(final Runnable runnable) {
        return new Runnable() {
            @Override
            public void run() {
                if(project==null)
                    return ;
                //支持 undo 操作
                CommandProcessor.getInstance().executeCommand(project, runnable, " delete "+name+SUFFIX, ActionGroup.EMPTY_GROUP);//cut 是 undo 的描述 我应该填写类名
            }
        };
    }


    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(folder==null)
                return;
            try {
                VirtualFile writeableFile = folder.createChildData(this, name+SUFFIX);
                writeableFile.setBinaryContent(type.getBinaryContent(packageName,name));
                VirtualFileManager.getInstance().syncRefresh();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };


    @Override
    public void update(final AnActionEvent e) {

        VirtualFile operationFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        // 如果是 src,就显示该 action
        if (operationFile!=null) {
            VirtualFile[] vFiles = ProjectRootManager.getInstance(e.getProject()).getContentSourceRoots();
            checkSourceFolder(vFiles,operationFile);
            if (isSourceFolder)
                e.getPresentation().setIcon(IconLoader.getIcon("res/icon.jpg"));
            e.getPresentation().setVisible(isSourceFolder);//该action 的可见性
        }else
            e.getPresentation().setVisible(false);//

    }

    public void checkSourceFolder(VirtualFile[] sourceFiles,VirtualFile operationFile){
        isSourceFolder=false;
        for (VirtualFile sourceFile : sourceFiles) {
            if(operationFile.getPath().contains(sourceFile.getPath())) {
                isSourceFolder =operationFile.isDirectory();
                packageName = operationFile.getPath().replace(sourceFile.getPath(), "");
                packageName =packageName.replace("/",".");
                if(packageName.length()>0&&".".equals(packageName.substring(0,1)))
                    packageName=packageName.substring(1);
                else
                    packageName="";
                break;
            }
        }
    }
}
