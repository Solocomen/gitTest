package epc.epcsalesapi.soap;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import epc.epcsalesapi.soap.wsclient.cancelTmpReserve.CancelTmpReserve;
import epc.epcsalesapi.soap.wsclient.cancelTmpReserve.CancelTmpReserveResponse;

public class CancelTmpReserveSoapClient extends WebServiceGatewaySupport {
    public CancelTmpReserveResponse cancelReserveTmp(CancelTmpReserve cancelTmpReserve) {
        return (CancelTmpReserveResponse)getWebServiceTemplate().marshalSendAndReceive(cancelTmpReserve);
    }
}
