package br.com.renan.projetodm114.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.models.InterestProduct;
import br.com.renan.projetodm114.util.WSUtil;
import br.com.renan.projetodm114.webservice.GCMWebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class InterestProductTasks {

    private static final String INTEREST_PRODUCTS = "/api/products";
    private static final String GET_INTEREST_PRODUCTS = "/api/products/byemail?email=";
    private InterestProductEvents interestProductEvents;
    private Context context;
    private String baseAddress;

    public InterestProductTasks(Context context, InterestProductEvents interestProductEvents) {
        String host;
        int port;
        this.context = context;
        this.interestProductEvents = interestProductEvents;
        baseAddress = WSUtil.getHostAddressGCM(context);
    }

    public void getInterestProducts() {
        new AsyncTask<Void, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(Void... params) {
                SharedPreferences sharedpreferences = context.getSharedPreferences("credenciais", Context.MODE_PRIVATE);
                String email = null;
                if(sharedpreferences.contains(context.getString(R.string.pref_user_login))){
                    email = sharedpreferences.getString(context.getString(R.string.pref_user_login), "");
                }
                return GCMWebServiceClient.get(context,baseAddress + GET_INTEREST_PRODUCTS + email);
            }
            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    Gson gson = new Gson();
                    try {
                        List<InterestProduct> interestProducts = gson.fromJson(
                                webServiceResponse.getResultMessage(),
                                new TypeToken<List<InterestProduct>>() {
                                }.getType());
                        interestProductEvents.getInterestProductsFinished(interestProducts);
                    } catch (Exception e) {
                        interestProductEvents.getInterestProductsFailed(webServiceResponse);
                    }
                } else {
                    interestProductEvents.getInterestProductsFailed(webServiceResponse);
                }
            }
        }.execute(null, null, null);
    }

    public void deleteInterestProduct(long id) {
        new AsyncTask<Long, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(Long... params) {
                return GCMWebServiceClient.delete(context,
                        baseAddress + GET_INTEREST_PRODUCTS+ "/"+ params[0]);
            }
            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    try {
                        interestProductEvents.deleteInterestProductsFinished();
                    } catch (Exception e) {
                        interestProductEvents.deleteInterestProductsFailed(webServiceResponse);
                    }
                } else {
                    interestProductEvents.deleteInterestProductsFailed(webServiceResponse);
                }
            }
        }.execute(id, null, null);
    }

    public void addInterestProduct(String json) {
        new AsyncTask<String, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(String... params) {
                return GCMWebServiceClient.post(context,
                        baseAddress + INTEREST_PRODUCTS, params[0]);
            }
            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    try {
                        interestProductEvents.addInterestProductsFinished();
                    } catch (Exception e) {
                        interestProductEvents.addInterestProductsFailed(webServiceResponse);
                    }
                } else {
                    interestProductEvents.addInterestProductsFailed(webServiceResponse);
                }
            }
        }.execute(json, null, null);
    }


}
