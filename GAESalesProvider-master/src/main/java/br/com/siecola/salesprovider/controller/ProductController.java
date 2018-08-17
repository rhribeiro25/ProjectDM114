package br.com.siecola.salesprovider.controller;

import br.com.siecola.salesprovider.exception.ProductAlreadyExistsException;
import br.com.siecola.salesprovider.exception.ProductNotFoundException;
import br.com.siecola.salesprovider.model.Product;
import br.com.siecola.salesprovider.repository.ProductRepository;
import br.com.siecola.salesprovider.util.CheckRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/products")
public class ProductController {

    private static final Logger log = Logger.getLogger("ProductController");

    @Autowired
    private ProductRepository productRepository;


    @PreAuthorize("hasAnyAuthority('" + CheckRole.ROLE_USER + "','" +
            CheckRole.ROLE_ADMIN + "')")
    @GetMapping
    public List<Product> getProducts() {
        return productRepository.getProducts();
    }

    @PreAuthorize("hasAnyAuthority('" + CheckRole.ROLE_USER + "','" +
            CheckRole.ROLE_ADMIN + "')")
    @GetMapping("/{code}")
    public ResponseEntity<Product> getProductByCode(@PathVariable String code) {
        Optional<Product> optProduct = productRepository.getByCode(code);
        return optProduct.map(
                product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('" + CheckRole.ROLE_ADMIN + "')")
    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody Product product) {
        try {
            return new ResponseEntity<>(productRepository.saveProduct(product),
                    HttpStatus.CREATED);
        } catch (ProductAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.PRECONDITION_FAILED);
        }
    }

    @PreAuthorize("hasAuthority('" + CheckRole.ROLE_ADMIN + "')")
    @PutMapping("/{code}")
    public ResponseEntity<?> updateProduct(@RequestBody Product product,
                                           @PathVariable String code) {
        try {
            return new ResponseEntity<>(productRepository
                    .updateProduct(product, code), HttpStatus.OK);
        } catch (ProductNotFoundException | ProductAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.PRECONDITION_FAILED);
        }
    }

    @PreAuthorize("hasAuthority('" + CheckRole.ROLE_ADMIN + "')")
    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteProduct(@PathVariable String code) {
        try {
            return new ResponseEntity<>(productRepository
                    .deleteProduct(code), HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.PRECONDITION_FAILED);
        }
    }
}