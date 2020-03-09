package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;

public class ObjectCreationController {

    private ObjectCreationWindow GUI;
    private ObjectPool objectPool;

    //HashMap of classes in the Project
    private Map<String, ClassReflection> classReflectionMap;

    /**
     * Constructor
     * @param GUI window class part that is passed to this controller
     */
    public ObjectCreationController(ObjectCreationWindow GUI) {
        this.GUI = GUI;
        this.objectPool = ObjectPool.getInstance();
        this.classReflectionMap = objectPool.getClassReflectionMap();
    }

    /**
     * Add Event Listeners
     */
    void addListeners() {
        JBList<String> classList = GUI.getClassListJBList();
        classList.addListSelectionListener(e -> {
            if (!classList.getValueIsAdjusting() &&
                    classList.getSelectedValue() != null) {

                populateMethodList(classList.getSelectedValue());
                populateVariableList(classList.getSelectedValue());
                populateConstructorList();
            }
        });
    }

    private void populateMethodList(String key) {
        DefaultListModel<String> javaMethodsListModel = GUI.getJavaMethodsListModel();
        javaMethodsListModel.clear();
        javaMethodsListModel.addAll(classReflectionMap.get(key).getMethodListAsText());
    }

    private void populateVariableList(String key) {
        DefaultListModel<String> javaVariablesListModel = GUI.getJavaVariablesListModel();
        javaVariablesListModel.clear();
        javaVariablesListModel.addAll(classReflectionMap.get(key).getVariableListAsText());
    }

    private void populateConstructorList() {
        //Get reference from GUI
        JTabbedPane constructorsTab = GUI.getConstructorsTabList();
        String className = GUI.getSelectedClassName();
        //Clear Tab
        constructorsTab.removeAll();
        //Get list of constructors as list
        List<String> constructorListAsText = classReflectionMap.get(className).getConstructorListAsText();
        for (int i = 0; i < constructorListAsText.size(); i++) {
            ArrayList<JTextField> arrayOfTextFields = new ArrayList<>();
            List<String> constructorParameters = classReflectionMap.get(className).getParameterListAsText(i);
            JPanel panel = createConstructorTab(arrayOfTextFields);

            createPanelElement(constructorParameters, panel, arrayOfTextFields);
            constructorsTab.addTab(constructorListAsText.get(i), panel);
        }
    }

    /**
     * Creates a constructor
     * @return
     */
    private JPanel createConstructorTab(List<JTextField> arrayOfTextFields){
        JPanel panel = new JPanel();
        FlowLayout layout = new FlowLayout();
        panel.setLayout(layout);
        panel.setPreferredSize(new Dimension(100, 400));
        JButton createObjectButton = new JButton("Create");
        panel.add(createObjectButton);
        addCreateObjectListener(createObjectButton, arrayOfTextFields);
        panel.setMaximumSize(new Dimension(100, 900));

        return panel;
    }

    private void createPanelElement(List<String> parameterList, JPanel panel, ArrayList<JTextField> textFieldList) {
        JLabel label;
        JTextField textField;
        for (String parameterName : parameterList) {
            label = new JLabel(parameterName);
            textField = new JTextField();
            textFieldList.add(textField);

            panel.add(textField);
            panel.add(label);
        }
    }

    void populateClassList() {
        DefaultListModel<String> javaListModel = GUI.getJavaClassListModel();
        javaListModel.clear();
        for (Map.Entry<String, ClassReflection> entry : classReflectionMap.entrySet()) {
            javaListModel.addElement(entry.getValue().getClassName());
        }
    }

    private void addCreateObjectListener(JButton button, List<JTextField> listParameters) {
        button.addActionListener(e -> {
            for (JTextField listParameter : listParameters) {
                System.out.println("Found: " + listParameter.getText());
            }
        });
    }
}
