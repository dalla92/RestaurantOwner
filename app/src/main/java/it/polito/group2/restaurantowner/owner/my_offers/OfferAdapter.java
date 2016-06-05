package it.polito.group2.restaurantowner.owner.my_offers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Offer;

/**
 * Created by Filippo on 05/06/2016.
 */
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private final ArrayList<Offer> offerList;

    public OfferAdapter(ArrayList<Offer> list) {
        this.offerList = list;
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView enabled;

        //TODO aggiungere tutti gli altri campi

        public OfferViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.offer_name);
            enabled = (TextView) itemView.findViewById(R.id.offer_enabled);
        }
    }

    @Override
    public OfferAdapter.OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_myoffers_activity_offer_item, parent, false);
        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OfferAdapter.OfferViewHolder holder, int position) {
        holder.name.setText(offerList.get(position).getOfferName());
        if(offerList.get(position).getOfferEnabled())
            holder.enabled.setVisibility(View.VISIBLE);
        else
            holder.enabled.setVisibility(View.GONE);

        //TODO aggiungere tutti gli altri campi nella parte espandibile
        //TODO aggiungere l'animazione nell'espanzione dei dettagli
        //TODO aggiungere la funzione per espandere
        //TODO aggiungere la funzione per rimandare alla modifica
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

}