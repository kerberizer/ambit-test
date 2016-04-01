package ambit2.rest.substance;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;

import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.substance.study.SubstanceStudyDetailsProcessor;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SubstanceRDFReporter<Q extends IQueryRetrieval<SubstanceRecord>>
		extends QueryRDFReporter<SubstanceRecord, Q> {
	private String base = "http://example.com";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4522513627076425922L;

	public SubstanceRDFReporter(Request request, MediaType mediaType) {
		super(request, mediaType, null);
		if (request != null)
			base = request.getRootRef().toString();
		SubstanceStudyDetailsProcessor paReader = new SubstanceStudyDetailsProcessor();

		getProcessors().clear();
		getProcessors().add(paReader);
		getProcessors().add(
				new DefaultAmbitProcessor<SubstanceRecord, SubstanceRecord>() {
					@Override
					public SubstanceRecord process(SubstanceRecord target)
							throws Exception {
						processItem(target);
						return target;
					};
				});
	}

	@Override
	public void header(Model output, Q query) {
		super.header(output, query);
		output.setNsPrefix("obo", "http://purl.obolibrary.org/obo/");
	}

	@Override
	protected QueryURIReporter<SubstanceRecord, IQueryRetrieval<SubstanceRecord>> createURIReporter(
			Request req, ResourceDoc doc) {
		return new SubstanceURIReporter<>(req);
	}

	@Override
	public Object processItem(SubstanceRecord record) throws Exception {
		HashFunction hf = Hashing.murmur3_32();

		Resource bioassayType = RDFTerms.BAO_0000015.getResource(getOutput());

		String substanceURI = uriReporter.getURI(record);
		Resource substanceResource = getOutput().createResource(substanceURI);

		String sownerURI = String.format("%s/owner/%s", base,
				record.getOwnerUUID());
		Resource sowner = getOutput().createResource(sownerURI);
		getOutput().add(substanceResource, DCTerms.source, sowner);
		if (record.getOwnerName() != null)
			getOutput().add(sowner, DCTerms.title, record.getOwnerName());

		if (record.getMeasurements() != null)
			for (ProtocolApplication<Protocol, String, String, IParams, String> pa : record
					.getMeasurements()) {
				/*
				 * assays - for now each protocol application as one assay,
				 * having one measure group. Later to consolidate assays on
				 * perhaps protocol + reference basis?
				 */

				String assayURI = String.format("%s/assay/%s", base,
						pa.getDocumentUUID());
				Resource assay = getOutput().createResource(assayURI);
				Protocol._categories assay_type = null;
				try {
					assay_type = Protocol._categories.valueOf(pa.getProtocol()
							.getCategory());
				} catch (Exception x) {
				}
				getOutput().add(
						assay,
						RDF.type,
						assay_type == null ? bioassayType : getOutput()
								.createResource(assay_type.getOntologyURI()));
				if (pa.getProtocol() != null
						&& pa.getProtocol().getEndpoint() != null)
					getOutput().add(assay, DC.title,
							pa.getProtocol().getEndpoint());
				if (pa.getProtocol() != null) {
					String guideline = null;
					if (pa.getProtocol().getGuideline() != null
							&& pa.getProtocol().getGuideline().size() > 0)

						guideline = pa.getProtocol().getGuideline().get(0);
					StringBuilder b = new StringBuilder();
					b.append(guideline == null ? "" : guideline);
					b.append(pa.getReference() == null ? "" : pa.getReference());
					b.append(pa.getParameters() == null ? "" : pa
							.getParameters().toString());

					HashCode hc = hf.newHasher()
							.putString(b.toString(), Charsets.UTF_8).hash();

					String protocolURI = String.format("%s/protocol/%s", base,
							hc.toString().toUpperCase());
					Resource protocol = getOutput().createResource(protocolURI);
					if (guideline != null)
						getOutput().add(protocol, DC.title, guideline);
					getOutput().add(assay,
							RDFTerms.BAO_0002846.getProperty(getOutput()),
							protocol);
				}

				/*
				 * this is not right, but just to have it for now the owner and
				 * the reference as source the owner of the assay, e.g. the
				 * company/lab
				 */
				if (pa.getReference() != null && !"".equals(pa.getReference())) {
					HashCode hc = hf.newHasher()
							.putString(pa.getReference(), Charsets.UTF_8)
							.hash();
					String referenceURI = String.format("%s/reference/%s",
							base, hc.toString().toUpperCase());
					Resource reference = getOutput().createResource(
							referenceURI);
					getOutput().add(reference, DC.title, pa.getReference());
					getOutput().add(assay, DCTerms.source, reference);

					if (pa.getReferenceOwner() != null
							&& !"".equals(pa.getReferenceOwner())) {
						String rownerURI = String.format("%s/owner/%s", base,
								pa.getReferenceOwner());
						Resource rowner = getOutput().createResource(rownerURI);
						getOutput().add(reference, DC.publisher, rowner);
					}
				}

				/*
				 * each protocol application as one measure group
				 */
				String measuregroupURI = String.format("%s/measuregroup/%s",
						base, pa.getDocumentUUID());
				Resource measuregroup = getOutput().createResource(
						measuregroupURI);
				getOutput().add(substanceResource,
						RDFTerms.BFO_0000056.getProperty(getOutput()),
						measuregroup);
				getOutput().add(assay,
						RDFTerms.BAO_0000209.getProperty(getOutput()),
						measuregroup);
				/*
				 * interpretation result as as separate endpoint group
				 */
				if (pa.getInterpretationResult() != null) {
					String endpointURI = String.format("%s/interpretation/%s",
							base, pa.getDocumentUUID());
					Resource endpoint = getOutput().createResource(endpointURI);
					getOutput().add(
							endpoint,
							RDFTerms.has_value.getProperty(getOutput()),
							getOutput().createLiteral(
									pa.getInterpretationResult()));
					getOutput().add(measuregroup,
							RDFTerms.OBI_0000299.getProperty(getOutput()),
							endpoint);
					getOutput().add(endpoint,
							RDFTerms.IAO_0000136.getProperty(getOutput()),
							substanceResource);
				}
				/*
				 * each effect as BAO endpoint
				 */
				if (pa.getEffects() != null)
					for (EffectRecord<String, IParams, String> effect : pa
							.getEffects()) {
						String endpointURI = String.format("%s/endpoint/ID%d",
								base, effect.getIdresult());
						Resource endpoint = getOutput().createResource(
								endpointURI);
						getOutput().add(measuregroup,
								RDFTerms.OBI_0000299.getProperty(getOutput()),
								endpoint);
						getOutput().add(endpoint,
								RDFTerms.IAO_0000136.getProperty(getOutput()),
								substanceResource);
						if (effect.getEndpoint() != null)
							getOutput().add(endpoint, RDFS.label,
									effect.getEndpoint());
						/**
						 * TODO
						 * 
						 * <pre>
						 * getOutput().add(endpoint, RDF.type, endpoint type as per BAO, e.g. AC50);
						 * </pre>
						 */
						if (effect.getLoValue() != null)
							getOutput()
									.add(endpoint,
											RDFTerms.has_value
													.getProperty(getOutput()),
											getOutput().createTypedLiteral(
													effect.getLoValue()));

						if (effect.getUnit() != null)
							getOutput().add(endpoint,
									RDFTerms.has_unit.getProperty(getOutput()),
									effect.getUnit());

						if (effect.getTextValue() != null
								&& !"".equals(effect.getTextValue())) {
							getOutput()
									.add(endpoint,
											RDFTerms.has_value
													.getProperty(getOutput()),
											getOutput().createTypedLiteral(
													effect.getTextValue()));
						}
					}
			}

		return record;
	}
}

