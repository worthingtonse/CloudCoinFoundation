import java.util.Arrays;
import java.security.SecureRandom;
import java.util.concurrent.*;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Bank tracks the entire contents of the Bank folder used to manage CloudCoins
 * 
 * 
 * @author Sean H. Worthington
 * @version 12/22/2016
 */
public class Bank
{
    // instance variables - replace the example below with your own
    public KeyboardReader reader = new KeyboardReader();
    public CloudCoin[] newCoins; 
    public CloudCoin[] frackedCoins;
    public CloudCoin[] importedCoins;
    public CloudCoin[] bankedCoins;
    public CloudCoin[] counterfeitCoins;

    /**
     * CONSTRUCTOR
     */
    public Bank()
    {
        // initialise instance variables
    }

    /**
     * METHODS
     */
    public int countCoins( CloudCoin[] coins, int denomination ){
        int totalCount =  0;
        for(int i = 0 ; i < coins.length; i++){
            if( coins[i].getDenomination() == denomination ){
                totalCount++;
            }//end if coin is the denomination
        }//end for each coin
        return totalCount;
    }//end count coins

    public boolean exportJson(int m1, int m5, int m25, int m100, int m250, String path, String tag, String rootFolder ){
        boolean jsonExported = true;
        int totalSaved = m1 + ( m5 * 5 ) + ( m25 * 25 ) + (m100 * 100 ) + ( m250  * 250 );
        int coinCount = m1 + m5 + m25 + m100 + m250;

        /* CONSRUCT JSON STRING FOR SAVING */
        CloudCoin[] coinsToDelete =  new CloudCoin[coinCount];
        int c = 0;//c= counter
        String json = "{ \"CloudCoin\": [";
        for(int i =0; i< bankedCoins.length; i++ ){
            if( bankedCoins[i].getDenomination() == 1 && m1 > 0 ){ 
                if( c !=0 ){ json += ",\n";} 
                coinsToDelete[c]=bankedCoins[i]; 
                c++; 
                json += bankedCoins[i].setJSON(); 
                m1--;   
            }//end if coin is a 1
            if( bankedCoins[i].getDenomination() == 5 && m5 > 0 ){  if( c !=0 ){ json += ",\n";}coinsToDelete[c]=bankedCoins[i]; c++; json += bankedCoins[i].setJSON(); m5--;   }//end if coin is a 5
            if( bankedCoins[i].getDenomination() == 25 && m25 > 0 ){ if( c !=0 ){ json += ",\n";} coinsToDelete[c]=bankedCoins[i]; c++; json += bankedCoins[i].setJSON(); m25--  ; }//end if coin is a 25
            if( bankedCoins[i].getDenomination() == 100 && m100 > 0 ){  if( c !=0 ){ json += ",\n";}coinsToDelete[c]=bankedCoins[i]; c++; json += bankedCoins[i].setJSON(); m100--;   }//end if coin is a 100
            if( bankedCoins[i].getDenomination() == 250 && m250 > 0 ){  if( c !=0 ){ json += ",\n";}coinsToDelete[c]=bankedCoins[i]; c++;  json += bankedCoins[i].setJSON(); m250--;   }//end if coin is a 250
            if( m1 ==0 && m5 ==0 && m25 == 0 && m100 == 0 && m250 == 0 ){break;}//Break if all the coins have been called for.     
        }//for each 1 note
        json += "]}";

        /* FIGURE OUT NEW STACK NAME AND SAVE TO FILE */
        String filename = path + File.separator + totalSaved +".CloudCoins." + tag + ".stack";
        if(  ifFileExists(filename)){//tack on a random number if a file already exists with the same tag
            //Add random 
            Random rnd = new Random();
            int tagrand = rnd.nextInt(999);
            filename = path + File.separator + totalSaved +".CloudCoins." + tag + tagrand + ".stack";
        }//end if file exists
        System.out.println("Writing to : " + filename);
        
        if ( stringToFile( json, filename ) ){
        
         /* DELETE EXPORTED CC FROM BANK */ 
        for(int cc = 0; cc < coinsToDelete.length; cc++){
            // System.out.println("Deleting "+ path + coinsToDelete[cc].fileName + "bank");
            coinsToDelete[cc].deleteCoin( rootFolder, "bank" );

        }//end for
        
        }else{
        //Write Failed
        jsonExported = false;
        }//end if write was good

       return jsonExported;

    }//end export

