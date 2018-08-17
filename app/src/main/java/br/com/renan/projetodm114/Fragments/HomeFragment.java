package br.com.renan.projetodm114.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import br.com.renan.projetodm114.R;

public class HomeFragment extends Fragment {
    private TextView textView1;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,
                container, false);

        textView1 = rootView.findViewById(R.id.textViewWelcome);

        getActivity().setTitle("Home");

        SharedPreferences shared = getActivity().getApplicationContext().getSharedPreferences("credenciais", Context.MODE_PRIVATE);

        String userLogged = shared.getString(this.getContext().getString(R.string.pref_user_login),
                this.getContext().getString(R.string.pref_ws_default_username));
        textView1.setText("Bem-vindo "+userLogged);

        return rootView;

    }
}
