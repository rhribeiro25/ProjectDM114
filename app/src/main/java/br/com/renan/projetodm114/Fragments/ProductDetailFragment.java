package br.com.renan.projetodm114.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.models.Product;
import br.com.renan.projetodm114.tasks.ProductTasks;
import br.com.renan.projetodm114.tasks.ProductEvents;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class ProductDetailFragment extends Fragment implements ProductEvents {
    private TextView textViewProductId;
    private TextView textViewProductName;
    private TextView textViewProductDescription;
    private TextView textViewProductCode;
    private TextView textViewProductPrice;
    private Button buttonBack;

    public ProductDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_detail,
                container, false);
        getActivity().setTitle("Detalhe do produto");

        textViewProductId = rootView.findViewById(R.id.textViewProductId);
        textViewProductName = rootView.findViewById(R.id.textViewProductName);
        textViewProductDescription = rootView.findViewById(R.id.textViewProductDescription);
        textViewProductCode = rootView.findViewById(R.id.textViewProductCode);
        textViewProductPrice = rootView.findViewById(R.id.textViewProductPrice);
        buttonBack = rootView.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        String orderCode;
        Bundle bundle = this.getArguments();
        if ((bundle != null) && (bundle.containsKey("product_code"))) {
            orderCode = bundle.getString("product_code");

            if (CheckNetworkConnection.isNetworkConnected(getActivity())) {
                ProductTasks orderTasks = new ProductTasks(getActivity(), this);
                orderTasks.getProductByCode(orderCode);
            }
        }

        return rootView;
    }

    public void closeFragment() {
        int backStackEntryCount;
        backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        getFragmentManager().popBackStack();
    }

    @Override
    public void getProductsFinished(List<Product> products) {
    }

    @Override
    public void getProductsFailed(WebServiceResponse webServiceResponse) {
    }

    @Override
    public void getProductByCodeFinished(Product product) {
        textViewProductId.setText(String.valueOf(product.getId()));
        textViewProductName.setText(product.getName());
        textViewProductDescription.setText(product.getDescription());
        textViewProductCode.setText(product.getCode());
        textViewProductPrice.setText(String.valueOf(product.getPrice()));
    }

    @Override
    public void getProductByCodeFailed(WebServiceResponse webServiceResponse) {
        Toast.makeText(getActivity(), "Falha na consulta dos detalhes do produto" +
                webServiceResponse.getResultMessage() + " - Código do erro: " +
                webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }
}

