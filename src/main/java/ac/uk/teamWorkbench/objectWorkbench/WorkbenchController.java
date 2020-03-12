package ac.uk.teamWorkbench.objectWorkbench;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * WorkbenchController
 * <p>
 * The main controller for the ObjectWorkbench.
 * Handles logic and updates the GUI.
 */
public class WorkbenchController {

    private ObjectDisplayWindow GUI;
    private Validator validator;
    private DialogFactory dialogFactory;

    private ArrayList<String> protectedCharacters;

    private JPopupMenu rightClickMenu;
    private JMenuItem closer;
    private JMenuItem allCloser;
    private JMenuItem adder;
    private JMenuItem nameChanger;

    private ExecutionLoop executionLoop;

    public WorkbenchController(ObjectDisplayWindow GUI) {
        init();
        this.GUI = GUI;
    }

    private void init() {
        this.validator = new Validator(initProtectedCharacters());
        this.dialogFactory = new DialogFactory();
        this.rightClickMenu = new JPopupMenu();
        this.executionLoop = ExecutionLoop.getInstance();
    }

    /**
     * Initialise the protected characters list.
     */
    private ArrayList<String> initProtectedCharacters() {
        return this.protectedCharacters = new ArrayList<>(Arrays.asList("+", "-", "/", "="));
    }

    /**
     * Add a new Tab with specified title
     *
     * @param title - the label of the tab.
     */
    private void addTab(String title) {
        GUI.getTabbedPane().addTab(title, new JPanel());
        int newIndex = GUI.getTabbedPane().getTabCount() - 1;
        setSelectedTabIndex(newIndex);
    }

    /**
     * Adds a new untitled tab
     */
    private void addTab() {
        addTab("Untitled");
    }

    /**
     * @param tabPosition - the position of the tab to remove
     *                    Remove a tabbed pane.
     */
    private void removeTab(int tabPosition) {
        GUI.getTabbedPane().removeTabAt(tabPosition);
    }

    /**
     * Closes all tabs
     */
    private void removeAllTabs() {
        int i = 0;
        while (!GUI.getTabbedPane().getTitleAt(i).equals("+")) {
            GUI.getTabbedPane().removeTabAt(i);
        }
    }

    /**
     * Get the selected tabbed pane
     *
     * @return int tabbedPaneIndex
     */
    private int getSelectedTabIndex() {
        return GUI.getTabbedPane().getSelectedIndex();
    }

    /**
     * Set the selected tab index
     *
     * @param index - the index to set
     */
    public void setSelectedTabIndex(int index) {
        GUI.getTabbedPane().setSelectedIndex(index);
    }

    /**
     * Performs necessary tasks to create a new tab
     * If the ObjectCreationWindow tries to instantiate an object
     * add it to the ExecutionLoop
     *
     * @param index - index of the tab
     */
    private void newAddTabTask(int index) {
        //True if user pressed ok to create object
        ObjectCreationWindow objectCreationWindow = new ObjectCreationWindow(true);
        ObjectCreationController controller = objectCreationWindow.getController();
        ExecutionLoop executionLoop = ExecutionLoop.getInstance();


        String objectName;
        if (objectCreationWindow.showAndGet()) {
            removeTab(index);
            objectName = objectCreationWindow.getSelectedClassName();
            int selectedConstructorIndex = objectCreationWindow.getSelectedConstructor();

            executionLoop.instantiateObject(objectName, selectedConstructorIndex, parameters);
            addTab(objectName);
        } else {
            removeTab(index);
        }
        addNewTabButton();
    }

    /**
     * Appends a new tab to the end of the tab list
     */
    private void appendTabToEnd() {
        int count = GUI.getTabbedPane().getTabCount();
        for (int i = 0; i < count; i++) {
            if (GUI.getTabbedPane().getTitleAt(i).equals("+")) {
                newAddTabTask(i);
            }
        }
    }

    /**
     * Get selected tab title
     *
     * @param index - the tab index
     * @return title - the selected tabs title
     */
    private String getSelectedTabTitle(int index) {
        return GUI.getTabbedPane().getTitleAt(index);
    }

