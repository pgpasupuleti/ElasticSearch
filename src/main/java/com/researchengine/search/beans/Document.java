package com.researchengine.search.beans;

/**
 * @author Praveen Kumar Pasupuleti
 *
 */
public class Document {

	private String id;
	private String index;
	private String type;
	private String title;
	private String contentType;
	private String content;
	private String date;
	private String metaData;
	
	/**
	 * 
	 */
	public Document() {
	}


	/**
	 * @param id
	 * @param index
	 * @param type
	 * @param title
	 * @param contentType
	 * @param content
	 * @param date
	 * @param metaData
	 */
	public Document(String id, String index, String type, String title,
			String contentType, String content, String date, String metaData) {
		this.id = id;
		this.index = index;
		this.type = type;
		this.title = title;
		this.contentType = contentType;
		this.content = content;
		this.date = date;
		this.metaData = metaData;
	}
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the index
	 */
	public String getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(String index) {
		this.index = index;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the metaData
	 */
	public String getMetaData() {
		return metaData;
	}
	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
}
