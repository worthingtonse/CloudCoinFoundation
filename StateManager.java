import java.util.ArrayList;
import java.util.HashMap;
/**
 * Creates the state with commands, links to other states and datastores. 
 * 
 * @author Sean Worthington
 * @version 12/25/2016
 */
public class StateManager
{
    // instance variables - replace the example below with your own
    private static ArrayList<State> states; 
    public State currentState;
    
    /**
     * Constructor for objects of class StateManager. 
     * Not much goin on here. 
     */
    public StateManager( )
    {
        /* MAKE STATES  */
        states = new ArrayList<State>();
        
            State start_mode = new State("Start mode");
            
            State bank_mode = new State("Bank mode");
            String bm = "Bank mode: \n Allows you to import untrused coins and check them for authenticity (take ownership)";
            bm+= "\n Export coins as stack files (holding multiple coins) or jpegs (single coins).";
            bm+= "\n Fracked coins can be fixed.";
            bank_mode.setLongDescription(bm);
              
            State import_mode = new State("Import mode");
            String im = "Import mode:";
            
            State fracked_mode = new State("Fracked mode");
            State test_mode = new State("Test mode");
            State founder_mode = new State("Founder mode");
            
        currentState = start_mode;

        /* SET CONNECTIONS FROM MODE TO MODE */ 
        start_mode.setDoor("bank", bank_mode );
        start_mode.setDoor("founder", founder_mode );
        start_mode.setDoor("test", test_mode );
        
        bank_mode.setDoor("import", import_mode );
        bank_mode.setDoor("fracked", fracked_mode );
        bank_mode.setDoor("start", start_mode );
        
        fracked_mode.setDoor("bank", bank_mode);
        fracked_mode.setDoor("start", start_mode );
         
       import_mode.setDoor("bank", bank_mode );
        
       test_mode.setDoor("start", start_mode );
  
       founder_mode.setDoor("start", start_mode );
        
        /* START MODE */  
        start_mode.setCommand("bank mode");
        start_mode.setCommand("founder mode");
        start_mode.setCommand("test mode");
        start_mode.setCommand("quit");
        
        /* BANK MODE */  
        bank_mode.setCommand("show coins");
        bank_mode.setCommand("show raida");
        bank_mode.setCommand("import");
        bank_mode.setCommand("fracked mode");
        bank_mode.setCommand("export");
        bank_mode.setCommand("back");
        bank_mode.setCommand("quit");

        /* IMPORT MODE */   
        import_mode.setCommand("show coins");
        import_mode.setCommand("import coins");
        import_mode.setCommand("bank");
        import_mode.setCommand("escape");
        import_mode.setCommand("quit");
        
        /* FRACKED MODE */  
        fracked_mode.setCommand("fix fracked");
        fracked_mode.setCommand("bank mode");
        fracked_mode.setCommand("quit");
        
        /* TEST MODE */
       // test_mode.setCommand("load test coin");
        test_mode.setCommand("test echo");
        test_mode.setCommand("dump raida");
        test_mode.setCommand("test hints");
        test_mode.setCommand("test detect");
        test_mode.setCommand("test get_ticket");
        test_mode.setCommand("test fix");
        test_mode.setCommand("back");
        test_mode.setCommand("quit");
        
        
        /* FOUNDER MODE */
        founder_mode.setCommand("import chest");
        founder_mode.setCommand("export");
        founder_mode.setCommand("back");
        founder_mode.setCommand("quit");
    }//End constructor

     /**
     * @return the States
     */
    public static ArrayList<State> getStates() {
        return states;
    }//end get rooms

    public State getCurrentState()
    {
     return currentState;
    }//end get current room
}//end class
    
 
