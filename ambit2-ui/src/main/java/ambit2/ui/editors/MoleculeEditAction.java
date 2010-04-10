package ambit2.ui.editors;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.vecmath.Vector2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.jchempaint.JChemPaintPanel;
import org.openscience.jchempaint.application.JChemPaint;

import ambit2.core.config.AmbitCONSTANTS;


/**
 * 
 * Launches JChemPaint structure diagram editor for a preset molecule
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-10-23
 */
public class MoleculeEditAction extends AbstractMoleculeAction {
	protected IMoleculeSet molecules;
	protected IChemModel jcpModel;
	protected StructureDiagramGenerator sdg = null;
	protected boolean query = false;
	protected int value = -1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5166718649430988452L;

	public MoleculeEditAction(IMolecule molecule) {
		this(molecule,"Edit");
	}

	public MoleculeEditAction(IMolecule molecule, String arg0) {
		this(molecule, arg0,null);
		//Utils.createImageIcon("ambit2/ui/images/edit.png"));
	}

	public MoleculeEditAction(IMolecule molecule, String arg0, Icon arg1) {
		super( molecule,arg0, arg1);
		setJCPModel();

	}
	protected void setJCPModel() {
	    
		jcpModel =  DefaultChemObjectBuilder.getInstance()
            .newChemModel();
		jcpModel.setMoleculeSet(jcpModel.getBuilder().newMoleculeSet());
		jcpModel.getMoleculeSet().addAtomContainer(
				jcpModel.getBuilder().newMolecule());			
		//jcpModel.setTitle("JChemPaint structure diagram editor");
		//jcpModel.setAuthor(JCPPropertyHandler.getInstance().getJCPProperties().getProperty("General.UserName"));
		//Package jcpPackage = Package.getPackage("org.openscience.cdk.applications.jchempaint");
		//String version = jcpPackage.getImplementationVersion();
		//jcpModel.setSoftware("JChemPaint " + version);
		//jcpModel.setGendate((Calendar.getInstance()).getTime().toString());		
	}

	public void actionPerformed(ActionEvent arg0) {
		setMolecule(editMolecule(getMolecule()));
	}
	public IMolecule editMolecule(IMolecule mole) {		
			if (mole == null) {
		    	mole = DefaultChemObjectBuilder.getInstance().newMolecule();
		    	mole.addAtom(new Atom("C"));
		    	mole.setProperty("SMILES","C");
			}
			setMolecule(mole);
		
	    	if (molecules != null) {
				jcpModel.setMoleculeSet(molecules);
				
				Dimension d = new Dimension(460,450);
				JChemPaintPanel jcpep = new JChemPaintPanel(jcpModel, JChemPaint.GUI_APPLICATION, false,null);
	    		jcpep.setPreferredSize(d);
	    		//jcpep.registerModel(jcpModel);
	    		jcpep.setChemModel(jcpModel);//,new Dimension(200,200));
	    		
	    		//JFrame pane = getJCPFrame(jcpep);
	    		
	    		
	    		JOptionPane pane = new JOptionPane(jcpep, JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION,null);    		
	    		JDialog dialog = pane.createDialog(null, "JChemPaint Structure diagram editor");
	    		dialog.setBounds(300,100,d.width+100,d.height+100);
	    		dialog.setVisible(true);
	    		
	    		if (pane.getValue() == null) return mole;
	    		
	    		//super.run(arg0);
	    		pane.setVisible(true);
	    		
	    		int value = ((Integer) pane.getValue()).intValue();
	    		//while (value != 0);
	    		if (value == 0) { //ok
	    	    	molecules = jcpep.getChemModel().getMoleculeSet();
	    	    	if (molecule == null)  molecule = new org.openscience.cdk.Molecule(); 
	    	    	else 	molecule.removeAllElements();
	    	        for (int i=0; i < molecules.getAtomContainerCount(); i++) 
	    	            molecule.add(molecules.getMolecule(i));
	    	        
	    	        
	    	        //((Compound)dbaData.getQueryObject()).setMolecule(molecule);
	    	        if (JOptionPane.showConfirmDialog(null, "Remove all properties of the molecule?\n(Yes - to remove, No - to keep)", 
	    	        			"Structure diagram editor", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	    	        	molecule.getProperties().clear();
					IAtomContainer m = null;
					try {
					    m = (IMolecule )molecule.clone();
					} catch (Exception x) {
					    m = molecule;
					}

					m = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(m);
     
	    	        SmilesGenerator g = new SmilesGenerator();
	    	        molecule.setProperty(AmbitCONSTANTS.SMILES,g.createSMILES((IMolecule)m));
	    	        m = null;

	    	        g = null;
	    	        return molecule;
	    	        /*
	    	        if (query) dbaData.setQuery(molecule);
	    	        else dbaData.setMolecule(molecule);
    	        
	    	        return;
	    	        */
	    	    	
	    		} else setMolecule(null);
	    	}
		//}
	    	return mole;
		
	}
	
	public void setMolecule(IMolecule molecule) {
		super.setMolecule(molecule);
		try {
			molecules = getMoleculeForEdit(molecule);
		} catch (Exception x) {
			x.printStackTrace();
			molecules = null;
		}
	}

	protected IMoleculeSet getMoleculeForEdit(IAtomContainer atomContainer) throws Exception {
		if (atomContainer == null) return null;
		if (atomContainer instanceof QueryAtomContainer) {
			return null;
		}
		
		Iterator<IAtomContainer> t = ConnectivityChecker.partitionIntoMolecules(atomContainer).molecules().iterator();
		IMoleculeSet molecules = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		while (t.hasNext()) {
			IMolecule a = (IMolecule)t.next();
			if (!GeometryTools.has2DCoordinates(a)) {
				if (sdg == null) sdg = new StructureDiagramGenerator();
				sdg.setMolecule(a);
				sdg.generateCoordinates(new Vector2d(0,1));
				a = sdg.getMolecule();
			}
			molecules.addMolecule(a);			
		}	
		
		/*
		
		if (molecules.getAtomContainerCount() == 0) {
			//TODO configure atoms
		 	IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
	    	mol.addAtom(new Atom("C"));
	    	mol.setProperty("SMILES","C");
	    	molecules.addAtomContainer(mol);
		}
		
		IMoleculeSet m =  DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
		for (int i=0; i< molecules.getMoleculeCount();i++) {
			IMolecule a = molecules.getMolecule(i);
			if (!GeometryTools.has2DCoordinates(a)) {
				if (sdg == null) sdg = new StructureDiagramGenerator();
				sdg.setMolecule(a);
				sdg.generateCoordinates(new Vector2d(0,1));
				molecules.replaceAtomContainer(i, sdg.getMolecule());
			}
			m.addMolecule(molecules.getMolecule(i));
		}
		return m;		
		*/
		return molecules;
	}

	

    public synchronized boolean isQuery() {
        return query;
    }
    public synchronized void setQuery(boolean query) {
        this.query = query;
    }
    public JFrame getJCPFrame(JChemPaintPanel jcpep) {
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
		    /* (non-Javadoc)
             * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
             */
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                value = 0;
            }
		});
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().add(jcpep);
		frame.setTitle(jcpModel.toString());
		frame.setBounds(100,100,400,400);
		return frame;
    }
}



