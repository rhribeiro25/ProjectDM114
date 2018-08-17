package br.com.siecola.salesprovider.controller;

import br.com.siecola.salesprovider.exception.OrderNotFoundException;
import br.com.siecola.salesprovider.model.Order;
import br.com.siecola.salesprovider.model.OrderItem;
import br.com.siecola.salesprovider.repository.OrderItemRepository;
import br.com.siecola.salesprovider.repository.OrderRepository;
import br.com.siecola.salesprovider.util.CheckRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/orders")
public class OrderController {

    private static final Logger log = Logger.getLogger("OrderController");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @PreAuthorize("hasAuthority('" + CheckRole.ROLE_ADMIN + "')")
    @GetMapping
    public List<Order> getOrders() {
        List<Order> orders = orderRepository.getOrders();
        for (Order order : orders) {
            order.setOrderItems(orderItemRepository.getOrderItemsByOrderId(order.getId()));
        }
        return orders;
    }

    @PreAuthorize("hasAnyAuthority('" + CheckRole.ROLE_USER + "','" +
            CheckRole.ROLE_ADMIN + "')")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable long orderId,
                                              Authentication authentication) {
        Optional<Order> optOrder = orderRepository.getOrderById(orderId);
        if (optOrder.isPresent()) {
            Order order = optOrder.get();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (CheckRole.hasRoleAdmin(authentication) ||
                    order.getEmail().equals(userDetails.getUsername())) {
                order.setOrderItems(orderItemRepository.getOrderItemsByOrderId(order.getId()));
                return new ResponseEntity<>(order, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('" + CheckRole.ROLE_USER + "','" +
            CheckRole.ROLE_ADMIN + "')")
    @GetMapping(path = "/byemail")
    public ResponseEntity<List<Order>> getOrdersByEmail(@RequestParam("email") String email,
                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (CheckRole.hasRoleAdmin(authentication) ||
                email.equals(userDetails.getUsername())) {
            List<Order> orders = orderRepository.getOrdersByEmail(email);
            for (Order order : orders) {
                order.setOrderItems(orderItemRepository.getOrderItemsByOrderId(order.getId()));
            }
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('" + CheckRole.ROLE_USER + "','" +
            CheckRole.ROLE_ADMIN + "')")
    @PostMapping()
    public ResponseEntity<Order> saveOrder(@RequestBody Order order,
                                           Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (CheckRole.hasRoleAdmin(authentication) ||
                order.getEmail().equals(userDetails.getUsername())) {
            order = orderRepository.saveOrder(order);
            List<OrderItem> orderItems = new ArrayList<OrderItem>();
            for (OrderItem orderItem : order.getOrderItems()) {
                orderItem.setOrderId(order.getId());
                orderItems.add(orderItemRepository.saveOrderItem(orderItem));
            }

            order.setOrderItems(orderItems);
            return new ResponseEntity<Order>(order, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAuthority('" + CheckRole.ROLE_ADMIN + "')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable long orderId) {
        try {
            Order order = orderRepository.deleteOrder(orderId);
            order.setOrderItems(orderItemRepository.deleteOrderItemsByOrderId(orderId));

            return new ResponseEntity<Order>(order, HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
