/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.sales.bean.EpcDailySalesFigures;
import epc.epcsalesapi.sales.bean.EpcDailySalesFiguresItem;
import epc.epcsalesapi.sales.bean.EpcDailySalesFiguresSalesman;
import epc.epcsalesapi.sales.bean.EpcDailySalesFiguresSummary;
import epc.epcsalesapi.sales.bean.EpcGetDailySalesFigures;
import epc.epcsalesapi.sales.bean.EpcGetDailySalesFiguresResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author DannyChan
 */
@Service
public class EpcDailySalesFiguresHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcDailySalesFiguresHandler.class);
    
    @Autowired
    private DataSource epcDataSource;

    // for testing only: functions for gnerating testing data (added on 2021-3-1): start
    public ArrayList<String> getTestingDataSalesmen() throws Exception {
        ArrayList<String> result = new ArrayList<String>();

        result.add("Tyler Cassin");
        result.add("Katherine Schumm");
        result.add("Wilfred Lunch");
        result.add("Leonardo Hilpert");
        result.add("Kennith Pasisian");
        result.add("Jaquan Zemiak");
        result.add("Barney Bernhard");
        result.add("Kerry Tsang");
        result.add("Danny Chan");
        result.add("Ted Kwan");    
        
        return result;
    }

    
    public ArrayList<EpcDailySalesFiguresItem> getTestingDataAccessories(String salesman) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();

        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item5 = new EpcDailySalesFiguresItem();

        switch (salesman) {
            case "Tyler Cassin":     
                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Apple AirPods with Charging Case");
                item2.setQty(2);
                result.add(item2);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("EVA 2020 X final TWS Earphones");
                item4.setQty(2);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
            case "Katherine Schumm": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("KARL LAGERFELD Karl PU Leather Case");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Apple AirPods with Charging Case");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Verbatim Tough Max Lightning");
                item3.setQty(2);
                result.add(item3);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(2);
                result.add(item5);
                
                                     break;

            case "Wilfred Lunch":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("KARL LAGERFELD Karl PU Leather Case");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Apple AirPods with Charging Case");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Verbatim Tough Max Lightning");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("EVA 2020 X final TWS Earphones");
                item4.setQty(5);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(2);
                result.add(item5);
                
                                     break;

            case "Leonardo Hilpert": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("KARL LAGERFELD Karl PU Leather Case");
                item1.setQty(1);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Verbatim Tough Max Lightning");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("EVA 2020 X final TWS Earphones");
                item4.setQty(4);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(1);
                result.add(item5);
                
                                     break;                   
                     
            case "Kennith Pasisian": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("KARL LAGERFELD Karl PU Leather Case");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Apple AirPods with Charging Case");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Verbatim Tough Max Lightning");
                item3.setQty(4);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("EVA 2020 X final TWS Earphones");
                item4.setQty(1);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(4);
                result.add(item5);
                
                                     break;                   

            case "Jaquan Zemiak":    
                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Apple AirPods with Charging Case");
                item2.setQty(2);
                result.add(item2);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("EVA 2020 X final TWS Earphones");
                item4.setQty(2);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
                     
            case "Barney Bernhard":  
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("KARL LAGERFELD Karl PU Leather Case");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Apple AirPods with Charging Case");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Verbatim Tough Max Lightning");
                item3.setQty(2);
                result.add(item3);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
                     
            case "Kerry Tsang": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("KARL LAGERFELD Karl PU Leather Case");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Apple AirPods with Charging Case");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Verbatim Tough Max Lightning");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("EVA 2020 X final TWS Earphones");
                item4.setQty(5);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
                     
            case "Danny Chan": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("KARL LAGERFELD Karl PU Leather Case");
                item1.setQty(1);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Verbatim Tough Max Lightning");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("EVA 2020 X final TWS Earphones");
                item4.setQty(4);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(1);
                result.add(item5);
                
                                     break;
                     
            case "Ted Kwan": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("KARL LAGERFELD Karl PU Leather Case");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Apple AirPods with Charging Case");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Verbatim Tough Max Lightning");
                item3.setQty(4);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("EVA 2020 X final TWS Earphones");
                item4.setQty(1);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
                item5.setQty(4);
                result.add(item5);
        }
        
        return result;
    }

    public ArrayList<EpcDailySalesFiguresItem> getTestingDataHandsets(String salesman) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item5 = new EpcDailySalesFiguresItem();

        switch (salesman) {
            case "Tyler Cassin":     
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("iPhone 11 Max Pro 128GB Gold");
                item2.setQty(2);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Sony Xperial 1");
                item4.setQty(2);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(2);
                result.add(item5);                
                                     break;
            case "Katherine Schumm": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(2);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(2);
                result.add(item3);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(2);
                result.add(item5);                
                
                                     break;

            case "Wilfred Lunch":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("iPhone 11 Max Pro 128GB Gold");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Sony Xperial 1");
                item4.setQty(5);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(2);
                result.add(item5);                
                
                                     break;

            case "Leonardo Hilpert": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("iPhone 11 Max Pro 128GB Gold");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Sony Xperial 1");
                item4.setQty(4);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(1);
                result.add(item5);                
                
                                     break;                   
                     
            case "Kennith Pasisian": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("iPhone 11 Max Pro 128GB Gold");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(4);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Sony Xperial 1");
                item4.setQty(1);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(4);
                result.add(item5);                
                
                                     break;                   

            case "Jaquan Zemiak":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("iPhone 11 Max Pro 128GB Gold");
                item2.setQty(2);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Sony Xperial 1");
                item4.setQty(2);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(2);
                result.add(item5);                
                
                                     break;
                     
            case "Barney Bernhard":  
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(2);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(2);
                result.add(item3);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(2);
                result.add(item5);                
                
                                     break;
                     
            case "Kerry Tsang": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("iPhone 11 Max Pro 128GB Gold");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Sony Xperial 1");
                item4.setQty(5);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(2);
                result.add(item5);                
                
                                     break;
                     
            case "Danny Chan": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("iPhone 11 Max Pro 128GB Gold");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Sony Xperial 1");
                item4.setQty(4);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(1);
                result.add(item5);                
                
                                     break;
                     
            case "Ted Kwan": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("iPhone 11 Max Pro 128GB White");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("iPhone 11 Max Pro 128GB Gold");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Samsung Galaxy 8 256GB");
                item3.setQty(4);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Sony Xperial 1");
                item4.setQty(1);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("HTC U19e");
                item5.setQty(4);
                result.add(item5);                
                
        }
        
        return result;
    }

    public ArrayList<EpcDailySalesFiguresItem> getTestingDataVAS(String salesman) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();

        switch (salesman) {
            case "Tyler Cassin":     
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(2);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(2);
                result.add(item4);
                
                                     break;
            case "Katherine Schumm": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(2);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(2);
                result.add(item3);

                                     break;

            case "Wilfred Lunch":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(5);
                result.add(item4);
                
                                     break;

            case "Leonardo Hilpert": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(4);
                result.add(item4);
                
                                     break;                   
                     
            case "Kennith Pasisian": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(4);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(1);
                result.add(item4);
                
                                     break;                   

            case "Jaquan Zemiak":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(2);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(2);
                result.add(item4);
                
                                     break;
                     
            case "Barney Bernhard":  
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(2);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(2);
                result.add(item3);

                                     break;
                     
            case "Kerry Tsang": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(5);
                result.add(item4);
                
                                     break;
                     
            case "Danny Chan":
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(4);
                result.add(item4);
                
                                     break;
                     
            case "Ted Kwan": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Apple Music");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Online Exclusive");
                item3.setQty(4);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(1);
                result.add(item4);
        }
        
        return result;
    }

    public ArrayList<EpcDailySalesFiguresItem> getTestingDataPlans(String salesman) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item5 = new EpcDailySalesFiguresItem();

        switch (salesman) {
            case "Tyler Cassin":     
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("SuperCare Plan 8GB");
                item2.setQty(2);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("SuperCare Plan 4GB");
                item4.setQty(2);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
            case "Katherine Schumm": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(2);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(2);
                result.add(item3);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(2);
                result.add(item5);
                
                                     break;

            case "Wilfred Lunch":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("SuperCare Plan 8GB");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("SuperCare Plan 4GB");
                item4.setQty(5);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(2);
                result.add(item5);
                
                                     break;

            case "Leonardo Hilpert": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("SuperCare Plan 8GB");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("SuperCare Plan 4GB");
                item4.setQty(4);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(1);
                result.add(item5);
                
                                     break;                   
                     
            case "Kennith Pasisian": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("SuperCare Plan 8GB");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(4);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("SuperCare Plan 4GB");
                item4.setQty(1);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(4);
                result.add(item5);
                
                                     break;                   

            case "Jaquan Zemiak":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("SuperCare Plan 8GB");
                item2.setQty(2);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("SuperCare Plan 4GB");
                item4.setQty(2);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
                     
            case "Barney Bernhard":  
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(2);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(2);
                result.add(item3);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
                     
            case "Kerry Tsang": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("SuperCare Plan 8GB");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("SuperCare Plan 4GB");
                item4.setQty(5);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
                     
            case "Danny Chan":
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("SuperCare Plan 8GB");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("SuperCare Plan 4GB");
                item4.setQty(4);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(1);
                result.add(item5);
                
                                     break;
                     
            case "Ted Kwan": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("SuperCare Plan 6GB");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("SuperCare Plan 8GB");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("ExtraCare Plan 8GB");
                item3.setQty(4);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("SuperCare Plan 4GB");
                item4.setQty(1);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("ExtraCare Plan 4GB");
                item5.setQty(4);
                result.add(item5);
                
        }
        
        return result;
    }

    public ArrayList<EpcDailySalesFiguresItem> getTestingDataOffers(String salesman) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item5 = new EpcDailySalesFiguresItem();

        switch (salesman) {
            case "Tyler Cassin":     
                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(2);
                result.add(item2);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(2);
                result.add(item4);

                                     break;
            case "Katherine Schumm": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Disney Summer");
                item1.setQty(5);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Onlne Exclusive");
                item3.setQty(5);
                result.add(item3);
                
                item5 = new EpcDailySalesFiguresItem();
                item5.setName("Mother's Day Special");
                item5.setQty(5);
                result.add(item5);
                
                                     break;

            case "Wilfred Lunch":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Disney Summer");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Onlne Exclusive");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(5);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("Mother's Day Special");
                item5.setQty(2);
                result.add(item5);
                
                                     break;

            case "Leonardo Hilpert": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Disney Summer");
                item1.setQty(5);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Onlne Exclusive");
                item3.setQty(5);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(4);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("Mother's Day Special");
                item5.setQty(5);
                result.add(item5);
                
                                     break;                   
                     
            case "Kennith Pasisian": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Disney Summer");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Onlne Exclusive");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(1);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("Mother's Day Special");
                item5.setQty(1);
                result.add(item5);
                
                                     break;                   

            case "Jaquan Zemiak":    
                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(2);
                result.add(item2);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(2);
                result.add(item4);
                                     break;
                     
            case "Barney Bernhard":  
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Disney Summer");
                item1.setQty(5);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Onlne Exclusive");
                item3.setQty(5);
                result.add(item3);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("Mother's Day Special");
                item5.setQty(5);
                result.add(item5);
                
                                     break;
                     
            case "Kerry Tsang": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Disney Summer");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Onlne Exclusive");
                item3.setQty(2);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(5);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("Mother's Day Special");
                item5.setQty(2);
                result.add(item5);
                
                                     break;
                     
            case "Danny Chan":
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Disney Summer");
                item1.setQty(5);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Onlne Exclusive");
                item3.setQty(5);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(4);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("Mother's Day Special");
                item5.setQty(5);
                result.add(item5);
                
                                     break;
                     
            case "Ted Kwan": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("Disney Summer");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Chillax Music & Movie");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Onlne Exclusive");
                item3.setQty(1);
                result.add(item3);

                item4 = new EpcDailySalesFiguresItem();
                item4.setName("Student Hang Seng Credit Card");
                item4.setQty(1);
                result.add(item4);

                item5 = new EpcDailySalesFiguresItem();
                item5.setName("Mother's Day Special");
                item5.setQty(1);
                result.add(item5);
                
        }        
        
        return result;
    }
    
    public ArrayList<EpcDailySalesFiguresItem> getTestingDataActivationTypes(String salesman) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();

        switch (salesman) {
            case "Tyler Cassin":     
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Port-in Acquisition");
                item2.setQty(2);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(2);
                result.add(item3);
                
                                     break;
            case "Katherine Schumm": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(2);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(2);
                result.add(item3);
                
                                     break;

            case "Wilfred Lunch":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Port-in Acquisition");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(2);
                result.add(item3);
                
                                     break;

            case "Leonardo Hilpert": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Port-in Acquisition");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(1);
                result.add(item3);
                
                                     break;                   
                     
            case "Kennith Pasisian": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Port-in Acquisition");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(4);
                result.add(item3);
                
                                     break;                   

            case "Jaquan Zemiak":    
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Port-in Acquisition");
                item2.setQty(2);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(2);
                result.add(item3);
                
                                     break;
                     
            case "Barney Bernhard": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(2);
                result.add(item1);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(2);
                result.add(item3);
                
                                     break;
                     
            case "Kerry Tsang": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(2);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Port-in Acquisition");
                item2.setQty(5);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(2);
                result.add(item3);
                
                                     break;
                     
            case "Danny Chan":
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(1);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Port-in Acquisition");
                item2.setQty(4);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(1);
                result.add(item3);
                
                                     break;
                     
            case "Ted Kwan": 
                item1 = new EpcDailySalesFiguresItem();
                item1.setName("New Line Acquisition");
                item1.setQty(4);
                result.add(item1);

                item2 = new EpcDailySalesFiguresItem();
                item2.setName("Port-in Acquisition");
                item2.setQty(1);
                result.add(item2);

                item3 = new EpcDailySalesFiguresItem();
                item3.setName("Retention");
                item3.setQty(4);
                result.add(item3);
                
        }
        
        return result;
    }
    
    
    public ArrayList<EpcDailySalesFiguresItem> getTestingDataAccessoriesTotal() throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        item1.setName("KARL LAGERFELD Karl PU Leather Case");
        item1.setQty(18);
        result.add(item1);
        
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        item2.setName("Apple AirPods with Charging Case");
        item2.setQty(24);
        result.add(item2);
        
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        item3.setName("Verbatim Tough Max Lightning");
        item3.setQty(18);
        result.add(item3);
        
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        item4.setName("EVA 2020 X final TWS Earphones");
        item4.setQty(23);
        result.add(item4);
        
        EpcDailySalesFiguresItem item5 = new EpcDailySalesFiguresItem();
        item5.setName("JITIAN Glassesc Anti-Blue Light Eye");
        item5.setQty(22);
        result.add(item5);
        
        return result;
    }

    public ArrayList<EpcDailySalesFiguresItem> getTestingDataHandsetsTotal() throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        item1.setName("iPhone 11 Max Pro 128GB White");
        item1.setQty(22);
        result.add(item1);
        
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        item2.setName("iPhone 11 Max Pro 128GB Gold");
        item2.setQty(24);
        result.add(item2);
        
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        item3.setName("Samsung Galaxy 8 256GB");
        item3.setQty(22);
        result.add(item3);
        
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        item4.setName("Sony Xperial 1");
        item4.setQty(24);
        result.add(item4);
        
        EpcDailySalesFiguresItem item5 = new EpcDailySalesFiguresItem();
        item5.setName("HTC U19e");
        item5.setQty(22);
        result.add(item5);
        
        return result;
    }    
    
    public ArrayList<EpcDailySalesFiguresItem> getTestingDataVASTotal() throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        item1.setName("Apple Music");
        item1.setQty(21);
        result.add(item1);
        
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        item2.setName("Chillax Music & Movie");
        item2.setQty(24);
        result.add(item2);
        
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        item3.setName("Online Exclusive");
        item3.setQty(22);
        result.add(item3);
        
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        item4.setName("Student Hang Seng Credit Card");
        item4.setQty(24);
        result.add(item4);
        
        return result;
    }        
    
    public ArrayList<EpcDailySalesFiguresItem> getTestingDataPlansTotal() throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        item1.setName("SuperCare Plan 6GB");
        item1.setQty(22);
        result.add(item1);
        
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        item2.setName("SuperCare Plan 8GB");
        item2.setQty(24);
        result.add(item2);
        
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        item3.setName("ExtraCare Plan 8GB");
        item3.setQty(22);
        result.add(item3);
        
        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        item4.setName("SuperCare Plan 4GB");
        item4.setQty(24);
        result.add(item4);

        EpcDailySalesFiguresItem item5 = new EpcDailySalesFiguresItem();
        item5.setName("ExtraCare Plan 4GB");
        item5.setQty(22);
        result.add(item5);        
        
        return result;
    }            
    
    public ArrayList<EpcDailySalesFiguresItem> getTestingDataActivationTypesTotal() throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        item1.setName("New Line Acquisition");
        item1.setQty(22);
        result.add(item1);
        
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        item2.setName("Port-in Acquisition");
        item2.setQty(24);
        result.add(item2);
        
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        item3.setName("Retention");
        item3.setQty(22);
        result.add(item3);
        
        return result;
    } 

    public ArrayList<EpcDailySalesFiguresItem> getTestingDataOffersTotal() throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        EpcDailySalesFiguresItem item1 = new EpcDailySalesFiguresItem();
        item1.setName("Disney Summer");
        item1.setQty(26);
        result.add(item1);
        
        EpcDailySalesFiguresItem item2 = new EpcDailySalesFiguresItem();
        item2.setName("Chillax Music & Movie");
        item2.setQty(24);
        result.add(item2);
        
        EpcDailySalesFiguresItem item3 = new EpcDailySalesFiguresItem();
        item3.setName("Onlne Exclusive");
        item3.setQty(26);
        result.add(item3);

        EpcDailySalesFiguresItem item4 = new EpcDailySalesFiguresItem();
        item4.setName("Student Hang Seng Credit Card");
        item4.setQty(23);
        result.add(item4);        

        EpcDailySalesFiguresItem item5 = new EpcDailySalesFiguresItem();
        item5.setName("Mother's Day Special");
        item5.setQty(26);
        result.add(item5);        
        
        return result;
    }                    
    // for testing only: functions for gnerating testing data (added on 2021-3-1): end    
    
    public ArrayList<EpcDailySalesFiguresItem> getDailySalesFiguresItemsTotal(String sql, String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, location);
            pstmt.setString(2, start_date);
            pstmt.setString(3, end_date);
            
            rs = pstmt.executeQuery();            
            
            while (rs.next()) {
                   EpcDailySalesFiguresItem item = new EpcDailySalesFiguresItem();
             
                   item.setName(rs.getString(2));
                   item.setQty(rs.getInt(1));
                   
                   result.add(item);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try { if(rs != null) { rs.close(); } } catch (Exception e) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
        }
        
        return result;        
    }    
    
    public ArrayList<EpcDailySalesFiguresItem> getDailySalesFiguresItems(String sql, String salesman, String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, salesman);
            pstmt.setString(2, location);
            pstmt.setString(3, start_date);
            pstmt.setString(4, end_date);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                   EpcDailySalesFiguresItem item = new EpcDailySalesFiguresItem();
             
                   item.setName(rs.getString(2));
                   item.setQty(rs.getInt(1));
                   
                   result.add(item);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try { if(rs != null) { rs.close(); } } catch (Exception e) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
        }
        
        return result;        
    }

    public ArrayList<EpcDailySalesFiguresItem> getAccessoriesTotal(String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and " + 
                     "item_cat = 'DEVICE' and warehouse = 'AA' and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' " + 
                     "and order_salesman is not null and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by cpq_item_desc";
        
        return getDailySalesFiguresItemsTotal(sql, location, start_date, end_date, conn);
    }
        
    public ArrayList<EpcDailySalesFiguresItem> getHandsetsTotal(String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and " + 
                     "item_cat = 'DEVICE' and warehouse = 'AH' and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' " + 
                     "and order_salesman is not null and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by cpq_item_desc";
        
        return getDailySalesFiguresItemsTotal(sql, location, start_date, end_date, conn);
    }
    
    public ArrayList<EpcDailySalesFiguresItem>getVASTotal(String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and " + 
                     //"item_cat is null and parent_item_id in ( select item_id from epc_order_item where cpq_item_desc = 'VAS Services' ) " + 
			         //"and order_status != 'I' and order_status != 'CA' and order_salesman is not null and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by cpq_item_desc";
			         "item_cat = 'VAS' and parent_item_id in ( select item_id from epc_order_item where cpq_item_desc = 'VAS Services' ) " + 
                     "and order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' and order_salesman is not null and place_order_location = ? " + 
                     "and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by cpq_item_desc";     // TBC, need to revise, return 0 records at this moment )
        
        return getDailySalesFiguresItemsTotal(sql, location, start_date, end_date, conn);
    }
    
    public ArrayList<EpcDailySalesFiguresItem> getActivationTypesTotal(String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), activation_type from epc_order a, epc_order_case b where a.order_id = b.order_id and " + 
                     //"order_status != 'I' and order_status != 'CA' and order_salesman is not null and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by activation_type";
                     "order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' and order_salesman is not null and place_order_location = ? " + 
                     "and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') and activation_type is not null group by activation_type";
        
        return getDailySalesFiguresItemsTotal(sql, location, start_date, end_date, conn);
    }
    
    public ArrayList<EpcDailySalesFiguresItem> getOffersTotal(String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and " + 
                     "parent_item_id is null and item_cat is null and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' " + 
                     "and order_salesman is not null and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') " + 
                     "and order_status!='LOCK' group by cpq_item_desc";
                     //"and cpq_item_desc like '%Offer%' group by cpq_item_desc";
        
        return getDailySalesFiguresItemsTotal(sql, location, start_date, end_date, conn);
    }    

    public ArrayList<EpcDailySalesFiguresItem> getPlansTotal(String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and " + 
                     //"parent_item_id is null and item_cat is null and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' " + 
                     "parent_item_id is null and item_cat = 'PLAN' and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' " + 
                     "and order_salesman is not null and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') " +
                     "and cpq_item_desc like '%Plan%' group by cpq_item_desc";    
                     // TBC, need to revise, return 0 records at this moment
        
        return getDailySalesFiguresItemsTotal(sql, location, start_date, end_date, conn);
    }    
    
    
    public ArrayList<EpcDailySalesFiguresItem>getAccessories(String location, String salesman, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and order_salesman = ? and " + 
                     "item_cat = 'DEVICE' and warehouse = 'AA' and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' " + 
                     "and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by cpq_item_desc";
        
        return getDailySalesFiguresItems(sql, salesman, location, start_date, end_date, conn);
    }
    
    
    public ArrayList<EpcDailySalesFiguresItem>getHandsets(String location, String salesman, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and order_salesman = ? and " + 
                     "item_cat = 'DEVICE' and warehouse = 'AH' and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' " + 
                     "and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by cpq_item_desc";
        
        return getDailySalesFiguresItems(sql, salesman, location, start_date, end_date, conn);
    }
    
    public ArrayList<EpcDailySalesFiguresItem>getVAS(String location, String salesman, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and order_salesman = ? " + 
                     //"and item_cat is null and parent_item_id in ( select item_id from epc_order_item where cpq_item_desc = 'VAS Services' ) " + 
                     //"and order_status != 'I' and order_status != 'CA' and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by cpq_item_desc";
                     "and item_cat = 'VAS' and parent_item_id in ( select item_id from epc_order_item where cpq_item_desc = 'VAS Services' ) " + 
                     "and order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' and place_order_location = ? " + 
			         "and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by cpq_item_desc";     // TBC, need to revise, return 0 records at this moment )
        
        return getDailySalesFiguresItems(sql, salesman, location, start_date, end_date, conn);
    }
    
    public ArrayList<EpcDailySalesFiguresItem> getActivationTypes(String location, String salesman, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), activation_type from epc_order a, epc_order_case b where a.order_id = b.order_id and order_salesman = ? and " + 
                     //"order_status != 'I' and order_status != 'CA' and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') group by activation_type";
                     "order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') " + 
			         "and order_date < to_date(?,'yyyyMMdd') and activation_type is not null group by activation_type";
        
        return getDailySalesFiguresItems(sql, salesman, location, start_date, end_date, conn);
    }
    
    public ArrayList<EpcDailySalesFiguresItem> getOffers (String location, String salesman, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and order_salesman = ? and " + 
                     "parent_item_id is null and item_cat is null and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' and " + 
                     "place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') and " + 
                     "order_status!='LOCK' group by cpq_item_desc"; 
                     //"cpq_item_desc like '%Offer%' group by cpq_item_desc"; 
        
        return getDailySalesFiguresItems(sql, salesman, location, start_date, end_date, conn);
    }

    public ArrayList<EpcDailySalesFiguresItem> getPlans (String location, String salesman, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<EpcDailySalesFiguresItem> result = new ArrayList();
        
        String sql = "select count(*), cpq_item_desc from epc_order a, epc_order_item b where a.order_id = b.order_id and order_salesman = ? and " + 
                     //"parent_item_id is null and item_cat is null and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' " + 
                     "parent_item_id is null and item_cat = 'PLAN' and cpq_item_desc is not null and order_status != 'I' and order_status != 'CA' and order_status != 'LOCK' " + 
                     "and place_order_location = ? and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') and " +
                     "cpq_item_desc like '%Plan%' group by cpq_item_desc";     
                     // TBC, need to revise, return 0 records at this moment
        
        return getDailySalesFiguresItems(sql, salesman, location, start_date, end_date, conn);
    }    
    
    public ArrayList<String> getSalesmen(String location, String start_date, String end_date, Connection conn) throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        
        String sql = "select distinct(order_salesman) from epc_order where order_status!='I' and order_status!='CA' and order_status != 'LOCK' and order_salesman is not null " +
                     "and order_date >= to_date(?,'yyyyMMdd') and order_date < to_date(?,'yyyyMMdd') and place_order_location=?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try { 
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, start_date);
            pstmt.setString(2, end_date);
            pstmt.setString(3, location);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                  result.add(rs.getString(1));
            }
            
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            try { if(rs != null) { rs.close(); } } catch (Exception e) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
        }
    }
    
    public EpcGetDailySalesFiguresResult getDailySalesFigures(EpcGetDailySalesFigures request) {
        EpcGetDailySalesFiguresResult result = new EpcGetDailySalesFiguresResult();
        result.setSearchCriteria(request);

        EpcDailySalesFigures DailySalesFigures = new EpcDailySalesFigures();
        //result.setDailySalesFigures(DailySalesFigures);
                
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
        
        String start_date = formatter.format(request.getStartDate());
        String end_date = formatter.format(new java.util.Date(request.getEndDate().getTime() + 86400000L));
        
        //result.setErrMsg( start_date + " - " + end_date);
        
        Connection conn = null;
        
        try {
            
            conn = epcDataSource.getConnection();
            
            if ( request.getType()!=null && !request.getType().equals("accessories") && !request.getType().equals("handsets") && 
                 !request.getType().equals("vas") && !request.getType().equals("activationTypes") && !request.getType().equals("offers") &&
                 !request.getType().equals("plans") ) {
                result.setResult("FAILED");
                result.setErrMsg("Type is invalid");
                return result;
            }
            
            String location = request.getLocation();
            
            boolean isTestingData = false;
            
            if (location.startsWith("testing_")) {
                location = location.substring(8);
                isTestingData = true;
            }                

            if (request.getSalesman() == null) {
                // statistics for the whole location
                
                if (request.getType()!=null) {
                    EpcDailySalesFiguresSummary total = new EpcDailySalesFiguresSummary();                    
                    DailySalesFigures.setTotal(total);

                    if (request.getType().equals("accessories")) {
                        if (isTestingData)
                           total.setAccessories(getTestingDataAccessoriesTotal());
                        else 
                           total.setAccessories(getAccessoriesTotal(location, start_date, end_date, conn));
                    }

                    if (request.getType().equals("handsets")) {
                        if (isTestingData)
                           total.setHandsets(getTestingDataHandsetsTotal());
                        else 
                           total.setHandsets(getHandsetsTotal(location, start_date, end_date, conn));
                    }

                    if (request.getType().equals("vas")) {
                        if (isTestingData)
                           total.setVas(getTestingDataVASTotal());
                        else 
                           total.setVas(getVASTotal(location, start_date, end_date, conn));
                    }

                    if (request.getType().equals("activationTypes")) {
                        if (isTestingData)
                           total.setActivationTypes(getTestingDataActivationTypesTotal());
                        else 
                           total.setActivationTypes(getActivationTypesTotal(location, start_date, end_date, conn));
                    }

                    if (request.getType().equals("offers")) {
                        if (isTestingData)
                           total.setOffers(getTestingDataOffersTotal());
                        else 
                           total.setOffers(getOffersTotal(location, start_date, end_date, conn));
                    }

                    if (request.getType().equals("plans")) {
                        if (isTestingData)
                           total.setPlans(getTestingDataPlansTotal());
                        else 
                           total.setPlans(getPlansTotal(location, start_date, end_date, conn));
                    }
                } else {
                    ArrayList<String> salesmen = null;
                            
                    if (isTestingData) 
                        salesmen = getTestingDataSalesmen();
                    else
                        salesmen = getSalesmen(location, start_date, end_date, conn);

                    ArrayList<EpcDailySalesFiguresSalesman> salesmen_info = new ArrayList<EpcDailySalesFiguresSalesman>();                    
                    DailySalesFigures.setSalesmen(salesmen_info);                    
    
                    for (int i=0; i<salesmen.size(); i++) {
                        EpcDailySalesFiguresSalesman item = new EpcDailySalesFiguresSalesman();
                        item.setSalesmanName((String)salesmen.get(i));
                        salesmen_info.add(item);
                    }               
                }
                /*                
                ArrayList<String> salesmen = getSalesmen(request.getLocation(), start_date, end_date, conn);
                
                for (int i=0; i<salesmen.size(); i++) {
                    EpcDailySalesFiguresSalesman item = new EpcDailySalesFiguresSalesman();
                    item.setSalesmanName((String)salesmen.get(i));
                    
                    EpcDailySalesFiguresSummary salesman_daily_sales_figures = new EpcDailySalesFiguresSummary();
                    
                    if (request.getType()==null || request.getType().equals("accessories"))                
                       salesman_daily_sales_figures.setAccessories(getAccessories(request.getLocation(),(String)salesmen.get(i),start_date, end_date,conn));
                    
                    if (request.getType()==null || request.getType().equals("handsets"))                    
                       salesman_daily_sales_figures.setHandset(getHandsets(request.getLocation(),(String)salesmen.get(i),start_date, end_date,conn));
                    
                    if (request.getType()==null || request.getType().equals("vas"))
                       salesman_daily_sales_figures.setVas(getVASItems(request.getLocation(),(String)salesmen.get(i),start_date, end_date,conn));
                    
                    if (request.getType()==null || request.getType().equals("activationType"))
                       salesman_daily_sales_figures.setActivationType(getActivationTypeItems(request.getLocation(),(String)salesmen.get(i),start_date, end_date,conn));
                    
                    if (request.getType()==null || request.getType().equals("offer"))
                       salesman_daily_sales_figures.setOffer(getOfferItems(request.getLocation(),(String)salesmen.get(i),start_date, end_date,conn));

                    if (request.getType()==null || request.getType().equals("plan"))
                       salesman_daily_sales_figures.setPlans(getPlanItems(request.getLocation(),(String)salesmen.get(i),start_date, end_date,conn));
                    
                    item.setSalesmanDailySalesFigures(salesman_daily_sales_figures);
                    
                    salesman_info.add(item);
                }*/
            } else {
                // individual salesman statistics 

                EpcDailySalesFiguresSalesman item = new EpcDailySalesFiguresSalesman();
                item.setSalesmanName(request.getSalesman());

                EpcDailySalesFiguresSummary salesman_daily_sales_figures = new EpcDailySalesFiguresSummary();

                if (request.getType()==null || request.getType().equals("accessories")) {
                   if (isTestingData) 
                      salesman_daily_sales_figures.setAccessories(getTestingDataAccessories(request.getSalesman()));
                   else 
                      salesman_daily_sales_figures.setAccessories(getAccessories(location,request.getSalesman(),start_date, end_date, conn));
                }
                
                if (request.getType()==null || request.getType().equals("handsets")) {
                   if (isTestingData)
                      salesman_daily_sales_figures.setHandsets(getTestingDataHandsets(request.getSalesman()));
                   else 
                      salesman_daily_sales_figures.setHandsets(getHandsets(location,request.getSalesman(),start_date, end_date, conn));
                }
                
                if (request.getType()==null || request.getType().equals("vas")) {
                   if (isTestingData)
                      salesman_daily_sales_figures.setVas(getTestingDataVAS(request.getSalesman()));
                   else 
                      salesman_daily_sales_figures.setVas(getVAS(location,request.getSalesman(), start_date, end_date,conn));
                }
                                
                if (request.getType()==null || request.getType().equals("activationTypes")) {
                   if (isTestingData)
                      salesman_daily_sales_figures.setActivationTypes(getTestingDataActivationTypes(request.getSalesman()));
                   else
                      salesman_daily_sales_figures.setActivationTypes(getActivationTypes(location,request.getSalesman(),start_date, end_date, conn));
                }
                
                if (request.getType()==null || request.getType().equals("offers")) {
                   if (isTestingData)
                      salesman_daily_sales_figures.setOffers(getTestingDataOffers(request.getSalesman()));
                   else 
                      salesman_daily_sales_figures.setOffers(getOffers(location,request.getSalesman(),start_date, end_date, conn));
                }

                if (request.getType()==null || request.getType().equals("plans")) {
                   if (isTestingData)
                      salesman_daily_sales_figures.setPlans(getTestingDataPlans(request.getSalesman()));
                   else 
                      salesman_daily_sales_figures.setPlans(getPlans(location,request.getSalesman(),start_date, end_date, conn));
                }

                item.setSalesmanDailySalesFigures(salesman_daily_sales_figures);

                ArrayList<EpcDailySalesFiguresSalesman> salesmen_info = new ArrayList<EpcDailySalesFiguresSalesman>();                    
                DailySalesFigures.setSalesmen(salesmen_info);                                  
                
                salesmen_info.add(item);
            }
            
            result.setDailySalesFigures(DailySalesFigures);            
            result.setResult("OK");
        } catch (Exception e) {
            result.setResult("FAILED");
            result.setErrMsg(e.toString());
            /*StackTraceElement element[] = e.getStackTrace();
            String msg = "";
            for (int i=0; i<element.length; i++) {
                msg = msg + "\\n" + element[i].toString();
            }
            result.setErrMsg(msg);*/
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}            
        }
        
        return result;
    }
}
