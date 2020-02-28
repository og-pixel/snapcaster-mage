package ac.uk.teamWorkbench;
/*
 * @Author: Matt
 * @Modification: Kacper
 */

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.Collection;

//Utility class for querying the java source files in the project tree.
public class SourceFileUtils {

    private static final String extension = "java";

    public static Collection<VirtualFile> getAllFilesByExt(Project project) {
        return FilenameIndex.getAllFilesByExt(project, extension);
    }

    public static Collection<VirtualFile> getAllFilesByExtInProjectScope(Project project) {
        return FilenameIndex.getAllFilesByExt(project, extension, GlobalSearchScope.projectScope(project));
    }

    public static Collection<PsiFile> getAllPSIFiles(Project project) {
        Collection<VirtualFile> virtualFiles = getAllFilesByExtInProjectScope(project);
        Collection<PsiFile> psiFiles = new ArrayList<>();
        for (VirtualFile vf : virtualFiles) {
            psiFiles.add(PsiManager.getInstance(project).findFile(vf));
        }
        return psiFiles;
    }

    public static Collection<PsiElement> getAllPsiClasses(Project project) {
        Collection<PsiElement> collection = new ArrayList<>();
        for (PsiFile psiFile : getAllPSIFiles(project)) {
            for (PsiElement psiElement : psiFile.getChildren()) {
                if ((psiElement.getContext() != null) && psiElement.toString().contains("PsiClass")) {
                    collection.add(psiElement);
                }
            }
        }
        return collection;
    }


}
