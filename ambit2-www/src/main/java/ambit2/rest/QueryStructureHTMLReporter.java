package ambit2.rest;

import java.io.IOException;
import java.io.Writer;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryHeaderReporter;

/**
 * HTML generation for structure queries
 * @author nina
 *
 * @param <Q>
 */
public abstract class QueryStructureHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>>  extends QueryHeaderReporter<Q,Writer>  {
	protected QueryURIReporter uriReporter;
	protected String prefix ;

	
	public QueryURIReporter getUriReporter() {
		return uriReporter;
	}
	public void setUriReporter(QueryURIReporter uriReporter) {
		this.uriReporter = uriReporter;
	}

	protected boolean collapsed = true;
	/**
	 * 
	 */
	private static final long serialVersionUID = 16821411854045588L;
	
	/**
	 * 
	 */
	public QueryStructureHTMLReporter(ResourceDoc doc) {
		this(null,true,doc);
	}
	public QueryStructureHTMLReporter(Request request, boolean collapsed,ResourceDoc doc) {
		this("", request, collapsed, doc);
	}
	public QueryStructureHTMLReporter(String prefix,Request request, boolean collapsed,ResourceDoc doc) {
		super();
		this.prefix = prefix;
		uriReporter =  createURIReporter(request,doc);
		this.collapsed = collapsed;
		processors.clear();
		/*
		ValuesReader valuesReader = new ValuesReader();
		Profile profile = new Profile<Property>();
		profile.add(Property.getCASInstance());
		valuesReader.setProfile(profile);
		processors.add(valuesReader);
		*/
		processors.add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});
		
	}	
	protected abstract QueryURIReporter createURIReporter(Request request,ResourceDoc doc);
	
	@Override
	public void header(Writer w, Q query) {
		try {
			AmbitResource.writeHTMLHeader(w,query.toString(),uriReporter.getRequest(),
					uriReporter.getResourceRef(),uriReporter.getDocumentation());
		} catch (IOException x) {}
	}
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			AmbitResource.writeHTMLFooter(output,query.toString(),uriReporter.getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
		
	}

	public void open() throws DbAmbitException {
		
	}	
}