import java.security.SecureRandom;
import java.io.*;
import java.util.Scanner;
import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
/**
 * Creats a CloudCoin
 * 
 * @author Sean H. Worthington
 * @version 12/29/2016
 */
public class CloudCoin
{
    // instance variables - replace the example below with your own
    /***
     * Network Number. Should be 1 unless we add more networks
     */
    public int nn;//Network Numbers
    /***
     * Serial Number 1 - 16,777,216
     */
    public int sn;//Serial Number
    /**
     * Authenticity Numbers. 25 GUIDs without hyphens
     */
    public String[] ans = new String[25] ;//Authenticity Numbers
    /**
     * Proposed Authenticity Numbers. 25 GUIDs without hyphens that will replace the ANs
     */
    public String[] pans = new String[25] ;//Proposed Authenticty Numbers
  /**
   * This is the status of the last detection attempt for each of the 25 RAIDA: pass, fail, error or unknown
   */
    public String[] pastStatus = new String[25] ;//fail, pass, error, unknown (could not connect to raida)
    /**
     * Expiration date: two years from last detection
     */
    public String ed; //Expiration Date expressed as a hex string like 97e2 Sep 2016
    /**
     * Expiration date in the form of hexidecimal numbers to be stored in jpgs. First number is month. 
     * last three numbers are the year. 
     */
    public String edHex;//ed in hex form. 
    /**
     * Health or Hit Points. (1-25, One point for each server not failed). Every time a RAIDA says it is counterfeit the HP goes down.
     */
    public int hp;//HitPoints (1-25, One point for each server not failed)
    /**
     * Added Owner Indexed Data: The owner of the coin can use this space to put an array of their own data. 
     */
    public String[] aoid = new String[1] ;//Account or Owner ID
    /**
     * What the file should be named useing the .stack naming standard: CloudCoin JSON File Naming Convention: Total Amount of CloudCoins in file, ".CloudCoin" , then a random number or use defined tab in case there are other stack/chests with the same amount of CloudCoins separated by dots. The extension could be Chest or stack. Example: 12750.cloudcoin.mytag.stack. Stack files are for the public. Chest files are for founders uncirculated coins. 12750.cloudcoin.userTagHere.chest If the stack is full of counterfeit CloudCoins then it may have a .counterfeit extension. If the stack is full of lost coins then it may have the .lost extension. If the stack has a .fracked extension, it is full of fracked coins.
     */
    public String fileName;
    public String json;
    public byte[] jpeg;
    public static final int YEARSTILEXPIRE = 2;
    

    /**
     * Constructor for objects of class CloudCoin
     */
    public CloudCoin(int nn, int sn, String[] ans, String ed, String[] aoid  )
    { // initialise instance variables
        this.nn = nn;
        this.sn = sn;     
        this.ans = ans;
        for(int i = 0; i < 25; i++ ){
            this.pans[i] = generatePan();//Generate random numbers by default
            this.pastStatus[i]= "unknown";
        }
        this.ed = ed;
        this.hp = 25;
        this.aoid = aoid;
        this.fileName = getDenomination() +".CloudCoin." + this.nn +"."+ this.sn + ".";
        this.json = "";
        this.jpeg = null;
    }

