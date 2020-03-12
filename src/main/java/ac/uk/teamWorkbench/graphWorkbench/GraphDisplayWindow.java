package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class GraphDisplayWindow {

    private JPanel content;
    private GraphPanel graphPanel;
    private Project project;

    public GraphDisplayWindow(Project project) {
        this.project = project;
        GraphPanel graphPanel = getGraphPanel(project);
        JButton refresh = new JButton("Refresh");
        advancedRefreshAction(refresh);
        content.add(refresh);
        content.add(graphPanel);
    }

    @NotNull
    private GraphPanel getGraphPanel(Project project) {
        graphPanel = new GraphPanel(project);
        graphPanel.build();
        return graphPanel;
    }

    /**
     * Simply removes and adds the graphPane back.
     * @param refresh
     */
    private void basicRefreshAction(JButton refresh) {
        refresh.addActionListener(e -> {
            content.remove(graphPanel);
            content.add(getGraphPanel(project));
        });
    }

    private void advancedRefreshAction(JButton refresh) {
        refresh.addActionListener(e -> {
            graphPanel.removeGraph();
            graphPanel.addGraph();
            graphPanel.updateUI();
        });
    }

    public JPanel getContent() {
        return content;
    }

}
