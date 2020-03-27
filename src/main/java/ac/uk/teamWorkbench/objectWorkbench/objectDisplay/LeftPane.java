package ac.uk.teamWorkbench.objectWorkbench.objectDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LeftPane extends JPanel {

    private GridBagConstraints cons;
    private static LeftPane instance;
    private ArrayList<JPanel> panelArray;

    public LeftPane(){

        cons = new GridBagConstraints();
        panelArray = new ArrayList<>();

        cons.weighty = 0.1;
        cons.weighty = 0.1;
        cons.insets = new Insets(10,10 ,0,0);
    }

    public static LeftPane getInstance(){
        if(instance == null) instance = new LeftPane();
        return instance;
    }

    /**
     * Draws JLabels to a JPanel and returns the JPanel
     */
    public JPanel drawLabels(Object[] params, Class<?>[] paramTypes){
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        for(int i = 0; i < params.length; i++){
            cons.gridx = 0;
            cons.gridy = i;
            panel.add(new JLabel("Type: " + paramTypes[i] + "    "), cons);

            cons.gridx = 1;
            cons.gridy = i;
            panel.add(new JLabel("Value: " + params[i]), cons);
        }

        return panel;
    }

    /**
     * Stores a JPanel to the JPanel ArrayList
     */
    public void addPanel(JPanel panel){
        panelArray.add(panel);
    }

    /**
     * Retrieves a JPanel from the JPanel ArrayList by index
     */
    public JPanel getPanel(int index){
        return panelArray.get(index);
    }
}
