package it.polito.group2.restaurantowner.owner.reservations;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.FirebaseUtil;
import it.polito.group2.restaurantowner.Utils.RemoveListenerUtil;
import it.polito.group2.restaurantowner.firebasedata.Order;

public class OrderFragment extends Fragment {

    private ArrayList<Order> orderList = null;
    private ArrayList<Order> orderListDate = null;
    String restaurantID;

    private Query q;
    private ValueEventListener l;
    private ProgressDialog mProgressDialog = null;

    private Calendar targetDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.owner_reservations_fragment_order, container, false);

        Bundle bundle = getArguments();
        restaurantID = bundle.getString("restaurant_id");

        mProgressDialog = FirebaseUtil.initProgressDialog(getActivity());
        FirebaseUtil.showProgressDialog(mProgressDialog);

        targetDate = Calendar.getInstance();

        if(orderList==null)
            orderList = new ArrayList<>();

        q = FirebaseUtil.getOrdersByRestaurantRef(restaurantID);
        l = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList = new ArrayList<>();
                orderListDate = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Order res = data.getValue(Order.class);
                    orderList.add(res);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(res.getOrder_date());
                    if (isEqualTo(c, targetDate))
                        orderListDate.add(res);
                }
                FirebaseUtil.hideProgressDialog(mProgressDialog);
                setOrderList(getView());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        q.addValueEventListener(l);
    }

    @Override
    public void onStop() {
        super.onStop();
        RemoveListenerUtil.remove_value_event_listener(q, l);
    }

    public void changeData(Calendar date){
        targetDate = date;
        orderListDate = new ArrayList<>();
        for(Order res: orderList){
            Calendar c =  Calendar.getInstance();
            c.setTimeInMillis(res.getOrder_date());
            if(isEqualTo(c, date))
                orderListDate.add(res);
        }

        setOrderList(getView());
    }

    private boolean isEqualTo(Calendar target, Calendar date) {
        return (target.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                target.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
                target.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH));
    }

    private void setOrderList(View v) {
        final RecyclerView list = (RecyclerView) v.findViewById(R.id.order_list);
        TextView empty_lable = (TextView) v.findViewById((R.id.empty_list));
        if(orderListDate.size() <= 0) {
            empty_lable.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            empty_lable.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            list.setLayoutManager(new LinearLayoutManager(getContext()));
            list.setNestedScrollingEnabled(false);
            OrderAdapter adapter = new OrderAdapter(this.getContext(), orderListDate);
            list.setAdapter(adapter);
        }
    }

}
