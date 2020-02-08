package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.uiDesigner.core.GridConstraints;

import javax.swing.*;

public class GraphDisplayWindow {

    private Project project;
    private ToolWindow toolWindow;

    private JPanel content;
    private GraphPanel graphPanel;

    public GraphDisplayWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        graphPanel = new GraphPanel();
        graphPanel.build();
        graphPanel.setSize(toolWindow.getComponent().getSize());
        content.add(graphPanel, new GridConstraints());
    }

    public JPanel getContent() {
        graphPanel.setSize(toolWindow.getComponent().getSize());
        return content;
    }

}