    public void exportJpeg(int m1, int m5, int m25, int m100, int m250, String path, String tag, String rootFolder ){
        String r = rootFolder;
        String b = "bank";
        String t = tag;
        String p = path;
        int coinCount = m1 + m5 + m25 + m100 + m250;
        /* SET JPEG, WRITE JPEG and DELETE CLOUDCOINS*/
        int c = 0;//c= counter
        for(int i =0; i< bankedCoins.length; i++ ){
            if( bankedCoins[i].getDenomination() == 1 && m1 > 0 ){ 
                bankedCoins[i].setJpeg(r); 
                if( bankedCoins[i].writeJpeg(p,t)){
                bankedCoins[i].deleteCoin(r,b); }
                m1--;  
            }//end if coin is a 1
            if( bankedCoins[i].getDenomination() == 5 && m5 > 0 ){ 
                bankedCoins[i].setJpeg(r); if( bankedCoins[i].writeJpeg(p,t)){ bankedCoins[i].deleteCoin(r,b);}  m5--;   }//end if coin is a 5
            if( bankedCoins[i].getDenomination() == 25 && m25 > 0 ){ 
                bankedCoins[i].setJpeg(r); if( bankedCoins[i].writeJpeg(p,t)){ bankedCoins[i].deleteCoin(r,b);}  m25--  ; }//end if coin is a 25
            if( bankedCoins[i].getDenomination() == 100 && m100 > 0 ){ 
                bankedCoins[i].setJpeg(r); if( bankedCoins[i].writeJpeg(p,t)){ bankedCoins[i].deleteCoin(r,b);}  m100--;   }//end if coin is a 100
            if( bankedCoins[i].getDenomination() == 250 && m250 > 0 ){ 
                bankedCoins[i].setJpeg(r); if( bankedCoins[i].writeJpeg(p,t)){ bankedCoins[i].deleteCoin(r,b);}  m250--;   }//end if coin is a 250
            if( m1 ==0 && m5 ==0 && m25 == 0 && m100 == 0 && m250 == 0 ){break;}//Break if all the coins have been called for.     
        }//for each 1 note

    }//end export

    public boolean ifFileExists( String filePathString ){
        File f = new File(filePathString);
        if(f.exists() && !f.isDirectory()) { 
            return true;
        }
        return false;
    }//end if file Exists

