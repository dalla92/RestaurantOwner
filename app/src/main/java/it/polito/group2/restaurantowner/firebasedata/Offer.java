package it.polito.group2.restaurantowner.firebasedata;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Offer implements Serializable {

    private String offerID = "";
    private String restaurantID = "";
    private String userID = "";
    private String offerName = "";
    private String offerDescription = "";

    private Boolean offerAtLunch = false;
    private Boolean offerAtDinner = false;
    private Long offerStart = 0L;
    private Long offerStop = 0L;
    private ArrayList<Boolean> offerOnWeekDays = new ArrayList<>();

    private Boolean offerForTotal = true;
    private Boolean offerForMeal = false;
    private Boolean offerForCategory = false;

    private HashMap<String, Boolean> offerOnCategories = new HashMap<>();
    private HashMap<String, Boolean> offerOnMeals = new HashMap<>();

    private Boolean offerEnabled = true;
    private Integer offerPercentage = 0;

    public Offer() {
        offerOnWeekDays.add(false);
        offerOnWeekDays.add(Calendar.SUNDAY, true);
        offerOnWeekDays.add(Calendar.MONDAY, true);
        offerOnWeekDays.add(Calendar.TUESDAY, true);
        offerOnWeekDays.add(Calendar.WEDNESDAY, true);
        offerOnWeekDays.add(Calendar.THURSDAY, true);
        offerOnWeekDays.add(Calendar.FRIDAY, true);
        offerOnWeekDays.add(Calendar.SATURDAY, true);
        offerStart = Calendar.getInstance().getTimeInMillis();
        offerStop = Calendar.getInstance().getTimeInMillis();
    }

    public Boolean isWeekDayInOffer(Integer weekDay) {
        if(isCorrectWeekday(weekDay))
            return offerOnWeekDays.get(weekDay);

        return false;
    }

    public Boolean isCorrectWeekday(Integer v) {
        switch (v) {
            case Calendar.SUNDAY:
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
            case Calendar.SATURDAY:
                return true;
            default:
                return false;
        }
    }

    public void addWeekDayInOffer(Integer weekDay) {
        if(isCorrectWeekday(weekDay))
        offerOnWeekDays.set(weekDay, true);
    }

    public void delWeekDayInOffer(Integer weekDay) {
        if(isCorrectWeekday(weekDay))
        offerOnWeekDays.set(weekDay, false);
    }

    //TODO cambiare il nome
    public Double getNewMealPrice(Meal meal, Calendar day) {
        Double price = meal.getMeal_price();
        if(isNowInOffer(day)) {
            if(isMealInOffer(meal)) {
                price = (meal.getMeal_price()*offerPercentage)/100;
            }
        }
        return price;
    }

    public Boolean isDayInOffer(Calendar day) {
        if(offerEnabled) {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(offerStart - 60000);
            Calendar stop = Calendar.getInstance();
            stop.setTimeInMillis(offerStop + 60000);
            if(day.after(start) && day.before(stop)) {
                return isWeekDayInOffer(day.get(Calendar.DAY_OF_WEEK));
            }
        }
        return false;
    }

    public Boolean isNowInOffer(Calendar day) {
        if(isDayInOffer(day)) {
            int timeOfDay = day.get(Calendar.HOUR_OF_DAY);
            if(timeOfDay >= 6 && timeOfDay < 16){
                return offerAtLunch;
            } else if(timeOfDay >= 16 || timeOfDay < 6){
                return offerAtDinner;
            }
        }
        return false;
    }

    public Boolean isMealInOffer(Meal meal) {
        if(offerForMeal && offerEnabled) {
            return isInMealList(meal);
        }
        return isInCategoryList(meal.getMeal_category());
    }

    public Boolean isCategoryInOffer(String categoryName) {
        if(offerForCategory && offerEnabled) {
            return isInCategoryList(categoryName);
        }
        return false;
    }

    public Boolean isInMealList(Meal meal) {
        return offerOnMeals.containsKey(meal.getMeal_id());
    }

    public Boolean isInCategoryList(String categoryName) {
        return offerOnCategories.containsKey(categoryName);
    }

    public void addCategoryInOffer(String categoryName) {
        if(!isInCategoryList(categoryName))
            offerOnCategories.put(categoryName, true);
    }

    public void delCategoryInOffer(String categoryName) {
        if(isInCategoryList(categoryName))
            offerOnCategories.remove(categoryName);
    }

    public void addMealInOffer(Meal meal) {
        if(!isInMealList(meal))
            offerOnMeals.put(meal.getMeal_id(), true);
    }

    public void delMealInOffer(Meal meal) {
        if(isInMealList(meal))
            offerOnMeals.remove(meal.getMeal_id());
    }

    //TODO cambiare il nome
    public ArrayList<String> getCategoryList() {
        ArrayList<String> list = new ArrayList<>();
        for(String key : offerOnCategories.keySet()) {
            if(offerOnCategories.get(key)) {
                list.add(key);
            }
        }
        return list;
    }

    //TODO cambiare il nome
    public ArrayList<String> getMealList() {
        ArrayList<String> list = new ArrayList<>();
        for(String key : offerOnMeals.keySet()) {
            if(offerOnMeals.get(key)) {
                list.add(key);
            }
        }
        return list;
    }

    public Long getOfferStart() {
        return offerStart;
    }

    public void setOfferStart(Long offerStart) {
        this.offerStart = offerStart;
    }

    public Long getOfferStop() {
        return offerStop;
    }

    public void setOfferStop(Long offerStop) {
        this.offerStop = offerStop;
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

    public Calendar startToCalendar() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(getOfferStart());
        return date;
    }

    public void calendarToStart(Calendar offerStartDate) {
        setOfferStart(offerStartDate.getTimeInMillis());
    }

    public Calendar stopToCalendar() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(getOfferStop());
        return date;
    }

    public void calendarToStop(Calendar offerStopDate) {
        setOfferStop(offerStopDate.getTimeInMillis());
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

    public ArrayList<Boolean> getOfferOnWeekDays() {
        return offerOnWeekDays;
    }

    public void setOfferOnWeekDays(ArrayList<Boolean> offerOnWeekDays) {
        this.offerOnWeekDays = offerOnWeekDays;
    }

    public HashMap<String, Boolean> getOfferOnCategories() {
        return offerOnCategories;
    }

    public void setOfferOnCategories(HashMap<String, Boolean> offerOnCategories) {
        this.offerOnCategories = offerOnCategories;
    }

    public HashMap<String, Boolean> getOfferOnMeals() {
        return offerOnMeals;
    }

    public void setOfferOnMeals(HashMap<String, Boolean> offerOnMeals) {
        this.offerOnMeals = offerOnMeals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Offer offer = (Offer) o;

        if (offerID != null ? !offerID.equals(offer.offerID) : offer.offerID != null) return false;
        if (restaurantID != null ? !restaurantID.equals(offer.restaurantID) : offer.restaurantID != null)
            return false;
        if (userID != null ? !userID.equals(offer.userID) : offer.userID != null) return false;
        if (offerName != null ? !offerName.equals(offer.offerName) : offer.offerName != null)
            return false;
        if (offerDescription != null ? !offerDescription.equals(offer.offerDescription) : offer.offerDescription != null)
            return false;
        if (offerAtLunch != null ? !offerAtLunch.equals(offer.offerAtLunch) : offer.offerAtLunch != null)
            return false;
        if (offerAtDinner != null ? !offerAtDinner.equals(offer.offerAtDinner) : offer.offerAtDinner != null)
            return false;
        if (offerStart != null ? !offerStart.equals(offer.offerStart) : offer.offerStart != null)
            return false;
        if (offerStop != null ? !offerStop.equals(offer.offerStop) : offer.offerStop != null)
            return false;
        if (offerOnWeekDays != null ? !offerOnWeekDays.equals(offer.offerOnWeekDays) : offer.offerOnWeekDays != null)
            return false;
        if (offerForTotal != null ? !offerForTotal.equals(offer.offerForTotal) : offer.offerForTotal != null)
            return false;
        if (offerForMeal != null ? !offerForMeal.equals(offer.offerForMeal) : offer.offerForMeal != null)
            return false;
        if (offerForCategory != null ? !offerForCategory.equals(offer.offerForCategory) : offer.offerForCategory != null)
            return false;
        if (offerOnCategories != null ? !offerOnCategories.equals(offer.offerOnCategories) : offer.offerOnCategories != null)
            return false;
        if (offerOnMeals != null ? !offerOnMeals.equals(offer.offerOnMeals) : offer.offerOnMeals != null)
            return false;
        if (offerEnabled != null ? !offerEnabled.equals(offer.offerEnabled) : offer.offerEnabled != null)
            return false;
        return !(offerPercentage != null ? !offerPercentage.equals(offer.offerPercentage) : offer.offerPercentage != null);

    }

    @Override
    public int hashCode() {
        int result = offerID != null ? offerID.hashCode() : 0;
        result = 31 * result + (restaurantID != null ? restaurantID.hashCode() : 0);
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        result = 31 * result + (offerName != null ? offerName.hashCode() : 0);
        result = 31 * result + (offerDescription != null ? offerDescription.hashCode() : 0);
        result = 31 * result + (offerAtLunch != null ? offerAtLunch.hashCode() : 0);
        result = 31 * result + (offerAtDinner != null ? offerAtDinner.hashCode() : 0);
        result = 31 * result + (offerStart != null ? offerStart.hashCode() : 0);
        result = 31 * result + (offerStop != null ? offerStop.hashCode() : 0);
        result = 31 * result + (offerOnWeekDays != null ? offerOnWeekDays.hashCode() : 0);
        result = 31 * result + (offerForTotal != null ? offerForTotal.hashCode() : 0);
        result = 31 * result + (offerForMeal != null ? offerForMeal.hashCode() : 0);
        result = 31 * result + (offerForCategory != null ? offerForCategory.hashCode() : 0);
        result = 31 * result + (offerOnCategories != null ? offerOnCategories.hashCode() : 0);
        result = 31 * result + (offerOnMeals != null ? offerOnMeals.hashCode() : 0);
        result = 31 * result + (offerEnabled != null ? offerEnabled.hashCode() : 0);
        result = 31 * result + (offerPercentage != null ? offerPercentage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "offerID='" + offerID + '\'' +
                ", restaurantID='" + restaurantID + '\'' +
                ", userID='" + userID + '\'' +
                ", offerName='" + offerName + '\'' +
                ", offerDescription='" + offerDescription + '\'' +
                ", offerAtLunch=" + offerAtLunch +
                ", offerAtDinner=" + offerAtDinner +
                ", offerStartDate=" + offerStart +
                ", offerStopDate=" + offerStop +
                ", offerOnWeekDays=" + offerOnWeekDays +
                ", offerForTotal=" + offerForTotal +
                ", offerForMeal=" + offerForMeal +
                ", offerForCategory=" + offerForCategory +
                ", offerOnCategories=" + offerOnCategories +
                ", offerOnMeals=" + offerOnMeals +
                ", offerEnabled=" + offerEnabled +
                ", offerPercentage=" + offerPercentage +
                '}';
    }
}