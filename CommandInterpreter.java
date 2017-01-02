import java.util.Arrays;
import java.security.SecureRandom;
import java.util.concurrent.*;
import java.util.Random;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;
//import org.json.*;
public class CommandInterpreter{
    /* Load items for all methods to share*/
    //final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();//For generating PANs (Proposed Authenticity Numbers)
    public static KeyboardReader reader = new KeyboardReader();
    public static StateManager stateManager = new StateManager();
    //private static ActivityLogger log = new ActivityLogger();
    public static Bank bank = new Bank();
    public static Random myRandom = new Random();//This is used for naming new chests
    //public static String rootFolder = "."+ File.separator + "Bank" + File.separator;
    public static String rootFolder = System.getProperty("user.dir") + File.separator +"Bank" + File.separator ;
    /* CHEST: Load variables for Chest Mode to use*/
    public static String chestTopFileName;
    public static String chestBottomFileName;
    public static int[][] topChestRegister;
    public static int[][] bottomChestRegister;
    public static String topChestTag;
    public static String bottomChestTag;
    public static CloudCoin testCoin;
    /* INCOME: Load variables for Income Mode to use*/
    public static int incomeTotalMovedToBank = 0;
    public static int incomeTotalMovedToFracked  = 0;
    public static int incomeTotalMovedToCounterfeit  = 0;
    public static int incomeTotalValueMoved = 0;
    public static String tagWhenMoving;
    public static RAIDA[] raidaArray = new RAIDA[25];
    public static String testCoinName = rootFolder + "1.CloudCoin.1.127002.test";
    public static String prompt = "Start Mode";
    public static ExecutorService executor = Executors.newFixedThreadPool(25);
    public static ExecutorService executor3 = Executors.newFixedThreadPool(3);
    public static void main(String[] args) {

        printWelcome();
        //Load up from files
        StateManager stateManager = new StateManager();
        //Start the Program. 
        run();

        System.out.println("Thank you for using CloudCoin Foundation. Goodbye.");
    }//End main

