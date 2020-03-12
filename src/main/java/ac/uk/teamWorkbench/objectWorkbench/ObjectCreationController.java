package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ObjectCreationController {

    private URLClassLoader classLoader;

    //HashMap of classes in the Project
    // It should be fine with unique key as
    // classes should have unique names anyway
    // (IntelliJ disallows creation anyway)
    private Map<String, ClassReflection> projectClassList = new HashMap<>();

    ArrayList<VirtualFile> findCompiledClasses(VirtualFile root) {
        ArrayList<VirtualFile> list = new ArrayList<>();
        return findCompiledClasses(root.getChildren(), list);
    }

    ArrayList<VirtualFile> findCompiledClasses(VirtualFile[] virtualFile, ArrayList<VirtualFile> list){
        for (VirtualFile file : virtualFile) {
            if (file.isDirectory()) {
                findCompiledClasses(file.getChildren(), list);
            } else {
                list.add(file);
            }
        }
        return list;
    }

    void findProjectClasses() {
        VirtualFile projectRoot = SourceFileUtils.getInstance().getCompilerModule().get(0);
        File allFiles = new File(Objects.requireNonNull(projectRoot.getCanonicalPath()));
        List<VirtualFile> compiledClassesList;
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
        for (VirtualFile virtualFile: compiledClassesList) {
            classReflection = new ClassReflection(virtualFile.getNameWithoutExtension());
            String className = classReflection.getClassName();
            projectClassList.put(className, classReflection);

            loadedClass = loadClass(virtualFile, className);

            getClassMethods(loadedClass).forEach(methodName ->
                    projectClassList.get(className).addMethod(methodName));

            getClassVariables(loadedClass).forEach(methodName ->
                    projectClassList.get(className).addVariable(methodName));

            String parentClassName = loadedClass.getSuperclass().toGenericString();
            projectClassList.get(className).setParentClass(parentClassName);

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
     * Extract methods from the Class by performing reflection
     * @param loadedClass Class name that methods are extracted from.
     * @return List of methods belonging to that class.
     */
    ArrayList<String> getClassMethods(Class<?> loadedClass) {
        ArrayList<String> methodList = new ArrayList<>();
        Method[] methods = loadedClass.getDeclaredMethods();
        for (int i = 0; i < loadedClass.getDeclaredMethods().length; i++) {
            methodList.add(methods[i].getName());
        }
        return methodList;
    }

    /**
     * Extract variables from the Class by performing reflection
     * @param loadedClass Class name that variables are extracted from.
     * @return List of variables belonging to that class.
     */
    ArrayList<String> getClassVariables(Class<?> loadedClass) {
        ArrayList<String> variableList = new ArrayList<>();
        Field[] fields = loadedClass.getDeclaredFields();
        for (int i = 0; i < loadedClass.getDeclaredFields().length; i++) {
            variableList.add(fields[i].getName());
        }
        return variableList;
    }

    Map<String, ClassReflection> getProjectClassList() {
        return projectClassList;
    }

}
