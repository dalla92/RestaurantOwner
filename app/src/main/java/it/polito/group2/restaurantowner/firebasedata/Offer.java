package it.polito.group2.restaurantowner.firebasedata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Alessio on 16/05/2016.
 */

public class Offer {

    private String offerID;
    private String restaurantID;
    private String offerName;
    private String offerDescription;

    private Boolean offerAtLunch = false;
    private Boolean offerAtDinner = false;
    private Boolean offerIsWeekly = false;
    private Calendar offerStartDate;
    private Calendar offerStopDate;
    private ArrayList<Integer> offerOnWeekDays = new ArrayList<Integer>();

    private Boolean offerForTotal = false;
    private Boolean offerForMeal = false;
    private Boolean offerForCategory = false;

    private ArrayList<MealCategory> offerOnCategories = new ArrayList<MealCategory>();
    private ArrayList<Meal> offerOnMeals = new ArrayList<Meal>();

    private Integer offerPercentage;

    public Offer() {}

    public Double getNewMealPrice(Meal meal, Calendar day) {
        Double price = meal.getMeal_price();
        if(isDayInOffer(day)) {
            if(isMealInOffer(meal)) {
                price = (Double)(meal.getMeal_price()*offerPercentage)/100;
            }
        }
        return price;
    }

    public Boolean isDayInOffer(Calendar day) {
        int timeOfDay = day.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 6 && timeOfDay < 16){
            return isDayLunchInOffer(day);
        } else if(timeOfDay >= 16 && timeOfDay < 6){
            return isDayDinnerInOffer(day);
        }

        return false;
    }

    public Boolean isDayLunchInOffer(Calendar day) {
        if(offerAtLunch) {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(offerStartDate.getTimeInMillis() - 60000);
            Calendar stop = Calendar.getInstance();
            stop.setTimeInMillis(offerStopDate.getTimeInMillis() + 60000);
            if(day.after(start) && day.before(stop)) {
                if(offerIsWeekly) {
                    for(Integer weekDay : offerOnWeekDays) {
                        if(day.get(Calendar.DAY_OF_WEEK) == weekDay)
                            return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean isDayDinnerInOffer(Calendar day) {
        if(offerAtDinner) {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(offerStartDate.getTimeInMillis() - 60000);
            Calendar stop = Calendar.getInstance();
            stop.setTimeInMillis(offerStopDate.getTimeInMillis() + 60000);
            if(day.after(start) && day.before(stop)) {
                if(offerIsWeekly) {
                    for(Integer weekDay : offerOnWeekDays) {
                        if(day.get(Calendar.DAY_OF_WEEK) == weekDay)
                            return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean isMealInOffer(Meal meal) {
        if(offerForMeal) {
            for(Meal m : offerOnMeals) {
                if(meal.getMeal_id().equals(m.getMeal_id()))
                    return true;
            }
        }
        return isCategoryInOffer(meal.getMeal_category());
    }

    public Boolean isCategoryInOffer(String categoryName) {
        if(offerForCategory) {
            for(MealCategory c : offerOnCategories) {
                if(c.getMeal_category_name().equals(categoryName))
                    return true;
            }
        }
        return false;
    }

    public String getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public Boolean getOfferAtLunch() {
        return offerAtLunch;
    }

    public void setOfferAtLunch(Boolean offerAtLunch) {
        this.offerAtLunch = offerAtLunch;
    }

    public Boolean getOfferAtDinner() {
        return offerAtDinner;
    }

    public void setOfferAtDinner(Boolean offerAtDinner) {
        this.offerAtDinner = offerAtDinner;
    }

    public Boolean getOfferIsWeekly() {
        return offerIsWeekly;
    }

    public void setOfferIsWeekly(Boolean offerIsWeekly) {
        this.offerIsWeekly = offerIsWeekly;
    }

    public Calendar getOfferStartDate() {
        return offerStartDate;
    }

    public void setOfferStartDate(Calendar offerStartDate) {
        this.offerStartDate = offerStartDate;
    }

    public Calendar getOfferStopDate() {
        return offerStopDate;
    }

    public void setOfferStopDate(Calendar offerStopDate) {
        this.offerStopDate = offerStopDate;
    }

    public ArrayList<Integer> getOfferOnWeekDays() {
        return offerOnWeekDays;
    }

    public void setOfferOnWeekDays(ArrayList<Integer> offerOnWeekDays) {
        this.offerOnWeekDays = offerOnWeekDays;
    }

    public Boolean getOfferForTotal() {
        return offerForTotal;
    }

    public void setOfferForTotal(Boolean offerForTotal) {
        this.offerForTotal = offerForTotal;
    }

    public Boolean getOfferForMeal() {
        return offerForMeal;
    }

    public void setOfferForMeal(Boolean offerForMeal) {
        this.offerForMeal = offerForMeal;
    }

    public Boolean getOfferForCategory() {
        return offerForCategory;
    }

    public void setOfferForCategory(Boolean offerForCategory) {
        this.offerForCategory = offerForCategory;
    }

    public ArrayList<MealCategory> getOfferOnCategories() {
        return offerOnCategories;
    }

    public void setOfferOnCategories(ArrayList<MealCategory> offerOnCategories) {
        this.offerOnCategories = offerOnCategories;
    }

    public ArrayList<Meal> getOfferOnMeals() {
        return offerOnMeals;
    }

    public void setOfferOnMeals(ArrayList<Meal> offerOnMeals) {
        this.offerOnMeals = offerOnMeals;
    }

    public Integer getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(Integer offerPercentage) {
        this.offerPercentage = offerPercentage;
    }
}