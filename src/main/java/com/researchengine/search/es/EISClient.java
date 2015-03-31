package com.researchengine.search.es;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.researchengine.search.beans.Document;
import com.researchengine.search.exception.ESException;


/**
 * @author Praveen Kumar Pasupuleti
 *
 */
public class EISClient {
	
	private String clusterName;
	
	private String esHostName;
	
	private int esPort;
	
	private Client client =  null;
	

	/**
	 * @param esHostName
	 * @param esPort
	 * @param clusterName
	 * @param theAuthContext
	 */ 
	public EISClient(String esHostName, int esPort, String clusterName) {
		this.clusterName = clusterName;
		this.esHostName = esHostName;
		this.esPort = esPort;
		this.client = createClient();
	}

	/**
	 * @param index
	 * @param sourceType
	 * @param base64Content
	 * @return
	 * @throws ESException
	 */
	public boolean indexDocument(String index, String sourceType, String base64Content, String name, String contentType) throws Exception{
		try {
			boolean hasIndex = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet().isExists();
			//Check index is exist or not. If not create the index with userRefId
			if (!hasIndex) {
				XContentBuilder mapper = createAttachmentMapper();
				CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(index).addMapping("attachment", mapper).execute().actionGet();
				if (!createIndexResponse.isAcknowledged()) {
					throw new ESException(NoticeCodeESBase.FAILED_TO_CREATE_INDEX, index, sourceType);
				}
			}
			
			//Attach Base 64 content to the index 
			XContentBuilder sourceBuilder = jsonBuilder().startObject()
					.field(ESIndexFields.FILE.getFieldName(), base64Content)
					.field(ESIndexFields.TITLE.getFieldName(),name)
					.field(ESIndexFields.DATE.getFieldName(), Calendar.getInstance().getTime())
					.field(ESIndexFields.CONTENT_TYPE.getFieldName(), contentType)
					.field(ESIndexFields.METADATA.getFieldName(),createAttachmentMapper().string())
					.endObject();
			
			IndexResponse indexResponse = client.prepareIndex().setIndex(index).setType(sourceType).setSource(sourceBuilder).setRefresh(true).execute().actionGet();
			if (!indexResponse.isCreated()) {
				throw new ESException(NoticeCodeESBase.FAILED_TO_CREATE_INDEX, index, sourceType);
			} else {
				System.out.println("Is Index Created:"+indexResponse.isCreated());
				//Refresh indices
				client.admin().indices().prepareRefresh().execute().actionGet();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ESException(NoticeCodeESBase.FAILED_TO_CREATE_INDEX, e, index, sourceType); 
		} finally{
			client.close();
		}
		return true;
	}
	
	
	/**
	 * @param index
	 * @return
	 * @throws ESException
	 */
	public boolean deleteDocumentIndex(String index) throws ESException {
		try {
			boolean hasIndex = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet().isExists();
			if (hasIndex) {
				DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
				if(!deleteIndexResponse.isAcknowledged()){
					throw new ESException(NoticeCodeESBase.FAILED_TO_DELETE_INDEX, index);
				}
			}
		} catch (Exception e){
			throw new ESException(NoticeCodeESBase.FAILED_TO_DELETE_INDEX, index);
		}
		return true;
	}
	
	
	/**
	 * @param searchQuery
	 * @param userRefId
	 * @return
	 * @throws ESException
	 */
	public List<Document> search(String searchQuery, String index, String sourceType) throws ESException {
		try {
			List<Document> documents = null;
			QueryBuilder query = QueryBuilders.queryString(searchQuery);
			SearchRequestBuilder searchBuilder = client.prepareSearch(index);

			if (StringUtils.isNotEmpty(sourceType)) {
				searchBuilder.setTypes(sourceType);
			}
			searchBuilder.setQuery(query);
			searchBuilder.addFields(ESIndexFields.TITLE.getFieldName(),
					ESIndexFields.DATE.getFieldName(),
					ESIndexFields.METADATA.getFieldName(),
					ESIndexFields.CONTENT_TYPE.getFieldName(),
					ESIndexFields.ATTACHMENT.getFieldName());
			
			System.out.println("Search Query Builder:"+ searchBuilder.toString());

			SearchResponse searchResponse = searchBuilder.execute().actionGet();
			System.out.println("search Results:"+ searchResponse.toString());
			SearchHit[] searchHits = searchResponse.getHits().getHits();
			documents = new ArrayList<Document>(searchHits.length);
			
			for (SearchHit searchHit : searchHits) {
				documents.add(getDocument(index,sourceType,searchHit.getId()));
			}
		}  catch(IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	return null;
	}
	
	/**
	 * @param searchQuery
	 * @param userRefId
	 * @return
	 * @throws ESException
	 */
	public boolean isFileIndexed(String fileName, String userRefId) throws ESException {
		QueryBuilder query = QueryBuilders.queryString(fileName);
		SearchRequestBuilder searchBuilder = client.prepareSearch().setQuery(query)
				.addFields("title","path")
				.addHighlightedField("file");

		SearchResponse searchResponse = searchBuilder.execute().actionGet();
		SearchHit[] searchHits = searchResponse.getHits().getHits();
		if (searchHits.length > 0) {
			return true;
		}
		return false;
	}
	//---------------HELPERS-------------
	
	private Client createClient() {
		try {
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
			TransportClient tpClient = new TransportClient(settings);
			tpClient.addTransportAddress(new InetSocketTransportAddress(esHostName, esPort));
			return (Client)tpClient;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private XContentBuilder createAttachmentMapper() {
		// Attachment Fields - title, file, date, name, keywords, content_type
		XContentBuilder mapper = null;
		try {
			mapper = jsonBuilder().startObject()
					.startObject("attachment")
						.startObject("properties")
							.startObject("file")
								.field("type", "attachment")
								.field("path", "full")
									.startObject("fields")
										.startObject("title")
											.field("type","string")
											.startObject("fields")
												.startObject("suggest")
													.field("type","string")
													.field("store","yes")
												.endObject()
											.endObject()
										.endObject()
										.startObject("file")
											.field("term_vector","with_positions_offsets")
											.field("store","yes")
										.endObject()
										.startObject("date")
											.field("type","date")
											.startObject("fields")
												.startObject("string")
													.field("type","string")
												.endObject()
											.endObject()
										.endObject()
										.startObject("name")
											.field("type","string")
											.startObject("fields")
												.startObject("suggest")
													.field("type","string")
													.field("store","true")
												.endObject()
											.endObject()
										.endObject()
										.startObject("keywords")
											.field("type","string")
											.startObject("fields")
												.startObject("suggest")
													.field("type","string")
												.endObject()
											.endObject()
										.endObject()
										.startObject("content_type")
											.field("type","string")
										.endObject()
										.startObject("metadata")
											.field("type","string")
											.startObject("fields")
												.startObject("suggest")
													.field("type","string")
												.endObject()
											.endObject()
										.endObject()
								.endObject()
							.endObject()
						.endObject()
					.endObject();
			System.out.println(mapper.string());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapper;
	}
	
	public Document getDocument(String index, String sourceType, String id) throws ESException,IOException {
		GetRequestBuilder getRequestBuilder = client.prepareGet(index, sourceType, id);
		getRequestBuilder.setFields("title", "date","metadata", "content_type","file");
		GetResponse docResponse = getRequestBuilder.get();
		
		if(!docResponse.isExists()){
			return null;
		}
		
		String title =  (String) docResponse.getField("title").getValue();
		String date =  (String) docResponse.getField("date").getValue();
		String metaData =  (String) docResponse.getField("metadata").getValue();
		String contentType =  (String) docResponse.getField("content_type").getValue();
		String content = (String) docResponse.getField("file").getValue();
		System.out.println(title);
		System.out.println(date);
		System.out.println(metaData);
		System.out.println(contentType);
//		FileOutputStream  fos  = new FileOutputStream(new File(id + ".pdf"));
//		fos.write(org.elasticsearch.common.Base64.decode(content.getBytes()));
//		fos.close();
		
		Document document = new Document();
		document.setTitle(title);
		document.setDate(date);
		document.setMetaData(metaData);
		document.setContentType(contentType);
		document.setContent(content);
		document.setType(sourceType);
		
		return document;
	}
	
	
	public static void main(String[] args) {
		try {
			String index = "index6";
			String sourceType = "nfs";
			String contentType = "application/pdf";
			String fileName = "sample.pdf";
			
			String pdfPath = ClassLoader.getSystemResource(fileName).getPath();
			String base64Content = org.elasticsearch.common.Base64.encodeFromFile(pdfPath);
			
			EISClient eisClient = new EISClient("localhost", 9300, "elasticsearch");
			eisClient.indexDocument(index, sourceType, base64Content, fileName, contentType);
			
			eisClient = null;
			eisClient = new EISClient("localhost", 9300, "elasticsearch");
			eisClient.search("isl99201", index, sourceType);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ESException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}