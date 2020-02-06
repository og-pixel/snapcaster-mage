package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.event.*;

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
//                    addTab(tabPosition);
                    addTab(tabbedPane.getTabCount() - 1);
//                    DialogBuilder db = new DialogBuilder();
//                    db.

                    new ObjectCreationWindow(true, project).showAndGet();
                }
            }
        });

        tabbedPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                   if(keyEvent.getKeyChar() == 'p'){
                       addTab(tabbedPane.getTabCount() - 1);
                   }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

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
        tabbedPane.setLocation(2,2);
    }
}