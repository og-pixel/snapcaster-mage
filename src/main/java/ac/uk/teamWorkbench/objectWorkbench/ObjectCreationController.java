package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ObjectCreationController {

    private URLClassLoader classLoader;
    private ObjectCreationWindow GUI;

    //HashMap of classes in the Project
    private Map<String, ClassReflection> projectClassList = new HashMap<>();

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

    private Map<String, VirtualFile> findCompiledClasses(VirtualFile[] virtualFile, Map<String, VirtualFile> list){
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
        }catch(ArrayIndexOutOfBoundsException indexException){
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
        }

        //Loop over all compiled classes and extract methods and variables and save them as String
        // in ObjectReflection Class
        ClassReflection classReflection;
        Class<?> loadedClass;
        for (Map.Entry<String, VirtualFile> entry : compiledClassesList.entrySet()) {
            String className = entry.getValue().getNameWithoutExtension();
            loadedClass = loadClass(entry.getValue(), className);
            classReflection = new ClassReflection(className, loadedClass);

            projectClassList.put(className, classReflection);
            getClassMethods(loadedClass).forEach(methodName ->
                    projectClassList.get(className).addMethod(methodName));
            getClassVariables(loadedClass).forEach(variableName ->
                    projectClassList.get(className).addVariable(variableName));
            getClassConstructor(loadedClass).forEach(constructorName ->
                    projectClassList.get(className).addConstructor(constructorName));
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
     * Add Event Listeners
     */
    void addListeners() {
        JBList<String> classList = GUI.getClassListJBList();
        //TODO works nicely, but twice for some reason
        classList.addListSelectionListener(e -> {
            if(!classList.getValueIsAdjusting() &&
                    classList.getSelectedValue() != null){

                populateMethodList(classList.getSelectedValue());
                populateVariableList(classList.getSelectedValue());
                populateConstructorList();
            }
        });
    }

        private void populateMethodList(String key) {
        DefaultListModel<String> javaMethodsListModel = GUI.getJavaMethodsListModel();
        Map<String, ClassReflection> projectClassList = getProjectClassList();

        javaMethodsListModel.clear();
        javaMethodsListModel.addAll(projectClassList.get(key).getMethodList());
    }

    private void populateVariableList(String key) {
        DefaultListModel<String> javaVariablesListModel = GUI.getJavaVariablesListModel();
        Map<String, ClassReflection> projectClassList = getProjectClassList();
        javaVariablesListModel.clear();
        javaVariablesListModel.addAll(projectClassList.get(key).getVariableList());
    }

    //TODO change
    private void populateConstructorList() {
        JTabbedPane constructorsTab = GUI.getConstructorsTabList();
        constructorsTab.removeAll();

        Map<String, ClassReflection> map = getProjectClassList();


        map.get(GUI.getSelectedClassName()).getConstructorList().forEach( e -> {
            Annotation[] aa = e.getDeclaredAnnotations();
            for (int i = 0; i < aa.length; i++) {
//                constructorsTab.addTab(aa[i].toString(), new JLabel());
                constructorsTab.addTab(aa[i].toString(), new JLabel());
            }
        });
    }

    void populateClassList() {
        Map<String, ClassReflection> projectClassList = getProjectClassList();
        GUI.getJavaClassListModel().clear();
        for (Map.Entry<String, ClassReflection> entry : projectClassList.entrySet()) {
            //TODO i am not sue of getClass
            GUI.getJavaClassListModel().addElement(entry.getValue().getClassName());
        }
    }

    /**
     * Extract methods from the Class by performing reflection
     * @param loadedClass Class name that methods are extracted from.
     * @return List of methods belonging to that class.
     */
    ArrayList<Method> getClassMethods(Class<?> loadedClass) {
        ArrayList<Method> methodList = new ArrayList<>();
        Method[] methods = loadedClass.getDeclaredMethods();
        for (int i = 0; i < loadedClass.getDeclaredMethods().length; i++) {
            methodList.add(methods[i]);
        }
        return methodList;
    }

    /**
     * Extract variables from the Class by performing reflection
     * @param loadedClass Class name that variables are extracted from.
     * @return List of variables belonging to that class.
     */
    ArrayList<Field> getClassVariables(Class<?> loadedClass) {
        ArrayList<Field> variableList = new ArrayList<>();
        Field[] fields = loadedClass.getDeclaredFields();
        for (int i = 0; i < loadedClass.getDeclaredFields().length; i++) {
            variableList.add(fields[i]);
        }
        return variableList;
    }

    //TODO description
    ArrayList<Constructor<?>> getClassConstructor(Class<?> loadedClass) {
        ArrayList<Constructor<?>> constructorList = new ArrayList<>();
        Constructor<?>[] constructors = loadedClass.getDeclaredConstructors();
        for (int i = 0; i < loadedClass.getDeclaredConstructors().length; i++) {
            constructorList.add(constructors[i]);
        }
        return constructorList;
    }

    public Class<?> loadSelectedClass(String className){
        VirtualFile projectRoot = null;
        try {
            projectRoot = SourceFileUtils.getInstance().getCompilerModule().get(0);
        }catch(ArrayIndexOutOfBoundsException indexException){
            System.out.println(indexException.getMessage());
            System.exit(1);
        }
        Map<String, VirtualFile> compiledClassesList;
        compiledClassesList = findCompiledClasses(projectRoot);

        return loadClass(compiledClassesList.get(className), className);
    }

    Map<String, ClassReflection> getProjectClassList() {
        return projectClassList;
    }
}
