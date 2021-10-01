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

import de.elamx.laminate.failure.ReserveFactor;

/**
 * Diese Klasse definiert einen Belastungszustand. Darin werden die 2D Spannungs-
 * und Dehnungstensoren gespeichert. Diese müssen im lokalen System sein. Wie diese
 * berechnet werden (CLT, FSDT, nichtlinear) ist dabei vollkommen egal.
 * 
 * @author Andreas Hauffe
 */
public class StressStrainState {
   
    private final double[] stress; // sigma11, sigma22, tau12
    private final double[] strain; // epsilon11, epsilon22, gamma12
    private ReserveFactor rf;

    /**
     * Erzeugen eines neuen StressStrainStates. Der Spannungs- bzw. Dehnungszustand
     * wird über einen Vektor übergeben. Darin müssen die entsprechenden 
     * Größen in folgender Reihenfolge definert sein -> 11, 22, 12! Alle Größen 
     * müssen im Lagensystem angegeben werden.
     * @param stress Spannungstensor im Lagensystem (lokal) als double[3] gespeichert
     * @param strain Dehnungstensor im Lagensystem (lokal) als double[3] gespeichert
     */
    public StressStrainState(double[] stress, double[] strain) {
        this.stress = stress;
        this.strain = strain;
    }

    /**
     * Dehnungstensor im lokalen System. Der Vektor in folgendermaßen aufgebaut:
     * (&epsilon;<sub>11</sub>, &epsilon;<sub>22</sub>, &gamma;<sub>12</sub>)<sup>T</sup>
     * @return Dehnungstensor im lokalen System
     */
    public double[] getStrain() {
        return strain;
    }

    /**
     * Spannungstensor im lokalen System. Der Vektor in folgendermaßen aufgebaut:
     * (&sigma;<sub>11</sub>, &sigma;<sub>22</sub>, &tau;<sub>12</sub>)<sup>T</sup>
     * @return Spannungstensor im lokalen System
     */
    public double[] getStress() {
        return stress;
    }

    public ReserveFactor getRf() {
        return rf;
    }

    public void setRf(ReserveFactor rf) {
        this.rf = rf;
    }
}