enum RDFTerms {
	/**
	 * assay
	 */
	BAO_0000015 {
		@Override
		public String toString() {
			return "assay";
		}

		@Override
		public boolean isProperty() {
			return false;
		}
	},
	/**
	 * protocol
	 */
	OBI_0000272 {
		@Override
		public String toString() {
			return "protocol";
		}

		@Override
		public boolean isProperty() {
			return false;
		}
	},
	/**
	 * Property. has assay protocol
	 */
	BAO_0002846 {
		@Override
		public String toString() {
			return "has assay protocol";
		}

	},
	/**
	 * Property. participates in at some time
	 */
	BFO_0000056 {
		@Override
		public String toString() {
			return "participates in at some time";
		}
	},
	/**
	 * Property. has specified output
	 */
	OBI_0000299 {
		@Override
		public String toString() {
			return "has specified output";
		}
	},
	/**
	 * Property. is about
	 */
	IAO_0000136 {
		@Override
		public String toString() {
			return "is about";
		}
	},
	/**
	 * Property. has participant at some time
	 */
	BFO_0000057 {
		// todo use for conditions and parameters , e.g. proteins
		@Override
		public String toString() {
			return "has participant at some time";
		}
	},
	/**
	 * Property. has measure group
	 */
	BAO_0000209 {
		@Override
		public String toString() {
			return "has measure group";
		}
	},
	has_value {
		@Override
		public String getTerm() {
			return "has-value";
		}

		@Override
		public String getNamespace() {
			return "http://semanticscience.org/resource/";
		}
	},
	has_unit {
		@Override
		public String getTerm() {
			return "has-unit";
		}

		@Override
		public String getNamespace() {
			return "http://semanticscience.org/resource/";
		}
	};
	public String getNamespace() {
		return "http://purl.obolibrary.org/obo/";
	}

	public String getTerm() {
		return name();
	}

	public String getURI() {
		return String.format("%s%s", getNamespace(), getTerm());
	}

	public boolean isProperty() {
		return true;
	};

	public Property getProperty(Model jenaModel) throws Exception {
		if (isProperty()) {
			Property p = jenaModel.getProperty(getURI());
			return p != null ? p : jenaModel.createProperty(getURI());
		} else
			throw new Exception("Expected property, found " + getTerm());
	};

	public Resource getResource(Model jenaModel) throws Exception {
		if (!isProperty()) {
			Resource p = jenaModel.getResource(getURI());
			return p != null ? p : jenaModel.createResource(getURI());
		} else
			throw new Exception("Expected resource, found " + getTerm());
	}

}