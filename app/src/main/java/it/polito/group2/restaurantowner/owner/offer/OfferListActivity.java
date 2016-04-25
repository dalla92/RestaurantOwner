package it.polito.group2.restaurantowner.owner.offer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.owner.JSONUtil;

public class OfferListActivity extends AppCompatActivity {

    private static final int ADD_REQUEST = 1;
    private ArrayList<Offer> offer_list;
    private BaseAdapter adapter;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        restaurantId = getIntent().getExtras().getString("restaurant_id");
        //createFakeData();
        offer_list = getDataJson();

        TextView offer_title = (TextView) findViewById(R.id.offer_list_title);
        if(offer_list.isEmpty()) {
            offer_title.setVisibility(View.VISIBLE);
            offer_title.setText(getString(R.string.no_offer));
        }
        else
            offer_title.setVisibility(View.GONE);

        ListView lv = (ListView) findViewById(R.id.offer_list_view);
        adapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return offer_list.size();
            }

            @Override
            public boolean isEnabled(int position){
                return false;
            }

            @Override
            public Object getItem(int position) {
                return offer_list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    LayoutInflater inflater = LayoutInflater.from(OfferListActivity.this);
                    convertView = inflater.inflate(R.layout.offer_item, parent, false);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                TextView name = (TextView) convertView.findViewById(R.id.offer_name);
                TextView desc = (TextView) convertView.findViewById(R.id.offer_desc);
                TextView from = (TextView) convertView.findViewById(R.id.offer_from);
                TextView to = (TextView) convertView.findViewById(R.id.offer_to);
                TextView lunch_dinner = (TextView) convertView.findViewById(R.id.offer_lunch_dinner);

                Offer offer = offer_list.get(position);
                name.setText(offer.getName());
                desc.setText(offer.getDescription());
                from.setText(dateFormat.format(offer.getFrom().getTime()));
                to.setText(dateFormat.format(offer.getTo().getTime()));
                if(offer.isLunch() && offer.isDinner())
                    lunch_dinner.setText(getString(R.string.lunch) + "/" + getString(R.string.dinner));
                else if(offer.isLunch())
                    lunch_dinner.setText(getString(R.string.lunch));
                else
                    lunch_dinner.setText(getString(R.string.dinner));

                ImageView delete = (ImageView) convertView.findViewById(R.id.offer_delete);
                Calendar today = Calendar.getInstance();
                Calendar target = offer.getTo();
                if(target.after(today.getTime()) ||
                        (target.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                         target.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                         target.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))){

                    delete.setClickable(true);
                    delete.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder alert = new AlertDialog.Builder(OfferListActivity.this);
                            alert.setTitle("Confirmation!");
                            alert.setMessage("Are you sure you want to delete the offer?\nThe operation cannot be undone!");
                            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    offer_list.remove(position);
                                    notifyDataSetChanged();
                                    saveDataToJson(offer_list);
                                    dialog.dismiss();

                                }
                            });
                            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });

                            alert.show();
                        }
                    });

                }
                else{
                    delete.setClickable(false);
                    delete.setVisibility(View.GONE);
                }
                return convertView;
            }
        };

        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_offer_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.icon_new_offer) {
            Intent intent = new Intent(getApplicationContext(), AddOfferActivity.class);
            startActivityForResult(intent, ADD_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createFakeData(){
        ArrayList<Offer> offers = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        Offer offer1 = new Offer(UUID.randomUUID().toString(), restaurantId, "Offer 1", "50% off on the dinner menu", today, today, false, true);
        Offer offer2 = new Offer(UUID.randomUUID().toString(), restaurantId, "Offer 2", "50% off on the lunch menu", today, today, true, false);
        Offer offer3 = new Offer(UUID.randomUUID().toString(), restaurantId, "Offer 3", "30% off on the menu", today, today, true, true);
        Offer offer4 = new Offer(UUID.randomUUID().toString(), restaurantId, "Offer 4", "every 20 euro a beer for free", today, today, true, true);
        Offer offer5 = new Offer(UUID.randomUUID().toString(), restaurantId, "Offer 5", "50% off on the dinner menu", today, today, false, true);
        offers.add(offer1);
        offers.add(offer2);
        offers.add(offer3);
        offers.add(offer4);
        offers.add(offer5);

        try {
            JSONUtil.saveJSONOfferList(this, offers);
        } catch (JSONException e) {
            Log.d("failed", "problema nel createFakeData delle offer");
        }
    }
    private ArrayList<Offer> getDataJson() {

        try {
            return JSONUtil.readJSONOfferList(this, restaurantId);
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                String desc = data.getStringExtra("description");
                String from = data.getStringExtra("from");
                String to = data.getStringExtra("to");
                String offerId = data.getStringExtra("offerID");
                boolean lunch = data.getBooleanExtra("lunch", false);
                boolean dinner = data.getBooleanExtra("dinner", false);

                Calendar from_date = getDateFromText(from);
                Calendar to_date = getDateFromText(to);
                Offer offer =new Offer(offerId, restaurantId, name, desc, from_date, to_date, lunch, dinner);
                offer_list.add(offer);
                adapter.notifyDataSetChanged();
                saveDataToJson(offer_list);
            }
        }
    }

    private void saveDataToJson(ArrayList<Offer> offers) {
        try {
            JSONUtil.saveJSONOfferList(this, offers);
        } catch (JSONException e) {
            Log.d("failed", "problema nel saveDataJson delle offer");
        }
    }

    private Calendar getDateFromText(String notValidDate){
        Calendar cal = Calendar.getInstance();
        if(notValidDate.equals("Today")){
            return cal;
        }

        try {
            String[] words = notValidDate.split("\\s+");
            String day = words[0];
            String month = getIntFromMonthName(words[1]);
            String year = words[2];

            Toast.makeText(OfferListActivity.this, words[1] + " " + month, Toast.LENGTH_SHORT).show();
            String validDate = day + "/" + month + "/" + year;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            cal.setTime(dateFormat.parse(validDate));
        }catch(ParseException pe){
            pe.printStackTrace();
        }

        return cal;
    }

    private String getIntFromMonthName(String monthName) throws ParseException {
        Date date = new SimpleDateFormat("MMMM").parse(monthName);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return Integer.toString(cal.get(Calendar.MONTH) + 1);
    }

}
