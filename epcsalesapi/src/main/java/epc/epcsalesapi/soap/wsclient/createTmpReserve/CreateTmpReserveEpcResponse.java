//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.06.02 at 05:15:32 PM CST 
//


package epc.epcsalesapi.soap.wsclient.createTmpReserve;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreateTmpReserveEpcResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateTmpReserveEpcResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InvCreateTmpReserveResultBean" type="{http://online_store.pos/}invCreateTmpReserveResultBean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "CreateTmpReserveEpcResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateTmpReserveEpcResponse", propOrder = {
    "invCreateTmpReserveResultBean"
})
public class CreateTmpReserveEpcResponse {

    @XmlElement(name = "InvCreateTmpReserveResultBean")
    protected InvCreateTmpReserveResultBean invCreateTmpReserveResultBean;

    /**
     * Gets the value of the invCreateTmpReserveResultBean property.
     * 
     * @return
     *     possible object is
     *     {@link InvCreateTmpReserveResultBean }
     *     
     */
    public InvCreateTmpReserveResultBean getInvCreateTmpReserveResultBean() {
        return invCreateTmpReserveResultBean;
    }

    /**
     * Sets the value of the invCreateTmpReserveResultBean property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvCreateTmpReserveResultBean }
     *     
     */
    public void setInvCreateTmpReserveResultBean(InvCreateTmpReserveResultBean value) {
        this.invCreateTmpReserveResultBean = value;
    }

}
