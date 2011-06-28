package ambit2.rest.aa.opensso.policy;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.Request;
import org.restlet.data.Form;

import ambit2.rest.AmbitResource;
import ambit2.rest.ResourceDoc;
import ambit2.rest.admin.AdminResource;

/**
 * HTML rendering of policies
 * @author nina
 *
 */
public class PolicyHTMLReporter extends PolicyURIReporter {
	protected boolean collapsed = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1395741382892340760L;

	public PolicyHTMLReporter(Request ref, boolean collapsed,ResourceDoc doc) {
		super(ref,doc);
		this.collapsed = collapsed;
	}
	
	/*
PolicyParser parser = new PolicyParser(policies.get(input));
	 */
	
	@Override
	public void header(Writer output, Iterator<Policy> query) {
		super.header(output, query);
		
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest(),getDocumentation());
			if (collapsed) {
			output.write("<h4>Create a new policy</h4>");
			output.write("<form method='POST' action=''>");
			output.write("<table>");
			output.write("<tr>");
			output.write("<td>URI</td>");
			output.write("<td>");
			output.write(String.format("<input type=\"text\" name='uri' title='%s' size=\"60\">","URI to create policy for"));
			output.write("</td>");
			output.write("<td>");
			
			output.write("<select name='type'>");
			for (CallablePolicyCreator._type matcher : CallablePolicyCreator._type.values())
				output.write(String.format("<option title='%s' value='%s' %s>%s</option>",
						    "The policy will allow access to a "+matcher.name()+" only\n",
							matcher.toString(),
							CallablePolicyCreator._type.user.equals(matcher)?"selected":"",
							matcher));
			output.write("</select>");			
			output.write("</td>");
			output.write("<td>");
			
			output.write(String.format("<input type=\"text\" name='name' title='%s' size=\"60\">","User (OpenTox user) or group name (one of member or partner)"));
			
			output.write("</td>");
			output.write("</tr>");
			//methods
			output.write("<tr>");
			output.write("<td>Methods</td>");
			output.write("<td>");
			for (CallablePolicyCreator._method matcher : CallablePolicyCreator._method.values())
				output.write(String.format("<input type=CHECKBOX name='%s' %s>%s",matcher.name(),matcher.name(),matcher.name()));
			output.write("</td>");
			output.write("</tr>");
			output.write("<tr><td colspan='2' align='left'><input type='submit' value='Submit'></td></tr>");
			output.write("</table>");
			
			output.write("</form>");
			output.write("<hr>");
			
			Form form = request.getResourceRef().getQueryAsForm();
			String uri = form.getFirstValue("search");
			if (uri!=null)
				output.write(String.format("<h4>Policies for <a href='%s'>%s</a></h4>",uri,uri));
			}  else output.write("<h6>Use ?search=<URI> , or a search box above to retrieve policies of a given URI</h6>");
		} catch (Exception x) {
			
		} 
	}

	public void processItem(Policy item, Writer output) {
		try {
			String t = super.getURI(item);
			output.write(String.format("Policy:&nbsp;<a href='%s'>%s</a>&nbsp;", t,item.getId()));
			

			if (!collapsed) {
				output.write("<form action='?method=DELETE' method='POST'><input type='submit' value='Delete policy'></a>");
				output.write("<h3>Policy explanation</h3>");
				if (item.getXml() != null) {
					PolicyParser parser = new PolicyParser(item.getXml());
					output.write(parser.getHTML());
				}
				output.write("<h4>XML</h4>");
				output.write(String.format("<textarea rows='10' cols='80'>%s</textarea>",item.getXml()==null?"Policy XML Not retrieved!":item.getXml()));
			}
			output.write("<br>");
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	@Override
	public void footer(Writer output, Iterator<Policy> query) {
		try {
			output.write("</tbody></table>");
			
			output.write(String.format("<a href='%s/%s/%s'>Back</a>",getRequest().getRootRef(),AdminResource.resource,OpenSSOPoliciesResource.resource));
			AmbitResource.writeHTMLFooter(output, OpenSSOPoliciesResource.resource, getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}	
}
