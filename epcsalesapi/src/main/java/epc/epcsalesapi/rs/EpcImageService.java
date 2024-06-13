package epc.epcsalesapi.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcQrCodeHandler;


@RestController
@RequestMapping("/salesOrder/images")
public class EpcImageService {

	@Autowired
    private EpcQrCodeHandler epcQrCodeHandler;
	
	@GetMapping(
		value = "/qrcode",
		produces = MediaType.IMAGE_JPEG_VALUE
	)
	public @ResponseBody byte[] generateQrCode(@RequestParam("message") String message)  {
        return epcQrCodeHandler.createQRCode(message);
	}
}
