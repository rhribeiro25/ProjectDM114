package br.com.renan.projetodm114.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.models.InterestProduct;
import br.com.renan.projetodm114.tasks.InterestProductEvents;
import br.com.renan.projetodm114.tasks.InterestProductTasks;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class InterestProductAddFragment extends Fragment implements InterestProductEvents {
    private InterestProduct interestProduct;
    protected EditText productCode;
    protected EditText priceProduct;
    private InterestProductTasks interestProductTasks;

    public InterestProductAddFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interest_product_add,
                container, false);
        getActivity().setTitle("Cadastro de Produtos de Interesse");

        interestProduct = new InterestProduct();

        Button addButton = rootView.findViewById(R.id.button_add);

        Button backButton = rootView.findViewById(R.id.button_back);

        productCode = rootView.findViewById(R.id.editTextProductCode);

        priceProduct = rootView.findViewById(R.id.editTextProductPrice);

        SharedPreferences shared = getActivity().getApplicationContext().getSharedPreferences("credenciais", Context.MODE_PRIVATE);

        String userLogged = shared.getString(getContext().getString(R.string.pref_gcm_user_login),
                getContext().getString(R.string.pref_gcm_default_username));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                addInterestProduct();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        interestProduct.setEmail(userLogged);
        interestProduct.setId(1);

        if (CheckNetworkConnection.isNetworkConnected(getActivity())) {
            interestProductTasks = new InterestProductTasks(getActivity(), this);
        }

        return rootView;
    }

    @Override
    public void getInterestProductsFinished(List<InterestProduct> interestProducts) {
    }

    @Override
    public void getInterestProductsFailed(WebServiceResponse webServiceResponse) {
    }

    @Override
    public void deleteInterestProductsFinished() {
    }

    @Override
    public void deleteInterestProductsFailed(WebServiceResponse webServiceResponse) {

    }

    @Override
    public void addInterestProductsFinished() {
        Toast.makeText(getActivity(),
                "Produto de interesse adicionado com sucesso!",
                Toast.LENGTH_SHORT).show();
        productCode.setText("");
        priceProduct.setText("");
    }

    @Override
    public void addInterestProductsFailed(WebServiceResponse webServiceResponse) {
        Toast.makeText(getActivity(), "Falha ao adicionar produto de interesse" +
                webServiceResponse.getResultMessage() + " - Código do erro: " +
                webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    public void closeFragment() {
        int backStackEntryCount;
        backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        getFragmentManager().popBackStack();
    }

    public void addInterestProduct() {
        if((productCode.getText() != null && !productCode.getText().toString().equals(""))
                && (priceProduct.getText() != null && !priceProduct.getText().toString().equals(""))) {

            interestProduct.setCode(productCode.getText().toString());
            interestProduct.setPrice(Double.parseDouble(priceProduct.getText().toString()));

            Gson gson = new Gson();

            if (CheckNetworkConnection.isNetworkConnected(getActivity())) {
                interestProductTasks.addInterestProduct(gson.toJson(interestProduct));
            }
        } else
        {
            Toast.makeText(getActivity(),
                    "Id ou preço do produto vazio, por favor preencher!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

