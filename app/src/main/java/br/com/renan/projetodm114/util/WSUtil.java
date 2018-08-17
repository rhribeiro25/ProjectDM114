package br.com.renan.projetodm114.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.com.renan.projetodm114.R;

public class WSUtil {
    private WSUtil(){}
    public static String getHostAddress (Context context) {
        String host;
        String baseAddress;
        int port;
        SharedPreferences	sharedSettings	= context.getSharedPreferences("wshost", Context.MODE_PRIVATE);
        host	=	sharedSettings.getString(
                context.getString(R.string.pref_ws_host),
                context.getString(R.string.pref_ws_default_host));
        port	=	sharedSettings.getInt(
                context.getString(R.string.pref_host_port),
                Integer.parseInt(context.getString(R.string.pref_ws_default_port)));
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        if (!host.startsWith("http://")) {
            host = "http://" + host;
        }
        baseAddress = host + ":" + port;
        return baseAddress;
    }

    public static String getHostAddressGCM (Context context) {
        String host;
        String baseAddress;
        int port;
        SharedPreferences	sharedSettings	= context.getSharedPreferences("gcmhost", Context.MODE_PRIVATE);
        host	=	sharedSettings.getString(
                context.getString(R.string.pref_gcm_host),
                context.getString(R.string.pref_gcm_default_host));
        port	=	sharedSettings.getInt(
                context.getString(R.string.pref_host_port),
                Integer.parseInt(context.getString(R.string.pref_gcm_default_port)));
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        if (!host.startsWith("http://")) {
            host = "http://" + host;
        }
        baseAddress = host + ":" + port;
        return baseAddress;
    }
}
