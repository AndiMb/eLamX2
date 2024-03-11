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
import java.beans.PropertyChangeListener;

/**
 * Definiert eine Lage im Laminat.
 *
 * @author Andreas Hauffe
 */
public abstract class Layer extends ELamXObject implements PropertyChangeListener {

    private int layerNumber = -1;
    
    private static final int UPDATE_PRIORITY = 200;
    public static final String PROP_LAYERNUMBER = "layer_number";
    public static final String PROP_ANGLE = "angle";
    public static final String PROP_THICKNESS = "thickness";
    public static final String PROP_MATERIAL = "material";
    public static final String PROP_CRITERION = "criterion";
    public static final String PROP_EMBEDDED = "embedded";

    public Layer(String uuid, String name) {
        super(uuid, name, false);
    }

    public Layer(String uuid, String name, int layerNumber) {
        this(uuid, name);
        this.layerNumber = layerNumber;
    }

    /**
     * Liefert die Nummer der Lage.
     * @return Lagennummer
     */
    public int getNumber() {
        return this.layerNumber;
    }

    /**
     * Setzt die Nummer der Lage.
     * @param layerNumber Lagennummer
     */
    public void setNumber(int layerNumber) {
        this.layerNumber = layerNumber;
    }

    /**
     * Liefert den Winkel der Lage im Laminatsystem im Gradmaß.
     * @return Winkel im Gradmaß
     */
    public abstract double getAngle();
    
    /**
     * Liefert den Winkel der Lage im Laminatsystem im Bogenmaß.
     * @return Winkel im Bogenmaß
     */
    public double getRadAngle() {
        return Math.toRadians(this.getAngle());
    }

    /**
     * Liefert das Materialobjekt der Lage zurück.
     *
     * @return Materialobjekt
     */
    public abstract LayerMaterial getMaterial();

    /**
     * Liefert die Dicke dieser Lage.
     *
     * @return Dicke der Lage
     */
    public abstract double getThickness();

    /**
     * Liefert das Versagenskriterium zurück.
     *
     * @return Versagenskriterium
     */
    public abstract Criterion getCriterion();
    
    /**
     * Liefert zurück, ob die Lage von weiteren Lagen umgeben ist oder am
     * Rand des Laminats liegt. Dies ist für einige Versagenskriterien wichtig.
     *
     * @return true, wenn die Lage von weiteren Lagen umgeben ist
     */
    public abstract boolean isEmbedded();

    /**
     * Liefert ein neues
     * <CODE>Layer</CODE>-Objekt mit den selben Eigenschaften.
     *
     * @return Kopie dieser Lage
     */
    public abstract Layer getCopy();
    
    public abstract Layer getCopyWithoutListeners(double angle);

    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;
    }
}
