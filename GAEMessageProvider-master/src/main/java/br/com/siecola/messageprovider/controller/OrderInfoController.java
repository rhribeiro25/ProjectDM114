package br.com.siecola.messageprovider.controller;

import br.com.siecola.messageprovider.model.OrderInfo;
import br.com.siecola.messageprovider.model.User;
import br.com.siecola.messageprovider.repository.UserRepository;
import br.com.siecola.messageprovider.util.CheckRole;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/orderinfo")
public class OrderInfoController {
    private static final Logger log = Logger.getLogger("OrderInfoController");

    @Value("${server.key.gae}")
    private String serverKey;

    private Gson gson;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public OrderInfoController() {
        gson = new Gson();
    }

    @PreAuthorize("hasAuthority('" + CheckRole.ROLE_ADMIN + "')")
    @PostMapping
    public ResponseEntity<String> sendOrderInfo(@Valid @RequestBody OrderInfo orderInfo) {

        String orderInfoMessage = gson.toJson(orderInfo);
        log.severe("OrderInfo: " + orderInfoMessage);

        Optional<User> optUser = userRepository.getByEmail(orderInfo.getEmail());
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.getGcmRegId() != null) {

                Sender sender = new Sender(serverKey);
                Message message = new Message.Builder().addData("orderInfo", orderInfoMessage).build();
                Result result;

                try {
                    result = sender.send(message, user.getGcmRegId(), 5);
                    if (result.getMessageId() != null) {
                        String canonicalRegId = result.getCanonicalRegistrationId();
                        if (canonicalRegId != null) {
                            log.severe("Usuário [" + user.getEmail() + "] com mais de um registro");
                        }
                    } else {
                        String error = result.getErrorCodeName();
                        log.severe(error);

                        log.severe("Usuário não registrado no GCM");
                        return new ResponseEntity<String>("Usuário não registrado no GCM",
                                HttpStatus.PRECONDITION_FAILED);
                    }
                } catch (IOException e) {
                    log.severe("Falha ao enviar mensagem");
                    return new ResponseEntity<String>("Falha ao enviar a mensagem",
                            HttpStatus.SERVICE_UNAVAILABLE);
                }

                log.severe("A mensagem foi enviada");
                return new ResponseEntity<>("A mensagem foi enviada!", HttpStatus.OK);
            } else {
                log.severe("Usuário sem o GCM Registration Id");
                return new ResponseEntity<>("Usuário sem o GCM Registration Id",
                        HttpStatus.PRECONDITION_REQUIRED);
            }
        } else {
            log.severe("Usuário não encontrado");
            return new ResponseEntity<>("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
    }
}
