package ambit2.sln.dictionary;

import ambit2.sln.SLNAtom;

public class AtomDictionaryObject extends GenericDictionaryObject
{
	
	public AtomDictionaryObject(String name, String sln, SLNAtom atom) {
		this.name = name;
		this.sln = sln;
		this.atom = atom;
		makeContainerFromAtom(atom);
	}
	
	
	@Override
	public Type getObjectType() {
		return Type.ATOM;
	}

	
		
}