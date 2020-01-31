package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ObjectDisplayWindow {

    private Project project;
    private ToolWindow toolWindow;

    private JPanel contentWindow;
    private JPanel leftPane;
    private JPanel rightPane;
    private JSplitPane splitPane;
    private JTabbedPane tabbedPane;

    private JLabel plus = new JLabel();

    private JDialog dialog;
    private JFrame dialogFrame;

    public ObjectDisplayWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        this.init();
    }

    private void init()
    {
        this.addLabelToTab();
    }

    private void addLabelToTab()
    {
        this.tabbedPane.addTab("+", this.plus);
    }

    private void onPlusTabClick()
    {
        plus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("CLick!");
            }
        });
    }


    public JPanel getContentWindow() {
        return contentWindow;
    }
}