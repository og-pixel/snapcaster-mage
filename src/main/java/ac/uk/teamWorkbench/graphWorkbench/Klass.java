package ac.uk.teamWorkbench.graphWorkbench;

import java.util.ArrayList;

public class Klass {
    String name;
    String type;
    String parentName;
    ArrayList<String> implementsList;

    public Klass(String name) {
        setName(name);
        setType("class");
        setParentName("");
        implementsList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public ArrayList<String> getImplementsList() {
        return implementsList;
    }

    public void setImplementsList(ArrayList<String> implementsList) {
        this.implementsList = implementsList;
    }

    public String getImplementsListItem(int index) {
        try {
            return implementsList.get(index);
        } catch (Exception e) {
            return "";
        }
    }

    public void addImplementsListItem(String name) {
        implementsList.add(name);
    }
}