    public CloudCoin( String loadFilePath ){
        //type s for json string, i for image  // System.out.println("Loading file: "+ fileName);
        String extension = "";
        for(int i = 0; i < 25; i++ ){
            this.pans[i] = generatePan();//Generate random numbers by default
            this.pastStatus[i]= "unknown";
        }

        int indx = loadFilePath.lastIndexOf('.');
        if (indx > 0) {
            extension = loadFilePath.substring(indx+1);
        }
        extension = extension.toLowerCase();
        boolean jpg = false;
        if ( extension.equals("jpeg") || extension.equals("jpg")){ jpg = true;   }

        if( !jpg ){//Json 
            String incomeJson = ""; 
            try{ incomeJson = loadJSON( loadFilePath ); }catch( IOException ex ){  System.out.println( "error " + ex );
            }
            JSONArray incomeJsonArray;
            try{  
                JSONObject o = new JSONObject( incomeJson );
                incomeJsonArray = o.getJSONArray("cloudcoin");
                //this.newCoins = new CloudCoin[incomeJsonArray.length()];
                for (int i = 0; i < incomeJsonArray.length(); i++) {  // **line 2**
                    JSONObject childJSONObject = incomeJsonArray.getJSONObject(i);
                    this.nn     = childJSONObject.getInt("nn");
                    this.sn     = childJSONObject.getInt("sn");
                    JSONArray an = childJSONObject.getJSONArray("an");
                    this.ans = toStringArray(an);
                    String ed     = childJSONObject.getString("ed");
                    JSONArray aoid     = childJSONObject.getJSONArray("aoid");
                    this.aoid = toStringArray(aoid);
                }//end for each coin
            }catch( JSONException ex){
                System.out.println("Error: " + ex);
            }//try */
        }else{//jpeg image
            FileInputStream fis;
            int y = 0;
            char c;
            byte[] jpegHeader = new byte[455];
            String wholeString ="";

            try {
                fis = new FileInputStream( loadFilePath );
                y=fis.read(jpegHeader);// read bytes to the buffer
                wholeString = toHexadecimal( jpegHeader );
                fis.close(); 
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int startAn, endAn;
            startAn = 40; endAn = 72;
            for(int i = 0; i< 25; i++){
                ans[i] = wholeString.substring( startAn +(i*32), endAn +(i*32) );
                // System.out.println(ans[i]);
            }//end for

            this.aoid[0] = wholeString.substring( 840, 895 );
            this.hp = 25;//Integer.parseInt(wholeString.substring( 896, 896 ), 16);
            this.ed = wholeString.substring( 898, 902   );
            this.nn = Integer.parseInt(wholeString.substring( 902, 904 ), 16);
            this.sn = Integer.parseInt(wholeString.substring( 904, 910 ), 16);

            //Set Pans equal to ans by default 

            //System.out.println("ed " + this.ed);
            //System.out.println("nn " + this.nn);
            //System.out.println("aoid " + wholeString.substring( 896, 896 ));
            //System.out.println("aoid " + this.aoid[0]);
            //System.out.println("hp " + wholeString.substring( 896, 897 ));
            // String month = wholeString.substring( 898, 899   );
            //String year = wholeString.substring( 899, 902   );
            //System.out.println("sn " + this.sn);
            // System.out.println("nn " + wholeString.substring( 902, 904 ));
            //System.out.println("sn " + wholeString.substring( 904, 910 ));
        }//end if if jpg
        this.fileName = getDenomination() +".CloudCoin." + this.nn +"."+ this.sn + ".";
        this.json = "";
        this.jpeg = null;
    }//end new cc based on file content
    /* 
    public CloudCoin( String fileName, char type){
    //type s for json string, i for image
    // System.out.println("Loading file: "+ fileName);
    try{
    String fileContents = fileToString( fileName );
    //System.out.println(fileContents);

    String[] parts = fileContents.split("<>");
    // System.out.println("Length of parts " + parts.length);

    this.nn =  Integer.parseInt(parts[0]);
    this.sn =  Integer.parseInt(parts[1]);
    ans = new String[25];
    for(int i = 0; i < 25; i++){
    this.ans[i] =  parts[i+2];
    // System.out.println("Part " + (i+2) + ": " + parts[i+2] );
    }//end for each an
    pans = new String[25];
    for(int j = 0; j< 25; j++){
    this.pans[j] =  parts[j+2+25];
    //  System.out.println("Part " + (j+2+25) + ": " + parts[j+2+25] );
    }//end for each an
    this.ed =  parts[52];
    this.hp =  Integer.parseInt(parts[53]);
    this.pastStatus = new String[25];
    for(int k = 0; k < 25; k++){
    this.pastStatus[k] =  parts[k+54];
    // System.out.println("Part " + (i+2) + ": " + parts[i+2] );
    }//end for each an
    this.aoid[0] =  parts[79];
    this.fileName = getDenomination() +".CloudCoin." + this.nn +"."+ this.sn + ".";
    }catch(IOException e){
    System.out.println(e);
    }

    }//end new cc based on file content
     */
    /**
     * Returns the denomination of the money based on the serial number
     * 
     * @param  sn Serial Numbers 
     * @return  1, 5, 25, 100, 250
     */
    public int getDenomination() 
    {
        int nom = 0;
        if(this.sn < 1 ){  nom = 0;}
        else if(this.sn < 2097153) {  nom = 1; } 
        else if (this.sn < 4194305) { nom = 5; } 
        else if (this.sn < 6291457) { nom = 25; } 
        else if (this.sn < 14680065) { nom = 100; } 
        else if (this.sn < 16777217) { nom = 250; } 
        else { nom = '0'; }
        return nom;
    }

    public String setJSON(){

        StringBuilder sb = new StringBuilder();
        sb.append("{\"nn\":\"");
        sb.append( this.nn );    
        sb.append("\",\"sn\":\""); 
        sb.append(sn);
        sb.append("\",\"an\": [\""); 
        sb.append( ans[0] ); sb.append( "\", \"");sb.append( ans[1] ); sb.append( "\", \"");sb.append( ans[2] ); sb.append( "\", \"");sb.append( ans[3] ); sb.append( "\", \"");
        sb.append( ans[4] ); sb.append( "\", \"");sb.append( ans[5] ); sb.append( "\", \"");sb.append( ans[6] ); sb.append( "\", \"");sb.append( ans[7] ); sb.append( "\", \"");
        sb.append( ans[8] ); sb.append( "\", \"");sb.append( ans[9] ); sb.append( "\", \"");sb.append( ans[10] ); sb.append( "\", \"");sb.append( ans[11] ); sb.append( "\", \"");
        sb.append( ans[12] ); sb.append( "\", \"");sb.append( ans[13] ); sb.append( "\", \"");sb.append( ans[14] ); sb.append( "\", \"");sb.append( ans[15] ); sb.append( "\", \"");
        sb.append( ans[16] ); sb.append( "\", \"");sb.append( ans[17] ); sb.append( "\", \"");sb.append( ans[18] ); sb.append( "\", \"");sb.append( ans[19] ); sb.append( "\", \"");
        sb.append( ans[20] ); sb.append( "\", \"");sb.append( ans[21] ); sb.append( "\", \"");sb.append( ans[22] ); sb.append( "\", \"");sb.append( ans[23] ); sb.append( "\", \"");
        sb.append( ans[24] ); sb.append( "\"], \"ed\": \"");
        sb.append( "9-2018" );
        sb.append( "\", \"aoid\": []}");
        this.json = sb.toString();
        return this.json;
    }//end get JSON

    public void setJpeg( String rootFolder){
        byte[] returnBytes =  null;
        //Make byte array from CloudCoin
        String cloudCoinStr ="";
        for( int i = 0; i < 25; i++  ){
            cloudCoinStr += this.ans[i];
        }//end for each an
        // cloudCoinStr +="Defeat tyrants and obey God0"; //27 AOID and comments
        cloudCoinStr +="204f42455920474f4420262044454645415420545952414e54532000"; //27 AOID and comments
        cloudCoinStr +="00";//LHC = 100%

        cloudCoinStr +="97E2";//0x97E2;//Expiration date Sep. 2018
        cloudCoinStr += "01";// cc.nn;//network number
        String hexSN = Integer.toHexString(this.sn);  
        String fullHexSN ="";
        switch (hexSN.length())
        {
            case 1: fullHexSN = "00000" +hexSN; break;
            case 2:fullHexSN = "0000" +hexSN; break;
            case 3:fullHexSN = "000" +hexSN; break;
            case 4:fullHexSN = "00" +hexSN; break;
            case 5:fullHexSN = "0" +hexSN; break;
            case 6:fullHexSN = hexSN; break;
        }
        cloudCoinStr += fullHexSN;

        switch( getDenomination() ){
            case   1: 
            Path jpeg1 = Paths.get( rootFolder +"jpegs/jpeg1.jpg");
            try{
                returnBytes = Files.readAllBytes(jpeg1);

            }catch(IOException e){
                System.out.println("General I/O exception: " + e.getMessage());
                e.printStackTrace();
            }//end catch
            break;

            case   5: 
            Path jpeg5 = Paths.get(rootFolder +"jpegs/jpeg5.jpg");
            try{
                returnBytes = Files.readAllBytes(jpeg5);
            }catch(IOException e){
                System.out.println("General I/O exception: " + e.getMessage());
                e.printStackTrace();
            }//end catch
            break;

            case  25: 
            Path jpeg25 = Paths.get(rootFolder +"jpegs/jpeg25.jpg");
            try{
                returnBytes = Files.readAllBytes(jpeg25);
            }catch(IOException e){
                System.out.println("General I/O exception: " + e.getMessage());
                e.printStackTrace();
            }//end catch
            break;

            case 100:
            Path jpeg100 = Paths.get(rootFolder +"jpegs/jpeg100.jpg");
            try{
                returnBytes = Files.readAllBytes(jpeg100);
            }catch(IOException e){
                System.out.println("General I/O exception: " + e.getMessage());
                e.printStackTrace();
            }//end catch
            break;

            case 250: 
            Path jpeg250 = Paths.get(rootFolder +"jpegs/jpeg250.jpg");
            try{

                returnBytes = Files.readAllBytes(jpeg250);
            }catch(IOException e){
                System.out.println("General I/O exception: " + e.getMessage());
                e.printStackTrace();
            }//end catch

            break;
        }//end switch
        /*OVERWRITE */
        byte[] ccArray = hexStringToByteArray( cloudCoinStr );

        int offset = 20;
        // System.out.println("ccArray length " + ccArray.length);

        for( int j =0; j < ccArray.length; j++  ){
            returnBytes[offset + j ] = ccArray[j];
        }//end for each byte in the ccArray

        this.jpeg = returnBytes;
    }//end get jpeg

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public boolean saveCoin(String extension ){
        boolean goodSave = false;
        File f = new File("./Bank/" + this.fileName + extension );
        if(f.exists() && !f.isDirectory()) { 
            System.out.println("A coin with that SN already exists in the bank. Export it first.");
            return goodSave;
        }

        String json = "{" + System.getProperty("line.separator");
        json +=   "\t\"cloudcoin\": [{" + System.getProperty("line.separator") ;
        json += "\t\t\"nn\":\"1\"," + System.getProperty("line.separator");
        json +="\t\t\"sn\":\""+ sn + "\"," + System.getProperty("line.separator");

        json += "\t\t\"an\": [\"";
        for(int i = 0; i < 25; i++){
            json += ans[i];
            if( i == 4 || i == 9 || i == 14 || i == 19){
                json += "\"," + System.getProperty("line.separator") + "\t\t\t\"";
            }else if( i == 24){
                //json += "\""; last one do nothing
            }
            else
            {//end if is line break
                json += "\",\"";
            }//end else
        }//end for 25 ans
        json += "\"]," + System.getProperty("line.separator");
        json += "\t\t\"ed\":\"9-2016\"," + System.getProperty("line.separator");
        json += "\t\t\"aoid\": []" + System.getProperty("line.separator");
        json += "\t}] "+ System.getProperty("line.separator"); 
        json += "}";  
        //Find date, replace the date in json with new date. 
        if( extension.equalsIgnoreCase("bank")  ){
            java.util.Date date= new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            year = year + YEARSTILEXPIRE;
            String expDate = month + "-" + year;
            json.replace("9-2016", expDate );
        }//end if extension is "import"

        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter( new FileWriter( "./Bank/" + this.fileName + extension ));
            // System.out.println("\nSaving Coin file to Bank/" + this.fileName + extension );
            writer.write( json );
            goodSave = true;
        }catch ( IOException e){ } finally{    try{
                if ( writer != null)
                    writer.close( );
            }catch ( IOException e){}
        }
        return goodSave;

    }//end saveCoin

