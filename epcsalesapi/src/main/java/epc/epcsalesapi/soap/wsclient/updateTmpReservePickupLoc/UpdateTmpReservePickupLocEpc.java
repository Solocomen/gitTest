//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.06.03 at 10:41:25 PM CST 
//


package epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateTmpReservePickupLocEpc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateTmpReservePickupLocEpc"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="inBean" type="{http://online_store.pos/}invUpdateTmpReservePickupLocBean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "UpdateTmpReservePickupLocEpc")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateTmpReservePickupLocEpc", propOrder = {
    "inBean"
})
public class UpdateTmpReservePickupLocEpc {

    protected InvUpdateTmpReservePickupLocBean inBean;

    /**
     * Gets the value of the inBean property.
     * 
     * @return
     *     possible object is
     *     {@link InvUpdateTmpReservePickupLocBean }
     *     
     */
    public InvUpdateTmpReservePickupLocBean getInBean() {
        return inBean;
    }

    /**
     * Sets the value of the inBean property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvUpdateTmpReservePickupLocBean }
     *     
     */
    public void setInBean(InvUpdateTmpReservePickupLocBean value) {
        this.inBean = value;
    }

}
