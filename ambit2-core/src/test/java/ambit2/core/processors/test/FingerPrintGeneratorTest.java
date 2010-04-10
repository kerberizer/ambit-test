package ambit2.core.processors.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;

public class FingerPrintGeneratorTest {
	@Test
	public void test() throws Exception {
		BigInteger [] expected = 
				/*{
				new BigInteger("1126174803623952"), 
				new BigInteger("1152961362175993860"), 
				new BigInteger("2305843011903291728"),
				new BigInteger("25769820160"), 
				new BigInteger("1099511627776"),
				new BigInteger("33554496"), 
				new BigInteger("8864814662912"),
				new BigInteger("4803285265416200"), 
				new BigInteger("9223372050830336000"),
				new BigInteger("2454675102173515776"),
				new BigInteger("1152921504611041536"),
				new BigInteger("155684661740175875"), 
				new BigInteger("6917529131798265856"), 
				new BigInteger("310748377023266816"),
				new BigInteger("67110400"), 
				new BigInteger("2305843009213710336")};
				*/
		{
		new BigInteger("576462128137896992"),
		new BigInteger("10376293541473171584"),
		new BigInteger("2305843009214747777"),
		new BigInteger("72127962782121984"),
		new BigInteger("52776558133248"),
		new BigInteger("0"),
		new BigInteger("338649581355264"),
		new BigInteger("4366794756"),
		new BigInteger("17730770043904"),
		new BigInteger("2314887591865356288"),
		new BigInteger("2305844109263241488"),
		new BigInteger("2400709834665132544"),
		new BigInteger("13835058125075449856"),
		new BigInteger("10741891072"),
		new BigInteger("9007208196998144"),
		new BigInteger("18014398511644672")	
		};
		
		MoleculeReader molreader = new MoleculeReader();
		FingerprintGenerator gen = new FingerprintGenerator();
		InputStream in = FingerPrintGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/core/data/fp/fptest.mol");
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			BitSet bs1 = gen.process(molreader.process(record));
			BigInteger[] h16 = new BigInteger[16];
			MoleculeTools.bitset2bigint16(bs1,64,h16);
			new Random(10000);
			BitSet bs2 = gen.process(molreader.process(record));
			Assert.assertEquals(bs1,bs2);
			MoleculeTools.bitset2bigint16(bs2,64,h16);
			for (int i=0; i <16;i++) {
				Assert.assertEquals(expected[i], h16[i]);
			}
		}
		/*
		IStructureRecord record = new StructureRecord();
		record.setContent();
		MoleculeReader reader = new MoleculeReader();
		reader.process(target)
		*/
	}
}
