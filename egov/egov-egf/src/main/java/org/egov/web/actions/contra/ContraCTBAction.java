/*******************************************************************************
 * eGov suite of products aim to improve the internal efficiency,transparency, 
 *    accountability and the service delivery of the government  organizations.
 * 
 *     Copyright (C) <2015>  eGovernments Foundation
 * 
 *     The updated version of eGov suite of products as by eGovernments Foundation 
 *     is available at http://www.egovernments.org
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or 
 *     http://www.gnu.org/licenses/gpl.html .
 * 
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 * 
 * 	1) All versions of this program, verbatim or modified must carry this 
 * 	   Legal Notice.
 * 
 * 	2) Any misrepresentation of the origin of the material is prohibited. It 
 * 	   is required that all modified versions of this material be marked in 
 * 	   reasonable ways as different from the original version.
 * 
 * 	3) This license does not grant any rights to any user of the program 
 * 	   with regards to rights under trademark law for use of the trade names 
 * 	   or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
package org.egov.web.actions.contra;

import org.apache.struts2.convention.annotation.Action;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.Bankaccount;
import org.egov.commons.CVoucherHeader;
import org.egov.egf.commons.EgovCommon;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.utils.HibernateUtil;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.model.contra.ContraBean;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.model.instrument.InstrumentOtherDetails;
import org.egov.model.instrument.InstrumentVoucher;
import org.egov.model.voucher.VoucherTypeBean;
import org.egov.services.contra.ContraService;
import org.egov.services.instrument.InstrumentService;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.egov.web.actions.voucher.BaseVoucherAction;
import org.springframework.transaction.annotation.Transactional;

import com.exilant.GLEngine.ChartOfAccounts;
import com.exilant.GLEngine.Transaxtion;
@Transactional(readOnly=true)
public class ContraCTBAction extends BaseVoucherAction
{
	private static final Logger	LOGGER	= Logger.getLogger(ContraCTBAction.class);
	private static final long serialVersionUID = 1L;
	private ContraService contraService;
	private VoucherService voucherService;
	private ContraBean contraBean;
	private VoucherTypeBean  voucherTypeBean;
	private EgovCommon egovCommon;
	private String message;
	private boolean close;
	private InstrumentService instrumentService;
	
	public InstrumentService getInstrumentService() {
		return instrumentService;
	}
	public void setInstrumentService(InstrumentService instrumentService) {
		this.instrumentService = instrumentService;
	}
	@SkipValidation
@Action(value="/contra/contraCTB-newform")
	public String newform()
	{
		if(LOGGER.isDebugEnabled())     LOGGER.debug("ContraCTBAction | newform | Start "); 
		final Date currDate = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		contraBean.setChequeDate(sdf.format(currDate));
		return NEW;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void prepare() {
		super.prepare();
		voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_CONTRA);
		addDropdownData("bankList", egovCommon.getBankBranchForActiveBanks());
		addDropdownData("accNumList", Collections.EMPTY_LIST);
		Map<String, Object> boundaryMap = egovCommon.getCashChequeInfoForBoundary();//contraService.getBoundaryLevel();
		addDropdownData("boundaryLevelList", (List<Boundary>)boundaryMap.get("listBndryLvl"));
		contraBean.setChequeInHand(boundaryMap.get("chequeInHand")!= null ?boundaryMap.get("chequeInHand").toString():null);
		contraBean.setCashInHand(boundaryMap.get("cashInHand")!= null ?boundaryMap.get("cashInHand").toString():null);
	}
	
	@Override
	protected void getHeaderMandateFields() {
		super.getHeaderMandateFields();
		mandatoryFields.add("boundarylevel");
	}
	
	public String create(){
		if(LOGGER.isDebugEnabled())     LOGGER.debug("ContraCTBAction | depositCTB | Start ");
		loadSchemeSubscheme();
		loadBankAccountNumber(contraBean);
		validateFields();
		if(validateBankTrans()){
			if(LOGGER.isDebugEnabled())     LOGGER.debug("validation passed ");
			try {
				if(LOGGER.isDebugEnabled())     LOGGER.debug("Cash Deposit Mode" + contraBean.getMode());
				if(null!=contraBean.getMode() &&  contraBean.getMode().equalsIgnoreCase("edit")){
					
					voucherHeader = voucherService.updateVoucherHeader(voucherHeader,voucherTypeBean); 
					InstrumentVoucher iVoucher = (InstrumentVoucher) persistenceService.find("from InstrumentVoucher where " +
							"voucherHeaderId=?", voucherHeader);
					Bankaccount bankAccount = (Bankaccount) persistenceService.find("from Bankaccount where id=?",
							Integer.valueOf(contraBean.getAccountNumberId()));
					InstrumentHeader instrHeader = iVoucher.getInstrumentHeaderId();
					if(LOGGER.isInfoEnabled())     LOGGER.info("instrument header id " +instrHeader.getBankAccountId().getId());			
					instrHeader.setBankAccountId(bankAccount); 
					instrHeader.setBankId(bankAccount.getBankbranch().getBank());
					instrHeader.setBankBranchName(bankAccount.getBankbranch().getBranchname());
					instrHeader.setInstrumentAmount(contraBean.getAmount());
					instrHeader.setTransactionNumber(contraBean.getChequeNumber());
					try {
						instrHeader.setTransactionDate(Constants.DDMMYYYYFORMAT2.parse(contraBean.getChequeDate()));
					} catch (ParseException e) {
						throw new ValidationException(Arrays.asList(new  ValidationError("Invalid Referece Date","invalid.date")));
					}
					
					InstrumentOtherDetails iOther = (InstrumentOtherDetails) persistenceService.find("from InstrumentOtherDetails where instrumentHeaderId=?",
							instrHeader);
					if(LOGGER.isDebugEnabled())     LOGGER.debug("cash deposit amount : = "+ instrHeader.getInstrumentAmount());
					instrumentService.instrumentHeaderService.update(instrHeader);
					instrumentService.instrumentVouherService.update(iVoucher);
					instrumentService.instrumentOtherDetailsService.update(iOther);
					contraService.updateIntoContraJournal(voucherHeader, contraBean);
					contraService.updateBankreconciliation(instrHeader, contraBean);
					voucherService.deleteVDByVHId(voucherHeader.getId());
					voucherService.deleteGLDetailByVHId(voucherHeader.getId());
					
				}else{
					voucherHeader = voucherService.postIntoVoucherHeader(voucherHeader,voucherTypeBean);
					voucherService.insertIntoRecordStatus(voucherHeader);
					//contraService.postIntoContraJournal(voucherHeader,contraBean);
					//contraService.postIntoBankreconciliation(voucherHeader,contraBean);
					List<InstrumentHeader> instrumentList = instrumentService.addToInstrument(createInstruments(contraBean));
					HibernateUtil.getCurrentSession().flush();
					Bankaccount bankAccount = (Bankaccount) persistenceService.find("from Bankaccount where id=?",
							Integer.valueOf(contraBean.getAccountNumberId()));
					Map valuesMap = contraService.prepareForUpdateInstrumentDeposit(bankAccount.getChartofaccounts().getGlcode());
					contraService.updateCashDeposit(voucherHeader.getId(), bankAccount.getChartofaccounts().getGlcode(), instrumentList.get(0),valuesMap);
					updateInstrument(instrumentList.get(0), voucherHeader);
				}
				
				
				
				if(LOGGER.isDebugEnabled())     LOGGER.debug("going to post into transactions");
				 List<Transaxtion> transactions = contraService.postInTransaction(voucherHeader, contraBean);
				HibernateUtil.getCurrentSession().flush();
				 ChartOfAccounts engine=ChartOfAccounts.getInstance();
				 Transaxtion txnList[]=new Transaxtion[transactions.size()];
				 txnList=(Transaxtion[])transactions.toArray(txnList);
				 SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
				 Connection conn = null;
				 if(!engine.postTransaxtions(txnList, formatter.format(voucherHeader.getVoucherDate())))
				 {
					 List<ValidationError> errors=new ArrayList<ValidationError>();
					 errors.add(new ValidationError("exp","Engine Validation failed"));
					 throw new ValidationException(errors);
				 }else{
					 contraBean.setResult("sucess");
				 }
			} catch (Exception e) {
				
				 List<ValidationError> errors=new ArrayList<ValidationError>();
				 errors.add(new ValidationError("exp",e.getMessage()));
				 throw new ValidationException(errors);
			}
			
			
		}
		return NEW;
	}
	private List<Map<String, Object>> createInstruments(final ContraBean cBean) {
		
		final Map<String, Object> iMap = new HashMap<String, Object>();
		final List<Map<String, Object>> iList = new ArrayList<Map<String, Object>>();
		iMap.put("Is pay cheque", FinancialConstants.IS_PAYCHECK_ZERO);
		iMap.put("Instrument type", FinancialConstants.INSTRUMENT_TYPE_CASH);
		iMap.put("Instrument amount", Double.valueOf(cBean.getAmount().toString()));
		Bankaccount bankAccount = (Bankaccount) persistenceService.find("from Bankaccount where id=?",
				Integer.valueOf(cBean.getAccountNumberId()));
		
		iMap.put("Bank code",  bankAccount.getBankbranch().getBank().getCode());
		iMap.put("Bank branch name", bankAccount.getBankbranch().getBranchaddress1());
		iMap.put("Bank account id", bankAccount.getId());
		
		iMap.put("Transaction number", cBean.getChequeNumber());
		try {
			iMap.put("Transaction date", Constants.DDMMYYYYFORMAT2.parse(cBean.getChequeDate()));
		} catch (ParseException e) {
			throw new ValidationException(Arrays.asList(new  ValidationError("Invalid Referece Date","invalid.date")));
		}
		
		iList.add(iMap);
		return iList;
		
		
	}

	private void updateInstrument(final InstrumentHeader ih, final CVoucherHeader vh) {
		final Map<String, Object> iMap = new HashMap<String, Object>();
		final List<Map<String, Object>> iList = new ArrayList<Map<String, Object>>();
		iMap.put("Instrument header", ih);
		iMap.put("Voucher header", vh);
		iList.add(iMap);
		instrumentService.updateInstrumentVoucherReference(iList);
	}
	public String loadCTBVoucher(){	
		String vhid = parameters.get("vhid")[0];
		if(LOGGER.isDebugEnabled())     LOGGER.debug("voucher header id received is :"+ vhid);
		Map<String, Object> vhInfoMap = contraService.getCTBVoucher(vhid,contraBean);
		voucherHeader = (CVoucherHeader)vhInfoMap.get(Constants.VOUCHERHEADER);
		contraBean = (ContraBean)vhInfoMap.get("contrabean");
		loadSchemeSubscheme();
		loadBankAccountNumber(contraBean);
		if(null != parameters.get("showMode")){
			contraBean.setMode(parameters.get("showMode")[0]);
		}
		if(null != parameters.get("showMode") && parameters.get("showMode")[0].equalsIgnoreCase("reverse")){
			return "reverse";
		}else if(null != parameters.get("showMode") && parameters.get("showMode")[0].equalsIgnoreCase("edit")){
			return EDIT;
		}
		return NEW;
	}
	private boolean validateBankTrans(){
		
		if(null == contraBean.getBankBranchId() || contraBean.getBankBranchId().equalsIgnoreCase("-1")){
			addActionError(getText("contra.validate.bank"));
			return false;
		}
		if(null == contraBean.getAccountNumberId() || contraBean.getAccountNumberId().equalsIgnoreCase("-1")){
			addActionError(getText("contra.validate.accnum"));
			return false;
		}
		if(null == contraBean.getAmount() || contraBean.getAmount().equals(BigDecimal.ZERO)){
			addActionError(getText("contra.validate.amt"));
			return false;
		}
		BigDecimal cashBalance = egovCommon.getCashBalance(voucherHeader.getVoucherDate(), contraBean.getCashInHand(), 
									voucherHeader.getFundId().getId());
		
		if(cashBalance.compareTo(contraBean.getAmount()) == -1){
			addActionError(getText("contra.validate.cashbalance1",new String[]{""+cashBalance}));
			return false;
		}else {
			 // get bank account balance.
			BigDecimal accountBalance = egovCommon.getAccountBalance(voucherHeader.getVoucherDate(),
					Integer.valueOf(contraBean.getAccountNumberId()));
			if(LOGGER.isDebugEnabled())     LOGGER.debug("Account balance for the bank account id : "+ contraBean.getAccountNumberId() + " is :"+ accountBalance);
			contraBean.setAccountBalance(accountBalance);
		}
		return true;
	}
	
	public ContraService getContraService() {
		return contraService;
	}
	public void setContraService(ContraService contraService) {
		this.contraService = contraService;
	}
	public VoucherService getVoucherService() {
		return voucherService;
	}
	public void setVoucherService(VoucherService voucherService) {
		this.voucherService = voucherService;
	}
	public ContraBean getContraBean() {
		return contraBean;
	}
	public void setContraBean(ContraBean contraBean) {
		this.contraBean = contraBean;
	}
	public VoucherTypeBean getVoucherTypeBean() {
		return voucherTypeBean;
	}
	public void setVoucherTypeBean(VoucherTypeBean voucherTypeBean) {
		this.voucherTypeBean = voucherTypeBean;
	}
	public EgovCommon getEgovCommon() {
		return egovCommon;
	}
	public void setEgovCommon(EgovCommon egovCommon) {
		this.egovCommon = egovCommon;
	}
	@Override
	public String getReversalVoucherDate() {
		return super.getReversalVoucherDate();
	}
	@Override
	public String getReversalVoucherNumber() {
		return super.getReversalVoucherNumber();
	}
	
	@Override
	public void setReversalVoucherDate(String reversalVoucherDate) {
		super.setReversalVoucherDate(reversalVoucherDate);
	}
	
	public String reverse(){
		super.saveReverse("CashToBank","Contra");
		return NEW;
	}
	
	public String reverseAndView(){
		super.saveReverse("CashToBank","Contra");
		setMessage(getText("transaction.success") + voucherHeader.getVoucherNumber());
		return Constants.VIEW;
	}

	public String reverseAndClose(){
		super.saveReverse("CashToBank","Contra");
		setClose(true);
		setMessage(getText("transaction.success") + voucherHeader.getVoucherNumber());
		return Constants.VIEW;
	}
	public void setClose(boolean close) {
		this.close = close;
	}
	public boolean isClose() {
		return close;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}


}
