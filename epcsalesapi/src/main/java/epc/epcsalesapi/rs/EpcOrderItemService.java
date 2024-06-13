package epc.epcsalesapi.rs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcAddQuoteItemHandler;
import epc.epcsalesapi.sales.EpcDeleteQuoteItemHandler;
import epc.epcsalesapi.sales.EpcExtendReserveTicketHandler;
import epc.epcsalesapi.sales.EpcFulfillHandler;
import epc.epcsalesapi.sales.EpcMoveQuoteItemHandler;
import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.EpcQuoteHandler;
import epc.epcsalesapi.sales.EpcUpdateQuoteItemHandler;
import epc.epcsalesapi.sales.bean.EpcAddSigmaItemToQuote;
import epc.epcsalesapi.sales.bean.EpcCreateSerial;
import epc.epcsalesapi.sales.bean.EpcCreateSerialResult;
import epc.epcsalesapi.sales.bean.EpcDeleteSigmaItemFromQuote;
import epc.epcsalesapi.sales.bean.EpcEvaluateQuoteItem;
import epc.epcsalesapi.sales.bean.EpcExtendAllReserveTmpTicket;
import epc.epcsalesapi.sales.bean.EpcMoveQuoteItemToOtherQuote;
import epc.epcsalesapi.sales.bean.EpcOrderItemDetail;
import epc.epcsalesapi.sales.bean.EpcReserveStock;
import epc.epcsalesapi.sales.bean.EpcStockReady;
import epc.epcsalesapi.sales.bean.EpcUpdateQuoteItem;
import epc.epcsalesapi.sales.bean.Fulfillment;
import epc.epcsalesapi.stock.EpcRbdReserveStockHandler;
import epc.epcsalesapi.stock.EpcReserveStockHandler;
import epc.epcsalesapi.stock.EpcStockReadyHandler;


@RestController
@RequestMapping("/salesOrder/items")
public class EpcOrderItemService {
    private final Logger logger = LoggerFactory.getLogger(EpcOrderItemService.class);

    private EpcDeleteQuoteItemHandler epcDeleteQuoteItemHandler;
    private EpcOrderHandler epcOrderHandler;
    private EpcQuoteHandler epcQuoteHandler;
    private EpcStockReadyHandler epcStockReadyHandler;
    private EpcMoveQuoteItemHandler epcMoveQuoteItemHandler;
    private EpcFulfillHandler epcFulfillHandler;
    private EpcAddQuoteItemHandler epcAddQuoteItemHandler;
    private EpcUpdateQuoteItemHandler epcUpdateQuoteItemHandler;
    private EpcExtendReserveTicketHandler epcExtendReserveTicketHandler;
    private EpcReserveStockHandler epcReserveStockHandler;
    private EpcRbdReserveStockHandler epcRbdReserveStockHandler;

    public EpcOrderItemService(
        EpcDeleteQuoteItemHandler epcDeleteQuoteItemHandler, EpcOrderHandler epcOrderHandler,
        EpcQuoteHandler epcQuoteHandler, EpcStockReadyHandler epcStockReadyHandler,
        EpcMoveQuoteItemHandler epcMoveQuoteItemHandler, EpcFulfillHandler epcFulfillHandler,
        EpcAddQuoteItemHandler epcAddQuoteItemHandler, EpcUpdateQuoteItemHandler epcUpdateQuoteItemHandler,
        EpcExtendReserveTicketHandler epcExtendReserveTicketHandler, EpcReserveStockHandler epcReserveStockHandler,
        EpcRbdReserveStockHandler epcRbdReserveStockHandler
    ) {
        this.epcDeleteQuoteItemHandler = epcDeleteQuoteItemHandler;
        this.epcOrderHandler = epcOrderHandler;
        this.epcQuoteHandler = epcQuoteHandler;
        this.epcStockReadyHandler = epcStockReadyHandler;
        this.epcMoveQuoteItemHandler = epcMoveQuoteItemHandler;
        this.epcFulfillHandler = epcFulfillHandler;
        this.epcAddQuoteItemHandler = epcAddQuoteItemHandler;
        this.epcUpdateQuoteItemHandler = epcUpdateQuoteItemHandler;
        this.epcExtendReserveTicketHandler = epcExtendReserveTicketHandler;
        this.epcReserveStockHandler = epcReserveStockHandler;
        this.epcRbdReserveStockHandler = epcRbdReserveStockHandler;
    }


