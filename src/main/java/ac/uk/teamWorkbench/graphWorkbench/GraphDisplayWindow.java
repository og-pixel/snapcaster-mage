package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import javax.swing.*;

public class GraphDisplayWindow {

    private JPanel content;
    private GraphPanel graphPanel;
    private Project project;
    private ToolWindow toolWindow;

    public GraphDisplayWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        GraphPanel graphPanel = getGraphPanel(project, toolWindow);
        JButton refresh = new JButton("Refresh");
        advancedRefreshAction(refresh);
        content.add(refresh);
        content.add(graphPanel);
    }

    @NotNull
    private GraphPanel getGraphPanel(Project project, ToolWindow toolWindow) {
        graphPanel = new GraphPanel(project, toolWindow);
        graphPanel.build();
        JBScrollPane jScrollPane = new JBScrollPane(graphPanel);
        content.add(jScrollPane, new GridConstraints());
    }

    public JPanel getContent() {
        return content;
    }

}
