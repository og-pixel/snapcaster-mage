package ac.uk.teamWorkbench.workbenchRuntime;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton as we expect only one execution loop.
 */
public class ExecutionLoop implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ExecutionLoop.class.getName());


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
                LOGGER.log(Level.INFO, "Starting Execution Loop");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Failed to start Execution Loop" + e.getMessage());
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

        Class<?>[] parameterTypes = x.getParameterTypes();


        Object[] f = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            try {
                f[i] = toObject(parameterTypes[i], arguments[i].toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Object newObject = null;
        try {
            newObject = x.newInstance(f);
            LOGGER.log(Level.INFO, "Successfully instantiated an object: " + newObject.getClass());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to instantiate the object: " + e.getMessage());
            return false;
        }
        loadedObjects.add(newObject);
        return true;
    }


    public static Object toObject(Class<?> clazz, String value) throws Exception {
        if (clazz.isPrimitive()) return toPrimitive(clazz, value);

        if (Boolean.class == clazz) return Boolean.parseBoolean(value);
        if (Byte.class == clazz) return Byte.parseByte(value);
        if (Short.class == clazz) return Short.parseShort(value);
        if (Integer.class == clazz) return Integer.parseInt(value);
        if (Long.class == clazz) return Long.parseLong(value);
        if (Float.class == clazz) return Float.parseFloat(value);
        if (Double.class == clazz) return Double.parseDouble(value);
        return value;
    }

    public static Object toPrimitive(Class<?> clazz, String value) throws Exception {
        if (clazz == Boolean.TYPE) return Boolean.parseBoolean(value);
        if (clazz == Byte.TYPE) return Byte.parseByte(value);
        if (clazz == Short.TYPE) return Short.parseShort(value);
        if (clazz == Integer.TYPE) return Integer.parseInt(value);
        if (clazz == Long.TYPE) return Long.parseLong(value);
        if (clazz == Float.TYPE) return Float.parseFloat(value);
        if (clazz == Double.TYPE) return Double.parseDouble(value);

        throw new Exception("Casting to primitive cast should never fail as it happens after checking if element is primitive");
    }
}
