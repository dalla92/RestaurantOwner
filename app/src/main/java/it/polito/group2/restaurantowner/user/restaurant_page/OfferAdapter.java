package it.polito.group2.restaurantowner.user.restaurant_page;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Offer;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private ArrayList<Offer> offers;
    private Context context;

    public OfferAdapter(ArrayList<Offer> offers, Context context) {
        this.offers = offers;
        this.context = context;
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView name, desc, from, to, validity;

        public OfferViewHolder(View view){
            super(view);
            name = (TextView) itemView.findViewById(R.id.offer_name);
            desc = (TextView) itemView.findViewById(R.id.offer_desc);
            from = (TextView) itemView.findViewById(R.id.offer_from);
            to = (TextView) itemView.findViewById(R.id.offer_to);
            validity = (TextView) itemView.findViewById(R.id.offer_validity);
        }

    }

    @Override
    public OfferAdapter.OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_restaurant_offer, parent, false);
        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, int position) {
        holder.name.setText(offers.get(position).getOfferName());
        holder.desc.setText(offers.get(position).getOfferDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar start = Calendar.getInstance();
        Calendar stop = Calendar.getInstance();
        start.setTimeInMillis(offers.get(position).getOfferStart());
        stop.setTimeInMillis(offers.get(position).getOfferStop());

        holder.from.setText(dateFormat.format(start.getTime()));
        holder.to.setText(dateFormat.format(stop.getTime()));
        if(offers.get(position).getOfferAtLunch() && offers.get(position).getOfferAtDinner())
           holder.validity.setText(context.getString(R.string.valid_for) + context.getString(R.string.lunch) + "/" + context.getString(R.string.dinner));
        else if(offers.get(position).getOfferAtLunch())
            holder.validity.setText(context.getString(R.string.lunch));
        else
            holder.validity.setText(context.getString(R.string.dinner));
    }


    @Override
    public int getItemCount() {
        return offers.size();
    }
}
