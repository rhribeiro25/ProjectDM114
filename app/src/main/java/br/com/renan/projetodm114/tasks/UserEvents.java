package br.com.renan.projetodm114.tasks;

import br.com.renan.projetodm114.webservice.WebServiceResponse;

public interface UserEvents {
    void putUserRegistrationIdFinished();
    void putUserRegistrationIdFailed(WebServiceResponse webServiceResponse);
}