    /*   
    public boolean saveCoin( String extension ){
    boolean goodSave = false;
    String coinText = nn +"<>";
    coinText += sn +"<>";
    for(int ii = 0; ii< 25; ii++){
    coinText += ans[ii] +"<>";
    }//end for each an

    for(int iii = 0; iii< 25; iii++){
    coinText += pans[iii] +"<>";
    }//end for each an
    coinText += ed +"<>";
    coinText += hp +"<>";
    for(int i = 0; i< 25; i++){
    coinText += pastStatus[i] +"<>";
    }//end for each an
    coinText += aoid +"<>";

    BufferedWriter writer = null;
    try
    {
    writer = new BufferedWriter( new FileWriter( "./Bank/" + this.fileName + extension ));
    // System.out.println("\nSaving Coin file to Bank/" + this.fileName + extension );
    writer.write( coinText );
    goodSave = true;
    }
    catch ( IOException e)
    {
    }
    finally
    {
    try
    {
    if ( writer != null)
    writer.close( );
    }
    catch ( IOException e)
    {
    }
    }
    return goodSave;
    }
     */
    public boolean deleteCoin( String path, String extension ){
        boolean deleted = false;
        //System.out.println("Deleteing Coin: "+path + this.fileName + extension);
        File f  = new File( path + this.fileName + extension);
        try {
            deleted = f.delete();
            if(deleted){
            }else{
                // System.out.println("Delete operation is failed.");
            }//end else
        }catch(Exception e){

            e.printStackTrace();

        }
        return deleted;
    }//end delete file

