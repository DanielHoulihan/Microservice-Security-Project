package info;

import java.util.ArrayList;

public class OrderApplication implements java.io.Serializable{

    public OrderApplication(long clientID, ClientInfo clientInfo, ArrayList<Order> orders){
        this.clientID = clientID;
        this.clientInfo = clientInfo;
        this.orders=orders;
    }

    public OrderApplication(){}

    private long clientID;
    private ClientInfo clientInfo;
    private ArrayList<Order> orders = new ArrayList<>();

    public long getClientID() {
        return clientID;
    }

    public void setClientID(long clientID) {
        this.clientID = clientID;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void addOrders(Order order){
        this.orders.add(order);
    }
    public ArrayList<Order> getOrders() {
        return orders;
    }

}
