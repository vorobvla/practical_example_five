/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Model.Game;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Vladimir Vorobyev (vorobvla)
 */
public class GameInfoPanel extends javax.swing.JPanel {

    /**
     * Creates new form gameInfoPanel
     */
    public GameInfoPanel() {
        initComponents();
    }
    
    public void setupTable(){
        Game.getInstance().startGame();
        System.out.println(Game.getInstance().getTable().toString());
        Object [][] data = new Object[2][Game.getInstance().getTable().size()];        
        data[0] = (Game.getInstance().getTable().keySet().toArray());
        data[1] = (Game.getInstance().getTable().values().toArray());
        Object [] colNames = {"Palyer", "Score"};
        scoreTable.setModel(new DefaultTableModel(data, colNames));
    }
    
    public void update(){
        CurrentStateLabel.setText(Game.getInstance().getGameState().toString());
        scoreTable.repaint();
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

        CurrentStateLabel = new javax.swing.JLabel();
        AnsweringLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scoreTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        logArea = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Game info\n"));
        setMinimumSize(new java.awt.Dimension(280, 80));
        setPreferredSize(new java.awt.Dimension(280, 80));
        setLayout(new java.awt.GridBagLayout());

        CurrentStateLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        add(CurrentStateLabel, gridBagConstraints);

        AnsweringLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(AnsweringLabel, gridBagConstraints);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Scores"));
        jScrollPane1.setToolTipText("");
        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 158));

        scoreTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scoreTable.setMinimumSize(new java.awt.Dimension(180, 70));
        scoreTable.setPreferredSize(new java.awt.Dimension(180, 70));
        jScrollPane1.setViewportView(scoreTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 196;
        gridBagConstraints.ipady = 241;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Log"));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(100, 92));

        logArea.setEditable(false);
        logArea.setColumns(20);
        logArea.setRows(5);
        jScrollPane2.setViewportView(logArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 233;
        gridBagConstraints.ipady = 245;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AnsweringLabel;
    private javax.swing.JLabel CurrentStateLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea logArea;
    private javax.swing.JTable scoreTable;
    // End of variables declaration//GEN-END:variables
}