    /**
     * Set the title of a tabbed pane tab
     *
     * @param index - the index of the tab
     * @param title - the title to set
     */
    private void setTabTitle(int index, String title) {
        StringBuilder out = new StringBuilder();
        if (validator.hasProtectedCharacter(title) || validator.isProtectedCharacter(title)) {
            for (String item : protectedCharacters) {
                out.append("       - ").append("'").append(item).append("'").append("\n");
            }
            dialogFactory.displayWarningDialog(GUI.getTabbedPane(),
                    "Please ensure there are not protected characters. \n" +
                            "\nProtected characters include: \n" +
                            out, "Warning: Use of protected characters is prohibited.");
            return;
        }
        GUI.getTabbedPane().setTitleAt(index, title);
    }

    /**
     * Adds a new tab with the + icon allowing users to add new tabs.
     */
    private void addNewTabButton() {
        GUI.getTabbedPane().addTab("+", new JPanel());
    }

    /**
     * Populates rightClickMenu JPopupDialog
     */
    private void populateRightClickMenu() {
        addRightClickMenuItem(adder);
        this.rightClickMenu.addSeparator();
        addRightClickMenuItem(nameChanger);
        this.rightClickMenu.addSeparator();
        addRightClickMenuItem(allCloser);
        addRightClickMenuItem(closer);
    }

    /**
     * Adds a item to the rightClickMenu
     *
     * @param item - the menu item to add
     */
    public void addRightClickMenuItem(JMenuItem item) {
        this.rightClickMenu.add(item);
    }

    /**
     * Checks if a mouse event is a right click event
     *
     * @param e - the event to be checked
     * @return - true if the event is a right click event, else false
     */
    public boolean isRightClickEvent(MouseEvent e) {
        return SwingUtilities.isRightMouseButton(e);
    }

    /**
     * Creates a mousePressedListener to listen for mouse actions
     *
     * @param parent - the component to add the listener to
     */
    public void addMousePressedListener(Component parent) {
        parent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    if (isRightClickEvent(e)) {
                        updateRightClickMenu();
                        updateMenuItems();
                        populateRightClickMenu();
                        rightClickMenu.show(GUI.getTabbedPane(), e.getX(), e.getY());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    int tabIndex = getSelectedTabIndex();
                    if (validator.isValidAddRequest(getSelectedTabTitle(tabIndex))) {
                        newAddTabTask(tabIndex);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void addButtonListener(JButton executeButton, JButton compileButton) {
        executeButton.addActionListener(e -> {
            //TODO not finished
            new Thread(executionLoop).start();
//            executionLoop.startLoop();
        });

        compileButton.addActionListener(e -> {

            System.out.println("TODO");
        });

    }

    /**
     * Updates the right click JPopMenu
     */
    private void updateRightClickMenu() {
        this.rightClickMenu = new JPopupMenu();
    }

    /**
     * Updates the items in the right click JPopMenu
     */
    private void updateMenuItems() {
        this.nameChanger = new JMenuItem(new AbstractAction("Change Tab Name...") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String title = dialogFactory.changeTabNameDialog(
                        getSelectedTabTitle(getSelectedTabIndex()),
                        GUI.getTabbedPane());
                setTabTitle(getSelectedTabIndex(), title);
            }
        });

        this.adder = new JMenuItem(new AbstractAction("Add Tab...") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                appendTabToEnd();
            }
        });

        this.allCloser = new JMenuItem(new AbstractAction("Close All Tabs...") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (dialogFactory.getUserConfirmation(GUI.getTabbedPane(),
                        "Are you sure you want to do this? (This action cannot be undone)",
                        "Close All Tabs?") == 0) {
                    removeAllTabs();
                }
            }
        });

        this.closer = new JMenuItem(new AbstractAction("Close Tab...") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (dialogFactory.getUserConfirmation(GUI.getTabbedPane(),
                        "Are you sure you want to do this? (This action cannot be undone)",
                        "Close This Tab? - " + getSelectedTabTitle(getSelectedTabIndex())) == 0) {
                    removeTab(getSelectedTabIndex());
                }
            }
        });
    }
}
