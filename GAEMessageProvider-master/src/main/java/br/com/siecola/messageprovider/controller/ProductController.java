package br.com.siecola.messageprovider.controller;

import br.com.siecola.messageprovider.exception.ProductOfInterestNotFoundException;
import br.com.siecola.messageprovider.model.ProductOfInterest;
import br.com.siecola.messageprovider.model.User;
import br.com.siecola.messageprovider.repository.ProductOfInterestRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/products")
public class ProductController {
    private static final Logger log = Logger.getLogger("ProductController");

    @Value("${server.key.gae}")
    private String serverKey;

    private Gson gson;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductOfInterestRepository productOfInterestRepository;

    @Autowired
    public ProductController() {
        gson = new Gson();
    }

    @PreAuthorize("hasAuthority('" + CheckRole.ROLE_ADMIN + "')")
    @GetMapping
    public List<ProductOfInterest> getAll() {
        return productOfInterestRepository.getAll();
    }

    @PreAuthorize("hasAnyAuthority('" + CheckRole.ROLE_USER + "','" +
            CheckRole.ROLE_ADMIN + "')")
    @PostMapping
    public ResponseEntity<ProductOfInterest> save (Authentication authentication,
            @Valid @RequestBody ProductOfInterest productOfInterest) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (CheckRole.hasRoleAdmin(authentication) ||
                productOfInterest.getEmail().equals(userDetails.getUsername())) {
            return new ResponseEntity<ProductOfInterest>(
                    productOfInterestRepository.save(productOfInterest), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('" + CheckRole.ROLE_USER + "','" +
            CheckRole.ROLE_ADMIN + "')")
    @GetMapping("/byemail")
    public ResponseEntity<List<ProductOfInterest>> getAllByEmail(Authentication authentication,
                                                 @RequestParam String email) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (CheckRole.hasRoleAdmin(authentication) ||
                email.equals(userDetails.getUsername())) {
            return new ResponseEntity<List<ProductOfInterest>>(
                    productOfInterestRepository.findByEmail(email), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('" + CheckRole.ROLE_USER + "','" +
            CheckRole.ROLE_ADMIN + "')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(Authentication authentication,
                                                        @PathVariable long id) {
        Optional<ProductOfInterest> optProduct = productOfInterestRepository.findById(id);
        if (optProduct.isPresent()) {
            ProductOfInterest productOfInterest = optProduct.get();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (CheckRole.hasRoleAdmin(authentication) ||
                    productOfInterest.getEmail().equals(userDetails.getUsername())) {
                try {
                    return new ResponseEntity<>(productOfInterestRepository.deleteById(id),
                            HttpStatus.OK);
                } catch (ProductOfInterestNotFoundException e) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
        return new ResponseEntity<>("Produto de interesse não encontrado",
                HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('" + CheckRole.ROLE_ADMIN + "')")
    @PostMapping("/notify/{code}/{price}")
    public ResponseEntity<String> notifyUsers(@PathVariable String code,
                                                         @PathVariable double price) {
        List<ProductOfInterest> products = productOfInterestRepository
                .findByCodeAndPriceGreaterThanOrEqual(code, price);

        int numberOfMessagesSent = 0;
        for (ProductOfInterest product : products) {
            Optional<User> optUser = userRepository.getByEmail(product.getEmail());
            if (optUser.isPresent()) {
                User user = optUser.get();
                if (user.getGcmRegId() != null) {
                    product.setPrice(price);
                    Sender sender = new Sender(serverKey);
                    Message message = new Message.Builder().addData("productOfInterest",
                            gson.toJson(product)).build();
                    Result result;

                    try {
                        result = sender.send(message, user.getGcmRegId(), 5);
                        if (result.getMessageId() != null) {
                            String canonicalRegId = result.getCanonicalRegistrationId();
                            if (canonicalRegId != null) {
                                log.severe("Usuário [" + user.getEmail() + "] " +
                                        "com mais de um registro");
                            }

                            numberOfMessagesSent++;
                        } else {
                            String error = result.getErrorCodeName();
                            log.severe(error);

                            log.severe("Usuário não registrado no GCM");
                        }
                    } catch (IOException e) {
                        log.severe("Falha ao enviar mensagem");
                    }
                }
            }
        }

        return new ResponseEntity<>("Mensagem enviada para " +
                numberOfMessagesSent + " usuários", HttpStatus.OK);
    }
}