    public static void run() {
        boolean restart = false;
        //System.out.println( stateManager.currentState.getLongDescription() );
        int commandCounter = 0;
        while( ! restart )
        {
            String[] commandsAvailable = stateManager.currentState.getCommands();
            System.out.println( "=======================");
            System.out.println( prompt + " Commands Available:");
            commandCounter = 1;
            for ( String command : commandsAvailable)
            {
                System.out.println( commandCounter + ". "+ command );
                commandCounter++;
            }

            System.out.print( prompt+">");
            String commandRecieved = reader.readString( commandsAvailable );          

            switch( commandRecieved.toLowerCase() )
            {     

                case "show coins":
                    showCoins();
                break;
                
                case "export all":
                    System.out.println("Which type of coin do you want to export all of?");
                    System.out.println("1. bank (all authentic coins)");
                    System.out.println("2. lost coins");
                    System.out.println("3. Counterfeit coins");
                    System.out.println("4. Fracked Coins");
                    System.out.println("5. Income (Coins that have not been detected yet)");
                    //String[] answers = {"bank","lost","counterfeit","fracked","income"};
                    int exportAll = reader.readInt( 1, 5 );
                    System.out.println("What is the path and folder you want to store it in? eg. c:\\temp");
                    String jsonpath2 = reader.readString(false);
                     System.out.println("What tag will you add to the file?");
                     String tag2 = reader.readString(false);
                    
                    switch( exportAll ){
                        case 1: 
                            bank.exportCoins = bank.loadCoins( rootFolder, "bank");
                            bank.exportAllJson(jsonpath2, tag2, rootFolder, "bank");
                        break;
                        case 2:
                          bank.exportCoins = bank.loadCoins( rootFolder ,"lost");
                          bank.exportAllJson(jsonpath2, tag2, rootFolder, "lost");
                        break;
                        case 3: 
                         bank.exportCoins = bank.loadCoins( rootFolder ,"counterfeit");
                         bank.exportAllJson(jsonpath2, tag2, rootFolder, "counterfeit");
                        break;
                        case 4: 
                         bank.exportCoins = bank.loadCoins( rootFolder ,"fracked");
                         bank.exportAllJson(jsonpath2, tag2, rootFolder, "fracked");
                        break;
                        case 5: 
                        bank.exportCoins = bank.loadCoins( rootFolder ,"income");
                         bank.exportAllJson(jsonpath2, tag2, rootFolder, "income");
                        break;
                    }//export all
                break;
                /*EXPORT*/
                case "export":
                    // System.out.println("Root folder is " + rootFolder);
                    bank.bankedCoins = bank.loadCoins( rootFolder ,"bank");
                    int total_1 =  bank.countCoins( bank.bankedCoins, 1 );
                    int total_5 =  bank.countCoins( bank.bankedCoins, 5 );
                    int total_25 =  bank.countCoins( bank.bankedCoins, 25 );
                    int total_100 =  bank.countCoins( bank.bankedCoins, 100 );
                    int total_250 =  bank.countCoins( bank.bankedCoins, 250 );
    
                    System.out.println("Your Bank Inventory:");
                    System.out.println("  1s: "+ total_1);
                    System.out.println("  5s: "+ total_5);
                    System.out.println(" 25s: "+ total_25 );
                    System.out.println("100s: "+ total_100);
                    System.out.println("250s: "+ total_250 );
                    //get all names in the folder
                    //state how many 1, 5, 25, 100 and 250
                    int exp_1, exp_5, exp_25, exp_100, exp_250;
                    exp_1 = 0;
                    exp_5 = 0;
                    exp_25 = 0;
                    exp_100 = 0;
                    exp_250 = 0;
    
                    System.out.println("Do you want to export your CloudCoin to (1)jpgs or (2) stack (JSON) file?");
                    int file_type = reader.readInt(1,2 ); //1 jpg 2 stack
    
                    if( total_1 > 0 ){
                        System.out.println("How many 1s do you want to export?");
                        exp_1 = reader.readInt(0,total_1 );
                    }//if 1s not zero 
                    if( total_5 > 0 ){
                        System.out.println("How many 5s do you want to export?");
                        exp_5 = reader.readInt(0,total_5 );
                    }//if 1s not zero 
                    if( total_25 > 0 ){
                        System.out.println("How many 25s do you want to export?");
                        exp_25 = reader.readInt(0,total_25 );
                    }//if 1s not zero 
                    if( total_100 > 0 ){
                        System.out.println("How many 100s do you want to export?");
                        exp_100 = reader.readInt(0,total_100 );
                    }//if 1s not zero 
                    if( total_250 > 0 ){
                        System.out.println("How many 250s do you want to export?");
                        exp_250 = reader.readInt(0,total_250 );
                    }//if 1s not zero 
    
                    //move to export
                    System.out.println("What is the path and folder you want to store it in? eg. c:\\temp");
                    String jsonpath = reader.readString(false);
                    System.out.println("What tag will you add to the file?");
                    String tag = reader.readString(false);
    
                    if( file_type == 2){
                        bank.exportJson(exp_1, exp_5, exp_25, exp_100, exp_250, jsonpath, tag, rootFolder);
                        //stringToFile( json, "test.txt");
                    }else{
                        bank.exportJpeg(exp_1, exp_5, exp_25, exp_100, exp_250, jsonpath, tag, rootFolder);
    
                    }//end if type jpge or stack
    
                    System.out.println("Exporting CloudCoins Completed.");
                break;

                case "quit":
                    System.out.println("Goodbye!"); System.exit(0);
                break;

                case "show raida":
                case "test echo":
                System.out.println("\nEchoing RAIDA.");
                loadRaida();
                setRaidaStatus();
                //Get JSON from RAIDA Directory
                System.out.println("\nRAIDA Status:");
                for(int i = 0; i < 25;i++){System.out.println("RAIDA"+ i +": "+ raidaArray[i].status +", ms:" + raidaArray[i].ms );}//end for each raida status
                break;

                case "db test":
                    setRaidaTestStatus();
                    for(int i = 0; i < 25;i++){System.out.println("RAIDA"+ i +": "+ raidaArray[i].testStatus +", ms:" + raidaArray[i].ms );}//end for each raida status
                    break;
    
                    case "dump raida":
                    int dr = 0;
                    while( true )//end while keep looping
                    {
    
                        System.out.println("What RAIDA # do you want to dump? Enter 25 to end");
                        System.out.print("dump>");
                        dr = reader.readInt(0,25);
                        if( dr == 25){break;}
                        raidaArray[dr].dumpvar();
                    }
                break;

                case "test detect":
                    System.out.println("Loading test coin: " + testCoinName );
                    CloudCoin testCoinDetect = new CloudCoin( testCoinName );
                    while( true){
                        System.out.println("What RAIDA # do you want to test detection for? Enter 25 to end.");
                        System.out.print("detect>");
                        int ticketID = reader.readInt(0,25);
                        if( ticketID == 25){break;}
                        System.out.println( raidaArray[ticketID].detect( testCoinDetect ) );
                    }//end while keep looping
                break;

                case "test get_ticket":
                System.out.println("Loading test coin: " + testCoinName );
                testCoin = new CloudCoin(testCoinName );
                System.out.println("nn of test coin is: " + testCoin.nn );
                while( true){
                    System.out.println("What RAIDA # do you want to get ticket for? Enter 25 to end.");
                    System.out.print("get ticket>");
                    int ticketID = reader.readInt(0,25);
                    if( ticketID == 25){break;}
                    System.out.println( raidaArray[ticketID].get_ticket( testCoin) );
                }//end while keep looping
                break;

                case "test hints":
                    System.out.println("Loading test coin: " + testCoinName );
                    testCoin = new CloudCoin(testCoinName);
                    int hintsID = 0;
                    while( true){
                        System.out.println("What RAIDA # do you want test hints for? Enter 25 to end.");
                        System.out.print("test hints>");
                        hintsID = reader.readInt(0,25);
                        if( hintsID == 25){break;}
                        System.out.println( raidaArray[hintsID].testHint(testCoin) );
                        // System.out.println("What RAIDA # do you want to get ticket? Enter 25 to end");
                    }//end while keep looping
                break;

                case "test fix":  
    
                    while( true){
                        System.out.println("What RAIDA # do you want to fix? Enter 25 to end.");
                        System.out.print("fix>");
                        int guid_idful = reader.readInt(0,25);
                        if( guid_idful == 25){break;}
                        System.out.println("What RAIDA triad do you want to use? 1.Upper-Left, 2.Upper-Right, 3.Lower-Left, 4.Lower-Right");
                        int cornerID = reader.readInt(1,4);
                        testCoin = new CloudCoin( testCoinName );
                        FixitHelper testFUL= new FixitHelper(guid_idful, testCoin, true);
                        testFUL.setCornerToTest(cornerID);
                        boolean hasTickets =  hasTickets = getTickets( testFUL.currentTriad, testFUL.currentAns, testCoin.nn, testCoin.sn, testCoin.getDenomination() ); //This test uses coin Network number 3, sn number 3 and denomination 1 to do the test. 
                        if( hasTickets ){
                            System.out.println( raidaArray[guid_idful].fix( testFUL.currentTriad, raidaArray[testFUL.currentTriad[0]].lastTicket, raidaArray[testFUL.currentTriad[1]].lastTicket, raidaArray[testFUL.currentTriad[2]].lastTicket, testCoin.ans[guid_idful]));
    
                        }else{//No tickets, go to next triad
                            System.out.println("Trusted Servers failed to help RAIDA " + guid_idful +". Fix may still work." );
                        }//all the tickets are good. 
                    }//end while keep looping
                break;

                case "import":
                    int totalValueToBank = 0;
                    int totalValueToCounterfeit = 0;
                    int totalValueToFractured = 0;
                    int totalValueLost = 0;
    
                    System.out.println("What is the path and name of the file you want to load?");
                    String loadFileName = reader.readString( false );   
                    //load the coins into an array of coin objects
                    if( !bank.ifFileExists(loadFileName)){
                        System.out.println( loadFileName + " not found. Please check your file name and try again."); 
                        break;
                    }
    
                    String extension = "";
                    int indx = loadFileName.lastIndexOf('.');
                    if (indx > 0) {
                        extension = loadFileName.substring(indx+1);
                    }
                    extension = extension.toLowerCase();
                    boolean jpg = false;
                    if ( extension.equals("jpeg") || extension.equals("jpg")){ jpg =true;   }
    
                   // System.out.println("How do you want to import coins into your bank?");
                   // System.out.println("1. Change all Authenticity Numbers (take ownership) - high Security.");
                    //System.out.println("2. Keep all Authenticity Numbers the same (just check authenticy) - trust last owner.");
                   // int mode = reader.readInt(1,2);
    
                   // String security = "random";
                  //  switch(mode){
                   //     case 1: 
                        if( jpg ){
                            if( ! bank.loadJpeg( loadFileName )){ 
                                System.out.println("Failed to load JPEG file");
                                break;}
                        }else{
                            if( ! bank.loadIncome( loadFileName, "income")){ 
                                System.out.println("Failed to load CloudCoin file");
                                break;
                            }
                        }//end if jpg
                    
    
                    //change imported file to have a .imported extention
                    bank.renameFileExtension(loadFileName, "imported" );

                    //LOAD THE .income COINS ONE AT A TIME AND TEST THEM
                    String[] incomeFileNames  = bank.selectAllFileNamesInFolder( rootFolder, "income" );
                    //bank.importedCoins = bank.loadCoins( rootFolder, "income" );//Load Coins from hard drive into RAM
    
                    System.out.println("Loaded " + incomeFileNames.length + " income files");
                    int RAIDAHealth = 25;
                    //Now the coin has been fixed. See if there were some improvements. 
                    CloudCoin newCC;
                    for(int j = 0; j < incomeFileNames.length; j++){
                        newCC = new CloudCoin( rootFolder + incomeFileNames[j]);
                        System.out.println("Detecting SN #"+ newCC.sn +", Denomination: "+ newCC.getDenomination() );
                        detectCoin( newCC );//Checks all 25 GUIDs in the Coin and sets the status. 
                        // System.out.println("Finished detecting coin index " + j);
                        //PRINT OUT ALL COIN'S RAIDA STATUS AND SET AN TO NEW PAN
                        System.out.println("");
                        System.out.println("CloudCoin SN #"+newCC.sn +", Denomination: "+ newCC.getDenomination() );
                        RAIDAHealth = 25;
                        newCC.hp=25;
                        for(int i = 0; i < 25;i++){
                            if ( i % 5 == 0 ) { System.out.println("");}//Give every five statuses a line break
                            newCC.pastStatus[i]= raidaArray[i].lastDetectStatus;
                            if( raidaArray[i].lastDetectStatus == "pass")
                            {    
                                newCC.ans[i] = newCC.pans[i];//RAIDA health stays up
                            }
                            else if(raidaArray[i].lastDetectStatus == "fail")
                            { 
                                newCC.hp--; 
                            }
                            else{
                                RAIDAHealth--;
                            }//check if failed
                            String fi = String.format("%02d", i);//Pad numbers with two digits
                            System.out.print("RAIDA"+ fi +": "+ raidaArray[i].lastDetectStatus.substring(0,4) + " | " );
                        }//End for each cloud coin GUID statu
    
                        //SORT OUT EACH COIN INTO CATAGORIES
                        System.out.println("\nRAIDA Health: " +RAIDAHealth + "/25");
                        switch( sortCoin(newCC, RAIDAHealth )){
                            case "bank": totalValueToBank++; break;
                            case "fractured": totalValueToFractured++; break;
                            case "lost": totalValueLost++; break;
                            case "counterfeit": totalValueToCounterfeit++; break;
                        }//end switch on the place the coin will go 
                        //NOW FIX FRACTURED IF IF NEEDED
                        //  bank.loadCloudCoins("./Bank/","fractured");
                    }//end for each coin to import
                    //REPORT ON DETECTION OUTCOME
                    System.out.println("Results of Import:");
                    System.out.println("Good and Moved in Bank: "+ totalValueToBank);
                    System.out.println("Counterfeit and Moved to trash: "+totalValueToCounterfeit);
                    System.out.println("Fracked and Moved to Fracked: "+ totalValueToFractured);
                    System.out.println("Lost and Moved to Lost: "+ totalValueLost);
    
                break;

                case "fix fracked":
                    //Load coins from file in to banks fracked array
                    totalValueToBank = 0;
                    totalValueToFractured = 0;
                    totalValueLost = 0;
                    totalValueToCounterfeit=0;
                    bank.frackedCoins = bank.loadCoins( rootFolder,"fracked");
    
                    System.out.println("Loaded " + bank.frackedCoins.length + " fracked files");
                 //   System.out.println("Do you want to (1) import in mass or (2)inspect each coin that is imported?");
                  //  int inspectionMode2 = reader.readInt(1,2);
                    /* LOOP THROUGH EVERY COIN THAT IS FRACKED */
                    for(int k = 0; k < bank.frackedCoins.length; k++){
    
                        //bank.frackedCoins[k].reportStatus();
                        System.out.println("Unfracking SN #"+bank.frackedCoins[k].sn +", Denomination: "+ bank.frackedCoins[k].getDenomination() );
                        fixCoin( bank.frackedCoins[k] );//Checks all 25 GUIDs in the Coin and sets the status.
    
                        //Check CloudCoin's hp. 
                        int RAIDAHealth2 = 25;
                        //Now the coin has been fixed. See if there were some improvements. 
                        bank.frackedCoins[k].hp = 25;
                        for(int i = 0; i < 25;i++){
                            if ( i % 5 == 0 ) { System.out.println("");}//Give every five statuses a line break
                            if( bank.frackedCoins[k].pastStatus[i] == "pass")
                            {    
                                //Keep ans because it is now good
                            }
                            else if(raidaArray[i].lastDetectStatus == "fail")
                            { 
                                bank.frackedCoins[k].hp--; 
                            }
                            else
                            {
                                RAIDAHealth2--;
                            }//check if failed
                            String fi = String.format("%02d", i);//Pad numbers with two digits
                            System.out.print("RAIDA"+ fi +": "+ bank.frackedCoins[k].pastStatus[i].substring(0,4) + " | " );
                        }//end switch on the place the coin will go
                        System.out.println("\nRAIDA Health " + RAIDAHealth2 + "/25");
                        switch( sortCoin( bank.frackedCoins[k], RAIDAHealth2)){
                            case "bank": totalValueToBank++; break;
                            case "fractured": totalValueToFractured++; break;
                            case "lost": totalValueLost++; break;
                            case "counterfeit": totalValueToCounterfeit++; break;
                        }//end for each guid
    
                    }//end for each fracked coin
                    //REPORT ON DETECTION OUTCOME
                    System.out.println("Results of Fix Fractured:");
                    System.out.println("Good and Moved in Bank: "+ totalValueToBank);
                    System.out.println("Counterfeit and Moved to trash: "+totalValueToCounterfeit);
                    System.out.println("Still Fracked and Moved to Fracked: "+ totalValueToFractured);
                    System.out.println("Lost and Moved to Lost: "+ totalValueLost);
                break;
                
                
                /*IMPORT CHEST*/
                case "import chest":
                    System.out.println("What Uncirculated Chest would you like import? Include path and name like c:\\chests\\25000.CloudCoin.100UncirculatedStart16777116.Chest");
                    String path = reader.readString(false);
                    //Confirm that file exists
                    if( !bank.ifFileExists( path )){
                        System.out.println( path + " not found. Please check your file name and try again."); 
                        break;
                    }  
                    //load the coins into an array of coin objects
                    bank.loadIncome( path, "bank");//Keep means do not chage ANs, Change means use High Security, No means straight to bank no check. 
                    System.out.println("Done importing into bank");
                    //change imported file to have a .imported extention
                    bank.renameFileExtension( path, "imported" );
                break;

                /* CHANGE STATE */
                case "escape":
                case "bank mode": changeState("bank");prompt="Bank Mode";
                System.out.println("\nEchoing RAIDA.");
                loadRaida();
                setRaidaStatus();
                //Get JSON from RAIDA Directory
                System.out.println("\nRAIDA Status:");
                for(int i = 0; i < 25;i++){System.out.println("RAIDA"+ i +": "+ raidaArray[i].status +", ms:" + raidaArray[i].ms );}//end for each raida status
                break;

                case "back": changeState("start"); prompt="Start Mode"; break;

                case "fracked mode": changeState("fracked"); prompt="Fracked Mode"; 
                //System.out.println("\nEchoing RAIDA.");
                loadRaida();
                setRaidaStatus();
                //Get JSON from RAIDA Directory
                System.out.println("\nRAIDA Status:");
                for(int i = 0; i < 25;i++){System.out.println("RAIDA"+ i +": "+ raidaArray[i].status +", ms:" + raidaArray[i].ms );}//end for each raida status
                break;

                case "test mode":  changeState("test");prompt="Test Mode"; break;

                case "founder mode": changeState("founder"); prompt="Founder Mode"; break;

                default: System.out.println("Command failed. Try again."); break;

            }//end switch
        }//end while
    }//end run method

