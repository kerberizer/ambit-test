/**
 * Created on 2005-3-21
 *
 */
package ambit2.base.data;




/**
 * A Literature entry: <br>
 * title, URL
 *   
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2008-4-20
 */
public class LiteratureEntry extends AmbitBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3089173811051934066L;
	public final static String p_title="title";
	public final static String p_url="URL";
	protected String title;
	protected String URL;
    protected int id = -1;
    protected boolean editable;
/*
	private static java.util.concurrent.CopyOnWriteArrayList<LiteratureEntry> references = 
		new CopyOnWriteArrayList<LiteratureEntry>();
		*/

	public static synchronized LiteratureEntry getInstance() {
		return getInstance("Default","http://ambit.sourceforge.net");
	}	
	public static synchronized LiteratureEntry getInstance(String name) {
		return getInstance(name,"http://ambit.sourceforge.net");
	}
	public static synchronized LiteratureEntry getCASReference() {
		return getInstance("CAS Registry Number","http://www.cas.org");
	}
	public static synchronized LiteratureEntry getIUPACReference() {
		return getInstance("IUPAC name","http://www.iupac.org");
	}	
	
	public static synchronized LiteratureEntry getEINECSReference() {
		return getInstance("EINECS","http://ec.europa.eu/environment/chemicals/exist_subst/einecs.htm");
	}
	public static synchronized LiteratureEntry getInstance(String name,String url) {
		return getInstance(name,url,-1);
	}
	public static synchronized LiteratureEntry getInstance(String name,String url, int id) {
		LiteratureEntry et = new LiteratureEntry(name,url);
		et.setId(id);
		return et;
	}	    
	public String getTitle() {
		return title;
	}

	public String getURL() {
		return URL;
	}
	/**
	 * We need separate instances for web services
	 * @param title
	 * @param url
	 */
	public LiteratureEntry(String title, String url) {
		this.title = (title.length()>255)?title.substring(1,255):title;
		this.URL = (url.length()>255)?url.substring(1,255):url;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getTitle());
		buf.append(getURL());
		return buf.toString();
	}
    public int getId() {
        return id;
    }
    public String getName() {
        return getTitle();
    }
    public boolean hasID() {
        return id > 0;
    }
    public void setId(int id) {
        this.id = id;
        
    }
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof LiteratureEntry)
    		return ((LiteratureEntry)obj).getTitle().equals(getTitle());
    	else return false;
    }
    public int hashCode() {
    	int hash = 7;
    	int var_code = (null == getName() ? 0 : getTitle().hashCode());
    	hash = 31 * hash + var_code; 

    	return hash;
    }	    


}
