package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

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
        return graphPanel;
    }

    /**
     * Simply removes and adds the graphPane back.
     * @param refresh - refreshes the graphPane.
     */
    private void basicRefreshAction(JButton refresh) {
        refresh.addActionListener(e -> {
            content.remove(graphPanel);
            content.add(getGraphPanel(project, toolWindow));
        });
    }

    /**
     * Refreshes the graph updating the GUI only (Faster than basic refresh).
     * @param refresh - refreshes the graph content
     */
    private void advancedRefreshAction(JButton refresh) {
        refresh.addActionListener(e -> {
            try {
                graphPanel.removeGraph();
                graphPanel.addGraph();
                graphPanel.updateUI();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public JPanel getContent() {
        return content;
    }

}
