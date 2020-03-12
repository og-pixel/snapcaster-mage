package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;

import javax.swing.*;

public class GraphDisplayWindow {

    private JPanel content;

    public GraphDisplayWindow(Project project) {
        GraphPanel graphPanel = new GraphPanel(project);
        graphPanel.build();
        JBScrollPane jScrollPane = new JBScrollPane(graphPanel);
        content.add(jScrollPane, new GridConstraints());
    }

    public JPanel getContent() {
        return content;
    }

}
