package epc.epcsalesapi.sales;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcDeliveryDetail;
import epc.epcsalesapi.sales.bean.EpcDeliveryInfo;
import epc.epcsalesapi.sales.bean.EpcExtendAllReserveTmpTicket;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcOrderItemDetail;
import epc.epcsalesapi.sales.bean.EpcReserveStock;
import epc.epcsalesapi.sales.bean.EpcReserveStockItem;
import epc.epcsalesapi.stock.EpcReserveStockHandler;
import epc.epcsalesapi.stock.EpcStockHandler;
import epc.epcsalesapi.stock.bean.EpcCreateTmpReserveResult;
import epc.epcsalesapi.stock.bean.EpcExtendTmpReserve;
import epc.epcsalesapi.stock.bean.EpcRemoveTmpReserve;

@Service
public class EpcExtendReserveTicketHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcExtendReserveTicketHandler.class);

    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcOrderAttrHandler epcOrderAttrHandler;
    private final EpcOrderHandler epcOrderHandler;
    private final EpcStockHandler epcStockHandler;
    private final EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler;
    private final EpcReserveStockHandler epcReserveStockHandler;

    public EpcExtendReserveTicketHandler(EpcSecurityHelper epcSecurityHelper, EpcOrderAttrHandler epcOrderAttrHandler,
            EpcOrderHandler epcOrderHandler, EpcStockHandler epcStockHandler,
            EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler, EpcReserveStockHandler epcReserveStockHandler) {
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcOrderAttrHandler = epcOrderAttrHandler;
        this.epcOrderHandler = epcOrderHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcGetDeliveryInfoHandler = epcGetDeliveryInfoHandler;
        this.epcReserveStockHandler = epcReserveStockHandler;
    }


    public void extendAllReserveTmpTicket(EpcExtendAllReserveTmpTicket epcExtendAllReserveTmpTicket) {
    	String orderReference = "";
        String custId = "";
        int orderId = 0;
        int extendMins = 0;
        boolean isValid = true;
        String errMsg = "";
        String createUser = "";
    	String createSalesman = "";
    	String createChannel = "";
    	String createLocation = "";
    	String itemIdKey = "";
    	String itemProductCode = "";
        String itemWarehouse = "";
    	String tmpReserveId = "";
    	EpcExtendTmpReserve epcExtendTmpReserve = null;
    	String tmpPickupLocation = "";
    	EpcDeliveryInfo epcDeliveryInfo = null;
    	EpcCreateTmpReserveResult epcCreateTmpReserveResult = null;
        ArrayList<EpcOrderItemDetail> itemList = null;
        EpcReserveStock epcReserveStock = null;
        EpcReserveStock epcReserveResult = null;
        ArrayList<EpcReserveStockItem> reserveItemList = null;
        EpcReserveStockItem epcReserveStockItem = null;
        EpcRemoveTmpReserve epcRemoveTmpReserve = null;
        String previousChannel = "";
        ArrayList<String> failItemList = new ArrayList<>();
        epcExtendAllReserveTmpTicket.setFailItemList(failItemList);
    	String logStr = "[extendAllReserveTmpTicket]";
        String tmpLogStr = "";
        
        try {
        	custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcExtendAllReserveTmpTicket.getCustId()));
            orderId = epcExtendAllReserveTmpTicket.getOrderId();
            extendMins = epcExtendAllReserveTmpTicket.getExtendMins();
            createUser = StringHelper.trim(epcExtendAllReserveTmpTicket.getCreateUser());
            createSalesman = StringHelper.trim(epcExtendAllReserveTmpTicket.getCreateSalesman());
            createChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcExtendAllReserveTmpTicket.getCreateChannel()));
            createLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcExtendAllReserveTmpTicket.getCreateLocation()));
            
            // if createChannel == previousChannel
            //  then extend
            // else
            //  remove previous tmp ticket, and re-new
            previousChannel = epcOrderAttrHandler.getAttrValue(orderId, "", "", epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_CURRENT_CHANNEL);

// commented by kerrytsang, 20220510
//            if(extendMins <= 0) {
//            	extendMins = 20; // default
//            }
            
            logStr += "[custId:" + custId + "][orderId:" + orderId + "] ";
