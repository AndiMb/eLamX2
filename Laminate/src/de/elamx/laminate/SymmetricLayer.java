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
import java.beans.PropertyChangeEvent;
import java.util.UUID;

/**
 * Definiert eine Lage im Laminat.
 *
 * @author Andreas Hauffe
 */
public class SymmetricLayer extends Layer {

    private DataLayer dataLayer;

    public SymmetricLayer(String uid, String name, DataLayer dataLayer) {
        super(uid, name);
        this.dataLayer = dataLayer;
    }

    /**
     * Liefert den Winkel der Lage im Laminatsystem im Gradmaß.
     * @return Winkel im Gradmaß
     */
    @Override
    public double getAngle() {
        return this.dataLayer.getAngle();
    }

    /**
     * Liefert das Materialobjekt der Lage zurück.
     *
     * @return Materialobjekt
     */
    @Override
    public LayerMaterial getMaterial() {
        return this.dataLayer.getMaterial();
    }

    /**
     * Liefert die Dicke dieser Lage.
     *
     * @return Dicke der Lage
     */
    @Override
    public double getThickness() {
        return this.dataLayer.getThickness();
    }

    /**
     * Liefert das Versagenskriterium zurück.
     *
     * @return Versagenskriterium
     */
    @Override
    public Criterion getCriterion() {
        return this.dataLayer.getCriterion();
    }
    
    /**
     * Liefert zurück, ob die Lage von weiteren Lagen umgeben ist oder am
     * Rand des Laminats liegt. Dies ist für einige Versagenskriterien wichtig.
     *
     * @return true, wenn die Lage von weiteren Lagen umgeben ist
     */
    @Override
    public boolean isEmbedded() {
        return this.dataLayer.isEmbedded();
    }

    /**
     * Liefert ein neues
     * <CODE>Layer</CODE>-Objekt mit den selben Eigenschaften.
     *
     * @return Kopie dieser Lage
     */
    @Override
    public SymmetricLayer getCopy() {
        return new SymmetricLayer(UUID.randomUUID().toString(), getName(), dataLayer);
    }

    @Override
    public Layer getCopyWithoutListeners(double angle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) { }
}
