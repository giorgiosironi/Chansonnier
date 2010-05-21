package it.polimi.chansonnier.servlet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smila.blackboard.Blackboard;
import org.eclipse.smila.blackboard.path.Path;
import org.eclipse.smila.datamodel.id.Id;
import org.eclipse.smila.datamodel.record.Annotation;
import org.eclipse.smila.datamodel.record.Record;
import org.eclipse.smila.datamodel.record.RecordFactory;
import org.eclipse.smila.lucene.LuceneSearchService;
import org.eclipse.smila.processing.ProcessingException;
import org.eclipse.smila.processing.parameters.SearchParameters;
import org.eclipse.smila.search.api.SearchResult;
import org.eclipse.smila.search.api.SearchService;

public class ChansonnierSearchService {
	public static final String DEFAULT_PIPELINE = "SearchPipeline";
	
	private SearchService _searchService;

	public void setSearchService(SearchService searchService) {
		this._searchService = searchService;
	}
	
	public List<Id> search(String attribute, String value) throws ProcessingException {
		Record queryRecord = RecordFactory.DEFAULT_INSTANCE.createRecord();
    	queryRecord.setMetadata(RecordFactory.DEFAULT_INSTANCE.createMetadataObject());
        final Annotation annotation = RecordFactory.DEFAULT_INSTANCE.createAnnotation();
        queryRecord.getMetadata().addAnnotation(SearchParameters.PARAMETERS, annotation);
        annotation.setNamedValue(SearchParameters.QUERY, value);
        annotation.setNamedValue(LuceneSearchService.SEARCH_ANNOTATION_QUERY_ATTRIBUTE, attribute);
		List<Id> records = new ArrayList<Id>();
        
		SearchResult result = Activator.getSearchService().search(DEFAULT_PIPELINE, queryRecord);
		if (result.getRecords() != null && result.getRecords().length > 0) {
			for (Record r : result.getRecords()) {
				Id id = r.getId();
				records.add(id);
			}
		}
       
		return records;
	}
}
