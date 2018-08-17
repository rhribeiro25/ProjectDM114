package br.com.renan.projetodm114.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.util.WSUtil;
import br.com.renan.projetodm114.webservice.WebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class TokenTasks {

    private TokenEvents tokenEvents;
    private Context context;
    private String baseAddress;
    private String wsUsername;
    private String wsPassword;

    public TokenTasks(Context context, TokenEvents tokenEvents, String wsUsername, String wsPassword) {
        this.context = context;
        this.wsUsername = wsUsername;
        this.wsPassword = wsPassword;
        this.tokenEvents = tokenEvents;
        baseAddress = WSUtil.getHostAddress(context);

    }

    public void getToken() {
        new AsyncTask<Void, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(Void... params) {
                return WebServiceClient.authenticate(context, wsUsername, wsPassword);
            }
            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    tokenEvents.getTokenFinished();

                } else {
                    tokenEvents.getTokenFailed(webServiceResponse);
                }
            }
        }.execute(null, null, null);
    }
}
