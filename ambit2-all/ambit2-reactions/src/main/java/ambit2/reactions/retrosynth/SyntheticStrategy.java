package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.GenericReaction;
import ambit2.reactions.rules.IRetroSynthRuleInstance;
import ambit2.reactions.rules.SyntheticStrategyDescriptorSolver;
import ambit2.reactions.rules.scores.ReactionScore;
import ambit2.reactions.rules.scores.ReactionScoreSchema;
import ambit2.rules.weight.DescriptorWeight;

public class SyntheticStrategy 
{
	public ReactionScoreSchema reactionScoreSchema = new ReactionScoreSchema();
	public List<DescriptorWeight> productComplexityDescirptors = new ArrayList<DescriptorWeight>();
	public List<DescriptorWeight> productSimilarityDescirptors = new ArrayList<DescriptorWeight>();
	public Map<String, Double> reactionClassScores = new HashMap<String, Double>();
	public Map<String, Double> trasformTypeScores = new HashMap<String, Double>();
	
	protected SyntheticStrategyDescriptorSolver solver = new SyntheticStrategyDescriptorSolver();
	
	public static Object[] getRandomSelection(Map<GenericReaction,List<List<IAtom>>> instances)
	{
		Random rn = new Random();
		rn.setSeed(1234555);
		Set<GenericReaction> grSet = instances.keySet();
		int grNum = rn.nextInt(grSet.size());
		int n = 0;
		for (GenericReaction gr : instances.keySet())
		{
			if (n == grNum)
			{	
				List<List<IAtom>> rInst = instances.get(gr);
				int rInstNum = rn.nextInt(rInst.size());
				//System.out.println("grNum = " + grNum + "  rInstNum = " + rInstNum + " rInst.size() = " + rInst.size());
				List<IAtom> inst = rInst.get(rInstNum);
				Object obj[] = new Object[2];
				obj[0] = gr;
				obj[1] = inst;
				return obj;
			}
			else
				n++;
		}
		return null;
	}
	
	public ArrayList<IRetroSynthRuleInstance> applyStrategy(IAtomContainer target, ArrayList<IRetroSynthRuleInstance> ruleInstances)
	{
		//Currently the strategy does nothing
		//TODO
		return ruleInstances;
	}
	
	public static SyntheticStrategy getDefaultSyntheticStrategy()
	{
		SyntheticStrategy synthStrategy = new SyntheticStrategy();
		synthStrategy.reactionScoreSchema = ReactionScoreSchema.getDefaultReactionScoreSchema();	
		return synthStrategy;
	}
	
}