logger.info("{}{}{}{}{}", logStr, " previousChannel:", previousChannel, ",createChannel:", createChannel);
logger.info("{}{}{}", logStr, " createLocation:", createLocation);

            
            // basic checking
            orderReference = epcOrderHandler.isOrderBelongCust(custId, orderId);
        	if("NOT_BELONG".equals(orderReference)) {
        		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
            	isValid = false;
        	}  
            
            if(epcOrderHandler.isOrderLocked(custId, orderId)) {
                errMsg += "input order [" + orderId + "] is locked. ";
                isValid = false;
            }

            if(!EpcLoginChannel.isChannelValid(createChannel)) {
                errMsg += "input channel [" + createChannel + "] is not valid. ";
                isValid = false;
            }
            // end of basic checking
        	
        	
        	if(isValid) {
        		// get all delivery info filled
        		epcDeliveryInfo = epcGetDeliveryInfoHandler.getPickupLocation(orderId);

        		
        		// get item list per order
        		itemList = epcOrderHandler.getAllDeviceItems(orderId, "FOR-RESERVE");

                if(previousChannel.equals(createChannel)) {
                    //  then extend
logger.info("{}{}", logStr, " previousChannel = createChannel");

                    for(EpcOrderItemDetail epcOrderItemDetail : itemList) {
                        itemIdKey = epcOrderItemDetail.getItemId(); // item id
                        itemProductCode = epcOrderItemDetail.getItemCode(); // item_code - product_code
                        tmpReserveId = epcOrderItemDetail.getReserveId(); // reserve_id
                        itemWarehouse = epcOrderItemDetail.getWarehouse();

                        if("AP".equals(itemWarehouse)) {
logger.info("{}{}{}{}", logStr, "  no need to proceed item ", itemIdKey, " due to warehouse AP");
                            continue;
                        } else if("NO_STOCK".equals(tmpReserveId)) {
logger.info("{}{}{}{}", logStr, "  no need to proceed item ", itemIdKey, " due to NO_STOCK");
                            continue;
                        }

                        if(!"".equals(tmpReserveId)) {
logger.info("{}{}", logStr, " tmp reserve ticket NOT empty, perform extension");
                            extendMins = epcStockHandler.getReserveMins(createChannel, itemProductCode);
                            epcExtendTmpReserve = epcStockHandler.extendEpcTmpReserve(tmpReserveId, extendMins, createChannel);
                            
                            tmpLogStr = " extend tmp ticket for itemId:" + itemIdKey + 
                                        ",productCode:" + itemProductCode + 
                                        ",reserveId:" + tmpReserveId + 
                                        ",extendMins:" + extendMins + 
                                        " epcExtendTmpReserve.getResult():" + epcSecurityHelper.encode(epcExtendTmpReserve.getResult()) + 
                                        ",epcExtendTmpReserve.getErrMsg():" + epcSecurityHelper.encode(epcExtendTmpReserve.getErrMsg());                
logger.info("{}{}", logStr, tmpLogStr);
                        } else {
logger.info("{}{}", logStr, " tmp reserve ticket empty, create another ticket");
                            epcExtendTmpReserve = new EpcExtendTmpReserve();
                            epcExtendTmpReserve.setResult("FAIL"); // used to trigger re-new ticket
                        }
    
                        if( ! EpcApiStatusReturn.RETURN_SUCCESS.equals(epcExtendTmpReserve.getResult()) ) {
                            // if extend action is fail, create another new tmp ticket, kerrytsang, 20210707
                            //  if still fail, then reject caller
                            
                            // get the latest pickup location
                            tmpPickupLocation = ""; // reset
                            
                            for(EpcDeliveryDetail d : epcDeliveryInfo.getDetails()) {
                                for(String itemId : d.getItems()) {
                                    if(itemId.equals(itemIdKey)) {
                                        tmpPickupLocation = d.getPickupStore();
                                        break;
                                    }
                                }
                            }

                            // if no delivery info is found, then use login location, kerrytsang, 20230907
                            if("".equals(tmpPickupLocation)) {
                                tmpPickupLocation = createLocation;
                            }
                            // end of if no delivery info is found, then use login location, kerrytsang, 20230907
    
                            tmpLogStr = "  create ANOTHER tmp ticket for itemId:" + itemIdKey + ",productCode:" + itemProductCode + ",location:" + tmpPickupLocation;
logger.info("{}{}", logStr, tmpLogStr);
                            // end of get the latest pickup location
    
                            epcReserveStock = new EpcReserveStock();
                            epcReserveStock.setCustId(custId);
                            epcReserveStock.setOrderId(orderId);
                            epcReserveStock.setLocation(tmpPickupLocation);
                            epcReserveStock.setChannel(createChannel);
                            
                            reserveItemList = new ArrayList<EpcReserveStockItem>();
                            epcReserveStock.setItems(reserveItemList);
    
                            epcReserveStockItem = new EpcReserveStockItem();
                            epcReserveStockItem.setItemId(itemIdKey);
                            epcReserveStockItem.setProductCode(itemProductCode);
                            reserveItemList.add(epcReserveStockItem);
                            
                            epcReserveResult = epcReserveStockHandler.reserveStock(epcReserveStock);
logger.info("{}{}{}{}{}", logStr, "  itemId:", itemIdKey, ",epcReserveResult.getSaveStatus():", epcSecurityHelper.encode(epcReserveResult.getSaveStatus()));
logger.info("{}{}{}{}{}", logStr, "  itemId:", itemIdKey, ",epcReserveResult.getErrorCode():", epcSecurityHelper.encode(epcReserveResult.getErrorCode()));
logger.info("{}{}{}{}{}", logStr, "  itemId:", itemIdKey, ",epcReserveResult.getErrorMessage():", epcSecurityHelper.encode(epcReserveResult.getErrorMessage()));
                            if( !EpcApiStatusReturn.RETURN_SUCCESS.equals(epcReserveResult.getSaveStatus()) ) {
                                isValid = false;
                                errMsg += "extend tmp ticket and create another tmp ticket fail for item " + itemIdKey + " (err msg: " + epcReserveResult.getErrorMessage() + "). ";

                                failItemList.add(itemIdKey);
                            }
                        }
                    }
                } else {
                    // remove previous tmp ticket, and re-new
logger.info("{}{}", logStr, " previousChannel != createChannel");

                    for(EpcOrderItemDetail epcOrderItemDetail : itemList) {
                        itemIdKey = epcOrderItemDetail.getItemId(); // item id
                        itemProductCode = epcOrderItemDetail.getItemCode(); // item_code - product_code
                        tmpReserveId = epcOrderItemDetail.getReserveId(); // reserve_id

                        // remove previous tmp ticket
                        if(!"".equals(tmpReserveId) && !"NO_STOCK".equals(tmpReserveId)) {
                            epcRemoveTmpReserve = epcStockHandler.removeTmpReserve(tmpReserveId);
                            tmpLogStr = "  remove previous tmp ticket for itemId:" + itemIdKey + ",productCode:" + itemProductCode + ",location:" + tmpPickupLocation +
                                        ",result:" + epcRemoveTmpReserve.getResult();
logger.info("{}{}", logStr, tmpLogStr);
                        }
                        // end of remove previous tmp ticket
                    
                        // get the latest pickup location
                        tmpPickupLocation = ""; // reset
                            
                        for(EpcDeliveryDetail d : epcDeliveryInfo.getDetails()) {
                            for(String itemId : d.getItems()) {
                                if(itemId.equals(itemIdKey)) {
                                    tmpPickupLocation = d.getPickupStore();
                                    break;
                                }
                            }
                        }

                        // if no delivery info is found, then use login location, kerrytsang, 20230907
                        if("".equals(tmpPickupLocation)) {
                            tmpPickupLocation = createLocation;
                        }
                        // end of if no delivery info is found, then use login location, kerrytsang, 20230907

                        tmpLogStr = "  create tmp ticket (change channel) for itemId:" + itemIdKey + ",productCode:" + itemProductCode + ",location:" + tmpPickupLocation;
logger.info("{}{}", logStr, tmpLogStr);
                        // end of get the latest pickup location

                        epcReserveStock = new EpcReserveStock();
                        epcReserveStock.setCustId(custId);
                        epcReserveStock.setOrderId(orderId);
                        epcReserveStock.setLocation(tmpPickupLocation);
                        epcReserveStock.setChannel(createChannel);
                        
                        reserveItemList = new ArrayList<EpcReserveStockItem>();
                        epcReserveStock.setItems(reserveItemList);

                        epcReserveStockItem = new EpcReserveStockItem();
                        epcReserveStockItem.setItemId(itemIdKey);
                        epcReserveStockItem.setProductCode(itemProductCode);
                        reserveItemList.add(epcReserveStockItem);
                        
                        epcReserveResult = epcReserveStockHandler.reserveStock(epcReserveStock);
logger.info("{}{}{}{}{}", logStr, "  itemId:", itemIdKey, ",epcReserveResult.getSaveStatus():", epcSecurityHelper.encode(epcReserveResult.getSaveStatus()));
logger.info("{}{}{}{}{}", logStr, "  itemId:", itemIdKey, ",epcReserveResult.getErrorCode():", epcSecurityHelper.encode(epcReserveResult.getErrorCode()));
logger.info("{}{}{}{}{}", logStr, "  itemId:", itemIdKey, ",epcReserveResult.getErrorMessage():", epcSecurityHelper.encode(epcReserveResult.getErrorMessage()));
                        if( !EpcApiStatusReturn.RETURN_SUCCESS.equals(epcReserveResult.getSaveStatus()) ) {
                            isValid = false;
                            errMsg += "extend tmp ticket and create another tmp ticket fail for item " + itemIdKey + " (err msg: " + epcReserveResult.getErrorMessage() + "). ";

                            failItemList.add(itemIdKey);
                        }
                    }
                }

                
                if(isValid) {
                    epcExtendAllReserveTmpTicket.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
                } else {
                    epcExtendAllReserveTmpTicket.setResult(EpcApiStatusReturn.RETURN_FAIL);
                    epcExtendAllReserveTmpTicket.setErrMsg(errMsg);
                }
        	} else {
        		epcExtendAllReserveTmpTicket.setResult(EpcApiStatusReturn.RETURN_FAIL);
        		epcExtendAllReserveTmpTicket.setErrMsg(errMsg);
        	}
        } catch(Exception e) {
            e.printStackTrace();
            
            epcExtendAllReserveTmpTicket.setResult(EpcApiStatusReturn.RETURN_FAIL);
    		epcExtendAllReserveTmpTicket.setErrMsg(e.getMessage());
        } finally {
        }
    }
}
