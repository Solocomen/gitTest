package epc.epcsalesapi.vakaCms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.vakaCms.bean.VakaCmsGetProduct;
import epc.epcsalesapi.vakaCms.bean.VakaCmsGetProductResult;
import epc.epcsalesapi.vakaCms.bean.VakaCmsProduct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VakaCmsHandler {
    private final Logger logger = LoggerFactory.getLogger(VakaCmsHandler.class);

    private final DataSource epcDataSource;

    public VakaCmsHandler(DataSource epcDataSource) {
        this.epcDataSource = epcDataSource;
    }


    public VakaCmsProduct getProductDesc(String productCode) {
        RestTemplate restTemplate = new RestTemplate();
        VakaCmsGetProduct vakaCmsGetProduct = null;
        VakaCmsGetProductResult vakaCmsGetProductResult = null;
        VakaCmsProduct vakaCmsProduct = null;
        String apiUrl = EpcProperty.getValue("VAKACMS_GET_PRODUCT_DESC_LINK");

        try {
            vakaCmsGetProduct = new VakaCmsGetProduct();
            vakaCmsGetProduct.setEnv(".");
            vakaCmsGetProduct.setChannel(".");
            vakaCmsGetProduct.setSkuCode(productCode);

            vakaCmsGetProductResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(vakaCmsGetProduct), VakaCmsGetProductResult.class);
            if(vakaCmsGetProductResult != null) {
                for(VakaCmsProduct c : vakaCmsGetProductResult.getData()) {
                    if(productCode.equals(c.getSkuCode())) {
                        vakaCmsProduct = new VakaCmsProduct();
                        vakaCmsProduct.setProductNameEng(c.getProductNameEng());
                        vakaCmsProduct.setProductNameChi(c.getProductNameChi());
                        break;
                    }
                }
            }
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();

            vakaCmsProduct = null;
        } catch (Exception e) {
            e.printStackTrace();

            vakaCmsProduct = null;
        } finally {
        }
        return vakaCmsProduct;
    }


    public HashMap<String, Object> getDummyCmsMapping(String productCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String str = "";
        HashMap<String, Object> aMap = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            conn = epcDataSource.getConnection();
            sql = "select msg_content " +
                  "  from gp_msg_template a " +
                  " where msg_type = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "CMS_MAPPING_" + productCode);
            pstmt.setString(2, "A");
            rset = pstmt.executeQuery();
            if(rset.next()) {
                str = StringHelper.trim(rset.getString("msg_content"));
                aMap = objectMapper.readValue(str, HashMap.class);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return aMap;
    }


    public VakaCmsProduct getOfferDesc(String offerGuid) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String apiUrl = EpcProperty.getValue("VAKACMS_GET_METADATA_LINK");
        String returnJsonStr = "";
        String status = "";
        String jsonPath = "$.data.*[?(@.offerGuid == '" + offerGuid + "')]";
        List<Object> cmsOffers = null;
        VakaCmsProduct vakaCmsProduct = null;
        LinkedHashMap<String, String> aMap = null;
        String desc = "";
        String descChi = "";

        try {
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

            returnJsonStr = restTemplate.postForObject(apiUrl, new HttpEntity<>(new HashMap<String, String>(), headers), String.class);
            status = StringHelper.trim(JsonPath.read(returnJsonStr, "$.status"));
            if("ok".equals(status)) {
                cmsOffers = JsonPath.read(returnJsonStr, jsonPath);
                if(cmsOffers != null) {
                    for(Object c : cmsOffers) {
                        aMap = (LinkedHashMap<String, String>)c;
                        desc = StringHelper.trim((String)aMap.get("displayNameEn"));
                        descChi = StringHelper.trim((String)aMap.get("displayNameZh"));

                        vakaCmsProduct = new VakaCmsProduct();
                        vakaCmsProduct.setProductNameEng(desc);
                        vakaCmsProduct.setProductNameChi(descChi);
                        break;
                    }
                }
            }
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return vakaCmsProduct;
    }
}
