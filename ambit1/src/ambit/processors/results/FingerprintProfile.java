/**
 * 
 */
package ambit.processors.results;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;

import org.openscience.cdk.interfaces.IChemObject;

import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.IAmbitResult;

/**
 * A fingerprint profile. Fingerprints are expected as a molecule property with a name {@link AmbitCONSTANTS#Fingerprint}
 * and have to be generated by {@link ambit.processors.structure.FingerprintGenerator} or directly by {@link org.openscience.cdk.fingerprint.Fingerprinter} 
 * The profile is simply a histogram with frequency [0,1] for each bit of the fingerprint.
 * @author Nina Jeliazkova
 *
 */
public class FingerprintProfile implements IAmbitResult {
	protected int length = 1024;
	protected long fpProfile[] = null;
	protected long count = 0;
	protected String title ;
	/**
	 * 
	 */
	public FingerprintProfile(String name) {
		this(name,1024);
		
	}	
	public FingerprintProfile(String name,int length) {
		super();
		this.length = length;
		fpProfile = new long[length];
		this.title = name;
	}

	/* (non-Javadoc)
	 * @see ambit.processors.IAmbitResult#update(org.openscience.cdk.interfaces.ChemObject)
	 */
	public void update(Object object) throws AmbitException {
		Object bits = null;
		if (object instanceof IChemObject)
			bits = ((IChemObject) object).getProperty(AmbitCONSTANTS.Fingerprint);
		else bits = object;
		if ((bits != null) && (bits instanceof BitSet)) {
			BitSet fp = (BitSet) bits;
			for (int j=fp.nextSetBit(0); j>= 0; j=fp.nextSetBit(j+1) )  
				fpProfile[j]++;			
			count++;
		}
	}
	public void clear() {
		for (int i=0; i < fpProfile.length; i++)
			fpProfile[i] = 0;
	}
	/**
	 * Returns a {@link BitSet} with a bit set on if the profile has frequency for that bit >= threshold 
	 * @param threshold
	 * @return BitSet
	 */
	public BitSet profileToBitSet(double threshold) {
		BitSet bs = new BitSet();
		if (count > 0)
			for (int i =0; i < fpProfile.length; i ++) {
				if (((double)fpProfile[i]/count) >= threshold) bs.set(i);
			}
		return bs;
	}
	/**
	 * 
	 * @param bins
	 * @return histogram
	 */
	public double[] getHist(int bins) {
		double freq = 0;
		double[] hist = new double[bins];
		for (int j=0; j < bins; j++) hist[0] = 0;
			
		for (int i=0; i < 1024; i++) { 
			freq = ((double)fpProfile[i]) / count;
			for (int j=(bins-1); j >=0; j--) 
				
				if (freq >= ((double) j)/bins) {
					hist[j]++; break;
				}
		}
		return hist;
	}

	public int getLength() {
		return length;
	}
	
	public double getBitFrequency(int index) {
		if (count == 0) return 0;
		if ((index >= 0) && (index < length)) 
			return ((double)fpProfile[index])/count;
		else return 0;
	}
	public String toString() {
		return title;
		/*
		if (count == 0) return "";
		StringBuffer b = new StringBuffer();
		DecimalFormat df = new DecimalFormat("###.##");
		for (int i=0; i < length; i++) {
			if (i>0) b.append(",");
			b.append(df.format(((double)fpProfile[i])/count));
		}	
		return b.toString();
		*/
	}
	
	public void write(Writer writer) throws AmbitException {
		try {
			writer.write(toString());
		} catch (IOException x) {
			throw new AmbitException(x);
		}
		
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		StringBuffer b = new StringBuffer(title);
		this.title = b.toString();
		b = null;
	}

}