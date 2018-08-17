package br.com.renan.projetodm114.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import br.com.renan.projetodm114.R;

public class SettingsFragment extends PreferenceFragment {
    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preferences);
        getActivity().setTitle("Configurações");
    }
}
