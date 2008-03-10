/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/
package ambit2.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import java.util.Vector;
import java.util.Stack;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class IsomorphismTester 
{
	QueryAtomContainer query;
	IAtomContainer target;
	boolean isomorphimsFound;
	Vector<SequenceElement> sequence = new Vector<SequenceElement>();
	Vector<IAtom> sequencedAtoms = new Vector<IAtom>();
	Vector<IAtom> sequencedBondAt1 = new Vector<IAtom>();
	Vector<IAtom> sequencedBondAt2 = new Vector<IAtom>();
	
	public void setQuery(QueryAtomContainer container)
	{
		query = container;
		TopLayer.setAtomTopLayers(query, TopLayer.TLProp);
		setQueryAtomSequence(null);
	}
	
	void setQueryAtomSequence(IAtom firstAt)
	{	
		IAtom firstAtom;
		SequenceElement seqEl;
		TopLayer topLayer;
		Vector<IAtom> curAddedAtoms = new Vector<IAtom>();  
		int n;
		
		if (firstAt == null)
			firstAtom = query.getFirstAtom();
		else
			firstAtom = firstAt;
		sequence.clear();
		sequencedAtoms.clear();
		sequencedBondAt1.clear();
		sequencedBondAt2.clear();
		
		//Set first sequence atom
		sequencedAtoms.add(firstAtom);		
		seqEl = new SequenceElement();
		seqEl.center = firstAtom;
		topLayer = (TopLayer)firstAtom.getProperty(TopLayer.TLProp);
		n = topLayer.atoms.size();
		seqEl.atoms = new IAtom[n];
		seqEl.bonds = new IBond[n];
		for (int i = 0; i < n; i++)
		{
			sequencedAtoms.add(topLayer.atoms.get(i));
			seqEl.atoms[i] = topLayer.atoms.get(i);
			seqEl.bonds[i] = topLayer.bonds.get(i);
			addSeqBond(seqEl.center,seqEl.atoms[i]);
		}
		sequence.add(seqEl);
		
		//Sequencing the entire query structure
		Stack<SequenceElement> stack = new Stack<SequenceElement>();
		stack.push(seqEl);
		while (!stack.empty())
		{
			curAddedAtoms.clear();
			SequenceElement curSeqAt = stack.pop();
			for (int i = 0; i < curSeqAt.atoms.length; i++)
			{
				topLayer = (TopLayer)curSeqAt.atoms[i].getProperty(TopLayer.TLProp);
				if (topLayer.atoms.size() == 1)
					continue; // it is terminal atom and no further sequencing should be done
				int a[] = getSeqAtomsInLayer(topLayer);
				
				n = 0;
				for (int k = 0; k<a.length; k++)
					if (a[k] == 0)
						n++;
				
				if (n > 0)
				{	
					seqEl = new SequenceElement();
					seqEl.center = curSeqAt.atoms[i];
					seqEl.atoms = new IAtom[n];
					seqEl.bonds = new IBond[n];
					sequence.add(seqEl);
					stack.add(seqEl);
				}	
				
				int j = 0;				
				for (int k = 0; k < a.length; k++)
				{
					if (a[k] == 0)
					{	
						seqEl.atoms[j] = topLayer.atoms.get(k);
						seqEl.bonds[j] = topLayer.bonds.get(k);
						addSeqBond(seqEl.center,seqEl.atoms[j]);
						curAddedAtoms.add(seqEl.atoms[j]);
						j++;
					}
					else
					{	
						if (curSeqAt.center == topLayer.atoms.get(k))
							continue;
						//Check whether  bond(curSeqAt.atoms[i]-topLayer.atoms.get(k))
						//is already sequenced
						if (getSeqBond(curSeqAt.atoms[i],topLayer.atoms.get(k)) != -1)
							continue;						
						//topLayer.atoms.get(k) atom is already sequenced.
						//Therefore sequnce element of 'bond' type is registered.						
						//newSeqEl is not added in the stack (this is not needed for this bond)
						SequenceElement newSeqEl = new SequenceElement();						
						newSeqEl.center = null;
						newSeqEl.atoms = new IAtom[2];
						newSeqEl.bonds = new IBond[1];
						newSeqEl.atoms[0] = curSeqAt.atoms[i];
						newSeqEl.atoms[1] = topLayer.atoms.get(k);
						addSeqBond(newSeqEl.atoms[0],newSeqEl.atoms[1]);
						newSeqEl.bonds[0] = topLayer.bonds.get(k);
						sequence.add(newSeqEl);						
					}
				}
			}
			
			for(int i = 0; i < curAddedAtoms.size(); i++)
				sequencedAtoms.add(curAddedAtoms.get(i));
		}
	}
		
	boolean containsAtom(Vector<IAtom> v, IAtom atom)
	{
		for(int i = 0; i < v.size(); i++)
			if (v.get(i) == atom)
				return(true);
		return(false);
	}
	
	int[] getSeqAtomsInLayer(TopLayer topLayer)
	{
		int a[] = new int[topLayer.atoms.size()];
		for (int i = 0; i <topLayer.atoms.size(); i++)
		{	
			if (containsAtom(sequencedAtoms,topLayer.atoms.get(i)))
			{	
				a[i] = 1;
			}	
			else
				a[i] = 0;
		}	
		return(a);
	}
	
	void addSeqBond(IAtom at1, IAtom at2)
	{
		sequencedBondAt1.add(at1);
		sequencedBondAt2.add(at2);
	}
	
	int getSeqBond(IAtom at1, IAtom at2)
	{
		for (int i = 0; i < sequencedBondAt1.size(); i++)
		{
			if (sequencedBondAt1.get(i)==at1)
			{
				if (sequencedBondAt2.get(i)==at2)
					return(i);
			}
			else
				if (sequencedBondAt1.get(i)==at2)
				{
					if (sequencedBondAt2.get(i)==at1)
						return(i);
				}
		}
		return(-1);		
	}
	
	public boolean hasIsomorphism(IAtomContainer container)
	{	
		target = container;
		TopLayer.setAtomTopLayers(target, TopLayer.TLProp);
		executeSequence();
		return(isomorphimsFound);
	}
	
	void executeSequence()
	{	
		isomorphimsFound = false;
	}
	
	//public Vector getAllIsomorphisms(IAtomContainer container)
	//{
	//	Vector res = new Vector();
	//	return(res);
	//}
	
	public void printDebugInfo()
	{
		System.out.println("Query Atoms Topological Layers");
		for (int i = 0; i < query.getAtomCount(); i++)						
			System.out.println(""+i+"  "+
					query.getAtom(i).getProperty(TopLayer.TLProp).toString());
		
		System.out.println();
		System.out.println("Query Sequence");
		for (int i = 0; i < sequence.size(); i++)
			System.out.println(sequence.get(i).toString(query));
	}
}
