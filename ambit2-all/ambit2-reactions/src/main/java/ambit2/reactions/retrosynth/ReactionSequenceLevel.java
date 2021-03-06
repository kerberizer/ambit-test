package ambit2.reactions.retrosynth;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.data.MoleculeTools;
import ambit2.reactions.GenericReaction;
import ambit2.reactions.retrosynth.ReactionSequence.MoleculeStatus;
import ambit2.reactions.rules.scores.ReactionScore;
import ambit2.smarts.SmartsHelper;

public class ReactionSequenceLevel 
{
	public int levelIndex = 0;
	public ReactionSequenceLevel previousLevel = null;
	public ReactionSequenceLevel nextLevel = null;
	public List<ReactionSequenceStep> steps = new ArrayList<ReactionSequenceStep>(); 
	public List<IAtomContainer> molecules = new ArrayList<IAtomContainer>();
	public List<IAtomContainer> prevLevelMolecules = new ArrayList<IAtomContainer>();
	
	public void addMolecule(IAtomContainer mol, ReactionSequenceStep step, IAtomContainer prevLevelMol)
	{
		molecules.add(mol);
		prevLevelMolecules.add(prevLevelMol);
		steps.add(step);
		if (step != null)
		{	
			step.inputMolecule = mol;
			linkStepToNextLevel(step);
		}	
	}
	
	public void associateStep(int index, ReactionSequenceStep step)
	{
		steps.set(index, step);
		step.inputMolecule = molecules.get(index);
		linkStepToNextLevel(step);
	}
	
	public void linkStepToNextLevel(ReactionSequenceStep step)
	{
		if (nextLevel == null)
			getNextLevel();
		for (int i = 0; i<step.outputMolecules.size(); i++)
			nextLevel.addMolecule(step.outputMolecules.get(i), null, step.inputMolecule);
	}
	
	public void removeMolecule(IAtomContainer mol, boolean updateUpperLevel)
	{
		//Removes the molecule, the associated step and 
		//corresponding reaction subsequence
		int index = molecules.indexOf(mol);
		molecules.remove(mol);
		prevLevelMolecules.remove(index);
		removeStep(index);
		
		if (updateUpperLevel)
		{	
			//TODO update up levels indices due to molecule removal
		}	
	}
	
	public void removeStep(int index)
	{
		//Removes the step 
		//and recursively all reaction subsequences generated by the step
		ReactionSequenceStep step = steps.get(index);
		steps.remove(index);
		
		if (step == null)
			return; //no step is associated with the molecule
		
		//Remove subsequence generated by this step in down levels
		for (int i = 0; i < step.outputMolecules.size(); i++)
			nextLevel.removeMolecule(step.outputMolecules.get(i), false);
	}
	
	public void getNextLevel()
	{
		nextLevel = new ReactionSequenceLevel();
		nextLevel.levelIndex = this.levelIndex + 1;
		nextLevel.previousLevel = this;
	}
	
	public Object[] getGenerationInfo(int molIndex)
	{
		if (prevLevelMolecules != null)
		{
			IAtomContainer genMol = prevLevelMolecules.get(molIndex);
			if (genMol == null)
				return null;
			int genMolPrevLevIndex = previousLevel.molecules.indexOf(genMol);
			Object obj[] = new Object[3];
			ReactionSequenceStep rss = previousLevel.steps.get(genMolPrevLevIndex); 
			obj[0] = rss.reaction;
			obj[1] = genMolPrevLevIndex;
			obj[2] = rss.reactionScore;
			return obj;
		}
		return null;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Level " + levelIndex + "\n");
		for (int i = 0; i < molecules.size(); i++)
		{	
			String smi = null;
			try {
				IAtomContainer mol = molecules.get(i).clone();
				MoleculeTools.convertExplicitHAtomsToImplicit(mol);
				smi = SmartsHelper.moleculeToSMILES(mol,true);
			}
			catch (Exception x) {};
			sb.append("" + smi );
			sb.append("   " + MoleculeStatus.getShortString((MoleculeStatus)
					molecules.get(i).getProperty(ReactionSequence.MoleculeStatusProperty)));
			//sb.append("       " + molecules.get(i).getProperty(ReactionSequence.MoleculeInChIKeyProperty));
			Object obj[] = getGenerationInfo(i);
			if (obj != null)
			{	
				GenericReaction genReaction = (GenericReaction)obj[0];
				sb.append("   <R" + genReaction.getExternId()+",M" + ((Integer)obj[1]+1));
				if (obj[2] != null)
				{
					ReactionScore rscore = (ReactionScore)obj[2];
					sb.append(",S" + ((Double)rscore.totalScore).intValue());
					sb.append(">");
					sb.append("     " + rscore.toStringLine());
				}
				else
					sb.append(">");
				
			}
			
			/*
			ReactionSequenceStep step = steps.get(i);
			if (step != null)
			{
				//TODO
			}
			*/
			sb.append("\n");
		}
		return sb.toString();
	}
}