    @GetMapping(value = "/allDeviceItems", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ArrayList<EpcOrderItemDetail>> getAllDeviceItems(
        @RequestParam("orderId") int orderId,
        @RequestParam(name = "retrieveType", required = false) String retrieveType
    ) {
        ArrayList<EpcOrderItemDetail> detailList = epcOrderHandler.getAllDeviceItems(orderId, retrieveType);
        return new ResponseEntity<ArrayList<EpcOrderItemDetail>>(detailList, HttpStatus.OK);
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcAddSigmaItemToQuote> addSigmaItemToQuote(@RequestBody EpcAddSigmaItemToQuote epcAddSigmaItemToQuote) {
        epcAddQuoteItemHandler.addSigmaItemToQuote(epcAddSigmaItemToQuote);

        if ("SUCCESS".equals(epcAddSigmaItemToQuote.getResult())) {
            return new ResponseEntity<EpcAddSigmaItemToQuote>(epcAddSigmaItemToQuote, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcAddSigmaItemToQuote>(epcAddSigmaItemToQuote, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcUpdateQuoteItem> configSigmaItem(@RequestBody EpcUpdateQuoteItem epcUpdateQuoteItem) {
        epcUpdateQuoteItemHandler.updateProductToQuote(epcUpdateQuoteItem);

        if ("SUCCESS".equals(epcUpdateQuoteItem.getResult())) {
            return new ResponseEntity<EpcUpdateQuoteItem>(epcUpdateQuoteItem, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcUpdateQuoteItem>(epcUpdateQuoteItem, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcDeleteSigmaItemFromQuote> deleteQuoteItem(@RequestBody EpcDeleteSigmaItemFromQuote epcDeleteSigmaItemFromQuote) {
        epcDeleteQuoteItemHandler.deleteSigmaItemFromQuote(epcDeleteSigmaItemFromQuote);
        if ("SUCCESS".equals(epcDeleteSigmaItemFromQuote.getResult())) {
            return new ResponseEntity<EpcDeleteSigmaItemFromQuote>(epcDeleteSigmaItemFromQuote, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcDeleteSigmaItemFromQuote>(epcDeleteSigmaItemFromQuote, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/evaluate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcEvaluateQuoteItem> evaluateProduct(@RequestBody EpcEvaluateQuoteItem epcEvaluateQuoteItem) {
        EpcEvaluateQuoteItem epcEvaluateQuoteItemResult = epcQuoteHandler.evaluateQuoteItem(epcEvaluateQuoteItem);

        if ("SUCCESS".equals(epcEvaluateQuoteItemResult.getResult())) {
            return new ResponseEntity<>(epcEvaluateQuoteItemResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcEvaluateQuoteItemResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/reserve", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcReserveStock> reserveStock(@RequestBody EpcReserveStock epcReserveStock) {
        EpcReserveStock epcReserveStock2 = epcReserveStockHandler.reserveStock(epcReserveStock);

        if ("SUCCESS".equals(epcReserveStock2.getSaveStatus())) {
            return new ResponseEntity<EpcReserveStock>(epcReserveStock2, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcReserveStock>(epcReserveStock2, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @PostMapping(value = "/reserveRbd", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public EpcReserveStock reserveRbdStock(@RequestBody EpcReserveStock epcReserveStock) {
//        return epcRbdReserveStockHandler.reserveStock(epcReserveStock);
//    }


    @PostMapping(value = "/extendAllTmpTicket", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcExtendAllReserveTmpTicket> extendAllTmpTicket(@RequestBody EpcExtendAllReserveTmpTicket epcExtendAllReserveTmpTicket) {
        epcExtendReserveTicketHandler.extendAllReserveTmpTicket(epcExtendAllReserveTmpTicket);

        if ("SUCCESS".equals(epcExtendAllReserveTmpTicket.getResult())) {
            return new ResponseEntity<EpcExtendAllReserveTmpTicket>(epcExtendAllReserveTmpTicket, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcExtendAllReserveTmpTicket>(epcExtendAllReserveTmpTicket, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/regenerateTmpTicket", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcExtendAllReserveTmpTicket regenerateTmpTicket(@RequestBody EpcExtendAllReserveTmpTicket epcExtendAllReserveTmpTicket) {
        epcReserveStockHandler.regenerateTmpTicket(epcExtendAllReserveTmpTicket);
        return epcExtendAllReserveTmpTicket;
    }


    @PostMapping(value = "/ready", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcStockReady> stockReady(@RequestBody EpcStockReady epcStockReady) {
        epcStockReadyHandler.stockReady(epcStockReady);

        if ("SUCCESS".equals(epcStockReady.getResult())) {
            return new ResponseEntity<EpcStockReady>(epcStockReady, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcStockReady>(epcStockReady, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /***
     * move quote item from 1 quote to another quote
     * i.e. quote item in tmp quote -> shopping bag quote
     * quote item in shopping bag quote -> checkout quote
     * 
     * @param epcMoveQuoteItemToOtherQuote
     * @return
     */
    @PostMapping(value = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcMoveQuoteItemToOtherQuote> moveQuoteItemToOtherQuote(@RequestBody EpcMoveQuoteItemToOtherQuote epcMoveQuoteItemToOtherQuote) {
        epcMoveQuoteItemHandler.moveQuoteItemToOtherQuote(epcMoveQuoteItemToOtherQuote);

        if ("SUCCESS".equals(epcMoveQuoteItemToOtherQuote.getResult())) {
            return new ResponseEntity<>(epcMoveQuoteItemToOtherQuote, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcMoveQuoteItemToOtherQuote, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/serialNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcCreateSerialResult> saveItemSerialNo(@RequestBody EpcCreateSerial epcCreateSerial) {
        EpcCreateSerialResult epcCreateSerialResult = epcFulfillHandler.saveItemSerialNo(epcCreateSerial);

        if ("OK".equals(epcCreateSerialResult.getSaveStatus())) {
            return new ResponseEntity<EpcCreateSerialResult>(epcCreateSerialResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCreateSerialResult>(epcCreateSerialResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/fulfillment")
    @ResponseBody
    public ResponseEntity<?> fulfillment(Integer orderId,String itemId) {
        try {
            return new ResponseEntity<List<Fulfillment>>(epcFulfillHandler.getFulfillment(orderId,itemId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("", e);
            return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
