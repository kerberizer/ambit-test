package ambit2.core.smiles;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.base.config.Preferences;
import ambit2.base.external.ShellException;


public class SmilesParserWrapper implements PropertyChangeListener {
	protected static SmilesParserWrapper wrapper = null;
	protected OpenBabelShell babel = null;
	protected SmilesParser cdkParser = null;
	protected DeduceBondSystemTool  dbt;
	//protected org.openscience.cdk.smiles.DeduceBondSystemTool dbt;
	public enum SMILES_PARSER {
	    CDK, OPENBABEL 
	}
	protected SMILES_PARSER parser = SMILES_PARSER.OPENBABEL;
	
	protected SmilesParserWrapper() {
		this((Preferences.getProperty(Preferences.SMILESPARSER) == "true") ? SMILES_PARSER.OPENBABEL : SMILES_PARSER.CDK);


	}	
	protected SmilesParserWrapper(SMILES_PARSER mode) {
		super();
		setParser(mode);
		//dbt = new org.openscience.cdk.smiles.DeduceBondSystemTool();
		dbt = new DeduceBondSystemTool();
		Preferences.getPropertyChangeSupport().addPropertyChangeListener(Preferences.SMILESPARSER, this);
	}
	public IMolecule parseSmiles(String smiles) throws InvalidSmilesException {
		//System.out.println(smiles + " " + parser);
		switch (parser) {
		case OPENBABEL: {
			try {
				if (babel == null) babel = new OpenBabelShell();
				return babel.runShell(smiles);
			} catch (ShellException x) {
				setParser(SMILES_PARSER.CDK);
				if (cdkParser == null) cdkParser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
				IMolecule mol = cdkParser.parseSmiles(smiles);
				
				try {
					return dbt.fixAromaticBondOrders(mol);
				} catch (CDKException xx) {
					xx.printStackTrace();
					return mol;
					//throw new InvalidSmilesException(xx.getMessage());	
				}
			} catch (Exception x) {
				throw new InvalidSmilesException(x.getMessage());
			}
		}
		default: {
			if (cdkParser == null) cdkParser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
			IMolecule mol = cdkParser.parseSmiles(smiles);
			try {
				return dbt.fixAromaticBondOrders(mol);
				//return mol;
			} catch (Exception xx) {
				xx.printStackTrace();
				return mol;
				//throw new InvalidSmilesException(xx.getMessage());	
			}
			
		}
		}
	}
	public SMILES_PARSER getParser() {
		return parser;
	}
	public void setParser(SMILES_PARSER parser) {
		this.parser = parser;
	}
	public static SmilesParserWrapper getInstance(SMILES_PARSER mode) {
		if (wrapper == null) {
			wrapper = new SmilesParserWrapper(mode);
		}
		return wrapper;
	}	
	public static SmilesParserWrapper getInstance() {
		return getInstance("true".equals(Preferences.getProperty(Preferences.SMILESPARSER).toLowerCase()) ? SMILES_PARSER.OPENBABEL : SMILES_PARSER.CDK);
	}
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			setParser(
					"true".equals(Preferences.getProperty(Preferences.SMILESPARSER).toLowerCase()) ? SMILES_PARSER.OPENBABEL : SMILES_PARSER.CDK
					);
		} catch (Exception x) {
			setParser(SMILES_PARSER.OPENBABEL);
		}
		
	}
}
