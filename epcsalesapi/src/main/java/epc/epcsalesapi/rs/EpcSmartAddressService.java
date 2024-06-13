package epc.epcsalesapi.rs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import epc.epcsalesapi.sales.EpcOCRHandler;
import epc.epcsalesapi.sales.EpcSmartAddressHandler;
import epc.epcsalesapi.sales.bean.ocr.OCRInputBean;
import jakarta.xml.bind.DatatypeConverter;

@RestController
@RequestMapping(value = "/smartAddress")
public class EpcSmartAddressService {

	@Autowired
	private EpcSmartAddressHandler smartAddressHandler;
	
	@Autowired
    private EpcOCRHandler OCRHandler;
	
	@GetMapping(value = "/getAddress")
    @ResponseBody
    public ResponseEntity<?> getAddress(Integer n,Boolean isChi, String query) {
		try {
			if(n==null)
				n=1;
			if(isChi==null)
				isChi=false;
			List<String> list = smartAddressHandler.getAddress(n, isChi, query);
			return new ResponseEntity<List<String>>(list, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
        
    }
	
	@PostMapping(value = "/getAddressByImg")
    @ResponseBody
    public ResponseEntity<?> getAddressByImg(Integer n,Boolean isChi,String txtid, MultipartFile file) {
		try {
			if(n==null)
				n=1;
			if(isChi==null)
				isChi=false;
			byte[] imgbyte= file.getBytes();
			OCRInputBean ocrInputBean = new OCRInputBean();
	        ocrInputBean.setImage(DatatypeConverter.printBase64Binary(imgbyte));
	        ocrInputBean.setTxnId(txtid);
	        ocrInputBean.setRequester("smartAddress");
	        String addrproof=OCRHandler.remoteOCRTemplate(ocrInputBean,null,"FES_OCR_ADDRPROOF_LINK");
	        String query= JSONObject.parseObject(addrproof).getString("address");
			List<String> list = smartAddressHandler.getAddress(n, isChi, query);
			return new ResponseEntity<List<String>>(list, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
        
    }
}
