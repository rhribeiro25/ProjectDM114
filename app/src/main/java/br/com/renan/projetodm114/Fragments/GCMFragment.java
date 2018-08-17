package br.com.renan.projetodm114.Fragments;

import android.app.Fragment;

import br.com.renan.projetodm114.gcm.GCMRegisterEvents;

import java.io.IOException;

import br.com.renan.projetodm114.R;
import br.com.renan.projetodm114.gcm.GCMRegister;
import br.com.renan.projetodm114.models.OrderInfo;
import br.com.renan.projetodm114.tasks.UserEvents;
import br.com.renan.projetodm114.tasks.UserTasks;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GCMFragment extends Fragment implements GCMRegisterEvents, UserEvents {
    private String registrationID;
    private GCMRegister gcmRegister;
    private Button btnUnregister;
    private Button btnRegister;
    private Button btnClearMessage;
    private EditText edtSenderID;
    private TextView txtRegistrationID;

    private TextView txtOrderId;
    private TextView txtEmail;
    private TextView txtStatus;
    private TextView txtReason;

    private OrderInfo orderInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gcm, container, false);
        getActivity().setTitle("GCM");
        edtSenderID = rootView.findViewById(R.id.edtSenderID);
        btnRegister = rootView.findViewById(R.id.btnRegister);
        btnUnregister = rootView.findViewById(R.id.btnUnregister);
        btnClearMessage = rootView.findViewById(R.id.btnClearMessage);
        txtRegistrationID = rootView.findViewById(R.id.txtRegistrationID);
        txtOrderId = rootView.findViewById(R.id.textViewOrdertId);
        txtEmail = rootView.findViewById(R.id.textViewOrderEmail);
        txtStatus = rootView.findViewById(R.id.txtOrderStatus);
        txtReason = rootView.findViewById(R.id.txtOrderReason);
        if (gcmRegister == null) gcmRegister = new GCMRegister(getActivity(), this);
        edtSenderID.setText(gcmRegister.getSenderId());
        if (!gcmRegister.isRegistrationExpired()) {
            registrationID = gcmRegister.getCurrentRegistrationId();
            setForRegistered(registrationID);
        } else {
            setForUnregistered();
        }

        Bundle bundle = this.getArguments();
        if ((bundle != null) && (bundle.containsKey("productInfo"))) {
            orderInfo = (OrderInfo) bundle.getSerializable("productInfo");
            txtOrderId.setText(String.valueOf(orderInfo.getId()));
            txtEmail.setText(orderInfo.getEmail());
            txtStatus.setText(orderInfo.getStatus());
            txtReason.setText(orderInfo.getReason());
        }

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtSenderID.getText().toString().isEmpty() || edtSenderID.getText().toString() == null){
                    edtSenderID.setText(getActivity().getApplicationContext().getString(R.string.sender_id));
                }
                registrationGCM(edtSenderID.getText().toString());
            }
        });

        btnUnregister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gcmRegister.unRegister();
            }
        });

        btnClearMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtOrderId.setText("");
                txtEmail.setText("");
                txtReason.setText("");
                txtStatus.setText("");
            }
        });
        return rootView;
    }

    public void registrationGCM(String senderID){
        registrationID = gcmRegister.getRegistrationId(senderID);
        if ((registrationID == null) || (registrationID.length() == 0)) {
            Toast.makeText(getActivity(), "Dispositivo ainda não registrado	na nuvem. " + "Tentando...", Toast.LENGTH_SHORT).show();
            setForUnregistered();
        } else {
            Toast.makeText(getActivity(), "Dispositivo já registrado na	nuvem.", Toast.LENGTH_SHORT).show();
            setForRegistered(registrationID);
        }
    }


    @Override
    public void gcmRegisterFinished(String registrationID) {
        Toast.makeText(getActivity(), "Dispositivo registrado na nuvem com sucesso.", Toast.LENGTH_SHORT).show();
        setForRegistered(registrationID);
        UserTasks usersTask = new UserTasks(getActivity(),this);
        usersTask.putRegistrationId(registrationID);
    }

    @Override
    public void gcmRegisterFailed(IOException ex) {
        Toast.makeText(getActivity(), "Falha ao registrar dispositivo na nuvem.	" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        setForUnregistered();
    }

    @Override

    public void gcmUnregisterFinished() {
        Toast.makeText(getActivity(), "Dispositivo desregistrado da nuvem.", Toast.LENGTH_SHORT).show();
        setForUnregistered();
    }

    @Override
    public void gcmUnregisterFailed(IOException ex) {
        Toast.makeText(getActivity(), "Falha ao desregistrar o dispositivo na nuvem.", Toast.LENGTH_SHORT).show();
    }

    private void setForRegistered(String regID) {
        txtRegistrationID.setText(regID);
        btnUnregister.setEnabled(true);
        btnRegister.setEnabled(false);
    }

    private void setForUnregistered() {
        txtRegistrationID.setText("");
        btnUnregister.setEnabled(false);
        btnRegister.setEnabled(true);
    }

    @Override
    public void putUserRegistrationIdFinished() {
        Toast.makeText(getActivity(), "Registration Id do usuário, trocado com sucesso!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void putUserRegistrationIdFailed(WebServiceResponse webServiceResponse) {
        Toast.makeText(getActivity(), "Falha ao trocar o registration Id do usuário!", Toast.LENGTH_SHORT).show();
    }
}
