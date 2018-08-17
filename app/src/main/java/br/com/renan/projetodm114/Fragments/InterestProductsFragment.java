package br.com.renan.projetodm114.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
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
import br.com.renan.projetodm114.adapters.InterestProductAdapter;
import br.com.renan.projetodm114.adapters.ProductAdapter;
import br.com.renan.projetodm114.models.InterestProduct;
import br.com.renan.projetodm114.models.Product;
import br.com.renan.projetodm114.tasks.InterestProductEvents;
import br.com.renan.projetodm114.tasks.InterestProductTasks;
import br.com.renan.projetodm114.tasks.ProductEvents;
import br.com.renan.projetodm114.tasks.ProductTasks;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class InterestProductsFragment extends Fragment implements InterestProductEvents {
    protected ListView listViewInterestProducts;
    private ArrayList<InterestProduct> interestProducts;

    public InterestProductsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interest_product_list,
                container, false);
        getActivity().setTitle("Produtos de Interesse");
        setHasOptionsMenu(true);
        listViewInterestProducts = rootView.
                findViewById(R.id.interest_products_list);

        listViewInterestProducts.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        InterestProduct interestProductSelected = (InterestProduct)
                                listViewInterestProducts.getItemAtPosition(position);
                       startInterestDetail(interestProductSelected.getId());
                    }

                });

        if (CheckNetworkConnection.isNetworkConnected(getActivity())) {
            InterestProductTasks interestProduct = new InterestProductTasks(getActivity(), this);
            interestProduct.getInterestProducts();
        }

        return rootView;
    }

    @Override
    public void getInterestProductsFinished(List<InterestProduct> interestProducts) {
        InterestProductAdapter interestProductAdapter = new InterestProductAdapter(
                getActivity(), interestProducts);
        listViewInterestProducts.setAdapter(interestProductAdapter);

        if(getContext() != null)
            Toast.makeText(getActivity(),
                    "Produtos de Interesse carregados com sucesso!",
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getInterestProductsFailed(WebServiceResponse webServiceResponse) {
        if(getContext() != null)
            Toast.makeText(getActivity(), "Falha na consulta da lista de produtos de interesse" +
                webServiceResponse.getResultMessage() + " - Código do erro: " +
                webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteInterestProductsFinished() {
        if(getContext() != null)
            Toast.makeText(getActivity(),
                    "Produtos de Interesse deletado com sucesso!",
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteInterestProductsFailed(WebServiceResponse webServiceResponse) {
        if(getContext() != null)
            Toast.makeText(getActivity(), "Falha ao deletar produto de interesse" +
                    webServiceResponse.getResultMessage() + " - Código do erro: " +
                    webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addInterestProductsFinished() {

    }

    @Override
    public void addInterestProductsFailed(WebServiceResponse webServiceResponse) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.interest_product_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class fragmentClass;
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.novo_produto_interesse:
                fragmentClass = InterestProductAddFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction =
                            fragmentManager.beginTransaction();
                    transaction.replace(R.id.container, fragment,
                            InterestProductAddFragment.class.getCanonicalName());
                    transaction.addToBackStack(
                            InterestProductAddFragment.class.getCanonicalName());
                    transaction.commit();
                } catch (Exception e) {
                    try {
                        if(getContext() != null)
                            Toast.makeText(getActivity(),
                                    "Erro ao tentar ir para tela de cadastro de produto de interesse",
                                    Toast.LENGTH_SHORT).show();
                    } catch (Exception e1) {}
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startInterestDetail (Long orderId) {
        Class fragmentClass;
        Fragment fragment = null;
        fragmentClass = InterestProductsFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (orderId >= 0) {
                Bundle args = new Bundle();
                args.putLong("interest_id", orderId);
                fragment.setArguments(args);
            }
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction =
                    fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment,
                    InterestProductEditFragment.class.getCanonicalName());
            transaction.addToBackStack(
                    InterestProductEditFragment.class.getCanonicalName());
            transaction.commit();
        } catch (Exception e) {
            try {
                if (getContext() != null)
                    Toast.makeText(getActivity(),
                            "Erro ao tentar abrir edição de interesses",
                            Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {
            }
        }
    }
}
