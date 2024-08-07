/*
 *  This program developed in Java is based on the netbeans platform and is used
 *  to design and to analyse composite structures by means of analytical and 
 *  numerical methods.
 * 
 *  Further information can be found here:
 *  http://www.elamx.de
 *    
 *  Copyright (C) 2021 Technische Universität Dresden - Andreas Hauffe
 * 
 *  This file is part of eLamX².
 *
 *  eLamX² is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  eLamX² is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with eLamX².  If not, see <http://www.gnu.org/licenses/>.
 */
package de.elamx.clt.springinui;

import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.springin.SpringInModel;
import de.elamx.clt.springin.SpringInResult;
import java.awt.EventQueue;
import org.jfree.chart.JFreeChart;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Andreas Hauffe
 */
public class ControlPanel extends javax.swing.JPanel {
    
    private final SpringInModuleData data;
    private JFreeChart chart;
    
    public ControlPanel(){
        this(null, null);
    }

    /**
     * Creates new form ControlPanel
     */
    @SuppressWarnings("this-escape")
    public ControlPanel(SpringInModuleData data, JFreeChart chart) {
        this.data = data;
        this.chart = chart;
        
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

        inputPanel = new InputPanel(data);
        calculationButton = new javax.swing.JButton();
        resultPanel = new ResultPanel(data, chart);

        org.openide.awt.Mnemonics.setLocalizedText(calculationButton, org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.calculationButton.text")); // NOI18N
        calculationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculationButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(calculationButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inputPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(inputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calculationButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void calculationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculationButtonActionPerformed
        calculationButton.setEnabled(false);
        //resultPanel.initListeners();
        final CLT_Laminate laminat = data.getLaminat().getLookup().lookup(CLT_Laminate.class);
        final ProgressHandle ph = ProgressHandle.createSystemHandle(NbBundle.getMessage(ControlPanel.class, "Task.springincalc"), null);
        if (checkInput(laminat)){
            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    ph.start();
                    SpringInModel model = data.getSpringInInput().getModel();
                    final SpringInResult result = model.getResult(laminat, data.getSpringInInput());
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            data.setResult(result);
                            calculationButton.setEnabled(true);
                            ph.finish();
                        }
                    });
                }
            });
        }else{
            calculationButton.setEnabled(true);
        }
    }//GEN-LAST:event_calculationButtonActionPerformed

    private boolean checkInput(CLT_Laminate laminat) {
        if (laminat.getCLTLayers().length == 0){
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(ControlPanel.class, "Warning.nolaminate"), NotifyDescriptor.WARNING_MESSAGE));
            return false;
        }
        String errorText = data.getSpringInInput().getModel().checkInput(laminat);
        if (errorText != null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(errorText, NotifyDescriptor.WARNING_MESSAGE));
            return false;
        }
        if (laminat.getTges()/2.0 > data.getSpringInInput().getRadius()){
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(ControlPanel.class, "Warning.radiustoosmall"), NotifyDescriptor.WARNING_MESSAGE));
            return false;
        }
        return true;
    }
    
    public void cleanup(){
        resultPanel.cleanup();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calculationButton;
    private de.elamx.clt.springinui.InputPanel inputPanel;
    private de.elamx.clt.springinui.ResultPanel resultPanel;
    // End of variables declaration//GEN-END:variables
}
