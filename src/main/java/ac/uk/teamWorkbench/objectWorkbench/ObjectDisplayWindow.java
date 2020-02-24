package ac.uk.teamWorkbench.objectWorkbench;

import com.intellij.openapi.wm.ToolWindow;
import javax.swing.*;

/**
 * ObjectDisplayWindow
 *
 * The main GUI form for the ObjectWorkbench.
 */
public class ObjectDisplayWindow {

    private JPanel contentWindow;
    private JPanel leftPane;
    private JPanel rightPane;
    private JSplitPane splitPane;
    private JTabbedPane tabbedPane;

    private WorkbenchController controller;

    public ObjectDisplayWindow() {
        // Project items
        controller = new WorkbenchController(this);
        init();
    }

    private void init() {
        controller.addMousePressedListener(this.tabbedPane);
    }

    public JTabbedPane getTabbedPane() { return this.tabbedPane; }
    public JPanel getContentWindow() { return this.contentWindow; }
}