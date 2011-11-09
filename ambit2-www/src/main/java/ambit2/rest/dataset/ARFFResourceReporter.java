package ambit2.rest.dataset;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.ARFFReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.property.PropertyURIReporter;

/**
 * Extension of {@link ARFFReporter} using URIs instead of names
 * @author nina
 *
 * @param <Q>
 */
public class ARFFResourceReporter<Q extends IQueryRetrieval<IStructureRecord>> extends ARFFReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2627930277795024333L;
	protected PropertyURIReporter reporter;

	public ARFFResourceReporter(Template template, Request request,ResourceDoc doc, String urlPrefix) {
		this(template,null,request,doc,urlPrefix);
	}
	public ARFFResourceReporter(Template template,Profile groupedProperties, Request request,ResourceDoc doc, String urlPrefix) {
		super(template,groupedProperties);
		setUrlPrefix(urlPrefix);
		reporter = new PropertyURIReporter(request,doc);
	}
	@Override
	protected String getRelationName() {
		return reporter.getResourceRef().toString();
	}
	
	@Override
	protected String getPropertyHeader(Property p) {
		
		return 
		String.format("@attribute %s %s\n", 
				reporter.getURI(p),
				p.getClazz()==Number.class?"numeric":"string");
	}
}
