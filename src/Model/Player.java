/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Model;
import Networking.Networking;
import edu.cvut.vorobvla.bap.BapMessages;

/**
 * <p> Represents player.
 * <p> This class is implemented as a singleton.
 * @author Vladimir Vorobyev (vorobvla)
 * @created on Sep 7, 2014 at 12:50:59 PM
 */

//singletone

public class Player {
    /** Identifier of the player. */
    private String name;   
    /** The  single instance of {@see Player} object (typical for 
     * singleton implementation). */
    private static Player instance;

    /**
     * Constructs a new {@see #Player} object with {@see name} set to
     * {@see Constants#DEFAULT_PLAYER_NAME}.
     */
    private Player() {
        name = Constants.DEFAULT_PLAYER_NAME;
    }
    
    /**
     * Typical singleton method to get the only instance of this object.
     * @return the only instance of {@see #Game} object.
     */    
    public static Player getInstance(){
        if (instance == null){
            instance = new Player();
        }
        return instance;
    }

    /**
     * Sets {@see #name} to specified content.
     * @param name desired content of {@see #name}.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Return content of {@see #name}.
     * @return content of {@see #name}. 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Apply for answer the current question.
     */
    public static void applyForAnswer(){
        Networking.getInstance().sendMsg(BapMessages.MSG_ANSWER_APPLYING);
    }
    
    
    
    
}
