package test;

import com.rameses.osiris3.core.AppContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.osiris3.custom.CustomOsirisServer;
import com.rameses.osiris3.rules.RuleService;
import com.rameses.rules.common.RuleRequest;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.*;

public class RuleTest extends TestCase {
    
    private RuleService ruleSvc;
    
    public RuleTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception 
    {
        Map conf = new HashMap();
        conf.put("cluster", "osiris3"); 
        
        OsirisServer svr = new CustomOsirisServer("file:///c:/osiris3_home", conf);
        svr.start(); 
        
        AppContext appctx = svr.getContext(AppContext.class, "test"); 
        ruleSvc = appctx.getService(RuleService.class);         
    }
    
    protected void tearDown() throws Exception {}
    
    public void test1() throws Exception 
    {
        RuleRequest req = new RuleRequest("loan"); 
        List results = new ArrayList();
        Map fact1 = new HashMap();
        fact1.put("amountPaid", 75);
        fact1.put("balance", 5000);
        fact1.put("dueAmount", 50);
        fact1.put("interest", 8.35);
        //fact1.put("datePaid", "2013-02-20");
        
        req.addFact("loan", "Payment", fact1); 
        req.addGlobal("results", results);
        ruleSvc.execute(req); 
    }    
}
