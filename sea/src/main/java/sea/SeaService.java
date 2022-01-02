package sea;

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

@RestController
public class SeaService {
    public static final String PREFIX = "SEA";
    public static final String COMPANY = "Navy Seals";
    private final ArrayList<TrackingInfo> trackings = new ArrayList<>();


    public Quotation generateQuotation(ClientInfo info) {
        boolean possible = true;
        double price = 5000;
        int urgency_charge = 0;
        if (info.getUrgency().equals("ASAP")){
            urgency_charge = 4500;
        }
        else if (info.getUrgency().equals("SOON")){
            urgency_charge = 2000;
        }
        if (!info.getLocation().equals("SEA")){
            possible = false;
        }
        return new Quotation(COMPANY, generateReference(), (price + urgency_charge), possible);
    }

    int counter1 = 0;
    protected String generateReference() {
        String ref = SeaService.PREFIX;
        int length = 100000;
        while (length > 1000) {
            if (counter1 / length == 0) ref += "0";
            length = length / 10;
        }
        return ref + counter1++;
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
        if (quotation == null) throw new NoSuchQuotationException(); return quotation;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class NoSuchQuotationException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L; }


    protected String generateOrderReference() {
        String ref = SeaService.PREFIX;
        ref += UUID.randomUUID().toString();
        return ref;
    }

    int count2 = 1000;
    protected String generateTrackingNumber() {
        String ref = SeaService.PREFIX;
        ref+="TRACK";
        int length = 11111111;
        while (length > 1000) {
            if (count2 / length == 0) ref += "0";
            length = length / 10;
        }
        return ref + count2++;
    }


    private Map<String, Order> orders = new HashMap<>();
    @RequestMapping(value="/ordering",method= RequestMethod.POST)
    public ResponseEntity<Order> createOrder(@RequestBody Quotation quote, ClientInfo info){
        Order order = generateOrder(info, quote);
        orders.put(order.getReference(), order);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString()+ "/ordering/"+order.getReference(); HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }


    protected Order generateOrder(ClientInfo info, Quotation quote) {
        String trackingNumber = generateTrackingNumber();
        startTracking(trackingNumber);
        return new Order(generateOrderReference(), trackingNumber, quote.getPrice());
    }

    protected void startTracking(String trackingNumber) {
        int time = 100;
        int distance = 200;
        TrackingInfo info = new TrackingInfo(trackingNumber, distance, time);
        trackings.add(info);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                info.setDistance(info.getDistance()-2);
                info.setTimeRemaining(info.getTimeRemaining()-1);
            }
        }, 0, 5000);
    }


    @RequestMapping(value="/tracking",method= RequestMethod.POST)
    public ResponseEntity<TrackingInfo> getTrackingInfo(@RequestBody String trackingNumber){

        TrackingInfo infoToReturn = new TrackingInfo();
        for(TrackingInfo info : trackings) {
            if(info.getTrackingNumber().equals(trackingNumber)) {
                infoToReturn=info;
            }
        }
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString()+ "/tracking/"+infoToReturn.getTrackingNumber();
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setLocation(new URI(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(infoToReturn, headers, HttpStatus.CREATED);
    }


}
