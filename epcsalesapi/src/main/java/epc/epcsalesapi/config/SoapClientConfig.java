package epc.epcsalesapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import epc.epcsalesapi.soap.CancelTmpReserveSoapClient;
import epc.epcsalesapi.soap.CreateTmpReserveSoapClient;
import epc.epcsalesapi.soap.ExtendTmpReserveSoapClient;
import epc.epcsalesapi.soap.UpdateTmpReservePickupLocationSoapClient;
import epc.epcsalesapi.soap.wsclient.cancelTmpReserve.CancelTmpReserveResponse;
import epc.epcsalesapi.soap.wsclient.createTmpReserve.CreateTmpReserveEpcResponse;
import epc.epcsalesapi.soap.wsclient.extendTmpReserve.ExtendTmpReserveResponse;
import epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc.UpdateTmpReservePickupLocEpcResponse;

@Configuration
public class SoapClientConfig {

    @Value("${FES_STOCK_CREATE_EPC_TMP_RESERVE_LINK}")
	private String createTmpReserveUrl;

    @Value("${FES_STOCK_CANCEL_TMP_RESERVE_LINK}")
	private String cancelTmpReserveUrl;

    @Value("${FES_STOCK_EXTEND_EPC_TMP_RESERVE_LINK}")
	private String extendTmpReserveUrl;

    @Value("${FES_STOCK_UPDATE_EPC_TMP_LOCATION_LINK}")
	private String updateTmpReservePickupLocationUrl;


    @Bean
    public CreateTmpReserveSoapClient createTmpReserveSoupClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setBeanClassLoader(CreateTmpReserveEpcResponse.class.getClassLoader());
        marshaller.setContextPath("epc.epcsalesapi.soap.wsclient.createTmpReserve");

        CreateTmpReserveSoapClient client = new CreateTmpReserveSoapClient();
        client.setDefaultUri(createTmpReserveUrl);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


    @Bean
    public CancelTmpReserveSoapClient cancelTmpReserveSoupClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setBeanClassLoader(CancelTmpReserveResponse.class.getClassLoader());
        marshaller.setContextPath("epc.epcsalesapi.soap.wsclient.cancelTmpReserve");

        CancelTmpReserveSoapClient client = new CancelTmpReserveSoapClient();
        client.setDefaultUri(cancelTmpReserveUrl);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


    @Bean
    public ExtendTmpReserveSoapClient extendTmpReserveSoupClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setBeanClassLoader(ExtendTmpReserveResponse.class.getClassLoader());
        marshaller.setContextPath("epc.epcsalesapi.soap.wsclient.extendTmpReserve");

        ExtendTmpReserveSoapClient client = new ExtendTmpReserveSoapClient();
        client.setDefaultUri(extendTmpReserveUrl);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


    @Bean
    public UpdateTmpReservePickupLocationSoapClient updateTmpReservePickupLocationSoupClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setBeanClassLoader(UpdateTmpReservePickupLocEpcResponse.class.getClassLoader());
        marshaller.setContextPath("epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc");

        UpdateTmpReservePickupLocationSoapClient client = new UpdateTmpReservePickupLocationSoapClient();
        client.setDefaultUri(updateTmpReservePickupLocationUrl);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
