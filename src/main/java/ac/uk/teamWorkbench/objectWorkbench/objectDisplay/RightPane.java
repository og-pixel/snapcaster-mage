package ac.uk.teamWorkbench.objectWorkbench.objectDisplay;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RightPane extends JPanel {

    private static RightPane instance;
    private ArrayList<JPanel> panelArray;

    public RightPane(){
        panelArray = new ArrayList<>();

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

    public void storePanel(JPanel panel){
        panelArray.add(panel);
    }

    public JPanel getPanel(int index){
        if(panelArray.isEmpty()){
            return new JPanel();
        }
        return panelArray.get(index);
    }

    public void removePanel(int index){
        panelArray.remove(index);
    }

    public void removeAllPanels(){
        panelArray.clear();
    }
}
