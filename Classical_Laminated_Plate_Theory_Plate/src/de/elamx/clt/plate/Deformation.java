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

import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.plate.Boundary.Boundary;
import de.elamx.clt.plate.Boundary.Boundary_CC_200;
import de.elamx.clt.plate.Boundary.Boundary_CF_200;
import de.elamx.clt.plate.Boundary.Boundary_FF_200;
import de.elamx.clt.plate.Boundary.Boundary_SC_200;
import de.elamx.clt.plate.Boundary.Boundary_SF_200;
import de.elamx.clt.plate.Boundary.Boundary_SS_200;
import de.elamx.clt.plate.Mechanical.Plate;
import de.elamx.clt.plate.Mechanical.TransverseLoad;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.clt.plate.Stiffener.Stiffener;
import de.elamx.clt.plate.Stiffener.Stiffenerx;
import de.elamx.clt.plate.Stiffener.Stiffenery;
import de.elamx.mathtools.MatrixTools;

/**
 * Diese Klasse enthält die statische Methode calc. Damit kann das statische 
 * Problem für eine Rechteckplatte unter verschiedenen Randbedingungen und
 * verschiedenen Lasten gelöst werden.
 * Sie wird in eLamX mit dem Berechnungsbutton gestartet werden. Innerhalb der
 * calc-Methode werden die Steifigkeitsmatrix und der Lastvektor
 * belegt. Anschließend wird das Gleichungssystem mit einem internen Löser
 * gelöst. Es wird er Lösungvektor, der alle Konstanten enthält berechnet.
 * Die Ergebnisse werden im DataMemory gespeichert.
 *
 * @author Martin Rädel
 * @author Andreas Hauffe
 * @author Markus Herrich
 */
public class Deformation {

    /**
     * Diese Methode berechnet für die gegebenen Daten die statische Lösung.
     * Alle Ergebnisse werden im DataMemory (m, n, Lösungsvektor,
     * Randbedingungen) gespeichert und können von da verwendet werden.
     * @param laminat Das betrachtete Laminat
     * @param input Alle Eingabegrößen als Objekt
     * @return DeformationResult
     */
    public static DeformationResult calc(CLT_Laminate laminat, DeformationInput input){

        // Speichern der Termanzahl
        int m = input.getM();
        int n = input.getM();

        double length = input.getLength();
        double width  = input.getWidth();

        // create new object plate with geometric dimensions
        Plate plate = new Plate(length, width);

        // create new object boundary x-direction with integrals needed for
        // calculation based on boundary condition and geometry
        Boundary bx;
        switch ( input.getBcx() )
        {
            case 0: bx = new Boundary_SS_200(length, m); break;
            case 1: bx = new Boundary_CC_200(length, m); break;
            case 2: bx = new Boundary_CF_200(length, m); break;
            case 3: bx = new Boundary_FF_200(length, m); break;
            case 4: bx = new Boundary_SC_200(length, m); break;
            case 5: bx = new Boundary_SF_200(length, m); break;
            default: bx = new Boundary_SS_200(length, m);
        }
        // create new object boundary y-direction with integrals needed for
        // calculation based on boundary condition and geometry
        Boundary by;
        switch ( input.getBcy() )
        {
            case 0: by = new Boundary_SS_200(width, n); break;
            case 1: by = new Boundary_CC_200(width, n); break;
            case 2: by = new Boundary_CF_200(width, n); break;
            case 3: by = new Boundary_FF_200(width, n); break;
            case 4: by = new Boundary_SC_200(width, n); break;
            case 5: by = new Boundary_SF_200(width, n); break;
            default: by = new Boundary_SS_200(width, n);
        }
        
        // Initialisierung der Matrizen
        // Achtung: An dieser Stelle wäre in anderen Programmiersprachen ein Nullsetzen
        // der Matrixeinträge notwendig. Java macht dies intern automatrisch.
        double[][] kmat = new double[m * n][m * n]; // Steifigkeitsmatrix
        double[]   fvec = new double[m * n];          // Kraftvektor

        // Aufruf der Methoden addStiffness für Platte, Last zum Füllen von Kmat und Kgmat
        plate.addStiffness(laminat, kmat, m, n, input.isWholeD(), bx, by); // fill stiffness matrix from Plate.class
        // fill Loadvector from Load.class
        for (TransverseLoad load : input.getLoads()) {
            load.add(fvec, m, n, bx, by);
        }

        for (StiffenerProperties s : input.getStiffenerProperties()) {
            Stiffener stiff;
            switch(s.getDirection()){
                case StiffenerProperties.X_DIRECTION:
                    stiff = new Stiffenerx(s, s.getPosition()); break;
                case StiffenerProperties.Y_DIRECTION:
                    stiff = new Stiffenery(s, s.getPosition()); break;
                default:
                    stiff = new Stiffenerx(s, s.getPosition()); break;
            }
            stiff.addStiffness(kmat, m, n, bx, by);
        }

        // Initialiesierung des Lösungsfeldes
        // m und n werden unabhängig gespeichert, da dies für die 3D-Darstellung
        // Vorteile bringt
        double[][] resultvecs = new double[m][n];

        double[] cvec = MatrixTools.solveAbCholesky(kmat, fvec);

        int iTemp, mm, nn;
        for ( mm = 0; mm < m; mm++ ){
            iTemp = mm*n;
            for(nn = 0; nn < n; nn++){
                resultvecs[mm][nn] = cvec[iTemp+nn];
            }
        }
        
        return new DeformationResult(laminat, input, resultvecs);
    }
}
