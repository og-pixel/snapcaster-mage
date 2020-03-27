package ac.uk.teamWorkbench.objectWorkbench.objectDisplay;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

public class RightPane extends JPanel {

    private static RightPane instance;

    public RightPane(){

    }

    public static RightPane getInstance(){
        if(instance == null) instance = new RightPane();
        return instance;
    }

    public JPanel drawButtons(Method[] methods){
        final String spacer = " ";
        JPanel panel = new JPanel();
        for(int i = 0; i < methods.length; i++){
            String methodName = methods[i].getName();
            if(methodName.equals("main")){ break; }
            else{
                panel.add(new JButton(methodName));
                panel.add(new JLabel(spacer));
            }
        }
        return panel;
    }
}
