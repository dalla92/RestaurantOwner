package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.MealAddition;
import it.polito.group2.restaurantowner.firebasedata.Meal;

/**
 * Created by Alessio on 12/04/2016.
 */
public class Adapter_Meals extends RecyclerView.Adapter<Adapter_Meals.MealViewHolder> implements ItemTouchHelperAdapter {
    private Activity activity;
    private static ArrayList<Meal> meals;
    private static LayoutInflater inflater = null;
    public String meal_name;
    public int index;
    public String restaurant_id;
    public Meal meal_to_delete;

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView MealImage;
        TextView MealName;
        TextView MealPrice;
        ImageView Type1;
        ImageView Type2;
        Switch availability;

        MealViewHolder(View itemView) {
            super(itemView);
            MealImage = (ImageView) itemView.findViewById(R.id.meal_image);
            MealName = (TextView) itemView.findViewById(R.id.meal_name);
            MealPrice = (TextView) itemView.findViewById(R.id.meal_price);
            Type1 = (ImageView) itemView.findViewById(R.id.meal_type1);
            Type2 = (ImageView) itemView.findViewById(R.id.meal_type2);
            availability = (Switch) itemView.findViewById(R.id.meal_availability);
        }
    }

    public Adapter_Meals(Activity activity, int textViewResourceId, ArrayList<Meal> meals, String restaurant_id) {
        try {
            this.activity = activity;
            this.meals = meals;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.restaurant_id = restaurant_id;
        } catch (Exception e) {
        }
        Log.d("ccc", "CALLED WITH SUCCESS1");
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meal_layout, viewGroup, false);
        MealViewHolder pvh = new MealViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MealViewHolder MealViewHolder, int i) {

        index = i;

        if (meals.get(i).getMeal_thumbnail() != null && !meals.get(i).getMeal_thumbnail().equals(""))
            MealViewHolder.MealImage.setImageURI(Uri.parse(meals.get(i).getMeal_thumbnail()));
        MealViewHolder.MealName.setText(meals.get(i).getMeal_name());
        MealViewHolder.MealPrice.setText(String.valueOf(meals.get(i).getMeal_price()));
        /*
        if(meals.get(i).getType1()!=null)
            MealViewHolder.Type1.setImageResource(Integer.parseInt(meals.get(i).getType1()));
        if(meals.get(i).getType2()!=null)
            MealViewHolder.Type2.setImageResource(Integer.parseInt(meals.get(i).getType2()));
        */
        MealViewHolder.availability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        MealViewHolder.availability.setOnCheckedChangeListener(null);
        MealViewHolder.availability.setChecked(meals.get(i).is_meal_available());
        MealViewHolder.availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((Switch)v).isChecked();
                String meal_key = meals.get(index).getMeal_id();
                FirebaseDatabase ref = FirebaseDatabase.getInstance();
                DatabaseReference ref2 = ref.getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/" + meal_key + "/_meal_available");
                ref2.setValue(checked);
                ((Switch)v).setChecked(checked);
            }
        });
        /*
        MealViewHolder.availability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String meal_key = meals.get(index).getMeal_id();
                DatabaseReference ref = new DatabaseReference("https://have-break-9713d.firebaseio.com/meals/" + meal_key);
                ref.getReference("_meal_available").setValue(buttonView.isPressed());
                /*
                String meal_key = meals.get(index).getMeal_id();
                DatabaseReference ref = new DatabaseReference("https://have-break-9713d.firebaseio.com/meals/" + meal_key);
                if (buttonView.isPressed() && meals.get(index).is_meal_available() == true)
                    ref.getReference("_meal_available").setValue(false);
                if (buttonView.isPressed() && meals.get(index).is_meal_available()==false)
                    ref.getReference("_meal_available").setValue(true);
                if(!buttonView.isPressed() && meals.get(index).is_meal_available()==true)
                    ref.getReference("_meal_available").setValue(true);
                if(!buttonView.isPressed() && meals.get(index).is_meal_available()==false)
                    ref.getReference("_meal_available").setValue(false);

            }
        });
        //MealViewHolder.availability.setEnabled();
        */
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void addItem(int position, Meal m) {
        meals.add(position, m);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, meals.size());
    }

    public void removeItem(int position) {
        meals.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, meals.size());
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Meal prev = meals.remove(fromPosition);
        meals.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Confirmation!");
        alert.setMessage("Are you sure you want to delete the restaurant?\nThe operation cannot be undone!");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String meal_key = meals.get(position).getMeal_id();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://have-break-9713d.firebaseio.com/meals/" + meal_key);
                //delete
                ref.setValue(null);
                removeItem(position);
                dialog.dismiss();

            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alert.show();

    }
}
