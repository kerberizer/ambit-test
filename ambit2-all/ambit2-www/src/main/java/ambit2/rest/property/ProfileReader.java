package ambit2.rest.property;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryTemplateReporter;
import ambit2.db.search.property.AbstractPropertyRetrieval;
import ambit2.rest.task.CallableQueryProcessor;

public class ProfileReader extends AbstractDBProcessor<Reference, Template> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1956891586130018936L;
	protected Template profile;
	protected Reference applicationReference;
	protected QueryTemplateReporter<IQueryRetrieval<Property>> reporter;
	
	public ProfileReader(Reference applicationReference, Template profile) throws AmbitException {
		super();
		setApplicationReference(applicationReference);
		setProfile(profile==null?new Template():profile);
		reporter = new QueryTemplateReporter<IQueryRetrieval<Property>>(getProfile());
		reporter.setCloseConnection(false);
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		reporter.setConnection(connection);
	}
	@Override
	public void close() throws SQLException {
		reporter.close();
		super.close();
	}
	public Reference getApplicationReference() {
		return applicationReference;
	}

	public void setApplicationReference(Reference applicationReference) {
		this.applicationReference = applicationReference;
	}

	public Template getProfile() {
		return profile;
	}

	public void setProfile(Template profile) {
		this.profile = profile;
	}

	public Template process(Reference uri) throws AmbitException {
		if (profile == null) setProfile(new Template());
		if (uri==null) return profile;
		try {
			Object q = CallableQueryProcessor.getQueryObject(uri, applicationReference);
			if ((q!=null) && (q instanceof AbstractPropertyRetrieval)) {
				
				try {
					reporter.setConnection(getConnection());
					reporter.process((AbstractPropertyRetrieval)q);
				} catch (NotFoundException x) {
					//this is ok
				} catch(Exception x) {
					x.printStackTrace();
				} finally {
					//the reporter closes the connection as well
					try { reporter.close();} catch (Exception x) {}
				}
			} else 
				readFeaturesXML(uri,profile);
			return profile;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	
	protected void readFeaturesXML(Reference uri,final Template profile) throws Exception {
		if (uri==null) return;
		Representation r = null;
		try {
			
			ClientResource client = new ClientResource(uri);
			//client.setClientInfo(getRequest().getClientInfo());
			//client.setReferrerRef(getRequest().getOriginalRef());
			r = client.get(MediaType.TEXT_XML);
			
			PropertyDOMParser parser = new PropertyDOMParser() {
				@Override
				public void handleItem(Property property)
						throws AmbitException {
					if (property!= null)  {
						property.setEnabled(true);
						profile.add(property);
					}
				}
			};		
			parser.parse(new InputStreamReader(r.getStream(),"UTF-8"));
		} catch (Exception x) {
			throw x;

		} finally {
			try {if (r != null) r.release(); } catch (Exception x) {}
			
		}
	}	
	public void open() throws DbAmbitException {
		
	}

}
