package epc.epcsalesapi.helper;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.OracleCodec;
import org.springframework.stereotype.Service;

@Service
public class EpcSecurityHelper {
	private Encoder encoder;
	private Codec<OracleCodec> codec;

	public EpcSecurityHelper() {
		encoder = ESAPI.encoder();
		codec = (Codec)new OracleCodec();
	}
	
	
	public String validateId(String tmpId) {
		String aId = encode(tmpId);
		if(!"".equals(aId))
			return aId;
		else
			return "";
	}
	
	
	public String validateString(String tmpString) {
		String str = encode(tmpString);
		if(!"".equals(str))
			return str;
		else
			return "";
	}
	
	
	/**
	 * kerry, 20220119
	 * for vars that will be written in app log
	 * https://www.baeldung.com/jvm-log-forging
	 * 
	 * @param message
	 * @return
	 */
	public String encode(String message) {
		String str = "";
		if(message != null) {
			str = message;
		}
		str = str.replace('\n',  '_').replace('\r', '_').replace('\t', '_');
		str = ESAPI.encoder().encodeForHTML(str);
		return str;
	}
	
	public String encodeForSQL(String value) {
		String a = encoder.canonicalize(value); 
		return encoder.encodeForSQL(codec, a);
	}
}
