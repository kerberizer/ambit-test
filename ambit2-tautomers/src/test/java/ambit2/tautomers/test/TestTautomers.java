package ambit2.tautomers.test;

import java.util.List;
import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tautomers.InChITautomerGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;
import ambit2.tautomers.TautomerManager;


public class TestTautomers 
{
	public TautomerManager tman = new TautomerManager();
	public InChITautomerGenerator itg = new InChITautomerGenerator(); 
	
	
	public static void main(String[] args) throws Exception 
	{		
		TestTautomers tt = new TestTautomers();
		tt.tman.tautomerFilter.FlagApplyWarningFilter = true;
		tt.tman.tautomerFilter.FlagApplyExcludeFilter = true;
		tt.tman.tautomerFilter.FlagApplyDuplicationFilter = true;
		tt.tman.tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true;
		tt.tman.tautomerFilter.FlagApplyDuplicationCheckInChI = false;
		tt.tman.tautomerFilter.FlagFilterIncorrectValencySumStructures = true;
		tt.tman.tautomerFilter.FlagApplySimpleAromaticityRankCorrection = true;
		
		tt.tman.FlagRecurseBackResultTautomers = false;
		
		tt.tman.FlagPrintTargetMoleculeInfo = false;
		tt.tman.FlagPrintExtendedRuleInstances = true;
		tt.tman.FlagPrintIcrementalStepDebugInfo = false;
		
		tt.tman.activateChlorineRules(false);
		tt.tman.activateRingChainRules(false);
		tt.tman.use13ShiftRulesOnly(false);
		tt.tman.maxNumOfBackTracks = 10000;
		
		
		
		//tt.performTestCases();
		
		//tt.visualTest("OC=CCC(CC=CO)CCCC=O");
		//tt.test("O=CC(C)([H])C");  --> problem with the explicit H atoms 
		//tt.test("OC=NCC(CC=CO)CCCC");
		//tt.test("OC=CCCNC=O");
		//tt.testCase("OC=CCCNC=O", new String[]{"OC=CCCNC=O", "OC=CCCN=CO", "O=CCCCNC=O", "O=CCCCN=CO"} , true);
		//tt.testCloning("CC(C)C");
		//tt.test("C=C(O)N");
		//tt.test("NN=CO");
		//tt.visualTest("N=C(N)NC=O");
		//tt.visualTest("NC(C)=N");
		//tt.visualTest("C1=CN=C(N)NC1=O");
		//tt.visualTest("OC=1C=CC=CC=1");  //Kekule aromatic - !!!!
		
		//tt.visualTestInChI("o1cccc1");
		//tt.visualTestInChI("O1C=CC=C1");
		//tt.visualTestInChI("O=CCC");
		//tt.visualTestInChI("NC1=CC(N)=NC(O)=N1");
		
		//tt.visualTest("NC1=CC(N)=NC(O)=N1");
		//tt.visualTest("OC1=CC=CC=C1");
		//tt.visualTest("CC(=O)C");
		//tt.visualTest("N=NNCCC");
		//tt.visualTest("S=CNCC");
		
		//tt.visualTest("N1=NC=CC=N1");
		//tt.visualTest("N1=CC=CN=C1");
		
		//tt.visualTest("O=C1C=CC(=CC)CC1");
		
		
		//tt.visualTest("SC1=CC=CC=C1");  
		
		//tt.visualTest("CS#CO");
		//tt.visualTest("S=N1CC=CC=C1");
		
		
		//tetracyclin
		//tt.visualTest("CN(C)C1=C(O)C(C(N)=O)=C(O)C2(O)C1CC1C(=C2O)C(=O)C2=C(C=CC=C2O)C1(C)O");
		
		//tt.visualTest("S=N1CC=CC=C1");
		
		//tt.visualTest("O1=CC=CN=C1");
		
		
		//tt.visualTest("OC=1N=CN=CC=1");  //Kekule aromatic - !!!!
		
		//tt.visualTest("O=C1N=C(N=CC1)N");
		
		//tt.visualTest("C1=CN=C(N)NC1(=O)");
		
		
		//tt.visualTest("O=C1CC=CC=C1");		
		tt.visualTest("OC1=CC=CC=C1");
		
		//tt.visualTest("NC1=CC=CC=C1");
		//tt.visualTest("N=C1C=CC=CC1");
		
		
		//tt.visualTest("N=C(O)C=CN");  //two problems (1) alene atoms are obtained, (2) missing tautomers
		
		//tt.visualTest("O=CCCCC=N");
		//tt.visualTest("O=C(N)C");
		
		
		
		//tt.testAdenine();
		
		//tt.testInChIGenerator("C=CCCC");
		//tt.testInChIGenerator("CCCC=C");
		
		//tt.testInChIGenerator("C1=CC=CC=C1");
		//tt.testInChIGenerator("c1ccccc1");
		
		//tt.testInChIGenerator("N=1C=OC=CC=1");
		//tt.testInChIGenerator("N1=CO=CC=C1");
		
		
	}
	
