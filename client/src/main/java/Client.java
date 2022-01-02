import info.*;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class Client {

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    private static final ArrayList<ClientInfo> clients = new ArrayList<>();

    private static final TreeMap<Integer, Quotation> cache = new TreeMap<Integer, Quotation>();
    private static final TreeMap<Integer, Order> orderCache = new TreeMap<Integer, Order>();

//    private static int index = 1;

    public static void main(String[] args) {

        displayLogo();



        while (true) {

            System.out.println("Would you like to create a new user (CREATE), track an order (TRACK), or order with an existing user (ORDER)?");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine().toUpperCase();

            if(input.equals("CREATE")){
                createUser();
            }

            if(clients.isEmpty()) {
                System.out.println("No users exist, you must create one");
                createUser();
            }

            if(input.equals("TRACK")){
                getTracking();
            }

            if(input.equals("ORDER") && !clients.isEmpty()){
                System.out.println("which user are you? Please give the ID");
                int user = sc.nextInt();
                getQuotes(clients.get(user));
                order(clients.get(user));
            }

            for (ClientInfo clientInfo : clients) {
                displayProfile(clientInfo);
            }

        }

    }


    public static void createUser() {
        String output = "";
        String name = "";
        String urgency = "";
        String location = "";
        do {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter your name: ");
            name = sc.nextLine().toUpperCase();
            System.out.println("Enter the urgency which the job requires (ASAP/SOON/WHENEVER): ");
            urgency = sc.nextLine().toUpperCase();
            System.out.println("Enter the location which the job requires (AIR/SEA/LAND): ");
            location = sc.nextLine().toUpperCase();

            System.out.println("\nThe details you entered are:\n");
            System.out.println("Name: " + name);
            System.out.println("Urgency: " + urgency);
            System.out.println("Location: " + location);
            if ((urgency.equals("ASAP") || urgency.equals("SOON")) && (location.equals("AIR") || location.equals("SEA") || location.equals("LAND"))) {
                System.out.println("\nAre you satisfied with the details entered? (Y) or (N).");
                output = sc.nextLine().toUpperCase();
            } else {
                output = "N";
                System.out.println("\n*** One or more of the inputs given were wrong, try again please. ***\n");
            }
        } while (output.equalsIgnoreCase("N"));
        clients.add(new ClientInfo(name, urgency, location));

    }


    public static void getQuotes(ClientInfo client) {
        int index = 1;
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ClientInfo> request = new HttpEntity<>(client);
        ClientApplication clientApplication = restTemplate.postForObject("http://localhost:8083/applications", request, ClientApplication.class);
        displayProfile(client);

        for (Quotation quotation : clientApplication.getQuotations()) {
            if (!quotation.getPossible()) {
                displayNoQuotation(quotation);
            } else {
                System.out.println("\n\n                                                        |===|");
                System.out.println("                                                        | " + index + " |");
                System.out.println("                                                        |===|");
                displayQuotation(quotation);
                cache.put(index, quotation);
                index++;
            }
        }
    }


    public static void getTracking() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter the tracking number ");
        String trackingNumber = sc.nextLine();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(trackingNumber);
        TrackingInfo trackingInfo = restTemplate.postForObject("http://localhost:8085/applications", request, TrackingInfo.class);
        displayTracking(trackingInfo);

        System.out.println(trackingInfo.getTrackingNumber());
        System.out.println(trackingInfo.getDistance());
        System.out.println(trackingInfo.getTimeRemaining());
        System.out.println(trackingInfo);
    }


    public static void order(ClientInfo info) {

        RestTemplate restTemplate = new RestTemplate();
        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter the quotation which you would like to order (E.g 1/2/3): ");
        int chosenOrder = sc.nextInt();

        Quotation quote1 = cache.get(chosenOrder);
        HttpEntity<Quotation> request2 = new HttpEntity<>(quote1);
        OrderApplication orderApplication = restTemplate.postForObject("http://localhost:8084/applications", request2, OrderApplication.class);

        for (Order order : orderApplication.getOrders()) {
            displayOrder(order);
        }
    }

    public static void displayTracking(TrackingInfo info) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                  Distributed Security Management Client Profile                                 |");
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("| distance: " + info.getDistance() + " | number: " + info.getTrackingNumber() + " | time remaining: "+ info.getTimeRemaining());
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|");
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
        System.out.println("|=================================================================================================================|");
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
        System.out.println("|                                       No Quotation Available from: " + String.format("%1$-45s", quotation.getCompany()) + "|");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayOrder(Order order) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                Distributed Security Management Order Reference                                  |");
        System.out.println("|=================================================================================================================|");
        System.out.println("|                           |                                                    |                                |");
        System.out.println(
                "| Price: " + String.format("%1$-18s", NumberFormat.getCurrencyInstance().format(order.getPrice())) +
                        " | Reference: " + String.format("%1$-30s", order.getReference()) +
                        " | Tracking Number: " + String.format("%1$-13s", order.getTrackingNumber())+" |");
        System.out.println("|=================================================================================================================|");
    }

    public static void displayLogo() {
        System.out.println(ANSI_YELLOW + "\n\n ______      ______    ____    ____  " + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "|_   _ `.  .' ____ \\  |_   \\  /   _| " + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "  | | `. \\ | (___ \\_|   |   \\/   |   " + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "  | |  | |  _.____`.    | |\\  /| |   " + ANSI_RESET);
        System.out.println(ANSI_YELLOW + " _| |_.' / | \\____) |  _| |_\\/_| |_  " + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "|______.'   \\______.' |_____||_____| \n\n" + ANSI_RESET);
    }
}

