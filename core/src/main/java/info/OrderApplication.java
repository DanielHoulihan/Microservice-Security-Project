package info;

import java.util.ArrayList;

public class OrderApplication implements java.io.Serializable{
    public long clientID;
    public ClientInfo clientInfo;
    public ArrayList<Order> orders = new ArrayList<>();
    public OrderApplication(long clientID, ClientInfo clientInfo, ArrayList<Order> orders){
        this.clientID = clientID;
        this.clientInfo = clientInfo;
        this.orders=orders;
    }

    public OrderApplication(){}

    public void addOrders(Order order){
        this.orders.add(order);
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }
}
