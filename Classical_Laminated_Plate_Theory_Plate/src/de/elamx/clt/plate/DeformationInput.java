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
package de.elamx.clt.plate;

import de.elamx.clt.plate.Mechanical.TransverseLoad;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.WeakListeners;


/**
 *
 * @author Andreas Hauffe
 */
public class DeformationInput extends Input implements PropertyChangeListener{

    public static final String PROP_LOAD_PROP = "PROP_LOAD_PROP";
    public static final String PROP_MAXDISPLACEMENTINZ = "maxDisplacementInZ";

    private final ArrayList<TransverseLoad> loads = new ArrayList<>();
    private double maxDisplacementInZ;

    public DeformationInput() {
        this(500.0, 500.0, true, 0, 0, 10, 10);
    }

    public DeformationInput(double length, double width, boolean wholeD,
            int bcx, int bcy, int m, int n) {
        super(length, width, wholeD, bcx, bcy, m, n);
    }
    
    public void addLoad(TransverseLoad load){
        loads.add(load);
        load.addPropertyChangeListener(WeakListeners.propertyChange(this, load));
        load.setParent(this);
        firePropertyChange(PROP_LOAD_PROP, null, load);
    }
    
    public void removeLoad(TransverseLoad load){
        loads.remove(load);
        load.removePropertyChangeListener(this);
        firePropertyChange(PROP_LOAD_PROP, load, null);
    }
    
    public List<TransverseLoad> getLoads(){
        return loads;
    }

    /**
     * Get the value of maxDisplacementInZ
     *
     * @return the value of maxDisplacementInZ
     */
    public double getMaxDisplacementInZ() {
        return maxDisplacementInZ;
    }

    /**
     * Set the value of maxDisplacementInZ
     *
     * @param maxDisplacementInZ new value of maxDisplacementInZ
     */
    public void setMaxDisplacementInZ(double maxDisplacementInZ) {
        double oldMaxDisplacementInZ = this.maxDisplacementInZ;
        this.maxDisplacementInZ = maxDisplacementInZ;
        firePropertyChange(PROP_MAXDISPLACEMENTINZ, oldMaxDisplacementInZ, maxDisplacementInZ);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange(PROP_LOAD_PROP, null, evt.getSource());
    }
    
    @Override
    public Input copy() {
        DeformationInput in = new DeformationInput(getLength(), getWidth(), isWholeD(), getBcx(), getBcy(), getM(), getN());
        for (TransverseLoad l : loads){
            in.addLoad(l.getCopy());
        }
        for (StiffenerProperties ss : getStiffenerProperties()){
            in.addStiffenerProperty(ss.getCopy());
        }
        in.setMaxDisplacementInZ(maxDisplacementInZ);
        return in;
    }
}
