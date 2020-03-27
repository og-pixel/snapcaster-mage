package ac.uk.teamWorkbench.objectWorkbench.objectDisplay;

import ac.uk.teamWorkbench.objectWorkbench.objectCreation.ObjectCreationController;
import ac.uk.teamWorkbench.objectWorkbench.objectCreation.ObjectCreationWindow;
import ac.uk.teamWorkbench.workbenchRuntime.ClassReflection;
import ac.uk.teamWorkbench.workbenchRuntime.ExecutionLoop;
import ac.uk.teamWorkbench.workbenchRuntime.ObjectCreator;
import ac.uk.teamWorkbench.workbenchRuntime.ObjectPool;
import com.sun.xml.bind.v2.TODO;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WorkbenchController
 * <p>
 * The main controller for the ObjectWorkbench.
 * Handles logic and updates the GUI.
 */
public class WorkbenchController {

    private static final Logger LOGGER = Logger.getLogger(WorkbenchController.class.getName());

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
    private Thread executionLoopThread;

    private LeftPane leftPane;
    private RightPane rightPane;

    public WorkbenchController(ObjectDisplayWindow GUI) {
        init();
        this.GUI = GUI;
    }

    private void init() {
        this.validator = new Validator(initProtectedCharacters());
        this.dialogFactory = new DialogFactory();
        this.rightClickMenu = new JPopupMenu();
        this.executionLoop = ExecutionLoop.getInstance();
        this.leftPane = LeftPane.getInstance();
        this.rightPane = RightPane.getInstance();
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
        ObjectCreationController controller = (ObjectCreationController) objectCreationWindow.getController();

        String className = "";
        Object[] parameters = new Object[0];
        boolean isInstantiated = false;

        if (objectCreationWindow.showAndGet()) {
            removeTab(index);
            className = objectCreationWindow.getSelectedClassName();
            int selectedConstructorIndex = objectCreationWindow.getSelectedConstructor();

            List<JTextField> fields = controller.getMapConstructorParameters().get(selectedConstructorIndex);
            parameters = new Object[fields.size()];

            for (int i = 0; i < fields.size(); i++) {
                parameters[i] = fields.get(i).getText();
            }

            if (executionLoop.instantiateObject(className, selectedConstructorIndex, parameters)) {
                addTab(className);
                isInstantiated = true;
            }
        } else {
            removeTab(index);
        }

        if(isInstantiated){
            ///////////// Handles the left panel of the split pane window functionality ///////////

            //Retrieve list of parameters types required by constructor of the created object
            Class<?>[] paramTypes = executionLoop.getParamTypeList();
            //Draw JLabels containing object variables and data types onto JPanel on the a UI and returns it
            JPanel leftPanel = leftPane.drawLabels(parameters, paramTypes);
            //Store the JLabel for later reference
            leftPane.addPanel(leftPanel);

            //Clear the left component of the JSplitPane and add leftPanel as a new component
            updateLeftPanel(leftPanel);

            /////////  Handles the right panel of the split pane window functionality  ///////////////

            //Get Map of all classes that can be reflected in the project
            Map<String, ClassReflection> classReflectionMap = ObjectPool.getInstance().getClassReflectionMap();
            //Find the class by its name and retrieve the methods attached to it
            ClassReflection classReflection = classReflectionMap.get(className);
            Class<?> clazz = classReflection.getClazz();
            Method[] methods = clazz.getMethods();

            JPanel panel = rightPane.drawButtons(methods);
            GUI.getSplitPane().remove(GUI.getSplitPane().getRightComponent());
            GUI.getSplitPane().setRightComponent(panel);
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

    private void updateLeftPanel(JPanel panel){
        GUI.getSplitPane().remove(GUI.getSplitPane().getLeftComponent());
        GUI.getSplitPane().setLeftComponent(panel);
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
                    //If tab name is equal to '+'
                    if (validator.isValidAddRequest(getSelectedTabTitle(tabIndex))) {
                        newAddTabTask(tabIndex);
                    }
                    else{
                    //Otherwise update the split screen left window
                        updateLeftPanel(leftPane.getPanel(tabIndex));
                        //TODO - Implement functionality for right panel to be updated
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void addButtonListener(JButton executeButton, JButton compileButton) {
        if(executionLoopThread == null) executionLoopThread = new Thread(executionLoop);

        executeButton.addActionListener(e -> {
            if(!executionLoopThread.isAlive()) executionLoopThread.start();
        });

        compileButton.addActionListener(e -> System.out.println("TODO"));

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
                    //Remove all tabs on the JTabbedPane
                    removeAllTabs();

                    //Remove All JPanels stored
                    leftPane.removeAllPanels();

                    //Update the left UI panel
                    updateLeftPanel(new JPanel());

                    //TODO - clear the right panel


                }
            }
        });

        this.closer = new JMenuItem(new AbstractAction("Close Tab...") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (dialogFactory.getUserConfirmation(GUI.getTabbedPane(),
                        "Are you sure you want to do this? (This action cannot be undone)",
                        "Close This Tab? - " + getSelectedTabTitle(getSelectedTabIndex())) == 0) {

                    //Remove tab at selected index
                    removeTab(getSelectedTabIndex());
                    //Remove panel at selected index
                    leftPane.removePanel(getSelectedTabIndex());
                    //Clear the left UI panel
                    updateLeftPanel(new JPanel());

                    //TODO - Clear the right UI panel

                }
            }
        });
    }
}
