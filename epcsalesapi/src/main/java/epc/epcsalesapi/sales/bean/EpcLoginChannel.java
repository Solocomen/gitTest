package epc.epcsalesapi.sales.bean;

public class EpcLoginChannel {
    
    // values in login session
    public static final String ONLINE = "Online";
    public static final String STORE = "Retail Business Development";
    public static final String DS = "Direct Sales";
    public static final String PR = "Proactive Retention";
    public static final String TS = "Telesales";
    public static final String CS = "Customer Service";


    public static boolean isChannelValid(String iChannel) {
        boolean isValid = false;
        if(ONLINE.equals(iChannel)
            || STORE.equals(iChannel)
            || DS.equals(iChannel)
            || PR.equals(iChannel)
            || TS.equals(iChannel)
            || CS.equals(iChannel)
        ) {
            isValid = true;
        }

        return isValid;
    }

}
