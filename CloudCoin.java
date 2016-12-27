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
 * @version 12/24/2016
 */
public class CloudCoin
{
    // instance variables - replace the example below with your own
    public int nn;//Network Numbers
    public int sn;//Serial Number
    public String[] ans ;//Authenticity Numbers
    public String[] pans;//Proposed Authenticty Numbers
    public String[] pastStatus;//fail, pass, error, unknown (could not connect to raida)
    public String ed; //Expiration Date
    public int hp;//HitPoints (1-25, One point for each server not failed)
    public String aoid;//Account or Owner ID
    public String fileName;
    public String json;
    public byte[] jpeg;

    /**
     * Constructor for objects of class CloudCoin
     */
    public CloudCoin(int nn, int sn, String[] ans, String ed, String aoid, String security )
    {
        // initialise instance variables
        //System.out.println("CloudCoin: New coin created " + sn + " ans[0] = " + ans[0]);
        this.nn = nn;
        this.sn = sn;     
        this.ans = ans;
        switch(security){
            case "random"://change pans
            this.pans = new String[25];
            this.pastStatus = new String[25];
            for(int i = 0; i < 25; i++ ){
                pans[i] = generatePan();
                pastStatus[i]= "unknown";
            }
            break;
            case "keep"://keep the current ans the same
            this.pans = new String[25];
            this.pastStatus = new String[25];
            for(int i = 0; i < 25; i++ ){
                pans[i] = ans[i];
                pastStatus[i]= "unknown";
            }
            break;
            case "no"://keep the current ans the same
            this.pans = new String[25];
            this.pastStatus = new String[25];
            for(int i = 0; i < 25; i++ ){
                pans[i] = ans[i];
                pastStatus[i]= "pass";
            }
            break;
        }

        this.ed = ed;
        this.hp = 25;
        this.aoid = aoid;
        this.fileName = getDenomination() +".CloudCoin." + this.nn +"."+ this.sn + ".";
        this.json = "";
        this.jpeg = null;
    }

    public CloudCoin( String fileName){
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
            this.aoid =  parts[79];
            this.fileName = getDenomination() +".CloudCoin." + this.nn +"."+ this.sn + ".";
        }catch(IOException e){
            System.out.println(e);
        }

    }//end new cc based on file content

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

    
    
    private String generatePan()
    {
        String AB = "0123456789ABCDEF";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( 25 );
        for( int i=0 ; i<32 ; i++ ) 
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

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
            passedDesc = "Consensus"; 
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
            failedDesc = "Consensus"; 
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
        switch( other ){
          case 0: otherDesc = "RAIDA 100% good"; break;
          case 1: 
          case 2: otherDesc = "Four or less RAIDA errors"; break;
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
 
        
        return  "\n " + passedDesc + " said Passed. " + "\n "+ failedDesc +" said Failed. \n RAIDA Status: "+ otherDesc;
    }//end grade coin
    
}
