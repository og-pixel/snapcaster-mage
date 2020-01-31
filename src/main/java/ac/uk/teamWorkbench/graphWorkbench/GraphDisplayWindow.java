package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.ui.tree.project.ProjectFileTreeModel;

import javax.swing.*;

public class GraphDisplayWindow {

    private Project project;

    private JPanel content;
    private JLabel awaiting_text;

    public GraphDisplayWindow(Project project) {
        this.project = project;
    }

    public JPanel getContent() {
        getAllClassesFromProject();
        return content;
    }

    private void getAllClassesFromProject() {
        try {
            ProjectFileTreeModel projectFileTreeModel = new ProjectFileTreeModel(project);

            String test = projectFileTreeModel.getRoot().toString();
            System.out.printf("test getRoot(): " + test);
//            StringBuilder stringBuilder = new StringBuilder();
//            for (String name :
//                    names) {
//                stringBuilder.append(name).append(" \n");
//            }
//            content.add(new JTextArea(stringBuilder.toString()));
        } catch (Exception e) {
            awaiting_text.setText("Indexing need to be finish before displaying graph");
        }
    }
}
