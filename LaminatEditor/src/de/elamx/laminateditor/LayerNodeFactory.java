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

import de.elamx.core.propertyeditor.AnglePropertyEditorSupport;
import de.elamx.core.propertyeditor.ThicknessPropertyEditorSupport;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminateditor.LayerNodeFactory.LayerProxy;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class LayerNodeFactory extends ChildFactory<LayerProxy> implements PropertyChangeListener {

    private boolean changing = false;
    private Laminat laminat;

    public LayerNodeFactory(Laminat laminat) {
        this.laminat = laminat;
        this.laminat.addPropertyChangeListener(this);
    }

    public void addLayer(Layer l) {
        changing = true;
        laminat.addLayer(l);
        refresh(true);
        changing = false;
    }

    private void removeLayer(Layer l) {
        changing = true;
        laminat.removeLayer(l);
        refresh(true);
        changing = false;
    }

    @Override
    protected boolean createKeys(List<LayerProxy> toPopulate) {
        ArrayList<Layer> layTemp = laminat.getLayers();

        int number = 1;
      
        double tges = laminat.getThickness();
        double offset = laminat.getOffset();
        double t, zm, z0, zold;

        if (! laminat.isInvertZ()) {
            z0 = tges / 2.0 + offset;
            zold = z0;
        } else {
            z0 = -tges / 2.0 + offset;
            zold = z0;
        }

        for (Layer layer : layTemp) {
            t     = layer.getThickness();
            if (! laminat.isInvertZ()) {    
                zm = zold - t/2.0;
                zold -= t;
            } else {
                zm = zold + t/2.0;
                zold += t;
            }
            toPopulate.add(new LayerProxy(layer, false, number++, zm));
        }

        if (laminat.isSymmetric()) {
            int start = layTemp.size() - 1;
            if (laminat.isWithMiddleLayer()) {
                start--;
            }
            Layer layer;
            for (int ii = start; ii >= 0; ii--) {
                layer = layTemp.get(ii);
                t     = layer.getThickness();
                if (! laminat.isInvertZ()) {    
                    zm = zold - t/2.0;
                    zold -= t;
                } else {
                    zm = zold + t/2.0;
                    zold += t;
                }
                toPopulate.add(new LayerProxy(layer, true, number++, zm));
            }
        }
        return true;
    }

    public void reorder(int[] perm) {
        ArrayList<Layer> layers = laminat.getLayers();
        Layer[] reordered = new Layer[layers.size()];

        // Wenn Ablage im Bereich der Symmetrielagen tue nichts
        for (int i = 0; i < layers.size(); i++) {
            if (perm[i] >= layers.size()) {
                return;
            }
        }

        //for (int i = 0; i < perm.length; i++) {
        for (int i = 0; i < layers.size(); i++) {
            int j = perm[i];

            Layer l = layers.get(i);
            reordered[j] = l;
        }
        changing = true;
        laminat.clear();
        laminat.addLayers(Arrays.asList(reordered));
        refresh(true);
        changing = false;
    }

    @Override
    protected Node createNodeForKey(LayerProxy key) {
        return new LayerNode(Lookups.fixed(key.layer), key.isSymmetryLayer(), key.getNumber(), key.getZM());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //if (!changing && !evt.getPropertyName().equals(Laminat.PROP_LAYER)) {
        if (!changing) {
            this.refresh(true);
        }
    }

    protected class LayerProxy {

        private final Layer layer;
        private final boolean symmetryLayer;
        private final int number;
        private final double zm;

        public LayerProxy(Layer layer, boolean symmetryLayer, int number, double zm) {
            this.layer = layer;
            this.symmetryLayer = symmetryLayer;
            this.number = number;
            this.zm = zm;
        }

        public Layer getLayer() {
            return layer;
        }

        public boolean isSymmetryLayer() {
            return symmetryLayer;
        }

        public int getNumber() {
            return number;
        }

        public double getZM() {
            return zm;
        }
    }

    public class LayerNode extends AbstractNode implements PropertyChangeListener {

        private boolean symmetryLayer;
        private final Layer layer;

        private int number;
        private double zm;

        public LayerNode(Lookup lookup, boolean symmetryLayer, int number, double zm) {
            super(Children.LEAF, lookup);
            this.symmetryLayer = !symmetryLayer;
            layer = lookup.lookup(Layer.class);
            layer.addPropertyChangeListener(WeakListeners.propertyChange(this, layer));
            this.number = number;
            this.zm = zm;
        }

        /**
         * Get the value of number
         *
         * @return the value of number
         */
        public int getNumber() {
            return number;
        }

        public double getZM() {
            return zm;
        }

        protected Laminat getLaminate() {
            return laminat;
        }

        @Override
        public boolean canCut() {
            return symmetryLayer;
        }

        @Override
        public boolean canCopy() {
            return symmetryLayer;
        }

        @Override
        public boolean canDestroy() {
            return symmetryLayer;
        }

        @Override
        public void destroy() throws IOException {
            removeLayer(getLookup().lookup(Layer.class));
        }

        @Override
        public boolean canRename() {
            return symmetryLayer;
        }

        @Override
        public void setName(String s) {
            getLookup().lookup(Layer.class).setName(s);
        }

        @Override
        public String getName() {
            return getLookup().lookup(Layer.class).getName();
        }

        @Override
        public Transferable clipboardCut() throws IOException {
            Transferable deflt = super.clipboardCut();
            ExTransferable added = ExTransferable.create(deflt);
            added.put(new ExTransferable.Single(LayerFlavor.LAYER_FLAVOR) {
                @Override
                protected Layer getData() {
                    return getLookup().lookup(Layer.class);
                }
            });
            return added;
        }

        @Override
        public Action[] getActions(boolean popup) {
            List<? extends Action> myActions = Utilities.actionsForPath("eLamXActions/Layer");
            return myActions.toArray(new Action[myActions.size()]);
        }

        @Override
        protected Sheet createSheet() {

            Sheet sheet = Sheet.createDefault();

            Sheet.Set generalProp = Sheet.createPropertiesSet();
            generalProp.setName("GeneralProperties");
            generalProp.setDisplayName(NbBundle.getMessage(LayerNode.class, "LayerNode.GeneralProperties"));

            try {
                Layer layer = getLookup().lookup(Layer.class);
                PropertySupport.ReadOnly<Integer> number = new PropertySupport.ReadOnly<Integer>("Number", Integer.class, "", "") {

                    @Override
                    public Integer getValue() {
                        return getNumber();
                    }
                };
                PropertySupport.ReadOnly<Double> zm = new PropertySupport.ReadOnly<Double>("ZM", Double.class, "", "") {

                    @Override
                    public Double getValue() {
                        return getZM();
                    }
                };
                PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<String>(layer, String.class, Layer.PROP_NAME) {
                    @Override
                    public boolean canWrite() {
                        return symmetryLayer;
                    }
                };
                PropertySupport.Reflection<Double> angleProp = new PropertySupport.Reflection<Double>(layer, double.class, Layer.PROP_ANGLE) {
                    @Override
                    public boolean canWrite() {
                        return symmetryLayer;
                    }
                };
                PropertySupport.Reflection<Double> thickProp = new PropertySupport.Reflection<Double>(layer, double.class, Layer.PROP_THICKNESS) {
                    @Override
                    public boolean canWrite() {
                        return symmetryLayer;
                    }
                };
                PropertySupport.ReadOnly<Boolean> embedded = new EmbeddedPropertySupport(layer);

                nameProp.setDisplayName(NbBundle.getMessage(LayerNode.class, "LayerNode.Name"));
                angleProp.setDisplayName(NbBundle.getMessage(LayerNode.class, "LayerNode.Angle"));
                angleProp.setPropertyEditorClass(AnglePropertyEditorSupport.class);
                thickProp.setDisplayName(NbBundle.getMessage(LayerNode.class, "LayerNode.Thickness"));
                thickProp.setPropertyEditorClass(ThicknessPropertyEditorSupport.class);

                generalProp.put(number);
                generalProp.put(nameProp);
                generalProp.put(angleProp);
                generalProp.put(thickProp);
                generalProp.put(zm);
                generalProp.put(new MaterialProperty(layer) {
                    @Override
                    public boolean canWrite() {
                        return symmetryLayer;
                    }
                });
                generalProp.put(new CriterionProperty(layer) {
                    @Override
                    public boolean canWrite() {
                        return symmetryLayer;
                    }
                });
                generalProp.put(embedded);
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }

            sheet.put(generalProp);

            return sheet;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage("de/elamx/laminateditor/resources/layer.png");
        }

        @Override
        public Image getOpenedIcon(int i) {
            return getIcon(i);
        }
    }

    private class EmbeddedPropertySupport extends PropertySupport.ReadOnly<Boolean> {
        
        private final Layer layer;

        public EmbeddedPropertySupport(Layer layer) {
            super(Layer.PROP_EMBEDDED, Boolean.class, NbBundle.getMessage(LayerNode.class, "LayerNode.embedded"), "");
            this.layer = layer;
        }

        @Override
        public Boolean getValue() {
            return layer.isEmbedded();
        }
    };
        }
