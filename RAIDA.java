import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.*;

/**
 * Write a description of class RAIDA here.
 * 
 * @author Sean Worthington
 * @version (a version number or a date)
 */
public class RAIDA
{
    // instance variables
    public int[] trustedServers = new int[8];
    public int[] trustedTriad1;
    public int[] trustedTriad2;
    public int[] trustedTriad3;
    public int[] trustedTriad4;

    public int RAIDANumber;
    public String name; 
    public String status; //Unknown, slow or ready
    public String testStatus; //Unknown, slow or ready
    public long ms; //milliseconds
    public long dms = 0; //ms to detect
    public String lastJsonRaFromServer = null;
    public String lastTicket = null;
    public String fullUrl;
    public String lastDetectStatus = "notdetected";//error, notdetected, pass, fail
    //public String lastDetectSn = null;//error, unknown, pass, fail
    public String lastTicketStatus = "empty";//ticket, fail, error
    public String lastFixStatus = "empty";//ticket, fail, error
    public String lastHtml = "empty";//ticket, fail, error

    /**
     * Constructor for objects of class RAIDA
     */
    public RAIDA( int RAIDANumber )
    {
        // initialise instance variables

        //Calculate the Trusted Servers
        // Calculate the 8 trusted servers that are directly attached to broken RAIDA
        
        trustedServers[0] = Math.floorMod(RAIDANumber - 6, 25);//T)rusted server 1 is the id of your servers minus 6 mod 25.
        trustedServers[1] = Math.floorMod(RAIDANumber - 5, 25);
        trustedServers[2] = Math.floorMod(RAIDANumber - 4, 25);
        trustedServers[3] = Math.floorMod(RAIDANumber - 1, 25);
        trustedServers[4] = Math.floorMod(RAIDANumber + 1, 25);
        trustedServers[5] = Math.floorMod(RAIDANumber + 4, 25);
        trustedServers[6] = Math.floorMod(RAIDANumber + 5, 25);
        trustedServers[7] = Math.floorMod(RAIDANumber + 6, 25);

        trustedTriad1 = new int[]{trustedServers[0] , trustedServers[1] , trustedServers[3] };
        trustedTriad2 = new int[]{trustedServers[1] , trustedServers[2] , trustedServers[4] };
        trustedTriad3 = new int[]{trustedServers[3] , trustedServers[5] , trustedServers[6] };
        trustedTriad4 = new int[]{trustedServers[4] , trustedServers[6] , trustedServers[7] };


        this.status = "unknown";
        this.testStatus = "unknown";
        this.ms = 0;
        this.fullUrl = "https://raida"+ RAIDANumber + ".cloudcoin.global/service/";
      
    }//RAIDA

    //Methods
    public String echo(){
        String html ="error";
        String url = this.fullUrl + "echo";//." + this.ext;
        
        Instant before = Instant.now();
        try{
            html = getHtml(url);
        }catch( IOException ex ){
            
            this.status = "error";
            //System.out.println( status );
            return "error";
        }
        Instant after = Instant.now();
        //System.out.println( html );
        boolean isReady = html.contains("ready");
        this.ms = Duration.between(before, after).toMillis();
        if(isReady){ 
            this.status = "ready";
            return "ready";}else{
            this.status = "error";
            return "error";
        }
    }//end echo

    public String test(){
        String html ="error";
        String url = this.fullUrl + "test";//." + this.ext;
        Instant before = Instant.now();
        try{
            html = getHtml(url);
        }catch( IOException ex ){
            System.out.println( testStatus );
            this.testStatus = "error";
            return "error";
        }
        Instant after = Instant.now();
        //System.out.println( html );
        boolean isReady = html.contains("ready");
        this.ms = Duration.between(before, after).toMillis();
        if(isReady){ 
            this.testStatus = "ready";
            return "ready";}else{
            this.testStatus = "error";
            return "error";
        }
    }//end echo

