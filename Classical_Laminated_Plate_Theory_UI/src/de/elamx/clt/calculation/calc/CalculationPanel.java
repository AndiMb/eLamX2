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
package de.elamx.clt.calculation.calc;

import de.elamx.clt.CLT_Input;
import de.elamx.clt.Loads;
import de.elamx.clt.Strains;
import de.elamx.clt.calculation.interfaces.CLT_3DView_Provider;
import de.elamx.clt.calculation.ssdialog.StressStrainDialogPanel;
import de.elamx.core.GlobalProperties;
import de.elamx.utilities.Utilities;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;

/**
 *
 * @author Andreas Hauffe
 */
public class CalculationPanel extends JPanel implements PropertyChangeListener {

    private final CLT_Input dataHolder;
    private static final String prefix = "CLT_CalculationPanel.";
    private JRadioButton[] forceRButtons = new JRadioButton[6];
    private JFormattedTextField[] forceFields = new JFormattedTextField[6];
    private JFormattedTextField[] thForceFields = new JFormattedTextField[6];
    private JFormattedTextField[] displFields = new JFormattedTextField[6];
    private JLabel[][] aMatLabels = new JLabel[3][3];
    private JLabel[][] b1MatLabels = new JLabel[3][3];
    private JLabel[][] b2MatLabels = new JLabel[3][3];
    private JLabel[][] dMatLabels = new JLabel[3][3];
    private JFormattedTextField dTempTextField = null;
    private JFormattedTextField dHygTextField = null;

    private final StressStrainDialogPanel ssDiaPanel;

    private TopComponent TPC;
    private CLT_3DView_Provider provider;

    public CalculationPanel(CLT_Input dataHolder, StressStrainDialogPanel ssDiaPanel) {
        super();
        this.dataHolder = dataHolder;
        this.ssDiaPanel = ssDiaPanel;
    }

    public void init() {

        this.dataHolder.addPropertyChangeListener(WeakListeners.propertyChange(this, this.dataHolder));

        DecimalFormat df_Forces = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);

