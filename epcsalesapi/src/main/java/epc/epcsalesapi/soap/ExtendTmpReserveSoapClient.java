package epc.epcsalesapi.soap;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import epc.epcsalesapi.soap.wsclient.extendTmpReserve.ExtendTmpReserve;
import epc.epcsalesapi.soap.wsclient.extendTmpReserve.ExtendTmpReserveResponse;

public class ExtendTmpReserveSoapClient extends WebServiceGatewaySupport {
    public ExtendTmpReserveResponse extendReserveTmp(ExtendTmpReserve extendTmpReserve) {
        return (ExtendTmpReserveResponse)getWebServiceTemplate().marshalSendAndReceive(extendTmpReserve);
    }
}
