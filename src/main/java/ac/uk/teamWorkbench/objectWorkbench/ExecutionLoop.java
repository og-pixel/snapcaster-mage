package ac.uk.teamWorkbench.objectWorkbench;

import ac.uk.teamWorkbench.graphWorkbench.GraphPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.sun.jna.platform.win32.WinDef;

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

        Class<?>[] parameterTypes = x.getParameterTypes();


        Object[] f = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            try {
                f[i] = toObject(parameterTypes[i], arguments[i].toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(Arrays.toString(f));


//        castDynamic(arguments[0], parameterTypes[0]);
//        Object ppp = getInstance(parameterTypes[0], arguments[0].toString());


        try {
            Object newObject = x.newInstance(f);
            System.out.println("sucessfully instantiated an object: " + newObject.getClass());
        } catch (Exception e) {
            //TODO better error checking
            e.printStackTrace();
            return false;
        }

        return true;
    }


    //TODO maybe delete or move it somewhere else
    private <T> T getInstance(Class<T> type, String o) {
        T t = type.cast(o);
        return t;
    }

    public void castDynamic(Object object, Class className) {
        Class cls = null;
        String objectString = object.toString();

        Object result;

//        if (className.equals("int")) {
////        if (Integer.class instanceof className) {
//            result = Integer.parseInt(objectString);
//        } else if (className.equals("char")) {
//            //TODO
//        } else if (className.equals("byte")) {
//            result = Byte.parseByte(objectString);
//        } else if (className.equals("short")) {
//            result = Short.parseShort(objectString);
//        } else if (className.equals("boolean")) {
//            result = Boolean.parseBoolean(objectString);
//        } else if (className.equals("float")) {
//            result = Float.parseFloat(objectString);
//        } else if (className.equals("long")) {
//            result = Long.parseLong(objectString);
//        } else if (className.equals("double")) {
//            result = Double.parseDouble(objectString);
//        } else {
//            try {
//                cls = Class.forName(className);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }

        result = cls.cast(object);
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

        throw new Exception("Should not happen! work milosz!");
//        return null;
    }
}
