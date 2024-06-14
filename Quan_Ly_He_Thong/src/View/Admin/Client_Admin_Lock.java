
package View.Admin;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;


public class Client_Admin_Lock extends javax.swing.JFrame {

   
    public Client_Admin_Lock() {
        initComponents();
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        BtnFresh = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        BtnHome = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        BtnComputer = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        BtnTurnoff = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        BtnLock = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        BtnProcess = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BtnFresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-refresh-50.png"))); // NOI18N
        BtnFresh.setText("Fresh");
        BtnFresh.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        BtnFresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BtnFreshMouseClicked(evt);
            }
        });

        jLabel1.setText("Fresh");

        BtnHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-home-50.png"))); // NOI18N
        BtnHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BtnHomeMouseClicked(evt);
            }
        });

        jLabel2.setText("Home");

        BtnComputer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-computer-50.png"))); // NOI18N
        BtnComputer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BtnComputerMouseClicked(evt);
            }
        });

        jLabel3.setText("Computer");

        BtnTurnoff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-turn-on-50.png"))); // NOI18N

        jLabel4.setText("Shutdown");

        BtnLock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-lock-50.png"))); // NOI18N
        BtnLock.setText("jLabel5");
        BtnLock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BtnLockMouseClicked(evt);
            }
        });

        jLabel5.setText("Lock");

        BtnProcess.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icon/icons8-process-50.png"))); // NOI18N
        BtnProcess.setText("jLabel6");

        jLabel6.setText("Process");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1))
                    .addComponent(BtnFresh, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnHome)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2)))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnComputer, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnTurnoff, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel5)
                        .addGap(40, 40, 40))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnLock, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(BtnProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(360, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(BtnProcess)
                                .addComponent(BtnLock))
                            .addComponent(BtnTurnoff)
                            .addComponent(BtnComputer))
                        .addGap(0, 6, Short.MAX_VALUE))
                    .addComponent(BtnHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnFresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Lock");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(0, 466, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnHomeMouseClicked
          Client_Admin_Computer clientAdminComputer = new Client_Admin_Computer();
        clientAdminComputer.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_BtnHomeMouseClicked

    private void BtnFreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnFreshMouseClicked
        Color originalColor = getContentPane().getBackground();
        getContentPane().setBackground(Color.darkGray);

        new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getContentPane().setBackground(originalColor);
                ((Timer)e.getSource()).stop();
            }
        }).start();
    }//GEN-LAST:event_BtnFreshMouseClicked

    private void BtnComputerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnComputerMouseClicked
         Client_Admin_Computer clientAdminComputer = new Client_Admin_Computer();
        clientAdminComputer.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_BtnComputerMouseClicked

    private void BtnLockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnLockMouseClicked
     Client_Admin_Lock clientAdminLock = new Client_Admin_Lock();
        clientAdminLock.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_BtnLockMouseClicked

   
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client_Admin_Lock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client_Admin_Lock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client_Admin_Lock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client_Admin_Lock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client_Admin_Lock().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BtnComputer;
    private javax.swing.JLabel BtnFresh;
    private javax.swing.JLabel BtnHome;
    private javax.swing.JLabel BtnLock;
    private javax.swing.JLabel BtnProcess;
    private javax.swing.JLabel BtnTurnoff;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
