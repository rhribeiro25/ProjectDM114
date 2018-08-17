package br.com.renan.projetodm114.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.models.Order;

public class OrderAdapter extends BaseAdapter {
    private final Activity activity;
    List<Order> orders;
    public OrderAdapter(Activity activity, List<Order> orders) {
        this.activity = activity;
        this.orders = orders;
    }
    @Override
    public int getCount() {
        return orders.size();
    }
    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }
    @Override
    public long getItemId(int position) {
        return orders.get(position).getId();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = activity.getLayoutInflater().inflate(
                R.layout.order_list_item, null);
        Order order = orders.get(position);
        int numberItens = 0;
        for(int i = 0; i < order.getOrderItems().size(); i++){
            numberItens++;
        }

        TextView orderListItemId = view.
                findViewById(R.id.orderListItemId);
        orderListItemId.setText(Long.toString(order.getId()));

        TextView orderListItemFretePrice = view.
                findViewById(R.id.orderListFreightPrice);
        orderListItemFretePrice.setText(Double.toString(order.getFreightPrice()));

        TextView orderListItemNumberItens = view.
                findViewById(R.id.orderListNumberItens);
        orderListItemNumberItens.setText(Integer.toString(numberItens));

        return view;
    }
}
