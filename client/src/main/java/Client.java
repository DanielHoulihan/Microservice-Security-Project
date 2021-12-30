import info.*;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.Scanner;

public class Client {

//    public static final ClientInfo[] clients = {
//            new ClientInfo("Daniel", "ASAP", "AIR"),
//            new ClientInfo("Shane", "SOON", "SEA"),
//            new ClientInfo("Daniel", "WHENEVER", "AIR"),
//    };

    public static void main(String[] args) {

        String output = "";
        String name = "";
        String urgency = "";
        String location = "";
        do {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter your name: ");
            name = sc.nextLine();
            System.out.println("Enter the urgency which the job requires (ASAP/SOON): ");
            urgency = sc.nextLine();
            System.out.println("Enter the location which the job requires (AIR/SEA/LAND): ");
            location = sc.nextLine();

            System.out.println("\nThe details you entered are:\n");
            System.out.println("Name: " + name);
            System.out.println("Urgency: " + urgency);
            System.out.println("Location: " + location);
            if((urgency.equals("ASAP") || urgency.equals("SOON")) && (location.equals("AIR") || location.equals("SEA") || location.equals("LAND"))) {
                System.out.println("\nAre you satisfied with the details entered? (Y) or (N).");
                output = sc.nextLine();
            } else {
                output = "N";
                System.out.println("\n*** One or more of the inputs given where wrong, try again please. ***\n");
            }
        } while(output.equalsIgnoreCase("N"));

        ClientInfo[] clients = {new ClientInfo(name, urgency, location)};

        displayLogo();

        // getting a quote
        RestTemplate restTemplate = new RestTemplate();
        for (ClientInfo clientInfo : clients){
            HttpEntity<ClientInfo> request = new HttpEntity<>(clientInfo);
            ClientApplication clientApplication = restTemplate.postForObject("http://localhost:8083/applications", request, ClientApplication.class);
            displayProfile(clientInfo);

            for (Quotation quotation : clientApplication.getQuotations()){
                if (!quotation.getPossible()) {
                    displayNoQuotation(quotation);
                }
                else {
                    displayQuotation(quotation);
                }
            }
        }

        //ordering
        Quotation quote1 = new Quotation("Army", "test1", 1000, true);
        HttpEntity<Quotation> request2 = new HttpEntity<>(quote1);
        OrderApplication orderApplication = restTemplate.postForObject("http://localhost:8084/applications", request2, OrderApplication.class);

        for (Order order : orderApplication.getOrders()) {
            System.out.println("price - > " + order.getPrice());
            System.out.println("reference -> " + order.getReference());
            System.out.println("tracking number -> " + order.getTrackingNumber());
        }
        // tracking
    }

    public static void displayProfile(ClientInfo info) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                  Distributed Security Management Client Profile                                 |");
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.getName()) +
                        " | Urgency: " + String.format("%1$-26s", (info.getUrgency())) +
                        " | Location: " + String.format("%1$-25s", info.getLocation())+" |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayQuotation(Quotation quotation) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                    Distributed Security Management Quotation                                    |");
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.getCompany()) +
                        " | Reference: " + String.format("%1$-24s", quotation.getReference()) +
                        " | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.getPrice()))+" |");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayNoQuotation(Quotation quotation) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                    No Quotation Available from: " + String.format("%1$-48s", quotation.getCompany()) + "|");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayLogo() {
        System.out.println("\n\n ______      ______    ____    ____  ");
        System.out.println("|_   _ `.  .' ____ \\  |_   \\  /   _| ");
        System.out.println("  | | `. \\ | (___ \\_|   |   \\/   |   ");
        System.out.println("  | |  | |  _.____`.    | |\\  /| |   ");
        System.out.println(" _| |_.' / | \\____) |  _| |_\\/_| |_  ");
        System.out.println("|______.'   \\______.' |_____||_____| \n\n");
    }
}

