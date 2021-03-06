package it.polito.group2.restaurantowner.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.group2.restaurantowner.owner.RestaurantService;

/**
 * Created by Daniele on 13/04/2016.
 */
public class JSONUtil {

    public static void saveJSONReviewList(ArrayList<Review> comments, Context context) throws JSONException {
        String FILENAME = "commentList.json";
        JSONArray jarray = new JSONArray();
        for (Review com : comments) {
            JSONObject jres = new JSONObject();
            jres.put("RestaurantId", com.getRestaurantId());
            jres.put("Date", com.getDate());
            jres.put("StarsNumber", com.getStars_number());
            jres.put("Comment", com.getComment());
            //jres.put("UserPhoto", com.getUserphoto());
            jres.put("UserId", com.getUserID());
            jres.put("ReviewId", com.getReviewID());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Comments", jarray);
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveJSONUsersList(ArrayList<User> users, Context context) throws JSONException {
        String FILENAME = "usersList.json";
        JSONArray jarray = new JSONArray();
        for (User us : users) {
            JSONObject jres = new JSONObject();
            //jres.put("Username",us.getUserID());
            jres.put("Password", us.getPassword());
            jres.put("FirstName", us.getFirst_name());
            jres.put("LastName", us.getLast_name());
            jres.put("Usermail", us.getEmail());
            jres.put("UserId", us.getId());
            jres.put("PhoneNum", us.getPhone_number());
            jres.put("VatNum", us.getVat_number());
            //photo?
            jres.put("IsOwner", us.getIsOwner());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Users", jarray);
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveJSONResList(Context mContext, ArrayList<Restaurant> resList) throws JSONException {
        String FILENAME = "restaurantList.json";
        JSONArray jarray = new JSONArray();
        for (Restaurant res : resList) {
            JSONObject jres = new JSONObject();
            jres.put("Address", res.getAddress());
            jres.put("Category", res.getCategory());
            jres.put("ClosestBus", res.getClosestBus());
            jres.put("ClosestMetro", res.getClosestMetro());
            jres.put("Fidelity", res.isFidelity());
            jres.put("Name", res.getName());
            jres.put("OrdersPerHour", res.getOrdersPerHour());
            jres.put("PhoneNum", res.getPhoneNum());
            jres.put("Photo", res.getPhotoUri());
            jres.put("Rating", res.getRating());
            jres.put("ReservationNumber", res.getReservationNumber());
            jres.put("ReservationPercentage", res.getReservedPercentage());
            jres.put("RestaurantId", res.getRestaurantId());
            jres.put("SquaredMeters", res.getSquaredMeters());
            jres.put("TableReservation", res.isTableReservation());
            jres.put("TableNum", res.getTableNum());
            jres.put("TakeAway", res.isTakeAway());
            jres.put("UserId", res.getUserId());
            jarray.put(jres);


        }
        JSONObject resObj = new JSONObject();
        resObj.put("Restaurants", jarray);
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<User> readJSONUsersList(Context context, String usermail) throws JSONException {
        ArrayList<User> users = new ArrayList<>();
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "usersList.json";
        try {
            fis = context.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return users;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Users"); //root tag?
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            User us = new User();
            if (usermail != null) {
                if (jsonObject.optString("Usermail").equals(usermail)) {
                    //us.setUserID(jsonObject.optString("Username"));
                    us.setPassword(jsonObject.optString("Password"));
                    us.setId(jsonObject.optString("UserId"));
                    us.setFirst_name(jsonObject.optString("FirstName"));
                    us.setLast_name(jsonObject.optString("LastName"));
                    us.setEmail(jsonObject.optString("Usermail"));
                    us.setPhone_number(jsonObject.optString("PhoneNum"));
                    us.setVat_number(jsonObject.optString("VatNum"));
                    //photo?
                    us.setIsOwner(jsonObject.optBoolean("IsOwner"));
                    users.add(us);
                }
            } else {
                //us.setUserID(jsonObject.optString("Username"));
                us.setPassword(jsonObject.optString("Password"));
                us.setId(jsonObject.optString("UserId"));
                us.setFirst_name(jsonObject.optString("FirstName"));
                us.setLast_name(jsonObject.optString("LastName"));
                us.setEmail(jsonObject.optString("Usermail"));
                us.setPhone_number(jsonObject.optString("PhoneNum"));
                us.setVat_number(jsonObject.optString("VatNum"));
                //photo?
                us.setIsOwner(jsonObject.optBoolean("IsOwner"));
                users.add(us);
            }
        }
        return users;
    }

    public static ArrayList<Bookmark> readBookmarkList(Context context, String user_id) throws JSONException {
        ArrayList<Bookmark> bookmarks = new ArrayList<>();
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "bookmarkList.json";
        try {
            fis = context.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return bookmarks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Bookmarks"); //root tag?
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Bookmark bo = new Bookmark();
            if (user_id != null) {
                if (jsonObject.optString("user_id").equals(user_id)) {
                    bo.setRestaurant_id(jsonObject.optString("restaurant_id"));
                    bo.setUser_id(jsonObject.optString("user_id"));
                    bookmarks.add(bo);
                }
            } else {
                bo.setRestaurant_id(jsonObject.optString("restaurant_id"));
                bo.setUser_id(jsonObject.optString("user_id"));
                bookmarks.add(bo);
            }
        }
        return bookmarks;
    }

    public static void saveBookmarksList(Context context, ArrayList<Bookmark> bookmarks) throws JSONException {
        String FILENAME = "bookmarkList.json";
        JSONArray jarray = new JSONArray();
        for (Bookmark boo : bookmarks) {
            JSONObject jres = new JSONObject();
            jres.put("restaurant_id", boo.getRestaurant_id());
            jres.put("user_id", boo.getUser_id());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Bookmarks", jarray);
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isBookmark(Context mContext, String targetUsername, String targetRestaurantId) throws JSONException {
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "bookmarkList.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Bookmarks");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String restaurantId = jsonObject.optString("RestaurantID");
            String username = jsonObject.optString("Username");
            if (restaurantId.equals(targetRestaurantId) && username.equals(targetUsername))
                return true;
        }
        return false;
    }

    public static ArrayList<Restaurant> readJSONResList(Context mContext) throws JSONException {
        String json = null;
        ArrayList<Restaurant> resList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "restaurantList.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return resList;
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Restaurants");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Restaurant res = new Restaurant();
            res.setAddress(jsonObject.optString("Address"));
            res.setCategory(jsonObject.optString("Category"));
            res.setClosestBus(jsonObject.optString("ClosestBus"));
            res.setClosestMetro(jsonObject.optString("ClosestMetro"));
            res.setFidelity(jsonObject.getBoolean("Fidelity"));
            res.setName(jsonObject.optString("Name"));
            res.setOrdersPerHour(jsonObject.optString("OrdersPerHour"));
            res.setPhoneNum(jsonObject.optString("PhoneNum"));
            res.setPhotoUri(jsonObject.optString("Photo"));
            res.setRating(jsonObject.optString("Rating"));
            res.setReservationNumber(jsonObject.optString("ReservationNumber"));
            res.setReservedPercentage(jsonObject.optString("ReservationPercentage"));
            res.setRestaurantId(jsonObject.optString("RestaurantId"));
            res.setSquaredMeters(jsonObject.optString("SquaredMeters"));
            res.setTableReservation(jsonObject.getBoolean("TableReservation"));
            res.setTableNum(jsonObject.optString("TableNum"));
            res.setTakeAway(jsonObject.getBoolean("TakeAway"));
            res.setUserId(jsonObject.optString("UserId"));
            resList.add(res);
        }
        return resList;
    }

    public static void saveJSONOfferList(Context mContext, ArrayList<Offer> offerList) throws JSONException {
        String FILENAME = "offer.json";
        JSONArray jarray = new JSONArray();
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (Offer offer : offerList) {
            JSONObject jres = new JSONObject();
            jres.put("RestaurantID", offer.getRestaurantId());
            jres.put("DateFrom", timeFormat.format(offer.getFrom().getTime()));
            jres.put("DateTo", timeFormat.format(offer.getTo().getTime()));
            jres.put("OfferName", offer.getName());
            jres.put("Description", offer.getDescription());
            jres.put("OfferID", offer.getOfferId());
            jres.put("Lunch", offer.isLunch());
            jres.put("Dinner", offer.isDinner());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("Offers", jarray);
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Offer> readJSONOfferList(Context mContext, String targetRestaurantId) throws JSONException {
        String json = null;
        ArrayList<Offer> offerList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "offer.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return offerList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Offers");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String restaurantId = jsonObject.optString("RestaurantID");
            if (!restaurantId.equals(targetRestaurantId))
                continue;

            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cFrom = Calendar.getInstance();
            Calendar cTo = Calendar.getInstance();

            String dateFrom = jsonObject.optString("DateFrom");
            String dateTo = jsonObject.optString("DateTo");
            try {
                cFrom.setTime(timeFormat.parse(dateFrom));
                cTo.setTime(timeFormat.parse(dateTo));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String offerName = jsonObject.optString("OfferName");
            String description = jsonObject.optString("Description");
            String offerId = jsonObject.optString("OfferID");
            boolean lunch = jsonObject.optBoolean("Lunch");
            boolean dinner = jsonObject.optBoolean("Dinner");

            Offer offer = new Offer(offerId, restaurantId, offerName, description, cTo, cFrom, lunch, dinner);
            offerList.add(offer);
        }

        return offerList;
    }

    public static ArrayList<Review> readJSONReviewList(Context mContext, String targetRestaurantId) throws JSONException {
        String json = null;
        ArrayList<Review> reviewList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "review.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return reviewList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Reviews");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String restaurantId = jsonObject.optString("RestaurantID");
            if (!restaurantId.equals(targetRestaurantId))
                continue;

            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Calendar reviewDate = Calendar.getInstance();

            String date = jsonObject.optString("Date");
            try {
                reviewDate.setTime(timeFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            float starsNumber = (float) jsonObject.optDouble("starsNumber");
            String comment = jsonObject.optString("Comment");
            String reviewID = jsonObject.optString("Id");
            String username = jsonObject.optString("UserID");

            Review review = new Review(restaurantId, username, date, comment, reviewID, starsNumber);
            reviewList.add(review);
        }

        return reviewList;
    }


    public static ArrayList<TableReservation> readJSONTableResList(Context mContext, Calendar targetDate, String targetRestaurantId) throws JSONException {
        String json = null;
        ArrayList<TableReservation> tableResList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "tableReservation.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return tableResList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("TableReservations");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String restaurantId = jsonObject.optString("RestaurantID");
            if (!restaurantId.equals(targetRestaurantId))
                continue;

            String date = jsonObject.optString("Date");
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(timeFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!(c.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                    c.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH) &&
                    c.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH)))
                continue;

            String username = jsonObject.optString("Username");
            String notes = jsonObject.optString("Notes");
            String tableReservationId = jsonObject.optString("TableReservationID");
            int seatReserved = jsonObject.optInt("SeatReserved");

            TableReservation table_res = new TableReservation(username, seatReserved, c, notes, restaurantId, tableReservationId);
            tableResList.add(table_res);
        }
        return tableResList;
    }

    public static ArrayList<TableReservation> readJSONTableResUsList(Context mContext, String targetRestaurantId, String userId) throws JSONException {
        String json = null;
        ArrayList<TableReservation> tableResList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "tableReservation.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return tableResList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("TableReservations");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String restaurantId = jsonObject.optString("RestaurantID");
            if(targetRestaurantId!=null)
                if (!restaurantId.equals(targetRestaurantId))
                    continue;

            String date = jsonObject.optString("Date");
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(timeFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String username = jsonObject.optString("UserId");
            if(userId!=null)
                if (!username.equals(userId))
                    continue;
            String notes = jsonObject.optString("Notes");
            String tableReservationId = jsonObject.optString("TableReservationID");
            int seatReserved = jsonObject.optInt("SeatReserved");

            TableReservation table_res = new TableReservation(username, seatReserved, c, notes, restaurantId, tableReservationId);
            tableResList.add(table_res);
        }
        return tableResList;
    }

    public static void saveJSONTableResList(Context mContext, ArrayList<TableReservation> reservations) throws JSONException {
        String FILENAME = "tableReservation.json";
        JSONArray jarray = new JSONArray();
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (TableReservation res : reservations) {
            JSONObject jres = new JSONObject();
            jres.put("RestaurantID", res.getRestaurantId());
            jres.put("Date", timeFormat.format(res.getDate().getTime()));
            jres.put("UserId", res.getUserID());
            jres.put("Notes", res.getNotes());
            jres.put("SeatReserved", res.getN_people());
            jres.put("TableReservationID", res.getTableReservationId());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("TableReservations", jarray);
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveJSONTakeAwayResList(Context mContext, ArrayList<Order> reservations) throws JSONException {
        String FILENAME = "takeAwayReservation.json";
        JSONArray jarray = new JSONArray();
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Order res : reservations) {
            JSONObject jres = new JSONObject();
            jres.put("RestaurantID", res.getRestaurantID());
            jres.put("Date", timeFormat.format(res.getTimestamp().getTime()));
            jres.put("Username", res.getUserID());
            jres.put("Notes", res.getNote());
            jres.put("TakeAwayReservationID", res.getOrderID());
            saveJSONOrderedMeal(mContext, res.getMealList());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("TakeAwayReservations", jarray);
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Order> readJSONTakeAwayResList(Context mContext, Calendar targetDate, String targetRestaurantId) throws JSONException {
        String json = null;
        ArrayList<Order> takeAwayResList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "takeAwayReservation.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return takeAwayResList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("TakeAwayReservations");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String restaurantId = jsonObject.optString("RestaurantID");
            if (!restaurantId.equals(targetRestaurantId))
                continue;

            String date = jsonObject.optString("Date");
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(timeFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!(c.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                    c.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH) &&
                    c.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH)))
                continue;

            String username = jsonObject.optString("Username");
            String notes = jsonObject.optString("Notes");
            String takeAwayReservationId = jsonObject.optString("TakeAwayReservationID");
            ArrayList<OrderMeal> orderedMeals = readJSONOrderedMeal(mContext, takeAwayReservationId);

            //Order takeAwayRes = new Order(username, orderedMeals, c, notes, restaurantId, takeAwayReservationId);
            //takeAwayResList.add(takeAwayRes);
        }
        return takeAwayResList;
    }

    private static void saveJSONOrderedMeal(Context mContext, ArrayList<OrderMeal> ordered_meals) throws JSONException {
        String FILENAME = "orderedMeal.json";
        JSONArray jarray = new JSONArray();
        ArrayList<OrderMeal> orderedMealComplete = readJSONOrderedMealFull(mContext);
        orderedMealComplete.addAll(ordered_meals);
        for (OrderMeal meal : orderedMealComplete) {
            JSONObject jres = new JSONObject();
            jres.put("TakeAwayReservationID", meal.getOrderID());
            jres.put("MealName", meal.getMeal().getMeal_name());
            jres.put("quantity", meal.getQuantity());
            jarray.put(jres);
        }
        JSONObject resObj = new JSONObject();
        resObj.put("OrderedMeals", jarray);
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<OrderMeal> readJSONOrderedMeal(Context mContext, String targetTakeAwayReservationId) throws JSONException {
        String json = null;
        ArrayList<OrderMeal> orderedMealList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "orderedMeal.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return orderedMealList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("OrderedMeals");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String takeAwayReservationId = jsonObject.optString("TakeAwayReservationID");
            if (!takeAwayReservationId.equals(targetTakeAwayReservationId))
                continue;

            String mealName = jsonObject.optString("MealName");
            int quantity = jsonObject.optInt("quantity");
            OrderMeal orderedMeal = new OrderMeal(mealName, quantity, takeAwayReservationId);
            orderedMealList.add(orderedMeal);
        }
        return orderedMealList;
    }

    private static ArrayList<OrderMeal> readJSONOrderedMealFull(Context mContext) throws JSONException {
        String json = null;
        ArrayList<OrderMeal> orderedMealList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "orderedMeal.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return orderedMealList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("OrderedMeals");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String takeAwayReservationId = jsonObject.optString("TakeAwayReservationID");
            String mealName = jsonObject.optString("MealName");
            int quantity = jsonObject.optInt("quantity");
            OrderMeal orderedMeal = new OrderMeal(mealName, quantity, takeAwayReservationId);
            orderedMealList.add(orderedMeal);
        }
        return orderedMealList;
    }


    public static void saveJSONServiceList(Context mContext, List<RestaurantService> serList) throws JSONException {
        String FILENAME = "serviceList.json";
        JSONArray jarray = new JSONArray();
        for (RestaurantService ser : serList) {
            JSONObject jres = new JSONObject();
            jres.put("RestaurantID", ser.getRestaurantId());
            jres.put("Name", ser.getName());
            jres.put("Attribute", ser.getAttribute());
            jarray.put(jres);


        }
        JSONObject resObj = new JSONObject();
        resObj.put("RestaurantServices", jarray);
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<RestaurantService> readJSONServicesList(Context mContext, String restaurantID) throws JSONException {
        String json = null;
        ArrayList<RestaurantService> resList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "serviceList.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return resList;
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("RestaurantServices");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if (!jsonObject.optString("RestaurantID").equals(restaurantID)) {

                RestaurantService res = new RestaurantService();
                res.setRestaurantId(jsonObject.optString("RestaurantID"));
                res.setName(jsonObject.optString("Name"));
                res.setAttribute(jsonObject.optString("Attribute"));

                resList.add(res);
            }
        }
        return resList;
    }

    public static ArrayList<RestaurantService> readJSONServicesList(Context mContext) throws JSONException {
        return readJSONServicesList(mContext, "");
    }


    public static void saveJSONOpenTimeList(Context mContext, List<OpenTime> otList) throws JSONException {
        String FILENAME = "openTime.json";
        JSONArray jarray = new JSONArray();
        for (OpenTime ot : otList) {
            JSONObject jres = new JSONObject();
            jres.put("RestaurantID", ot.getRestaurantId());
            jres.put("CloseHour", ot.getCloseHour());
            jres.put("DayOfWeek", ot.getDayOfWeek());
            jres.put("OpenHour", ot.getOpenHour());
            jres.put("Type", ot.getType());
            jres.put("isOpen", ot.isOpen());
            jarray.put(jres);


        }
        JSONObject resObj = new JSONObject();
        resObj.put("OpenTimes", jarray);
        FileOutputStream fos = null;
        try {
            fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(resObj.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<OpenTime> readJSONOpenTimeList(Context mContext, String restaurantId) throws JSONException {
        String json = null;
        ArrayList<OpenTime> otList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "openTime.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return otList;
        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("OpenTimes");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!jsonObject.optString("RestaurantID").equals(restaurantId)) {
                OpenTime ot = new OpenTime();
                ot.setRestaurantId(jsonObject.optString("RestaurantID"));
                ot.setCloseHour(jsonObject.optString("CloseHour"));
                ot.setDayOfWeek(jsonObject.optInt("DayOfWeek"));
                ot.setOpenHour(jsonObject.optString("OpenHour"));
                ot.setType(jsonObject.optString("Type"));
                ot.setIsOpen(jsonObject.optBoolean("isOpen"));
                otList.add(ot);
            }
        }
        return otList;
    }

    public static ArrayList<OpenTime> readJSONOpenTimeList(Context mContext) throws JSONException {
        return readJSONOpenTimeList(mContext, null);
    }

    /* Filippo edits start */
    public static ArrayList<Review> readJsonReviewList(Context mContext, String restaurantID)
            throws JSONException {
        String json = null;
        ArrayList<Review> reviewList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "commentList.json";
        try {
            fis = mContext.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return reviewList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Comments");
        Review review;

        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.optString("RestaurantID").equals(restaurantID)) {
                review = new Review();
                SimpleDateFormat format = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
                Calendar date = Calendar.getInstance();
                try {
                    date.setTime(format.parse(jsonObject.optString("Date") + " " + jsonObject.optString("Time")));
                    review.setDate(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                reviewList.add(review);
            }
        }
        return reviewList;
    }
    /* Filippo edits stop */

    public static ArrayList<Meal> readJSONMeList(Context context, String restaurant_id) throws JSONException {
        //mealList.json
        ArrayList<Meal> meals = new ArrayList<Meal>();
        String json = null;
        FileInputStream fis = null;
        String FILENAME = "mealList.json";
        try {
            fis = context.openFileInput(FILENAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj = new JSONObject(json);
        JSONArray jsonArray = jobj.optJSONArray("Meals");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Meal me = new Meal();
            me.setRestaurantId(jsonObject.optString("RestaurantId"));
            me.setMealId(jsonObject.optString("MealId"));
            me.setMeal_photo(jsonObject.optString("MealPhoto"));
            me.setMeal_name(jsonObject.optString("MealName"));
            me.setMeal_price(jsonObject.optDouble("MealPrice"));
            me.setType1(jsonObject.optString("MealType1"));
            me.setType2(jsonObject.optString("MealType2"));
            me.setCategory(jsonObject.optString("Category"));
            me.setAvailable(jsonObject.getBoolean("MealAvailable"));
            me.setTake_away(jsonObject.getBoolean("MealTakeAway"));
            me.setCooking_time(jsonObject.optInt("MealCookingTime"));
            me.setDescription(jsonObject.getString("MealDescription"));
            if (me.getRestaurantId().equals(restaurant_id))
                meals.add(me);
        }
        //mealMealAdditions.json
        String json2 = null;
        FileInputStream fis2 = null;
        String FILENAME2 = "mealMealAddition.json";
        ArrayList<MealAddition> meals_additions = new ArrayList<MealAddition>();
        try {
            fis2 = context.openFileInput(FILENAME2);
            int size2 = fis2.available();
            byte[] buffer2 = new byte[size2];
            fis2.read(buffer2);
            fis2.close();
            json2 = new String(buffer2, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj2 = new JSONObject(json2);
        JSONArray jsonArray2 = jobj2.optJSONArray("MealsMealAdditions");
        for (int i = 0; i < jsonArray2.length(); i++) {
            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
            MealAddition ad = new MealAddition();
            if (jsonObject2.optString("RestaurantId").equals(restaurant_id)) {
                ad.setRestaurant_id(jsonObject2.optString("RestaurantId"));
                ad.setmeal_id(jsonObject2.optString("MealId"));
                ad.setName(jsonObject2.optString("MealAdditionName"));
                ad.setSelected(jsonObject2.getBoolean("MealAdditionSelected"));
                ad.setPrice(jsonObject2.optDouble("MealAdditionPrice"));
                meals_additions.add(ad);
            }
        }
        //mealCategories.json
        String json3 = null;
        FileInputStream fis3 = null;
        String FILENAME3 = "mealCategory.json";
        ArrayList<MealAddition> meals_categories = new ArrayList<MealAddition>();
        try {
            fis3 = context.openFileInput(FILENAME3);
            int size3 = fis3.available();
            byte[] buffer3 = new byte[size3];
            fis3.read(buffer3);
            fis3.close();
            json3 = new String(buffer3, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jobj3 = new JSONObject(json3);
        JSONArray jsonArray3 = jobj3.optJSONArray("MealsCategories");
        for (int i = 0; i < jsonArray3.length(); i++) {
            JSONObject jsonObject3 = jsonArray3.getJSONObject(i);
            MealAddition ad = new MealAddition();
            if (jsonObject3.optString("RestaurantId").equals(restaurant_id)) {
                ad.setRestaurant_id(jsonObject3.optString("RestaurantId"));
                ad.setmeal_id(jsonObject3.optString("MealId"));
                ad.setName(jsonObject3.optString("CategoryName"));
                ad.setSelected(jsonObject3.getBoolean("CategorySelected"));
                ad.setPrice(0);
                //ad.setPrice(jsonObject3.optDouble("CategoryPrice"));
                meals_categories.add(ad);
            }
        }

        return meals;
    }


}
