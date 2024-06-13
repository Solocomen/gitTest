/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.EpcOrderAttach
 * @author	TedKwan
 * @date	20-Jul-2022
 * Description:
 *
 * History:
 * 20220720-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean.orderAttachment;

import java.sql.Blob;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class EpcOrderAttach {
	
	private Integer REC_ID;
	private String CUST_ID;
	private Integer ORDER_ID;
	private String ATTACH_TYPE;
	private Blob ATTACH_CONTENT;
        private Long IMG_SEQ_NO;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date CREATE_DATE;
	
	private String CREATE_TIMESTAMP;
	private String CREATE_MTH;
	private String CONTENT_TYPE;
        
        //JSON ignore for internal use
        @JsonIgnore
        private transient byte[] ATTACH_CONTENT_BYTE;
	
	public Integer getREC_ID() {
		return REC_ID;
	}
	public void setREC_ID(Integer rEC_ID) {
		REC_ID = rEC_ID;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public Integer getORDER_ID() {
		return ORDER_ID;
	}
	public void setORDER_ID(Integer oRDER_ID) {
		ORDER_ID = oRDER_ID;
	}
	public String getATTACH_TYPE() {
		return ATTACH_TYPE;
	}
	public void setATTACH_TYPE(String aTTACH_TYPE) {
		ATTACH_TYPE = aTTACH_TYPE;
	}
	public Blob getATTACH_CONTENT() {
		return ATTACH_CONTENT;
	}
	public void setATTACH_CONTENT(Blob aTTACH_CONTENT) {
		ATTACH_CONTENT = aTTACH_CONTENT;
	}
	public Date getCREATE_DATE() {
		return CREATE_DATE;
	}
	public void setCREATE_DATE(Date cREATE_DATE) {
		CREATE_DATE = cREATE_DATE;
	}
	public String getCREATE_TIMESTAMP() {
		return CREATE_TIMESTAMP;
	}
	public void setCREATE_TIMESTAMP(String cREATE_TIMESTAMP) {
		CREATE_TIMESTAMP = cREATE_TIMESTAMP;
	}
	public String getCREATE_MTH() {
		return CREATE_MTH;
	}
	public void setCREATE_MTH(String cREATE_MTH) {
		CREATE_MTH = cREATE_MTH;
	}
	public String getCONTENT_TYPE() {
		return CONTENT_TYPE;
	}
	public void setCONTENT_TYPE(String cONTENT_TYPE) {
		CONTENT_TYPE = cONTENT_TYPE;
	}

        public Long getIMG_SEQ_NO()
        {
            return IMG_SEQ_NO;
        }

        public void setIMG_SEQ_NO(Long IMG_SEQ_NO)
        {
            this.IMG_SEQ_NO = IMG_SEQ_NO;
        }

        @JsonIgnore
        public byte[] getATTACH_CONTENT_BYTE()
        {
            return ATTACH_CONTENT_BYTE;
        }

        @JsonIgnore
        public void setATTACH_CONTENT_BYTE(byte[] ATTACH_CONTENT_BYTE)
        {
            this.ATTACH_CONTENT_BYTE = ATTACH_CONTENT_BYTE;
        }
}
