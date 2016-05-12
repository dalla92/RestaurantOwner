package it.polito.group2.restaurantowner.user.order;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 10/05/2016.
 */
public class MealAdapter extends ArrayAdapter<MealModel> {

    private final List<MealModel> modelList;
    private final Activity context;

    public MealAdapter(Activity context, List<MealModel> list) {
        super(context, R.layout.order_fragment_meal_item, list);
        this.context = context;
        this.modelList = list;
    }

    static class ViewHolder {
        protected TextView text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.order_fragment_meal_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(modelList.get(position).getName());
        return view;
    }
}