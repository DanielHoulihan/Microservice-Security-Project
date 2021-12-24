package info;

import java.util.ArrayList;

public class ClientApplication implements java.io.Serializable{
    public long clientID;
    public ClientInfo clientInfo;
    public ArrayList<Quotation> quotations = new ArrayList<>();
    public ClientApplication(long clientID, ClientInfo clientInfo, ArrayList<Quotation> quotations){
        this.clientID = clientID;
        this.clientInfo = clientInfo;
        this.quotations=quotations;
    }

    public ClientApplication(){}

    public void addQuotation(Quotation quotation){
        this.quotations.add(quotation);
    }

    public ArrayList<Quotation> getQuotations() {
        return quotations;
    }
}