    public String detect(CloudCoin cc, boolean usePan){
        
        String returnString = "";
        System.out.println(returnString);
     
            String html ="error";
            String url ="";
           if( usePan ){
             url = this.fullUrl + "detect?nn=" + cc.nn + "&sn=" + cc.sn + "&an=" + cc.ans[RAIDANumber] + "&pan=" + cc.pans[RAIDANumber]  + "&denomination=" + cc.getDenomination();
            }else{
             url = this.fullUrl + "detect?nn=" + cc.nn + "&sn=" + cc.sn + "&an=" + cc.ans[RAIDANumber] + "&pan=" + cc.ans[RAIDANumber]  + "&denomination=" + cc.getDenomination();
            }//end if use pan
            
            // System.out.print( ".  Raida number " + RAIDANumber );
             System.out.println("\n Request: " + url);
            Instant before = Instant.now();
            
            try{
                html = getHtml(url);
               System.out.println( html );
            }catch( IOException ex ){
                returnString = "RAIDA " +this.RAIDANumber + " " +ex;
                lastDetectStatus = "error";
                //lastHtml = ex;
                System.out.println(returnString);
                return returnString;
            }
            Instant after = Instant.now();
            this.lastJsonRaFromServer = html;
            this.dms = Duration.between(before, after).toMillis();
            if( html.contains("pass") )
            { 
                lastDetectStatus = "pass";
                if( usePan ){
                cc.ans[RAIDANumber] = cc.pans[RAIDANumber];
                cc.calcExpirationDate();
                }else{
                cc.calcExpirationDate();
                }
                System.out.println(returnString);
            }
            else if( html.contains("fail") && html.length() < 200 )//less than 200 incase their is a fail message inside errored page
            {  lastDetectStatus = "fail"; 
            
            }
            else
            { 
                lastDetectStatus = "error"; 
            }
       
         return returnString;
    }//end detect

    public String get_ticket( String an, int nn, int sn, int denomination  ){
        //Will only use ans to fix
        String returnStatus ="";
        this.lastTicket = "none";
        String url = fullUrl + "get_ticket?nn="+nn+"&sn="+sn+"&an="+an+"&pan=" +an+ "&denomination="+denomination;
        System.out.println("\n Request: " + url );

        String html = "";
        Instant before = Instant.now();
        try{
            html = getHtml(url);
            
            JSONObject o = new JSONObject( html );
            this.lastTicketStatus = o.getString("status");
            String message = o.getString("message");
            if (this.lastTicketStatus.equalsIgnoreCase("ticket") ){
                this.lastTicket = message;

            }//end if
            //  System.out.println( html );
        }catch( JSONException ex ){
            returnStatus = "Error in RAIDA get_ticket() " +ex ;
            return returnStatus;

        }catch( MalformedURLException ex ){
            returnStatus = "Error in RAIDA get_ticket() " +ex ;
            return returnStatus;
        } catch( IOException ex ){
            returnStatus = "Error in RAIDA get_ticket() " +ex ;
            return returnStatus;
        }
        Instant after = Instant.now();
        this.lastJsonRaFromServer = html;
        this.dms = Duration.between(before, after).toMillis();
        //System.out.println(html);
        return returnStatus + "\n Response: " + this.lastJsonRaFromServer +"\n Time to process in ms:"+ this.dms; 
    }//end get ticket

    public String get_ticket( CloudCoin cc ){
        String returnStatus = "";
        this.lastTicket = "none";
        String url = fullUrl + "get_ticket?nn="+cc.nn+"&sn="+cc.sn+"&an="+cc.ans[RAIDANumber]+"&pan="+cc.ans[RAIDANumber]+"&denomination="+ cc.getDenomination();
        System.out.println("\n Request: "  +  url );

        String html = "";
        Instant before = Instant.now();
        try{
            html = getHtml(url);
            JSONObject o = new JSONObject( html );
            this.lastTicketStatus = o.getString("status");
            String message = o.getString("message");
            if (this.lastTicketStatus.equalsIgnoreCase("ticket") ){
                this.lastTicket = message;
            }//end if
            //  System.out.println( html );
        }catch( JSONException ex ){
            returnStatus = "Error in RAIDA get_ticket() " +ex ;
            return returnStatus;
        }catch( MalformedURLException ex ){
            returnStatus = "Error in RAIDA get_ticket() " +ex ;
            return returnStatus;
        } catch( IOException ex ){
            returnStatus = "Error in RAIDA get_ticket() " +ex ;
            return returnStatus;
        }
        Instant after = Instant.now();
        this.lastJsonRaFromServer = html;
        this.dms = Duration.between(before, after).toMillis();
        return returnStatus +  "\n Response: " + this.lastJsonRaFromServer +"\n Time to process in ms: "+ this.dms; 
    }//end get ticket

