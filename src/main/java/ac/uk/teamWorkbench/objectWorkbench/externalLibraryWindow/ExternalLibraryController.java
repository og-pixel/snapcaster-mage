package ac.uk.teamWorkbench.objectWorkbench.externalLibraryWindow;

import ac.uk.teamWorkbench.SourceFileUtils;
import ac.uk.teamWorkbench.objectWorkbench.ObjectPool;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalLibraryController {

    private ExternalLibraryWindow GUI;
    private List<Library> libraryList;


    public ExternalLibraryController(ExternalLibraryWindow GUI) {
        this.GUI = GUI;
        this.libraryList = new ArrayList<>();
    }


    public void populateLibraryList() {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(
                SourceFileUtils.getInstance().getProject());
        libraryList = Arrays.asList(projectLibraryTable.getLibraries());
        List<String> libraryNames = new ArrayList<>();

        libraryList.forEach(e -> libraryNames.add(e.getName()));

        DefaultListModel<String> listModel = GUI.getLibraryNamesListModel();
        listModel.addAll(libraryNames);
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }

    public void addButtonListener(JButton addButton, JButton removeButton) {
        addButton.addActionListener(e -> {
            List<String> selectedLibraries = GUI.getLibraryNamesList().getSelectedValuesList();
            if(!selectedLibraries.isEmpty()) {
                GUI.getLoadedLibrariesNamesListModel().addAll(selectedLibraries);
            }
        });

        removeButton.addActionListener(e -> {
            List<String> loadedSelectedLibraries = GUI.getLoadedLibrariesList().getSelectedValuesList();
            if(!loadedSelectedLibraries.isEmpty()) {
                for (String loadedSelectedLibrary : loadedSelectedLibraries) {
                    GUI.getLoadedLibrariesNamesListModel().removeElement(loadedSelectedLibrary);
                    System.out.println("removed: " + loadedSelectedLibrary);
                }
            }
        });

    }
}
