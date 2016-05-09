package tests;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
import mcnet.components.IsolationResult;
import tests.examples.Scenario_1;
public class Test{
    Context ctx;
    public void resetZ3() throws Z3Exception{
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        ctx = new Context(cfg);
        
    public void printVector (Object[] array){
        int i=0;
        System.out.println( "*** Printing vector ***");
        for (Object a : array){
            i+=1;
            System.out.println( "#"+i);
            System.out.println(a);
            System.out.println(  "*** "+ i+ " elements printed! ***");
        }
        
    public void printModel (Model model) throws Z3Exception{
        for (FuncDecl d : model.getFuncDecls()){
            System.out.println(d.getName() +" = "+ d.toString());
              System.out.println("");
        }
        
    //public static void main(String[] args) throws Z3Exception{
    public void doStuff(){
        Test p = new Test();
        int k = 0;
        long t = 0;
        int result = -1;
        for(;k<10;k++){
            p.resetZ3();
            Scenario_1 model = new Scenario_1(p.ctx);
            Calendar cal = Calendar.getInstance();
            Date start_time = cal.getTime();
            IsolationResult ret =model.check.checkIsolationProperty(model.user1, model.webserver);
            Calendar cal2 = Calendar.getInstance();
            t = t+(cal2.getTime().getTime() - start_time.getTime());
            if (ret.result == Status.UNSATISFIABLE){
                System.out.println("UNSAT");
                result = 0;
            }else if (ret.result == Status.SATISFIABLE){
                System.out.println("SAT");
                result = 1;
            }else{
                System.out.println("UNPREDICTED");
                result = -1;
        }
        System.out.printf("Mean execution time user1 -> webserver: %.16f", ((float) t/(float)1000)/k);
        System.exit(result);
    }
    
    public int doStuff2(){
        Test p = new Test();
        int k = 0;
        long t = 0;
        int result = -1;
        for(;k<10;k++){
            p.resetZ3();
            Scenario_1 model = new Scenario_1(p.ctx);
            Calendar cal = Calendar.getInstance();
            Date start_time = cal.getTime();
            IsolationResult ret =model.check.checkIsolationProperty(model.user1, model.webserver);
            Calendar cal2 = Calendar.getInstance();
            t = t+(cal2.getTime().getTime() - start_time.getTime());
            if (ret.result == Status.UNSATISFIABLE){
                System.out.println("UNSAT");
                result = 0;
            }else if (ret.result == Status.SATISFIABLE){
                System.out.println("SAT");
                result = 1;
            }else{
                System.out.println("UNPREDICTED");
                result = -1;
			}
        }
        System.out.printf("Mean execution time user1 -> webserver: %.16f", ((float) t/(float)1000)/k);
        return result;
    }
}
