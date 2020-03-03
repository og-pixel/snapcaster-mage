package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Purpose of this class is to store all class reflections and update information if it does change
 * it is meant to prevent constant reloading of classes when user presses +
 */
public class ObjectPool {

    private static ObjectPool instance = null;

    private Map<String, ClassReflection> classReflectionMap;
    private URLClassLoader classLoader;

    public static ObjectPool getInstance() {
        if (instance == null) instance = new ObjectPool();
        return instance;
    }

    public ObjectPool() {
        init();
    }

    private void init() {
        classReflectionMap = new HashMap<>();
        findProjectClasses();

    }
    /**
     * Finds all compiled classes from the root of the project and
     * populates the projectHashList
     * It does also needs to be a first project in; "Project Settings/Modules".
     */
    void findProjectClasses() {
        VirtualFile projectRoot;
        try {
            projectRoot = SourceFileUtils.getInstance().getCompilerModule().get(0);
        } catch (ArrayIndexOutOfBoundsException indexException) {
            System.out.println(indexException.getMessage());
            return;
        }

        File allFiles = new File(Objects.requireNonNull(projectRoot.getCanonicalPath()));

        Map<String, VirtualFile> compiledClassesList;
        compiledClassesList = findCompiledClasses(projectRoot);
        //Load classes into class loader
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{allFiles.toURI().toURL()});
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //Loop over all compiled classes and extract methods and variables and save them as String
        // in ObjectReflection Class
        ClassReflection classReflection;
        Class<?> loadedClass;
        for (Map.Entry<String, VirtualFile> entry : compiledClassesList.entrySet()) {
            String className = entry.getValue().getNameWithoutExtension();
            loadedClass = loadClass(entry.getValue(), className);
            classReflection = new ClassReflection(className, loadedClass);

            classReflectionMap.put(className, classReflection);
            getClassMethods(loadedClass).forEach(methodName ->
                    classReflectionMap.get(className).addMethod(methodName));
            getClassVariables(loadedClass).forEach(variableName ->
                    classReflectionMap.get(className).addVariable(variableName));
            getClassConstructor(loadedClass).forEach(constructorName ->
                    classReflectionMap.get(className).addConstructor(constructorName));
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
            if (packageName.isEmpty()) {
                loadedClass = classLoader.loadClass(className);
            } else {
                loadedClass = classLoader.loadClass(packageName + "." + className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedClass;
    }

    /**
     * Extract methods from the Class by performing reflection
     *
     * @param loadedClass Class name that methods are extracted from.
     * @return List of methods belonging to that class.
     */
    ArrayList<Method> getClassMethods(Class<?> loadedClass) {
        Method[] methods = loadedClass.getDeclaredMethods();
        return new ArrayList<>(Arrays.asList(methods));
    }

    /**
     * Extract variables from the Class by performing reflection
     *
     * @param loadedClass Class name that variables are extracted from.
     * @return List of variables belonging to that class.
     */
    ArrayList<Field> getClassVariables(Class<?> loadedClass) {
        Field[] fields = loadedClass.getDeclaredFields();
        return new ArrayList<>(Arrays.asList(fields));
    }

    /**
     * Extract available constructors from the Class by performing reflection
     *
     * @param loadedClass Class name that variables are extracted from.
     * @return List of constructors belonging to that class.
     */
    ArrayList<Constructor<?>> getClassConstructor(Class<?> loadedClass) {
        Constructor<?>[] constructors = loadedClass.getDeclaredConstructors();
        return new ArrayList<>(Arrays.asList(constructors));
    }

    Map<String, VirtualFile> findCompiledClasses(VirtualFile root) {
        Map<String, VirtualFile> list = new HashMap<>();
        return findCompiledClasses(root.getChildren(), list);
    }

    private Map<String, VirtualFile> findCompiledClasses(VirtualFile[] virtualFile, Map<String, VirtualFile> list) {
        for (VirtualFile file : virtualFile) {
            if (file.isDirectory()) {
                findCompiledClasses(file.getChildren(), list);
            } else {
                list.put(file.getNameWithoutExtension(), file);
            }
        }
        return list;
    }

    public Map<String, ClassReflection> getClassReflectionMap() {
        return classReflectionMap;
    }
}
