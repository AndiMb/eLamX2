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
package de.elamx.clt.calculation.info;

import de.elamx.clt.CLTRefreshListener;
import de.elamx.clt.CLT_Laminate;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
public final class CLT_InfoTopComponent extends TopComponent implements LookupListener, CLTRefreshListener, PropertyChangeListener {

    public final static Set<Laminat> uniqueLaminates = new HashSet<Laminat>();
    private final Laminat laminat;
    private final CLT_Laminate clt_lam;
    private final Lookup.Result<Laminat> result;

    public CLT_InfoTopComponent(Laminat laminat) {
        this.laminat = laminat;
        this.laminat.addPropertyChangeListener(this);
        CLT_Laminate tClt_lam = laminat.getLookup().lookup(CLT_Laminate.class);
        if (tClt_lam == null) {
            clt_lam = new CLT_Laminate(laminat);
        } else {
            clt_lam = tClt_lam;
        }
        clt_lam.addCLTRefreshListener(this);
        result = eLamXLookup.getDefault().lookupResult(Laminat.class);
        result.addLookupListener(this);
        initComponents();
        setName(NbBundle.getMessage(CLT_InfoTopComponent.class, "CTL_CLT_InfoTopComponent", laminat.getName()));
        setToolTipText(NbBundle.getMessage(CLT_InfoTopComponent.class, "HINT_CLT_InfoTopComponent"));
        associateLookup(Lookups.singleton(laminat));
        setValues();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        engineeringConstantsPanel = new de.elamx.clt.calculation.info.EngineeringConstantsPanel();
        hygrothermalCoefficientPanel = new de.elamx.clt.calculation.info.HygrothermalCoefficientPanel();
        ABDMatrixPanel = new de.elamx.clt.calculation.info.ABDMatrixPanel();
        invABDMatrixPanel = new de.elamx.clt.calculation.info.InvABDMatrixPanel();
        laminateSummaryPanel1 = new de.elamx.clt.calculation.info.LaminateSummaryPanel();
        nonDimensionalParametersPanel = new de.elamx.clt.calculation.info.NonDimensionalParametersPanel();

        setAutoscrolls(true);
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ABDMatrixPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(engineeringConstantsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
            .addComponent(hygrothermalCoefficientPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(invABDMatrixPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(laminateSummaryPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(nonDimensionalParametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(ABDMatrixPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(invABDMatrixPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(engineeringConstantsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hygrothermalCoefficientPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nonDimensionalParametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(laminateSummaryPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.elamx.clt.calculation.info.ABDMatrixPanel ABDMatrixPanel;
    private de.elamx.clt.calculation.info.EngineeringConstantsPanel engineeringConstantsPanel;
    private de.elamx.clt.calculation.info.HygrothermalCoefficientPanel hygrothermalCoefficientPanel;
    private de.elamx.clt.calculation.info.InvABDMatrixPanel invABDMatrixPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private de.elamx.clt.calculation.info.LaminateSummaryPanel laminateSummaryPanel1;
    private de.elamx.clt.calculation.info.NonDimensionalParametersPanel nonDimensionalParametersPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        this.laminat.removePropertyChangeListener(this);
        clt_lam.removeCLTRefreshListener(this);
        uniqueLaminates.remove(laminat);
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (!result.allInstances().contains(laminat)) {
            this.close();
        }
    }

    private void setValues() {
        engineeringConstantsPanel.setValues(clt_lam);
        hygrothermalCoefficientPanel.setValues(clt_lam);
        nonDimensionalParametersPanel.setValues(clt_lam);
        ABDMatrixPanel.setValues(clt_lam);
        invABDMatrixPanel.setValues(clt_lam);
        laminateSummaryPanel1.setValues(laminat);
    }

    @Override
    public void refreshed() {
        setValues();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Laminat.PROP_NAME)) {
            setName(NbBundle.getMessage(CLT_InfoTopComponent.class, "CTL_CLT_InfoTopComponent", laminat.getName()));
        }
    }
}
