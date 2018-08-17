package br.com.renan.projetodm114.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import br.com.renan.projetodm114.util.WSUtil;
import br.com.renan.projetodm114.webservice.GCMWebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class TokenGCMTask {

    private TokenGCMEvents tokenGcmEvents;
    private Context context;
    private String baseAddress;
    private String gcmUsername;
    private String gcmPassword;

    public TokenGCMTask(Context context, TokenGCMEvents tokenGcmEvents, String gcmUsername, String gcmPassword) {
        this.context = context;
        this.tokenGcmEvents = tokenGcmEvents;
        this.gcmUsername = gcmUsername;
        this.gcmPassword = gcmPassword;
        baseAddress = WSUtil.getHostAddressGCM(context);
    }

    public void getTokenGCM() {
        new AsyncTask<Void, Void, WebServiceResponse>() {
            @Override
            protected WebServiceResponse doInBackground(Void... params) {
                return GCMWebServiceClient.authenticate(context, gcmUsername, gcmPassword);
            }
            @Override
            protected void onPostExecute(
                    WebServiceResponse webServiceResponse) {
                if (webServiceResponse.getResponseCode() == 200) {
                    tokenGcmEvents.getTokenGCMFinished();
                } else {
                    tokenGcmEvents.getTokenGCMFailed(webServiceResponse);
                }
            }
        }.execute(null, null, null);
    }
}

