package ac.uk.teamWorkbench.objectWorkbench.externalLibraryWindow;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalLibraryController {

    private ExternalLibraryWindow GUI;

    public ExternalLibraryController(ExternalLibraryWindow GUI) {
        this.GUI = GUI;
    }


    public void populateLibraryList() {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(
                SourceFileUtils.getInstance().getProject());
        List<Library> dasd = Arrays.asList(projectLibraryTable.getLibraries());
        List<String> libraries = new ArrayList<>();

        for (Library library : dasd) {
            libraries.add(library.getName());
        }

        DefaultListModel<String> listModel = GUI.getLibraryListModel();
        listModel.addAll(libraries);
        System.out.println("found: " + libraries);
    }
}
