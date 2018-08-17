package br.com.renan.projetodm114.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.models.OrderItem;
import br.com.renan.projetodm114.tasks.OrderEvents;
import br.com.renan.projetodm114.tasks.OrderTasks;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class OrderDetailFragment extends Fragment implements OrderEvents {
    private TextView textViewNumeroPedido;
    private TextView textViewEmail;
    private TextView textViewPrecoFrete;
    private Button buttonBack;

    public OrderDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_detail,
                container, false);
        getActivity().setTitle("Detalhe do pedido");

        textViewNumeroPedido = rootView.findViewById(R.id.textViewOrdertId);
        textViewEmail = rootView.findViewById(R.id.textViewOrderEmail);
        textViewPrecoFrete = rootView.findViewById(R.id.textViewOrderPreco);
        buttonBack = rootView.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        Long orderId;
        Bundle bundle = this.getArguments();
        if ((bundle != null) && (bundle.containsKey("order_id"))) {
            orderId = bundle.getLong("order_id");
            if (CheckNetworkConnection.isNetworkConnected(getActivity())) {
                OrderTasks orderTasks = new OrderTasks(getActivity(), this);
                orderTasks.getOrderById(orderId);
            }
        }


        return rootView;
    }

    @Override
    public void getOrdersFinished(List<Order> orders) {

    }

    @Override
    public void getOrdersFailed(WebServiceResponse webServiceResponse) {

    }

    @Override
    public void getOrderByIdFinished(Order order) {
        textViewNumeroPedido.setText(String.valueOf(order.getId()));
        textViewPrecoFrete.setText(String.valueOf(order.getFreightPrice()));
        textViewEmail.setText(String.valueOf(order.getEmail()));
    }

    @Override
    public void getOrderByIdFailed(WebServiceResponse webServiceResponse) {
        Toast.makeText(getActivity(), "Falha na consulta dos detalhes do pedido" +
                webServiceResponse.getResultMessage() + " - Código do erro: " +
                webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    public void closeFragment() {
        int backStackEntryCount;
        backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        getFragmentManager().popBackStack();
    }
}
