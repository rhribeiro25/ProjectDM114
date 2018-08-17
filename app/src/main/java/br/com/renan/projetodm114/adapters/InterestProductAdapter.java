package br.com.renan.projetodm114.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.models.InterestProduct;
import br.com.renan.projetodm114.tasks.InterestProductEvents;
import br.com.renan.projetodm114.tasks.InterestProductTasks;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class InterestProductAdapter extends BaseAdapter {

        private final Activity activity;
        List<InterestProduct> interestProducts;
        public InterestProductAdapter(Activity activity, List<InterestProduct> interestProducts) {
            this.activity = activity;
            this.interestProducts = interestProducts;
        }
        @Override
        public int getCount() {
            return interestProducts.size();
        }
        @Override
        public Object getItem(int position) {
            return interestProducts.get(position);
        }
        @Override
        public long getItemId(int position) {
            return interestProducts.get(position).getId();
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = activity.getLayoutInflater().inflate(
                    R.layout.interest_product_item_list, null);
            final InterestProduct interestProduct = interestProducts.get(position);

            TextView interestProductListItemNumber = view.
                    findViewById(R.id.interestProductListItemNumber);
            interestProductListItemNumber.setText(Integer.toString(position + 1));

            TextView interestProductListItemId = view.
                    findViewById(R.id.interestProductListItemCode);
            interestProductListItemId.setText(interestProduct.getCode());

            TextView interestProductListItemPrice = view.
                    findViewById(R.id.interestProductListItemPrice);
            interestProductListItemPrice.setText(Double.toString(interestProduct.getPrice()));

            return view;
        }
}
