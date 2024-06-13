package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcDeliveryDetail;
import epc.epcsalesapi.sales.bean.EpcDeliveryInfo;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcSaveDeliveryInfoResult;
import epc.epcsalesapi.sales.bean.EpcUpdateCourier;
import epc.epcsalesapi.stock.EpcStockHandler;
import epc.epcsalesapi.stock.bean.EpcUpdateLocationToTmpReserve;

@Service
public class EpcDeliveryInfoHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcDeliveryInfoHandler.class);

    private DataSource epcDataSource;
    private EpcOrderHandler epcOrderHandler;
    private EpcSecurityHelper epcSecurityHelper;
    private EpcStockHandler epcStockHandler;

    public EpcDeliveryInfoHandler(DataSource epcDataSource, EpcOrderHandler epcOrderHandler,
            EpcSecurityHelper epcSecurityHelper, EpcStockHandler epcStockHandler) {
        this.epcDataSource = epcDataSource;
        this.epcOrderHandler = epcOrderHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcStockHandler = epcStockHandler;
    }


    public EpcSaveDeliveryInfoResult saveDeliveryInfo(EpcDeliveryInfo epcDeliveryInfo) {
        EpcSaveDeliveryInfoResult epcSaveDeliveryInfoResult = new EpcSaveDeliveryInfoResult();
        int orderId = 0;
        String custId = "";
        String deliveryMethod = "";
        String pickupStore = "";
        String deliveryAddress1 = "";
        String deliveryAddress2 = "";
        String deliveryAddress3 = "";
        String deliveryAddress4 = "";
        String deliveryContactPerson = "";
        String deliveryContactNo = "";
        String addrType = "";
        String addrToProfile = "";
        ArrayList<EpcDeliveryDetail> detailList = null;
        ArrayList<String> itemIdList = null;
        String decryptStr = "";
        boolean isValid = true;
        String errMsg = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtId = null;
        PreparedStatement pstmtUpdatePickupDate = null;
        PreparedStatement pstmtUpdateDeliveryId = null;
        ResultSet rsetId = null;
        String sql = "";
        int updateCnt = 0;
        String orderReference = "";
        int deliveryId = 0;
        TreeMap<String, String> reserveIdListMap = null;
        String tmpReserveId = "";
        EpcUpdateLocationToTmpReserve epcUpdateLocationToTmpReserve = null;
        boolean isItemIdBelongToOrder = false;
        String updateChannel = ""; // for inventory api
        String loginChannel = epcDeliveryInfo.getCreateChannel(); // for inventory api
        String createChannel = "";
        String logStr = "[saveDeliveryInfo]";
        String tmpLogStr = "";
        
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_item " + 
                    "   set pickup_date = ? " +
                    " where order_id = ? " +
                    "   and item_id = ? " +
                    "   and reserve_id = ? ";
            pstmtUpdatePickupDate = conn.prepareStatement(sql);
            
            sql = "update epc_order_item " + 
                    "   set delivery_id = ? " +
                    " where order_id = ? " +
                    "   and item_id = ? ";
            pstmtUpdateDeliveryId = conn.prepareStatement(sql);
            

            custId = StringHelper.trim(epcDeliveryInfo.getCustId());
            orderId = epcDeliveryInfo.getOrderId();
            detailList = epcDeliveryInfo.getDetails();
            
            epcSaveDeliveryInfoResult.setOrderId(orderId);
            epcSaveDeliveryInfoResult.setCustId(custId);

            logStr += "[orderId:" + orderId + "] ";


            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }
            
            if(StringUtils.isNotBlank(custId)) {
                // check with crm ?
                // ...
            } else {
                isValid = false;
                errMsg += "cust id is empty. ";
            }
            
            orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, orderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
                isValid = false;
            }

            if(epcOrderHandler.isOrderLocked(conn, custId, orderId)) {
                errMsg += "input order [" + orderId + "] is locked. ";
                isValid = false;
            }

            
            for(EpcDeliveryDetail d : detailList) {
                deliveryMethod = StringHelper.trim(d.getDeliveryMethod());
                pickupStore = StringHelper.trim(d.getPickupStore());
                deliveryAddress1 = StringHelper.trim(d.getDeliveryAddress1());
                deliveryAddress2 = StringHelper.trim(d.getDeliveryAddress2());
                deliveryAddress3 = StringHelper.trim(d.getDeliveryAddress3());
                deliveryAddress4 = StringHelper.trim(d.getDeliveryAddress4());
                deliveryContactPerson = StringHelper.trim(d.getDeliveryContactPerson());
                deliveryContactNo = StringHelper.trim(d.getDeliveryContactNo());
                addrType = StringHelper.trim(d.getAddrType());
                addrToProfile = StringHelper.trim(d.getAddrToProfile());
                itemIdList = d.getItems(); // cpq itemId(s)
                
                // check whether item is under the order
                for(String itemId : itemIdList) {
                    isItemIdBelongToOrder = epcOrderHandler.isItemIdBelongToOrder(conn, orderId, itemId);
                    if(!isItemIdBelongToOrder) {
                        errMsg += "item id [" + itemId + "] is not belonged to order [" + orderId + "]. ";
                        isValid = false;
                    }
                }
                // end of check whether item is under the order
                
                // check delivery method
                if("".equals(deliveryMethod)) {
                    isValid = false;
                    errMsg += "delivery method is empty. ";
                } else {
                    if(!"COURIER".equals(deliveryMethod) && !"STORE".equals(deliveryMethod)) {
                        isValid = false;
                        errMsg += "invalid delivery method. ";
                    }
                }
                // end of check delivery method


                // check pickup location
                if("".equals(pickupStore)) {
                    isValid = false;
                    errMsg += "pickup store is empty. ";
                } else {
                    if("COURIER".equals(deliveryMethod) && !"WHS".equals(pickupStore)) {
                        isValid = false;
                        errMsg += "invalid pickup store for courier order. ";
                    }
                }
                // end of check pickup location
                
                // check when delivery method = COURIER
                if("COURIER".equals(deliveryMethod)) {
                    // check contact person
                    if(StringUtils.isNotBlank(deliveryContactPerson)) {
                        try {
                            decryptStr = StringHelper.trim(EpcCrypto.dGet(deliveryContactPerson, "utf-8"));

                            if("".equals(decryptStr)) {
                                isValid = false;
                                errMsg += "contact person is empty. ";
                            }
                        } catch (Exception e) {
                            isValid = false;
                            errMsg += "contact person can't be decrypted. ";
                        }
                    } else {
                        isValid = false;
                        errMsg += "contact person is empty. ";
                    }
                    // end of check contact person

                    // check contact no
                    if("".equals(deliveryContactNo)) {
                        isValid = false;
                        errMsg += "contact no is empty. ";
                    }
                    // end of check contact no
                    
                    if(StringUtils.isNotBlank(deliveryAddress1)) {
                        try {
                            decryptStr = StringHelper.trim(EpcCrypto.dGet(deliveryAddress1, "utf-8"));

                            if("".equals(decryptStr)) {
                                isValid = false;
                                errMsg += "address1 is empty. ";
                            }
                        } catch (Exception e) {
                            isValid = false;
                            errMsg += "address1 can't be decrypted. ";
                        }
                    } else {
                        isValid = false;
                        errMsg += "address1 is empty. ";
                    }

                    if(StringUtils.isNotBlank(deliveryAddress2)) {
                        try {
                            decryptStr = StringHelper.trim(EpcCrypto.dGet(deliveryAddress2, "utf-8"));

                            if("".equals(decryptStr)) {
                                isValid = false;
                                errMsg += "address2 is empty. ";
                            }
                        } catch (Exception e) {
                            isValid = false;
                            errMsg += "address2 can't be decrypted. ";
                        }
                    } else {
                        isValid = false;
                        errMsg += "address2 is empty. ";
                    }

                    if(StringUtils.isNotBlank(deliveryAddress3)) {
                        try {
                            decryptStr = StringHelper.trim(EpcCrypto.dGet(deliveryAddress3, "utf-8"));

                            if("".equals(decryptStr)) {
                                isValid = false;
                                errMsg += "address3 is empty. ";
                            }
                        } catch (Exception e) {
                            isValid = false;
                            errMsg += "address3 can't be decrypted. ";
                        }
                    } else {
                        isValid = false;
                        errMsg += "address3 is empty. ";
                    }

                    if(StringUtils.isNotBlank(deliveryAddress4)) {
                        try {
                            decryptStr = StringHelper.trim(EpcCrypto.dGet(deliveryAddress4, "utf-8"));

                            if("".equals(decryptStr)) {
                                isValid = false;
                                errMsg += "address4 is empty. ";
                            }
                        } catch (Exception e) {
                            isValid = false;
                            errMsg += "address4 can't be decrypted. ";
                        }
                    } else {
                        isValid = false;
                        errMsg += "address4 is empty. ";
                    }
                    
                    if("".equals(addrType)) {
                        isValid = false;
                        errMsg += "addr type is empty. ";
                    } else {
                        if(!"C".equals(addrType) && !"R".equals(addrType)) {
                            isValid = false;
                            errMsg += "addr type is invalid. ";
                        }
                    }
                }
                // end of check when delivery method = COURIER
                
                // check addr to profile
                if("".equals(addrToProfile)) {
                    isValid = false;
                    errMsg += "addr to profile is empty. ";
                } else {
                    if(!"Y".equals(addrToProfile) && !"N".equals(addrToProfile)) {
                        isValid = false;
                        errMsg += "addr to profile is invalid. ";
                    }
                }
                // end of check addr to profile
            }
            // end of basic checking
            
            
            if(isValid) {
                // update location to tmp ticket
                reserveIdListMap = epcOrderHandler.getReserveIdsByItemId(conn, orderId); // get reserve id from epc tables, kerrytsang, 20210802
                
                for(EpcDeliveryDetail d : detailList) {
                    deliveryMethod = StringHelper.trim(d.getDeliveryMethod());
                    pickupStore = StringHelper.trim(d.getPickupStore());
                    itemIdList = d.getItems(); // cpq itemId(s)
logger.info("{}{}{}", logStr, "item size:", itemIdList.size());

    //                    updateChannel = "WEB";

                    if( EpcLoginChannel.ONLINE.equals(loginChannel) ) {
                        createChannel = "W";
                    } else if( EpcLoginChannel.STORE.equals(loginChannel) ) {
                        createChannel = "S";
                    } else if( EpcLoginChannel.DS.equals(loginChannel) ) {
                        createChannel = "W";
                    } else if( EpcLoginChannel.PR.equals(loginChannel) ) {
                        createChannel = "C";
                    } else if( EpcLoginChannel.TS.equals(loginChannel) ) {
                        createChannel = "C";
                    } else if( EpcLoginChannel.CS.equals(loginChannel) ) {
                        createChannel = "C";
                    } else {
                        createChannel = "";
                    }

                    for(String itemId : itemIdList) {
                        tmpReserveId = epcSecurityHelper.validateId(StringHelper.trim(reserveIdListMap.get(itemId))); // for AH & AA only, AP doesn't have reserve ticket

                        tmpLogStr = "itemId:" + itemId + ",reserveId:" + tmpReserveId + ",loginChannel:" + loginChannel + ",createChannel:" + createChannel + ",pickupStore:" + pickupStore;
logger.info("{}{}", logStr, tmpLogStr);

                        if(StringUtils.isNotBlank(tmpReserveId) && !"NO_STOCK".equals(tmpReserveId)) {
    //                    		epcUpdateLocationToTmpReserve = epcStockHandler.updateLocationToTmpReserve(updateChannel, tmpReserveId, pickupStore);
                            epcUpdateLocationToTmpReserve = epcStockHandler.updateLocationToEpcTmpReserve(loginChannel, tmpReserveId, pickupStore, createChannel);

                            tmpLogStr = " epcUpdateLocationToTmpReserve result:" + epcUpdateLocationToTmpReserve.getResult() + ",errMsg:" + epcUpdateLocationToTmpReserve.getErrMsg() + ",pickupDate:" + epcUpdateLocationToTmpReserve.getPickupDate();
logger.info("{}{}", logStr, tmpLogStr);

                            if(!"SUCCESS".equals(epcUpdateLocationToTmpReserve.getResult())) {
                                throw new Exception("cannot update location " + pickupStore + " to tmp ticket " + tmpReserveId + ", err:" + epcUpdateLocationToTmpReserve.getErrMsg());
                            } else {
                                // update pickup date to epc table
                                pstmtUpdatePickupDate.setString(1, epcUpdateLocationToTmpReserve.getPickupDate()); // pickup_date
                                pstmtUpdatePickupDate.setInt(2, orderId); // order_id
                                pstmtUpdatePickupDate.setString(3, itemId); // item_id
                                pstmtUpdatePickupDate.setString(4, tmpReserveId); // reserve_id
                                updateCnt = pstmtUpdatePickupDate.executeUpdate();
                                if(updateCnt != 1) {
                                    throw new Exception("cannot update pickup date to epc table, item_id:" + itemId + ",reserve_id:" + tmpReserveId);
                                }
                            }
                        } else {
                            tmpLogStr = "no need to proceed itemId " + itemId + ",reserveId:" + tmpReserveId;
logger.info("{}{}", logStr, tmpLogStr);
                        }
                    }
                }
                // end of update location to tmp ticket
                
                
                // obsolete previous record(s) - epc_order_delivery
                sql = "update epc_order_delivery " +
                        "   set status = ? " +
                        " where order_id = ? " + 
                        "   and status = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "O"); // status
                pstmt.setInt(2, orderId); // order_id
                pstmt.setString(3, "A"); // status - previous
                pstmt.executeUpdate();
                // end of obsolete previous record(s) - epc_order_delivery


                // insert delivery table
                sql = "select epc_order_id_seq.nextval from dual ";
                pstmtId = conn.prepareStatement(sql);
                
                sql = "insert into epc_order_delivery ( " +
                        "  delivery_id, order_id, delivery_method, pickup_location, deliver_contact_person, " + 
                        "  deliver_contact_no, deliver_addr_1, deliver_addr_2, deliver_district, deliver_area, " + 
                        "  deliver_addr_1_chi, deliver_addr_2_chi, deliver_district_chi, deliver_area_chi, courier_form_num, " + 
                        "  courier_company, status, create_user, create_date, addr_type, " +
                        "  addr_to_profile " +
                        ") values ( " +
                        "  ?,?,?,?,?, " +
                        "  ?,?,?,?,?, " +
                        "  ?,?,?,?,?, " +
                        "  ?,?,?,sysdate,?, " +
                        "  ? " +
                        ") ";
                pstmt = conn.prepareStatement(sql);
                
                for(EpcDeliveryDetail d : detailList) {
                    deliveryMethod = StringHelper.trim(d.getDeliveryMethod());
                    pickupStore = StringHelper.trim(d.getPickupStore());
                    deliveryAddress1 = StringHelper.trim(d.getDeliveryAddress1());
                    deliveryAddress2 = StringHelper.trim(d.getDeliveryAddress2());
                    deliveryAddress3 = StringHelper.trim(d.getDeliveryAddress3());
                    deliveryAddress4 = StringHelper.trim(d.getDeliveryAddress4());
                    deliveryContactPerson = StringHelper.trim(d.getDeliveryContactPerson());
                    deliveryContactNo = StringHelper.trim(d.getDeliveryContactNo());
                    itemIdList = d.getItems(); // cpq itemId(s)
                    
                    rsetId = pstmtId.executeQuery();
                    if(rsetId.next()) {
                        deliveryId = rsetId.getInt(1);
                    } rsetId.close(); rsetId = null;
                    
                    pstmt.setInt(1, deliveryId); // delivery_id
                    pstmt.setInt(2, orderId); // order_id
                    pstmt.setString(3, deliveryMethod); // delivery_method
                    pstmt.setString(4, pickupStore); // pickup_location
                    pstmt.setString(5, deliveryContactPerson); // deliver_contact_person
                    pstmt.setString(6, deliveryContactNo); // deliver_contact_no
                    pstmt.setString(7, deliveryAddress1); // deliver_addr_1
                    pstmt.setString(8, deliveryAddress2); // deliver_addr_2
                    pstmt.setString(9, deliveryAddress3); // deliver_addr_3
                    pstmt.setString(10, deliveryAddress4); // deliver_addr_4
                    pstmt.setString(11, deliveryAddress1); // deliver_addr_1_chi
                    pstmt.setString(12, deliveryAddress2); // deliver_addr_2_chi
                    pstmt.setString(13, deliveryAddress3); // deliver_district_chi
                    pstmt.setString(14, deliveryAddress4); // deliver_area_chi
                    pstmt.setString(15, ""); // courier_form_num
                    pstmt.setString(16, ""); // courier_company
                    pstmt.setString(17, "A"); // status
                    pstmt.setString(18, ""); // create_user
                    pstmt.setString(19, addrType); // addr_type
                    pstmt.setString(20, addrToProfile); // addr_to_profile
                    
                    pstmt.addBatch();
                    
                    // mark item with this delivery id
                    for(String itemId : itemIdList) {
                        pstmtUpdateDeliveryId.setInt(1, deliveryId); // delivery_id
                        pstmtUpdateDeliveryId.setInt(2, orderId); // order_id
                        pstmtUpdateDeliveryId.setString(3, itemId); // item_id
                        updateCnt = pstmtUpdateDeliveryId.executeUpdate();
                        if(updateCnt != 1) {
                            throw new Exception("cannot update delivery id to epc table, item_id:" + itemId);
                        }
                    }
                    // end of mark item with this delivery id
                }
                pstmt.executeBatch();
                // end of insert delivery table

                
                conn.commit();
                
                epcSaveDeliveryInfoResult.setSaveStatus("SUCCESS");
            } else {
                epcSaveDeliveryInfoResult.setSaveStatus("FAIL");
                epcSaveDeliveryInfoResult.setErrorCode("1001");
                epcSaveDeliveryInfoResult.setErrorMessage(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcSaveDeliveryInfoResult.setSaveStatus("FAIL");
            epcSaveDeliveryInfoResult.setErrorCode("1002");
            epcSaveDeliveryInfoResult.setErrorMessage(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcSaveDeliveryInfoResult;
    }

    public EpcUpdateCourier updateCourierInfo(EpcUpdateCourier epcUpdateCourier) {
        int orderId = 0;
        String deliveryDate = "";
        String deliveryPeriod = "";
        String courierCompany = "";
        String courierFormNum = "";
        String user = "";
        boolean isValid = true;
        String errMsg = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String logStr = "[updateCourierInfo]";
        
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            orderId = epcUpdateCourier.getOrderId();
            deliveryDate = epcUpdateCourier.getDeliveryDate();
            deliveryPeriod = epcUpdateCourier.getDeliveryPeriod();
            courierCompany = epcUpdateCourier.getCourierCompany();
            courierFormNum = epcUpdateCourier.getCourierFormNum();
            user = epcUpdateCourier.getUser();

            logStr += "[orderId:" + orderId + "] ";
            logStr += "[deliveryDate:" + deliveryDate + "] ";
            logStr += "[deliveryPeriod:" + deliveryPeriod + "] ";
            logStr += "[courierCompany:" + courierCompany + "] ";
            logStr += "[courierFormNum:" + courierFormNum + "] ";
            logStr += "[user:" + user + "] ";
            logger.info("{}", logStr);

            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }
            // end of basic checking
            
            if(isValid) {
                sql = "update epc_order_delivery " +
                        "   set delivery_date = ?, " +
                		" delivery_period = ?, courier_company = ?, courier_form_num = ?, " +
                		" modify_date = sysdate, modify_user = ? " +
                        " where order_id = ? " + 
                        "   and delivery_method = 'COURIER' " +
                        "   and status = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, deliveryDate); // delivery date
                pstmt.setString(2, deliveryPeriod); // delivery period
                pstmt.setString(3, courierCompany); // courier company
                pstmt.setString(4, courierFormNum); // courier form num
                pstmt.setString(5, user); // modify user
                pstmt.setInt(6, orderId); // order_id
                pstmt.setString(7, "A"); // status
                pstmt.executeUpdate();
                epcUpdateCourier.setResult("SUCCESS");
            } else {
            	epcUpdateCourier.setResult("FAIL");
            	epcUpdateCourier.setErrMsg(errMsg);
            }
            // end of update delivery table            
            conn.commit();
            
        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            epcUpdateCourier.setResult("FAIL");
            epcUpdateCourier.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }
        return epcUpdateCourier;
    }

}

