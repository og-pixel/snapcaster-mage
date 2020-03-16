package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

public class GraphDisplayWindow {

    private JPanel content;

    public GraphDisplayWindow(Project project, ToolWindow toolWindow) {
        JBScrollPane graphPanel = getGraphPanel(project, toolWindow);
        JButton refresh = new JButton("Refresh");
        advancedRefreshAction(refresh);
        content.add(refresh);
        content.add(graphPanel);
    }

    private void advancedRefreshAction(JButton refresh) {
        refresh.addActionListener(e -> {

        });
    }

    private JBScrollPane getGraphPanel(Project project, ToolWindow toolWindow) {
        GraphPanel graphPanel = new GraphPanel(project, toolWindow);
        graphPanel.build();
        //        content.add(jScrollPane, new GridConstraints());
        return new JBScrollPane(graphPanel);
    }

    public JPanel getContent() {
        return content;
    }

}
