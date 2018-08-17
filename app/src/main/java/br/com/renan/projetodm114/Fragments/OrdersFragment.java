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

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.adapters.OrderAdapter;
import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.tasks.OrderEvents;
import br.com.renan.projetodm114.tasks.OrderTasks;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceResponse;
import java.util.ArrayList;
import java.util.List;
public class OrdersFragment extends Fragment implements OrderEvents {
    private ListView listViewOrders;
    private ArrayList<Order> orders;

    public OrdersFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders_list,
                container, false);
        getActivity().setTitle("Pedidos");
        setHasOptionsMenu(true);
        listViewOrders = rootView.
                findViewById(R.id.orders_list);

        listViewOrders.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        Order orderSelected = (Order)
                                listViewOrders.getItemAtPosition(position);
                        startOrderDetail(orderSelected.getId());
                    }
                });

        if (CheckNetworkConnection.isNetworkConnected(getActivity())) {
            OrderTasks orderTasks = new OrderTasks(getActivity(), this);
            orderTasks.getOrders();
        }

        return rootView;
    }

    @Override
    public void getOrdersFinished(List<Order> orders) {
        OrderAdapter orderAdapter = new OrderAdapter(
                getActivity(), orders);
        listViewOrders.setAdapter(orderAdapter);

        if(getContext() != null)
            Toast.makeText(getActivity(),
                    "Pedidos carregados com sucesso!",
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getOrdersFailed(WebServiceResponse webServiceResponse) {
        if(getContext() != null)
            Toast.makeText(getActivity(), "Falha na consulta da lista de pedidos" +
                    webServiceResponse.getResultMessage() + " - Código do erro: " +
                    webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getOrderByIdFinished(Order order) {
    }

    @Override
    public void getOrderByIdFailed(WebServiceResponse webServiceResponse) {
    }

    private void startOrderDetail (Long orderId) {
        Class fragmentClass;
        Fragment fragment = null;
        fragmentClass = OrderDetailFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (orderId >= 0) {
                Bundle args = new Bundle();
                args.putLong("order_id", orderId);
                fragment.setArguments(args);
            }
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction =
                    fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment,
                    OrderDetailFragment.class.getCanonicalName());
            transaction.addToBackStack(
                    OrderDetailFragment.class.getCanonicalName());
            transaction.commit();
        } catch (Exception e) {
            try {
                if(getContext() != null)
                    Toast.makeText(getActivity(),
                            "Erro ao tentar abrir detalhes do pedido",
                            Toast.LENGTH_SHORT).show();
            } catch (Exception e1) {}
        }
    }
}
