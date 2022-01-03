import info.*;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class Client {

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final ArrayList<ClientInfo> clients = new ArrayList<>();
    private static final TreeMap<Integer, Quotation> cache = new TreeMap<Integer, Quotation>();

    public static void main(String[] args) {

        displayLogo();

        System.out.println("Welcome to Distributed Security Management!\n");
        String check = "";

        while (true) {
            do {
                System.out.println("\nPlease choose one of the following options in order to proceed: \n");
                System.out.println("1) To create a new user profile please enter: CREATE");
                System.out.println("2) To create a new order please enter:        ORDER");
                System.out.println("3) To track an existing order please enter:   TRACK\n");
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine().toUpperCase();

                if (input.equals("CREATE")) {
                    check = "Y";
                    createUser();
                    displayProfiles(clients);
                } else if (clients.isEmpty()) {
                    System.out.println("No users exist, you must create one");
                    check = "Y";
                    createUser();
                    displayProfiles(clients);
                } else if (input.equals("TRACK")) {
                    check = "Y";
                    getTracking();
                } else if (input.equals("ORDER") && !clients.isEmpty()) {
                    check = "Y";
                    System.out.println("\nWhich user would you like to order as? The available user accounts are: ");
                    for (ClientInfo client : clients) {
                        System.out.println("-> " + client.getName());
                    }
                    String user = sc.nextLine().toUpperCase();
                    for (ClientInfo client : clients) {
                        if (client.getName().equals(user)) {
                            getQuotes(client);
                        }
                    }
                    order();
                } else {
                    check = "N";
                    System.out.println("\n*** Wrong input given, please try again. ***\n");
                }
            } while (check.equalsIgnoreCase("N"));
        }
    }

    public static void displayProfiles(ArrayList<ClientInfo> info){
        System.out.println("\n|=================================================================================================================|");
        System.out.println("|                                  Distributed Security Management Client Profiles                                |");
        System.out.println("|=================================================================================================================|");
        for (ClientInfo clientInfo : clients) {
            displayProfile(clientInfo);
        }
    }

    public static void createUser() {
        String output = "";
        String name = "";
        String urgency = "";
        String location = "";
        do {
            Scanner sc = new Scanner(System.in);
            System.out.println("\nEnter your name: ");
            name = sc.nextLine().toUpperCase();
            System.out.println("Enter the urgency which the job requires (ASAP/SOON/WHENEVER): ");
            urgency = sc.nextLine().toUpperCase();
            System.out.println("Enter the location which the job requires (AIR/SEA/LAND): ");
            location = sc.nextLine().toUpperCase();

            System.out.println("\nThe details you entered are:\n");
            System.out.println("Name: " + name);
            System.out.println("Urgency: " + urgency);
            System.out.println("Location: " + location);
            if ((urgency.equals("ASAP") || urgency.equals("SOON") || urgency.equals("WHENEVER")) && (location.equals("AIR") || location.equals("SEA") || location.equals("LAND"))) {
                System.out.println("\nAre you satisfied with the details entered? (Y) or (N).");
                output = sc.nextLine().toUpperCase();
            } else {
                output = "N";
                System.out.println("\n*** One or more of the inputs given were wrong, try again please. ***\n");
            }
        } while (output.equalsIgnoreCase("N"));
        clients.add(new ClientInfo(name, urgency, location));
        System.out.println("\n** New User Created Successfully! **");
    }


    public static void getQuotes(ClientInfo client) {
        int index = 1;
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ClientInfo> request = new HttpEntity<>(client);
        ClientApplication clientApplication = restTemplate.postForObject("http://localhost:8083/applications", request, ClientApplication.class);
//        ClientApplication clientApplication = restTemplate.postForObject("http://0.0.0.0:8083/applications", request, ClientApplication.class);
        displayProfile(client);

        assert clientApplication != null;
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
        System.out.println("\nEnter the assigned tracking number please: ");
        String trackingNumber = sc.nextLine();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(trackingNumber);
        TrackingApplication trackingApplication = restTemplate.postForObject("http://localhost:8085/applications", request, TrackingApplication.class);
//        TrackingApplication trackingApplication = restTemplate.postForObject("http://docker.for.mac.host.internal:8085/applications", request, TrackingApplication.class);
        assert trackingApplication != null;

        System.out.println("\n** Tracking Reference Found Successfully! **");
        for (TrackingInfo tracking : trackingApplication.getTracking()) {
            if(tracking.getTrackingNumber()!=null) {
                displayTracking(tracking);
            }
        }
    }

    public static void order() {

        RestTemplate restTemplate = new RestTemplate();
        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter the quotation which you would like to order (E.g 1/2/3): ");
        int chosenOrder = sc.nextInt();

        Quotation quote1 = cache.get(chosenOrder);
        HttpEntity<Quotation> request2 = new HttpEntity<>(quote1);
        OrderApplication orderApplication = restTemplate.postForObject("http://localhost:8084/applications", request2, OrderApplication.class);
//        OrderApplication orderApplication = restTemplate.postForObject("http://docker.for.mac.host.internal:8084/applications", request2, OrderApplication.class);

        assert orderApplication != null;
        System.out.println("\n** New Order Created Successfully! **");
        for (Order order : orderApplication.getOrders()) {
            displayOrder(order);
        }
    }

    public static void displayTracking(TrackingInfo tracking) {
        System.out.println("\n\n|=================================================================================================================|");
        System.out.println("|                                  Distributed Security Management Tracking Details                               |");
        System.out.println("|=================================================================================================================|");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("| Distance: " + String.format("%1$-25s", tracking.getDistance()) + " | Tracking Number: " + String.format("%1$-18s", tracking.getTrackingNumber()) + " | Time Remaining: "+ String.format("%1$-19s", tracking.getTimeRemaining())+" |");
        System.out.println("|                                     |                                     |                                     |");
        System.out.println("|=================================================================================================================|\n\n");
    }


    public static void displayProfile(ClientInfo info) {
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
        System.out.println("|                                     |                                     |                                     |");
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
        System.out.println("|                       |                                                    |                                    |");
        System.out.println(
                "| Price: " + String.format("%1$-14s", NumberFormat.getCurrencyInstance().format(order.getPrice())) +
                        " | Reference: " + String.format("%1$-30s", order.getReference()) +
                        " | Tracking Number: " + String.format("%1$-17s", order.getTrackingNumber())+" |");
        System.out.println("|                       |                                                    |                                    |");
        System.out.println("|=================================================================================================================|\n\n");
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

