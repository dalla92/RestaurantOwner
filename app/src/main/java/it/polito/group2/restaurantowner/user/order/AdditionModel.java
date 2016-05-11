package it.polito.group2.restaurantowner.user.order;

import it.polito.group2.restaurantowner.data.MealAddition;

/**
 * Created by Filippo on 10/05/2016.
 */
public class AdditionModel {
    private String id;
    private String name;
    private boolean selected;
    private MealAddition addition;

    public AdditionModel(String id, String name, boolean selected, MealAddition addition) {
        this.id = id;
        this.name = name;
        this.selected = selected;
        this.addition = addition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public MealAddition getAddition() {
        return addition;
    }

    public void setAddition(MealAddition addition) {
        this.addition = addition;
    }

}
