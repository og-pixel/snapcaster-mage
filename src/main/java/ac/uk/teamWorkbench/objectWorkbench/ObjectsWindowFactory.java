package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Milosz Jakubanis
 * Simple factory class creating a toolwindow Object Creation Window
 */
public class ObjectsWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ObjectDisplayWindow objectDisplayWindow = new ObjectDisplayWindow(toolWindow, project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(objectDisplayWindow.getContentWindow(),"",false);
        toolWindow.getContentManager().addContent(content);
    }
}