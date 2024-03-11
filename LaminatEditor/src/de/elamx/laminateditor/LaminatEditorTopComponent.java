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
package de.elamx.laminateditor;

import de.elamx.core.GlobalProperties;
import de.elamx.core.LaminateStringParser;
import de.elamx.laminate.DataLayer;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.Puck;
import de.elamx.laminateditor.LayerNodeFactory.LayerNode;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.NodePopupFactory;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "LaminatEditorTopComponent",
        iconBase = "de/elamx/laminateditor/resources/laminate.png"
)
public final class LaminatEditorTopComponent extends TopComponent implements ExplorerManager.Provider, PropertyChangeListener, LookupListener {

    private static final DecimalFormat thicknessFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_THICKNESS);
    private static final DecimalFormat angleFormat = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE);

    public final static Set<Laminat> uniqueLaminates = new HashSet<>();
    private final Laminat laminat;
    private final ExplorerManager explorerManager = new ExplorerManager();
    private final Lookup.Result<Laminat> laminatResult;
    private final Lookup.Result<LayerMaterial> materialResult;
    private final Lookup.Result<Criterion> failureResult;
    private boolean updateOffset = true;

    public LaminatEditorTopComponent(Laminat laminat) {
        this.laminat = laminat;
        this.laminat.addPropertyChangeListener(this);

        laminatResult = eLamXLookup.getDefault().lookupResult(Laminat.class);
        laminatResult.addLookupListener(this);

        materialResult = eLamXLookup.getDefault().lookupResult(LayerMaterial.class);
        materialResult.addLookupListener(new MaterialLookupListener());

        Lookup lkp = Lookups.forPath("elamx/failurecriteria");
        failureResult = lkp.lookupResult(Criterion.class);
        failureResult.addLookupListener(new CriterionLookupListener());

        initComponents();
        invertZCheckBox.setSelected(GlobalProperties.getDefault().isInvertZDefault());
        setName(NbBundle.getMessage(LaminatEditorTopComponent.class, "CTL_LaminatEditorTopComponent", laminat.getName()));
        setToolTipText(NbBundle.getMessage(LaminatEditorTopComponent.class, "HINT_LaminatEditorTopComponent"));
        initView();
        associateLookup(new ProxyLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()), Lookups.singleton(laminat)));
        updateMaterialComboBox();
        updateFailureComboBox();

        //enterKeyListener kl = new enterKeyListener();
        //anglesField.addKeyListener(kl);
        TextFieldFocusListener tffl = new TextFieldFocusListener();
        anglesField.addFocusListener(tffl);
        //thicknessField.addFocusListener(tffl);
    }

    private void initView() {
        outlineView1.setPropertyColumns(DataLayer.PROP_ANGLE, NbBundle.getMessage(LayerNode.class, "LayerNode.Angle"),
                DataLayer.PROP_THICKNESS, NbBundle.getMessage(LayerNode.class, "LayerNode.Thickness"),
                DataLayer.PROP_MATERIAL, NbBundle.getMessage(LayerNode.class, "LayerNode.Material"),
                DataLayer.PROP_CRITERION, NbBundle.getMessage(LayerNode.class, "LayerNode.Criterion"),
                "Number", NbBundle.getMessage(LayerNode.class, "LayerNode.Number"),
                "ZM", NbBundle.getMessage(LayerNode.class, "LayerNode.ZM"));
        ((DefaultOutlineModel) outlineView1.getOutline().getModel()).setNodesColumnLabel(NbBundle.getMessage(LayerNode.class, "LayerNode.Name"));
        outlineView1.getOutline().getColumnModel().moveColumn(5, 0);
        outlineView1.getOutline().getColumnModel().moveColumn(6, 1);
        outlineView1.getOutline().getColumnModel().getColumn(0).setPreferredWidth(20);
        for (int ii = 1; ii < outlineView1.getOutline().getColumnModel().getColumnCount(); ii++) {
            outlineView1.getOutline().getColumnModel().getColumn(ii).setPreferredWidth(150);
        }
        outlineView1.getOutline().setRootVisible(false);
        outlineView1.getOutline().setRowSorter(null);
        outlineView1.getOutline().setDragEnabled(true);
        NodePopupFactory npf = new NodePopupFactory() {
            @Override
            public JPopupMenu createPopupMenu(int row, int column, Node[] selectedNodes, Component component) {
                return super.createPopupMenu(row, column, selectedNodes, component);
            }
        };
        outlineView1.setNodePopupFactory(npf);
        outlineView1.getNodePopupFactory().setShowQuickFilter(false);
        explorerManager.setRootContext(new LaminateNode(new LayerNodeFactory(laminat)));
        symmetricCheckBox.setSelected(laminat.isSymmetric());
        withMiddleLayerCheckBox.setSelected(laminat.isWithMiddleLayer());
        withMiddleLayerCheckBox.setEnabled(laminat.isSymmetric());
        invertZCheckBox.setSelected(laminat.isInvertZ());
        offsetField.setValue(laminat.getOffset());
        totThicknessLabel.setText(thicknessFormat.format(laminat.getThickness()));
        numLayersLabel.setText("" + laminat.getNumberofLayers());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        stackingSequencePanel = new javax.swing.JPanel();
        outlineView1 = new org.openide.explorer.view.OutlineView();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        addLayerPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        materialLabel = new javax.swing.JLabel();
        failureLabel = new javax.swing.JLabel();
        thicknessLabel = new javax.swing.JLabel();
        anglesLabel = new javax.swing.JLabel();
        failureComboBox = new javax.swing.JComboBox<>();
        materialComboBox = new javax.swing.JComboBox<>();
        nameField = new javax.swing.JTextField();
        thicknessField = new javax.swing.JFormattedTextField();
        anglesField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        addLayerButton = new javax.swing.JButton();
        symmetryOptionsPanel = new javax.swing.JPanel();
        symmetricCheckBox = new javax.swing.JCheckBox();
        withMiddleLayerCheckBox = new javax.swing.JCheckBox();
        offsetLabel = new javax.swing.JLabel();
        offsetField = new javax.swing.JFormattedTextField();
        editPanel = new javax.swing.JPanel();
        invertButton = new javax.swing.JButton();
        rotateButton = new javax.swing.JButton();
        addStackButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rotationAngleField = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        totThicknessLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        numLayersLabel = new javax.swing.JLabel();
        invertZOptionsPanel = new javax.swing.JPanel();
        invertZCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        stackingSequencePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.stackingSequencePanel.title"))); // NOI18N
        stackingSequencePanel.setLayout(new java.awt.BorderLayout());
        stackingSequencePanel.add(outlineView1, java.awt.BorderLayout.CENTER);

        add(stackingSequencePanel, java.awt.BorderLayout.CENTER);

        addLayerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.addLayerPanel.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.nameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(materialLabel, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.materialLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(failureLabel, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.failureLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(thicknessLabel, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.thicknessLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(anglesLabel, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.anglesLabel.text")); // NOI18N

        nameField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        nameField.setText(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.nameField.text")); // NOI18N

        thicknessField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(thicknessFormat)));
        thicknessField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        thicknessField.setValue(0.125);

        anglesField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        anglesField.setText(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.anglesField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addLayerButton, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.addLayerButton.text")); // NOI18N
        addLayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLayerButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addLayerButton);

        javax.swing.GroupLayout addLayerPanelLayout = new javax.swing.GroupLayout(addLayerPanel);
        addLayerPanel.setLayout(addLayerPanelLayout);
        addLayerPanelLayout.setHorizontalGroup(
            addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addLayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(addLayerPanelLayout.createSequentialGroup()
                        .addGroup(addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(failureLabel)
                            .addComponent(nameLabel)
                            .addComponent(materialLabel)
                            .addComponent(thicknessLabel)
                            .addComponent(anglesLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(failureComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameField)
                            .addComponent(thicknessField)
                            .addComponent(anglesField)
                            .addComponent(materialComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        addLayerPanelLayout.setVerticalGroup(
            addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addLayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(materialLabel)
                    .addComponent(materialComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(failureLabel)
                    .addComponent(failureComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thicknessLabel)
                    .addComponent(thicknessField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anglesLabel)
                    .addComponent(anglesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        symmetryOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.symmetryOptionsPanel.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(symmetricCheckBox, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.symmetricCheckBox.text")); // NOI18N
        symmetricCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                symmetricCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(withMiddleLayerCheckBox, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.withMiddleLayerCheckBox.text")); // NOI18N
        withMiddleLayerCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withMiddleLayerCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(offsetLabel, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.offsetLabel.text")); // NOI18N

        offsetField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(thicknessFormat)));
        offsetField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        offsetField.setText(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.offsetField.text")); // NOI18N
        offsetField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                offsetFieldPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout symmetryOptionsPanelLayout = new javax.swing.GroupLayout(symmetryOptionsPanel);
        symmetryOptionsPanel.setLayout(symmetryOptionsPanelLayout);
        symmetryOptionsPanelLayout.setHorizontalGroup(
            symmetryOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(symmetryOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(symmetryOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(symmetryOptionsPanelLayout.createSequentialGroup()
                        .addComponent(symmetricCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(withMiddleLayerCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(symmetryOptionsPanelLayout.createSequentialGroup()
                        .addComponent(offsetLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(offsetField, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        symmetryOptionsPanelLayout.setVerticalGroup(
            symmetryOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(symmetryOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(symmetryOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(symmetricCheckBox)
                    .addComponent(withMiddleLayerCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(symmetryOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(offsetLabel)
                    .addComponent(offsetField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        editPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.editPanel.title"))); // NOI18N

        invertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elamx/laminateditor/resources/loop.png"))); // NOI18N
        invertButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        invertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertButtonActionPerformed(evt);
            }
        });

        rotateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elamx/laminateditor/resources/rotate_16.png"))); // NOI18N
        rotateButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateButtonActionPerformed(evt);
            }
        });

        addStackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elamx/laminateditor/resources/bottom.png"))); // NOI18N
        addStackButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        addStackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStackButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.jLabel3.text")); // NOI18N

        rotationAngleField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(angleFormat)));
        rotationAngleField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        rotationAngleField.setValue(0.0);

        javax.swing.GroupLayout editPanelLayout = new javax.swing.GroupLayout(editPanel);
        editPanel.setLayout(editPanelLayout);
        editPanelLayout.setHorizontalGroup(
            editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editPanelLayout.createSequentialGroup()
                        .addComponent(rotateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(rotationAngleField, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(editPanelLayout.createSequentialGroup()
                        .addGroup(editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(editPanelLayout.createSequentialGroup()
                                .addComponent(invertButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addGroup(editPanelLayout.createSequentialGroup()
                                .addComponent(addStackButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        editPanelLayout.setVerticalGroup(
            editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(invertButton)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rotateButton)
                    .addGroup(editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(rotationAngleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addStackButton)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.infoPanel.title"))); // NOI18N
        jPanel3.setToolTipText(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.jPanel3.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(totThicknessLabel, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.totThicknessLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.jLabel5.text")); // NOI18N
        jLabel5.setToolTipText(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.jLabel5.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(numLayersLabel, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.numLayersLabel.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(totThicknessLabel))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(numLayersLabel)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(totThicknessLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(numLayersLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        invertZOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.invertZOptionsPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(invertZCheckBox, org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.invertZCheckBox.text_1")); // NOI18N
        invertZCheckBox.setActionCommand(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.invertZCheckBox.actionCommand")); // NOI18N
        invertZCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertZCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout invertZOptionsPanelLayout = new javax.swing.GroupLayout(invertZOptionsPanel);
        invertZOptionsPanel.setLayout(invertZOptionsPanelLayout);
        invertZOptionsPanelLayout.setHorizontalGroup(
            invertZOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(invertZOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(invertZCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        invertZOptionsPanelLayout.setVerticalGroup(
            invertZOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(invertZOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(invertZCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        invertZCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.invertZCheckBox.AccessibleContext.accessibleName")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(symmetryOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(addLayerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(invertZOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(addLayerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(symmetryOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(invertZOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        invertZOptionsPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LaminatEditorTopComponent.class, "LaminatEditorTopComponent.invertZOptionsPanel.AccessibleContext.accessibleName")); // NOI18N

        jScrollPane2.setViewportView(jPanel2);

        add(jScrollPane2, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void addStackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStackButtonActionPerformed
        Node[] nodes = explorerManager.getSelectedNodes();
        if (nodes.length == 0) {
            return;
        }
        ArrayList<LayerNode> lNodes = new ArrayList<>();
        for (Node n : nodes) {
            if (n instanceof LayerNode) {
                lNodes.add((LayerNode) n);
            }
        }
        (new CloneLayersAction(lNodes)).actionPerformed(evt);
    }//GEN-LAST:event_addStackButtonActionPerformed

    private void rotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateButtonActionPerformed
        double rotAngle = ((Number) rotationAngleField.getValue()).doubleValue();
        for (DataLayer l : laminat.getOriginalLayers()) {
            l.setAngle(rotAngle + l.getAngle());
        }
    }//GEN-LAST:event_rotateButtonActionPerformed

    private void invertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertButtonActionPerformed
        int size = laminat.getOriginalLayers().size();
        int[] perm = new int[size];
        for (int ii = 0; ii < perm.length; ii++) {
            perm[ii] = size - 1 - ii;
        }
        explorerManager.getRootContext().getCookie(Index.class).reorder(perm);
    }//GEN-LAST:event_invertButtonActionPerformed

    private void addLayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLayerButtonActionPerformed

        Double thickness = getThickness();
        String name = nameField.getText();
        double[] angles = LaminateStringParser.parseStackingSequence(anglesField.getText());
        if (angles != null) {
            LayerMaterial material = (LayerMaterial) materialComboBox.getSelectedItem();
            Criterion criterion = (Criterion) failureComboBox.getSelectedItem();

            // Invalid values.
            if (thickness == null) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        NbBundle.getMessage(LaminatEditorTopComponent.class, "MSG_InvalidThicknessValues"),
                        NotifyDescriptor.ERROR_MESSAGE));
                return;
            }

            // Invalid values.
            if (material == null) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        NbBundle.getMessage(LaminatEditorTopComponent.class, "MSG_InvalidMaterialValues"),
                        NotifyDescriptor.ERROR_MESSAGE));
                return;
            }

            List<DataLayer> layers = new ArrayList<>(angles.length);
            for (double a : angles) {
                layers.add(new DataLayer(UUID.randomUUID().toString(), name, material, a, thickness, criterion));
            }
            laminat.addLayers(layers);
        } else {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(LaminatEditorTopComponent.class, "MSG_AngleParsingError"),
                    NotifyDescriptor.ERROR_MESSAGE));
        }
        anglesField.requestFocus();
        anglesField.selectAll();
    }//GEN-LAST:event_addLayerButtonActionPerformed

    private void offsetFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_offsetFieldPropertyChange
        if (updateOffset) {
            laminat.setOffset(((Number) offsetField.getValue()).doubleValue());
        }
    }//GEN-LAST:event_offsetFieldPropertyChange

    private void withMiddleLayerCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withMiddleLayerCheckBoxActionPerformed
        laminat.setWithMiddleLayer(withMiddleLayerCheckBox.isSelected());
    }//GEN-LAST:event_withMiddleLayerCheckBoxActionPerformed

    private void symmetricCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symmetricCheckBoxActionPerformed
        laminat.setSymmetric(symmetricCheckBox.isSelected());
    }//GEN-LAST:event_symmetricCheckBoxActionPerformed

    private void invertZCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertZCheckBoxActionPerformed
        laminat.setInvertZ(invertZCheckBox.isSelected());
    }//GEN-LAST:event_invertZCheckBoxActionPerformed

    private Double getThickness() {
        ParsePosition pos = new ParsePosition(0);
        double thickness = thicknessFormat.parse(thicknessField.getText(), pos).doubleValue();
        if (pos.getErrorIndex() != -1 || pos.getIndex() != thicknessField.getText().length()) {
            return null;
        }
        return thickness;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLayerButton;
    private javax.swing.JPanel addLayerPanel;
    private javax.swing.JButton addStackButton;
    private javax.swing.JTextField anglesField;
    private javax.swing.JLabel anglesLabel;
    private javax.swing.JPanel editPanel;
    private javax.swing.JComboBox<Criterion> failureComboBox;
    private javax.swing.JLabel failureLabel;
    private javax.swing.JButton invertButton;
    private javax.swing.JCheckBox invertZCheckBox;
    private javax.swing.JPanel invertZOptionsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox<LayerMaterial> materialComboBox;
    private javax.swing.JLabel materialLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel numLayersLabel;
    private javax.swing.JFormattedTextField offsetField;
    private javax.swing.JLabel offsetLabel;
    private org.openide.explorer.view.OutlineView outlineView1;
    private javax.swing.JButton rotateButton;
    private javax.swing.JFormattedTextField rotationAngleField;
    private javax.swing.JPanel stackingSequencePanel;
    private javax.swing.JCheckBox symmetricCheckBox;
    private javax.swing.JPanel symmetryOptionsPanel;
    private javax.swing.JFormattedTextField thicknessField;
    private javax.swing.JLabel thicknessLabel;
    private javax.swing.JLabel totThicknessLabel;
    private javax.swing.JCheckBox withMiddleLayerCheckBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
        uniqueLaminates.remove(laminat);
        laminat.removePropertyChangeListener(this);
        laminatResult.removeLookupListener(this);
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Laminat.PROP_SYMMETRIC)) {
            symmetricCheckBox.setSelected(laminat.isSymmetric());
            withMiddleLayerCheckBox.setEnabled(laminat.isSymmetric());
        } else if (evt.getPropertyName().equals(Laminat.PROP_WITHMIDDLELAYER)) {
            withMiddleLayerCheckBox.setSelected(laminat.isWithMiddleLayer());
        } else if (evt.getPropertyName().equals(Laminat.PROP_INVERTZ)) {
            invertZCheckBox.setSelected(laminat.isInvertZ());
        } else if (evt.getPropertyName().equals(Laminat.PROP_NAME)) {
            setName(NbBundle.getMessage(LaminatEditorTopComponent.class, "CTL_LaminatEditorTopComponent", laminat.getName()));
        } else if (evt.getPropertyName().equals(Laminat.PROP_OFFSET)) {
            updateOffset = false;
            offsetField.setValue(laminat.getOffset());
            updateOffset = true;
        }
        totThicknessLabel.setText(thicknessFormat.format(laminat.getThickness()));
        numLayersLabel.setText("" + laminat.getNumberofLayers());
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (!laminatResult.allInstances().contains(laminat)) {
            this.close();
        }
    }

    private void updateMaterialComboBox() {
        ArrayList<LayerMaterial> materials = new ArrayList<>();
        for (LayerMaterial mat : materialResult.allInstances()) {
            materials.add(mat);
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(materials, new Comparator<LayerMaterial>() {
            @Override
            public int compare(LayerMaterial o1, LayerMaterial o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        DefaultComboBoxModel<LayerMaterial> materialModel = new DefaultComboBoxModel<>(materials.toArray(new LayerMaterial[materials.size()]));
        materialComboBox.setModel(materialModel);
    }

    @Override
    protected void componentDeactivated() {
        super.componentDeactivated(); //To change body of generated methods, choose Tools | Templates.
        JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
        mainFrame.getRootPane().removePropertyChangeListener("defaultButton", defaultButtonListener);
        mainFrame.getRootPane().setDefaultButton(null);
    }

    @Override
    protected void componentActivated() {
        super.componentActivated(); //To change body of generated methods, choose Tools | Templates.
        JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
        mainFrame.getRootPane().setDefaultButton(addLayerButton);
        anglesField.requestFocus();
        mainFrame.getRootPane().addPropertyChangeListener("defaultButton", defaultButtonListener);
    }

    private final PropertyChangeListener defaultButtonListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
            mainFrame.getRootPane().removePropertyChangeListener("defaultButton", defaultButtonListener);
            mainFrame.getRootPane().setDefaultButton(addLayerButton);
            mainFrame.getRootPane().addPropertyChangeListener("defaultButton", defaultButtonListener);
        }
    };

    private void updateFailureComboBox() {
        ArrayList<Criterion> criteria = new ArrayList<>();
        for (Criterion crit : failureResult.allInstances()) {
            criteria.add(crit);
        }
        // Sortieren der Liste nach den Name der Versagenskriterien
        Collections.sort(criteria, new Comparator<Criterion>() {
            @Override
            public int compare(Criterion o1, Criterion o2) {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
            }
        });
        Object selectedItem = failureComboBox.getSelectedItem();
        if (selectedItem == null) {
            selectedItem = Lookups.forPath("elamx/failurecriteria").lookup(Puck.class);
        }
        DefaultComboBoxModel<Criterion> critModel = new DefaultComboBoxModel<>(criteria.toArray(new Criterion[criteria.size()]));
        failureComboBox.setModel(critModel);
        failureComboBox.setSelectedItem(selectedItem);
    }

    private class MaterialLookupListener implements LookupListener {

        @Override
        public void resultChanged(LookupEvent ev) {
            updateMaterialComboBox();
        }
    }

    private class CriterionLookupListener implements LookupListener {

        @Override
        public void resultChanged(LookupEvent ev) {
            updateFailureComboBox();
        }
    }

    private class TextFieldFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            JTextField field = (JTextField) e.getSource();
            field.selectAll();
        }

        @Override
        public void focusLost(FocusEvent e) {
        }
    }
}
