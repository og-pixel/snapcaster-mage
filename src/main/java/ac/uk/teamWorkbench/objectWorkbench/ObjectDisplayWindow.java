package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public ObjectDisplayWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        addTabbedPaneListener();

    }

    public JPanel getContentWindow() {
        return contentWindow;
    }

    public void addTabbedPaneListener(){
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                //Get the position and title of the tab that was selected
                int tabPosition = tabbedPane.getSelectedIndex();
                String tabTitle = tabbedPane.getTitleAt(tabPosition);

                if (tabTitle.equals("+")) {
                    //Add a new tab before the '+' tab
                    addTab(tabPosition);
                }
            }
        });

    }

    public void addTab(int tabPosition){
        //Remove '+' tab from the tabbed pane
        tabbedPane.removeTabAt(tabPosition);
        //Add new tab to pane
        tabbedPane.addTab("Tab", new JPanel());
        //Re-add '+' tab to end of tabbed pane
        tabbedPane.addTab("+", new JPanel());
    }
}