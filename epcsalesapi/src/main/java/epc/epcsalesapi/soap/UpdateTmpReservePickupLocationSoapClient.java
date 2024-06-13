package epc.epcsalesapi.soap;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc.UpdateTmpReservePickupLocEpc;
import epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc.UpdateTmpReservePickupLocEpcResponse;

public class UpdateTmpReservePickupLocationSoapClient extends WebServiceGatewaySupport {
    public UpdateTmpReservePickupLocEpcResponse updateTmpReservePickupLocation(UpdateTmpReservePickupLocEpc updateTmpReservePickupLocEpc) {
        return (UpdateTmpReservePickupLocEpcResponse)getWebServiceTemplate().marshalSendAndReceive(updateTmpReservePickupLocEpc);
    }
}
