package epc.epcsalesapi.rs;

import java.sql.*;
import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.sales.EpcAsiaMilesHandler;
import epc.epcsalesapi.sales.EpcQuoteHandler;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.asiaMiles.CreateAsiaMiles;
import epc.epcsalesapi.stock.EpcStockHandler;

@RestController
@RequestMapping("/test")
public class EpcTestService {
    @Autowired
    private EpcQuoteHandler epcQuoteHandler;

    @Autowired
    private EpcAsiaMilesHandler epcAsiaMilesHandler;

    @Autowired
    private DataSource epcDataSource;

    @Autowired
    private EpcStockHandler epcStockHandler;


    @Cacheable(value = "quotes", key = "#quoteId")
    @GetMapping(
      value = "/quote/{quoteId}",
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public EpcQuote getQuoteInfoWithParam(@PathVariable("quoteId") String quoteId, @RequestParam(name = "param", required = false) String param) {
System.out.println("inside EpcTestService.getQuoteInfoWithParam [" + quoteId + "]");
        return epcQuoteHandler.getQuoteInfo(quoteId, param);
    }


    @Cacheable(value = "quoteItems", key = "#quoteItemId")
    @GetMapping(
      value = "/quote/{quoteId}/item/{quoteItemId}/candidate",
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public HashMap<String, Object> getQuoteItemCandidate(@PathVariable("quoteId") String quoteId, @PathVariable(name = "quoteItemId") String quoteItemId) {
System.out.println("inside EpcTestService.getQuoteItemCandidate [" + quoteId + "][" + quoteItemId + "]");
      EpcQuoteItem i = epcQuoteHandler.getQuoteItem(quoteId, quoteItemId, "");
      HashMap<String, Object> candidateMap = i.getProductCandidate();
        return candidateMap;
    }


    @Cacheable(value = "quoteItems", key = "#quoteItemId")
    @GetMapping(
      value = "/quote/{quoteId}/item/{quoteItemId}/candidate2",
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public HashMap<String, Object> getQuoteItemCandidate2(@PathVariable("quoteId") String quoteId, @PathVariable(name = "quoteItemId") String quoteItemId) {
System.out.println("inside EpcTestService.getQuoteItemCandidate2 [" + quoteId + "][" + quoteItemId + "]");
      HashMap<String, Object> candidateMap = new HashMap<>();
      Connection conn = null;
      PreparedStatement pstmt = null;
      ResultSet rset = null;
      String sql = "";
      String a = "";

      try {
        conn = epcDataSource.getConnection();
        sql = "select quote_content from epc_order_quote a where order_id = -1 and quote_id = -1 ";
        pstmt = conn.prepareStatement(sql);
        rset = pstmt.executeQuery();
        if(rset.next()) {
          a = rset.getString(1);
          candidateMap = new ObjectMapper().readValue(a, HashMap.class);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
      }
      
      return candidateMap;
    }


    @PostMapping(
      value = "/asiaMiles",
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public CreateAsiaMiles createAsiaMilesRecord(@RequestBody CreateAsiaMiles createAsiaMiles) {
        epcAsiaMilesHandler.createAsiaMilesRecord(createAsiaMiles);
        return createAsiaMiles;
    }


//    @PostMapping(
//      value = "/markItemRefunding/{cancelReceipt}"
//    )
//    public boolean markItemRefunding(@PathVariable("cancelReceipt") String cancelReceipt) {
//        return epcStockHandler.markStockAsRefunding(cancelReceipt);
//    }
}
