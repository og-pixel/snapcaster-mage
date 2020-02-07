package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBList;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.xml.ui.PsiClassPanel;
import gnu.trove.THashSet;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.net.*;
import java.util.*;

public class ObjectCreationWindow extends DialogWrapper {

    private JPanel content;
    private JLabel text;
    private JBList classListJBList;
    private JBList methodListJBList;

    // List for JList
    private DefaultListModel<String> javaClassListModel;
    private List<VirtualFile> javaFilesList;

    private Project project;

    private List<String> objectMethodList;
    private DefaultListModel<String> javaMethodsListModel;

    protected ObjectCreationWindow(boolean canBeParent, Project project) {
        super(canBeParent);
        init();
        this.project = project;
        setTitle("Object Creator");

        //TODO this needs to be maybe in some kind of update system or something along those lines
        javaFilesList = (ArrayList<VirtualFile>) FilenameIndex.getAllFilesByExt(project , "class",
                GlobalSearchScope.projectScope(project));

        ArrayList<String> classes = new ArrayList<>();
        //TODO delete
//        for (VirtualFile virtualFile : javaFilesList) {
//            System.out.println(virtualFile.getFileType());
//            System.out.println(virtualFile.getNameWithoutExtension());
//            System.out.println(virtualFile.getCanonicalFile());
//            System.out.println(virtualFile.getCanonicalPath());
//            System.out.println(virtualFile.getParent().getCanonicalPath());
//        }
        File file = new File(javaFilesList.get(1).getParent().getCanonicalPath());
        URLClassLoader loader;
        try {
             loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
             Class x = loader.loadClass(javaFilesList.get(1).getNameWithoutExtension());
             System.out.println(x.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        javaClassListModel.addAll(classes);
        classListJBList.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {

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
        javaMethodsListModel = new DefaultListModel<>();

        javaFilesList = new ArrayList<>();
        classListJBList = new JBList(javaClassListModel);
        //TODO add variable inside jblist
        objectMethodList = new ArrayList<>();
        methodListJBList = new JBList(javaMethodsListModel);
    }
}
