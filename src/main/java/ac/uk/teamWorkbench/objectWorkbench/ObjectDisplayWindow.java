package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        tabbedPane.addChangeListener(changeEvent -> {
            //Get the title of the tab that was selected
            int tabPosition = tabbedPane.getSelectedIndex();
            String tabTitle = tabbedPane.getTitleAt(tabPosition);

            if(tabTitle.equals("+")){
                //Open dialog window

                //Set tab position -1,

                //Add new tab
            }
            else{

            }

        });

    }
}