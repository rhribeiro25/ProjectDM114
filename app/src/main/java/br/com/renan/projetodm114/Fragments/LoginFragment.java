package br.com.renan.projetodm114.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.tasks.TokenEvents;
import br.com.renan.projetodm114.tasks.TokenGCMEvents;
import br.com.renan.projetodm114.tasks.TokenGCMTask;
import br.com.renan.projetodm114.tasks.TokenTasks;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceClient;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class LoginFragment extends Fragment implements TokenEvents, TokenGCMEvents {

    View rootView;
    String user;
    String password;
    Context context;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        getActivity().setTitle("Login");

        context = getActivity().getApplicationContext();

        final EditText txtUser = rootView.findViewById(R.id.pref_user_login);
        final EditText txtPassword = rootView.findViewById(R.id.pref_user_password);
        Button btnLogin = rootView.findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = txtUser.getText().toString();
                password = txtPassword.getText().toString();
                login(user, password);
            }
        });

        return rootView;
    }

    public void fragmentTransaction(Class fragmentClass){

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
        }
    }

    private void login(String user, String password){
        if (CheckNetworkConnection.isNetworkConnected(context)) {
            TokenTasks tokenTask = new TokenTasks(context, this, user, password);
            tokenTask.getToken();
            TokenGCMTask tokenGcmTask = new TokenGCMTask(context, this, user, password);
            tokenGcmTask.getTokenGCM();
        }
    }

    @Override
    public void getTokenFinished() {
        Toast.makeText(context,
                "Login efetuado com sucesso no provedor de vendas!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getTokenFailed(WebServiceResponse webServiceResponse) {
        Toast.makeText(context, "Erro ao efetuar o login no provedor de vendas " +
                webServiceResponse.getResultMessage() + " - Código do erro: " +
                webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getTokenGCMFinished() {
        Toast.makeText(context,
                "Login efetuado com sucesso no provedor de mensagens!",
                Toast.LENGTH_SHORT).show();
        fragmentTransaction(HomeFragment.class);
    }

    @Override
    public void getTokenGCMFailed(WebServiceResponse webServiceResponse) {
        Toast.makeText(context, "Erro ao efetuar o login no provedor de mensagens" +
                webServiceResponse.getResultMessage() + " - Código do erro: " +
                webServiceResponse.getResponseCode(), Toast.LENGTH_SHORT).show();
    }

}
