package it.polito.group2.restaurantowner.user.order;

import it.polito.group2.restaurantowner.data.MenuCategory;

/**
 * Created by Filippo on 11/05/2016.
 */
public class CategoryModel {

    private String id;
    private String name;
    private MenuCategory category;

    public CategoryModel(String id, String name, MenuCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
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

    public MenuCategory getCategory() {
        return category;
    }

    public void setCategory(MenuCategory category) {
        this.category = category;
    }
}
