package air;

import info.ClientInfo;
import info.Order;
import info.Quotation;
import info.TrackingInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class AirService {
    public static final String PREFIX = "AIR";
    public static final String COMPANY = "Royal Air Force";
    private final ArrayList<TrackingInfo> trackings = new ArrayList<>();


    public Quotation generateQuotation(ClientInfo info) {
        boolean possible = true;
        double price = 10000;
        int urgency_charge = 0;
        if (info.getUrgency().equals("ASAP")){
            urgency_charge = 10000;
        }
        else if (info.getUrgency().equals("SOON")){
            urgency_charge = 5000;
        }
        return new Quotation(COMPANY, generateReference(), (price + urgency_charge), possible);
    }

    int counter = 0;
    protected String generateReference() {
        String ref = AirService.PREFIX;
        int length = 100000;
        while (length > 1000) {
            if (counter / length == 0) ref += "0";
            length = length / 10;
        }
        return ref + counter++;
    }


    private Map<String, Quotation> quotations = new HashMap<>();
    @RequestMapping(value="/quotations",method= RequestMethod.POST)
    public ResponseEntity<Quotation> createQuotation(@RequestBody ClientInfo info){
        Quotation quotation = generateQuotation(info);
        quotations.put(quotation.getReference(), quotation);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString()+ "/quotations/"+quotation.getReference(); HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(quotation, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value="/quotations/{reference}",method=RequestMethod.GET)
    public Quotation getResource(@PathVariable("reference") String reference) {
        Quotation quotation = quotations.get(reference);
        if (quotation == null) throw new NoSuchQuotationException();
        return quotation;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NoSuchQuotationException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L;
    }



    protected String generateOrderReference() {
        String ref = AirService.PREFIX;
        ref += UUID.randomUUID().toString();
        return ref;
    }

    int count2 = 0;
    protected String generateTrackingNumber() {
        String ref = AirService.PREFIX;
        ref+="TRACK";
        int length = 777777777;
        while (length > 1000) {
            if (count2 / length == 0) ref += "0";
            length = length / 10;
        }
        return ref + count2++;
    }

    protected Order generateOrder(ClientInfo info, Quotation quote) throws InterruptedException {
        String trackingNumber = generateTrackingNumber();
        startTracking(trackingNumber);
        return new Order(generateOrderReference(), trackingNumber, quote.getPrice());
    }

    int time = 100;
    int distance = 200;
    protected void startTracking(String trackingNumber) {
        trackings.add(new TrackingInfo(trackingNumber, distance, time));
    }


    private Map<String, Order> orders = new HashMap<>();
    @RequestMapping(value="/ordering",method= RequestMethod.POST)
    public ResponseEntity<Order> createOrder(@RequestBody Quotation quote, ClientInfo info) throws InterruptedException {
        Order order = generateOrder(info, quote);
        orders.put(order.getReference(), order);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString()+ "/ordering/"+order.getReference();
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value="/tracking",method= RequestMethod.POST)
    public ResponseEntity<TrackingInfo> getTrackingInfo(@RequestBody String trackingNumber){

        TrackingInfo infoToReturn = new TrackingInfo();
        for(TrackingInfo info : trackings) {
            if(info.getTrackingNumber().equals(trackingNumber)) {
                infoToReturn=info;
            }
        }

        TrackingInfo info = new TrackingInfo(trackingNumber, distance, time);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString()+ "/tracking/"+info.getTrackingNumber();
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(infoToReturn, headers, HttpStatus.CREATED);
    }


}
