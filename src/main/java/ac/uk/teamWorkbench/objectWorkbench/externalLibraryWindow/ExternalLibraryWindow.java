package ac.uk.teamWorkbench.objectWorkbench.externalLibraryWindow;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ExternalLibraryWindow extends DialogWrapper {

    private ExternalLibraryController controller;
    private JPanel content;
    private JBList<String> libraryList;
    private DefaultListModel<String> libraryListModel;

    private void createUIComponents() {
        libraryListModel = new DefaultListModel<>();

        libraryList = new JBList<>(libraryListModel);
    }

    public ExternalLibraryWindow(boolean canBeParent) {
        super(canBeParent);
        init();
        setTitle("External Library Chooser");
        controller = new ExternalLibraryController(this);
        test();

    }

    void test() {
        controller.populateLibraryList();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return content;
    }

    public DefaultListModel<String> getLibraryListModel() {
        return libraryListModel;
    }

    public ExternalLibraryController getController(){
        return controller;
    }

    public int[] getSelectedLibraries() {
        return libraryList.getSelectedIndices();
    }


}
