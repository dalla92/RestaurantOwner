package it.polito.group2.restaurantowner.firebasedata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alessio on 16/05/2016.
 */

public class Offer implements Serializable {

    private String offerID;
    private String restaurantID;
    private String userID;
    private String offerName;
    private String offerDescription;

    private Boolean offerAtLunch = false;
    private Boolean offerAtDinner = false;
    private Calendar offerStartDate;
    private Calendar offerStopDate;
    private ArrayList<Integer> offerOnWeekDays = new ArrayList<Integer>();

    private Boolean offerForTotal = true;
    private Boolean offerForMeal = false;
    private Boolean offerForCategory = false;

    private ArrayList<String> offerOnCategories = new ArrayList<String>();
    private ArrayList<Meal> offerOnMeals = new ArrayList<Meal>();

    private Boolean offerEnabled = true;
    private Integer offerPercentage;

    public Offer() {
        offerOnWeekDays.add(Calendar.MONDAY);
        offerOnWeekDays.add(Calendar.TUESDAY);
        offerOnWeekDays.add(Calendar.WEDNESDAY);
        offerOnWeekDays.add(Calendar.THURSDAY);
        offerOnWeekDays.add(Calendar.FRIDAY);
        offerOnWeekDays.add(Calendar.SATURDAY);
        offerOnWeekDays.add(Calendar.SUNDAY);
    }

    public Boolean isWeekDayInOffer(Integer weekDay) {
        if(this.offerOnWeekDays.size()>0) {
            for(Integer n : this.offerOnWeekDays) {
                if(n.equals(weekDay)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addWeekDayInOffer(Integer weekDay) {
        if(!isWeekDayInOffer(weekDay)) {
            offerOnWeekDays.add(weekDay);
        }
    }

    public void delWeekDayInOffer(Integer weekDay) {
        if(isWeekDayInOffer(weekDay)) {
            offerOnWeekDays.remove(weekDay);
        }
    }


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
        if(offerAtLunch && offerEnabled) {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(offerStartDate.getTimeInMillis() - 60000);
            Calendar stop = Calendar.getInstance();
            stop.setTimeInMillis(offerStopDate.getTimeInMillis() + 60000);
            if(day.after(start) && day.before(stop)) {
                for(Integer weekDay : offerOnWeekDays) {
                    if(day.get(Calendar.DAY_OF_WEEK) == weekDay)
                        return true;
                }
            }
        }
        return false;
    }

    public Boolean isDayDinnerInOffer(Calendar day) {
        if(offerAtDinner && offerEnabled) {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(offerStartDate.getTimeInMillis() - 60000);
            Calendar stop = Calendar.getInstance();
            stop.setTimeInMillis(offerStopDate.getTimeInMillis() + 60000);
            if(day.after(start) && day.before(stop)) {
                for(Integer weekDay : offerOnWeekDays) {
                    if(day.get(Calendar.DAY_OF_WEEK) == weekDay)
                        return true;
                }
            }
        }
        return false;
    }

    public Boolean isMealInOffer(Meal meal) {
        if(offerForMeal && offerEnabled) {
            for(Meal m : offerOnMeals) {
                if(meal.getMeal_id().equals(m.getMeal_id()))
                    return true;
            }
        }
        return isInCategoryList(meal.getMeal_category());
    }

    public Boolean isInMealList(Meal meal) {
        if(offerForMeal && offerEnabled) {
            for(Meal m : offerOnMeals) {
                if(meal.getMeal_id().equals(m.getMeal_id()))
                    return true;
            }
        }
        return false;
    }

    public Boolean isInCategoryList(String categoryName) {
        if(offerForCategory && offerEnabled) {
            for(String c : offerOnCategories) {
                if(c.equals(categoryName))
                    return true;
            }
        }
        return false;
    }

    public void addCategoryInOffer(String categoryName) {
        if(!isInCategoryList(categoryName))
            offerOnCategories.add(categoryName);
    }

    public void delCategoryInOffer(String categoryName) {
        if(isInCategoryList(categoryName))
            offerOnCategories.remove(categoryName);
    }

    public void addMealInOffer(Meal meal) {
        if(!isInMealList(meal))
            offerOnMeals.add(meal);
    }

    public void delMealInOffer(Meal meal) {
        if(isInMealList(meal))
            offerOnMeals.remove(meal);
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public ArrayList<String> getOfferOnCategories() {
        return offerOnCategories;
    }

    public void setOfferOnCategories(ArrayList<String> offerOnCategories) {
        this.offerOnCategories = offerOnCategories;
    }

    public ArrayList<Meal> getOfferOnMeals() {
        return offerOnMeals;
    }

    public void setOfferOnMeals(ArrayList<Meal> offerOnMeals) {
        this.offerOnMeals = offerOnMeals;
    }

    public Boolean getOfferEnabled() {
        return offerEnabled;
    }

    public void setOfferEnabled(Boolean offerEnabled) {
        this.offerEnabled = offerEnabled;
    }

    public Integer getOfferPercentage() {
        return offerPercentage;
    }

    public void setOfferPercentage(Integer offerPercentage) {
        this.offerPercentage = offerPercentage;
    }
}