    public String fileToString(String pathname) throws IOException {
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

    public void calculateHP(){
        this.hp = 25;
        for( int i = 0; i< 25; i++){
            if( this.pastStatus[i].equalsIgnoreCase("fail")   )
            { 
                this.hp--;
            }
        }

    }//End calculate hp

    public void reportStatus(){
        System.out.println("NN:"+this.nn+", SN: " + this.sn );
        for( int i = 0; i< 25; i++){
            System.out.println( i +"'s status is "+ this.pastStatus[i] );
        }

    }//End report status

    public boolean writeJpeg( String path, String tag ){  
        boolean writeGood = true;
        String file = path + File.separator  + this.fileName + tag +".jpg";
        System.out.println("Saving jpg: " + file);
        try{
            Files.write(Paths.get( file ), this.jpeg );
        }catch( IOException ex ){
            System.out.println( "Error Saving Jpeg: " + ex );
            writeGood = false;
        }//end try ioexception
        return writeGood;
    }//end jpeg to file

    
    
    
    public String gradeCoin(){
        int passed = 0;
        int failed = 0;
        int other = 0;
        String passedDesc ="";
        String failedDesc ="";
        String otherDesc ="";
        for( int i=0; i< 25; i++ ){

            if( pastStatus[i].equalsIgnoreCase("pass")  ){
                passed++;
            }else if( pastStatus[i].equalsIgnoreCase("fail")){
                failed++;
            }else{
                other++;
            }//end if pass, fail or unknown
        }//for each status

        //Calculate passed
        if( passed == 25 ){
            passedDesc = "100% Passed!"; 
        }else if( passed > 17 ){
            passedDesc = "Super Majority";
        }else if( passed > 13){
            passedDesc = "Majority";
        }else if( passed == 0){
            passedDesc = "None";
        }else if(passed < 5) {
            passedDesc = "Super Minority";
        }else{
            passedDesc = "Minority";
        }
        //Calculate failed
        if( failed == 25 ){
            failedDesc = "100% Failed!"; 
        }else if( failed > 17 ){
            failedDesc = "Super Majority";
        }else if( failed > 13){
            failedDesc = "Majority";
        }else if( failed == 0){
            failedDesc = "None";
        }else if(failed < 5) {
            failedDesc = "Super Minority";
        }else{
            failedDesc = "Minority";
        }
        //Calcualte Other RAIDA Servers did not help. 
        //Calcualte Other RAIDA Servers did not help. 
        
        
        
        switch( other ){
            case 0: otherDesc = "RAIDA 100% good"; break;
            case 1: 
            case 2: otherDesc = "Four or less RAIDA errors";  break;
            case 3: 
            case 4: otherDesc = "Four or less RAIDA errors"; break;
            case 5: 
            case 6: otherDesc = "Six or less RAIDA errors"; break;
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12:  otherDesc = "Between 7 and 12 RAIDA errors"; break;
            case 13:  
            case 14:  
            case 15:  
            case 16: 
            case 17:  
            case 18: 
            case 19:  
            case 20:  
            case 21:  
            case 22:  
            case 23: 
            case 24:  
            case 25: otherDesc = "RAIDA total failure"; break;
            default: otherDesc = "FAILED TO EVALUATE RAIDA HEALTH"; break;
        }//end RAIDA other errors and unknowns

      /*
        if(){
            if one 
        if a majority say passed. passed
        }else if(){
        a majority say failed counterfiet
        }else if(){
         minority say passed 
        }else{
        
        }//end if grades
        grade = "Authentic";
        grade = "Counterfeit";
        grade = "Fracked";
         grade = "Lost";
        
        */
        return  "\n " + passedDesc + " said Passed. " + "\n "+ failedDesc +" said Failed. \n RAIDA Status: "+ otherDesc;
    }//end grade coin
    public String loadJSON( String jsonfile) throws FileNotFoundException {
        String jsonData = "";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader( jsonfile ));
            while ((line = br.readLine()) != null) {
                jsonData += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return jsonData;
    }//en d json test
    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }//end toStringArray

    private String toHexadecimal(byte[] digest){
        String hash = "";
        for(byte aux : digest) {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) hash += "0";
            hash += Integer.toHexString(b);
        }
        return hash;
    }

    public void calcExpirationDate(){
        java.util.Date date= new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        year = year + YEARSTILEXPIRE;
        this.ed = month + "-" + year;
        this.edHex = Integer.toHexString(month);
        this.edHex += Integer.toHexString(year);       
    }//end calc exp date

    private String generatePan()
    {
        String AB = "0123456789ABCDEF";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( 25 );
        for( int i=0 ; i<32 ; i++ ) 
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

}
