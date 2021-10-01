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
package de.elamx.clt.plateui.deformation.load;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

public final class NewLoadWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor> {
    private int index;

    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    
    private WizardDescriptor wizDes;
    
    private int maxPanel = 2;
    
    public void initialize(WizardDescriptor wizDes){
        this.wizDes = wizDes;
    }

    private void initPanels() {
        if (panels == null) {
            panels = new ArrayList<>();
            panels.add(new ForceTypeWizardPanel());
            panels.add(new PointLoadWizardPanel());
            panels.add(new SurfaceLoad_const_fullWizardPanel());
            String[] steps = new String[]{
                NbBundle.getMessage(ForceTypeVisualPanel.class, "ForceTypePanel.title"),
                NbBundle.getMessage(ForceTypeVisualPanel.class, "LoadDataPanel.title")
            };
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
        }
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        initPanels();
        if (index == 0){
            return panels.get(0);
        }else if(index == 1){
            int loadType = ((Integer)wizDes.getProperty("loadtype"));
            switch(loadType){
                case ForceTypeVisualPanel.LOAD_POINT:
                    return panels.get(1);
                case ForceTypeVisualPanel.LOAD_SURFACE:
                    return panels.get(2);
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public String name() {
        return NbBundle.getMessage(NewLoadWizardIterator.class, "NewLoadWizardIterator.caption", index+1, maxPanel);
    }

    @Override
    public boolean hasNext() {
        return index < maxPanel - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

}
