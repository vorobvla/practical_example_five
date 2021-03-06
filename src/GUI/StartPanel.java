/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;
import Model.*;
import Networking.NetworkState;
import Networking.Networking;
import java.awt.event.ItemEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Vladimir Vorobyev (vorobvla)
 */
public class StartPanel extends javax.swing.JPanel {

    /**
     * Creates new form StartPanel
     */
    public StartPanel() {
        initComponents();     
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PlayerField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        readyRadioButton = new javax.swing.JRadioButton();
        tipLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Starting setup"));
        setLayout(new java.awt.GridBagLayout());

        PlayerField.setText(Player.getInstance().getName());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 162;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(PlayerField, gridBagConstraints);

        jLabel1.setText("Player Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel1, gridBagConstraints);

        readyRadioButton.setText("Ready");
        readyRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                readyRadioButtonItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(readyRadioButton, gridBagConstraints);

        tipLabel.setText("Print the player name and click \"Ready\".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.ipady = 11;
        add(tipLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    synchronized private void readyForGame(){
        Networking.getInstance().setState(NetworkState.WAITING_FOR_CALL);
        if (PlayerField.getText().matches("\\s*")){
            JOptionPane.showMessageDialog(this,
            "Player name must not be empty",
            "Invalid Player name",
            JOptionPane.ERROR_MESSAGE);
            return;
        }
        PlayerField.setEditable(false);
        Player.getInstance().setName(PlayerField.getText());
        Networking.getInstance().startNetworkingListener();    
        //wait the game to start
        new Thread(){            
            @Override
            public void run() {                        
                while (Networking.getInstance().getState() != NetworkState.IN_GAME){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StartPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                startGame();
            }
        }.start();
        
    }
    
    private void startGame(){
        Player.getInstance().setName(PlayerField.getText());
        Game.getInstance().startGame();
        GamePanel.setup();
        MainFrame.showCard("gamePanel");        
    }
    
    synchronized private void notReadyForGame(){
        Networking.getInstance().setState(NetworkState.NOT_CONNECTED);
        Networking.getInstance().interrtuptCallListener();
        PlayerField.setEditable(true);
        tipLabel.setText(Constants.TIP_START);        
    }
    
    private void readyRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_readyRadioButtonItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED){
            tipLabel.setText(Constants.TIP_WAIT_FOR_CALL);
            readyForGame();
        } else {
            notReadyForGame();
        }
    }//GEN-LAST:event_readyRadioButtonItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField PlayerField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton readyRadioButton;
    private javax.swing.JLabel tipLabel;
    // End of variables declaration//GEN-END:variables



    
    
}
