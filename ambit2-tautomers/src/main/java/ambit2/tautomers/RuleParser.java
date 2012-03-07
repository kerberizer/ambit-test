package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.smarts.SmartsParser;

public class RuleParser 
{
	String errors = "";
	String atomIndexCheckError = "";
	String atomIndexFixError = "";
	Rule curRule = null;
	
	public Rule parse(String ruleString)
	{	
		//System.out.println("rule: " + ruleString);
		errors = "";
		Rule rule = new Rule();
		curRule = rule;
		
		int res = ruleString.indexOf(TautomerConst.KeyWordPrefix, 0);
		int curPos = res;
		
		while (res != -1)
		{	
			res = ruleString.indexOf(TautomerConst.KeyWordPrefix, curPos+TautomerConst.KeyWordPrefix.length());
			String keyword;
			if (res == -1)
				keyword = ruleString.substring(curPos);
			else
			{	
				keyword = ruleString.substring(curPos,res);
				curPos = res;	
			}
			
			parseKeyWord(keyword);
		}			
		
		
		postProcessing();
		rule.OriginalRuleString = ruleString;
		
		//System.out.println("errors: " + errors);		
		if (errors.equals(""))
			return(rule);
		else
		{	
			//adding rule name as a prefix
			if (rule.name != null)
				errors = "'" + rule.name + "'\n" + errors;
			return(null);
		}	
	}
	
	
	void postProcessing()
	{
		if (curRule.smartsStates.length <= 1)
		{	
			errors += "Too few states. At leats two states are needed! \n";
			return;
		}	
		
		if (curRule.smartsStates.length != curRule.mobileGroupPos[0].length)
		{
			errors += "The number of states and number of group positionas are not the same!\n";
			errors += "nStates = " + curRule.smartsStates.length + ", nPos = " + curRule.mobileGroupPos[0].length;
			return;
		}
		
		curRule.stateQueries = new QueryAtomContainer[curRule.smartsStates.length];
		curRule.stateFlags = new RuleStateFlags[curRule.smartsStates.length];
		curRule.stateBonds = new RuleStateBondDistribution[curRule.smartsStates.length];
		
		SmartsParser sp = new SmartsParser();
		sp.mSupportDoubleBondAromaticityNotSpecified = true;
		boolean FlagStateSmartsOK = true; 
		
		for (int i = 0; i<curRule.smartsStates.length; i++)
		{
			QueryAtomContainer q = sp.parse(curRule.smartsStates[i]);			
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))
			{	
				errors += "Incorrect state description: " + errorMsg + "\n";
				FlagStateSmartsOK = false;
			}
			else
			{	
				sp.setNeededDataFlags();	
				RuleStateFlags flags = new RuleStateFlags();
				flags.hasRecursiveSmarts = sp.hasRecursiveSmarts;
				flags.mNeedExplicitHData = sp.needExplicitHData();
				flags.mNeedNeighbourData = sp.needNeighbourData();
				flags.mNeedParentMoleculeData = sp.needParentMoleculeData();
				flags.mNeedRingData = sp.needRingData();
				flags.mNeedRingData2 = sp.needRingData2();
				flags.mNeedValenceData = sp.needValencyData();
				RuleStateBondDistribution bdistr = new RuleStateBondDistribution();
				bdistr.calcDistribution(q, -1);
				//System.out.println("  BondDistribution:" + bdistr.toString());				
				curRule.stateQueries[i] = q;
				curRule.stateFlags[i] = flags;
				curRule.stateBonds[i] = bdistr;
			}
		}
		
		if (FlagStateSmartsOK)
			checkStateAtomsBondIndexesAndRingClosure();
	}
	
	void parseKeyWord(String keyWord)
	{		
		//System.out.println("   keyword: " + keyWord);
		int sepPos = keyWord.indexOf(TautomerConst.KeyWordSeparator);		
		if (sepPos == -1)
		{	
			errors += "Incorrect key word syntax: " + keyWord + "\n";
			return;
		}
		
		String key = keyWord.substring(TautomerConst.KeyWordPrefix.length(), sepPos).trim();		
		String keyValue = keyWord.substring(sepPos+1).trim();
		//System.out.println(">"+keyValue+"<");
		
		if (key.equals("NAME"))	
		{	
			parseName(keyValue);
			return;
		}
				
		if (key.equals("TYPE"))
		{	
			parseType(keyValue);
			return;
		}
		
		if (key.equals("GROUP"))	
		{	
			parseGroup(keyValue);
			return;
		}
		
		if (key.equals("STATES"))	
		{	
			parseStates(keyValue);
			return;
		}
		
		if (key.equals("GROUP_POS"))	
		{	
			parseGroup_Pos(keyValue);
			return;
		}
		
		if (key.equals("INFO"))	
		{	
			parseInfo(keyValue);
			return;
		}
		
		errors += "Unknow key word: " + key + "\n";
		
	}
	
	void parseName(String keyValue)
	{
		curRule.name = keyValue;
	}
	
	void parseType(String keyValue)
	{
		if (keyValue.equals("MOBILE_GROUP"))
		{	
			curRule.type = TautomerConst.RT_MobileGroup;
			return;
		}
		
		if (keyValue.equals("RING_CHAIN"))
		{	
			curRule.type = TautomerConst.RT_RingChain;
			return;
		}
		
		errors += "Unknow rule type: " + keyValue + "\n";
	}
	
	void parseGroup(String keyValue)
	{
		curRule.mobileGroup = keyValue;
		if (curRule.mobileGroup.equals("H"))
			curRule.isMobileH[0] = true;
		else
			curRule.isMobileH[0] = false;
	}
	
	
	void parseStates(String keyValue)
	{	
		String elements [] = keyValue.split(" ");
		Vector<String> vs = new Vector<String>(); 
		 
		for (int i = 0; i < elements.length; i++)
		{	
			String s = elements[i].trim();
			if(!s.equals(""))
				vs.add(s);
		}
		
		curRule.smartsStates = new String[vs.size()];
		for (int i = 0; i < vs.size(); i++)
			curRule.smartsStates[i] = vs.get(i);
	}
	
	void parseGroup_Pos(String keyValue)
	{
		Vector<String> elements = getStringElements(keyValue, TautomerConst.KeyWordElementSeparator);
		curRule.mobileGroupPos = new int[1][elements.size()]; 
		for (int i = 0; i < elements.size(); i++)
		{	
			try {
			int pos = Integer.parseInt(elements.get(i));
			curRule.mobileGroupPos[0][i]  = pos;
			}
			catch (Exception e)
			{
				errors += "Incorrect group position: " + keyValue + "\n";
			}
		}	
	}
	
	void parseInfo(String keyValue)
	{
		curRule.RuleInfo = keyValue.trim();
	}
	
	void checkStateAtomsBondIndexesAndRingClosure()
	{
		//This function checks whether all each bond with atom indexes (a1,a2)
		//from one state is present in the other state 
		//except the ring closure present
		
		//Currently this version works with two states
		//Actually at the moment rules with more than two states are not needed
		
		QueryAtomContainer q0 = curRule.stateQueries[0];
		QueryAtomContainer q1 = curRule.stateQueries[1];
		
		if (q0.getAtomCount() != q1.getAtomCount())
		{	
			errors += "The rule states have different number ot atoms: " + 
			q0.getAtomCount() + "  " +  q1.getAtomCount() + "\n";
			return;
		}
		
		if (!checkStateAtomsTypes())
			return;
		
		
		boolean FlagHasRingClosure = false;
		int closureState = 1;
		
		if (q0.getBondCount() != q1.getBondCount())
		{
			FlagHasRingClosure  = true;
			
			if (Math.abs(q0.getBondCount() - q1.getBondCount()) > 1)
			{	
				errors += "Too large difference between the number of bonds for the rule states: " + 
				q0.getBondCount() + "  " +  q1.getBondCount() + "\n";
				//return is not called in order to have additional error messages
			}
			
			if (q0.getBondCount() > q1.getBondCount())	
				closureState = 0;
		}
		
		
		QueryAtomContainer q, q_2; 
		
		if (closureState == 1)
		{
			q = q0;
			q_2 = q1;
		}
		else
		{
			q = q1;
			q_2 = q0;
		}
		
		//Register atom indexes for each bond from the 'non-closure' state
		int a0[] = new int[q.getBondCount()];
		int a1[] = new int[q.getBondCount()];
		for (int i = 0; i < q.getBondCount(); i++)
		{	
			IBond b = q.getBond(i);
			a0[i] = q.getAtomNumber(b.getAtom(0));
			a1[i] = q.getAtomNumber(b.getAtom(1));
		}
		
		int maxDiffIndexes = 0;
		int nDiffIndexes = 0; 
		if (FlagHasRingClosure)
		{	
			if (curRule.type == TautomerConst.RT_RingChain)
				maxDiffIndexes = 1;
			else
			{
				maxDiffIndexes = 0;
				errors += "This rule is not a ring_chain but it contains a ring closure.\n";
			}
		}
		
			
		int closureBondIndex = -1;
		
		//Checking the atoms indexes of the other state query (q_2)		
		for (int i = 0; i < q_2.getBondCount(); i++)
		{
			IBond b = q_2.getBond(i);
			int ind0 = q_2.getAtomNumber(b.getAtom(0));
			int ind1 = q_2.getAtomNumber(b.getAtom(1));
			
			if (! containsBondIndexes(ind0, ind1,  a0, a1))
			{
				closureBondIndex = i;
				nDiffIndexes++;
				if (nDiffIndexes > maxDiffIndexes)
				{
					errors += "The atom ndexes for bond " + (i+1) + " of state " + (closureState + 1)
					+ " does not match the indexes at the other state.\n"
					+ "This means that this state has more closures than it is allowed for this rule.\n" 
					+ "States: " + curRule.smartsStates[0] + "  " + curRule.smartsStates[1] + "\n";
				}
			}
		}
				
		if ((nDiffIndexes == 0) && (curRule.type == TautomerConst.RT_RingChain))
			errors += "This is a ring_chain rule, but no ring closures are found.\n";
	
		if ((nDiffIndexes == 1) && (curRule.type == TautomerConst.RT_RingChain))
		{
			curRule.ringClosureBondNum = closureBondIndex;
			curRule.ringClosureState = closureState;
		}
		
		
	}
	
	boolean containsBondIndexes(int ind0, int ind1,  int a0[], int a1[])
	{
		for (int i = 0; i < a0.length; i++)
		{	
			if ((ind0 == a0[i]) && (ind1 == a1[i]))
				return true;
			if ((ind1 == a0[i]) && (ind0 == a1[i]))
				return true;
		}	
		return false;
	}
	
	
	boolean checkStateAtomsTypes()
	{
		//So far this check is not so crucial.
		//Nothing is done currently.
		
		//TODO
		return true;
	}
	
	
	//This function is not used
	boolean checkAtomIndexes(QueryAtomContainer q)
	{
		//It is expected that the ring closure bond to be the last bond and
		//previous bonds to comply the rule: 
		//bond #i contains atoms with indexes i and i+1
		//
		//These indexes are expected to be in this way because of the SMARTS parser algorithm
		//and the fact that 
		//rule state definitions are written as linear simple SMARTS without branching "()" brackest  
		
		atomIndexCheckError = "";
		//Molecule with n atoms has at least n-1 bonds
		for (int i = 0; i < q.getAtomCount()-1; i++)
		{
			IBond b = q.getBond(i);
			int ind0 = q.getAtomNumber(b.getAtom(0));
			int ind1 = q.getAtomNumber(b.getAtom(1));
			
			if ((ind0 == i) && (ind1 == i+1))
				continue; //it is OK
			
			if ((ind0 == i+1) && (ind1 == i))
				continue; //it is OK
			
			//Error
			atomIndexCheckError = "bond #" + i + " has atom indexes: " + ind0 + ", " + ind1 + " ";
			return false;
		}
		
		return true;
	}
	
	
	/*
	 
	 //Index fixing is not necessary 
	 //Also this code does not work properly 
	  
	boolean fixAtomIndexes(QueryAtomContainer q)
	{
		atomIndexFixError = "";
		Vector<IBond> v = new Vector<IBond>(); 
		
		for (int i = 0; i < q.getBondCount(); i++)
			v.add(q.getBond(i));
		
		q.removeAllBonds();
		
		//Handling the first groups of bond (linear part)
		for (int i = 0; i < q.getAtomCount()-1; i++)
		{
			IBond b = getBondWithAtomIndexes(i, i+1, v, q);
			if (b == null)
				return false;
			
			v.remove(b);
			q.addBond(b);
		}
		
		//Handling the rest
		for (int i = 0; i < v.size(); i++)
			q.addBond(q.getBond(i));
		
		return true;
	}
	
	IBond getBondWithAtomIndexes(int ind0, int ind1, Vector<IBond> v, QueryAtomContainer q)
	{
		for (int i = 0; i < v.size(); i++)
		{
			IBond b = v.get(i);
			int i0 = q.getAtomNumber(b.getAtom(0));
			int i1 = q.getAtomNumber(b.getAtom(1));
			
			if ((ind0 == i0) && (ind1 == i1))
				return b;
			
			if ((ind0 == i1) && (ind1 == i0))
				return b;
		}
		return null;
	}
	
	*/
	
	
	//Helper function --------------------------------------------------
	
	Vector<String> getStringElements(String string, String separator)
	{
		Vector<String> elements = new Vector<String>();
		int curPos = 0;
		int res = string.indexOf(separator, curPos);
		
		if (res == -1)
		{	
			elements.add(string);
			return(elements);
		}
		else
		{
			String el = string.substring(curPos,res);
			elements.add(el);
			curPos = res + separator.length();
		}
			
				
		while (res != -1)
		{	
			res = string.indexOf(separator, curPos + separator.length());
						
			if (res == -1)
			{	
				String el = string.substring(curPos);
				elements.add(el);
			}	
			else
			{	
				String el = string.substring(curPos,res);
				elements.add(el);
				curPos = res + separator.length();	
			}
		}	
		return (elements);
	}
	 
	
	
}
