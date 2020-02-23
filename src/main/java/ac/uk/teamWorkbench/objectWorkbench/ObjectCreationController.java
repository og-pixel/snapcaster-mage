package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ObjectCreationController {

    private Project project;
    private URLClassLoader classLoader;
    private Class loadedClass;

    private List<VirtualFile> javaClassFilesList;

    public ObjectCreationController(){

    }

    public void findProjectClasses(){

    }

    private void getClassMethods(){

    }

    private void getClassVariables(){

    }

}
