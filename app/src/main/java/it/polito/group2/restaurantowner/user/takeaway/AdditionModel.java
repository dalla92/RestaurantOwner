package it.polito.group2.restaurantowner.user.takeaway;

/**
 * Created by Filippo on 10/05/2016.
 */
public class AdditionModel {
    private String additionName;
    private String additionID;
    private boolean selected;

    public AdditionModel(String name, String id) {
        this.additionName = name;
        this.additionID = id;
        selected = false;
    }

    public String getName() {
        return additionName;
    }

    public void setName(String name) {
        this.additionName = name;
    }

    public String getAdditionID() {
        return additionID;
    }

    public void setAdditionID(String additionID) {
        this.additionID = additionID;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
