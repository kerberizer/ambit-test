
package ambit2.mopac;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.smi23d.ShellMengine;

/**
 * Prepares input file for running MOPAC by  {@link MopacShell} <br>
 * Optimization is switched on if there are no coordinates. 
 * If only 2D coordinates are supplied,
 * then an initial structure is generated by {@link ShellMengine}<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2008-12-13
 */
public class Mopac7Writer extends DefaultChemObjectWriter {
    private BufferedWriter writer;
    private static LoggingTool logger = new LoggingTool(Mopac7Writer.class);
   // protected String mopac_commands = "EF GNORM=0.100 MMOK GEO-OK AM1 LET XYZ";
    //protected String mopac_commands = "AM1 NOINTER NOMM BONDS PRECISE MULLIK XYZ"; 
    protected String default_mopac_commands[] =	{
    		//"AM1 NOINTER NOMM BONDS MULLIK XYZ 1SCF",
    		"PM3 NOINTER NOMM BONDS MULLIK XYZ 1SCF",
    		"PM3 NOINTER NOMM BONDS MULLIK PRECISE"};
    protected String mopac_commands = "";
    private IsotopeFactory isotopeFactory = null;
    protected String blank = " ";
    protected int optimize = 1;
    protected NumberFormat nf;
    

    public Mopac7Writer(OutputStream out) throws Exception {
        this(new BufferedWriter(new OutputStreamWriter(out)));
    }
    public Mopac7Writer(Writer out) throws Exception {
        nf = NumberFormat.getInstance(Locale.US);
        nf.setMaximumFractionDigits(4);
        writer = new BufferedWriter(out);
        try {
            isotopeFactory = IsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance());
        } catch (Exception exception) {
            logger.error("Failed to initiate isotope factory: ", exception.getMessage());
            logger.debug(exception);
            if (exception instanceof CDKException) {
                throw exception;
            } else {
                throw new CDKException("Failed to initiate isotope factory: " + exception.getMessage());
            }
        }
        
    }
    
    public void generate2d(IMolecule a) throws CDKException {
    	boolean coordinates2D = true;
    	boolean coordinates3d = true;
    	
        for (int i=0; i < a.getAtomCount();i++) {
            IAtom atom = a.getAtom(i);
			if (atom.getPoint3d() == null) {
				coordinates3d = false;
			}
			if (atom.getPoint2d() == null) {
			    coordinates2D = false;
			}
        }
        if (!coordinates3d) optimize = 1;
        
        if (!coordinates2D && !coordinates3d) {
        	optimize = 1;
        	StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			sdg.setMolecule(a,false);
			try {
				sdg.generateCoordinates(new Vector2d(0,1));
			} catch (Throwable x ) {
				throw new CDKException(x.getMessage());
			}


        }
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectWriter#write(IChemObject)
     */
    public synchronized void  write(IChemObject arg0) throws CDKException {
        if (arg0 instanceof IMolecule)
	        try {
	        	IMolecule a = (IMolecule) arg0;
	            generate2d(a);
	            writer.write(getMopacCommands());

	            int formalCharge = AtomContainerManipulator.getTotalFormalCharge(a);
	            if (formalCharge != 0)
	            	writer.write(" CHARGE=" + formalCharge);	            
	            writer.newLine();
	            if (a.getProperty(AmbitCONSTANTS.NAMES) != null)
	                writer.write(a.getProperty(AmbitCONSTANTS.NAMES).toString());
	            writer.newLine();
	            writer.write(getTitle());
	            writer.newLine();
	            
	            
	            for (int i=0; i < a.getAtomCount();i++) {
	                IAtom atom = a.getAtom(i);
	    			if (atom.getPoint3d() != null) {
	    				Point3d p = atom.getPoint3d();
	    			    writeAtom(atom,p.x,p.y,p.z,optimize);
	    			} else if (atom.getPoint2d() != null) {
	    				Point2d p = atom.getPoint2d();
	    			    writeAtom(atom,p.x,p.y,0,optimize);
	    			} else
	    			    writeAtom(atom,0,0,0,1);
	            }
	            writer.write("0");
	            writer.newLine();
	                
	        } catch (IOException x) {
	            logger.error(x);
	            throw new CDKException(x.getMessage());
	        }
	    else throw new CDKException("Unsupported object!\t"+arg0.getClass().getName());    
    }
    protected void writeAtom(IAtom atom, double x, double y, double z, int optimize) throws  IOException {
 
        writer.write(atom.getSymbol());
        writer.write(blank);
        writer.write(nf.format(x));
        writer.write(blank);
        writer.write(Integer.toString(optimize));
        writer.write(blank);
        writer.write(nf.format(y));
        writer.write(blank);
        writer.write(Integer.toString(optimize));
        writer.write(blank);
        writer.write(nf.format(z));
        writer.write(blank);
        writer.write(Integer.toString(optimize));
        writer.write(blank);
        writer.newLine();
    }
 
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#close()
     */
    public void close() throws IOException {
        writer.close();

    }
    
    
    
    /**
     * @return true if to perform optimization of the structure
     */
    public synchronized int isOptimize() {
        return optimize;
    }
    /**
     * @param optimize true if to perform optimization of the structure
     */
    public synchronized void setOptimize(int optimize) {
        this.optimize = optimize;
    }
    public String toString() {
    	return "MOPAC7 format";
    }
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IMoleculeSet.class.equals(interfaces[i])) return true;
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
		return false;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.OutputStream)
     */
    public void setWriter(OutputStream writer) throws CDKException {
        setWriter(new OutputStreamWriter(writer));

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectWriter#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer) throws CDKException {
        if (this.writer != null) {
            try {
            this.writer.close();
            } catch (IOException x) {
                logger.error(x);
            }
            this.writer = null;
        }
        this.writer = new BufferedWriter(writer);
    }
    public String getTitle() {
    	return "Generated by "+getClass().getName() + " at " + new Date(System.currentTimeMillis());
    }
    public String getMopacCommands() {
    	if ((mopac_commands!= null) && ("".equals(mopac_commands))) return  default_mopac_commands[optimize];
    	else return mopac_commands;
    }
    public void setMopacCommands(String commands) {
    	this.mopac_commands = commands;
    }
}