    public String testHint( CloudCoin cc){
        String returnStatus = "";
        //get ticket
        get_ticket(cc);
        if( lastTicketStatus.equalsIgnoreCase("ticket")){
            String url = fullUrl + "hints?rn=" + this.lastTicket;
            System.out.println( "\n Request: "  +  url   );
            String html = "";
            Instant before = Instant.now();
            try{
                this.lastJsonRaFromServer = getHtml(url);
            }catch( MalformedURLException ex ){//quit
                return "Error in RAIDA fix() " +ex ;
            } catch( IOException ex ){
                return "Error in RAIDA fix() " +ex ;
            }
            Instant after = Instant.now();
            this.dms = Duration.between(before, after).toMillis();
           return returnStatus + "\n Response: " + this.lastJsonRaFromServer +"\n Time to process in ms:"+ this.dms;  
        }else{//Getting ticket failed
            return "Get_ticket failed on this RAIDA so hints could not be checked.";
        }//end if ticket was got
     
    }//End test hints

    public String fix( int[] ans, String m1,String m2, String m3, String pan ){
 
        this.lastFixStatus = "error"; 

        int f1 = ans[0];
        int f2 = ans[1];
        int f3 = ans[2];
        String url = fullUrl;
        url += "fix?fromserver1="+f1+"&message1="+m1+"&fromserver2="+f2+"&message2="+m2+"&fromserver3="+f3+"&message3="+m3+"&pan="+pan;
        System.out.println("\n Request: "  +  url );
        Instant before = Instant.now();
        try{
            this.lastJsonRaFromServer = getHtml(url);
        }catch( MalformedURLException ex ){//quit
            return "Error in RAIDA fix() " +ex ;
        } catch( IOException ex ){
            return "Error in RAIDA fix() " +ex ;
        }
        Instant after = Instant.now();
        this.dms = Duration.between(before, after).toMillis();
        if( this.lastJsonRaFromServer.contains("success") ){ 
            this.lastFixStatus = "success"; 
            System.out.println(  "\n Response: " + this.lastJsonRaFromServer +"\n Time to process in ms:"+ this.dms );
            return "success"; 
        }
        this.dms = Duration.between(before, after).toMillis();
        System.out.println(  "\n Response: " + this.lastJsonRaFromServer +"\n Time to process in ms:"+ this.dms );
        return lastFixStatus +  "\n Response: " + this.lastJsonRaFromServer +"\n Time to process in ms:"+ this.dms; 
    }//end fixit

    public void dumpvar(){

        System.out.println("RAIDANumber " + RAIDANumber );
        //Calculate the Trusted Servers
        // Calculate the 8 trusted servers that are directly attached to broken RAIDA
        System.out.println("trustedServes[0] " + trustedServers[0] );
        System.out.println("trustedServes[1] " + trustedServers[1] );
        System.out.println("trustedServes[2] " + trustedServers[2] );
        System.out.println("trustedServes[3] " + trustedServers[3]);
        System.out.println("trustedServes[4] " + trustedServers[4]);
        System.out.println( "trustedServes[5] " +trustedServers[5] );
        System.out.println("trustedServes[6] " + trustedServers[6] );
        System.out.println("trustedServes[7] " + trustedServers[7]);

        System.out.println("trustedTriad1 " + trustedTriad1[0] + ","+ trustedTriad1[1] + ", "+ trustedTriad1[2] );
        System.out.println("trustedTriad2 " + trustedTriad2[0] + ","+ trustedTriad2[1] + ", "+ trustedTriad2[2] );
        System.out.println("trustedTriad3 " + trustedTriad3[0] + ","+ trustedTriad3[1] + ", "+ trustedTriad3[2] );
        System.out.println("trustedTriad4 " + trustedTriad4[0] + ","+ trustedTriad4[1] + ", "+ trustedTriad4[2] );

        System.out.println("status " + this.status );
        System.out.println("testStatus " + this.testStatus);
        System.out.println( "ms " +this.ms );
    
        System.out.println("fullurl " + this.fullUrl );

        System.out.println("lastTicket " + lastTicket);
        System.out.println("lastDetectStatus " + lastDetectStatus);
        //public String lastDetectSn = null;//error, unknown, pass, fail
        System.out.println("lastTicketStatus " + lastTicketStatus );
        System.out.println("lastFixStatus " + lastFixStatus);
        System.out.println("lastJsonRaFromServer " + lastJsonRaFromServer );
        System.out.println("lastHtml " + lastHtml );

    }//end dump var

    public String getHtml(String url_in) throws MalformedURLException, IOException {
        int c;
        URL cloudCoinGlobal = new URL(url_in);
        URLConnection conn = cloudCoinGlobal.openConnection();
        conn.setReadTimeout(10000); //set for two seconds
        conn.setReadTimeout(10000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        InputStream input = conn.getInputStream();

        StringBuilder sb = new StringBuilder();

        while((( c = input.read()) != -1))
        {
            sb.append((char)c); 
        }//end while   
        input.close();
        this.lastHtml = sb.toString();
        return sb.toString();
    }//end get url

}
