/**
 * 
 */
package epc.epcsalesapi.sales.bean;

/**
 * @author williamtam
 *
 */
public class EpcInstallmentCharge extends EpcCharge {

    private String label;
    private boolean isAllowInstallment;
    private boolean isHandlingFeeWaive;

    public EpcInstallmentCharge() {
        super();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isAllowInstallment() {
        return isAllowInstallment;
    }

    public void setAllowInstallment(boolean isAllowInstallment) {
        this.isAllowInstallment = isAllowInstallment;
    }

    public boolean isHandlingFeeWaive() {
        return isHandlingFeeWaive;
    }

    public void setHandlingFeeWaive(boolean isHandlingFeeWaive) {
        this.isHandlingFeeWaive = isHandlingFeeWaive;
    }
    
}
