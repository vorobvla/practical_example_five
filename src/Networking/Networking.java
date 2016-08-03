/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Networking;

import Model.Player;
import edu.cvut.vorobvla.bap.BapJSONKeys;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.cvut.vorobvla.bap.BapMessages;
import edu.cvut.vorobvla.bap.BapPorts;
import java.io.Closeable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
 

/**
 * <p> Enables local network communication with moderator.
 * <p>The communication protocol is described in documentation
 * of {@code CommonLib} (see {@see edu.cvut.vorobvla.bap.BapMessages}).
 * <p> This class is implemented as a singleton.
 * @author Vladimir Vorobyev (vorobvla)
 * @created on Sep 7, 2014 at 12:50:59 PM
 */

public class Networking implements Closeable{
    /** Number of port that is used for listening to moderator's broadcast. */
    private int playerPortUDP;
    /** Input buffer used for storing data received via UDP. */
    private byte[] udpInData;
    /** Socket for listening to moderator's broadcast. */
    private DatagramSocket udpSocket;
    /** Socket for TCP-based communication with moderator. */
    private Socket tcpSocket;
    /** Handles input from {@see #tcpSocket}. */
    private BufferedReader inTCP;
    /** Handles output to {@see #tcpSocket}. */
    private PrintWriter outTCP;
    /** The  single instance of {@see Networking} object (typical for 
     * singleton implementation). */ 
    private static Networking instance;
    /** Describes network communication state. */
    private NetworkState state;
    /** Thread to perform listening for moderator's broadcast of 
     * {@see edu.cvut.vorobvla.bap.BapMessages#MSG_CALL_FOR_PLAYERS} message. */
    private Thread networkListener;
    /** Thread to perform listening for moderator's broadcast of 
     *  game information. (maybe redundant, will be fixed in next versions) */
    private Thread gameListener;
    /** Handles parsing of JSONObjects with game information. */
    private JSONParser parser;
    /** Timestamp parsed from the latest JSONObjects that has been received.
     * Needed for ignoring incoming information if it is not up-to-date.*/
    private long broadcastLatestTimestamp;
    /** If moderator enabled broadcasting. */
    private boolean broadcast;
    
