package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

public class ObjectDisplayWindow {

    private Project project;
    private ToolWindow toolWindow;

    private JPanel contentWindow;
    private JPanel leftPane;
    private JPanel rightPane;
    private JSplitPane splitPane;
    private JTabbedPane tabbedPane;

    public ObjectDisplayWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
    }

    public JPanel getContentWindow() {
        return contentWindow;
    }
}