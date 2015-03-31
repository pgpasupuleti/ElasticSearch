package com.researchengine.search.exception;

import com.researchengine.search.es.NoticeCodeESBase;


/**
 * @author Praveen Kumar Pasupuleti
 *
 */
public class ESException extends Exception {

	private static final long serialVersionUID = -9177443662584616709L;

	protected NoticeCodeESBase dcNoticeCode = null;
	
	protected String[] messageVars = null;

	public ESException(NoticeCodeESBase dcNoticeCode){
		super(dcNoticeCode.toString());
		this.dcNoticeCode = dcNoticeCode;
	}
	
	public ESException(NoticeCodeESBase dcNoticeCode, Throwable t) {
		super(dcNoticeCode.toString(), t);
		this.dcNoticeCode = dcNoticeCode;
	}
	
	public ESException(NoticeCodeESBase dcNoticeCode, String... messageVars) {
		super(dcNoticeCode.toString((Object[])messageVars));
		this.dcNoticeCode = dcNoticeCode;
		this.messageVars = messageVars;
	}
	
	public ESException(NoticeCodeESBase dcNoticeCode, Throwable t, String... messageVars) {
		super(dcNoticeCode.toString((Object[])messageVars), t);
		this.dcNoticeCode = dcNoticeCode;
		this.messageVars = messageVars;
	}
	
	public String getErrorCode(){
		return dcNoticeCode.getCode();
	}
}