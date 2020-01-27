package uk.ac.hud.objectworkbench.ui;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class ObjectDisplayWindow {
    private Project project;
    private JPanel object_display_panel;
    private JLabel Test_object_label;

    public ObjectDisplayWindow(Project project) {
        this.project = project;
    }

    public JComponent getContent() {
        Test_object_label.setText("Objects of "+project.getName() + " project.");
        return object_display_panel;
    }
}