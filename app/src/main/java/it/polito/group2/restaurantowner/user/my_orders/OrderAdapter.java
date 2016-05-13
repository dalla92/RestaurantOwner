package it.polito.group2.restaurantowner.user.my_orders;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 13/05/2016.
 */
public class OrderAdapter extends ArrayAdapter<OrderModel> {

    private final List<OrderModel> modelList;
    private final Activity context;

    public OrderAdapter(Activity context, List<OrderModel> list) {
        super(context, R.layout.myorders_activity_order_item, list);
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
            view = inflator.inflate(R.layout.myorders_activity_order_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.list = (ListView) view.findViewById(R.id.list_meal);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        Date date = modelList.get(position).getOrder().getTimestamp().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        holder.text.setText(formatDate.format(date));
        MealAdapter adapter = new MealAdapter(((Activity)context),modelList.get(position).getMealList());
        holder.list.setAdapter(adapter);
        return view;
    }
}