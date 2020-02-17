package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Milosz Jakubanis
 * Class displaying a window with all compiled classes
 * and its available methods and variables
 */
public class ObjectCreationWindow extends DialogWrapper {

    //Pane items
    private JPanel content;
    private JLabel text;

    private JBList classListJBList;
    private JBList methodListJBList;

    //List for JList
    private List<VirtualFile> javaClassFilesList;

    //TODO these are for revision
    private Project project;
    private List<String> objectMethodList;
    private DefaultListModel<String> javaClassListModel;
    private DefaultListModel<String> javaMethodListModel;

    protected ObjectCreationWindow(boolean canBeParent, Project project) {
        super(canBeParent);
        init();
        setTitle("Object Creator");
        this.project = project;

        // Find all files by class extension in the project and get their path
        javaClassFilesList = (ArrayList<VirtualFile>)
                SourceFileUtils.getAllFilesByExtensionsInLocalScope(project, "class");

//        File allFiles = new File(javaClassFilesList.get(0).getParent().getCanonicalPath());
//        URLClassLoader loader;
//        try {
//            loader = URLClassLoader.newInstance(new URL[]{allFiles.toURI().toURL()});
//            Class loadedClass = loader.loadClass(javaClassFilesList.get(0).getNameWithoutExtension());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        for (VirtualFile virtualFile : javaClassFilesList) {
            javaClassListModel.addElement(virtualFile.getNameWithoutExtension());
        }

        //TODO works nicely, but twice for some reason
        classListJBList.addListSelectionListener(e -> {
            if(classListJBList.getSelectedValue() != null){
                javaMethodListModel.clear();
                ArrayList<String> methods = getClassMethods(classListJBList.getSelectedValue());
                //TODO null check
                javaMethodListModel.addAll(methods);
                System.out.println("selected: " + classListJBList.getSelectedValue());
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }

    private void createUIComponents() {
        javaClassListModel = new DefaultListModel<>();
        javaMethodListModel = new DefaultListModel<>();

        javaClassFilesList = new ArrayList<>();


        //TODO add variable inside jblist
        objectMethodList = new ArrayList<>();
        classListJBList = new JBList(javaClassListModel);
        methodListJBList = new JBList(javaMethodListModel);
    }

    //TODO this name is stupid and it should be in controller
    // This method checks if there are any class files that do not
    // exists as java files.
    private void checkForFakeClasses(){

    }


    //TODO return methods belonging to the class, used
    // by the event listener
    private ArrayList<String> getClassMethods(Object object){
        // Find all files by class extension in the project and get their path
        javaClassFilesList = (ArrayList<VirtualFile>)
                SourceFileUtils.getAllFilesByExtensionsInLocalScope(project, "class");

        File allFiles = new File(javaClassFilesList.get(0).getParent().getCanonicalPath());
        URLClassLoader loader;
        Class loadedClass = null;
        try {
            loader = URLClassLoader.newInstance(new URL[]{allFiles.toURI().toURL()});
            loadedClass = loader.loadClass(object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO this is kinda weird
        ArrayList<String> methodList = new ArrayList<>();
        Method[] methods = loadedClass.getDeclaredMethods();
        for (int i = 0; i < loadedClass.getDeclaredMethods().length; i++) {
            methodList.add(methods[i].getName());
        }
        System.out.println();
        return methodList;
    }

    //TODO I need to extract what is in the constructor to
    // not repeat code
    private void getProjectClasses(){

    }
}
