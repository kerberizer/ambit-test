package ambit2.descriptors.processors;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.data.AbstractDescriptorResultType;

public class DescriptorCalculationProcessor extends
		DefaultAmbitProcessor<IAtomContainer, DescriptorValue> {
	protected IMolecularDescriptor descriptor;
	public DescriptorCalculationProcessor() {
		this(null);
	}
	public DescriptorCalculationProcessor(IMolecularDescriptor descriptor) {
		setDescriptor(descriptor);
	}

	public IMolecularDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(IMolecularDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3399104328409649302L;

	public DescriptorValue process(IAtomContainer target) throws AmbitException {
		if (descriptor == null) 
			throw new AmbitException("Undefined descriptor");
		
		try {
				return descriptor.calculate(target);
		} catch (Exception x) {
				return new DescriptorValue(
						descriptor.getSpecification(),
						new String[]{},
						new Object[]{},
						new AbstractDescriptorResultType<Exception>(x),
						new String[] {descriptor.getClass().getName()}
						);
				//throw new AmbitException(getDescriptor().toString(),x);
		
		}
	}
}
