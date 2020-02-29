package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.vfs.VirtualFile;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Singleton as we expect only one execution loop.
 */
public class ExecutionLoop {

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

    //TODO here program will wait for input and add objects to the bar here
    public void startLoop() {
        isRunning = true;
//        while (isRunning) {
//
//        }
    }

    //TODO plenty of sanity checks
    public void addObject(Class<?> object){
        loadedObjects.add(object);
    }
}
