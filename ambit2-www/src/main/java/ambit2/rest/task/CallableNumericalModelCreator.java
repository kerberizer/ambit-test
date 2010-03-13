package ambit2.rest.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import weka.core.Instances;
import Jama.Matrix;
import ambit2.core.data.model.Algorithm;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.CoverageModelBuilder;

public class CallableNumericalModelCreator extends CallableModelCreator<Instances,Matrix,CoverageModelBuilder> {
	
	public CallableNumericalModelCreator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter) {

		super(form, context,algorithm,
				new CoverageModelBuilder(applicationRootReference,
						reporter,
						alg_reporter,
						OpenTox.params.target.getValuesArray(form),
						OpenTox.params.parameters.getValuesArray(form)));
	}
	
	public Reference call() throws Exception {
		Context.getCurrentLogger().info("Start()");
		Connection connection = null;
		try {

			try {
				target = createTarget(sourceReference);
				builder.setTrainingData((target instanceof Instances)?(Instances)target:null);
			} catch (Exception x) {
				target = sourceReference;
			}
			DBConnection dbc = new DBConnection(context);
			connection = dbc.getConnection();			
			return createReference(connection);
		} catch (Exception x) {

            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
            x.printStackTrace(new PrintWriter(stackTraceWriter));
			Context.getCurrentLogger().severe(stackTraceWriter.toString());
			throw x;
		} finally {
			Context.getCurrentLogger().info("Done");
			try { connection.close(); } catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
		
	}
	
	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		return null;
	}
	

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		Representation r = null;
		BufferedReader reader = null;
		try {
			ClientResource client = new ClientResource(reference);
			r = client.get(ChemicalMediaType.WEKA_ARFF);
			reader = new BufferedReader(new InputStreamReader(r.getStream()));
			return new Instances(reader);
		} catch (Exception x) {
			throw x;
		} finally {
			try {reader.close(); } catch (Exception x) {}
			try {r.release(); } catch (Exception x) {}
		}
	}

}
