package ac.uk.teamWorkbench.objectWorkbench;

import java.util.ArrayList;
import java.util.List;


/**
 * Class containing all information about one class in the project,
 * it contains all methods and variables that it has, as well as
 * its parent and children.
 */
public class ClassReflection {

    private String className;
    private String parentClass;
    private List<String> childrenList = new ArrayList<>();
    private List<String> variableList = new ArrayList<>();
    private List<String> methodList = new ArrayList<>();


    public ClassReflection(String className) {
        this.className = className;
    }

    /**
     * Add a method to the list of the object
     *
     * @param methodName Name of the method as a string
     * @return true if addition is successful, false otherwise
     */
    public boolean addMethod(String methodName) {
        return methodList.add(methodName);
    }

    /**
     * Add a variable to the list of the object
     *
     * @param variableName Name of the variable as a string
     * @return true if addition is successful, false otherwise
     */
    public boolean addVariable(String variableName) {
        return variableList.add(variableName);
    }

    ////Getters
    public String getClassName() {
        return className;
    }

    public List<String> getMethodList() {
        return methodList;
    }

    public List<String> getVariableList() {
        return variableList;
    }

    public String getParentClass(){
        return parentClass;
    }

    public List<String> getChildrenList() {
        return childrenList;
    }

    ////Setters
    public void setParentClass(String parentClassName) {
        parentClass = parentClassName;
    }
}

