package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

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

    //HashMap of classes in the Project
    // It should be fine with unique key as
    // classes should have unique names anyway
    // (IntelliJ disallows creation anyway)
    private Map<String, ClassReflection> projectClassList = new HashMap<>();

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

        findProjectClasses();
        populateClassList();
        addListeners();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }

    private ArrayList<VirtualFile> findCompiledClasses(VirtualFile root) {
        ArrayList<VirtualFile> list = new ArrayList<>();
        return findCompiledClasses(root.getChildren(), list);
    }

    private ArrayList<VirtualFile> findCompiledClasses(VirtualFile[] virtualFile, ArrayList<VirtualFile> list) {
        for (VirtualFile file : virtualFile) {
            if (file.isDirectory()) {
                findCompiledClasses(file.getChildren(), list);
            } else {
                list.add(file);
            }
        }
        return list;
    }

    private void findProjectClasses() {

        VirtualFile projectRoot;

        try {
            projectRoot = SourceFileUtils.getInstance().getCompilerModule().get(0);
        }catch(ArrayIndexOutOfBoundsException indexException){
            System.out.println(indexException.getMessage());
            return;
        }

        File allFiles = new File(Objects.requireNonNull(projectRoot.getCanonicalPath()));
        List<VirtualFile> compiledClassesList;
        compiledClassesList = findCompiledClasses(projectRoot);

        //Load classes into class loader
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{allFiles.toURI().toURL()});
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Loop over all compiled classes and extract methods and variables and save them as String
        // in ObjectReflection Class
        ClassReflection classReflection;
        Class<?> loadedClass;
        for (VirtualFile virtualFile: compiledClassesList) {
            classReflection = new ClassReflection(virtualFile.getNameWithoutExtension());
            String className = classReflection.getClassName();
            projectClassList.put(className, classReflection);

            loadedClass = loadClass(virtualFile, className);

            getClassMethods(loadedClass).forEach(methodName ->
                    projectClassList.get(className).addMethod(methodName));

            getClassVariables(loadedClass).forEach(methodName ->
                    projectClassList.get(className).addVariable(methodName));

            //String parentClassName = loadedClass.getSuperclass().toGenericString();
            //projectClassList.get(className).setParentClass(parentClassName);

        }
    }

    private Class<?> loadClass(VirtualFile virtualFile, String className) {
        PsiManager psiManager = SourceFileUtils.getInstance().getPsiManager();
        PsiFile psiFile;

        Class<?> loadedClass = null;
        String packageName;

        try {
            psiFile = psiManager.findFile(virtualFile);
            packageName = ((PsiJavaFile) psiFile).getPackageName();

            if(packageName.isEmpty()){
                loadedClass = classLoader.loadClass(className);
            }else {
                loadedClass = classLoader.loadClass(packageName +  "." + className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedClass;
    }


    /**
     * Extract methods from the Class by performing reflection
     * @param loadedClass Class name that methods are extracted from.
     * @return List of methods belonging to that class.
     */
    private ArrayList<String> getClassMethods(Class<?> loadedClass) {
        ArrayList<String> methodList = new ArrayList<>();
        Method[] methods = loadedClass.getDeclaredMethods();
        for (int i = 0; i < loadedClass.getDeclaredMethods().length; i++) {
            methodList.add(methods[i].getName());
        }
        return methodList;
    }

    /**
     * Extract variables from the Class by performing reflection
     * @param loadedClass Class name that variables are extracted from.
     * @return List of variables belonging to that class.
     */
    private ArrayList<String> getClassVariables(Class<?> loadedClass) {
        ArrayList<String> variableList = new ArrayList<>();
        Field[] fields = loadedClass.getDeclaredFields();
        for (int i = 0; i < loadedClass.getDeclaredFields().length; i++) {
            variableList.add(fields[i].getName());
        }
        return variableList;
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
        javaClassListModel.clear();
        for (Map.Entry<String, ClassReflection> entry : projectClassList.entrySet()) {
            javaClassListModel.addElement(entry.getValue().getClassName());
        }
    }

    private void populateMethodList(String key) {
        javaMethodsListModel.clear();
        javaMethodsListModel.addAll(projectClassList.get(key).getMethodList());
    }

    private void populateVariableList(String key) {
        javaVariablesListModel.clear();
        javaVariablesListModel.addAll(projectClassList.get(key).getVariableList());
    }
}
