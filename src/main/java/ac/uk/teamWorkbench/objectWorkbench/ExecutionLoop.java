package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.graphWorkbench.GraphPanel;
import com.intellij.openapi.vfs.VirtualFile;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Singleton as we expect only one execution loop.
 */
public class ExecutionLoop implements Runnable {

    private static ExecutionLoop instance = null;
    private boolean isRunning;

    private List<Object> loadedObjects;

    private ExecutionLoop() {
        loadedObjects = new ArrayList<>();
        isRunning = false;
    }

    public static ExecutionLoop getInstance() {
        if (instance == null) instance = new ExecutionLoop();
        return instance;
    }

    @Override
    public void run() {
        startLoop();
    }

    private void startLoop() {
        isRunning = true;
        while (isRunning) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO plenty of sanity checks
    public boolean instantiateObject(String objectName, int chosenConstructor, Object[] arguments) {
        Map<String, ClassReflection> classReflectionMap = ObjectPool.getInstance().getClassReflectionMap();
        ClassReflection classReflection = classReflectionMap.get(objectName);

        Class<?> clazz = classReflection.getClazz();
        Constructor<?> x = clazz.getDeclaredConstructors()[chosenConstructor];

        System.out.println(arguments);

        try {
            Object newObject = x.newInstance(arguments);
            System.out.println("sucessfully instantiated an object: " + newObject.getClass());
        } catch (Exception e) {
            //TODO better error checking
            e.printStackTrace();
            return false;
        }

        return true;

    }


}
