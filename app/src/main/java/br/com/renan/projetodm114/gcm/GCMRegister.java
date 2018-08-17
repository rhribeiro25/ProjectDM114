package br.com.renan.projetodm114.gcm;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.util.WSUtil;
import br.com.renan.projetodm114.webservice.GCMWebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class GCMRegister {
    private static final String TAG = "GCMRegister";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
    private static final String PROPERTY_REG_ID = "registration_id";
    static final String PROPERTY_SENDER_ID = "senderID";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private IOException ioException;
    private static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
    private GoogleCloudMessaging gcm;
    private String regid;
    private String senderID;
    private GCMRegisterEvents gcmRegisterEvents;
    private Context context;

    public GCMRegister(Context context, GCMRegisterEvents gcmRegisterEvents) {
        this.context = context;
        this.gcmRegisterEvents = gcmRegisterEvents;
    }

    private void setRegistrationId(String regId) {
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion(context);
        Log.v(TAG, "Saving regId on app	version	" + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;
        Log.v(TAG, "Setting	registration expiry	time	to	" + new Timestamp(expirationTime));
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }

    private void setSenderId(String senderId) {
        final SharedPreferences prefs = getGCMPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_SENDER_ID, senderId);
        editor.commit();
    }

    private void clearRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.remove(PROPERTY_APP_VERSION);
        editor.remove(PROPERTY_ON_SERVER_EXPIRATION_TIME);
        editor.commit();
    }

    private SharedPreferences getGCMPreferences() {
        return context.getSharedPreferences(context.getClass().getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name:	" + e);
        }
    }

    public String getRegistrationId(String senderID) {
        this.senderID = senderID;
        setSenderId(senderID);
        regid = getCurrentRegistrationId();
        if (regid.length() == 0) {
            registerBackground();
        }
        return regid;
    }

    public String getCurrentRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            Log.v(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion || isRegistrationExpired()) {
            Log.v(TAG, "App	version	changed	or registration expired.");
            return "";
        }
        return registrationId;
    }

    public String getSenderId() {
        final SharedPreferences prefs = getGCMPreferences();
        String senderId = prefs.getString(PROPERTY_SENDER_ID, "");
        if (senderId.length() == 0) {
            return "";
        }
        return senderId;
    }

    public boolean isRegistrationExpired() {
        final SharedPreferences prefs = getGCMPreferences();
        long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
        return System.currentTimeMillis() > expirationTime;
    }

    private void registerBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(senderID);
                    setRegistrationId(regid);
                    msg = regid;
                } catch (IOException ex) {
                    msg = null;
                    ioException = ex;
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String registrationID) {
                if (registrationID != null) {
                    Log.i(TAG, "Device	registered,	registration	id=" + registrationID);
                    gcmRegisterEvents.gcmRegisterFinished(registrationID);
                } else {
                    gcmRegisterEvents.gcmRegisterFailed(ioException);
                }
            }
        }.execute(null, null, null);
    }

        public void unRegister(){
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(context);
                        }
                        gcm.unregister();
                        clearRegistrationId();
                        return true;
                    } catch (IOException ex) {
                        ioException = ex;
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean unregistered) {
                    if (unregistered == true) {
                        Log.i(TAG, "Device	unregistered");
                        gcmRegisterEvents.gcmUnregisterFinished();
                    } else {
                        gcmRegisterEvents.gcmUnregisterFailed(ioException);
                    }
                }
            }.execute(null, null, null);
        }
}

