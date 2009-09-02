package ambit2.rest.propertyvalue;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveFieldPropertyValue;
import ambit2.db.search.AbstractQuery;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

public class PropertyValueResource<T> extends QueryResource<IQueryRetrieval<T>, T> {
	public static final String featureKey = "/feature";
	public static final String compoundFeature = String.format("%s/feature",CompoundResource.compoundID);
	public static final String compoundFeatureName = String.format("%s/feature/{name}",CompoundResource.compoundID);
	public static final String conformerFeatureName =  String.format("%s/feature/{name}",ConformerResource.conformerID);
	
	public static final String FeatureNameConformer =  String.format("/feature/{name}%s",ConformerResource.conformerID);
	public static final String FeatureNameCompound = String.format("/feature/{name}%s",CompoundResource.compoundID);
	
	public PropertyValueResource(Context context, Request request, Response response) {
		super(context,request,response);
		try {
			query = createQuery(context, request, response);
			error = null;
		} catch (AmbitException x) {
			query = null;
			error = x;
		}
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));
	}
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	
		return new StringConvertor(new PropertyValueReporter());
		} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new PropertyValueXMLReporter(getRequest().getRootRef()));
			
		} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new OutputStreamConvertor(
					new PropertyValueHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	getURUReporter(getRequest().getRootRef()),MediaType.TEXT_URI_LIST);
			
		} else return new StringConvertor(new PropertyValueReporter());
					
	}		
	@Override
	protected QueryURIReporter<T, IQueryRetrieval<T>> getURUReporter(
			Reference baseReference) throws ResourceException {
		PropertyValueURIReporter reporter = new PropertyValueURIReporter<T, IQueryRetrieval<T>>(baseReference);
		if (query instanceof AbstractQuery) {
			if (((AbstractQuery)query).getValue() instanceof IStructureRecord)
			reporter.setRecord((IStructureRecord)((AbstractQuery)query).getValue());
		}
		return reporter;
		
	}
	@Override
	protected IQueryRetrieval<T> createQuery(Context context,
			Request request, Response response) throws StatusException {
		RetrieveFieldPropertyValue  field = new RetrieveFieldPropertyValue();
		field.setSearchByAlias(true);
		
		IStructureRecord record = new StructureRecord();
		try {
			record.setIdchemical(Integer.parseInt(Reference.decode(request.getAttributes().get(CompoundResource.idcompound).toString())));
		} catch (NumberFormatException x) {
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid resource id %d",request.getAttributes().get(CompoundResource.idcompound)))
					);
		}
		try {
			record.setIdstructure(Integer.parseInt(Reference.decode(request.getAttributes().get(ConformerResource.idconformer).toString())));
			field.setChemicalsOnly(false);
		
		} catch (Exception x) {
			field.setChemicalsOnly(true);
		} finally {
			field.setValue(record);
		}
		try {
			field.setFieldname(null);
			Object name = request.getAttributes().get("name");
			if (name != null) {
				name = Reference.decode(name.toString());
				field.setFieldname(Property.getInstance(name.toString(),LiteratureEntry.getInstance()));
			} 
		} catch (Exception x) {
			field.setFieldname(null);
		}
		return (IQueryRetrieval) field;
	}

}
