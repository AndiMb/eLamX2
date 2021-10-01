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
package de.elamx.clt.plateui.stiffenerui.wizard;

import de.elamx.clt.plateui.stiffenerui.StiffenerDefinitionService;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class StiffenerTypeVisualPanel extends JPanel{
    
    private javax.swing.ButtonGroup buttonGroup1;
    private JToggleButton[] toggleButtons;

    public StiffenerTypeVisualPanel() {
        initComponents();
    }
    
    private void initComponents(){
        buttonGroup1 = new javax.swing.ButtonGroup();
 
        Collection<? extends StiffenerDefinitionService> services = Lookup.getDefault().lookupAll(StiffenerDefinitionService.class);
    
        toggleButtons = new JToggleButton[services.size()];
        
        int ii = 0;
        for (StiffenerDefinitionService s : services) {
            toggleButtons[ii] = new JToggleButton();
            toggleButtons[ii].setModel(new StiffenerDefinitionButtonModel(s));
            buttonGroup1.add(toggleButtons[ii]);
            //Mnemonics.setLocalizedText(toggleButtons[ii], s.getDisplayName()); // NOI18N
            toggleButtons[ii].setToolTipText(s.getDisplayName());
            ImageIcon icon = s.getImage();
            if (icon != null){
                toggleButtons[ii].setIcon(icon);
            }
            add(toggleButtons[ii]);
            ii++;
        }
        
        toggleButtons[0].setSelected(true);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(StiffenerTypeVisualPanel.class, "StiffenerTypeVisualPanel.Title");
    }
    
    public Class<? extends StiffenerDefinitionService> getStiffenerType(){
        return ((StiffenerDefinitionButtonModel)buttonGroup1.getSelection()).getService().getClass();
    }
    
    public void setStiffenerType(Class<? extends StiffenerDefinitionService> cl){
        for (JToggleButton button : toggleButtons) {
            if (((StiffenerDefinitionButtonModel)button.getModel()).getService().getClass().getName().equals(cl.getName())){
                button.setSelected(true);
                return;
            }
        }
        toggleButtons[0].setSelected(true);
    }
    
    public StiffenerDefinitionService getSelectedService(){
        return ((StiffenerDefinitionButtonModel)buttonGroup1.getSelection()).getService();
    }
    
    private class StiffenerDefinitionButtonModel extends JToggleButton.ToggleButtonModel{
        
        private final StiffenerDefinitionService service;

        public StiffenerDefinitionButtonModel(StiffenerDefinitionService service) {
            this.service = service;
        }

        public StiffenerDefinitionService getService() {
            return service;
        }
    }
}
