package ac.uk.teamWorkbench.objectWorkbench;

/**
 * Author Matthew Lavene
 * Date: 27/02/2020
 *
 * Refactor: Luke McCann
 * Date: 05/02/2020
 */

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
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
        addMousePressedListener();
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
    private void addMousePressedListener() {
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu rightClickMenu = new JPopupMenu();
                    JMenuItem closer = new JMenuItem(new AbstractAction("Close") {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            removeTab(getSelectedTabIndex());
                        }
                    });
                    rightClickMenu.add(closer);
                    rightClickMenu.show(tabbedPane, e.getX(), e.getY());
                }
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
     * Set the title of a tabbed pane tab
     * @param index - the index of the tab
     * @param title - the title to set
     */
    private void setTabTitle(int index, String title) {
        tabbedPane.setTitleAt(index, title);
    }

    /**
     * Adds a new tab with the + icon allowing users to add new tabs.
     */
    private void addNewTabButton() {
        tabbedPane.addTab("+", new JPanel());
    }

}