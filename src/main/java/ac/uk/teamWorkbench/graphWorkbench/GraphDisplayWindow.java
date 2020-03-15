package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;

public class GraphDisplayWindow {

    private JPanel content;

    public GraphDisplayWindow(Project project) {
        JBScrollPane graphPanel = getGraphPanel(project);
        JButton refresh = new JButton("Refresh");
        advancedRefreshAction(refresh);
        content.add(refresh);
        content.add(graphPanel);
    }

    private void advancedRefreshAction(JButton refresh) {
        refresh.addActionListener(e -> {

        });
    }

    private JBScrollPane getGraphPanel(Project project) {
        GraphPanel graphPanel = new GraphPanel(project);
        graphPanel.build();
        //        content.add(jScrollPane, new GridConstraints());
        return new JBScrollPane(graphPanel);
    }

    public JPanel getContent() {
        return content;
    }

}
