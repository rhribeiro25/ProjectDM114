package br.com.renan.projetodm114.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.util.WSUtil;
import br.com.renan.projetodm114.webservice.WebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

import java.util.List;

public class OrderTasks {
    private static final String GET_ORDERS = "/api/orders/byemail?email=";
    private static final String GET_ORDERS_BY_ID = "/api/orders";
    private OrderEvents orderEvents;
    private Context context;
    private String baseAddress;


    public OrderTasks(Context context, OrderEvents orderEvents) {
        String host;
        int port;
        this.context = context;
        this.orderEvents = orderEvents;
        baseAddress = WSUtil.getHostAddress(context);
    }

    @SuppressLint("StaticFieldLeak")
    public void getOrders() {
        new AsyncTask<Void, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(Void... params) {
                    SharedPreferences sharedpreferences = context.getSharedPreferences("credenciais", Context.MODE_PRIVATE);
                    String email = null;
                    if(sharedpreferences.contains(context.getString(R.string.pref_user_login))){
                        email = sharedpreferences.getString(context.getString(R.string.pref_user_login), "");
                    }
                return WebServiceClient.get(context, baseAddress + GET_ORDERS + email );
            }

            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    Gson gson = new Gson();
                    try {
                        List<Order> orders = gson.fromJson(
                                webServiceResponse.getResultMessage(),
                                new TypeToken<List<Order>>() {
                                }.getType());
                        orderEvents.getOrdersFinished(orders);
                    } catch (Exception e) {
                        orderEvents.getOrdersFailed(webServiceResponse);
                    }
                } else {
                    orderEvents.getOrdersFailed(webServiceResponse);
                }
            }
        }.execute(null, null, null);
    }

    @SuppressLint("StaticFieldLeak")
    public void getOrderById(long id) {
        new AsyncTask<Long, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(Long... id) {
                return WebServiceClient.get(context,
                        baseAddress + GET_ORDERS_BY_ID + "/" +
                                Long.toString(id[0]));
            }

            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    Gson gson = new Gson();
                    try {
                        Order order = gson.fromJson(
                                webServiceResponse.getResultMessage(),
                                Order.class);
                        orderEvents.getOrderByIdFinished(order);
                    } catch (Exception e) {
                        orderEvents.getOrderByIdFailed(webServiceResponse);
                    }
                } else {
                    orderEvents.getOrderByIdFailed(webServiceResponse);
                }
            }
        }.execute(id, null, null);
    }
}
