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
package de.elamx.clt.plateui.vibration;

import de.elamx.laminate.modules.eLamXModuleData;
import de.elamx.laminate.modules.eLamXModuleDataFlavor;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.actions.CopyAction;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author raedel
 */
public class VibrationDataNode extends AbstractNode implements PropertyChangeListener{
    
    private final VibrationModuleData data;

    public VibrationDataNode(VibrationModuleData data) {
        super(Children.LEAF, Lookups.singleton(data));
        this.data = data;
        data.addPropertyChangeListener(WeakListeners.propertyChange(this, data));
    }

    @Override
    public String getDisplayName() {
        return data.getName();
    }
    
    @Override
    public Action[] getActions(boolean context) {
        List<Action> vibrationAction = new ArrayList<>();
        vibrationAction.addAll(Arrays.asList(super.getActions(context)));
        vibrationAction.add(CopyAction.get(CopyAction.class));
        vibrationAction.add(null);
        vibrationAction.addAll(Utilities.actionsForPath("eLamXActions/Vibration"));
        return vibrationAction.toArray(new Action[vibrationAction.size()]);
    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName("GeneralProperties");
        generalProp.setDisplayName(NbBundle.getMessage(VibrationDataNode.class, "VibrationDataNode.GeneralProperties"));

        try {
            PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<>(data, String.class, VibrationModuleData.PROP_NAME);

            nameProp.setDisplayName(NbBundle.getMessage(VibrationDataNode.class, "VibrationDataNode.Name"));
            
            generalProp.put(nameProp);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        sheet.put(generalProp);

        return sheet;
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("Vibration", "de.elamx.clt.vibration.OpenVibrationAction");
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage ("de/elamx/clt/plateui/resources/vibration.png");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.fireDisplayNameChange(null, getDisplayName());
    }

    @Override
    public Transferable clipboardCopy() throws IOException {
        Transferable deflt = super.clipboardCopy();
        ExTransferable added = ExTransferable.create(deflt);
        added.put(new ExTransferable.Single(eLamXModuleDataFlavor.ELAMXMODULEDATA_FLAVOR) {
            @Override
            protected eLamXModuleData getData() {
                return getLookup().lookup(eLamXModuleData.class);
            }
        });
        return added;
    }
    
    @Override
    public Transferable clipboardCut() throws IOException {
        Transferable deflt = super.clipboardCut();
        ExTransferable added = ExTransferable.create(deflt);
        added.put(new ExTransferable.Single(eLamXModuleDataFlavor.ELAMXMODULEDATA_FLAVOR) {
            @Override
            protected eLamXModuleData getData() {
                return getLookup().lookup(eLamXModuleData.class);
            }
        });
        return added;
    }
    
}
