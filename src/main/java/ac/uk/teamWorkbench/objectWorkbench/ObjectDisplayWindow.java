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
    private JPopupMenu rightClickMenu;
    private JMenuItem closer;
    private JMenuItem allCloser;
    private JMenuItem adder;

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
     * Adds mouse listeners to the tabbed pane
     */
    private void addMousePressedListener() {
        tabbedPane.addMouseListener(new MouseAdapter() {
            /**
             * Checks for mousePressed Events if right-click open menu
             * @param e - the event
             */
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    // Open menu on right mouse click
                    if(SwingUtilities.isRightMouseButton(e)) {
                        rightClickMenu = new JPopupMenu();

                        adder = new JMenuItem(new AbstractAction("Add Tab...")
                        {
                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                appendTabToEnd();
                            }
                        });

                        allCloser = new JMenuItem(new AbstractAction("Close All Tabs...")
                        {
                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                removeAllTabs();
                            }
                        });
                        // Close menu item
                        closer = new JMenuItem(new AbstractAction("Close Tab...") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                removeTab(getSelectedTabIndex());
                            }
                        });
                        // Add menuitem to list
                        rightClickMenu.add(adder);
                        rightClickMenu.addSeparator();
                        rightClickMenu.add(allCloser);
                        rightClickMenu.add(closer);
                        // Show menu item on list
                        rightClickMenu.show(tabbedPane, e.getX(), e.getY());
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    int tabIndex = getSelectedTabIndex();
                    String tabTitle = getSelectedTabTitle(tabIndex);
                    if(isValidAddRequest(tabTitle))
                    {
                        newAddTabTask(tabIndex);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    /**
     * Checks the addTab request is to a valid tab
     * @param tabTitle - title of tab
     * @return true if tabtitle is "+" else false
     */
    private boolean isValidAddRequest(String tabTitle) {
        return tabTitle.equals("+");
    }

    /**
     * Performs necessary tasks to create a new tab
     * @param index - index of the tab
     */
    private void newAddTabTask(int index) {
        removeTab(index);
        addTab();
        addNewTabButton();
    }

    /**
     * Appends a new tab to the end of the tab list
     */
    private void appendTabToEnd() {
        int count = tabbedPane.getTabCount();
        for(int i = 0; i < count; i++) {
            if(tabbedPane.getTitleAt(i).equals("+")) {
                tabbedPane.removeTabAt(i);
                addTab();
                addNewTabButton();
            }
        }
    }

    /**
     * Add a new Tab with specified title
     * @param title - the label of the tab.
     */
    private void addTab(String title){
        tabbedPane.addTab(title, new JPanel());
        int newIndex = tabbedPane.getTabCount()-1;
        setTabIndex(newIndex);
    }

    /**
     * Adds a new untitled tab
     */
    private void addTab() {
        tabbedPane.addTab("Untitled", new JPanel());
        int newIndex = tabbedPane.getTabCount()-1;
        setTabIndex(newIndex);
    }

    /**
     * @param tabPosition - the position of the tab to remove
     * Remove a tabbed pane.
     */
    private void removeTab(int tabPosition) {
        tabbedPane.removeTabAt(tabPosition);
    }

    /**
     * Closes all tabs
     */
    private void removeAllTabs() {
        int i = 0;
        while(!tabbedPane.getTitleAt(i).equals("+"))
        {
            tabbedPane.removeTabAt(i);
        }
    }

    /**
     * Get the selected tabbed pane
     * @return int tabbedPaneIndex
     */
    private int getSelectedTabIndex() {
        return tabbedPane.getSelectedIndex();
    }

    /**
     * Set the selected tab index
     * @param index - the index to set
     */
    public void setTabIndex(int index) {
        tabbedPane.setSelectedIndex(index);
    }

    /**
     * Get selected tab title
     * @param index - the tab index
     * @return title - the selected tabs title
     */
    private String getSelectedTabTitle(int index) {
        return tabbedPane.getTitleAt(index);
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