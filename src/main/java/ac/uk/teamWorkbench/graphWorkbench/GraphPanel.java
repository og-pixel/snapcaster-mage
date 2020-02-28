package ac.uk.teamWorkbench.graphWorkbench;

import ac.uk.teamWorkbench.SourceFileUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("CanBeFinal")
public class GraphPanel extends JPanel {

    private ArrayList<Object> graphElements;
    private Project project;

    public GraphPanel(Project project) {
        this.project = project;
    }

    public void build() {
        setSize(750, 750);
        graphElements = new ArrayList<>();
        add(addGraph());
        setSize(750, 750);
    }

    @NotNull
    private mxGraphComponent addGraph() {

        mxGraph graph = new mxGraph();
        getStylesheet(graph);
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            createAnVertexes(graph, parent);

        } finally {
            graph.getModel().endUpdate();
        }


        mxGraphComponent graphComponent = addComponentActionListener(graph);
        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());

        graphComponent.setEnabled(false);
        return graphComponent;
    }


    @NotNull
    private mxGraphComponent addComponentActionListener(mxGraph graph) {

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.getMaximumSize().setSize(400, 400);
        graphComponent.setEnterStopsCellEditing(true);

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Object target = graphComponent.getCellAt(mouseEvent.getX(), mouseEvent.getY());
                if (target != null) {
                    System.out.println("mouseEvent = " + mouseEvent);
                    //  JBPopupFactory.getInstance().createMessage("You click on : "+graph.getLabel(target)).showInFocusCenter();
                }
            }
        });

        return graphComponent;
    }


    private void createAnVertexes(@NotNull mxGraph graph, Object parent) {

        Collection<PsiElement> classesNames = SourceFileUtils.getAllPsiClasses(project);
        for (PsiElement element : classesNames) {
            String id = SourceFileUtils.getPsiClassName(element);
            graphElements.add(graph.insertVertex(parent,
                    id,            // id of vertex
                    (Arrays.toString(element.getChildren()).contains("PsiKeyword:interface")) ? "<<Interface>>\n" + id
                            : (Arrays.toString(element.getChildren()).contains("PsiKeyword:enum")) ? "<<Enum>>\n" + id
                            : id,            // Text in the square
                    25,             // x position of top left corner
                    25,             // y position of top left corner
                    85,        // width of box
                    35,         // height of box
                    (Arrays.toString(element.getChildren()).contains("PsiKeyword:interface")) ? "fillColor=#ffcc66"
                            : (Arrays.toString(element.getChildren()).contains("PsiKeyword:enum")) ? "fillColor=#33cccc"
                            : ""));
        }
    }

    private void getStylesheet(mxGraph graph) {
        mxStylesheet style = new mxStylesheet();
        for (Map.Entry<String, Map<String, Object>> entry : style.getStyles().entrySet()) {
            for (Map.Entry<String, Object> objects : entry.getValue().entrySet()) {
                if (objects.getKey().equals("fillColor")) objects.setValue("#99ff33");
                if (objects.getKey().equals("strokeColor")) objects.setValue("#3d3d5c");
                if (objects.getKey().equals("fontColor")) objects.setValue("#00264d");
            }
        }
        graph.setStylesheet(style);
    }
}