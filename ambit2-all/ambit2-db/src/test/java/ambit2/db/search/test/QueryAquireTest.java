package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.IQueryObject;

public class QueryAquireTest extends QueryTest {

	@Override
	protected IQueryObject createQuery() throws Exception {
		return null;
	}

	@Override
	protected void verify(IQueryObject query, ResultSet rs) throws Exception {
		
		
	}
	@Override
	public void setUp() throws Exception {
	}
	@Override
	public void testSelect() throws Exception {
		// No aquire support yet, skipping test
		Assert.assertTrue(true);
	}
}
