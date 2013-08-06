/* DeeleteStructure.java
 * Author: nina
 * Date: Mar 31, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.substance.relation;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.chemrelation.AbstractUpdateStructureRelation;
import ambit2.db.search.QueryParam;

/**
 * Deletes row from substance_relation table
 * @author nina
 *
 */
public class DeleteSubstanceRelation extends AbstractUpdateStructureRelation<SubstanceRecord,StructureRecord,STRUCTURE_RELATION,Proportion> {


	public static final String[] delete_sql = {"delete from substance_relation where idsubstance=? and idchemical=? and relation=?"};

	public DeleteSubstanceRelation() {
		this(null,null,null);
	}
	public DeleteSubstanceRelation(SubstanceRecord structure1,StructureRecord structure2,STRUCTURE_RELATION relation) {
		super(structure1,structure2,relation,null);
	}
	
	public String[] getSQL() throws AmbitException {
		return delete_sql;
	}
	public void setID(int index, int id) {
		
	}
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class, getGroup().getIdsubstance()));
		params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
		params1.add(new QueryParam<String>(String.class, getRelation().name()));
		return params1;
	}
}
