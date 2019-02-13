package facts;
import com.rameses.rules.common.FactList;
import com.rameses.rules.common.FactFilter;
import com.rameses.rules.common.FactItem;
import com.rameses.rules.common.PrimaryKeyFactFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.*;
/*
 * FactTest.java
 * JUnit based test
 *
 * Created on June 10, 2013, 12:00 PM
 */

/**
 *
 * @author Elmo
 */
public class FactTest extends TestCase {
    
    public FactTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    
    public class OverrideHigherAmount extends PrimaryKeyFactFilter {
        public OverrideHigherAmount(String keys) {
            super(keys);
        }
        public int handle(Object oldValue, Object newValue) {
            Map oldTf = (Map) oldValue;
            Map newTf = (Map) newValue;
            boolean test = Double.parseDouble(newTf.get("amount")+"")  > Double.parseDouble( oldTf.get("amount")+"");
            if( test ) {
                return FactFilter.LOG_SWAP_AND_BREAK;
            }
            else {
                return FactFilter.LOG_RETAIN_AND_BREAK;
            }
        }
    }
    
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        FactList fc = new FactList();
        //fc.setFilter( new RetainLastFactFilter("acctid,lobid") );
        fc.setFilter( new OverrideHigherAmount("acctid,lobid") );
        
        //fc.set("acctid,lobid");
        fc.add( createTaxFee("mp", "sari-sari", 300.0, "RF",1), "rule3", 1 );
        fc.add( createTaxFee("mp", "sari-sari",500.0, "RF",2), "rule1", 3 );
        fc.add( createTaxFee("mp", "sari-sari",200.0, "RF",5), "rule2", 5 );
        fc.add( createTaxFee("mp", "bakeshop",1200.0, "RF",3), "rule2", 5 );
        fc.add( createTaxFee("bp", "sari-sari",400.0, "TAX",6), "rule4", 5 );
        fc.add( createTaxFee("pf", null, 400.0, "OC",7), "rule5", 6 );
        
        
        System.out.println(fc.size());
        for(Object o: fc) {
            FactItem fh = (FactItem)o;
            System.out.println("data is " + fh.getData());
            //System.out.println("amount is ->"+((TaxFee)fh.getData()).getAmount());
            System.out.println(fh.getRulenames());
        }
        
        
        List<FactItem> list = fc.findAll( "#{accttype == 'RF'}" );
        for( FactItem f:list) {
            System.out.println("found->"+f.getData());
        }
         
        System.out.println("sorted list...");
        fc.sort("amount");
        for(Object o: fc) {
            System.out.println(((FactItem)o).getData());
        }
        System.out.println("max->"+fc.getLastItem());
        System.out.println("min->"+fc.getFirstItem());
        System.out.println("filter list greater than 400 aranged by lobid");
        FactList fl2 = fc.findAll("#{amount> 400}");
        fl2.sort("lobid");
        for(Object o: fl2) {
            System.out.println(((FactItem)o).getData());
        }
        
        
        /*
        FactGroup[] fg = fc.groupBy( "lobid");
        for(FactGroup f: fg) {
            System.out.println(f.getKeys()+" = " + f);
            for(FactItem fi: f.getItems()) {
                System.out.println("   " + fi.getData());
            }
        }
        System.out.println("********************************************");
        FactGroup[] fg1 = fc.groupBy( "acctid", "#{accttype=='RF'}");
        for(FactGroup f: fg1) {
            System.out.println(f.getKeys()+" = " + f);
            for(FactItem fi: f.getItems()) {
                System.out.println("   " + fi.getData());
            }
            System.out.println("acctid is " + f.getKey("acctid"));
            System.out.println("maximum is " + f.max("amount").getData());
            System.out.println("sum " + f.sum("amount"));
            System.out.println("emp no " + f.sum("empnum"));
        }
         */
    }
    
    private Map createTaxFee( String acctid, String lobid, double amount, String accttype, int emp) {
        Map map = new HashMap();
        map.put( "acctid", acctid );
        map.put( "lobid", lobid );
        map.put( "amount", amount );
        map.put( "accttype", accttype );
        map.put( "empnum", emp );
        return map;
    }
    
    public class TaxFee {
        private String acctid;
        private String lobid;
        private double amount;
        public TaxFee(String acctid, String lobid, double amount) {
            this.acctid = acctid;
            this.lobid = lobid;
            this.amount = amount;
        }
        
        public String getAcctid() {
            return acctid;
        }
        
        public void setAcctid(String acctid) {
            this.acctid = acctid;
        }
        
        public String getLobid() {
            return lobid;
        }
        
        public void setLobid(String lobid) {
            this.lobid = lobid;
        }
        
        public double getAmount() {
            return amount;
        }
    }
    
}
