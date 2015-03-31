package com.researchengine.search.es;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;


/**
 * This class is the base class notice codes. Notice Codes encapsulate information
 * about error, warning and info codes and should contain static instances for each notice code
 * 
 * The codes adhere to a 7 digit structure. An example of the structure for the "ProxyFort" project
 * follows: <br> 
 * Product Code = DC<br>
 * Action<br>
 * <i>Fatal error = 1</i><br>
 * <i>Non-fatal error = 5</i><br>
 * <i>Info = 7</i><br>
 * <br>
 * Category<br>
 * <i>Conf = 10</i><br>
 * <i>Input = 11</i><br>
 * <i>Database = 12</i><br>
 * <i>Network/Communication = 13</i><br>
 * <i>OS = 14</i><br>
 * <i>Other = 15</i><br>
 * <br>
 * ErrorCode<br>
 * <i>Two digit error code</i><br>
 * 
 * @author Praveen Kumar Pasupuleti
 * 
 */

public class NoticeCodeESBase {

	private String code;
	
	private String description;
	
	//-------------Notice codes------------

	public static final NoticeCodeESBase FAILED_TO_CREATE_INDEX = new NoticeCodeESBase(
			"101111597", "Failed to create index in elastic search for userRefId: {0}, SourceType: {1}.");
	
	public static final NoticeCodeESBase FAILED_TO_DELETE_INDEX = new NoticeCodeESBase(
			"101111598", "Failed to delete index in elastic search for index: {0}");
	
	//------------- Getters ------------
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the description
	 */
	public String getDescription(Object... args) {
		
		if (args == null) {
			return getDescription();
		}
		return MessageFormat.format(getDescription(), args);
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	public String toString(){
		return code + " : " + description;
	}
	
	public String toString(Object...args) {
		if(args == null){
			return toString();
		}
		return code + ": " + MessageFormat.format(getDescription(), args);
	}

	//------------- Constructors  ------------
	
	public NoticeCodeESBase(String code, String description){
		this.code = code;
		if (StringUtils.isNotEmpty(getResourceBundleName())) {
			try {
				this.description = ResourceBundle.getBundle(getResourceBundleName()).getString(code);
				return;
			} catch(MissingResourceException e){
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		this.description = description;
	}
	
	
	public NoticeCodeESBase(){
		
	}
	
	//-------------Get Properties Bundle------------
	public String getResourceBundleName() {
		return "properties.messages";
	}
}
