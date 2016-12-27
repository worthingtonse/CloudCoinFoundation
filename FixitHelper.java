
/**
 * Used to track the progress of fixing a fracktured cloud coin
 * 
 * @author Sean H. Worthington
 * @version 12/11/2016 (December 11th 2016)
 */

public class FixitHelper
{
    // instance variables - replace the example below with your own
    public int id;//Raida to be fixed
    public int[] trustedServers = new int[8];//Each servers only trusts eight others
    public int[] trustedTriad1;
    public int[] trustedTriad2;
    public int[] trustedTriad3;
    public int[] trustedTriad4;
    public int[] currentTriad;
    
    public String[] ans1;
    public String[] ans2;
    public String[] ans3;
    public String[] ans4;
    public String[] currentAns;
    
    public String[] currentTrustedTriadTickets;

    public boolean triad_1_is_ready = false;
    public boolean triad_2_is_ready = false;
    public boolean triad_3_is_ready = false;
    public boolean triad_4_is_ready = false;
    public boolean currentTriadReady = false;
    
    public boolean fixed = false;//Is the GUID fixed?
    public boolean finnished = false;//All triads have been tried

    public CloudCoin cc; //cloudcoin

    /**
     * Constructor for objects of class FixitHelper
     */
    public FixitHelper( int id, CloudCoin brokeCoin )
    {
        this.id = id;
        trustedServers[0] = Math.floorMod(id - 6, 25);//T)rusted server 1 is the id of your servers minus 6 mod 25.
        trustedServers[1] = Math.floorMod(id - 5, 25);
        trustedServers[2] = Math.floorMod(id - 4, 25);
        trustedServers[3] = Math.floorMod(id - 1, 25);
        trustedServers[4] = Math.floorMod(id + 1, 25);
        trustedServers[5] = Math.floorMod(id + 4, 25);
        trustedServers[6] = Math.floorMod(id + 5, 25);
        trustedServers[7] = Math.floorMod(id + 6, 25);

        trustedTriad1 = new int[]{trustedServers[0] , trustedServers[1] , trustedServers[3] };    
        trustedTriad2 = new int[]{trustedServers[1] , trustedServers[2] , trustedServers[4] };
        trustedTriad3 = new int[]{trustedServers[3] , trustedServers[5] , trustedServers[6] };
        trustedTriad4 = new int[]{trustedServers[4] , trustedServers[6] , trustedServers[7] };

        cc =  brokeCoin;
       // cc.reportStatus();

        //To be ready, the triad must be authentic and it must be available. This can be learned by looking at the coin's status
       triad_1_is_ready = is_triad_authentic( trustedTriad1 );
       triad_2_is_ready = is_triad_authentic( trustedTriad2 );
       triad_3_is_ready = is_triad_authentic( trustedTriad3 );
      triad_4_is_ready = is_triad_authentic( trustedTriad4 );

        currentTrustedTriadTickets = null;

        ans1 = new String[] { brokeCoin.ans[trustedTriad1[0]], brokeCoin.ans[trustedTriad1[1]] , brokeCoin.ans[trustedTriad1[2]]  };
        ans2 = new String[] { brokeCoin.ans[trustedTriad2[0]], brokeCoin.ans[trustedTriad2[1]] , brokeCoin.ans[trustedTriad2[2]]  };
        ans3 = new String[] { brokeCoin.ans[trustedTriad3[0]], brokeCoin.ans[trustedTriad3[1]] , brokeCoin.ans[trustedTriad3[2]]  };
        ans4 = new String[] { brokeCoin.ans[trustedTriad4[0]], brokeCoin.ans[trustedTriad4[1]] , brokeCoin.ans[trustedTriad4[2]]  };
        
        currentTriad = trustedTriad1;//Try the first tried first
        currentAns = ans1;
        currentTriadReady = triad_1_is_ready;
    }//end of constructor

    
    /**
     * Constructor for objects of class FixitHelper
     * This is used for testing one RAIDA
     */
    public FixitHelper( int id, CloudCoin testCoin, boolean isGood )
    {
        this.id = id;
        trustedServers[0] = Math.floorMod(id - 6, 25);//T)rusted server 1 is the id of your servers minus 6 mod 25.
        trustedServers[1] = Math.floorMod(id - 5, 25);
        trustedServers[2] = Math.floorMod(id - 4, 25);
        trustedServers[3] = Math.floorMod(id - 1, 25);
        trustedServers[4] = Math.floorMod(id + 1, 25);
        trustedServers[5] = Math.floorMod(id + 4, 25);
        trustedServers[6] = Math.floorMod(id + 5, 25);
        trustedServers[7] = Math.floorMod(id + 6, 25);

        trustedTriad1 = new int[]{trustedServers[0] , trustedServers[1] , trustedServers[3] };    
        trustedTriad2 = new int[]{trustedServers[1] , trustedServers[2] , trustedServers[4] };
        trustedTriad3 = new int[]{trustedServers[3] , trustedServers[5] , trustedServers[6] };
        trustedTriad4 = new int[]{trustedServers[4] , trustedServers[6] , trustedServers[7] };

        cc =  null;
       // cc.reportStatus();

        //To be ready, the triad must be authentic and it must be available. This can be learned by looking at the coin's status
       triad_1_is_ready = true;
       triad_2_is_ready = true;
       triad_3_is_ready = true;
      triad_4_is_ready = true;

        currentTrustedTriadTickets = null;

       ans1 = new String[] { testCoin.ans[trustedTriad1[0]], testCoin.ans[trustedTriad1[1]] , testCoin.ans[trustedTriad1[2]]  };
        ans2 = new String[] { testCoin.ans[trustedTriad2[0]], testCoin.ans[trustedTriad2[1]] , testCoin.ans[trustedTriad2[2]]  };
        ans3 = new String[] { testCoin.ans[trustedTriad3[0]], testCoin.ans[trustedTriad3[1]] , testCoin.ans[trustedTriad3[2]]  };
        ans4 = new String[] { testCoin.ans[trustedTriad4[0]], testCoin.ans[trustedTriad4[1]] , testCoin.ans[trustedTriad4[2]]  };
        
        currentTriad = trustedTriad1;//Try the first tried first
        currentAns = ans1;
        currentTriadReady = triad_1_is_ready;
    }//end of constructor
    
    
    public void reportCoinStatus(){
    
       cc.reportStatus();
    }//end report coin status
    

