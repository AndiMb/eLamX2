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
package de.elamx.mathtools;

/**
 * Diese Klasse repräsentiert eine quadratische (n,n)-Matrix bestehend aus
 * Komplexen Zahlen.
 *
 * @author Franziska Sommerfeld
 */
public class QuadraticComplexMatrix extends Matrix {

    private Complex[][] m_;

    /**
     * Konstruktor einer quadratischen (n,n)-Matrix bestehend aus Komplexen
     * Zahlen. Übergeben wird die Anzahl der Spalten bzw. Zeilen.
     *
     * @param l Spalten- bzw. Zeilenanzahl
     */
    public QuadraticComplexMatrix(int l) {
        super(l, l);
        m_ = new Complex[r_][c_];
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < c_; jj++) {
                m_[ii][jj] = new Complex();
            }
        }
    }

    /**
     * Konstruktor einer quadratischen (n,n)-Matrix bestehend aus Komplexen
     * Zahlen. Übergeben wird ein Array aus Komplexen Zahlen, welches die
     * gleiche Anzahl Spalten und Zeilen besitzen muss.
     *
     * @param m Array bestehend aus Komplexen Zahlen
     */
    public QuadraticComplexMatrix(Complex[][] m) {
        super(m.length, m[0].length);
        if (r_ == c_) {
            m_ = new Complex[r_][c_];
            for (int ii = 0; ii < r_; ii++) {
                System.arraycopy(m[ii], 0, m_[ii], 0, r_);
            }
        }
    }

    /**
     * Konstruktor einer quadratischen (n,n)-Matrix bestehend aus Komplexen
     * Zahlen. Übergeben wird ein Array aus Komplexen Zahlen, welches die
     * gleiche Anzahl Spalten und Zeilen besitzen muss.
     *
     * @param m Array bestehend aus Komplexen Zahlen
     */
    public QuadraticComplexMatrix(double[][] m) {
        super(m.length, m[0].length);
        if (r_ == c_) {
            m_ = new Complex[r_][c_];
            for (int ii = 0; ii < r_; ii++) {
                for (int jj = 0; jj < c_; jj++) {
                    m_[ii][jj] = new Complex(m[ii][jj]);
                }
            }
        }
    }

    /**
     * Konstruktor einer quadratischen (n,n)-Matrix bestehend aus Komplexen
     * Zahlen. Übergeben wird eine Matrix bestehen aus Realen Zahlen, welche in
     * Komplexe Zahlen umgewandelt werden.
     *
     * @param m Matrix
     */
    public QuadraticComplexMatrix(QuadraticMatrix m) {
        super(m.getRows(), m.getColumns());
        if (r_ == c_) {
            m_ = new Complex[r_][c_];
            for (int ii = 0; ii < r_; ii++) {
                for (int jj = 0; jj < c_; jj++) {
                    m_[ii][jj] = new Complex(m.getValueAt(ii, jj));
                }
            }
        }
    }

    public Complex[][] getAsArray() {
        return m_;
    }

    /**
     * Setzt an einer bestimmten Stelle in der Matrix den Wert der angegebenen
     * Komplexen Zahl.
     *
     * @param c Komplexe Zahl
     * @param z Zeilen-Index des zu ändernden Wertes
     * @param s Spalten-Index des zu ändernden Wertes
     */
    public void setValueAt(Complex c, int z, int s) {
        m_[z][s] = c;
    }

    /**
     * Gibt den Wert an der angegebene Stelle der Matrix zurück.
     *
     * @param z Zeilen-Index des Wertes
     * @param s Spalten-Index des zu ändernden Wertes
     * @return Komplexe Zahl an der angebenen Stelle
     */
    public Complex getValueAt(int z, int s) {
        return m_[z][s];
    }

    /**
     * Gibt die Determinate der Matrix zurück.
     *
     * @return Determinate
     */
    public Complex getDeterminant() {

        Complex det = new Complex();

        switch (r_) {
            case 1:
                det = getValueAt(0, 0);
                break;
            default:
                QuadraticComplexMatrix tmp = new QuadraticComplexMatrix(m_);
                for (int ii = 0; ii < r_; ii++) {
                    det = det.add(Complex.multiplymultiple(new Complex(Math.pow(-1, ii + 2)), tmp.getValueAt(ii, 0), tmp.deleteZeileSpalte(ii, 0).getDeterminant()));
                }
        }
        return det;
    }

    /**
     * Gibt die adjungierte Matrix zurueck.
     *
     * @return adjungierte Matrix
     */
    private QuadraticComplexMatrix getAdjoint() {
        QuadraticComplexMatrix adj = new QuadraticComplexMatrix(r_);
        QuadraticComplexMatrix tmp = new QuadraticComplexMatrix(m_);
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < r_; jj++) {
                adj.setValueAt(tmp.deleteZeileSpalte(ii, jj).getDeterminant().multiply(Math.pow(-1, ii + jj + 2)), ii, jj);
            }
        }
        return adj;
    }

    /**
     * Gibt die Transponierte Matrix zurück.
     *
     * @return Transponierte Matrix
     */
    public QuadraticComplexMatrix getTranspose() {
        QuadraticComplexMatrix tmp = new QuadraticComplexMatrix(m_);
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < r_; jj++) {
                tmp.setValueAt(m_[jj][ii], ii, jj);
            }
        }
        return tmp;
    }

    /**
     * Gibt die Inverse Matrix zurück
     *
     * @return Inverse Matrix
     */
    public QuadraticComplexMatrix getInverse() {
        QuadraticComplexMatrix tmp = getAdjoint().getTranspose();
        QuadraticComplexMatrix inv = new QuadraticComplexMatrix(r_);
        Complex det = getDeterminant();
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < r_; jj++) {
                inv.setValueAt(tmp.getValueAt(ii, jj).divide(det), ii, jj);
            }
        }
        return inv;
    }

    /**
     * Multipliziert die Matrix mit einem Vektor (Array).
     *
     * @param v der zu multiplizierende Vektor als Array
     * @return M*v
     */
    public Complex[] multiplyComplexVektor(Complex[] v) {
        if (r_ != v.length) {
            return null;
        }

        Complex[] tmp = new Complex[r_];
        for (int ii = 0; ii < r_; ii++) {
            tmp[ii] = new Complex();
            for (int jj = 0; jj < r_; jj++) {
                tmp[ii] = tmp[ii].add(m_[ii][jj].multiply(v[jj]));
            }
        }
        return tmp;
    }

    /**
     * Multipliziert die Matrix m mit einer anderen Matrix n. Die Matrizen
     * müssen die gleiche Spalten- bzw Zeilenanzahl besitzen.
     *
     * @param n zu multiplizierende Matrix
     * @return m*n
     */
    public QuadraticComplexMatrix multiplyComplexMatrix(QuadraticComplexMatrix n) {
        if (r_ != n.getRows()) {
            return null;
        }

        QuadraticComplexMatrix tmp = new QuadraticComplexMatrix(r_);
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < r_; jj++) {
                tmp.setValueAt(new Complex(), ii, jj);
                for (int kk = 0; kk < r_; kk++) {
                    tmp.setValueAt(tmp.getValueAt(ii, jj).add(m_[ii][kk].multiply(n.getValueAt(kk, jj))), ii, jj);
                }
            }
        }
        return tmp;
    }

    /**
     * Gibt ein String-Objekt zurück, welches die Matrix repräsentiert.
     *
     * @return String
     */
    @Override
    public String toString() {
        String tmp = "";
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < c_; jj++) {
                tmp += m_[ii][jj].toString() + "; ";
            }
            tmp += "\n";
        }
        return tmp;
    }

    private QuadraticComplexMatrix deleteZeileSpalte(int z, int s) {
        QuadraticComplexMatrix tmp = new QuadraticComplexMatrix(r_ - 1);
        int ii = 0;
        int jj = 0;

        for (int i = 0; i < r_; i++) {
            if (i == z) {
                continue;
            }

            for (int j = 0; j < r_; j++) {
                if (j == s) {
                    continue;
                }
                tmp.setValueAt(m_[i][j], ii, jj);
                jj++;
            }
            jj = 0;
            ii++;
        }
        return tmp;
    }
}
