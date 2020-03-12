package ac.uk.teamWorkbench.graphWorkbench;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.Collection;

public class KlassController {

    KlassController controller;
    ArrayList<Klass> klasses;

    private KlassController() {
        klasses = new ArrayList<>();
    }

    public KlassController getInstance() {
        if (controller == null) {
            controller = new KlassController();
        }
        return controller;
    }

    public ArrayList<Klass> getKlasses() {
        return klasses;
    }

    public void setKlasses(ArrayList<Klass> klasses) {
        this.klasses = klasses;
    }

    public void addKlass(String name) {
        klasses.add(new Klass(name));
    }

    private void populateKlasses(Project project) {
        Collection<PsiElement> classesNames = SourceFileUtils.getAllPsiClasses(project);
        for (PsiElement element : classesNames) {
            String name = element.toString().split(":")[1];
            addKlass(name);
        }
    }
}
