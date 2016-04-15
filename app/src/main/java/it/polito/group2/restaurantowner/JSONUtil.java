package it.polito.group2.restaurantowner;

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

/**
 * Created by Daniele on 13/04/2016.
 */
public class JSONUtil {

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
        for(int i=0; i < jsonArray.length(); i++){
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

    public static ArrayList<TableReservation> readJSONTableResList(Context mContext, Calendar targetDate, String targetRestaurantId) throws JSONException{
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
        for(int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String restaurantId = jsonObject.optString("RestaurantID");
            if(!restaurantId.equals(targetRestaurantId))
                continue;

            String date = jsonObject.optString("Date");
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(timeFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(     !(c.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                    c.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH) &&
                    c.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH))  )
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

    public static ArrayList<TakeAwayReservation> readJSONTakeAwayResList(Context mContext, Calendar targetDate, String targetRestaurantId) throws JSONException{
        String json = null;
        ArrayList<TakeAwayReservation> takeAwayResList = new ArrayList<>();
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
        JSONArray jsonArray = jobj.optJSONArray("TableReservations");
        //Iterate the jsonArray and print the info of JSONObjects
        for(int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String restaurantId = jsonObject.optString("RestaurantID");
            if(!restaurantId.equals(targetRestaurantId))
                continue;

            String date = jsonObject.optString("Date");
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(timeFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(     !(c.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                    c.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH) &&
                    c.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH))  )
                continue;

            String username = jsonObject.optString("Username");
            String notes = jsonObject.optString("Notes");
            String takeAwayReservationId = jsonObject.optString("TakeAwayReservationID");
            ArrayList<OrderedMeal> orderedMeals = readJSONTakeAwayMeal(mContext, takeAwayReservationId);

            TakeAwayReservation takeAwayRes = new TakeAwayReservation(username, orderedMeals, c, notes, restaurantId, takeAwayReservationId);
            takeAwayResList.add(takeAwayRes);
        }
        return takeAwayResList;
    }

    private static ArrayList<OrderedMeal> readJSONTakeAwayMeal(Context mContext, String targetTakeAwayReservationId) throws JSONException {
        String json = null;
        ArrayList<OrderedMeal> orderedMealList = new ArrayList<>();
        FileInputStream fis = null;
        String FILENAME = "takeAwayMeal.json";
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
        JSONArray jsonArray = jobj.optJSONArray("TableReservations");
        //Iterate the jsonArray and print the info of JSONObjects
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String takeAwayReservationId = jsonObject.optString("TakeAwayReservationID");
            if (!takeAwayReservationId.equals(targetTakeAwayReservationId))
                continue;

            String mealName = jsonObject.optString("MealName");
            int quantity = jsonObject.optInt("quantity");
            OrderedMeal orderedMeal = new OrderedMeal(mealName, quantity);
            orderedMealList.add(orderedMeal);
        }
        return orderedMealList;
    }



    public static void saveJSONServiceList(Context mContext, List<RestaurantService> serList) throws JSONException {
        String FILENAME = "serviceList.json";
        JSONArray jarray = new JSONArray();
        for(RestaurantService ser : serList){
            JSONObject jres = new JSONObject();
            jres.put("RestaurantID",ser.getRestaurantId());
            jres.put("Name",ser.getName());
            jres.put("Attribute",ser.getAttribute());
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

    public static ArrayList<RestaurantService> readJSONServicesList(Context mContext) throws JSONException {
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
        for(int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            RestaurantService res = new RestaurantService();
            res.setRestaurantId(jsonObject.optString("RestaurantID"));
            res.setName(jsonObject.optString("Name"));
            res.setAttribute(jsonObject.optString("Attribute"));

            resList.add(res);
        }
        return resList;
    }


    public static void saveJSONOpenTimeList(Context mContext, List<OpenTime> otList) throws JSONException {
        String FILENAME = "openTime.json";
        JSONArray jarray = new JSONArray();
        for(OpenTime ot : otList){
            JSONObject jres = new JSONObject();
            jres.put("RestaurantID",ot.getRestaurantId());
            jres.put("CloseHour",ot.getCloseHour());
            jres.put("DayOfWeek",ot.getDayOfWeek());
            jres.put("OpenHour",ot.getOpenHour());
            jres.put("Type",ot.getType());
            jres.put("isOpen",ot.isOpen());
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

    public static ArrayList<OpenTime> readJSONOpenTimeList(Context mContext) throws JSONException {
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
        for(int i=0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            OpenTime ot = new OpenTime();
            ot.setRestaurantId(jsonObject.optString("RestaurantID"));
            ot.setCloseHour(jsonObject.optString("CloseHour"));
            ot.setDayOfWeek(jsonObject.optInt("DayOfWeek"));
            ot.setOpenHour(jsonObject.optString("OpenHour"));
            ot.setType(jsonObject.optString("Type"));
            ot.setIsOpen(jsonObject.optBoolean("isOpen"));

            otList.add(ot);
        }
        return otList;
    }
}
