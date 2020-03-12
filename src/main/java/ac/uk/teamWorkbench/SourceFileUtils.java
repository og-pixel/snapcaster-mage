package ac.uk.teamWorkbench;
/*
 * @Author: Matt, Milosz
 * @Modification: Kacper
 */

import ac.uk.teamWorkbench.exception.NotInstantiatedException;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import com.intellij.openapi.wm.ToolWindow;

import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

//Utility class for querying the java source files in the project tree.
public class SourceFileUtils {

    //Singleton Instance reference
    private static SourceFileUtils instance = null;
    private static boolean isInstantiated = false;

    //Information useful for the entire project
    private Project project;
    private ToolWindow toolWindow;

    //Root of the project
    private VirtualFile projectRoot;

    private PsiManager psiManager;

    private List<VirtualFile> compilerModule = new ArrayList<>();

    //Singleton
    private SourceFileUtils(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        this.projectRoot = ModuleRootManager.getInstance(
                ModuleManager.getInstance(project).getModules()[0]).getContentRoots()[0];
        this.psiManager = PsiManager.getInstance(project);

        ModuleManager moduleManager = ModuleManager.getInstance(project);
        for (int i = 0; i < moduleManager.getModules().length; i++) {
            Module module = moduleManager.getModules()[i];
            if(Objects.requireNonNull(CompilerModuleExtension.getInstance(module)).getCompilerOutputPath() != null){
                this.compilerModule.add(Objects.requireNonNull(CompilerModuleExtension.getInstance(module)).getCompilerOutputPath());
                //TODO compare here
//                compareCompiledWithSource();
            }
        }

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

    public Collection<VirtualFile> getAllFilesInProject(String extension, VirtualFile virtualFile) {
        return FilenameIndex.getAllFilesByExt(project, extension,
                GlobalSearchScope.fileScope(project, virtualFile));
    }

    public Collection<VirtualFile> getAllFilesByExtInProjectScope(String extension) {
        return FilenameIndex.getAllFilesByExt(project, extension, GlobalSearchScope.projectScope(project));
    }

    private void compareCompiledWithSource(){

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

    public List<VirtualFile> getCompilerModule() {
        return compilerModule;
    }

    public PsiManager getPsiManager() {
        return psiManager;
    }

    public static Collection<VirtualFile> getAllFilesByExtInProjectScope(Project project) {
        return FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project));
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

    public static Collection<String> getPsiClassInheritanceList(PsiElement element, String inheritanceType) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (PsiElement child :
                element.getChildren()) {
            if (child.toString().contains("PsiReferenceList")) {
                for (PsiElement grandchild : child.getChildren()) {
                    if (grandchild.toString().contains("PsiJavaCodeReferenceElement")
                            && child.getText().contains(inheritanceType)
                            && !grandchild.getText().isEmpty())
                        arrayList.add(grandchild.getText());
                }
            }
        }
        return arrayList;
    }

    public static String getPsiClassName(PsiElement element) {
        return element.toString().split(":")[1];
    }
}
