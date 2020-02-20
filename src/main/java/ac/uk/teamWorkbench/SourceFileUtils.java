package ac.uk.teamWorkbench;
/*
 * @Author: Matt, Milosz
 */

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Collection;

//Utility class for querying the java source files in the project tree.
public class SourceFileUtils {

    //TODO delete
    private static final String extension = "java";

    //TODO remove if nobody uses it, version below is more flexible and searches in correct project scope
    @Deprecated
    public static Collection<VirtualFile> getAllFilesByExt(Project project){
        return FilenameIndex.getAllFilesByExt(project, extension);
    }

    //This might be used over the top one as "GlobalSearchScope.projectScope(project)"
    // makes sure it will only look in current project (using top one returns weird gradle class files)
    public static Collection<VirtualFile> getAllFilesByExtensionsInLocalScope(Project project, String extension){
        return FilenameIndex.getAllFilesByExt(project, extension,
                GlobalSearchScope.projectScope(project));
//        return FilenameIndex.getAllFilesByExt(project, extension);
    }
}