package it.polito.group2.restaurantowner.user.takeaway;

/**
 * Created by Filippo on 10/05/2016.
 */
public class AdditionModel {
    private String name;
    private boolean selected;

    public AdditionModel(String name) {
        this.name = name;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