    /**
     * Print out the opening message for the player. 
     */
    public static void printWelcome() {
        System.out.println("Welcome to CloudCoin Foundation Opensource.");
        System.out.println("The Software is provided as is, with all faults, defects and errors,");
        System.out.println("and without warranty of any kind.");
    }//End print welcome

    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y
     */
    public static void loadRaida() {
        String directoryJson = "blank";
        try{
            directoryJson = getHtml("http://CloudCoin.co/servers.html");
        }catch( IOException ex ){
            System.out.println( "error " + ex );
            System.out.println("Reading cached RAIDA directory");
            try{
                directoryJson = fileToString("directory.json" );
            }catch(IOException exio){
                System.out.println("Loading the RAIDA directory failed " + exio);
                return;
            }//end catch ioexception
        }
        //Parse the json file
        //System.out.println("1." + directoryJson);

        JSONArray directoryJsonArray;
        try{
            JSONObject o = new JSONObject( directoryJson );
            directoryJsonArray = o.getJSONArray("server"); 

            for (int i = 0; i < directoryJsonArray.length(); i++) {  // **line 2**
                JSONObject childJSONObject = directoryJsonArray.getJSONObject(i);
                String url     = childJSONObject.getString("url");
                String bkurl     = childJSONObject.getString("bkurl");
                String name = childJSONObject.getString("name");
                String status     = childJSONObject.getString("status");
                int ms     = childJSONObject.getInt("ms");
                String ext     = childJSONObject.getString("ext");
                String location     = childJSONObject.getString("location");
                String img     = childJSONObject.getString("img");
                String protocol = childJSONObject.getString("protocol");
                int port     = childJSONObject.getInt("port");
                raidaArray[i] = new RAIDA( url, bkurl, name, status, ms, ext, location, img, protocol, port);
            }   
        }catch(JSONException e){
            System.out.println("Json array error: " + e);
        }

    }

