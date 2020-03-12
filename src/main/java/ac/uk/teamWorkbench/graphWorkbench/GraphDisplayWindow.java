package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphDisplayWindow {

    private JPanel content;
    private GraphPanel graphPanel;
    private Project project;

    public GraphDisplayWindow(Project project) {
        this.project = project;
        GraphPanel graphPanel = getGraphPanel(project);
        JButton refresh = new JButton("Refresh");
        refreshAction(refresh);
        content.add(refresh);
        content.add(graphPanel);
    }

    @NotNull
    private GraphPanel getGraphPanel(Project project) {
        graphPanel = new GraphPanel(project);
        graphPanel.build();
        return graphPanel;
    }

    private void refreshAction(JButton refresh) {
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                content.remove(graphPanel);
                content.add(getGraphPanel(project));
            }
        });
    }

    public JPanel getContent() {
        return content;
    }

}
