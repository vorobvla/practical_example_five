/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;

import Networking.Networking;
import edu.cvut.vorobvla.bap.BapJSONKeys;
import edu.cvut.vorobvla.bap.GameStateEnum;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;


/**
 * <p> Represents the game that is being played by the player.
 * The game algorithm is implemented in moderator application,
 * this class just stores information received from moderator.
 * <p> This class is implemented as a singleton.
 * @author Vladimir Vorobyev (vorobvla)
 * @created on Sep 7, 2014 at 12:50:59 PM
 */

public class Game {
    /** The  single instance of {@see Game} object (typical for 
     * singleton implementation). */
    private static Game instance;
    
    /** Describes the state of the game. */
    private String state;
    /** Describes identity of the player that answers. */
    private String answeringPlayerID;
    /** Contains identities of players participating in this game (as keys)
     * and their current score (as values).
     */
    private HashMap<String, Integer> scoreTable;
    /**
     * While running, updates information stored in this class.
     */
    private Thread gameListener;

    /**
     * Constructs a new {@see #Game} object. Initializes 
     * {@see #scoreTable} as an empty {@code HashMap}
     * and {@see #gameListener} as an anonymous child of {@code Tread}
     * with overridden method {@code run()}.
     */
    public Game() {
        scoreTable = new HashMap<>();
        state = GameStateEnum.BEFORE.toString();
        gameListener = new Thread(){
                @Override
                public void run() {
                    while (true){
                        updateFromNetwork();
                    }
                }                
            };
    }
    
    /**
     * Typical singleton method to get the only instance of this object.
     * @return the only instance of {@see Game} object.
     */
    public static Game getInstance() {
        if (instance == null){
            instance = new Game();
        }
        return instance;
    }

    /**
     * Returns value of {@see #state}.
     * @return value of {@see #state}.
     */
    public  String getGameState() {
        return state;
    }

    /**
     * Returns {@see #scoreTable}.
     * @return {@see #scoreTable}.
     */
    public  HashMap getTable() {
        return scoreTable;
    }
    
    /**
     * Updates score of specified player (adds a new player to {@see #scoreTable}
     * if needed).
     * @param player identifier of the player.
     * @param score desired score value for the player.
     */
    public  void updateScoreTable(String player, int score){
        scoreTable.put(player, score);
    }

    /**
     * Returns identifier of the player that answers ({@see #answeringPlayerID}).
     * @return {@see #answeringPlayerID}.
     */
    public  String getAnsweringPlayer() {
        return answeringPlayerID;
    }
    
    /**
     * Resets all fields to default values (as they were initialized in constructor).
     */
    public  void reset(){
        state = GameStateEnum.BEFORE.toString();
        scoreTable.clear();
        answeringPlayerID = "";
    }
    
    /**
     * Updates {@see #state}, {@see #answeringPlayerID} and the content of
     * {@see #scoreTable} from JSONObject received from moderator via network. 
     * (This method does not work right because of buggy {@see Networking#recvGameInfo})
     */
    private  void updateFromNetwork(){
        JSONObject info = Networking.getInstance().recvGameInfo();
        if (info == null){
            System.out.println("Update is null");
            return;
        };
        state = (String) info.get(BapJSONKeys.KEY_STATE);
        answeringPlayerID = (String) info.get(BapJSONKeys.KEY_ANSWERING_PLAYER);
        System.out.println(info);
        System.out.println(state.toString());
        System.out.println(answeringPlayerID);
        System.out.println("UPD state" + state);
        
    }
    
    /**
     * If information broadcasting is enabled ({@see Networking.isBroadcast})
     * starts {@see #gameListener}. Otherwise does nothing.
     */
    public  void startGame(){
    //    updateFromNetwork();
        if (!Networking.getInstance().isBroadcast()){
            return;
        }
        gameListener.start();
    //wait for data from moderator
        while(state.matches(GameStateEnum.BEFORE.toString())){
            try {
                System.out.println("Waiting for game info");
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    /**
     * Interrupts {@see #gameListener}. (Maybe redundant).
     */
    public  void finishGame(){
        gameListener.interrupt();
    }
}
