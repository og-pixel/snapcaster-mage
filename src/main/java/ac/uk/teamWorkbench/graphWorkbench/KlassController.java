package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;

import static ac.uk.teamWorkbench.SourceFileUtils.getAllPSIFiles;

public class KlassController {

    ArrayList<Klass> klasses;

    public KlassController() {
        klasses = new ArrayList<>();
    }

    public ArrayList<Klass> getKlasses() {
        return klasses;
    }

    public void setKlasses(ArrayList<Klass> klasses) {
        this.klasses = klasses;
    }

    public void addKlass(Klass klass) {
        klasses.add(klass);
    }

    public void populateKlasses(Project project) {
        for (PsiFile psiFile : getAllPSIFiles(project))
            for (PsiElement file : psiFile.getChildren()) {
                if (file.getContext() == null) continue;
                if (file.toString().contains("PsiClass")) {
                    Klass klass = new Klass(file.toString().split(":")[1]);
                    for (PsiElement child :
                            file.getChildren()) {
                        if (child.toString().contains(("PsiReferenceList"))) {
                            if (child.getChildren().length < 1) continue;
                            for (PsiElement grandchild :
                                    child.getChildren())
                                if (grandchild.toString().contains("PsiJavaCodeReferenceElement")) {
                                    if (grandchild.getText().isEmpty()) continue;
                                    if (child.getText().contains("extends"))
                                        klass.setParentName(grandchild.getText());
                                    if (child.getText().contains("implements"))
                                        klass.addImplementsListItem(grandchild.getText());
                                }
                        }
                        if (child.toString().contains("PsiField"))
                            if (child.getText().contains("<") || child.getText().contains(">"))
                                klass.addFieldsListItem(child.getText().split("[<>]")[1]);
                            else klass.addFieldsListItem(child.getText().split("\\s")[0]);
                        if (child.toString().contains("PsiKeyword:interface")) klass.setType("interface");
                        if (child.toString().contains("PsiKeyword:enum")) klass.setType("enum");
                    }
                    addKlass(klass);
                }
            }
    }

    public void printKlassesToConsole() {
        StringBuilder sb = new StringBuilder("--- Classes ---").append("\n");
        klasses.forEach(klass -> {
            sb.append("-").append(klass.type).append("\n");
            sb.append("-").append(klass.name).append("\n");
            sb.append("-e-").append(klass.parentName).append("\n");
            klass.implementsList.forEach(s -> sb.append("-i-").append(s).append("\n"));
            klass.fieldsList.forEach(s -> sb.append("-f-").append(s).append("\n"));
            sb.append("- -- --- -- -").append("\n");
        });
        System.out.println(sb);
    }
}
