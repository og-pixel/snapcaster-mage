package ac.uk.teamWorkbench.objectWorkbench;

public class ExecutionLoop {

    private static ExecutionLoop instance = null;

    private ExecutionLoop() {

    }

    public static ExecutionLoop getInstance(){
        if(instance == null) instance = new ExecutionLoop();
        return instance;
    }

    //TODO here program will wait for input and add objects to the bar here
    public void startLoop() {

    }
}
