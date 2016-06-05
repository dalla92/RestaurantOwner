package it.polito.group2.restaurantowner.user.restaurant_list;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Daniele on 05/06/2016.
 */
public class SendNotificationAsync extends AsyncTask<String, Void, Void> {

    protected Void doInBackground(String... params) {
        try{
            String resName = params[0];
            String resId = params[1];
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoInput(true);
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Authorization", "key=AIzaSyD0qNOLTZMm52-leXiiydHslS22dY8I9pI");
            client.setDoOutput(true);
            JSONObject root = new JSONObject();
            JSONObject msg = new JSONObject();
            msg.put("body",resName);
            msg.put("title","New offer from your favourite restaurant!");

            root.put("to","/topics/asd");
            root.put("notification", msg);

            String str = root.toString();

            byte[] outputBytes = str.getBytes("UTF-8");
            OutputStream os = client.getOutputStream();
            os.write(outputBytes);

            int responseCode = client.getResponseCode();
            String response = "";
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
