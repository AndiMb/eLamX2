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
package de.elamx.clt.optimizationui.actions;

import de.elamx.clt.optimization.OptimizationInput;
import de.elamx.clt.optimization.Optimizer;
import de.elamx.clt.optimizationui.OptimizationModuleData;
import de.elamx.clt.optimizationui.OptimizationWizardPanel1;
import de.elamx.clt.optimizationui.OptimizationWizardPanel2;
import de.elamx.clt.optimizationui.OptimizationWizardPanel3;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.optimization.MinimalReserveFactorCalculator;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
// @ActionID(category="...", id="de.elamx.clt.optimizationui.OptimizationWizardAction")
// @ActionRegistration(displayName="Open Optimization Wizard")
// @ActionReference(path="Menu/Tools", position=...)
@ActionID(category = "Wizard", id = "de.elamx.clt.optimizationui.actions.OptimizationWizardAction")
@ActionRegistration(iconBase = "de/elamx/clt/optimizationui/resources/optimization.png", displayName = "#CTL_OptimizationAction")
@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 335),
    @ActionReference(path = "Toolbars/eLamX_Modules_General", position = 300),
    @ActionReference(path = "eLamXActions/Optimizations", position = 10)
})
public final class OptimizationWizardAction implements ActionListener {

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new OptimizationWizardPanel1());
        panels.add(new OptimizationWizardPanel2());
        panels.add(new OptimizationWizardPanel3());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(OptimizationWizardAction.class, "OptimizationWizardAction.DialogTitel"));
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            LayerMaterial material = (LayerMaterial)wiz.getProperty("Material");
            Criterion criterion = (Criterion)wiz.getProperty("Criterion");
            Double thickness = (Double)wiz.getProperty("Thickness");
            double[] angles = (double[])wiz.getProperty("Angles");
            int angleType = (Integer)wiz.getProperty("AngleType");
            Boolean symmetry = (Boolean)wiz.getProperty("Symmetry");
            ArrayList<MinimalReserveFactorCalculator> calculators = (ArrayList<MinimalReserveFactorCalculator>)wiz.getProperty("Calculators");
            Optimizer optimizer = (Optimizer)wiz.getProperty("Optimizer");
            
            OptimizationInput input = new OptimizationInput(angles, thickness, material, criterion, calculators, symmetry);
            OptimizationModuleData data = new OptimizationModuleData(input, false);
            data.setOptimizer(optimizer);
            data.setAngleType(angleType);
            eLamXLookup.getDefault().add(data);
            (new OpenOptimizationAction(data)).actionPerformed(e);
        }
    }

}
