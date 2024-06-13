package epc.epcsalesapi.rs;

import epc.epcsalesapi.img.EpcImgHandler;
import epc.epcsalesapi.img.bean.EpcDeleteDocFromImg;
import epc.epcsalesapi.img.bean.EpcUploadDoc;
import epc.epcsalesapi.img.bean.EpcUploadDocToPci;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/img")
public class EpcImgService {

    @Autowired
    private EpcImgHandler epcImgHandler;

    @DeleteMapping(value = "/{recId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcDeleteDocFromImg> deleteDoc(@PathVariable(value = "recId", required = true) int recId) {
        EpcDeleteDocFromImg epcDeleteDocFromImg = null;

        epcDeleteDocFromImg = new EpcDeleteDocFromImg(recId);

        epcImgHandler.voidAttachmentToImg(epcDeleteDocFromImg);

        if (EpcApiStatusReturn.RETURN_SUCCESS.equals(epcDeleteDocFromImg.getResult())) {
            return new ResponseEntity<EpcDeleteDocFromImg>(epcDeleteDocFromImg, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcDeleteDocFromImg>(epcDeleteDocFromImg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcUploadDoc> uploadDoc(@RequestBody EpcUploadDoc epcUploadDoc) {
        epcImgHandler.uploadAttachment(epcUploadDoc);

        if (EpcApiStatusReturn.RETURN_SUCCESS.equals(epcUploadDoc.getResult())) {
            return new ResponseEntity<EpcUploadDoc>(epcUploadDoc, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcUploadDoc>(epcUploadDoc, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/uploadPci", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcUploadDocToPci> uploadDoc(@RequestBody EpcUploadDocToPci epcUploadDocToPci) {
        epcImgHandler.uploadAttachmentToPci(epcUploadDocToPci);

        if (EpcApiStatusReturn.RETURN_SUCCESS.equals(epcUploadDocToPci.getResult())) {
            return new ResponseEntity<EpcUploadDocToPci>(epcUploadDocToPci, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcUploadDocToPci>(epcUploadDocToPci, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
