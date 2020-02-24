package ac.uk.teamWorkbench;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PopupAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        StringBuilder message = new StringBuilder("------");
        try {
            Collection<VirtualFile> collation;
//            collation = SourceFileUtils.getAllFilesByExtInProjectScope(e.getProject());
            collation = SourceFileUtils.getInstance().getAllFilesByExtInProjectScope("java");
            for (VirtualFile vf : collation) {
                message.append("getName(): ").append(vf.getName()).append("\n");
                message.append("getExtension(): ").append(vf.getExtension()).append("\n");
                message.append("getPresentableName(): ").append(vf.getPresentableName()).append("\n");
                message.append("getPresentableUrl(): ").append(vf.getPresentableUrl()).append("\n");
                message.append("getUrl(): ").append(vf.getUrl()).append("\n");
                message.append("getParent().getName(): ").append(vf.getParent().getName()).append("\n");        //returns package name
                message.append("getChildren[].getName():\n");                                                   //sub-package if file is a package
                for (VirtualFile child : vf.getChildren()) {
                    message.append(" - ").append(child.getName());
                }
                message.append("-------------------------\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            message.append(ex.toString());
        }
        Messages.showMessageDialog(message.toString(), "Popup Test Class", Messages.getInformationIcon());
    }
}
