package ac.uk.teamWorkbench.objectWorkbench.externalLibraryWindow;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

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
        Library[] libraries = projectLibraryTable.getLibraries();
        List<String> libraryNames = new ArrayList<>();
//        ModuleManager manager = ModuleManager.getInstance(SourceFileUtils.getInstance().getProject());

        for (Library library : libraries) {
            libraryNames.add(library.getName());
            for (int i = 0; i < library.getFiles(OrderRootType.SOURCES).length; i++) {
                System.out.println(library.getFiles(OrderRootType.SOURCES)[i].getPath());
            }
        }

        DefaultListModel<String> listModel = GUI.getLibraryListModel();
        listModel.addAll(libraryNames);
    }
}