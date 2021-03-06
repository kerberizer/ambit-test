package ambit2.rest.substance.templates;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

import net.enanomapper.maker.TR;
import net.enanomapper.maker.TemplateMakerExtended;
import net.enanomapper.maker.TemplateMakerSettings;
import net.enanomapper.maker.TemplateMakerSettings._LAYOUT_RAW_DATA;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_CMD;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_TYPE;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryReporter;

public class AssayTemplateEntrySpreadsheetReporter<Q extends IQueryRetrieval<TR>>
		extends QueryReporter<TR, Q, OutputStream> {
	protected final HashSet<String> templateids = new HashSet<String>();
	protected final List<TR> records = new ArrayList<TR>();
	protected _TEMPLATES_TYPE templates_type;
	protected int number_of_experiments = 1;
	protected int number_of_endpoints = 1;
	protected int number_of_replicates = 1;
	protected int number_of_timepoints = 3;
	protected int number_of_concentration = 6;
	protected _LAYOUT_RAW_DATA layout = _LAYOUT_RAW_DATA.x_replicate_y_experiment;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4995013793566213468L;

	public AssayTemplateEntrySpreadsheetReporter(_TEMPLATES_TYPE templates_type, int number_of_replicates,
			int number_of_timepoints, int number_of_concentrations) {
		this(templates_type,_LAYOUT_RAW_DATA.x_replicate_y_experiment,1,1,number_of_replicates,number_of_timepoints,number_of_concentrations);
		
	}
	public AssayTemplateEntrySpreadsheetReporter(_TEMPLATES_TYPE templates_type, _LAYOUT_RAW_DATA layout, int number_of_experiments, int number_of_endpoints, int number_of_replicates,
			int number_of_timepoints, int number_of_concentrations) {
		this(templates_type);
		this.layout= layout;
		this.number_of_experiments = number_of_experiments;
		this.number_of_concentration = number_of_concentrations;
		this.number_of_replicates = number_of_replicates;
		this.number_of_timepoints = number_of_timepoints;
		this.number_of_endpoints = number_of_endpoints;
	}

	public AssayTemplateEntrySpreadsheetReporter(_TEMPLATES_TYPE templates_type) {
		this.templates_type = templates_type;
	}

	@Override
	public Object processItem(TR item) throws Exception {
		templateids.add(TR.hix.id.get(item).toString());
		records.add(item);
		return item;
	}

	@Override
	public OutputStream process(Q arg0) throws Exception {
		return super.process(arg0);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		super.close();
	}

	@Override
	public void footer(OutputStream output, Q query) {
		TemplateMakerSettings settings = new TemplateMakerSettings() {
			public java.lang.Iterable<TR> getTemplateRecords() throws Exception {
				return records;
			};

			@Override
			public File write(String templateid, _TEMPLATES_TYPE ttype, Workbook workbook) throws IOException {
				try {
					if (workbook!=null)
					workbook.write(output);
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
					if (workbook != null)
						workbook.close();
				}
				return null;
			}

		};

		try {
			TemplateMakerExtended maker = new TemplateMakerExtended();
			settings.setLayout_raw_data(layout);
			settings.setTemplatesType(templates_type);
			settings.setNumber_of_experiments(number_of_experiments);
			settings.setNumber_of_concentration(number_of_concentration);
			settings.setNumber_of_replicates(number_of_replicates);
			settings.setNumber_of_timepoints(number_of_timepoints);
			settings.setTemplatesCommand(_TEMPLATES_CMD.generate);
			settings.setNumber_of_endpoints(number_of_endpoints);
			// settings.setTemplatesType(_TEMPLATES_TYPE.jrc);
			settings.setSinglefile(true);
			// FIXME no input for generation needed, this is a placeholder
			File tmpdir = new File(System.getProperty("java.io.tmpdir"));
			settings.setInputfolder(tmpdir);
			settings.setOutputfolder(tmpdir);
			settings.setSinglefile(true);
			maker.generate(settings, templateids);
		} catch (Exception x) {
			x.printStackTrace();
		} finally {

		}
	}

	@Override
	public String getFileExtension() {
		return "xlsx";
	}

	@Override
	public void header(OutputStream output, Q query) {

	}

}
