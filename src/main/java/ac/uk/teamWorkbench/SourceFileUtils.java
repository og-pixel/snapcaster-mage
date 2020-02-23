package ac.uk.teamWorkbench;
/*
 * @Author: Matt, Milosz
 */

import ac.uk.teamWorkbench.exception.NotInstantiatedException;
import com.intellij.compiler.CompilerConfiguration;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeExtensionFactory;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.BuildTarget;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

//Utility class for querying the java source files in the project tree.
public class SourceFileUtils {

    //TODO delete
    @Deprecated
    private static final String extension = "java";

    private static SourceFileUtils instance = null;
    private static boolean isInstantiated = false;

    //Information useful for the entire project
    private Project project;
    private ToolWindow toolWindow;
    //Root of the project
    private VirtualFile projectRoot;
    //Folder inside root where compiled files are
    private VirtualFile objectWorkbenchDirectory;

    private PsiDirectoryFactory psiDirectoryFactory;
    private PsiFileFactory psiFileFactory;

    //Singleton
    private SourceFileUtils(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        this.projectRoot = ModuleRootManager.getInstance(
                ModuleManager.getInstance(project).getModules()[0]).getContentRoots()[0];
        isInstantiated = true;
    }

    /*
     * Ensures that the object has been instantiated only once.
     */
    public static SourceFileUtils instantiateObject(Project project, ToolWindow toolWindow) {
        if (instance == null) instance = new SourceFileUtils(project, toolWindow);
        return instance;
    }

    public static SourceFileUtils getInstance() throws NotInstantiatedException {
        if (isInstantiated) return instance;
        else throw new NotInstantiatedException("SourceFileUtils needs to be instantiated at least once." +
                "\nUse instantiateObject() method.");
    }

    //TODO remove if nobody uses it, version below is more flexible and searches in correct project scope
    @Deprecated
    public static Collection<VirtualFile> getAllFilesByExt(Project project) {
        return FilenameIndex.getAllFilesByExt(project, extension);
    }

    public Collection<VirtualFile> getAllFilesInProject(String extension) {
        return FilenameIndex.getAllFilesByExt(project, extension,
                GlobalSearchScope.projectScope(project));
    }

    public void createCompiledFilesFolder(Project project) throws IncorrectOperationException {
        psiDirectoryFactory = PsiDirectoryFactory.getInstance(project);
        //TODO found on internet, I need to test it
        PsiDirectory psiDirectory = psiDirectoryFactory.createDirectory(projectRoot);
        try{
            psiDirectory.checkCreateSubdirectory("ObjectWorkbench");
            objectWorkbenchDirectory = psiDirectory.createSubdirectory("ObjectWorkbench").getVirtualFile();
        }catch (IncorrectOperationException e){
            //Ignore for now
        }
        testCompileJavaFilesToObjectWorkbench();
    }

    //TODO this method needs to compile files and put them inside our compilation directory
    public void testCompileJavaFilesToObjectWorkbench() {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            objectWorkbenchDirectory.createChildData(null, "test.java");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //Getters
    public Project getProject() {
        return project;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public VirtualFile getProjectRoot() {
        return projectRoot;
    }
}
