package ac.uk.teamWorkbench.objectWorkbench;
/*
  Original Author Matthew Lavene
  Date: 27/02/2020

  Refactor: Luke McCann
  Date: 05/02/2020

  Functions added: Luke McCann
  Date: 05/02/2020 - 07/02/2020

  The GUI class for the ObjectWorkbench display window.
 */

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ObjectDisplayWindow {

    // Main pane items
    private JPanel contentWindow;
    private JPanel leftPane;
    private JPanel rightPane;
    private JSplitPane splitPane;
    private JTabbedPane tabbedPane;
    private ArrayList<String> protectedCharacters = new ArrayList<>();
    // Right-Click Menu items
    private JPopupMenu rightClickMenu;
    private JMenuItem closer;
    private JMenuItem allCloser;
    private JMenuItem adder;
    private JMenuItem nameChanger;
    // Project and Toolwindow elements
    private ToolWindow toolWindow;
    private Project project;

    public ObjectDisplayWindow(ToolWindow toolWindow, Project project) {
        // Project items
        addMousePressedListener();
        protectedCharacters.add("+");
        //TODO added by Milosz, constructor from factory needs to pass project as well, I've made it so
        // it saves the reference
        this.toolWindow = toolWindow;
        this.project = project;
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
                        nameChanger = new JMenuItem(new AbstractAction("Change Tab Name...") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                String title = changeTabNameDialog();
                                setTabTitle(getSelectedTabIndex(), title);
                            }
                        });
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
                                if(getUserConfirmation(tabbedPane,
                                        "Are you sure you want to do this? (This action cannot be undone)",
                                        "Close All Tabs?") != 0) { return; }
                                removeAllTabs();
                            }
                        });
                        // Close menu item
                        closer = new JMenuItem(new AbstractAction("Close Tab...") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                if(getUserConfirmation(tabbedPane,
                                        "Are you sure you want to do this? (This action cannot be undone)",
                                        "Close This Tab?") != 0) { return; }
                                removeTab(getSelectedTabIndex());
                            }
                        });
                        // Add menuitem to list
                        populateRightClickMenu();
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
     * Gets user confirmation - 0 = yes, 1 = no, 2 = cancel
     * @param parent - The parent component of the dialog
     * @param message - The message in the dialog
     * @param title - The title of the dialog
     * @return answer from the user
     */
    private int getUserConfirmation(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent,
                message, title,JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Dialog for changing the name of a tab
     * @return the new name of the tab (String)
     */
    private String changeTabNameDialog() {
        return (String)JOptionPane.showInputDialog(tabbedPane,
                "Enter New Title: ",
                "Title",
                JOptionPane.PLAIN_MESSAGE,
                null, null,
                getSelectedTabTitle(getSelectedTabIndex()));
    }

    /**
     * Displays a warning dialog
     * @param parent - the parent of the dialog
     * @param message - the message in the dialog
     * @param title - the title of the dialog
     */
    private void displayWarningMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
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
        //TODO added by Milosz, I think here a dialog window (Object Creation Window) needs to be added
        new ObjectCreationWindow(true, project).showAndGet();
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
        if(hasProtectedCharacter(title) || isProtectedCharacter(title)) {
            JOptionPane.showMessageDialog(tabbedPane,
                    "Please ensure there are no protected characters. \n" +
                    "\nProtected characters include:\n" +
                    "       - '+'", "Warning: Use of protected characters is prohibited.",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        tabbedPane.setTitleAt(index, title);
    }

    /**
     * Adds a new tab with the + icon allowing users to add new tabs.
     */
    private void addNewTabButton() {
        tabbedPane.addTab("+", new JPanel());
    }

    /**
     * Checks if a string contains a protected character
     * @param x - the character
     * @return true if the string contains a protected character, else false
     */
    private boolean hasProtectedCharacter(String x) {
        boolean result = false;
        for(String character : protectedCharacters) {
            if(x.contains(character)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Adds items to rightClickMenu JPopupDialog
     */
    private void populateRightClickMenu() {
        rightClickMenu.add(adder);
        rightClickMenu.addSeparator();
        rightClickMenu.add(nameChanger);
        rightClickMenu.addSeparator();
        rightClickMenu.add(allCloser);
        rightClickMenu.add(closer);
    }

    /**
     * Checks if a string is a protected character
     * @param x - the string to check
     * @return true if the character is protected, else false
     */
    private boolean isProtectedCharacter(String x) {
        return protectedCharacters.contains(x);
    }
}