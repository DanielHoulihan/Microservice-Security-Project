import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import info.ClientApplication;
import info.ClientInfo;
import info.Quotation;

import java.util.Scanner;

public class Client {

    public static final ClientInfo[] clients = {
            new ClientInfo("Daniel", "ASAP", "AIR"),
            new ClientInfo("Shane", "SOON", "SEA"),
            new ClientInfo("Daniel", "WHENEVER", "AIR"),
    };

    public static void main(String[] args) {

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

        System.out.println("Which would you like to order? Enter the quotation reference");

        Scanner sc = new Scanner(System.in);
        String i = sc.nextLine();
        System.out.println("input-> " + i); // will print the variable
    }
}

