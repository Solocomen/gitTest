package epc.epcsalesapi.sales;

import org.springframework.stereotype.Service;

@Service
public class EpcOrderItemSql {
    
    public final String SQL_INSERT_ORDER_CASE = 
        "insert into epc_order_case ( " +
        "  order_id, quote_id, case_id, cpq_offer_guid, cpq_offer_desc, " +
        "  cpq_offer_desc_chi, quote_item_guid, quote_item_content, cms_item_mapping, subr_num, " +
        "  cust_num, acct_num " +
        ") values ( " +
        "  ?,?,?,?,?, " +
        "  ?,?,?,?,?, " +
        "  ?,? " +
        ") ";

    public final String SQL_INSERT_ORDER_ITEM = 
        "insert into epc_order_item ( " +
        "  order_id, quote_id, case_id, item_id, parent_item_id, " +
        "  cpq_item_guid, item_cat, item_code, cpq_item_desc, cpq_item_desc_chi, " +
        "  cpq_item_value, item_charge, reserve_id, warehouse, delivery_id, " +
        "  serial, premium, template_name, catalog_item_desc, catalog_rrp " +
        ") values ( " +
        "  ?,?,?,?,?, " +
        "  ?,?,?,?,?, " +
        "  ?,?,?,?,?, " +
        "  ?,?,?,?,? " +
        ") ";

    public final String SQL_INSERT_ORDER_VOUCHER = 
        "insert into epc_order_voucher ( " +
        "  order_id, case_id, item_id, assign_redeem, voucher_guid, " +
        "  voucher_master_id, status, create_date, modify_date " +
        ") values ( " +
        "  ?,?,?,?,?, " +
        "  ?,?,sysdate,sysdate " +
        ") ";
	
	// added by Danny Chan on 2023-3-25: start
	public final String SQL_INSERT_TNC_ITEM = 
		"insert into epc_order_tnc ( " + 
        "  order_id, case_id, item_id, parent_item_id, tnc_number, " + 
		"  tnc_short_description_zh_hk, tnc_zh_hk, tnc_short_description_en, tnc_en, create_date " + 
		") values (" + 
        "  ?, ?, ?, ?, ?, " + 
		"  ?, ?, ?, ?, sysdate " + 
        ") "; 
	// added by Danny Chan on 2023-3-25: end
	
	// added by Danny Chan on 2023-3-30: start 
	public final String SQL_INSERT_CONTRACT_DETAILS_HDR_ITEM = 
		"insert into epc_order_contract_details_hdr ( " + 
        "  order_id, case_id, item_id, parent_item_id, " + 
	    "  action_on_contract, can_be_appended, can_be_voided, contract_expiration_date_align_extend_options, contract_renewal_option, contract_type, " + 
        "  cooling_period, duration, effective_start_date, effective_end_date, effective_start_date_type, fixed_penalty_amount, " +
        "  fixed_penalty_amount_cpqchargelink, grace_period, is_contract_valid, max_penalty_amount, min_penalty_amount, min_req_price_value, " +
        "  min_req_total_vas_value, min_service_period, penalty_amount, penalty_type, reason, reference_id, required_payment_method, status, " + 
        "  target_align_extend_options, variable_penalty_amount " + 
        ") values (" + 
		"  ?, ?, ?, ?, " + 
		"  ?, ?, ?, ?, ?, ?, " + 
		"  ?, ?, ?, ?, ?, ?, " + 
		"  ?, ?, ?, ?, ?, ?, " + 
		"  ?, ?, ?, ?, ?, ?, ?, ?, " + 
		"  ?, ? " + 
		") "; 
	
    public final String SQL_INSERT_CONTRACT_DETAILS_DTL_ITEM = 
		"insert into epc_order_contract_details_dtl ( " + 
        "  order_id, case_id, item_id, " + 
		"  rec_id, rec_type, rec_value " + 
		") values (" + 
		"  ?, ?, ?, " + 
		"  epc_order_contract_details_dtl_seq.nextval, ?, ? " +
		")";
	// added by Danny Chan on 2023-3-30: end
	
	// added by Danny Chan on 2023-4-21 (save BuyBack info): start
	public final String SQL_INSERT_BUYBACK_ITEM = 
		"insert into epc_order_buyback ( " +
		"  order_id, case_id, item_id, parent_item_id, " + 
		"  bank, trade_in_product_code, price, buyback_date_from, " +
		"  buyback_date_to, buyback_month, buyback_grace_period " + 
		") values (" + 
		"  ?, ?, ?, ?, " + 
		"  ?, ?, ?, ?, " + 
		"  ?, ?, ? " + 
	    ")";
	// added by Danny Chan on 2023-4-21 (save BuyBack info): end

}
