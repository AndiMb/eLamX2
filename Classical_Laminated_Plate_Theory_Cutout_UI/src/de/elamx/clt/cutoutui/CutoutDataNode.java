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
package de.elamx.clt.cutoutui;

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
public class CutoutDataNode extends AbstractNode implements PropertyChangeListener{
    
    private final CutoutModuleData data;

    public CutoutDataNode(CutoutModuleData data) {
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
        List<Action> cutOutActions = new ArrayList<>();
        cutOutActions.addAll(Arrays.asList(super.getActions(context)));
        cutOutActions.add(CopyAction.get(CopyAction.class));
        cutOutActions.add(null);
        cutOutActions.addAll(Utilities.actionsForPath("eLamXActions/Cutout"));
        return cutOutActions.toArray(new Action[cutOutActions.size()]);
    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName("GeneralProperties");
        generalProp.setDisplayName(NbBundle.getMessage(CutoutDataNode.class, "CutoutDataNode.GeneralProperties"));

        try {
            PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<>(data, String.class, CutoutModuleData.PROP_NAME);

            nameProp.setDisplayName(NbBundle.getMessage(CutoutDataNode.class, "CutoutDataNode.Name"));
            
            generalProp.put(nameProp);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        sheet.put(generalProp);

        return sheet;
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("Cutout", "de.elamx.clt.cutoutui.actions.OpenCutoutAction");
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage ("de/elamx/clt/cutoutui/resources/cutout.png");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.fireDisplayNameChange(null, getDisplayName());
    }

    @Override
    public boolean canCopy() {
        return true;
    }
    
    @Override
    public boolean canCut() {
        return true;
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
