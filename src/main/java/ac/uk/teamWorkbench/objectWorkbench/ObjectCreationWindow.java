package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.ui.components.JBList;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.*;
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

    //TODO logic will go there
    //Window Creation Controller
    private ObjectCreationController controller = new ObjectCreationController();
    private Project project;

    //Pane items
    private JPanel content;
    private JLabel text;

    //Lists displaying Classes information
    private JBList classListJBList;
    private JBList methodListJBList;
    private JBList variableListJBList;

    //Default list models for JBList to add data into
    private DefaultListModel<String> javaClassListModel;
    private DefaultListModel<String> javaMethodsListModel;
    private DefaultListModel<String> javaVariablesListModel;

    //HashMap of classes in the Project
    // It should be fine with unique key as
    // classes should have unique names anyway
    // (IntelliJ disallows creation anyway)
    private Map<String, ClassReflection> projectClassList = new HashMap<>();

    private List<VirtualFile> javaClassFilesList;

    //Loads classes
    private URLClassLoader classLoader;
    private Class loadedClass = null;

    protected ObjectCreationWindow(boolean canBeParent, Project project) {
        super(canBeParent);
        this.project = project;
        init();
        setTitle("Object Creator");

        findProjectClasses();
        populateClassList();
        addListeners();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }

    /**
     * Instantiate GUI elements before constructor is called.
     * It makes sure JBList have default list models assigned to them.
     */
    private void createUIComponents() {
        javaClassListModel = new DefaultListModel<>();
        javaMethodsListModel = new DefaultListModel<>();
        javaVariablesListModel = new DefaultListModel<>();

        classListJBList = new JBList(javaClassListModel);
        methodListJBList = new JBList(javaMethodsListModel);
        variableListJBList = new JBList(javaVariablesListModel);
    }

    //TODO I need to extract what is in the constructor to
    // not repeat code
    private void findProjectClasses() {
        //Find all class files
        //TODO if I cant make it work again, look for modules instead
        javaClassFilesList = (ArrayList<VirtualFile>)
                SourceFileUtils.getAllFilesByExtensionsInLocalScope(project, "class");
        System.out.println(FilenameIndex.getAllFilesByExt(project, "class", GlobalSearchScope.projectScope(project)));

        //Instantiate loader and get all files
        File allFiles = new File(javaClassFilesList.get(0).getParent().getCanonicalPath());

        //Load classes into class loader
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{allFiles.toURI().toURL()});
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClassReflection classReflection;
        for (VirtualFile virtualFile : javaClassFilesList) {
            //TODO no error checking and it will override if class already exists
            classReflection = new ClassReflection(virtualFile.getNameWithoutExtension());
            String className = classReflection.getClassName();
            projectClassList.put(className, classReflection);

            //TODO I should split it into separate methods
            try {
                PsiFile psifile = PsiManager.getInstance(project).findFile(virtualFile);
                if (psifile instanceof PsiJavaFile) {
                    PsiJavaFile psiJavaFile = (PsiJavaFile) psifile;
                    String packageName = psiJavaFile.getPackageName();
                    System.out.println(packageName);
//                    PsiPackage pack = JavaPsiFacade.getInstance(project).findPackage(PackageName);
//                    ret.add(pack);
                }
                loadedClass = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                try{
                    //TODO this works, but obviously I need a way to find a full package name
                    loadedClass = classLoader.loadClass("Package.Boat");
                }catch (Exception e2){
                    e2.printStackTrace();
                }
                e.printStackTrace();
            }

//            methodList = getClassMethods(loadedClass);
//            variableList = getClassVariables(loadedClass);
            //TODO its all in nice one line, but is it readable?
            getClassMethods(loadedClass).forEach(methodName ->
                    projectClassList.get(className).addMethod(methodName));

            getClassVariables(loadedClass).forEach(methodName ->
                    projectClassList.get(className).addVariable(methodName));

            String parentClassName = loadedClass.getSuperclass().toGenericString();
            projectClassList.get(className).setParentClass(parentClassName);

        }
    }

    //TODO this will replace method of almost the same name
    private ArrayList<String> getClassMethods(Class loadedClass) {
        ArrayList<String> methodList = new ArrayList<>();
        Method[] methods = loadedClass.getDeclaredMethods();
        for (int i = 0; i < loadedClass.getDeclaredMethods().length; i++) {
            methodList.add(methods[i].getName());
        }
        return methodList;
    }

    /**
     * Extract variables from the Class by performing reflection
     *
     * @param loadedClass
     */
    private ArrayList<String> getClassVariables(Class loadedClass) {
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

    private void populateMethodList(Object object) {
        javaMethodsListModel.clear();
        javaMethodsListModel.addAll(projectClassList.get(object).getMethodList());
    }

    private void populateVariableList(Object object) {
        javaVariablesListModel.clear();
        javaVariablesListModel.addAll(projectClassList.get(object).getVariableList());
    }
}
