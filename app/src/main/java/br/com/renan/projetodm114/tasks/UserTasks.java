package br.com.renan.projetodm114.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.util.WSUtil;
import br.com.renan.projetodm114.webservice.GCMWebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class UserTasks {

    private UserEvents userEvents;
    private Context context;
    private String baseAddress;
    private static final String REGISTER_REGISTRATION_ID = "/api/users/updatereggcm?email=";

    public UserTasks(Context context, UserEvents userEvents) {
        this.context = context;
        this.userEvents = userEvents;
        baseAddress = WSUtil.getHostAddressGCM(context);
    }

    public void putRegistrationId(String registrationId) {
        new AsyncTask<String, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(String... registrationId) {
                    SharedPreferences sharedpreferences = context.getSharedPreferences("credenciais", Context.MODE_PRIVATE);
                    String email = null;
                    if(sharedpreferences.contains(context.getString(R.string.pref_gcm_user_login))){
                         email= sharedpreferences.getString(context.getString(R.string.pref_gcm_user_login), "");
                    }
                return GCMWebServiceClient.put(context, baseAddress + REGISTER_REGISTRATION_ID + email + "&reggcm=" +registrationId[0]);
            }
            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    userEvents.putUserRegistrationIdFinished();
                } else {
                    userEvents.putUserRegistrationIdFailed(webServiceResponse);
                }
            }
        }.execute(registrationId, null, null);
    }
}
