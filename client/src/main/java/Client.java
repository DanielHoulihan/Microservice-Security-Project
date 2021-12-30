import info.*;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Scanner;

public class Client {

    public static final ClientInfo[] clients = {
            new ClientInfo("Daniel", "ASAP", "AIR"),
            new ClientInfo("Shane", "SOON", "SEA"),
            new ClientInfo("Daniel", "WHENEVER", "AIR"),
    };

    public static void main(String[] args) {


        // getting a quote
        RestTemplate restTemplate = new RestTemplate();
        for (ClientInfo clientInfo : clients){
            HttpEntity<ClientInfo> request = new HttpEntity<>(clientInfo);
            ClientApplication clientApplication = restTemplate.postForObject("http://localhost:8083/applications", request, ClientApplication.class);
            System.out.println("Name -> " + clientInfo.getName());
            System.out.println("Urgency -> " + clientInfo.getUrgency());
            System.out.println("Location -> " + clientInfo.getLocation());

            for (Quotation quotation : clientApplication.getQuotations()){
                System.out.println("***Quotation***");
                if (!quotation.getPossible()) {
                    System.out.println("No quotation possible from "+ quotation.getCompany());
                }
                else {

                    System.out.println("Company -> " + quotation.getCompany());
                    System.out.println("Reference -> " + quotation.getReference());
                    System.out.println("Price -> " + quotation.getPrice());
                }
            }
        }


        //ordering
        Quotation quote1 = new Quotation("Navy Seals", "test1", 1000, true);
        HttpEntity<Quotation> request2 = new HttpEntity<>(quote1);
        OrderApplication orderApplication = restTemplate.postForObject("http://localhost:8084/applications", request2, OrderApplication.class);

        for (Order order : orderApplication.getOrders()) {
            System.out.println("price - > " + order.getPrice());
            System.out.println("reference -> " + order.getReference());
            System.out.println("tracking number -> " + order.getTrackingNumber());
        }
        // tracking

    }
}

