/*
 * EchoServiceTest.java
 *
 * Created on February 24, 2013, 8:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.script.ScriptDocHelper;
import com.rameses.osiris3.script.ScriptDocHelper.ScriptName;
import com.rameses.osiris3.script.ScriptInfo;
import com.rameses.osiris3.script.ScriptService;

/**
 *
 * @author Elmo
 */
public class ScriptDocTest extends AbstractTestCase{
    
    public interface CacheTestIntf  {
        Object put(String key, Object params);
        Object get(String key);
    }
    
    protected String getRootUrl() {
        return "file:///C:/Users/Elmo/Desktop/osiris3-etracs/osiris3_home";
    }
    
    public void testDoc() throws Exception {
        AppContext ctx = server.getContext(AppContext.class, "etracs221");
        String name = null;
        for( ScriptName sn: ScriptDocHelper.getAllScripts( ctx ) ){
            System.out.println( sn.getContext() + "/" + sn + " " + sn.getUrl().getFile());
            name = sn.getName();
        }
        ScriptService ss = ctx.getService(ScriptService.class);
        ScriptInfo sinfo = ss.findScriptInfo(name);
        //System.out.println(sinfo.getStringInterface());
    }
}
