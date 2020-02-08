package ac.uk.teamWorkbench.graphWorkbench;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

public class GraphPanel extends JPanel {

    private ArrayList<Object> graphElements;

    public void build() {
        setSize(750, 750);
        graphElements = new ArrayList<>();
        add(addGraph());
    }

    @NotNull
    private mxGraphComponent addGraph() {

        mxGraph graph = new mxGraph();
        getStylesheet(graph);
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            createAnVertexes(graph, parent);
            createAnEdges(graph, parent);
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

    private void createAnEdges(@NotNull mxGraph graph, Object parent) {
        graph.insertEdge(parent,
                "2",                      // id of edge
                "edge",              // text on that edge
                graphElements.get(0),       // starting point
                graphElements.get(0),       // ending point
                "dashed=true");       // style of edge
    }

    private void createAnVertexes(@NotNull mxGraph graph, Object parent) {
        graphElements.add(graph.insertVertex(parent,
                "1",            // id of vertex
                "Test",      // Text in the square
                25,             // x position of top left corner
                25,             // y position of top left corner
                100,         // width of box
                30));       // height of box
    }

    private void getStylesheet(mxGraph graph) {
        mxStylesheet style = new mxStylesheet();
        for (Map.Entry<String, Map<String, Object>> entry : style.getStyles().entrySet()) {
            for (Map.Entry<String, Object> objects : entry.getValue().entrySet()) {
                if (objects.getKey().equals("fillColor")) objects.setValue("#AAAAAA");
                if (objects.getKey().equals("strokeColor")) objects.setValue("#666666");
                if (objects.getKey().equals("fontColor")) objects.setValue("#FFFFFF");
            }
        }
        graph.setStylesheet(style);
    }
}
