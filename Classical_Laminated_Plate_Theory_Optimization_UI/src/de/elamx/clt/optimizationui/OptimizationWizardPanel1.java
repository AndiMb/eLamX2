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

import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.failure.Criterion;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class OptimizationWizardPanel1 implements WizardDescriptor.ValidatingPanel<WizardDescriptor> {
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private OptimizationVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public OptimizationVisualPanel1 getComponent() {
        if (component == null) {
            component = new OptimizationVisualPanel1();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        if (wiz.getProperty("Material") == null){
            return;
        }
        getComponent().setMaterial((LayerMaterial)wiz.getProperty("Material"));
        getComponent().setCriterion((Criterion)wiz.getProperty("Criterion"));
        getComponent().setThickness((Double)wiz.getProperty("Thickness"));
        getComponent().setAngleType((Integer)wiz.getProperty("AngleType"));
        getComponent().setAngles((double[])wiz.getProperty("Angles"));
        getComponent().setSymmetry((Boolean)wiz.getProperty("Symmetry"));
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty("Material", getComponent().getMaterial());
        wiz.putProperty("Criterion", getComponent().getCriterion());
        wiz.putProperty("Thickness", getComponent().getThickness());
        wiz.putProperty("AngleType", getComponent().getAngleType());
        wiz.putProperty("Angles", getComponent().getAngles());
        wiz.putProperty("Symmetry", getComponent().getSymmetry());
    }

    @Override
    public void validate() throws WizardValidationException {
        if (getComponent().getAngles() == null) {
            throw new WizardValidationException(null, NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.error.angledef"), null);
        }
        if (getComponent().getMaterial() == null) {
            throw new WizardValidationException(null, NbBundle.getMessage(OptimizationVisualPanel1.class, "OptimizationVisualPanel1.error.missMat"), null);
        }
    }
}