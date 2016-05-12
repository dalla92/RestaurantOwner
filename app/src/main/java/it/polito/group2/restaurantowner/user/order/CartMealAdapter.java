package it.polito.group2.restaurantowner.user.order;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 12/05/2016.
 */
public class CartMealAdapter extends ArrayAdapter<MealModel> {

    private final List<MealModel> modelList;
    private final Activity context;

    public CartMealAdapter(Activity context, List<MealModel> list) {
        super(context, R.layout.fragment_order_cart_meal, list);
        this.context = context;
        this.modelList = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected ListView list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.fragment_order_cart_meal, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.list = (ListView) view.findViewById(R.id.list_addition);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(modelList.get(position).getName());
        CartAdditionAdapter adapter = new CartAdditionAdapter(((Activity)context),modelList.get(position).getAdditionModel());
        holder.list.setAdapter(adapter);

        return view;
    }
}