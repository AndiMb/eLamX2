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
 * Diese Klasse repräsentiert eine quadratische (n,n)-Matrix.
 *
 * @author Franziska Sommerfeld
 *
 */
public class QuadraticMatrix extends Matrix {

    private double[][] m_;

    /**
     * Konstruktor einer quadratischen (n,n)-Matrix. Übergeben wird die Anzahl
     * der Spalten bzw. Zeilen.
     *
     * @param l Spalten- bzw. Zeilenanzahl
     */
    public QuadraticMatrix(int l) {
        super(l, l);
        m_ = new double[r_][c_];
    }

    /**
     * Konstruktor einer quadratischen (n,n)-Matrix. Übergeben wird ein Array
     * aus double-Werten, welches die gleiche Anzahl Spalten und Zeilen besitzen
     * muss.
     *
     * @param m Array bestehend aus Komplexen Zahlen
     */
    public QuadraticMatrix(double[][] m) {
        super(m.length, m[0].length);
        if (r_ == c_) {
            m_ = new double[r_][r_];
            for (int ii = 0; ii < r_; ii++) {
                System.arraycopy(m[ii], 0, m_[ii], 0, r_);
            }
        }
    }

    public double[][] getAsArray() {
        return m_;
    }

    /**
     * Setzt an einer bestimmten Stelle in der Matrix den Wert der neuen Zahl.
     *
     * @param d neue Zahl
     * @param z Zeilen-Index des zu ändernden Wertes
     * @param s Spalten-Index des zu ändernden Wertes
     */
    public void setValueAt(double d, int z, int s) {
        m_[z][s] = d;
    }

    /**
     * Gibt den Wert der angegebenen Stelle in der Matrix zurück.
     *
     * @param z Zeilen-Index des zu ändernden Wertes
     * @param s Spalten-Index des zu ändernden Wertes
     * @return Wert an der angegebenen Stelle
     */
    public double getValueAt(int z, int s) {
        return m_[z][s];
    }

    /**
     * Gibt die Determinate der Matrix zurück.
     *
     * @return Determinate
     */
    public double getDeterminant() {

        double det = 0;

        switch (r_) {
            case 1:
                det = m_[0][0];
                break;
            case 2:
                det = m_[0][0] * m_[1][1]
                        - m_[0][1] * m_[1][0];
                break;
            case 3:
                det = m_[0][0] * m_[1][1] * m_[2][2]
                        + m_[0][1] * m_[1][2] * m_[2][0]
                        + m_[0][2] * m_[1][0] * m_[2][1]
                        - m_[0][0] * m_[1][2] * m_[2][1]
                        - m_[0][1] * m_[1][0] * m_[2][2]
                        - m_[0][2] * m_[1][1] * m_[2][0];
                break;
            default:
                QuadraticMatrix tmp = new QuadraticMatrix(m_);
                for (int ii = 0; ii < r_; ii++) {
                    det += Math.pow(-1, ii + 2) * tmp.getValueAt(ii, 0) * tmp.deleteZeileSpalte(ii, 0).getDeterminant();
                }
                break;
        }

        return det;
    }

    /**
     * Gibt die adjungierte Matrix zurueck.
     *
     * @return adjungierte Matrix
     */
    private QuadraticMatrix getAdjoint() {
        QuadraticMatrix adj = new QuadraticMatrix(r_);
        QuadraticMatrix tmp = new QuadraticMatrix(m_);
        for (int i = 0; i < r_; i++) {
            for (int j = 0; j < r_; j++) {
                adj.setValueAt(Math.pow(-1, i + j + 2) * tmp.deleteZeileSpalte(i, j).getDeterminant(), i, j);
            }
        }
        return adj;
    }

    /**
     * Gibt die Transponierte Matrix zurück.
     *
     * @return Transponierte Matrix
     */
    public QuadraticMatrix getTranspose() {
        QuadraticMatrix tmp = new QuadraticMatrix(m_);
        for (int i = 0; i < r_; i++) {
            for (int j = 0; j < r_; j++) {
                tmp.setValueAt(m_[j][i], i, j);
            }
        }
        return tmp;
    }

    /**
     * Gibt die Inverse Matrix zurück
     *
     * @return Inverse Matrix
     */
    public QuadraticMatrix getInverse() {
        QuadraticMatrix tmp = new QuadraticMatrix(m_).getAdjoint().getTranspose();
        QuadraticMatrix inv = new QuadraticMatrix(r_);
        double det = getDeterminant();
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < r_; jj++) {
                inv.setValueAt(1 / det * tmp.getValueAt(ii, jj), ii, jj);
            }
        }
        return inv;
    }

    /**
     * Multipliziert die Matrix mit einen Vektor (Array).
     *
     * @param v der zu multiplizierende Vektor als Array
     * @return M*v
     */
    public double[] multiplyVektor(double[] v) {
        if (r_ != v.length) {
            return null;
        }

        double[] tmp = new double[r_];
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < r_; jj++) {
                tmp[ii] += m_[ii][jj] * v[jj];
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
    public QuadraticMatrix multiplyMatrix(QuadraticMatrix n) {
        QuadraticMatrix tmp = new QuadraticMatrix(r_);
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < r_; jj++) {
                tmp.setValueAt(0, ii, jj);
                for (int k = 0; k < r_; k++) {
                    tmp.setValueAt(tmp.getValueAt(ii, jj) + m_[ii][k] * n.getValueAt(k, jj), ii, jj);
                }
            }
        }
        return tmp;
    }

    /**
     * Gibt die Matrix zurück, die ensteht, wenn jeder Wert der Matrix durch die
     * Zahl d dividiert wird.
     *
     * @param d Divisor
     * @return M/d
     */
    public QuadraticMatrix divideDouble(double d) {
        QuadraticMatrix tmp = new QuadraticMatrix(r_);
        for (int i = 0; i < r_; i++) {
            for (int j = 0; j < r_; j++) {
                tmp.setValueAt(m_[i][j] / d, i, j);
            }
        }
        return tmp;
    }

    /**
     * Gibt ein String-Object zurück, welches die Matrix repräsentiert.
     *
     * @return String der Matrix
     */
    @Override
    public String toString() {
        String tmp = "";
        for (int ii = 0; ii < r_; ii++) {
            for (int jj = 0; jj < r_; jj++) {
                tmp += m_[ii][jj] + "; ";
            }
            tmp += "\n";
        }
        return tmp;
    }

    private QuadraticMatrix deleteZeileSpalte(int z, int s) {
        QuadraticMatrix tmp = new QuadraticMatrix(r_ - 1);
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
