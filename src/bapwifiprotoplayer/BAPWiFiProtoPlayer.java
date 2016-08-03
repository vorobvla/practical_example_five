/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bapwifiprotoplayer;
import GUI.MainFrame;
import java.io.IOException;
        
/**
 * <p> Main class of the application.
 * @author Vladimir Vorobyev (vorobvla)
 * @created on Sep 7, 2014 at 12:50:59 PM
 */

public class BAPWiFiProtoPlayer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //Networking.getInstance().establishConnectionWithModerator();
        //    networking.recieveUDP();

        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

}
