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

    /**
     * Gets the content window of the form
     * @return JPanel contentWindow - the window
     */
    public JPanel getContentWindow() {
        return contentWindow;
    }

    /**
     * Adds a new tab to the tabbed pane
     */
    private void addTabbedPaneListener() {
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    int tabIndex = getSelectedTabIndex();
                    String tabTitle = getSelectedTabTitle(tabIndex);
                    if (isValidAddRequest(tabTitle)) {
                        removeTab(tabIndex);
                        addTab(tabIndex);
                        addNewTabButton();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Checks the addTab request is to a valid tab
     * @param tabTitle
     * @return
     */
    private boolean isValidAddRequest(String tabTitle) {
        if(!tabTitle.equals("+")) { return false; }
        return true;
    }

    /**
     *
     * @param tabPosition - position to add tab.
     * @param title - the label of the tab.
     */
    private void addTab(int tabPosition, String title){
        tabbedPane.addTab(title, new JPanel());
    }

    /**
     * Adds a new tab
     * @param tabPosition
     */
    private void addTab(int tabPosition) {
        tabbedPane.addTab("Untitled", new JPanel());
    };

    /**
     * @param tabPosition - the position of the tab to remove
     * Remove a tabbed pane.
     */
    private void removeTab(int tabPosition) {
        tabbedPane.removeTabAt(tabPosition);
    }

    /**
     * Get the selected tabbed pane
     * @return int tabbedPaneIndex
     */
    private int getSelectedTabIndex() {
        return tabbedPane.getSelectedIndex();
    }

    private String getSelectedTabTitle(int tabIndex) {
        return tabbedPane.getTitleAt(tabIndex);
    }

    /**
     * Adds a new tab with the + icon allowing users to add new tabs.
     */
    private void addNewTabButton() {
        tabbedPane.addTab("+", new JPanel());
    }

}