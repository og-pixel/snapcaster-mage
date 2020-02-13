package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;

import javax.swing.*;

public class GraphDisplayWindow {

    private JPanel content;

    public GraphDisplayWindow(Project project) {
        GraphPanel graphPanel = new GraphPanel(project);
        graphPanel.build();
        content.add(graphPanel, new GridConstraints());
    }

    public JPanel getContent() {
        return content;
    }

}