	public void performTestCases() throws Exception
	{
		int nErrors = 0;
		
		nErrors += testCase("OC=CCCNC=O", 
				new String[]{"OC=CCCNC=O", "OC=CCCN=CO", "O=CCCCNC=O", "O=CCCCN=CO"}, false);
		
		
		System.out.println("Errors: " + nErrors);
		
	}
	
	public void test0(String smi) throws Exception
	{	
		System.out.println("Testing0(combinatorial aproach)0: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		tman.setStructure(mol);
		Vector<IAtomContainer> resultTautomers = tman.generateTautomers();			
	}
	
	
	public void test(String smi) throws Exception
	{	
		System.out.println("Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		tman.setStructure(mol);
		//Vector<IAtomContainer> resultTautomers = tman.generateTautomers();
		
		
		Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
		for (int i = 0; i < resultTautomers.size(); i++)		
			System.out.print("   " + SmartsHelper.moleculeToSMILES(resultTautomers.get(i)));
			
	}
	
	public void visualTest(String smi) throws Exception
	{
		System.out.println("Visual Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		
		
		tman.setStructure(mol);
		//Vector<IAtomContainer> resultTautomers = tman.generateTautomers();
		
		
		Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
		tman.printDebugInfo();
		
		System.out.println("\n  Result tautomers: ");
		Vector<IAtomContainer> v = new Vector<IAtomContainer>();
		v.add(mol);
		v.add(null);
		for (int i = 0; i < resultTautomers.size(); i++)		
		{	
			Double rank = (Double)resultTautomers.get(i).getProperty("TAUTOMER_RANK");
			System.out.print("   " + rank.toString() + "   " +
					SmartsHelper.moleculeToSMILES(resultTautomers.get(i)));
			v.add(resultTautomers.get(i));
		}
		System.out.println();
		
		System.out.println("Generated: " + resultTautomers.size() + " tautomers.");
		
		//preProcessStructures(v);
		TestStrVisualizer tsv = new TestStrVisualizer(v);
		
	} 
	
	
	void clearAromaticityFlags(IAtomContainer ac)
	{
		for (int i = 0; i < ac.getAtomCount(); i++)
		{
			ac.getAtom(i).setFlag(CDKConstants.ISAROMATIC, false);
		}
		
		for (int i = 0; i < ac.getBondCount(); i++)
		{
			ac.getBond(i).setFlag(CDKConstants.ISAROMATIC, false);
		}
		
	}
	
	
	void preProcessStructures(Vector<IAtomContainer> v) throws Exception
	{
		for (int i = 0; i < v.size(); i++)
		{
			IAtomContainer ac = v.get(i);
			if (ac == null)
				continue;
			
			clearAromaticityFlags(ac);
			
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
			CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
			adder.addImplicitHydrogens(ac);
			//AtomContainerManipulator.convertImplicitToExplicitHydrogens(ac);
			
			CDKHueckelAromaticityDetector.detectAromaticity(ac);
			
		}
	}
	
	
	public void visualTestInChI(String smi) throws Exception
	{
		System.out.println("Visual Testing of InChI algorithm: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);

		//Pre-processing (although aromaticity and implicit atoms should be handle from Smiles)
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(mol);
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);


		List<IAtomContainer> resultTautomers = itg.getTautomers(mol);


		System.out.println("\n  Result tautomers: ");
		for (int i = 0; i < resultTautomers.size(); i++)		
			System.out.print("   " + SmartsHelper.moleculeToSMILES(resultTautomers.get(i)));

		TestStrVisualizer tsv = new TestStrVisualizer(resultTautomers, "InChI");
		
	} 
	
	
	
	public int testCase(String smi, String expectedTautomers[], boolean FlagPrintTautomers) throws Exception
	{			
		System.out.println("Testing: " + smi);
		IMolecule mol = SmartsHelper.getMoleculeFromSmiles(smi);
		tman.setStructure(mol);
		//Vector<IAtomContainer> resultTautomers = tman.generateTautomers();
		
		
		Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
		if (FlagPrintTautomers)
			for (int i = 0; i < resultTautomers.size(); i++)		
				System.out.print("   " + SmartsHelper.moleculeToSMILES(resultTautomers.get(i)));
		
		int res = checkResultTautomerSet(resultTautomers, expectedTautomers);
		if (res == 0)
		{	
			System.out.println("Tautomers OK");
			return (0);
		}	
		else
		{	
			System.out.println("Tautomers test error = " + res);
			return (1);
		}
		
	}
	
	
	//helper utilities for the tests
	public int checkResultTautomerSet(Vector<IAtomContainer> resultStr, String expectedStr[]) throws Exception
	{
		//preProcessStructures(resultStr); this is done inside TautomerGenerattor
		
		SmartsParser sp = new SmartsParser();
		sp.mSupportDoubleBondAromaticityNotSpecified = true;
		IsomorphismTester isoTester = new IsomorphismTester();
		int nNotFound = 0;
		
		if (resultStr.size() != expectedStr.length)
			return (-1);
		
		
		int checked[] = new int[resultStr.size()];
		for (int i = 0; i < checked.length; i++)
			checked[i] = 0;
		
		for (int i = 0; i < expectedStr.length; i++)
		{	
			QueryAtomContainer query  = sp.parse(expectedStr[i]);
			sp.setNeededDataFlags();
			String errorMsg = sp.getErrorMessages();
			if (!errorMsg.equals(""))
			{
				System.out.println("Smarts Parser errors:\n" + errorMsg);
				continue;
			}						
			
			isoTester.setQuery(query);
			
			boolean FlagFound = false;
			for (int k = 0; k < resultStr.size(); k++)
			{	
				
				//The query must have the same number of atoms and bonds as the result structure
				if (resultStr.get(k).getAtomCount() != query.getAtomCount())
					continue;				
				if (resultStr.get(k).getBondCount() != query.getBondCount())
					continue;
				
				sp.setSMARTSData(resultStr.get(k));
				boolean res = isoTester.hasIsomorphism(resultStr.get(k));
				if (res)
				{
					FlagFound = true;
					checked[k]++;
					break;
				}
			}
			
			if (!FlagFound)
				nNotFound++;
		}
		
		return(nNotFound);
	}
	
	 
	public void testAdenine() throws CDKException, CloneNotSupportedException 
	{
		InChITautomerGenerator tautomerGenerator = new InChITautomerGenerator();
		IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
		IMolecule mol = builder.newInstance(IMolecule.class);
		IAtom a1 = builder.newInstance(IAtom.class,"N");
		mol.addAtom(a1);
		IAtom a2 = builder.newInstance(IAtom.class,"N");
		mol.addAtom(a2);
		IAtom a3 = builder.newInstance(IAtom.class,"N");
		mol.addAtom(a3);
		IAtom a4 = builder.newInstance(IAtom.class,"N");
		mol.addAtom(a4);
		IAtom a5 = builder.newInstance(IAtom.class,"N");
		mol.addAtom(a5);
		IAtom a6 = builder.newInstance(IAtom.class,"C");
		mol.addAtom(a6);
		IAtom a7 = builder.newInstance(IAtom.class,"C");
		mol.addAtom(a7);
		IAtom a8 = builder.newInstance(IAtom.class,"C");
		mol.addAtom(a8);
		IAtom a9 = builder.newInstance(IAtom.class,"C");
		mol.addAtom(a9);
		IAtom a10 = builder.newInstance(IAtom.class,"C");
		mol.addAtom(a10);
		IAtom a11 = builder.newInstance(IAtom.class,"H");
		mol.addAtom(a11);
		IAtom a12 = builder.newInstance(IAtom.class,"H");
		mol.addAtom(a12);
		IAtom a13 = builder.newInstance(IAtom.class,"H");
		mol.addAtom(a13);
		IAtom a14 = builder.newInstance(IAtom.class,"H");
		mol.addAtom(a14);
		IAtom a15 = builder.newInstance(IAtom.class,"H");
		mol.addAtom(a15);
		IBond b1 = builder.newInstance(IBond.class,a1, a6, IBond.Order.SINGLE);
		mol.addBond(b1);
		IBond b2 = builder.newInstance(IBond.class,a1, a9, IBond.Order.SINGLE);
		mol.addBond(b2);
		IBond b3 = builder.newInstance(IBond.class,a1, a11, IBond.Order.SINGLE);
		mol.addBond(b3);
		IBond b4 = builder.newInstance(IBond.class,a2, a7, IBond.Order.SINGLE);
		mol.addBond(b4);
		IBond b5 = builder.newInstance(IBond.class,a2, a9, IBond.Order.DOUBLE);
		mol.addBond(b5);
		IBond b6 = builder.newInstance(IBond.class,a3, a7, IBond.Order.DOUBLE);
		mol.addBond(b6);
		IBond b7 = builder.newInstance(IBond.class,a3, a10, IBond.Order.SINGLE);
		mol.addBond(b7);
		IBond b8 = builder.newInstance(IBond.class,a4, a8, IBond.Order.SINGLE);
		mol.addBond(b8);
		IBond b9 = builder.newInstance(IBond.class,a4, a10, IBond.Order.DOUBLE);
		mol.addBond(b9);
		IBond b10 = builder.newInstance(IBond.class,a5, a8, IBond.Order.SINGLE);
		mol.addBond(b10);
		IBond b11 = builder.newInstance(IBond.class,a5, a14, IBond.Order.SINGLE);
		mol.addBond(b11);
		IBond b12 = builder.newInstance(IBond.class,a5, a15, IBond.Order.SINGLE);
		mol.addBond(b12);
		IBond b13 = builder.newInstance(IBond.class,a6, a7, IBond.Order.SINGLE);
		mol.addBond(b13);
		IBond b14 = builder.newInstance(IBond.class,a6, a8, IBond.Order.DOUBLE);
		mol.addBond(b14);
		IBond b15 = builder.newInstance(IBond.class,a9, a12, IBond.Order.SINGLE);
		mol.addBond(b15);
		IBond b16 = builder.newInstance(IBond.class,a10, a13, IBond.Order.SINGLE);
		mol.addBond(b16);

		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

		List<IAtomContainer> tautomers = tautomerGenerator.getTautomers(mol);
		//Assert.assertEquals(8, tautomers.size());
		System.out.println("tautomers.size() = " + tautomers.size());
		
		TestStrVisualizer tsv = new TestStrVisualizer(tautomers, "InChI - algorithm");
		
	}	
	
	
	public void testInChIGenerator(String smi) throws Exception
	{
		System.out.println("Testing: " + smi);
		IAtomContainer mol = SmartsHelper.getMoleculeFromSmiles(smi,true);
		
		
		InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
		InChIGenerator ig = igf.getInChIGenerator(mol);
		String inchi = ig.getInchi();
		String inchiKey = ig.getInchiKey();
		System.out.println("inchi = " + inchi);
		System.out.println("inchiKey = " + inchiKey);
	}
	
	
	public int testTautomerEquivalence(String smiles)
	{
		return 0;
	}
	
	public int testTautomerEquivalence(IAtomContainer ac) throws Exception 
	{
		tman.setStructure(ac);
		Vector<IAtomContainer> initialTautomers = tman.generateTautomersIncrementaly();
		String expectedTautomers[] = new String[initialTautomers.size()];
		
		for (int i = 0; i < initialTautomers.size(); i++)		
			expectedTautomers[i] = SmartsHelper.moleculeToSMILES(initialTautomers.get(i));
		
		int nErrors = 0;
		
		for (int i = 0; i < initialTautomers.size(); i++)
		{
			tman.setStructure(initialTautomers.get(i));
			Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
			try
			{
				int res = checkResultTautomerSet(resultTautomers, expectedTautomers);
				if (res != 0)
					nErrors++;
			}
			catch(Exception e)
			{
				nErrors++;
			}
		}
		
		return (nErrors); //OK result = 0
	}
	
	
	
}
