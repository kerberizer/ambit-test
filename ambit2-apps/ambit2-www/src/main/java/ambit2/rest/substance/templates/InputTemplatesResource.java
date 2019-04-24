package ambit2.rest.substance.templates;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Workbook;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.StreamConvertor;
import ambit2.rest.algorithm.CatalogResource;
import net.enanomapper.maker.TemplateMaker;
import net.enanomapper.maker.TemplateMakerSettings;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_CMD;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_TYPE;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.AbstractReporter;

public class InputTemplatesResource extends CatalogResource<TemplateMakerSettings> {
	protected String filename = "datatemplate";

	public InputTemplatesResource() {
		super();
		setHtmlbyTemplate(true);

	}

	@Override
	public String getTemplateName() {
		return "template_placeholder.ftl";
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.APPLICATION_MSOFFICE_XLSX });
		// getJSONConfig

	}

	@Override
	protected Iterator<TemplateMakerSettings> createQuery(Context context, Request request, Response response)
			throws ResourceException {
		ArrayList<TemplateMakerSettings> q = new ArrayList<TemplateMakerSettings>();
		TemplateMakerSettings ts = new TemplateMakerSettings();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		Object idtemplate = getRequest().getAttributes().get("idtemplate");
		if (idtemplate != null) {
			ts.setSinglefile(false);
			ts.setQueryTemplateid(idtemplate.toString());
			try {
				filename = ts.getOutputFile(idtemplate.toString(), _TEMPLATES_TYPE.jrc).getName();
			} catch (Exception x) {
				filename = "template.xlsx";
			}
		} else {
			ts.setSinglefile(true);
			String endpoint = form.getFirstValue("endpoint");
			if (endpoint == null)
				endpoint = "isoelectric point zeta potential";
			String assayname = form.getFirstValue("assay");
			if (assayname == null)
				assayname = "isoElectric point";
			ts.setQueryEndpoint(endpoint);
			ts.setQueryAssay(assayname);
			filename = String.format("datatemplate_%s_%s", endpoint.replaceAll(".xlsx", ""),
					assayname.replaceAll(" ", "_"));
		}
		q.add(ts);
		return q.iterator();
	}

	@Override
	public IProcessor<Iterator<TemplateMakerSettings>, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.APPLICATION_MSOFFICE_XLSX)) {
			TemplateReporter reporter = new TemplateReporter();
			return new StreamConvertor(reporter, MediaType.APPLICATION_MSOFFICE_XLSX, filename);

		}
		throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
	}

}

class TemplateReporter extends AbstractReporter<Iterator<TemplateMakerSettings>, OutputStream> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4282879409634932625L;

	@Override
	public OutputStream process(Iterator<TemplateMakerSettings> ts) throws Exception {
		Workbook workbook = null;
		while (ts.hasNext()) {
			try {
				TemplateMaker maker = new TemplateMaker();
				TemplateMakerSettings settings = ts.next();
				settings.setTemplatesCommand(_TEMPLATES_CMD.generate);
				settings.setTemplatesType(_TEMPLATES_TYPE.jrc);

				// FIXME no input for generation needed, this is a placeholder
				File tmpdir = new File(System.getProperty("java.io.tmpdir"));
				settings.setInputfolder(tmpdir);
				settings.setOutputfolder(tmpdir);
				settings.setSinglefile(true);

				workbook = maker.generate(settings);
				workbook.write(output);

				break;
			} catch (Exception x) {
				throw x;
			} finally {
				if (workbook != null)
					workbook.close();

			}

		}
		return output;
	}

	@Override
	public String getFileExtension() {
		return "xlsx";
	}

}
