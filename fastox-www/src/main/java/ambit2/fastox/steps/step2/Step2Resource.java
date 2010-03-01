package ambit2.fastox.steps.step2;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.NotFoundException;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.RemoteTaskPool;

/**
 * Browse & check structure
 * @author nina
 *
 */
public class Step2Resource extends FastoxStepResource {
	protected String compound = null;
	public enum structure_mode {
		d1 {
			public String getName() {
				return "1D";
			}
		},
		d2 {
			public String getName() {
				return "2D";
			}
		},
		d3 {
			public String getName() {
				return "3D";
			}
		};	
		public abstract String getName();
	};		
	public enum structure_browser {
		prev {
			public String getName() {
				return "Previous compound";
			}
			@Override
			public String getShortcut() {
				return "<";
			}
		},
		next {
			public String getName() {
				return "Next compound";
			}
			@Override
			public String getShortcut() {
				return ">";
			}
		};	
		public abstract String getName();
		public abstract String getShortcut();
	};		


	public Step2Resource() {
		super(2);
		helpResource = "step2.html";
	}
	@Override
	protected String getDefaultTab() {

		return "Verify structure";
	}

	public void renderFormContent(Writer writer, String key) throws IOException {
		if (renderCompounds(writer)==0) {
			session.setError(new NotFoundException("We did not find any matching entries for the search you performed in the OpenTox database. Please go back to Step 1 of your ToxPredict workflow and try again."));
			getResponse().redirectSeeOther(getRequest().getReferrerRef());
		}
		super.renderFormContent(writer, key);
	}

	@Override
	public void renderResults(Writer writer, String key) throws IOException {

	}

	@Override
	protected Representation processMultipartForm(Representation entity,
			Variant variant) throws ResourceException {
		RemoteTaskPool pool = new RemoteTaskPool();
		try {
              //factory.setSizeThreshold(100);
			RemoteTask task = new RemoteTask(wizard.getService(SERVICE.dataset),variant.getMediaType(),entity,Method.POST,null);
			
			pool.add(task);
			pool.run();
			if (Status.SUCCESS_OK.equals(task.getStatus())) {
				session.setDatasetURI(task.getResult().toString());
				return get(variant);	
			} else if (Status.CLIENT_ERROR_NOT_FOUND.equals(task.getStatus())) {
				 throw new ResourceException(task.getStatus(),"Please use the \"Browse...\" button to select a file to upload!");
			} else throw new ResourceException(task.getStatus(),task.getResult()==null?task.getStatus().getDescription():task.getResult().toString());
			
		} catch (ResourceException x) {

			session.setError(x);
			getResponse().redirectSeeOther(getRequest().getReferrerRef());
			return null;
		} catch (Exception x) {
			session.setError(x);
			getResponse().redirectSeeOther(getRequest().getReferrerRef());
			return null;
		} finally {
			pool.clear();
		}
	}

}

