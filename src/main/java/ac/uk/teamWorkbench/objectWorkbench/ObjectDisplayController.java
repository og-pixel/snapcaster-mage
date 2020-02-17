package ac.uk.teamWorkbench.objectWorkbench;

public class ObjectDisplayController {

    private ObjectDisplayWindow GUI;

    public ObjectDisplayController(ObjectDisplayWindow GUI) {
        this.GUI = GUI;
    }

    /**
     * Checks the addTab request is to a valid tab
     * @param tabTitle - title of tab
     * @return true if tabtitle is "+" else false
     */
    private boolean isValidAddRequest(String tabTitle) {
        return tabTitle.equals("+");
    }


}
