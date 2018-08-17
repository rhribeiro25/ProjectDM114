package br.com.renan.projetodm114.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.adapters.ProductAdapter;
import br.com.renan.projetodm114.models.Product;
import br.com.renan.projetodm114.tasks.ProductEvents;
import br.com.renan.projetodm114.tasks.ProductTasks;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class ProductsFragment extends Fragment implements ProductEvents {
    protected ListView listViewProducts;
    private ArrayList<Product> products;

    public ProductsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products_list,
                container, false);
        getActivity().setTitle("Produtos");
        listViewProducts = rootView.
                findViewById(R.id.products_list);

        listViewProducts.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        Product productSelected = (Product)
                                listViewProducts.getItemAtPosition(position);
                        startProductDetail(productSelected.getCode());
                    }
                });

        if (CheckNetworkConnection.isNetworkConnected(getActivity())) {
            ProductTasks productTasks = new ProductTasks(getActivity(), this);
            productTasks.getProducts();
        }

        return rootView;
    }

    @Override
    public void getProductsFinished(List<Product> products) {
        ProductAdapter productAdapter = new ProductAdapter(
                getActivity(), products);
        listViewProducts.setAdapter(productAdapter);

        if (getContext() != null)
            Toast.makeText(getActivity(),
                    "Produtos carregados com sucesso!",
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getProductsFailed(WebServiceResponse webServiceResponse) {
        if (getContext() != null)
            Toast.makeText(getActivity(), "Falha na consulta da lista de produtos" +
                    webServiceResponse.getResultMessage() + " - Código do erro: " +
                    webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getProductByCodeFinished(Product product) {

    }

    @Override
    public void getProductByCodeFailed(WebServiceResponse webServiceResponse) {

    }

    private void startProductDetail(String code) {
        Class fragmentClass;
        Fragment fragment = null;
        fragmentClass = ProductDetailFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (code != null) {
                Bundle args = new Bundle();
                args.putString("product_code", code);
                fragment.setArguments(args);
            }
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction =
                    fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment,
                    ProductDetailFragment.class.getCanonicalName());
            transaction.addToBackStack(
                    ProductDetailFragment.class.getCanonicalName());
            transaction.commit();
        } catch (Exception e) {
            try {
                Toast.makeText(getActivity(),
                        "Erro ao tentar abrir detalhes do produto",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {
            }
        }
    }
}
