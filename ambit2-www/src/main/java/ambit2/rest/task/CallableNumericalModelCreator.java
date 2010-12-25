package ambit2.rest.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;

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
import ambit2.rest.task.dsl.ClientResourceWrapper;

public class CallableNumericalModelCreator<USERID> extends CallableModelCreator<Instances,Matrix,CoverageModelBuilder,USERID> {
	
	public CallableNumericalModelCreator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter,
			USERID token) {

		super(form, context,algorithm,
				new CoverageModelBuilder(applicationRootReference,
						reporter,
						alg_reporter,
						OpenTox.params.target.getValuesArray(form),
						OpenTox.params.parameters.getValuesArray(form)),
						token);
	}
	@Override
	public Reference doCall() throws Exception {
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
		ClientResourceWrapper client = null;
		try {
			client = new ClientResourceWrapper(reference);
			r = client.get(ChemicalMediaType.WEKA_ARFF);
			reader = new BufferedReader(new InputStreamReader(r.getStream()));
			return new Instances(reader);
		} catch (Exception x) {
			throw x;
		} finally {
			try {reader.close(); } catch (Exception x) {}
			try {r.release(); } catch (Exception x) {}
			try {if (client!=null) client.release(); } catch (Exception x) {}
		}
	}

}
