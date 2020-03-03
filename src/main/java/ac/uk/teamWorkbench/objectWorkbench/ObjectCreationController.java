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

    private URLClassLoader classLoader;
    private ObjectCreationWindow GUI;

    //HashMap of classes in the Project
    private Map<String, ClassReflection> projectClassMap = new HashMap<>();


    /**
     * Constructor
     * @param GUI window class part that is passed to this controller
     */
    public ObjectCreationController(ObjectCreationWindow GUI) {
        this.GUI = GUI;
    }

    Map<String, VirtualFile> findCompiledClasses(VirtualFile root) {
        Map<String, VirtualFile> list = new HashMap<>();
        return findCompiledClasses(root.getChildren(), list);
    }

    private Map<String, VirtualFile> findCompiledClasses(VirtualFile[] virtualFile, Map<String, VirtualFile> list) {
        for (VirtualFile file : virtualFile) {
            if (file.isDirectory()) {
                findCompiledClasses(file.getChildren(), list);
            } else {
                list.put(file.getNameWithoutExtension(), file);
            }
        }
        return list;
    }

    /**
     * Finds all compiled classes from the root of the project and
     * populates the projectHashList
     * It does also needs to be a first project in; "Project Settings/Modules".
     */
    void findProjectClasses() {
        VirtualFile projectRoot;
        try {
            projectRoot = SourceFileUtils.getInstance().getCompilerModule().get(0);
        } catch (ArrayIndexOutOfBoundsException indexException) {
            System.out.println(indexException.getMessage());
            return;
        }

        File allFiles = new File(Objects.requireNonNull(projectRoot.getCanonicalPath()));

        Map<String, VirtualFile> compiledClassesList;
        compiledClassesList = findCompiledClasses(projectRoot);
        //Load classes into class loader
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{allFiles.toURI().toURL()});
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //Loop over all compiled classes and extract methods and variables and save them as String
        // in ObjectReflection Class
        ClassReflection classReflection;
        Class<?> loadedClass;
        for (Map.Entry<String, VirtualFile> entry : compiledClassesList.entrySet()) {
            String className = entry.getValue().getNameWithoutExtension();
            loadedClass = loadClass(entry.getValue(), className);
            classReflection = new ClassReflection(className, loadedClass);

            projectClassMap.put(className, classReflection);
            getClassMethods(loadedClass).forEach(methodName ->
                    projectClassMap.get(className).addMethod(methodName));
            getClassVariables(loadedClass).forEach(variableName ->
                    projectClassMap.get(className).addVariable(variableName));
            getClassConstructor(loadedClass).forEach(constructorName ->
                    projectClassMap.get(className).addConstructor(constructorName));
        }
    }

    Class<?> loadClass(VirtualFile virtualFile, String className) {
        PsiManager psiManager = SourceFileUtils.getInstance().getPsiManager();
        PsiFile psiFile;

        Class<?> loadedClass = null;
        String packageName;

        try {
            psiFile = psiManager.findFile(virtualFile);
            packageName = ((PsiJavaFile) psiFile).getPackageName();
            if (packageName.isEmpty()) {
                loadedClass = classLoader.loadClass(className);
            } else {
                loadedClass = classLoader.loadClass(packageName + "." + className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedClass;
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
        javaMethodsListModel.addAll(projectClassMap.get(key).getMethodListAsText());
    }

    private void populateVariableList(String key) {
        DefaultListModel<String> javaVariablesListModel = GUI.getJavaVariablesListModel();
        javaVariablesListModel.clear();
        javaVariablesListModel.addAll(projectClassMap.get(key).getVariableListAsText());
    }

    private void populateConstructorList() {
        //Get reference from GUI
        JTabbedPane constructorsTab = GUI.getConstructorsTabList();
        String className = GUI.getSelectedClassName();
        //Clear Tab
        constructorsTab.removeAll();
        //Get list of constructors as list
        List<String> constructorListAsText = projectClassMap.get(className).getConstructorListAsText();
        for (int i = 0; i < constructorListAsText.size(); i++) {
            ArrayList<JTextField> arrayOfTextFields = new ArrayList<>();
            List<String> constructorParameters = projectClassMap.get(className).getParameterListAsText(i);
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
        Map<String, ClassReflection> projectClassList = getProjectClassMap();
        DefaultListModel<String> javaListModel = GUI.getJavaClassListModel();
        javaListModel.clear();
        for (Map.Entry<String, ClassReflection> entry : projectClassList.entrySet()) {
            javaListModel.addElement(entry.getValue().getClassName());
        }
    }

    /**
     * Extract methods from the Class by performing reflection
     *
     * @param loadedClass Class name that methods are extracted from.
     * @return List of methods belonging to that class.
     */
    ArrayList<Method> getClassMethods(Class<?> loadedClass) {
        Method[] methods = loadedClass.getDeclaredMethods();
        return new ArrayList<>(Arrays.asList(methods));
    }

    /**
     * Extract variables from the Class by performing reflection
     *
     * @param loadedClass Class name that variables are extracted from.
     * @return List of variables belonging to that class.
     */
    ArrayList<Field> getClassVariables(Class<?> loadedClass) {
        Field[] fields = loadedClass.getDeclaredFields();
        return new ArrayList<>(Arrays.asList(fields));
    }

    /**
     * Extract available constructors from the Class by performing reflection
     *
     * @param loadedClass Class name that variables are extracted from.
     * @return List of constructors belonging to that class.
     */
    ArrayList<Constructor<?>> getClassConstructor(Class<?> loadedClass) {
        Constructor<?>[] constructors = loadedClass.getDeclaredConstructors();
        return new ArrayList<>(Arrays.asList(constructors));
    }

    public Class<?> loadSelectedClass(String className) {
        VirtualFile projectRoot = null;
        try {
            projectRoot = SourceFileUtils.getInstance().getCompilerModule().get(0);
        } catch (ArrayIndexOutOfBoundsException indexException) {
            System.out.println(indexException.getMessage());
            System.exit(1);
        }
        Map<String, VirtualFile> compiledClassesList;
        compiledClassesList = findCompiledClasses(projectRoot);

        return loadClass(compiledClassesList.get(className), className);
    }

    Map<String, ClassReflection> getProjectClassMap() {
        return projectClassMap;
    }

    private void addCreateObjectListener(JButton button, List<JTextField> listParameters) {
        button.addActionListener(e -> {
            for (JTextField listParameter : listParameters) {
                System.out.println("Found: " + listParameter.getText());
            }
        });
    }
}
