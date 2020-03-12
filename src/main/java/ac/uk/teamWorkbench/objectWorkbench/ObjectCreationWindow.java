package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Milosz Jakubanis
 * Class displaying a window with all compiled classes
 * and its available methods and variables
 */
public class ObjectCreationWindow extends DialogWrapper {

    //Window Creation Controller
    private ObjectCreationController controller;

    //Pane items
    private JPanel content;
    private JLabel title;

    //Lists displaying Classes information
    private JBList<String> classListJBList;
    private JBList<String> methodListJBList;
    private JBList<String> variableListJBList;
    private JTabbedPane constructorsTabList;

    //Default list models for JBList to add data into
    private DefaultListModel<String> javaClassListModel;
    private DefaultListModel<String> javaMethodsListModel;
    private DefaultListModel<String> javaVariablesListModel;

    //TODo this name might be wrong
//    private List<List<JTextField>> parametervalues;

    /**
     * Instantiate GUI elements before constructor is called.
     * It makes sure JBList have default list models assigned to them.
     */
    private void createUIComponents() {
        javaClassListModel = new DefaultListModel<>();
        javaMethodsListModel = new DefaultListModel<>();
        javaVariablesListModel = new DefaultListModel<>();

        classListJBList = new JBList<>(javaClassListModel);
        methodListJBList = new JBList<>(javaMethodsListModel);
        variableListJBList = new JBList<>(javaVariablesListModel);

//        parametervalues = new ArrayList<>();
    }

    /**
     * Constructor
     */
    protected ObjectCreationWindow(boolean canBeParent) {
        super(canBeParent);
        init();
        setTitle("Instantiate Object");
        controller = new ObjectCreationController(this);

        controller.populateClassList();
        controller.addListeners();
    }

    //Getters
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }

    public DefaultListModel<String> getJavaClassListModel() {
        return javaClassListModel;
    }

    public DefaultListModel<String> getJavaMethodsListModel() {
        return javaMethodsListModel;
    }

    public DefaultListModel<String> getJavaVariablesListModel() {
        return javaVariablesListModel;
    }

    public JBList<String> getClassListJBList() {
        return classListJBList;
    }

    public String getSelectedClassName() {
        return classListJBList.getSelectedValue();
    }

    public int getSelectedConstructor() {
        return constructorsTabList.getSelectedIndex();
    }

    public JTabbedPane getConstructorsTabList() {
        return constructorsTabList;
    }

    public ObjectCreationController getController() {
        return controller;
    }

//    public List<List<JTextField>> getParametervalues(){
//        return parametervalues;
//    }
}
