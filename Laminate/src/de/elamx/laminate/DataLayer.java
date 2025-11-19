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
package de.elamx.laminate;

import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.Puck;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.UUID;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Definiert eine Lage im Laminat.
 *
 * @author Andreas Hauffe
 */
public class DataLayer extends Layer {

    // Winkel
    private double angle = 0.0;
    // Dicke der Schicht
    private double thickness = 0.0;
    // Matrial der Lage
    private LayerMaterial material = null;
    // z-Koordinate der Mittelebene dieser Schicht
    //private double   zm        = 0.0;
    //public static final String PROP_ZM = "zm";
    // Versagenskriterium
    private Criterion criterion = null;
    // Flag, ob die Lage oberhalb und unterhalb noch weitere Lagen berührt
    // für Versagenskriterien wichtig!
    private boolean embedded;

    public DataLayer(String uid, String name, LayerMaterial material, double angle, double thickness) {
        this(uid, name, material, angle, thickness, null);
    }

    @SuppressWarnings("this-escape")
    public DataLayer(String uid, String name, LayerMaterial material, double angle, double thickness, Criterion criterion) {
        this(uid, name, material, angle, thickness, criterion, true);
    }
    
    @SuppressWarnings("this-escape")
    private DataLayer(String uid, String name, LayerMaterial material, double angle, double thickness, Criterion criterion, boolean withListeners) {
        super(uid, name);
        this.material = material;
        if (withListeners){
            material.addPropertyChangeListener(this);
        }
        this.angle = reduceAngle(angle);
        this.thickness = thickness;
        this.criterion = criterion;

        if (this.criterion == null) {
            Lookup lkp = Lookups.forPath("elamx/failurecriteria");
            Collection<? extends Criterion> c = lkp.lookupAll(Criterion.class);
            for (Criterion crit : c) {
                if (crit instanceof Puck) {
                    this.criterion = crit;
                    break;
                }
            }
        }
    }

    /**
     * Liefert den Winkel der Lage im Laminatsystem im Gradmaß.
     * @return Winkel im Gradmaß
     */
    @Override
    public double getAngle() {
        return angle;
    }
    
    /**
     * Liefert den Winkel der Lage im Laminatsystem im Bogenmaß.
     * @return Winkel im Bogenmaß
     */
    @Override
    public double getRadAngle() {
        return Math.toRadians(angle);
    }

    private static double reduceAngle(final double angle) {
        double sign = Math.signum(angle);
        double a = Math.abs(angle);

        a %= 180.0;
        if (a > 90.0) {
            a -= 180.0;
        }
        return sign * a;
    }

    /**
     * Setzen den Winkels der Lage im Laminatsystem im Gradmaß. Der Winkel wird
     * dabei solange mit 180° modifiziert bis -90° &le; angle &le; 90°.
     *
     * @param angle Winkel im Gradmaß
     */
    public void setAngle(double angle) {
        double oldAngle = this.angle;
        this.angle = reduceAngle(angle);
        firePropertyChange(PROP_ANGLE, oldAngle, this.angle);
    }

    /**
     * Liefert das Materialobjekt der Lage zurück.
     *
     * @return Materialobjekt
     */
    @Override
    public LayerMaterial getMaterial() {
        return material;
    }

    /**
     * Setzen des Materials der Lage.
     *
     * @param material Neues Material der Lage
     */
    public void setMaterial(LayerMaterial material) {
        LayerMaterial oldMaterial = this.material;
        oldMaterial.removePropertyChangeListener(this);
        this.material = material;
        this.material.addPropertyChangeListener(this);
        firePropertyChange(PROP_MATERIAL, oldMaterial, this.material);
    }

    /**
     * Liefert die Dicke dieser Lage.
     *
     * @return Dicke der Lage
     */
    @Override
    public double getThickness() {
        return thickness;
    }

    /**
     * Setzen der Dicke der Lage.
     *
     * @param thickness Dicke der Lage
     */
    public void setThickness(double thickness) {
        double oldThickness = this.thickness;
        this.thickness = thickness;
        firePropertyChange(PROP_THICKNESS, oldThickness, this.thickness);
    }

    /**
     * Liefert das Versagenskriterium zurück.
     *
     * @return Versagenskriterium
     */
    @Override
    public Criterion getCriterion() {
        return criterion;
    }

    /**
     * Setzen des Versagenskriteriums der Lage im Laminat.
     *
     * @param criterion Versagenskriterium der Lages
     */
    public void setCriterion(Criterion criterion) {
        Criterion oldCriterion = this.criterion;
        this.criterion = criterion;
        firePropertyChange(PROP_CRITERION, oldCriterion, this.criterion);
    }
    
    /**
     * Liefert zurück, ob die Lage von weiteren Lagen umgeben ist oder am
     * Rand des Laminats liegt. Dies ist für einige Versagenskriterien wichtig.
     *
     * @return true, wenn die Lage von weiteren Lagen umgeben ist
     */
    @Override
    public boolean isEmbedded() {
        return embedded;
    }

    /**
     * Setzt, ob die Lage von weiteren Lagen umgeben ist oder am
     * Rand des Laminats liegt.
     *
     * @param embedded new value of embedded
     */
    protected void setEmbedded(boolean embedded) {
        boolean oldEmbedded = this.embedded;
        this.embedded = embedded;
        firePropertyChange(PROP_EMBEDDED, oldEmbedded, embedded);
    }

    /**
     * Liefert ein neues
     * <CODE>Layer</CODE>-Objekt mit den selben Eigenschaften.
     *
     * @return Kopie dieser Lage
     */
    @Override
    public DataLayer getCopy() {
        DataLayer dataLayerCopy = new DataLayer(UUID.randomUUID().toString(), getName(), material, angle, thickness, criterion);
        dataLayerCopy.setNumber(this.getNumber());
        return dataLayerCopy;
    }
    
    @Override
    public DataLayer getCopyWithoutListeners(double angle) {
        DataLayer dataLayerCopy = new DataLayer("", getName(), material, angle, thickness, criterion, false);
        dataLayerCopy.setNumber(this.getNumber());
        return dataLayerCopy;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.firePropertyChange(PROP_MATERIAL, null, material);
    }
}
