package air;

import info.ClientInfo;
import info.Order;
import info.Quotation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AirOrder {
    public static final String PREFIX = "AIR";
    public static final String COMPANY = "Royal Air Force";

    public Order generateOrder(ClientInfo info) {
        return new Order("XOSFG328756");
    }

    private Map<String, Order> orders = new HashMap<>();
    @RequestMapping(value="/orders",method= RequestMethod.POST)
    public ResponseEntity<Order> createOrder(@RequestBody ClientInfo info){
        Order order = generateOrder(info);
        orders.put("yes", order);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/orders/"+order;
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value="/orders/{reference}",method=RequestMethod.GET)
    public Order getResource(@PathVariable("reference") String reference) {
        Order order = orders.get(reference);
        if (order == null) throw new NoSuchQuotationException(); return order;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class NoSuchQuotationException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L; }

}
