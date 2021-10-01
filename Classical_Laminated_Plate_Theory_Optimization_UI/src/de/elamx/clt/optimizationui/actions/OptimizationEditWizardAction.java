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

@ActionID(category = "Optimization", id = "de.elamx.clt.optimizationui.actions.OptimizationEditWizardAction")
@ActionRegistration(iconBase = "de/elamx/clt/optimizationui/resources/optimization.png", displayName = "#CTL_OptimizationEditAction")
@ActionReferences({
    @ActionReference(path = "eLamXActions/Optimization", position = 100)
})
public final class OptimizationEditWizardAction implements ActionListener {

    private final OptimizationModuleData context;

    public OptimizationEditWizardAction(OptimizationModuleData context) {
        this.context = context;
    }

    @Override
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
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle(NbBundle.getMessage(OptimizationWizardAction.class, "OptimizationWizardAction.DialogTitel"));
        
        OptimizationInput input = context.getOptimizationInput();
        wiz.putProperty("Material", input.getMaterial());
        wiz.putProperty("Criterion", input.getCriterion());
        wiz.putProperty("Thickness", input.getThickness());
        wiz.putProperty("Angles", input.getAngles());
        wiz.putProperty("AngleType", context.getAngleType());
        wiz.putProperty("Symmetry", input.isSymmetricLaminat());
        wiz.putProperty("Calculators", input.getCalculators());
        wiz.putProperty("Optimizer", context.getOptimizer());
        
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            LayerMaterial material = (LayerMaterial)wiz.getProperty("Material");
            Criterion criterion = (Criterion)wiz.getProperty("Criterion");
            Double thickness = (Double)wiz.getProperty("Thickness");
            double[] angles = (double[])wiz.getProperty("Angles");
            int angleType = (Integer)wiz.getProperty("AngleType");
            Boolean symmetry = (Boolean)wiz.getProperty("Symmetry");
            ArrayList<MinimalReserveFactorCalculator> calculators = (ArrayList<MinimalReserveFactorCalculator>)wiz.getProperty("Calculators");
            Optimizer optimizer = (Optimizer)wiz.getProperty("Optimizer");
            
            input.setAngles(angles);
            input.setCriterion(criterion);
            input.setMaterial(material);
            input.setSymmetricLaminat(symmetry);
            input.setThickness(thickness);
            input.setCalculators(calculators);
            context.setAngleType(angleType);
            context.setOptimizer(optimizer);
        }
    }

}