    public static boolean getTickets( int[] triad, String[] ans, int nn, int sn, int denomination ){
        // String echo1 = raidaArray[0].echo());
        //create a callable for each method
        Callable<Void> callable0 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[triad[0]].get_ticket( ans[0],nn,sn,denomination );
                    //System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable1 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[triad[1]].get_ticket( ans[1],nn,sn, denomination );
                    // System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable2 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[triad[2]].get_ticket( ans[2],nn,sn, denomination );
                    //System.out.print(".");
                    return null;
                }
            };

        //add to a list
        List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
        taskList.add(callable0);
        taskList.add(callable1);
        taskList.add(callable2);
        //create a pool executor with 3 threads
        //ExecutorService executor3 = Executors.newFixedThreadPool(3);

        try
        {
            //start the threads
            List<Future<Void>> futureList = executor3.invokeAll(taskList);

            for(Future<Void> voidFuture : futureList)
            {
                try
                {
                    //check the status of each future.  get will block until the task
                    //completes or the time expires
                    voidFuture.get(100, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException e)
                {
                    System.out.println("Error executing task " + e.getMessage());
                }
                catch (TimeoutException e)
                {
                    System.out.println("Timed out executing task" + e.getMessage());
                }

            }

        }
        catch (InterruptedException ie)
        {
            //do something if you care about interruption;
        }
        //Check that all ticket status are good
        if ( raidaArray[triad[0]].lastTicketStatus.equalsIgnoreCase("ticket") && raidaArray[triad[1]].lastTicketStatus.equalsIgnoreCase("ticket") && raidaArray[triad[2]].lastTicketStatus.equalsIgnoreCase("ticket") )
        {
            return true;
        }else{
            return false;
        }

    }//end get Ticket

    public static void detectCoin( CloudCoin newCoin ){
        // String echo1 = raidaArray[0].echo());
        //create a callable for each method
        Callable<Void> callable0 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[0].detect( newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable1 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[1].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable2 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[2].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable3 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[3].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable4 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[4].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable5 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[5].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable6 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[6].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable7 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[7].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable8 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[8].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable9 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[9].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable10 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[10].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable11 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[11].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable12 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[12].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable13 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[13].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable14 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[14].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable15 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[15].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable16 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[16].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable17 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[17].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable18 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[18].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable19 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[19].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable20 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[20].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable21 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[21].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable22 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[22].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable23 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[23].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable24 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[24].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        //add to a list
        List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
        taskList.add(callable0);
        taskList.add(callable1);
        taskList.add(callable2);
        taskList.add(callable3);
        taskList.add(callable4);
        taskList.add(callable5);
        taskList.add(callable6);
        taskList.add(callable7);
        taskList.add(callable8);
        taskList.add(callable9);
        taskList.add(callable10);
        taskList.add(callable11);
        taskList.add(callable12);
        taskList.add(callable13);
        taskList.add(callable14);
        taskList.add(callable15);
        taskList.add(callable16);
        taskList.add(callable17);
        taskList.add(callable18);
        taskList.add(callable19);
        taskList.add(callable20);
        taskList.add(callable21);
        taskList.add(callable22);
        taskList.add(callable23);
        taskList.add(callable24);

        //create a pool executor with 25 threads
        //ExecutorService executor = Executors.newFixedThreadPool(25);

        try
        {
            //start the threads
            List<Future<Void>> futureList = executor.invokeAll(taskList);

            for(Future<Void> voidFuture : futureList)
            {
                try
                {
                    //check the status of each future.  get will block until the task
                    //completes or the time expires
                    voidFuture.get(100, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException e)
                {
                    System.out.println("Error executing task " + e.getMessage());
                }
                catch (TimeoutException e)
                {
                    System.out.println("Timed out executing task" + e.getMessage());
                }

            }

        }
        catch (InterruptedException ie)
        {
            //do something if you care about interruption;
        }

    }//end detect

    /***
     * This sends an echo to each RAIDA server and records the results.
     */
    public static void setRaidaTestStatus(){
        // String echo1 = raidaArray[0].echo());
        //create a callable for each method
        Callable<Void> callable0 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[0].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable1 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[1].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable2 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[2].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable3 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[3].test();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable4 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[4].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable5 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[5].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable6 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[6].test();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable7 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[7].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable8 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[8].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable9 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[9].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable10 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[10].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable11 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[11].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable12 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[12].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable13 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[13].test();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable14 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[14].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable15 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[15].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable16 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[16].test();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable17 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[17].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable18 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[18].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable19 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[19].test();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable20 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[20].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable21 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[21].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable22 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[22].test();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable23 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[23].test();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable24 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[24].test();
                    System.out.print(".");
                    return null;
                }
            };

        //add to a list
        List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
        taskList.add(callable0);
        taskList.add(callable1);
        taskList.add(callable2);
        taskList.add(callable3);
        taskList.add(callable4);
        taskList.add(callable5);
        taskList.add(callable6);
        taskList.add(callable7);
        taskList.add(callable8);
        taskList.add(callable9);
        taskList.add(callable10);
        taskList.add(callable11);
        taskList.add(callable12);
        taskList.add(callable13);
        taskList.add(callable14);
        taskList.add(callable15);
        taskList.add(callable16);
        taskList.add(callable17);
        taskList.add(callable18);
        taskList.add(callable19);
        taskList.add(callable20);
        taskList.add(callable21);
        taskList.add(callable22);
        taskList.add(callable23);
        taskList.add(callable24);

        //create a pool executor with 3 threads
        //ExecutorService executor = Executors.newFixedThreadPool(25);

        try
        {
            //start the threads
            List<Future<Void>> futureList = executor.invokeAll(taskList);

            for(Future<Void> voidFuture : futureList)
            {
                try
                {
                    //check the status of each future.  get will block until the task
                    //completes or the time expires
                    voidFuture.get(100, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException e)
                {
                    System.out.println("Error executing task " + e.getMessage());
                }
                catch (TimeoutException e)
                {
                    System.out.println("Timed out executing task" + e.getMessage());
                }

            }

        }
        catch (InterruptedException ie)
        {
            //do something if you care about interruption;
        }

    }

    /***
     * This sends an echo to each RAIDA server and records the results.
     */
    public static void setRaidaStatus(){
        // String echo1 = raidaArray[0].echo());
        //create a callable for each method
        Callable<Void> callable0 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[0].echo();
                    System.out.print(".0");
                    return null;
                }
            };

        Callable<Void> callable1 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[1].echo();
                    System.out.print(".1");
                    return null;
                }
            };

        Callable<Void> callable2 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[2].echo();
                    System.out.print(".2");
                    return null;
                }
            };

        Callable<Void> callable3 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[3].echo();
                    System.out.print(".3");
                    return null;
                }
            };
        Callable<Void> callable4 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[4].echo();
                    System.out.print(".4");
                    return null;
                }
            };

        Callable<Void> callable5 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[5].echo();
                    System.out.print(".5");
                    return null;
                }
            };

        Callable<Void> callable6 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[6].echo();
                    System.out.print(".6");
                    return null;
                }
            };
        Callable<Void> callable7 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[7].echo();
                    System.out.print(".7");
                    return null;
                }
            };

        Callable<Void> callable8 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[8].echo();
                    System.out.print(".8");
                    return null;
                }
            };

        Callable<Void> callable9 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[9].echo();
                    System.out.print(".9");
                    return null;
                }
            };

        Callable<Void> callable10 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[10].echo();
                    System.out.print(".10");
                    return null;
                }
            };

        Callable<Void> callable11 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[11].echo();
                    System.out.print(".11");
                    return null;
                }
            };

        Callable<Void> callable12 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[12].echo();
                    System.out.print(".12");
                    return null;
                }
            };

        Callable<Void> callable13 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[13].echo();
                    System.out.print(".13");
                    return null;
                }
            };
        Callable<Void> callable14 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[14].echo();
                    System.out.print(".14");
                    return null;
                }
            };

        Callable<Void> callable15 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[15].echo();
                    System.out.print(".15");
                    return null;
                }
            };

        Callable<Void> callable16 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[16].echo();
                    System.out.print(".16");
                    return null;
                }
            };
        Callable<Void> callable17 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[17].echo();
                    System.out.print(".17");
                    return null;
                }
            };

        Callable<Void> callable18 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[18].echo();
                    System.out.print(".18");
                    return null;
                }
            };

        Callable<Void> callable19 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[19].echo();
                    System.out.print(".19");
                    return null;
                }
            };
        Callable<Void> callable20 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[20].echo();
                    System.out.print(".20");
                    return null;
                }
            };

        Callable<Void> callable21 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[21].echo();
                    System.out.print(".21");
                    return null;
                }
            };

        Callable<Void> callable22 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[22].echo();
                    System.out.print(".22");
                    return null;
                }
            };

        Callable<Void> callable23 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[23].echo();
                    System.out.print(".23");
                    return null;
                }
            };
        Callable<Void> callable24 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[24].echo();
                    System.out.print(".24");
                    return null;
                }
            };

        //add to a list
        List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
        taskList.add(callable0);
        taskList.add(callable1);
        taskList.add(callable2);
        taskList.add(callable3);
        taskList.add(callable4);
        taskList.add(callable5);
        taskList.add(callable6);
        taskList.add(callable7);
        taskList.add(callable8);
        taskList.add(callable9);
        taskList.add(callable10);
        taskList.add(callable11);
        taskList.add(callable12);
        taskList.add(callable13);
        taskList.add(callable14);
        taskList.add(callable15);
        taskList.add(callable16);
        taskList.add(callable17);
        taskList.add(callable18);
        taskList.add(callable19);
        taskList.add(callable20);
        taskList.add(callable21);
        taskList.add(callable22);
        taskList.add(callable23);
        taskList.add(callable24);

        //create a pool executor with 3 threads
        //ExecutorService executor = Executors.newFixedThreadPool(25);

        try
        {
            //start the threads
            List<Future<Void>> futureList = executor.invokeAll(taskList);

            for(Future<Void> voidFuture : futureList)
            {
                try
                {
                    //check the status of each future.  get will block until the task
                    //completes or the time expires
                    voidFuture.get(100, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException e)
                {
                    System.out.println("Error executing task " + e.getMessage());
                }
                catch (TimeoutException e)
                {
                    System.out.println("Timed out executing task" + e.getMessage());
                }

            }

        }
        catch (InterruptedException ie)
        {
            //do something if you care about interruption;
        }

    }

    public static void showRaidaStatus(){
        for(int i =0; i<25; i++){
            System.out.println("Raida "+ i + " status:" +  raidaArray[i].status + ". " + raidaArray[i].ms + " ms "  );
        }//end for
    }//end show raida status

    public static void showRaidaTestStatus(){
        for(int i =0; i<25; i++){
            System.out.println("Raida "+ i + " status:" +  raidaArray[i].testStatus + ". " + raidaArray[i].ms + " ms "  );
        }//end for
    }//end show raida status

    public static void changeState( String newState )
    {
        State nextState = stateManager.currentState.getExit( "bank" );
        switch( newState ){

            case "bank":  nextState = stateManager.currentState.getExit( "bank" );  
            //Look and see if there are income files. 
            //If yes ask the person if they want to finnish importing them. (Give them a command)
            System.out.println("Bank Mode:");
            System.out.println("Take ownership of other people's CloudCoins.");
            System.out.println("Fix fractured coins.");
            System.out.println("Export coins.");
            showCoins();

            break;
            case "import":  nextState = stateManager.currentState.getExit( "import" ); break;
            case "start":  nextState = stateManager.currentState.getExit( "start" );break;
            case "test":  nextState = stateManager.currentState.getExit( "test" );
            System.out.println("\nEchoing RAIDA.");
            loadRaida();
            setRaidaStatus();
            //Get JSON from RAIDA Directory
            System.out.println();
            for(int i = 0; i < 25;i++){System.out.println("RAIDA"+ i +": "+ raidaArray[i].status +", ms:" + raidaArray[i].ms );}//end for each raida status
            break;
            case "founder":  nextState = stateManager.currentState.getExit( "founder" ); break;
            case "fracked":  nextState = stateManager.currentState.getExit( "fracked" ); break;
        }//end switch
        stateManager.currentState = nextState;
    }//end changeState
