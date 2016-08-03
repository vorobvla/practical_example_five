/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Networking;

/**
 * <p> Describes state of connection with moderator.
 * @author Vladimir Vorobyev (vorobvla)
 * @created on Sep 7, 2014 at 12:50:59 PM
 */

public enum NetworkState {
    /** Starting state before attempts to establish connection.  */
    NOT_CONNECTED,
    /** Waiting for {@see edu.cvut.vorobvla.bap.BapMessages#MSG_CALL_FOR_PLAYERS }
     message from moderator. */
    WAITING_FOR_CALL,
    /** Message {@see edu.cvut.vorobvla.bap.BapMessages#MSG_CONNECTION_EST} has
     * been received but message 
     * {@see edu.cvut.vorobvla.bap.BapMessages#MSG_OPT_BROADCAST} has not.
     */
    CONNECTED,
    /** Message {@see edu.cvut.vorobvla.bap.BapMessages#MSG_OPT_BROADCAST} has
     * been received. */
    IN_GAME;
}