    /**
     * Typical singleton method to get the only instance of this object.
     * @return the only instance of {@see #Networking} object.
     */
    public static Networking getInstance() {
        if (instance == null){
            try {
                instance = new Networking();
            } catch (SocketException ex) {
                Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }  

    /**
     * Constructs a new {@see #Networking} object. Sets {@see #state} to 
     * {@see Networking.NetworkState#NOT_CONNECTED}, initializes {@see #udpInData}
     * as array of 512 bytes. Sets {@see #playerPortUDP} to value defined in 
     * {@see edu.cvut.vorobvla.bap.BapPorts#PLAYER_PORT} and initializes
     * {@see udpSocket} with this value as constructor parameter (a port to
     * bind with this socket). If initialization fails 
     * ({@code SocketException} occurs) increments {@see #playerPortUDP} and
     * attempts to initialize {@see udpSocket} with parameter {@see #playerPortUDP}.
     * Proceeds like this until initialization succeeds or until {@see #playerPortUDP}
     * reaches {@see edu.cvut.vorobvla.bap.BapPorts#PLAYER_PORT}{@code + }
     * {@see edu.cvut.vorobvla.bap.BapPorts#PLAYER_PORT_RANGE}{@code - 1}.
     * Initializes {@see networkListener} as an anonymous {@code Thread} child
     * with overridden {@code run()} method. 
     * <p> The {@code run()} method
     * the greatest part of the network protocol (waits for
     * {@see edu.cvut.vorobvla.bap.BapMessages#MSG_CALL_FOR_PLAYERS}, processes it
     * and calls {@see establishConnectionWithModerator} and {@see recvOpts}).
     * <p>Constructor of {@see #Networking} proceeds with setting {@see #gameListener}
     * to {@code null}, {@see #broadcastLatestTimestamp} to {@code 0} and
     * initializing {@see #parser}.
     * 
     * @throws SocketException if all attempts to initialize {@see udpSocket}
     * were unsuccessful.
     */
    private Networking() throws SocketException { 
        state = NetworkState.NOT_CONNECTED;
        this.playerPortUDP = BapPorts.PLAYER_PORT;
    //    identity = Player.getInstance().getName();
        udpInData = new byte[512];
        while(udpSocket == null){//may be bug
            try {
                udpSocket = new DatagramSocket(playerPortUDP);
            } catch (SocketException ex) {
                if (playerPortUDP < BapPorts.PLAYER_PORT + BapPorts.PLAYER_PORT_RANGE){
                    Logger.getLogger(Networking.class.getName()).log(Level.INFO, "bad UDP port " + 
                            playerPortUDP + ". trying another...", "bad UDP port " + 
                            playerPortUDP + ". trying another...");
                    playerPortUDP++;
                    
                } else {
                    throw new SocketException("no suttable UDP port");
                    //Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, "no suttable UDP port");
                    //break;
                }
            }  
        }   
        networkListener = new Thread(){

            @Override
            public void run() {
                    try {
                    //    System.out.println("Start listening to UDP broadcast on"
                      //  + udpSocket.getLocalSocketAddress().toString());
                        DatagramPacket initMsgFromModerator = recvUDP();
                        String msg = new String(initMsgFromModerator.getData()); 
                        if (!msg.split(":")[0].matches(BapMessages.MSG_CALL_FOR_PLAYERS)){
                            System.err.println("wrong message. got '" + msg + "' while '" + 
                                    BapMessages.MSG_CALL_FOR_PLAYERS + "' expexted");
                            return;
                        }
                        System.out.println("RECEVED: " + msg);
                        //get the moderator's IP from the pachage and the port that is listen by it from the message                 
                    
                        establishConnectionWithModerator(initMsgFromModerator.getAddress(),
                                Integer.parseInt(msg.split(BapMessages.FIELD_DELIM)[1].replaceAll("\\D", "")));
                        recvOpts();
                    } catch (IOException ex) {
                        Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
                        System.err.println("exception while establishing connection");
                    }                               
            }        
        };
       
        gameListener = null;
        parser = new JSONParser();
        broadcastLatestTimestamp = 0;
    //    establishConnectionWithModerator();
    }
    
    /**
     * If state is {@see Networking.NetworkState#WAITING_FOR_CALL} 
     * starts {@see #networkListener} otherwise does nothing.
     */
    synchronized public void startNetworkingListener(){
        //recieve initializing message from the moderator.
        if (state != NetworkState.WAITING_FOR_CALL){
            return;
        }
        if (networkListener.getState() != Thread.State.RUNNABLE){           
            networkListener.start();
        }
    }
    
    /**
     * Initializes {@see #tcpSocket} with specified parameters (address and
     * port listened by moderator's server) and 
     * establishes TCP connection with moderator's server.
     * Sends via TCP {@see edu.cvut.vorobvla.bap.BapMessages#MSG_INIT_CONNECTION}
     * message with {@see Model.Player.name} as identifier of this player and waits
     * for message {@see edu.cvut.vorobvla.bap.BapMessages#MSG_CONNECTION_EST}
     * from moderator.
     * @param moderatorAddr address of moderator's server (used as constructor 
     * parameter while initializing {@see #tcpSocket}).
     * @param moderatorTcpPort port of moderator's server (used as constructor 
     * parameter while initializing {@see #tcpSocket}).
     * 
     * @throws IOException if {@see edu.cvut.vorobvla.bap.BapMessages#MSG_CONNECTION_EST}
     * is not received when expected.
     */
    private void establishConnectionWithModerator(InetAddress moderatorAddr, 
            int moderatorTcpPort) throws IOException{
        System.out.println("moderatorAddr " + moderatorAddr + 
                            "; moderatorTcpPort " + moderatorTcpPort);
        tcpSocket = new Socket(moderatorAddr, moderatorTcpPort);
        tcpSocket.setReuseAddress(false);
        inTCP = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        outTCP = new PrintWriter(tcpSocket.getOutputStream(), true);
        sendMsg(BapMessages.MSG_INIT_CONNECTION + BapMessages.FIELD_DELIM + Player.getInstance().getName());
        String msg = recvMsg();
        System.out.println("RECEVED: " + msg);
        if (msg.matches(BapMessages.MSG_CONNECTION_EST)){
            System.out.println("Connected to moderator");
            state = NetworkState.CONNECTED;
        } else {
            throw new IOException("Connection failed");
        }
    }
    
    /**
     * Receives {@see edu.cvut.vorobvla.bap.BapMessages#MSG_CONNECTION_EST}
     * message with information about broadcast options. Sets {@see #broadcast}
     * to {@code true} if {@see edu.cvut.vorobvla.bap.BapMessages#OPT_ON}
     * and to {@code false} otherwise.
     * @throws IOException if thrown by {@see #recvMsg} (is called to
     * receive message).
     * @throws IllegalStateException with {@code message} 
     * {@code "Illegal network state"} if called while {@see #state}
     * is not {@see Networking.NetworkState#CONNECTED}.
     */
    private void recvOpts() throws IOException, IllegalStateException{
        if (state != NetworkState.CONNECTED){
            throw new IllegalStateException("Illegal network state");
        }
        String msg = recvMsg();
        System.out.println("GOT: " + msg);
        if (msg.split(BapMessages.FIELD_DELIM)[0].matches(BapMessages.MSG_OPT_BROADCAST)){
            if (msg.split(BapMessages.FIELD_DELIM)[1].matches(BapMessages.OPT_ON)){
                broadcast = true;
            } else {
                broadcast = false;
            }
        }
        state = NetworkState.IN_GAME;
    }

    /**
     * If broadcast is enabled.
     * @return value of {@see #broadcast}
     */
    public boolean isBroadcast() {
        return broadcast;
    }
    
    

 /*   
    public void startModeratorListener(){
        if(state != NetworkState.CONNECTED){
            throw new IllegalStateException("Illegal Network state");
        }
        if (moderatorListener == null){
            udpListener = new Thread() {
                @Override
                public void run() {
                   String msg;
                   try {
                        while(true){
                                msg = inTCP.readLine();
                                
                        /*        switch(msg){
                                    case BapMessages.MSG_GAME_FIN :
                                        
                                        break;
                                    default : throw new SecurityException("Unexpected message from moderator");
                                }*//*
                            } 
                    }
                    catch (IOException ex) {
                    Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
                   } 
                }
            };
            moderatorListener.start();
        }
    }*/
    
    
   /**
    * Receives data via UDP on {@see #udpSocket}.
    * @return {@code DatagramPacket} the packet received.
    */
    private DatagramPacket recvUDP() {
        try {
            // while(true){
            DatagramPacket receivePacket = new DatagramPacket(udpInData, udpInData.length);
       //     System.out.println("Listen UDP on " + udpSocket.getLocalSocketAddress().toString());
            udpSocket.receive(receivePacket);
       //     System.out.println("Listen UDP on " + udpSocket.getLocalAddress().toString());
            String msg = new String(receivePacket.getData());
        //    System.out.println("recvUDP: " + msg);
        //    System.out.println(" at " + udpSocket.getLocalSocketAddress().toString());
            return receivePacket;        
        } catch (IOException ex) {
            Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("exception while broadcasting UDP");
        }
        return null;       
    }
    
    /**
     * Receives data via UDP (calling {@see #recvUDP}) and 
     * parses it to {@code JSONObject}. Gets from this {@code JSONObject}
     * information about time of sending (with key 
     * {@see edu.cvut.vorobvla.bap.BapJSONKeys#KEY_BROADCAST_TIMESTAMP}).
     * If the data is up-to-date (time of sending is greater then
     * {@see #broadcastLatestTimestamp}) sets {@see #broadcastLatestTimestamp}
     * to the time of sending and returns the parsed JSONObject. Otherwise
     * returns {@code null}
     * @return {@code JSONObject} with game info normally, {@code null}
     * if the information is invalid ({@code ParseException occurs}) or not
     * up-to-date.
     */
    public JSONObject recvGameInfo(){
        System.out.println("recvGameInfo");
        String str = new String(recvUDP().getData());
        try {
            JSONObject jobj = (JSONObject) parser.parse(str);
            long tstmp = (long) jobj.get(BapJSONKeys.KEY_BROADCAST_TIMESTAMP);
            if (tstmp < broadcastLatestTimestamp){
                System.out.println("Timestamp ignore");
                return null;
                //ignore info that is not up-to-date
            }
            broadcastLatestTimestamp = tstmp;
            System.out.println(jobj);
            return jobj;
        } catch (ParseException ex) {
            System.out.println("ParseException "  + ex.toString());
            return null;
        }
    }
    
    /**
     * Sends message with specified content to moderator with TCP protocol.
     * @param msg desired message to send.
     */
    public void sendMsg(String msg){
        outTCP.println(msg);
    }
    
    /**
     * Receives message from moderator with TCP protocol.
     * @return {@code String} with content of the received message.
     * @throws IOException if occurs wile receiving (while calling 
     * {@code inTCP.readLine()}) 
     */
    private String recvMsg() throws IOException{
        return inTCP.readLine();
    }

    /**
     * Closes {@see #tcpSocket}.
     * @throws IOException caused by {@code tcpSocket.close()}.
     */
    @Override
    public void close() throws IOException {
        tcpSocket.close();
    }

    /**
     * Interrupts {@see #networkListener}. (maybe useless).
     */
    public void interrtuptCallListener() {
        networkListener.interrupt();
    }

    /**
     * Returns {@see #state} of network communication with moderator.
     * @return {@see #state}.
     */
    public NetworkState getState() {
        return state;
    }

    /**
     * Sets {@see #state} specified value.
     * @param state the desired value.
     */
    public void setState(NetworkState state) {
        this.state = state;
    }    
    
    /**
     * Informs moderator about terminating connection (
     * sends message {@see edu.cvut.vorobvla.bap.BapMessages#MSG_CONNECTION_TERM}).
     */
    public void sendConnTerm(){
        if (state == NetworkState.CONNECTED){
            sendMsg(BapMessages.MSG_CONNECTION_TERM);
            state = NetworkState.NOT_CONNECTED;
        }
    }
}
