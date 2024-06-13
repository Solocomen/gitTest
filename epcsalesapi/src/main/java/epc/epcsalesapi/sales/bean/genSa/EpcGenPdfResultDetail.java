package epc.epcsalesapi.sales.bean.genSa;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "merge_doc")
@XmlAccessorType(XmlAccessType.FIELD)
public class EpcGenPdfResultDetail {
    @XmlElement(name = "result")
    private String result;
    @XmlElement(name = "merge_file")
    private String mergeFile;

    public EpcGenPdfResultDetail() {}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMergeFile() {
        return mergeFile;
    }

    public void setMergeFile(String mergeFile) {
        this.mergeFile = mergeFile;
    }

    
}
