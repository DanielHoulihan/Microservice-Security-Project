package quote;

import info.ClientApplication;
import info.ClientInfo;
import info.Quotation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
@RestController
public class QuotationBroker {

    public static HashMap<Integer, ClientApplication> map = new HashMap();
    public static int clientNumber = 0;

    @RequestMapping(value="/applications",method = RequestMethod.POST)
    public ClientApplication getQuotations(@RequestBody ClientInfo info){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ClientInfo> request = new HttpEntity<>(info);
        ArrayList<Quotation> quotations = new ArrayList<>();

        quotations.add(restTemplate.postForObject("http://air:8080/quotations", request, Quotation.class));
        quotations.add(restTemplate.postForObject("http://sea:8081/quotations", request, Quotation.class));
        quotations.add(restTemplate.postForObject("http://ground:8082/quotations", request, Quotation.class));

        ClientApplication clientApplication = new ClientApplication(clientNumber, info, quotations);
        map.put(clientNumber, clientApplication);
        clientNumber++;
        return clientApplication;
    }

    @RequestMapping(value="applications/{clientNumber}",method=RequestMethod.GET)
    public ClientApplication getResource() {
        if (map == null) throw new NoSuchQuotationException(); return map.get(clientNumber);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class NoSuchQuotationException extends RuntimeException {
        static final long serialVersionUID = -6516152229878843037L;
    }

    @RequestMapping(value="/applications",method=RequestMethod.GET)
    public ArrayList<ClientApplication> listApplications() {
        return new ArrayList<>(map.values());
    }
}
