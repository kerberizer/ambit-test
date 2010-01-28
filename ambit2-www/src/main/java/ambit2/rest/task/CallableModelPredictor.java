package ambit2.rest.task;

import java.io.Serializable;
import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ClientResource;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.ValuesReader;
import ambit2.rest.AmbitApplication;
import ambit2.rest.model.ModelURIReporter;

/**
 * 
 * @author nina
 *
 */
public abstract class CallableModelPredictor<ModelItem> extends CallableQueryProcessor<Object, IStructureRecord> {

	protected ModelQueryResults model;
	//protected Reference datasetURI;
	protected ModelURIReporter<IQueryRetrieval<ModelQueryResults>> modelUriReporter;
	
	public CallableModelPredictor(Form form, 
			Reference appReference,
			Context context,
			ModelQueryResults model,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter		
				) {
		super(form,appReference,context);
		this.model = model;
		this.modelUriReporter = reporter;
	}	

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		
		if (!applicationRootReference.isParent(reference)) throw 
			new Exception(String.format("Remote reference %s %s",applicationRootReference,reference));
		ObjectRepresentation<Serializable> repObject = null;
		try {
			ClientResource resource  = new ClientResource(reference);
			resource.setMethod(Method.GET);
			resource.get(MediaType.APPLICATION_JAVA_OBJECT);
			if (resource.getStatus().isSuccess()) {
				repObject = new ObjectRepresentation<Serializable>(resource.getResponseEntity());
				Serializable object = repObject.getObject();
				return object;
			}
			return reference;
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (repObject!=null) repObject.release();} catch (Exception x) {}
		}
	}
	protected abstract IProcessor<ModelItem,IStructureRecord> createPredictor(ModelQueryResults model) throws Exception ;

	
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors() throws Exception {
		createProfileFromReference(new Reference(modelUriReporter.getURI(model)+"/dependent"),null,model.getDependent());
		createProfileFromReference(new Reference(modelUriReporter.getURI(model)+"/independent"),null,model.getPredictors());
		createProfileFromReference(new Reference(modelUriReporter.getURI(model)+"/predicted"),null,model.getPredicted());

		IProcessor<ModelItem,IStructureRecord> calculator = createPredictor(model);
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		
		ValuesReader readProfile = new ValuesReader();
		/*
		Template template = new Template();
		template.setName("DailyIntake");
		Property di = new Property("DailyIntake");
		di.setEnabled(true);
		template.add(di); // this is a hack for TTC application, TODO make it generic!!!
		*/
		readProfile.setProfile(model.getPredictors());
		
		p1.add(readProfile);
		
		p1.add(calculator);
		p1.setAbortOnError(true);
		
		return p1;
	}
	/**
	 * Returns reference to the same dataset, with additional features, predicted by the model
	 */
	@Override
	protected Reference createReference(Connection connection) throws Exception {
		String predicted = String.format("%s/predicted", 
				(new Reference(modelUriReporter.getURI(model))).toString());
		return new Reference(
				String.format("%s?feature_uris[]=%s",
						sourceReference.toString(),
						Reference.encode(predicted)));
	}
	

}