/*
    public static String[] generatePANs( ){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        String[] pans = new String[25];
        for(int i = 0; i < 25; i++){
            random.nextBytes(bytes);
            pans[i] = bytesToHex( bytes );
        }//end for 25 pans
        return pans;
    }//end count bank

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
*/
    public static void fixCoin( CloudCoin brokeCoin ){
        //Make an array of broken coins or go throug each if broken fix
        int mode = 1;
        boolean hasTickets = false;
        String fix_result = "";

        //brokeCoin.reportStatus();

        for (int guid_id = 0; guid_id < 25; guid_id++  ){//Check every Guid in the cloudcoin to see if it is fractured
            //  System.out.println("Inspecting RAIDA guid " + guid_id );

            FixitHelper fixer;
            if( brokeCoin.pastStatus[guid_id].equalsIgnoreCase("fail")){//This guid has failed, get tickets
                System.out.println("RAIDA" +guid_id +" failed." );
                fixer = new FixitHelper( guid_id, brokeCoin );
                //fixer.reportCoinStatus();
                mode = 1;
                hasTickets = false;
                while( ! fixer.finnished ){

                    if( fixer.currentTriadReady ){
                        hasTickets = getTickets( fixer.currentTriad, fixer.currentAns, brokeCoin.nn, brokeCoin.sn, brokeCoin.getDenomination() ); 
                        if( hasTickets ){
                            fix_result = raidaArray[guid_id].fix( fixer.currentTriad, raidaArray[fixer.currentTriad[0]].lastTicket, raidaArray[fixer.currentTriad[1]].lastTicket, raidaArray[fixer.currentTriad[2]].lastTicket, brokeCoin.ans[guid_id]);
                            if( fix_result.equalsIgnoreCase("success")){
                                //Save pan to an, stop looping, report sucess. 
                                brokeCoin.pastStatus[guid_id] = "pass";
                                // brokeCoin.ans[guid_id] = brokeCoin.pans[guid_id];
                                //The CloudCoin an does not change. The RAIDA's an changes. No need to save the pan. 
                                fixer.finnished = true;
                                System.out.println("GUID fixed for guid " + guid_id );

                            }else{
                                System.out.println("Fix it command failed for guid  " + guid_id );
                                mode++;//beed to try another corner
                                fixer.setCornerToCheck( mode );
                            }//end if success fixing

                        }else{//No tickets, go to next triad

                            System.out.println("Get ticket commands failed for guid " + guid_id );
                            mode++;
                            fixer.setCornerToCheck( mode );
                        }//all the tickets are good. 
                    }else{//Triad will not work change it 
                        System.out.println("Trusted triad "+ mode + " is not able to help: " + brokeCoin.pastStatus[fixer.currentTriad[0]] +", "+brokeCoin.pastStatus[fixer.currentTriad[1]]+", "+brokeCoin.pastStatus[fixer.currentTriad[2]]);
                        mode++;
                        fixer.setCornerToCheck( mode );
                    }//end if traid is ready
                }//end while still trying to fix
                //Finnished fixing 
            }//end if guid is broken and needs to be fixed
        }//end for each guid
    }//end fix coin

    public static String getHtml(String url_in) throws MalformedURLException, IOException {
        int c;
        URL cloudCoinGlobal = new URL(url_in);
        URLConnection conn = cloudCoinGlobal.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        InputStream input = conn.getInputStream();

        StringBuilder sb = new StringBuilder();

        while((( c = input.read()) != -1))
        {
            sb.append((char)c); 
        }//end while   
        input.close();
        return sb.toString();
    }//end get url

    public static String fileToString(String pathname) throws IOException {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }

        } finally {
            scanner.close();
        }
        return fileContents.toString();
    }

    public static void showCoins(){
        //Get JSON from RAIDA Directory
        //for(int i = 0; i < 25;i++){System.out.println("RAIDA"+ i +": "+ raidaArray[i].status +", ms:" + raidaArray[i].ms );}//end for each raida status
        int total_1 =  0;
        int total_5 =  0;
        int total_25 = 0;
        int total_100 =  0;
        int total_250 =  0;

        //System.out.println("'\nRoot folder is " + rootFolder);
        bank.bankedCoins = bank.loadCoins( rootFolder ,"bank");
        total_1 =  bank.countCoins( bank.bankedCoins, 1 );
        total_5 =  bank.countCoins( bank.bankedCoins, 5 );
        total_25 =  bank.countCoins( bank.bankedCoins, 25 );
        total_100 =  bank.countCoins( bank.bankedCoins, 100 );
        total_250 =  bank.countCoins( bank.bankedCoins, 250 );

        System.out.println("Your Bank Inventory:");
        System.out.print("  1s: "+ total_1  +" || ");
        System.out.print("  5s: "+ total_5  +" ||");
        System.out.print(" 25s: "+ total_25 +" ||" );
        System.out.print("100s: "+ total_100+" ||");
        System.out.println("250s: "+ total_250 );

        bank.frackedCoins = bank.loadCoins( rootFolder ,"fracked");
        if( bank.frackedCoins.length > 0 ){
            total_1 =  bank.countCoins( bank.frackedCoins, 1  );
            total_5 =  bank.countCoins( bank.frackedCoins, 5  );
            total_25 =  bank.countCoins( bank.frackedCoins, 25  );
            total_100 =  bank.countCoins( bank.frackedCoins, 100  );
            total_250 =  bank.countCoins( bank.frackedCoins, 250  );

            System.out.println("Your fracked Inventory:");
            System.out.print("  1s: "+ total_1  +" || ");
            System.out.print("  5s: "+ total_5  +" ||");
            System.out.print(" 25s: "+ total_25 +" ||" );
            System.out.print("100s: "+ total_100+" ||");
            System.out.println("250s: "+ total_250 );

        }else{
            System.out.println("No fractured coins. " );
        }
        //if has fracked coins
        //get all names in the folder
        //state how many 1, 5, 25, 100 and 250
        bank.counterfeitCoins = bank.loadCoins( rootFolder ,"lost");
        if( bank.counterfeitCoins.length > 0 ){
            total_1 =  bank.countCoins( bank.counterfeitCoins, 1 );
            total_5 =  bank.countCoins( bank.counterfeitCoins, 5 );
            total_25 =  bank.countCoins( bank.counterfeitCoins, 25 );
            total_100 =  bank.countCoins( bank.counterfeitCoins, 100 );
            total_250 =  bank.countCoins( bank.counterfeitCoins, 250 );

            System.out.println("Your lost Inventory:");
            System.out.print("  1s: "+ total_1  +" || ");
            System.out.print("  5s: "+ total_5  +" ||");
            System.out.print(" 25s: "+ total_25 +" ||" );
            System.out.print("100s: "+ total_100+" ||");
            System.out.println("250s: "+ total_250 );

        }else{
            System.out.println("No lost coins. " );
        }
        bank.counterfeitCoins = bank.loadCoins( rootFolder ,"counterfeit");
        if( bank.counterfeitCoins.length > 0 ){
            total_1 =  bank.countCoins( bank.counterfeitCoins, 1 );
            total_5 =  bank.countCoins( bank.counterfeitCoins, 5 );
            total_25 =  bank.countCoins( bank.counterfeitCoins, 25 );
            total_100 =  bank.countCoins( bank.counterfeitCoins, 100 );
            total_250 =  bank.countCoins( bank.counterfeitCoins, 250 );

            System.out.println("Your fracked Inventory:");
            System.out.print("  1s: "+ total_1  +" || ");
            System.out.print("  5s: "+ total_5  +" ||");
            System.out.print(" 25s: "+ total_25 +" ||" );
            System.out.print("100s: "+ total_100+" ||");
            System.out.println("250s: "+ total_250 );

        }else{
            System.out.println("No counterfeit coins. " );
        }
        //write file name to bank

    }//end show coins

    public static String sortCoin( CloudCoin coin, int RAIDAHealth ){
        String returnString = "";
        coin.calculateHP();
        String grade = coin.gradeCoin();
        System.out.println("\nResults:" + grade );
        System.out.println("Health Points are: " + coin.hp +"/25");
        coin.calcExpirationDate();
        System.out.println("Expiration Data is " + coin.ed);
   
            //SORT OUT EACH COIN INTO CATAGORIES
            System.out.println("HP is: " + coin.hp );
            if( coin.hp > 24 && RAIDAHealth > 11 ){//No Problems Move to Bank
                coin.saveCoin("bank");
                coin.deleteCoin(rootFolder, "fracked");//The coin is being brought in from fracked or income
                coin.deleteCoin(rootFolder, "income");
                returnString ="bank";
            }
            else if( coin.hp > 9 )
            {//Can be fixed
                coin.saveCoin("fracked");
                coin.deleteCoin(rootFolder, "fracked");
                coin.deleteCoin(rootFolder, "income");
                returnString ="fracked";
                //greater than 20, send to bank
            } else if( coin.hp > 1) {//Lost coin
                coin.saveCoin("lost");
                coin.deleteCoin(rootFolder,"fracked");
                coin.deleteCoin(rootFolder,"income");//Could be comming from fracked or income
                returnString ="lost";
            }else{ //Counterfeit - send to counterfeit
                coin.saveCoin("counterfeit");
                coin.deleteCoin(rootFolder,"fracked");
                coin.deleteCoin(rootFolder,"income");//Could be comming from fracked or income
                returnString ="counterfeit";
            }
            
        return returnString;
    }//end grade coin
}//EndMain
