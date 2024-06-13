package epc.epcsalesapi.soap;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import epc.epcsalesapi.soap.wsclient.createTmpReserve.CreateTmpReserveEpc;
import epc.epcsalesapi.soap.wsclient.createTmpReserve.CreateTmpReserveEpcResponse;


public class CreateTmpReserveSoapClient extends WebServiceGatewaySupport {
    public CreateTmpReserveEpcResponse reserveTmp(CreateTmpReserveEpc createTmpReserveEpc) {
        return (CreateTmpReserveEpcResponse)getWebServiceTemplate().marshalSendAndReceive(createTmpReserveEpc);
    }
}
