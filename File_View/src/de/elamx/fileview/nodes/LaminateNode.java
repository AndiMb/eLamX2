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
package de.elamx.fileview.nodes;

import de.elamx.core.LaminatStringGenerator;
import de.elamx.core.propertyeditor.PositionPropertyEditorSupport;
import de.elamx.fileview.FileExploererViewTopComponent;
import de.elamx.fileview.nodefactories.eLamXModuleDataNodeFactory;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.modules.eLamXModuleData;
import de.elamx.laminate.modules.eLamXModuleDataFlavor;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.actions.PasteAction;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.actions.Presenter;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class LaminateNode extends AbstractNode implements PropertyChangeListener, NodeListener {

    private final Laminat laminate;
    private String stackSeq = "";

    @SuppressWarnings("this-escape")
    public LaminateNode(Laminat laminate) {
        super(Children.create(new eLamXModuleDataNodeFactory(laminate), true), Lookups.singleton(laminate));
        this.laminate = laminate;
        stackSeq = LaminatStringGenerator.getLaminatAsHTMLString(laminate);
        laminate.addPropertyChangeListener(WeakListeners.propertyChange(this, laminate));
        this.addNodeListener(this);
    }

    @Override
    public String getDisplayName() {
        return laminate.getName();
    }

    @Override
    public String getHtmlDisplayName() {
        return getDisplayName() + " - " + stackSeq;
    }

    /*@Override
     public Action[] getActions(boolean context) {
     List<? extends Action> laminateActions = Utilities.actionsForPath("eLamXActions/Laminate");
     return laminateActions.toArray(new Action[laminateActions.size()]);
     }*/
    /**
     * { @inheritDoc}
     *
     * @return
     */
    @Override
    public Action[] getActions(boolean context) {
        return this.createActions(context, "eLamXActions/Laminate");
    }

    /**
     * Retrieves actions from the given paths.
     *
     * @param context Action context sensitivity
     * @param paths Action paths
     * @return All found actions from the given paths
     */
    protected Action[] createActions(boolean context, String... paths) {
        ArrayList<Action> subActions = new ArrayList<>();
        ArrayList<Action> actions = new ArrayList<>();

        actions.addAll(Arrays.asList(super.getActions(context)));
        actions.add(PasteAction.get(PasteAction.class));
        actions.add(null);
        
        for (String path : paths) {
            List<? extends Action> actionsForPath = Utilities.actionsForPath(path);
            for (Action a : actionsForPath) {
                if (a instanceof Presenter.Popup) {
                    List<Action> presenterActions = this.findSubActions((Presenter.Popup) a);
                    if (!presenterActions.isEmpty()) {
                        subActions.addAll(presenterActions);
                    } else {
                        continue;
                    }
                }

                actions.add(a);
            }
        }

        // remove all actions that are already in a submenu
        actions.removeAll(subActions);

        return actions.toArray(new Action[actions.size()]);
    }

    private List<Action> findSubActions(Presenter.Popup subMenu) {
        List<Action> actions = new ArrayList<>();

        JMenuItem item = subMenu.getPopupPresenter();
        if (item instanceof JMenu) {
            JMenu menu = (JMenu) item;
            for (int i = 0; i < menu.getItemCount(); i++) {
                Action a = menu.getItem(i).getAction();
                actions.add(a);

                if (a instanceof Presenter.Popup) {
                    actions.addAll(this.findSubActions((Presenter.Popup) a));
                }
            }
        }

        return actions;
    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName("GeneralProperties");
        generalProp.setDisplayName(NbBundle.getMessage(LaminateNode.class, "LaminateNode.GeneralProperties"));

        try {
            PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<>(laminate, String.class, Laminat.PROP_NAME);
            PropertySupport.Reflection<Boolean> symmetryProp = new PropertySupport.Reflection<>(laminate, boolean.class, Laminat.PROP_SYMMETRIC);
            PropertySupport.Reflection<Boolean> withMiddleLayerProp = new PropertySupport.Reflection<Boolean>(laminate, boolean.class, Laminat.PROP_WITHMIDDLELAYER) {
                @Override
                public boolean canWrite() {
                    return super.canWrite() && laminate.isSymmetric();
                }
            };
            PropertySupport.Reflection<Double> offsetProp = new PropertySupport.Reflection<>(laminate, double.class, Laminat.PROP_OFFSET);
            offsetProp.setPropertyEditorClass(PositionPropertyEditorSupport.class);

            PropertySupport.ReadOnly<Integer> layerNumProp = new PropertySupport.ReadOnly<Integer>("layerNum", Integer.class, NbBundle.getMessage(LaminateNode.class, "LaminateNode.layerNum"), NbBundle.getMessage(LaminateNode.class, "LaminateNode.layerNum.short")) {
                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return laminate.getNumberofLayers();
                }
            };

            nameProp.setDisplayName(NbBundle.getMessage(LaminateNode.class, "LaminateNode.Name"));
            symmetryProp.setDisplayName(NbBundle.getMessage(LaminateNode.class, "LaminateNode.Symmetry"));
            withMiddleLayerProp.setDisplayName(NbBundle.getMessage(LaminateNode.class, "LaminateNode.WithMiddleLayer"));
            offsetProp.setDisplayName(NbBundle.getMessage(LaminateNode.class, "LaminateNode.Offset"));

            generalProp.put(nameProp);
            generalProp.put(symmetryProp);
            generalProp.put(withMiddleLayerProp);
            generalProp.put(offsetProp);
            generalProp.put(layerNumProp);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        sheet.put(generalProp);

        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Laminat.PROP_NAME)) {
            this.fireDisplayNameChange(null, this.getHtmlDisplayName());
        } else {
            String oldHTMLName = this.getHtmlDisplayName();
            stackSeq = LaminatStringGenerator.getLaminatAsHTMLString(laminate);
            this.fireDisplayNameChange(oldHTMLName, this.getHtmlDisplayName());
        }
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/elamx/fileview/resources/laminate.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("Laminate", "de.elamx.core.laminateeditor.OpenLaminateEditorAction");
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        FileExploererViewTopComponent.getInstance().expandNode(ev.getNode());
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
    }

    @Override
    public PasteType getDropType(final Transferable t, int arg1, int arg2) {
        if (t.isDataFlavorSupported(eLamXModuleDataFlavor.ELAMXMODULEDATA_FLAVOR)) {
            return new PasteType() {
                @Override
                public Transferable paste() throws IOException {
                    try {
                        eLamXModuleData data = (eLamXModuleData) t.getTransferData(eLamXModuleDataFlavor.ELAMXMODULEDATA_FLAVOR);
                        laminate.getLookup().add(data.copy(laminate));
                        // Nur Kopieren bisher vorgesehen
                        /*final Node node = NodeTransfer.node(t, NodeTransfer.DND_MOVE + NodeTransfer.CLIPBOARD_CUT);
                        if (node != null) {
                            node.destroy();
                        }*/
                    } catch (UnsupportedFlavorException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    return null;
                }
            };
        } else {
            return null;
        }
    }

    @Override
    protected void createPasteTypes(Transferable t, List<PasteType> s) {
        super.createPasteTypes(t, s);
        PasteType p = getDropType(t, 0, 0);
        if (p != null) {
            s.add(p);
        }
    }
}
