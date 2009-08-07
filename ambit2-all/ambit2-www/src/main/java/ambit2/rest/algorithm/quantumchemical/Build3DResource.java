package ambit2.rest.algorithm.quantumchemical;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.OutputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import ambit2.core.data.MoleculeTools;
import ambit2.core.io.MDLWriter;
import ambit2.mopac.MopacShell;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.algorithm.AlgorithmResource;
import ambit2.rest.error.EmptyMoleculeException;

public class Build3DResource extends AlgorithmResource {
	

	protected String smiles = null;
	protected MopacShell shell;
	
	public Build3DResource(Context context, Request request, Response response) {
		super(context,request,response);
		setCategory("");
		try {
			shell = new MopacShell();
		} catch (Exception x) {
			x.printStackTrace();
			shell = null;
		}
		this.getVariants().add(new Variant(ChemicalMediaType.CHEMICAL_MDLSDF));	
	
	}
	public Representation getreRepresentation(Variant variant) {
		
		try {
			try {
				this.smiles = Reference.decode(getRequest().getAttributes().get("smiles").toString());
			} catch (Exception x) {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, x);
				return null;
			}		
			
	        if (smiles != null) {
	        	IAtomContainer mol = MoleculeTools.getMolecule(smiles);
	        	if ((mol ==  null) || (mol.getAtomCount()==0)) {
		        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, new EmptyMoleculeException());
		        	return null;
	        	}
	        	final IAtomContainer newmol = shell.process(mol);	        	
	        	return new OutputRepresentation(ChemicalMediaType.CHEMICAL_MDLSDF) {
	        		@Override
	        		public void write(OutputStream out) throws IOException {
	        			MDLWriter writer  = new MDLWriter(out);
	        			try {
	        				writer.setSdFields(newmol.getProperties());
	        				writer.write(newmol);
	        			} catch (CDKException x) {
	        				x.printStackTrace();
	        			} finally {
		        			out.flush();
		        			out.close();
	        			}
	        		}
	        	};
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Undefined query");
	        	return null;        	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,x);
			return null;
		
		}
	}			

}
