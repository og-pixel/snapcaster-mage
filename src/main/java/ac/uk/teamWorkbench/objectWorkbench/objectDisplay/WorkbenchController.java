package ac.uk.teamWorkbench.objectWorkbench.objectDisplay;

import ac.uk.teamWorkbench.objectWorkbench.objectCreation.ObjectCreationController;
import ac.uk.teamWorkbench.objectWorkbench.objectCreation.ObjectCreationWindow;
import ac.uk.teamWorkbench.workbenchRuntime.ClassReflection;
import ac.uk.teamWorkbench.workbenchRuntime.ExecutionLoop;
import ac.uk.teamWorkbench.workbenchRuntime.ObjectPool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
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

        if(isInstantiated) {
            ///////////// Handles the left panel of the split pane window functionality ///////////

            //Retrieve list of parameters types required by constructor of the created object
            Class<?>[] paramTypes = executionLoop.getParamTypeList();
            //Draw JLabels containing object variables and data types onto a JPanel and returns it
            JPanel leftPanel = leftPane.drawLabels(parameters, paramTypes);
            //Store the JLabel for later reference
            leftPane.storePanel(leftPanel);

            //Clear the left component of the JSplitPane and add leftPanel as a new component
            updateLeftPanel(leftPanel);

            /////////  Handles the right panel of the split pane window functionality  ///////////////

            //Get a reference to a reflected class by class name
            Class<?> clazz = getClassReflection(className);
            //Retrieve the methods attached to the class;
            Method[] methods = clazz.getMethods();

            //Draw buttons containing method names on the panel and return it
            JPanel rightPanel = rightPane.drawButtons(methods);
            //Store panel for later reference
            rightPane.storePanel(rightPanel);

            //Clear the right component of the JSplitPane and add rightPanel as a new component
            updateRightPanel(rightPanel);
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

    private void updateRightPanel(JPanel panel){
        GUI.getSplitPane().remove(GUI.getSplitPane().getRightComponent());
        GUI.getSplitPane().setRightComponent(panel);
    }

    private Class<?> getClassReflection(String className){
        //Get Map of all classes that can be reflected in the project
        Map<String, ClassReflection> classReflectionMap = ObjectPool.getInstance().getClassReflectionMap();
        //Find the class by its name and retrieve the methods attached to it
        ClassReflection classReflection = classReflectionMap.get(className);
        Class<?> clazz = classReflection.getClazz();
        return clazz;
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
                    //Otherwise update the split screen left and right windows
                        updateLeftPanel(leftPane.getPanel(tabIndex));
                        updateRightPanel(rightPane.getPanel(tabIndex));
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

        rightPane.addButtonListener(new RightPaneListener() {
            @Override
            public void buttonEventOccurred(ActionEvent event) {
                //Get the panel currently being looked at
                JPanel panel = (JPanel) GUI.getSplitPane().getRightComponent();
                //Find out which button threw the event and get the text stored.
                for(int i = 0; i < panel.getComponents().length; i++){
                    if(event.getSource() == panel.getComponent(i)){
                        JButton button = (JButton) panel.getComponent(i);

                        //TODO method invocation requires:
                        // 1) Reference to Class<?> object using the class name, look at ClassReflection?
                        // 2) Method = Call to class object get declared methods, takes method name and param data types
                        // 3) Call to invoke, takes object <loaded object? and actual parameter values to pass

                        Integer result = 0;
                        //Get the created object by the tab index
                        Object object = executionLoop.getObject(GUI.getTabbedPane().getSelectedIndex());
                        String methodName = button.getText();
                        //Find the class name from the object
                        String className = object.getClass().getName();
                        //Get the class reference
                        Class<?> clazz = getClassReflection(className);

                        //Invoke the method
                        try{
                            //TODO program works if setMileage is the method selected
                            //Needs to be more generic to support any method
                            Method method = clazz.getMethod(methodName, Integer.class);
                            method.invoke(object, 94000);

                            Method getMethod = clazz.getMethod("getMileage");
                            result = (Integer) getMethod.invoke(object);
                            System.out.println(result.toString());
                        }
                        catch(NoSuchMethodException nsme){ nsme.getMessage(); } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }
            }
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
                    //Remove all tabs on the JTabbedPane
                    removeAllTabs();

                    //Get the divider location
                    int divLocation = GUI.getSplitPane().getDividerLocation();

                    //Remove All JPanels stored
                    leftPane.removeAllPanels();
                    rightPane.removeAllPanels();

                    //set divider location
                    GUI.getSplitPane().setDividerLocation(divLocation);

                    //Update the left and right UI display panels
                    updateLeftPanel(new JPanel());
                    updateRightPanel(new JPanel());


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

                    //Get the divider location
                    int divLocation = GUI.getSplitPane().getDividerLocation();

                    //Remove panels at selected index
                    leftPane.removePanel(getSelectedTabIndex());
                    rightPane.removePanel(getSelectedTabIndex());
                    //Clear the left and right UI panel
                    updateLeftPanel(new JPanel());
                    updateRightPanel(new JPanel());

                    //Set the divider location
                    GUI.getSplitPane().setDividerLocation(divLocation);

                }
            }
        });
    }
}
