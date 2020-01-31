package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

public class GraphDisplayWindow {

    private Project project;
    private ToolWindow toolwindow;

    private JPanel content;
    private JLabel text;
    private JButton button1;

    public GraphDisplayWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolwindow = toolWindow;
    }

    public JPanel getContent() {
        return content;
    }

}
