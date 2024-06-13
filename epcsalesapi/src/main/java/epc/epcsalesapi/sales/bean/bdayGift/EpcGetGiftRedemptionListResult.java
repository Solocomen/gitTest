package epc.epcsalesapi.sales.bean.bdayGift;

import java.util.List;

public class EpcGetGiftRedemptionListResult {

    private String resultCode;
    private String resultMsg;
    private List<EpcGiftRedemption> giftList;

    public String getResultCode() {
        return resultCode;
    }
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
    public String getResultMsg() {
        return resultMsg;
    }
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
    public List<EpcGiftRedemption> getGiftList() {
        return giftList;
    }
    public void setGiftList(List<EpcGiftRedemption> giftList) {
        this.giftList = giftList;
    }

}
