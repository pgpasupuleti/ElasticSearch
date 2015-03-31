package com.researchengine.search.es;

/**
 * @author Praveen Kumar Pasupuleti
 *
 */
public enum ESIndexFields {
	FILE("file"),
	TITLE("title"),
	DATE("date"),
	CONTENT_TYPE("content_type"),
	METADATA("metadata"),
	NAME("name"),
	KEYWORDS("keywords"),
	ATTACHMENT("attachment"),
	;
	
	String fieldName;
	
	ESIndexFields(String field){
		fieldName = field;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
}
