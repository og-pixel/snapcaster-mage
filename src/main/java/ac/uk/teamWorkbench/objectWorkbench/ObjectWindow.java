package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ObjectWindow extends DialogWrapper {

    private JPanel content;
    private JLabel text;
    private JList list1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;

    protected ObjectWindow(boolean canBeParent) {
        super(canBeParent);
        init();
        setTitle("HELLO MOTO");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {




        return content;
    }
}
