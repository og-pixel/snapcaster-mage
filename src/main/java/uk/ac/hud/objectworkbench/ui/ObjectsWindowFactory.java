package uk.ac.hud.objectworkbench.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class ObjectsWindowFactory implements ToolWindowFactory {
    @java.lang.Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ObjectDisplayWindow objectDisplayWindow = new ObjectDisplayWindow(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(objectDisplayWindow.getContent(),project.getName(),false);
        toolWindow.getContentManager().addContent(content);
    }
}
