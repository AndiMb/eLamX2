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
package de.elamx.clt.optimizationui;

import de.elamx.laminate.optimization.ConstraintDefinitionService;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public final class OptimizationVisualPanel2 extends JPanel implements ExplorerManager.Provider, PropertyChangeListener, Lookup.Provider {

    private final ExplorerManager explorerManager = new ExplorerManager();

    private final ConstraintDefinitionHolder defHolder = new ConstraintDefinitionHolder();
    private final Lookup lookup;

    /**
     * Creates new form OptimizationVisualPanel2
     */
    public OptimizationVisualPanel2() {
        initComponents();

        ArrayList<ConstraintDefinitionService> services = new ArrayList<>();
        for (ConstraintDefinitionService service : Lookup.getDefault().lookupAll(ConstraintDefinitionService.class)) {
            services.add(service);
        }
        // Sortieren der Liste nach den Name der möglichen Constraints
        Collections.sort(services, new Comparator<ConstraintDefinitionService>() {
            @Override
            public int compare(ConstraintDefinitionService o1, ConstraintDefinitionService o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        DefaultComboBoxModel<ConstraintDefinitionService> serviceModel = new DefaultComboBoxModel<>(services.toArray(new ConstraintDefinitionService[services.size()]));
        constrainsComboBox.setModel(serviceModel);

        AbstractNode rootNode = new AbstractNode(Children.create(new ConstraintNodeFactory(), false));
        explorerManager.setRootContext(rootNode);
        explorerManager.addPropertyChangeListener(this);

        beanTreeView1.setRootVisible(false);
        
        lookup = ExplorerUtils.createLookup (explorerManager, getActionMap());
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(OptimizationVisualPanel2.class, "OptimizationVisualPanel2.name");
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
    // ...methods as before, but replace componentActivated and
    // componentDeactivated with e.g.:
    @Override
    public void addNotify() {
        super.addNotify();
        ExplorerUtils.activateActions(explorerManager, true);
    }
    @Override
    public void removeNotify() {
        ExplorerUtils.activateActions(explorerManager, false);
        super.removeNotify();
    }

    private class ConstraintNodeFactory extends ChildFactory<Node> implements PropertyChangeListener {

        public ConstraintNodeFactory() {
            defHolder.addPropertyChangeListener(this);
        }

        @Override
        protected Node createNodeForKey(Node key) {
            return key; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected boolean createKeys(List<Node> toPopulate) {
            for (Node n : defHolder.getNodes()){
                toPopulate.add(n);
            }
            //toPopulate.add("allNodes" + Math.random());
            return true;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            this.refresh(true);
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (isVisible() && evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
            Node[] selNodes = explorerManager.getSelectedNodes();
            if (selNodes.length > 0) {
                propertySheet1.setNodes(new Node[]{selNodes[selNodes.length - 1]});
            } else {
                propertySheet1.setNodes(new Node[0]);
            }
        }
    }
    
    public ArrayList<MinimalReserveFactorCalculator> getCalculators(){
        ArrayList<MinimalReserveFactorCalculator> calculators = new ArrayList<>();
        for (Node n : defHolder.getNodes()) {
            MinimalReserveFactorCalculator mri = n.getLookup().lookup(MinimalReserveFactorCalculator.class);
            if (mri != null) {
                calculators.add(mri.getCopy());
            }
        }
        return calculators;
    }
    
    public void setCalculators(ArrayList<MinimalReserveFactorCalculator> calculators){
        HashMap<String, ConstraintDefinitionService> services = new HashMap<>();
        for (ConstraintDefinitionService service : Lookup.getDefault().lookupAll(ConstraintDefinitionService.class)) {
            services.put(service.getMRFCClassName(), service);
        }
        
        defHolder.clear();
        for (MinimalReserveFactorCalculator c : calculators) {
            System.out.println("" + c.getClass().getName());
            defHolder.addNode(services.get(c.getClass().getName()).getNode(c));
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        constraintLabel = new javax.swing.JLabel();
        constrainsComboBox = new javax.swing.JComboBox<>();
        addRestrictionButton = new javax.swing.JButton();
        constraintListLabel = new javax.swing.JLabel();
        beanTreeView1 = new org.openide.explorer.view.BeanTreeView();
        constraintPropertiesLabel = new javax.swing.JLabel();
        propertySheet1 = new org.openide.explorer.propertysheet.PropertySheet();

        org.openide.awt.Mnemonics.setLocalizedText(constraintLabel, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel2.class, "OptimizationVisualPanel2.constraintLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addRestrictionButton, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel2.class, "OptimizationVisualPanel2.addRestrictionButton.text")); // NOI18N
        addRestrictionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRestrictionButtonActionPerformed(evt);
            }
        });

        constraintListLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(constraintListLabel, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel2.class, "OptimizationVisualPanel2.constraintListLabel.text")); // NOI18N

        constraintPropertiesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(constraintPropertiesLabel, org.openide.util.NbBundle.getMessage(OptimizationVisualPanel2.class, "OptimizationVisualPanel2.constraintPropertiesLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(constraintLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(constrainsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addRestrictionButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(beanTreeView1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(constraintListLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(constraintPropertiesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(propertySheet1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(constraintLabel)
                    .addComponent(constrainsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addRestrictionButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(constraintListLabel)
                    .addComponent(constraintPropertiesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(propertySheet1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(beanTreeView1, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addRestrictionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRestrictionButtonActionPerformed
        defHolder.addNode(((ConstraintDefinitionService) constrainsComboBox.getSelectedItem()).getNewNode());
    }//GEN-LAST:event_addRestrictionButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRestrictionButton;
    private org.openide.explorer.view.BeanTreeView beanTreeView1;
    private javax.swing.JComboBox<ConstraintDefinitionService> constrainsComboBox;
    private javax.swing.JLabel constraintLabel;
    private javax.swing.JLabel constraintListLabel;
    private javax.swing.JLabel constraintPropertiesLabel;
    private org.openide.explorer.propertysheet.PropertySheet propertySheet1;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }
}
