package ambit2.db.substance.ids;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.IParameterizedQuery;
import ambit2.db.search.QueryParam;

public class ReadSubstanceIdentifiers extends AbstractQuery<SubstanceRecord,ExternalIdentifier,EQCondition,ExternalIdentifier> 
												implements IQueryRetrieval<ExternalIdentifier>,
												IParameterizedQuery<SubstanceRecord, ExternalIdentifier, EQCondition>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2268008311829764871L;
	private static String readSQL = "select prefix,uuid,type,id from substance_ids where prefix=? and hex(uuid)=?";
	
	@Override
	public String getSQL() throws AmbitException {
		return readSQL;
	}
	
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Substance not defined");
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		String o_uuid = getFieldname().getCompanyUUID();
		String[] uuid = {null,o_uuid};
		if (o_uuid == null) throw  new AmbitException("Substance not defined");
		uuid = I5Utils.splitI5UUID(o_uuid.toString());
		params1.add(new QueryParam<String>(String.class, uuid[0]));
		params1.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		return params1;
	}
	
	@Override
	public ExternalIdentifier getObject(ResultSet rs) throws AmbitException {
		try {
			return new ExternalIdentifier(rs.getString("type"),rs.getString("id"));
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public double calculateMetric(ExternalIdentifier object) {
		return 1;
	}
	
	
	@Override
	public void setValue(ExternalIdentifier value) {
		super.setValue(value);
	}
	
	@Override
	public void setFieldname(SubstanceRecord fieldname) {
		super.setFieldname(fieldname);
	}

}
