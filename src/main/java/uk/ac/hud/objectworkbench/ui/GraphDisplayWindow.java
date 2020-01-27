package uk.ac.hud.objectworkbench.ui;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class GraphDisplayWindow {
    private Project project;
    private JPanel graph_display_field;
    private JLabel JlabelTextField;

    public GraphDisplayWindow(Project project) {
        this.project = project;
    }

    public JComponent getContent() {
        JlabelTextField.setText("graph for "+project.getName() + " project.");
        return graph_display_field;
    }
}