    /***
     * This changes the Triads that will be used
     */
     public void setCornerToCheck( int mode ){
         
         switch( mode ){
            case 1:
             currentTriad = trustedTriad1;
             currentAns = ans1 ;
             currentTriadReady = triad_1_is_ready;
            break;
            case 2:
             currentTriad = trustedTriad2;
             currentAns = ans2 ;
             currentTriadReady = triad_2_is_ready;
            break;
            case 3:
             currentTriad = trustedTriad3;
             currentAns = ans3 ;
             currentTriadReady = triad_3_is_ready;
            break;
            case 4:
             currentTriad = trustedTriad4;
             currentAns = ans4 ;
             currentTriadReady = triad_4_is_ready;
            break;
            default:
             this.finnished = true;
            break;
            }//end switch
    }//End fix Guid
    
    
    
    /***
     * This changes the Triads that will be used
     */
     public void setCornerToTest( int mode ){
         
         switch( mode ){
            case 1:
             currentTriad = trustedTriad1;
             currentAns = ans1 ;
             currentTriadReady = true;
            break;
            case 2:
             currentTriad = trustedTriad2;
             currentAns = ans2 ;
             currentTriadReady = true;
            break;
            case 3:
             currentTriad = trustedTriad3;
             currentAns = ans3 ;
             currentTriadReady = true;
            break;
            case 4:
             currentTriad = trustedTriad4;
             currentAns = ans4 ;
             currentTriadReady = true;
            break;
            default:
             this.finnished = true;
            break;
            }//end switch
    }//End fix Guid
    
    
    
    
    /***
     * Checks to see if all the servers in the Triad believe that the cloud coin is authentic (Must be true)
     * Uses information from the coin
     */
    public boolean is_triad_authentic( int[] triad ){
        boolean results = false;
        
       
        if(  cc.pastStatus[triad[0]].equalsIgnoreCase("pass") && cc.pastStatus[triad[1]].equalsIgnoreCase("pass") && cc.pastStatus[triad[2]].equalsIgnoreCase("pass")  ){
            results = true;
        }
        return results;
    }//end check traid

    public void setTickets( String[] tickets){
        this.currentTrustedTriadTickets = tickets;
    }//end set tickets

    public void reportTriads()
    {
        System.out.println("Triad one is " + cc.pastStatus[trustedTriad1[0]] + ", " + cc.pastStatus[trustedTriad1[1]]+", "+cc.pastStatus[trustedTriad1[2]]);
        System.out.println("Triad two is " + cc.pastStatus[trustedTriad2[0]] + ", " + cc.pastStatus[trustedTriad2[1]]+", "+cc.pastStatus[trustedTriad2[2]]);
        System.out.println("Triad three is " + cc.pastStatus[trustedTriad3[0]] + ", " + cc.pastStatus[trustedTriad3[1]]+", "+cc.pastStatus[trustedTriad3[2]]);
        System.out.println("Triad four is " + cc.pastStatus[trustedTriad4[0]] + ", " + cc.pastStatus[trustedTriad4[1]]+", "+cc.pastStatus[trustedTriad4[2]]);

    }//End Repo

    public void reportTrustedServers()
    {
        for(int t = 0; t<8; t++)
        {
            System.out.println("Trusted server " + t + " is " +  trustedServers[t]);
        }//end for each trusted server
    }//end Re[prt Trusted Servers
}
