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
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.clt.plate.Stiffener.Stiffener;
import de.elamx.clt.plate.Stiffener.Stiffenerx;
import de.elamx.clt.plate.Stiffener.Stiffenery;
import de.elamx.mathtools.MatrixTools;

/**
 * Diese Klasse enthält die statische Methode calc. Damit kann das Vibrationsproblem
 * für eine Rechteckplatte unter verschiedenen Randbedingungen gelöst werden.
 * Sie wird in eLamX mit dem Berechnungsbutton gestartet werden. Innerhalb der
 * calc-Methode werden die Steifigkeitsmatrix und die Massenmatrix
 * belegt. Anschließend wird das Eigenwertproblem mit einem internen Eigenwertrechner
 * gelöst. Es werden alle Eigenwerte m*n und deren Eigenformen bestimmt. Die Ergebnisse
 * werden im DataMemory gespeichert.
 *
 * @author Martin Rädel
 * @author Andreas Hauffe
 * @author Markus Herrich
 */
public class Vibration {

    private static int m_ = 10; //  number of terms used in ritz-approach in x-direction
    private static int n_ = 10; //  number of terms used in ritz-approach in y-direction

    private static double[][][] eigenvecs; // Eigenvektoren

    /**
     * Diese Methode berechnet für die gegebenen Daten die Eigenwerte und Eigenformen
     * Alle Ergebnisse werden im DataMemory (m, n, Eigenwerte, Eigenvektoren,
     * Randbedingungen) gespeichert und können von da verwendet werden.
     *
     * @param laminat Das betrachtete Laminat
     * @param input Eingabegrößen
     * @return Ergebnisse der Vibrationsberechnung
     */
    public static VibrationResult calc(CLT_Laminate laminat, VibrationInput input){
        
        VibrationResult result = new VibrationResult(laminat, input);

        // Speichern der Termanzahl
        m_ = input.getM();
        n_ = input.getM();

        double length = input.getLength();
        double width  = input.getWidth();

        // create new object plate with geometric dimensions// create new object plate with geometric dimensions
        Plate plate = new Plate(length, width);

        // create new object boundary x-direction with integrals needed for
        // calculation based on boundary condition and geometry
        Boundary bx, by;
        switch ( input.getBcx() )
        {
            case 0: bx = new Boundary_SS_200(length, m_); break;
            case 1: bx = new Boundary_CC_200(length, m_); break;
            case 2: bx = new Boundary_CF_200(length, m_); break;
            case 3: bx = new Boundary_FF_200(length, m_); break;
            case 4: bx = new Boundary_SC_200(length, m_); break;
            case 5: bx = new Boundary_SF_200(length, m_); break;
            default: bx = new Boundary_SS_200(length, m_);
        }
        // create new object boundary y-direction with integrals needed for
        // calculation based on boundary condition and geometry
        switch ( input.getBcy() )
        {
            case 0: by = new Boundary_SS_200(width, n_); break;
            case 1: by = new Boundary_CC_200(width, n_); break;
            case 2: by = new Boundary_CF_200(width, n_); break;
            case 3: by = new Boundary_FF_200(width, n_); break;
            case 4: by = new Boundary_SC_200(width, n_); break;
            case 5: by = new Boundary_SF_200(width, n_); break;
            default: by = new Boundary_SS_200(width, n_);
        }

        // Initialisierung der Matrizen
        // Achtung: An dieser Stelle wäre in anderen Programmiersprachen ein Nullsetzen
        // der Matrixeinträge notwendig. Java macht dies intern automatrisch.
        double[][] kmat  = new double[m_*n_][m_*n_]; // Steifigkeitsmatrix
        double[][] mmat  = new double[m_*n_][m_*n_]; // Massenmatrix

        // Aufruf der Methoden add für Platte, Last zum Füllen von Kmat und Kgmat
        plate.addStiffnessAndMass(laminat, kmat, mmat, m_, n_, input.getDMatrixService(), bx, by); // fill stiffness and mass matrix from Plate.class

        
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
            stiff.addStiffnessAndMass(kmat, mmat, m_, n_, bx, by);
        }

        // Initialiesierung des Eigenvektorfeldes
        // m und n werden unabhängig gespeichert, da dies für die 3D-Darstellung
        // Vorteile bringt
        eigenvecs = new double[m_*n_][m_][n_];
        
        // Eigener Eigenwertlöser
        // gelöst wird das Problem (K+lambda*M)*x=0,
        // wobei lambda=w_0^2 entspricht und in der Massenmatrix die negativen
        // Einträge stehen, sodass die Lösung (K-w_0^2*M)*x=0 entspricht.
        double[] eigenvalues = MatrixTools.getEigenValues(mmat, kmat, m_*n_, m_, n_, eigenvecs);       // calculate eigenvalues and eigenvalues with eLamX eigenvaluesolver-method eigenvaluescalc
        
        // kleinsten positiven eigenwert aus array suchen nicht nötig, sollten nur positive eigenwerte sein
        result.setEigenForms(eigenvalues, eigenvecs);
        
        return result;
    }
    
}
