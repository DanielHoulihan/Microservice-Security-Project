package info;

public class ClientInfo {

    public ClientInfo(String name, String urgency, String location) {
        this.name = name;
        this.urgency = urgency;
        this.location = location;
    }

    public ClientInfo() {}

    /**
     * Public fields are used as modern best practice argues that use of set/get
     * methods is unnecessary as (1) set/get makes the field mutable anyway, and
     * (2) set/get introduces additional method calls, which reduces performance.
     */
    private String name;
    private String urgency;
    private String location;

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getUrgency() {return urgency;}

    public void setUrgency(String urgency) {this.urgency = urgency;}

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

}
