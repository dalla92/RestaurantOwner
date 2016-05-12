package it.polito.group2.restaurantowner.user.order;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import it.polito.group2.restaurantowner.R;

/**
 * Created by Filippo on 12/05/2016.
 */
public class CartAdditionAdapter extends ArrayAdapter<AdditionModel> {

    private final List<AdditionModel> list;
    private final Activity context;

    public CartAdditionAdapter(Activity context, List<AdditionModel> list) {
        super(context, R.layout.fragment_order_cart_meal_addition, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.fragment_order_addition_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getName());
        return view;
    }
}