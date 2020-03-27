package ac.uk.teamWorkbench.objectWorkbench.objectDisplay;

import javax.swing.*;
import java.awt.*;

public class LeftPane extends JPanel {

    private GridBagConstraints cons = new GridBagConstraints();

    public LeftPane(Object[] parameters, Class<?>[] paramTypes){
        setLayout(new GridBagLayout());

        cons.weighty = 0.1;
        cons.weighty = 0.1;
        cons.insets = new Insets(10,10 ,0,0);

        drawLabels(parameters, paramTypes);
    }
    private void drawLabels(Object[] params, Class<?>[] paramTypes){
        for(int i = 0; i < params.length; i++){
            cons.gridx = 0;
            cons.gridy = i;
            add(new JLabel("Type: " + paramTypes[i] + "   "), cons);

            cons.gridx = 1;
            cons.gridy = i;
            add(new JLabel("Value: " + params[i]), cons);

        }
    }
}
