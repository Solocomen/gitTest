package epc.epcsalesapi.sales.bean.genSa;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "epc")
@XmlAccessorType(XmlAccessType.FIELD)
public class EpcGenPdfResult {
    @XmlElement(name = "merge_doc")
    private EpcGenPdfResultDetail epcGenPdfResultDetail;

    public EpcGenPdfResult() {}

    public EpcGenPdfResultDetail getEpcGenPdfResultDetail() {
        return epcGenPdfResultDetail;
    }

    public void setEpcGenPdfResultDetail(EpcGenPdfResultDetail epcGenPdfResultDetail) {
        this.epcGenPdfResultDetail = epcGenPdfResultDetail;
    }

    
}
