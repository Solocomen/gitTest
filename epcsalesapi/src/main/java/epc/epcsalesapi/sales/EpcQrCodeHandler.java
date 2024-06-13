package epc.epcsalesapi.sales;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import com.swetake.util.Qrcode;
import org.springframework.stereotype.Service;


@Service
public class EpcQrCodeHandler {

	
	public byte[] createQRCode(String message) {
        String errMsg = "";
        char errorCorrect = 'M'; //H,Q,M,L
        char encodeMode = 'B';
        int factor = 8;
        Qrcode qrcode = new Qrcode();
        byte[] imageArray = null;

        try {
            qrcode.setQrcodeErrorCorrect(errorCorrect);
            qrcode.setQrcodeEncodeMode(encodeMode);

            byte[] d = message.getBytes();
            boolean[][] matrix = qrcode.calQrcode(d);
            BufferedImage bi = new BufferedImage(matrix.length * factor, matrix.length * factor, BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g = bi.createGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, matrix.length * factor, matrix.length * factor);
            g.setColor(Color.BLACK);

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[j][i]) {
                        g.fillRect(j * factor, i * factor, 1 * factor, 1 * factor);
                    }
                }
            }

            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpeg", baos1);
            imageArray = baos1.toByteArray();

            g.dispose();
            bi.flush();
        } catch (Exception e) {
            e.printStackTrace();
            imageArray = null;
        }
        return imageArray;
    }

}
