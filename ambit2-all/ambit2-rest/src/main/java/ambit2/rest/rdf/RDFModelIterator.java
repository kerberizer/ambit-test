package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.core.data.model.ModelWrapper;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Parses RDF representation of a OpenTox Model {@link ModelQueryResults}
 * @author nina
 *
 */
public abstract class RDFModelIterator<T,TrainingInstances extends T,TestInstances extends T,Content> 
								extends RDFObjectIterator<ModelWrapper<T,TrainingInstances,TestInstances,Content>> {

	public RDFModelIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,OT.OTClass.Model.toString());
	}
		
	public RDFModelIterator(Reference reference) throws ResourceException {
		super(reference,OT.OTClass.Model.toString());
	}	
	public RDFModelIterator(Reference reference,MediaType mediaType) throws ResourceException {
		super(reference,mediaType,OT.OTClass.Model.toString());
	}
	
	public RDFModelIterator(InputStream in,MediaType mediaType) throws ResourceException {
		super(in,mediaType,OT.OTClass.Model.toString());
	}	
	
	public RDFModelIterator(OntModel model) {
		super(model,OT.OTClass.Model.toString());
	}	
	
	@Override
	protected Template createTemplate() {
		return new Template(String.format("%s%s",baseReference==null?"":baseReference,
				OpenTox.URI.model.getResourceID()));
	}

	@Override
	protected void parseObjectURI(RDFNode uri, ModelWrapper<T,TrainingInstances,TestInstances,Content> record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		
		try {
			getTemplate().parse(getIdentifier(uri), vars);
			record.setId(Integer.parseInt(vars.get(OpenTox.URI.model.getKey()).toString())); } 
		catch (Exception x) { record.setId(-1);};
		
	}

	@Override
	protected ModelWrapper<T,TrainingInstances,TestInstances,Content> parseRecord(Resource newEntry,
			ModelWrapper<T,TrainingInstances,TestInstances,Content> record) {
		//TODO
		return null;
	}

}
