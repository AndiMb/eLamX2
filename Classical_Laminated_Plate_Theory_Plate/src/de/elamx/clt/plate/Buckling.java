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
import de.elamx.clt.plate.Mechanical.InplaneLoad;
import de.elamx.clt.plate.Mechanical.Plate;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.clt.plate.Stiffener.Stiffener;
import de.elamx.clt.plate.Stiffener.Stiffenerx;
import de.elamx.clt.plate.Stiffener.Stiffenery;
import de.elamx.mathtools.MatrixTools;

/**
 * Diese Klasse enthält die statische Methode calc. Damit kann das Beulproblem
 * für eine Rechteckplatte unter verschiedenen Randbedingungen gelöst werden.
 * Sie wird in eLamX mit dem Berechnungsbutton gestartet werden. Innerhalb der
 * calc-Methode werden die Steifigkeitsmatrix und die geometrische
 * Streifigkeitsmatrix belegt. Anschließend wird das Eigenwertproblem mit einem
 * internen Eigenwertrechner gelöst. Es werden alle Eigenwerte m*n und deren
 * Eigenformen bestimmt. Die Ergebnisse werden im DataMemory gespeichert.
 *
 * @author Martin Rädel
 * @author Andreas Hauffe
 * @author Markus Herrich
 */
public class Buckling {

    private static int m_ = 10; //  number of terms used in ritz-approach in x-direction
    private static int n_ = 10; //  number of terms used in ritz-approach in y-direction
    private static double[][][] eigenvecs; // Eigenvektoren
    private static double eigvalcrit;

    /**
     * Diese Methode berechnet für die gegebenen Daten die Eigenwerte und
     * Eigenformen Alle Ergebnisse werden im DataMemory (m, n, Eigenwerte,
     * Eigenvektoren, Randbedingungen) gespeichert und können von da verwendet
     * werden.
     *
     * @param laminat Das betrachtete Laminat
     * @param input Eingabegrößen
     * @return Ergebnisse der Beulberechnung
     * 
     */
    public static BucklingResult calc(CLT_Laminate laminat, BucklingInput input) {

        BucklingResult result = new BucklingResult(laminat, input);

        // Speichern der Termanzahl
        m_ = input.getM();
        n_ = input.getN();

        double length = input.getLength();
        double width = input.getWidth();

        // create new object plate with geometric dimensions
        Plate plate_ = new Plate(length, width);

        // create new object load with in-plane loads
        InplaneLoad load_ = new InplaneLoad(input.getNx(), input.getNy(), input.getNxy());

        // create new object boundary x-direction with integrals needed for
        // calculation based on boundary condition and geometry
        Boundary bx, by;
        switch (input.getBcx()) {
            case 0:
                bx = new Boundary_SS_200(length, m_);
                break;
            case 1:
                bx = new Boundary_CC_200(length, m_);
                break;
            case 2:
                bx = new Boundary_CF_200(length, m_);
                break;
            case 3:
                bx = new Boundary_FF_200(length, m_);
                break;
            case 4:
                bx = new Boundary_SC_200(length, m_);
                break;
            case 5:
                bx = new Boundary_SF_200(length, m_);
                break;
            default:
                bx = new Boundary_SS_200(length, m_);
        }
        // create new object boundary y-direction with integrals needed for
        // calculation based on boundary condition and geometry
        switch (input.getBcy()) {
            case 0:
                by = new Boundary_SS_200(width, n_);
                break;
            case 1:
                by = new Boundary_CC_200(width, n_);
                break;
            case 2:
                by = new Boundary_CF_200(width, n_);
                break;
            case 3:
                by = new Boundary_FF_200(width, n_);
                break;
            case 4:
                by = new Boundary_SC_200(width, n_);
                break;
            case 5:
                by = new Boundary_SF_200(width, n_);
                break;
            default:
                by = new Boundary_SS_200(width, n_);
        }

        // Initialisierung der Matrizen
        // Achtung: An dieser Stelle wäre in anderen Programmiersprachen ein Nullsetzen
        // der Matrixeinträge notwendig. Java macht dies intern automatrisch.
        double[][] kmat = new double[m_ * n_][m_ * n_]; // Steifigkeitsmatrix
        double[][] kgmat = new double[m_ * n_][m_ * n_]; // geometrische Steifigkeitsmatrix

        // Aufruf der Methoden add für Platte, Last zum Füllen von Kmat und Kgmat
        plate_.addStiffness(laminat, kmat, m_, n_, input.getDMatrixService(), bx, by); // fill stiffness matrix from Plate.class
        load_.add(kgmat, m_, n_, bx, by);                  // fill geometric stiffness matrix from Load.class

        
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
            stiff.addStiffness(kmat, m_, n_, bx, by);
        }

        // Initialiesierung des Eigenvektorfeldes
        // m und n werden unabhängig gespeichert, da dies für die 3D-Darstellung
        // Vorteile bringt
        int number = m_ * n_;
        eigenvecs = new double[number][m_][n_];

        // Eigener Eigenwertlöser
        double[] eigenvalues = MatrixTools.getEigenValues(kgmat, kmat, number, m_, n_, eigenvecs);

        // kleinsten positiven eigenwert aus array suchen
        // initialisieren mit erstem eigenwert
        if ((m_ * n_) > 0) {
            eigvalcrit = eigenvalues[0];
            //System.out.println(eigvalcrit);
            // kleinsten positiven eigenwert aus array suchen
            // Eigenwerte kommen betragsmäßig sortiert aus eigenvaluesCalc
            // Suche nach erstem positiven Eigenwert
            for (int ii = 0; ii < eigenvalues.length; ii++) {
                if (eigenvalues[ii] >= 0) {
                    eigvalcrit = eigenvalues[ii];
                    break;
                }
            }
            
            double[] nveccrit = new double[3];

            nveccrit[0] = eigvalcrit * input.getNx();
            nveccrit[1] = eigvalcrit * input.getNy();
            nveccrit[2] = eigvalcrit * input.getNxy();

            result.setN_crit(nveccrit);
            result.setSmallestPositiveEigenValue(eigvalcrit);
        }

        result.setEigenForms(eigenvalues, eigenvecs);
        
        return result;
    }
}