    /***
     * GIven a directory and an extension, loads all CloudCoins of that extension
     * 
     */
    public CloudCoin[] loadCoins(String directoryPath, String extension){
        File f = null;
        String[] paths;
        CloudCoin[] loadedCoins =null;
        try{ 
            f = new File(directoryPath); // System.out.println("Checking " + directoryPath + " for " + extension + " files.");
            FilenameFilter fileNameFilter = new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.lastIndexOf('.')>0){
                            int lastIndex = name.lastIndexOf('.')+1; // get last index for '.' char
                            String str = name.substring(lastIndex);  // get extension
                            if(str.equals( extension )) {// match path name extension
                                return true;
                            }
                        }
                        return false;
                    }
                };
            paths = f.list(fileNameFilter);// returns pathnames for files and directory
            loadedCoins = new CloudCoin[ paths.length ];// for each pathname in pathname array
            for(int i = 0; i < paths.length; i++)            {
                loadedCoins[i] = new CloudCoin( directoryPath + paths[i] );  //  System.out.println("Loading " + directoryPath + paths[i]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return loadedCoins;
    }//end load fracked  

    /**
     * This method is used to load .chest and .stack files that are in JSON notation.
     * 
     * @param  loadFilePath: The path to the Bank file and the name of the file. 
     * @param  Security: How the ANs are going to be changed during import (Random, Keep, password).
     */
    public void loadIncome( String loadFilePath, String security ) {  
        System.out.println("Trying to load: " + loadFilePath );
        String incomeJson = ""; 
        // String new fileName = coinCount +".CloudCoin.New"+ rand.nextInt(5000) + "";
        try{
            incomeJson = loadJSON( loadFilePath );
        }catch( IOException ex ){
            System.out.println( "error " + ex );
        }
        // String ans[] = new String[25];
        JSONArray incomeJsonArray;

        try{
            JSONObject o = new JSONObject( incomeJson );
            incomeJsonArray = o.getJSONArray("CloudCoin");
            this.newCoins = new CloudCoin[incomeJsonArray.length()];
            for (int i = 0; i < incomeJsonArray.length(); i++) {  // **line 2**
                JSONObject childJSONObject = incomeJsonArray.getJSONObject(i);
                int nn     = childJSONObject.getInt("nn");
                int sn     = childJSONObject.getInt("sn");
                JSONArray an = childJSONObject.getJSONArray("an");
                String ed     = childJSONObject.getString("ed");
                String aoid     = childJSONObject.getString("aoid");
                this.newCoins[i] = new CloudCoin( nn, sn, toStringArray(an), ed, aoid, security );//security should be change or keep for pans.
                //System.out.println("bank: New coin "+ i +" created " + this.newCoins[i].sn + ", ans[0] =" + this.newCoins[i].ans[0]);
                //System.out.println("bank: [0] coin 0 created " + this.newCoins[0].sn + ", ans[0] =" + this.newCoins[0].ans[0]);
                // System.out.println( "Loading Coin: nn " + nn + ", sn " + sn + ", ed " + ed + ", aoid " + aoid );
            }//end for each coin

        }catch( JSONException ex)
        {
            System.out.println("Error: " + ex);

        }//try 

    }//end load income

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

    /***
     * Given a file name, changes the file extension without changing the file
     * @parameter source The name of the target file
     * @parameter newExtension The new extension to be give the file
     * @return boolean True if the extension is changes otherwise false. 
     */
    public static boolean renameFileExtension(String source, String newExtension){
        String target;
        String currentExtension = getFileExtension(source);

        if (currentExtension.equals("")){
            target = source + "." + newExtension;
        }
        else {
            target = source.replaceFirst(Pattern.quote("." + currentExtension) + "$", Matcher.quoteReplacement("." + newExtension));
        }
        return new File(source).renameTo(new File(target));
    }

    /***
     * Given string that repressents a file name, return the file extention
     * @parameter f The filename
     * @return ext The file extention 
     */
    public static String getFileExtension(String f) {
        String ext = "";
        int i = f.lastIndexOf('.');
        if (i > 0 &&  i < f.length() - 1) {
            ext = f.substring(i + 1);
        }
        return ext;
    }

    /***
     * Given directory path return an array of strings of all the files in the directory.
     * @parameter directoryPath The location of the directory to be scanned
     * @return filenames The names of all the files in the directory
     */
    public String[] selectAllFileNamesInFolder(String directoryPath) {
        File dir = new File(directoryPath);
        Collection<String> files  =new ArrayList<String>();
        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();

            for(File file : listFiles){
                if(file.isFile()) {
                    files.add(file.getName());
                }
            }
        }
        return files.toArray(new String[]{});
    }//End select all file names in a folder

    /***
     * Given a byte array and a file name, write the bytes to the harddrive.
     * @parameter text The string to go into the file
     * @paramerter filename The name to be given to the file.
     */
    public static void bytesToFile( String text, String filename){

        try(  PrintWriter out = new PrintWriter( filename )  ){
            out.println( text );
        }catch( FileNotFoundException ex){
            System.out.println(ex);
        }
    }//end string to file 

    /***
     * Given a string and a file name, write the string to the harddrive.
     * @parameter text The string to go into the file
     * @paramerter filename The name to be given to the file.
     */
    public static boolean stringToFile( String text, String filename) {
        boolean writeGood =  false;
        try(  PrintWriter out = new PrintWriter( filename )  ){
            out.println( text );
            writeGood = true;
        }catch( FileNotFoundException ex){
            System.out.println(ex);
        }
        return writeGood;
    }//end string to file 

}
