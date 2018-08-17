package br.com.renan.projetodm114.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.models.Product;
import br.com.renan.projetodm114.util.WSUtil;
import br.com.renan.projetodm114.webservice.WebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class ProductTasks {
    private static final String GET_PRODUCTS = "/api/products";
    private static final String GET_PRODUCTS_BY_ID = "/api/products";
    private ProductEvents productEvents;
    private Context context;
    private String baseAddress;

    public ProductTasks(Context context, ProductEvents productEvents) {
        String host;
        int port;
        this.context = context;
        this.productEvents = productEvents;
        baseAddress = WSUtil.getHostAddress(context);
    }

    public void getProducts() {
        new AsyncTask<Void, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(Void... params) {
                return WebServiceClient.get(context,
                        baseAddress + GET_PRODUCTS);
            }

            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    Gson gson = new Gson();
                    try {
                        List<Product> products = gson.fromJson(
                                webServiceResponse.getResultMessage(),
                                new TypeToken<List<Product>>() {
                                }.getType());
                        productEvents.getProductsFinished(products);
                    } catch (Exception e) {
                        productEvents.getProductsFailed(webServiceResponse);
                    }
                } else {
                    productEvents.getProductsFailed(webServiceResponse);
                }
            }
        }.execute(null, null, null);
    }

    @SuppressLint("StaticFieldLeak")
    public void getProductByCode(String code) {
        new AsyncTask<String, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(String... code) {
                return WebServiceClient.get(context,
                        baseAddress + GET_PRODUCTS_BY_ID + "/" + code[0]);
            }

            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    Gson gson = new Gson();
                    try {
                        Product prod = gson.fromJson(
                                webServiceResponse.getResultMessage(),
                                Product.class);
                        productEvents.getProductByCodeFinished(prod);
                    } catch (Exception e) {
                        productEvents.getProductByCodeFailed(webServiceResponse);
                    }
                } else {
                    productEvents.getProductByCodeFailed(webServiceResponse);
                }
            }
        }.execute(code, null, null);
    }
}
