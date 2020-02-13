package ac.uk.teamWorkbench;
/*
 * @Author: Matt
 */

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;

import java.util.Collection;

//Utility class for querying the java source files in the project tree.
public class SourceFileUtils {

    private static final String extension = "java";

    public static Collection<VirtualFile> getAllFilesByExt(Project project){
        return FilenameIndex.getAllFilesByExt(project, extension);
    }
}