        DecimalFormat df_Strains = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRAIN);

        DecimalFormat df_HygTh = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_HYGROTHERMCOEFF);

        DecimalFormat df_Temp = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_TEMPERATURE);

        this.setBorder(BorderFactory.createTitledBorder(NbBundle.getMessage(CalculationPanel.class, prefix + "forcepanel.title")));
        GridBagLayout gbl = new GridBagLayout();
        this.setLayout(gbl);

        // Force TextFields
        JPanel forceFieldPanel = new JPanel();
        forceFieldPanel.setBorder(new MatteBorder(0, 1, 0, 1, Color.BLACK));

        GridBagLayout ffgbl = new GridBagLayout();
        forceFieldPanel.setLayout(ffgbl);

        for (int i = 0; i < 6; i++) {
            forceFields[i] = new JFormattedTextField(df_Forces);
            forceFields[i].setName("f" + i);
            forceFields[i].setColumns(8);
            forceFields[i].setHorizontalAlignment(JTextField.RIGHT);
            forceFields[i].addPropertyChangeListener("value", this);
            Utilities.addComponent(forceFieldPanel, ffgbl, forceFields[i], 0, i, 1, 1, 1.0, 1.0, 0, 2, 0, 2);
        }

        forceFields[0].setValue(dataHolder.getLoad().getN_x());
        forceFields[1].setValue(dataHolder.getLoad().getN_y());
        forceFields[2].setValue(dataHolder.getLoad().getN_xy());
        forceFields[3].setValue(dataHolder.getLoad().getM_x());
        forceFields[4].setValue(dataHolder.getLoad().getM_y());
        forceFields[5].setValue(dataHolder.getLoad().getM_xy());

        Utilities.addComponent(this, gbl, forceFieldPanel, 3, 1, 1, 6, 0.0, 0.0, 0, 0, 0, 0);

        ButtonGroup[] btGroups = new ButtonGroup[6];

        for (int i = 0; i < 6; i++) {
            btGroups[i] = new ButtonGroup();
        }

        // Force RadioButtons
        for (int i = 0; i < 6; i++) {
            final int ii = i;
            forceRButtons[i] = new JRadioButton(NbBundle.getMessage(CalculationPanel.class, prefix + "ForceRB" + i + ".text"));
            forceRButtons[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    forceFields[ii].setEditable(((JRadioButton) e.getSource()).isSelected());
                }
            });
            forceRButtons[i].setName("" + i);
            forceRButtons[i].setSelected(true);
            btGroups[i].add(forceRButtons[i]);
            Utilities.addComponent(this, gbl, forceRButtons[i], 1, i + 1, 1, 1, 0.0, 0.0, 0, 0, 0, 0);
            Utilities.addComponent(this, gbl, new JLabel("="), 2, i + 1, 1, 1, 0.0, 0.0, 0, 2, 0, 2);
        }

        // Displacement TextFields
        JPanel displFieldPanel = new JPanel();
        displFieldPanel.setBorder(new MatteBorder(0, 1, 0, 1, Color.BLACK));

        GridBagLayout digbl = new GridBagLayout();
        displFieldPanel.setLayout(digbl);

        for (int i = 0; i < 6; i++) {
            displFields[i] = new JFormattedTextField(df_Strains);
            displFields[i].setColumns(8);
            displFields[i].setName("d" + i);
            displFields[i].addPropertyChangeListener("value", this);
            displFields[i].setHorizontalAlignment(JTextField.RIGHT);
            displFields[i].setEditable(false);
            Utilities.addComponent(displFieldPanel, digbl, displFields[i], 0, i, 1, 1, 1.0, 1.0, 0, 2, 0, 2);
        }

        displFields[0].setValue(dataHolder.getStrains().getEpsilon_x());
        displFields[1].setValue(dataHolder.getStrains().getEpsilon_y());
        displFields[2].setValue(dataHolder.getStrains().getGamma_xy());
        displFields[3].setValue(dataHolder.getStrains().getKappa_x());
        displFields[4].setValue(dataHolder.getStrains().getKappa_y());
        displFields[5].setValue(dataHolder.getStrains().getKappa_xy());

        Utilities.addComponent(this, gbl, displFieldPanel, 14, 1, 1, 6, 0.0, 0.0, 0, 0, 0, 0);

        // Displacement RadioButtons
        JRadioButton[] displRButtons = new JRadioButton[6];

        for (int i = 0; i < 6; i++) {
            final int ii = i;
            displRButtons[i] = new JRadioButton(NbBundle.getMessage(CalculationPanel.class, prefix + "DisplRB" + i + ".text"));
            displRButtons[i].setName("" + i);
            btGroups[i].add(displRButtons[i]);
            displRButtons[i].setHorizontalTextPosition(JRadioButton.LEFT);
            Utilities.addComponent(this, gbl, new JLabel(displRButtons[i].getText()), 15, i + 1, 1, 1, 0.0, 0.0, 0, 0, 0, 0);
            displRButtons[i].setText(null);
            Utilities.addComponent(this, gbl, displRButtons[i], 16, i + 1, 1, 1, 0.0, 0.0, 0, 0, 0, 0);
            displRButtons[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    boolean bool = ((JRadioButton) e.getSource()).isSelected();
                    displFields[ii].setEditable(bool);
                    dataHolder.setUseStrains(ii, bool);
                }
            });
        }

        for (int i = 0; i < 6; i++) {
            displRButtons[i].setSelected(dataHolder.isUseStrains(i));
        }

        //                                                                                                    x  y  w  h  wx   wy    t  l  b  r
        Utilities.addComponent(this, gbl, new JLabel("<html>nm<sub>mech.</sub></html>", JLabel.CENTER), 3, 0, 1, 1, 0.0, 0.0, 0, 0, 10, 0);
        Utilities.addComponent(this, gbl, new JLabel("+", JLabel.CENTER), 4, 0, 1, 1, 0.0, 0.0, 0, 0, 10, 0);
        Utilities.addComponent(this, gbl, new JLabel("<html>nm<sub>hygrotherm.</sub></html>", JLabel.CENTER), 5, 0, 1, 1, 0.0, 0.0, 0, 0, 10, 0);
        Utilities.addComponent(this, gbl, new JLabel("=", JLabel.CENTER), 6, 0, 1, 1, 0.0, 0.0, 0, 0, 10, 0);
        Utilities.addComponent(this, gbl, new JLabel("<html>ABD-Matrix</html>", JLabel.CENTER), 7, 0, 6, 1, 0.0, 0.0, 0, 0, 10, 0);
        Utilities.addComponent(this, gbl, new JLabel("*", JLabel.CENTER), 13, 0, 1, 1, 0.0, 0.0, 0, 0, 10, 0);
        Utilities.addComponent(this, gbl, new JLabel("<html>&epsilon;&kappa;</html>", JLabel.CENTER), 14, 0, 1, 1, 0.0, 0.0, 0, 0, 10, 0);

        JPanel thForFieldPanel = new JPanel();
        thForFieldPanel.setBorder(new MatteBorder(0, 1, 0, 1, Color.BLACK));

        GridBagLayout tfgbl = new GridBagLayout();
        thForFieldPanel.setLayout(tfgbl);
        for (int i = 0; i < 6; i++) {
            thForceFields[i] = new JFormattedTextField(df_Forces);
            thForceFields[i].setHorizontalAlignment(JTextField.RIGHT);
            thForceFields[i].setEditable(false);
            thForceFields[i].setValue(0.0);
            thForceFields[i].setColumns(8);
            Utilities.addComponent(thForFieldPanel, tfgbl, thForceFields[i], 0, i, 1, 1, 1.0, 1.0, 0, 2, 0, 2);
        }

        thForceFields[0].setValue(dataHolder.getLoad().getnT_x());
        thForceFields[1].setValue(dataHolder.getLoad().getnT_y());
        thForceFields[2].setValue(dataHolder.getLoad().getnT_xy());
        thForceFields[3].setValue(dataHolder.getLoad().getmT_x());
        thForceFields[4].setValue(dataHolder.getLoad().getmT_y());
        thForceFields[5].setValue(dataHolder.getLoad().getmT_xy());

        Utilities.addComponent(this, gbl, thForFieldPanel, 5, 1, 1, 6, 0.0, 0.0, 0, 5, 0, 5);

        Utilities.addComponent(this, gbl, new JLabel("+"), 4, 1, 1, 6, 0.0, 0.0, 0, 5, 0, 5);

        Utilities.addComponent(this, gbl, new JLabel("="), 6, 1, 1, 6, 0.0, 0.0, 0, 5, 0, 5);
        Utilities.addComponent(this, gbl, new JLabel("*"), 13, 1, 1, 6, 0.0, 0.0, 0, 5, 0, 5);

        JPanel ABDPanel = new JPanel();
        ABDPanel.setBorder(new MatteBorder(0, 1, 0, 1, Color.BLACK));
        ABDPanel.setLayout(new GridLayout(2, 2, 5, 5));

        LetterPanel APanel = new LetterPanel("A", new Color(255, 200, 200), new Color(255, 170, 170));
        APanel.setLayout(new GridLayout(3, 3, 5, 0));
        LetterPanel BPanel1 = new LetterPanel("B", new Color(200, 255, 200), new Color(150, 255, 150));
        BPanel1.setLayout(new GridLayout(3, 3, 5, 0));
        LetterPanel BPanel2 = new LetterPanel("B", new Color(200, 255, 200), new Color(150, 255, 150));
        BPanel2.setLayout(new GridLayout(3, 3, 5, 0));
        LetterPanel DPanel = new LetterPanel("D", new Color(200, 200, 255), new Color(170, 170, 255));
        DPanel.setLayout(new GridLayout(3, 3, 5, 0));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                aMatLabels[i][j] = new JLabel();
                aMatLabels[i][j].setHorizontalAlignment(JLabel.RIGHT);
                aMatLabels[i][j].setOpaque(false);
                APanel.add(aMatLabels[i][j]);
            }
            for (int j = 0; j < 3; j++) {
                b1MatLabels[i][j] = new JLabel();
                b1MatLabels[i][j].setHorizontalAlignment(JLabel.RIGHT);
                b1MatLabels[i][j].setOpaque(false);
                BPanel1.add(b1MatLabels[i][j]);
            }
            for (int j = 0; j < 3; j++) {
                b2MatLabels[i][j] = new JLabel();
                b2MatLabels[i][j].setHorizontalAlignment(JLabel.RIGHT);
                b2MatLabels[i][j].setOpaque(false);
                BPanel2.add(b2MatLabels[i][j]);
            }
            for (int j = 0; j < 3; j++) {
                dMatLabels[i][j] = new JLabel();
                dMatLabels[i][j].setHorizontalAlignment(JLabel.RIGHT);
                dMatLabels[i][j].setOpaque(false);
                DPanel.add(dMatLabels[i][j]);
            }
        }

        ABDPanel.add(APanel);
        ABDPanel.add(BPanel1);
        ABDPanel.add(BPanel2);
        ABDPanel.add(DPanel);

        Utilities.addComponent(this, gbl, ABDPanel, 7, 1, 6, 6, 0.0, 0.0, 0, 0, 0, 5);

        // betaParallel - Label
        JLabel label = new JLabel(NbBundle.getMessage(CalculationPanel.class, prefix + "deltaTemplabel.text"));
        label.setHorizontalAlignment(JLabel.RIGHT);
        Utilities.addComponent(this, gbl, label, 1, 7, 2, 1, 0.0, 0.0, 15, 2, 2, 2);
        dTempTextField = new JFormattedTextField(df_Temp);
        dTempTextField.setValue(dataHolder.getLoad().getDeltaT());
        dTempTextField.setHorizontalAlignment(JFormattedTextField.RIGHT);
        dTempTextField.addPropertyChangeListener("value", this);
        dTempTextField.setName("temp");
        //                                                       x  y  w  h  wx   wy    t  l  b  r
        Utilities.addComponent(this, gbl, dTempTextField, 3, 7, 1, 1, 0.0, 0.0, 15, 2, 2, 2);

        // betaParallel - Label
        label = new JLabel(NbBundle.getMessage(CalculationPanel.class, prefix + "deltaHyglabel.text"));
        label.setHorizontalAlignment(JLabel.RIGHT);
        Utilities.addComponent(this, gbl, label, 1, 8, 2, 1, 0.0, 0.0, 2, 2, 2, 2);
        dHygTextField = new JFormattedTextField(df_HygTh);
        dHygTextField.setValue(dataHolder.getLoad().getDeltaH());
        dHygTextField.setHorizontalAlignment(JFormattedTextField.RIGHT);
        dHygTextField.addPropertyChangeListener("value", this);
        dHygTextField.setName("hyg");
        //                                                       x  y  w  h  wx   wy    t  l  b  r
        Utilities.addComponent(this, gbl, dHygTextField, 3, 8, 1, 1, 0.0, 0.0, 2, 2, 2, 2);

        JButton displButton = new JButton(NbBundle.getMessage(CalculationPanel.class, prefix + "DisplayButton"));
        displButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ssDiaPanel.actionPerformed(e);
                DialogDescriptor dd = new DialogDescriptor(
                        ssDiaPanel,
                        NbBundle.getMessage(CalculationPanel.class, "CalculationPanel.StressStrainDialog.Title"),
                        true,
                        new Object[]{NotifyDescriptor.OK_OPTION},
                        DialogDescriptor.OK_OPTION,
                        DialogDescriptor.DEFAULT_ALIGN,
                        null,
                        null);

                // let's display the dialog now...
                DialogDisplayer.getDefault().notify(dd);
            }

        });

        JButton clearButton = new JButton(NbBundle.getMessage(CalculationPanel.class, prefix + "ClearButton"));
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataHolder.setNotify(false);
                Loads l = dataHolder.getLoad();
                l.setN_x(0.0);
                l.setN_y(0.0);
                l.setN_xy(0.0);
                l.setM_x(0.0);
                l.setM_y(0.0);
                l.setM_xy(0.0);

                l.setDeltaH(0.0);
                l.setDeltaT(1.0);

                Strains s = dataHolder.getStrains();
                s.setEpsilon_x(0.0);
                s.setEpsilon_y(0.0);
                s.setGamma_xy(0.0);
                s.setKappa_x(0.0);
                s.setKappa_y(0.0);
                s.setKappa_xy(0.0);

                for (int ii = 0; ii < 6; ii++) {
                    displFields[ii].setValue(0.0);
                    forceFields[ii].setValue(0.0);
                }
                dHygTextField.setValue(0.0);
                dTempTextField.setValue(1.0);

                dataHolder.setNotify(true);

                valueWasSet = false;
                dTempTextField.setValue(0.0);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        buttonPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        buttonPanel.add(displButton);
        buttonPanel.add(clearButton);

        provider = Lookup.getDefault().lookup(CLT_3DView_Provider.class);
        if (provider != null) {
            JButton View3DButton = new JButton(NbBundle.getMessage(CalculationPanel.class, prefix + "View3DButton"));
            View3DButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (TPC == null || !TPC.isOpened()){
                        TPC = provider.getTopComponent(dataHolder);
                    }
                    TPC.open();
                    TPC.requestActive();
                }
            });
            buttonPanel.add(View3DButton);
        }

        Utilities.addComponent(this, gbl, buttonPanel, 4, 7, 13, 2, 0.0, 0.0, 10, 0, 0, 5);

        Utilities.addComponent(this, gbl, new JPanel(), 0, 1, 1, 6, 1.0, 0.0, 0, 0, 0, 0);
        Utilities.addComponent(this, gbl, new JPanel(), 17, 1, 1, 6, 1.0, 0.0, 0, 0, 0, 0);
    }
    private boolean valueWasSet = false;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (valueWasSet) {
            valueWasSet = false;
            return;
        }
        if (evt.getPropertyName().equals("value")) {
            JFormattedTextField field = (JFormattedTextField) evt.getSource();
            String name = field.getName();
            double newValue = ((Number) evt.getNewValue()).doubleValue();
            if (name.startsWith("t")) {
                valueWasSet = true;
                dataHolder.getLoad().setDeltaT(newValue);
            } else if (name.startsWith("h")) {
                valueWasSet = true;
                dataHolder.getLoad().setDeltaH(newValue);
            } else if (name.startsWith("f")) {
                int i = Integer.parseInt(name.substring(1));
                switch (i) {
                    case 0:
                        valueWasSet = true;
                        dataHolder.getLoad().setN_x(newValue);
                        break;
                    case 1:
                        valueWasSet = true;
                        dataHolder.getLoad().setN_y(newValue);
                        break;
                    case 2:
                        valueWasSet = true;
                        dataHolder.getLoad().setN_xy(newValue);
                        break;
                    case 3:
                        valueWasSet = true;
                        dataHolder.getLoad().setM_x(newValue);
                        break;
                    case 4:
                        valueWasSet = true;
                        dataHolder.getLoad().setM_y(newValue);
                        break;
                    case 5:
                        valueWasSet = true;
                        dataHolder.getLoad().setM_xy(newValue);
                        break;
                }
            } else if (name.startsWith("d")) {
                int i = Integer.parseInt(name.substring(1));
                switch (i) {
                    case 0:
                        valueWasSet = true;
                        dataHolder.getStrains().setEpsilon_x(newValue);
                        break;
                    case 1:
                        valueWasSet = true;
                        dataHolder.getStrains().setEpsilon_y(newValue);
                        break;
                    case 2:
                        valueWasSet = true;
                        dataHolder.getStrains().setGamma_xy(newValue);
                        break;
                    case 3:
                        valueWasSet = true;
                        dataHolder.getStrains().setKappa_x(newValue);
                        break;
                    case 4:
                        valueWasSet = true;
                        dataHolder.getStrains().setKappa_y(newValue);
                        break;
                    case 5:
                        valueWasSet = true;
                        dataHolder.getStrains().setKappa_xy(newValue);
                        break;
                }
            }
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_N_X)) {
            forceFields[0].removePropertyChangeListener("value", this);
            forceFields[0].setValue(dataHolder.getLoad().getN_x());
            forceFields[0].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_N_Y)) {
            forceFields[1].removePropertyChangeListener("value", this);
            forceFields[1].setValue(dataHolder.getLoad().getN_y());
            forceFields[1].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_N_XY)) {
            forceFields[2].removePropertyChangeListener("value", this);
            forceFields[2].setValue(dataHolder.getLoad().getN_xy());
            forceFields[2].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_M_X)) {
            forceFields[3].removePropertyChangeListener("value", this);
            forceFields[3].setValue(dataHolder.getLoad().getM_x());
            forceFields[3].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_M_Y)) {
            forceFields[4].removePropertyChangeListener("value", this);
            forceFields[4].setValue(dataHolder.getLoad().getM_y());
            forceFields[5].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_M_XY)) {
            forceFields[5].removePropertyChangeListener("value", this);
            forceFields[5].setValue(dataHolder.getLoad().getM_xy());
            forceFields[5].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_EPSILON_X)) {
            displFields[0].removePropertyChangeListener("value", this);
            displFields[0].setValue(dataHolder.getStrains().getEpsilon_x());
            displFields[0].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_EPSILON_Y)) {
            displFields[1].removePropertyChangeListener("value", this);
            displFields[1].setValue(dataHolder.getStrains().getEpsilon_y());
            displFields[1].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_GAMMA_XY)) {
            displFields[2].removePropertyChangeListener("value", this);
            displFields[2].setValue(dataHolder.getStrains().getGamma_xy());
            displFields[2].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_KAPPA_X)) {
            displFields[3].removePropertyChangeListener("value", this);
            displFields[3].setValue(dataHolder.getStrains().getKappa_x());
            displFields[3].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_KAPPA_Y)) {
            displFields[4].removePropertyChangeListener("value", this);
            displFields[4].setValue(dataHolder.getStrains().getKappa_y());
            displFields[4].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_KAPPA_XY)) {
            displFields[5].removePropertyChangeListener("value", this);
            displFields[5].setValue(dataHolder.getStrains().getKappa_xy());
            displFields[5].addPropertyChangeListener("value", this);
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_DELTAT)) {
            dTempTextField.setValue(dataHolder.getLoad().getDeltaT());
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_DELTAH)) {
            valueWasSet = true;
            dHygTextField.setValue(dataHolder.getLoad().getDeltaH());
        } else if (evt.getPropertyName().equals(CLT_Input.PROP_HYGTERFORCES)) {
            thForceFields[0].setValue(dataHolder.getLoad().getnT_x());
            thForceFields[1].setValue(dataHolder.getLoad().getnT_y());
            thForceFields[2].setValue(dataHolder.getLoad().getnT_xy());
            thForceFields[3].setValue(dataHolder.getLoad().getmT_x());
            thForceFields[4].setValue(dataHolder.getLoad().getmT_y());
            thForceFields[5].setValue(dataHolder.getLoad().getmT_xy());
        }
    }

    public void setABDMatrix(double[][] A, double[][] B, double[][] D) {
        DecimalFormat format = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STIFFNESS);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                aMatLabels[i][j].setText(format.format(A[i][j]));
            }
            for (int j = 0; j < 3; j++) {
                b1MatLabels[i][j].setText(format.format(B[i][j]));
            }
            for (int j = 0; j < 3; j++) {
                b2MatLabels[i][j].setText(format.format(B[i][j]));
            }
            for (int j = 0; j < 3; j++) {
                dMatLabels[i][j].setText(format.format(D[i][j]));
            }
        }
    }
    
    public void close(){
        if (TPC != null){
            TPC.close();
        }
    }
}
