package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URLClassLoader;
import java.util.Map;

/**
 * Author: Milosz Jakubanis
 * Class displaying a window with all compiled classes
 * and its available methods and variables
 */
public class ObjectCreationWindow extends DialogWrapper {

    //Window Creation Controller
    private ObjectCreationController controller = new ObjectCreationController();

    //Pane items
    private JPanel content;
    private JLabel text;

    //Lists displaying Classes information
    private JBList<String> classListJBList;
    private JBList<String> methodListJBList;
    private JBList<String> variableListJBList;

    //Default list models for JBList to add data into
    private DefaultListModel<String> javaClassListModel;
    private DefaultListModel<String> javaMethodsListModel;
    private DefaultListModel<String> javaVariablesListModel;

    //Loads classes, needs to be instantiated with URL source of class files.
    private URLClassLoader classLoader;

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
    }

    /**
     * Constructor
     */
    protected ObjectCreationWindow(boolean canBeParent) {
        super(canBeParent);
        init();
        setTitle("Instantiate Object");

        controller.findProjectClasses();
        populateClassList();
        addListeners();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }

    /**
     * Add Event Listeners
     */
    private void addListeners() {
        //TODO works nicely, but twice for some reason
        classListJBList.addListSelectionListener(e -> {
            if (classListJBList.getSelectedValue() != null) {
                populateMethodList(classListJBList.getSelectedValue());
                populateVariableList(classListJBList.getSelectedValue());
            }
        });
    }

    private void populateClassList() {
        Map<String, ClassReflection> projectClassList = controller.getProjectClassList();
        javaClassListModel.clear();
        for (Map.Entry<String, ClassReflection> entry : projectClassList.entrySet()) {
            javaClassListModel.addElement(entry.getValue().getClassName());
        }
    }

    private void populateMethodList(String key) {
        Map<String, ClassReflection> projectClassList = controller.getProjectClassList();
        javaMethodsListModel.clear();
        javaMethodsListModel.addAll(projectClassList.get(key).getMethodList());
    }

    private void populateVariableList(String key) {
        Map<String, ClassReflection> projectClassList = controller.getProjectClassList();
        javaVariablesListModel.clear();
        javaVariablesListModel.addAll(projectClassList.get(key).getVariableList());
    }
}
