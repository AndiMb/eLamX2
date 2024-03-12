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

import java.util.Arrays;

/**
 *
 * @author Andreas Hauffe
 */
public class MatrixTools {

    /**
     * Liefert die Determinante einer 3x3 Matrix. Weitere Berechnungsverfahren
     * für andere Matrizen müssen noch implementiert werden.
     *
     * @param mat
     * @return Determinante der übergebenen Matrix
     */
    public static double getDet(double[][] mat) {
        switch (mat.length) {
            case 3:
                return getDet3x3(mat);
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * Liefert die Determinate einer 3x3-Matrix.
     *
     * @return Determinate der 3x3-Matrix
     */
    private static double getDet3x3(double[][] mat) {
        double value
                = mat[0][0] * mat[1][1] * mat[2][2]
                + mat[0][1] * mat[1][2] * mat[2][0]
                + mat[0][2] * mat[1][0] * mat[2][1]
                - mat[0][0] * mat[1][2] * mat[2][1]
                - mat[0][1] * mat[1][0] * mat[2][2]
                - mat[0][2] * mat[1][1] * mat[2][0];
        return value;
    }

    /**
     * Liefert die Inverse eine quadratischen positiv definiten Matrix In der
     * Methode wird entschieden, welches Algorithmus dafür verwendet wird. Für
     * erst ab größer 3x3 wird eine Choleskyzerlegung verwendet.
     *
     * @param mat
     * @return inverse der Matrix mat
     */
    public static double[][] getInverse(double[][] mat) {
        switch (mat.length) {
            case 3:
                return getInverse3x3(mat);
            default:
                return getInverseCholesky(mat);
        }
    }

    private static double[][] getInverse3x3(double[][] mat) {
        double[][] inv = new double[3][3];

        // Hier kann nochmal korrigiert werden. Die Inverse einer symmetrischen Matrix sollte wieder symmetrisch sein.
        inv[0][0] = mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1];
        inv[0][1] = mat[0][2] * mat[2][1] - mat[0][1] * mat[2][2];
        inv[0][2] = mat[0][1] * mat[1][2] - mat[0][2] * mat[1][1];
        inv[1][0] = mat[1][2] * mat[2][0] - mat[1][0] * mat[2][2];
        inv[1][1] = mat[0][0] * mat[2][2] - mat[0][2] * mat[2][0];
        inv[1][2] = mat[0][2] * mat[1][0] - mat[0][0] * mat[1][2];
        inv[2][0] = mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0];
        inv[2][1] = mat[0][1] * mat[2][0] - mat[0][0] * mat[2][1];
        inv[2][2] = mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];

        double detInv = 1.0 / getDet(mat);

        for (int ii = 0; ii < 3; ii++) {
            for (int jj = 0; jj < 3; jj++) {
                inv[ii][jj] *= detInv;
            }
        }

        return inv;
    }

    private static double[][] getInverseCholesky(double[][] mat) {

        int i, ii, j;
        int m = mat.length;
        double h;
        double[] tempVec;
        double[] x = new double[m];
        double[][] L = new double[m][m];

        // Choleskyzerlegung der Matrix mat
        for (i = 0; i < m; i++) {
            h = 0.0;
            tempVec = L[i];
            for (j = 0; j < i; j++) {
                h += tempVec[j] * tempVec[j];
            }
            L[i][i] = Math.sqrt(mat[i][i] - h); // Wenn das nicht geht, ist die Matrix nicht positiv definit!
            for (ii = i + 1; ii < m; ii++) {
                h = 0;
                for (j = 0; j <= i - 1; j++) {
                    h += L[ii][j] * tempVec[j];
                }
                L[ii][i] = (mat[ii][i] - h) / tempVec[i];
            }
        }

        //Elemente von L sind jetzt unterhalb der Hauptdiagonale gespeichert
        //Inverse von L berechnen:
        for (i = 0; i < m; i++) {
            x[i] = 1.0 / L[i][i];
            for (j = i + 1; j < m; j++) {
                h = 0;
                tempVec = L[j];
                for (ii = i; ii <= j - 1; ii++) {
                    h += tempVec[ii] * x[ii];
                }

                x[j] = -h / tempVec[j];
            }

            for (j = i; j < m; j++) {
                L[i][j] = x[j];
            }
        }

        double[][] kInverse = new double[m][m];

        //Berechnung des Matrizenprodukts inv(L) * inv(L^t):
        //(gespeichert wird dies in K; beachte Symmetrie)
        for (i = 0; i < m; i++) {
            for (j = 0; j <= i; j++) {
                kInverse[i][j] = L[j][i];
            }
        }

        for (i = 0; i < m; i++) {
            tempVec = kInverse[i];
            for (j = i; j < m; j++) {
                h = 0;
                for (ii = Math.min(i, j); ii < m; ii++) {
                    h += L[i][ii] * kInverse[ii][j];
                }
                tempVec[j] = h;
            }
        }

        for (i = 0; i < m; i++) {
            for (j = i; j < m; j++) {
                kInverse[j][i] = kInverse[i][j];
            }
        }

        return kInverse;
    }

    /**
     * Löst das lineare Gleichungssystem Ax=b mittels Cholesky-Faktorisierung.
     * Das Verfahren basiert auf der Arbeit von M. Herrich: "Eigenwertberechnung
     * nach dem Jacobi-Verfahren" und wird auch im Beulmodul verwendet.
     *
     * @param A symmetrisch positiv definite Matrix
     * @param b rechte Seite
     * @return Lösungsvektor des Gleichungssystems.
     */
    public static double[] solveAbCholesky(double[][] A, double[] b) {

        int i, j, ii;
        int nm = A.length;
        double[][] L = new double[nm][nm];
        double[] x = new double[nm];
        double h;
        double[] tempVec;

        // Cholesky-Faktorisierung von A:
        // L*L'*x = b
        for (i = 0; i < nm; i++) {
            h = 0.0;
            tempVec = L[i];
            for (j = 0; j <= i - 1; j++) {
                h += tempVec[j] * tempVec[j];
            }
            L[i][i] = Math.sqrt(A[i][i] - h);
            for (ii = i + 1; ii < nm; ii++) {
                h = 0;
                for (j = 0; j <= i - 1; j++) {
                    h += L[ii][j] * tempVec[j];
                }
                L[ii][i] = (A[ii][i] - h) / tempVec[i];
            }
        }//Elemente von L sind jetzt unterhalb der Hauptdiagonale gespeichert

        // Umspeichern der rechte Seite in den Lösungsvektor
        for (i = 0; i < nm; i++) {
            x[i] = b[i];
        }

        // Löse L*y = b;
        for (int k = 0; k < nm; k++) {
            for (i = 0; i < k; i++) {
                x[k] -= x[i] * L[k][i];
            }
            x[k] /= L[k][k];
        }

        // Löse L'*x = y;
        for (int k = nm - 1; k >= 0; k--) {
            for (i = k + 1; i < nm; i++) {
                x[k] -= x[i] * L[i][k];
            }
            x[k] /= L[k][k];
        }
        return x;
    }

    /**
     * Löst das lineare Gleichungssystem Ax=b mittels LU-Faktorisierung.
     *
     * @param B symmetrische Matrix
     * @param b rechte Seite
     * @return Lösungsvektor des Gleichungssystems.
     */
    public static double[] solveAbLU(double[][] B, double[] b) {

        int n;
        double u;

        double[][] A, L, U;

        n = B.length;
        A = new double[n][n];
        L = new double[n][n];
        U = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(B[i], 0, A[i], 0, n);
        } // i-te Zeile von B kopieren

        // Zerlegung
        for (int k = 0; k < n; k++) {
            // k-te Zeile+Spalte von L+U berechnen
            u = A[k][k];
            U[k][k] = u;
            L[k][k] = 1.0;
            System.arraycopy(A[k], k + 1, U[k], k + 1, n - (k + 1));
            for (int i = k + 1; i < n; i++) {
                L[i][k] = A[i][k] / u;
            }
            // A in A'' umformen fuer nächsten Schritt
            for (int i = k + 1; i < n; i++) {
                for (int j = k + 1; j < n; j++) {
                    A[i][j] -= L[i][k] * U[k][j];
                }
            }
        }

        // löse untere L * y = b
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = b[i];                                                       // i-te Zeile auflösen nach y[i]
            for (int j = 0; j < i; j++) {
                y[i] -= L[i][j] * y[j];
            }             // dazu alle y[j] mit j<i einsetzen
        }

        // lösere obere löse U * x = y
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = y[i];                                                       // i-te Zeile auflösen nach U[i][i]*x[i]
            for (int j = i + 1; j < n; j++) {
                x[i] -= U[i][j] * x[j];
            }        // dazu alle x[j] mit j>i einsetzen
            x[i] /= U[i][i];                                                   // jetzt auflösen nach x[i]
        }

        return x;
    }

    /**
     * Löst das lineare Gleichungssystem Ax=b mittels LU-Faktorisierung. Das
     * Verfahren basiert auf der Arbeit von M. Herrich: "Eigenwertberechnung
     * nach dem Jacobi-Verfahren" und wird auch im Beulmodul verwendet. Vorher
     * wird für ausgewählte Werte die linke und die rechte Seite getauscht.
     *
     * @param A symmetrisch positiv definite Matrix
     * @param b rechte Seite
     * @param exchange Angabe ueber Austausch der entsprechenden Elemente
     * @return Lösungsvektor des Gleichungssystems.
     */
    public static double[] solveAbWithExchange(double[][] A, double[] b, boolean[] exchange) {
        double[][] tA = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            System.arraycopy(A[i], 0, tA[i], 0, A[0].length);
        }
        for (int i = 0; i < 6; i++) {
            if (exchange[i]) {
                tA = exchange(tA, i, i);
            }
        }

        return solveAbLU(tA, b);
    }

    /**
     * Austausch der Abhängigen nach Bronstein S279.
     *
     * Dieser Austausch erzeugt unsymmetrische Matrizen. Somit kann das
     * Cholesky-Verfahren zur Lösung des Gleichungssystems nicht verwendet
     * werden.
     *
     */
    private static double[][] exchange(double[][] mat, int i, int k) {
        if (mat == null) {
            return null;
        }

        int m = mat.length;
        int n = mat[0].length;

        double[][] newMat = new double[m][n];

        newMat[i][k] = 1.0 / mat[i][k];

        for (int mu = 0; mu < m; mu++) {
            if (mu == i) {
                continue;
            }
            newMat[mu][k] = mat[mu][k] / mat[i][k];
        }

        for (int nu = 0; nu < n; nu++) {
            if (nu == k) {
                continue;
            }
            newMat[i][nu] = -mat[i][nu] / mat[i][k];
        }

        for (int mu = 0; mu < m; mu++) {
            if (mu == i) {
                continue;
            }
            for (int nu = 0; nu < n; nu++) {
                if (nu == k) {
                    continue;
                }
                newMat[mu][nu] = mat[mu][nu] - mat[mu][k] * mat[i][nu] / mat[i][k];
            }
        }

        return newMat;
    }

    /**
     * eigenvaluescalc computes the m*n eigenvalues and resulting eigenvectors
     * calculations are based on the document: "Eigenwertberechnung nach dem
     * Jacobi-Verfahren" by M.Herrich
     *
     * @param Kg
     * @param K
     * @param number
     * @param m
     * @param n
     * @param eigenvecs
     * @return
     */
    public static double[] getEigenValues(double[][] Kg, double[][] K, int number, int m, int n, double[][][] eigenvecs) {

        //int m_ = K.length;
        //int n_ = K[0].length;
        int nm = m * n;

        int i, j, ii, zeiger, p, q, zaehler;
        double[][] L = new double[nm][nm];
        double[] x = new double[nm];
        double[] eigenvalues = new double[number];
        double[] z = new double[nm];
        double h, tau, delta, rho, t, s, c, w, eps, skp1;
        double[][] eigvec = new double[nm][number];
        double[] tempVec;
        //Cholesky-Faktorisierung von K:

        for (i = 0; i < nm; i++) {
            h = 0.0;
            tempVec = L[i];
            for (j = 0; j <= i - 1; j++) {
                h += tempVec[j] * tempVec[j];
            }
            L[i][i] = Math.sqrt(K[i][i] - h);           // hier steigt der Eigenwertlöser aus, weil der Wert aus dem die Wurzel gezogen werden soll negativ wird
            for (ii = i + 1; ii < nm; ii++) {
                h = 0;
                for (j = 0; j <= i - 1; j++) {
                    h += L[ii][j] * tempVec[j];
                }
                L[ii][i] = (K[ii][i] - h) / tempVec[i];
            }
        }

        //Elemente von L sind jetzt unterhalb der Hauptdiagonale gespeichert
        //Inverse von L berechnen:
        for (i = 0; i < nm; i++) {
            x[i] = 1 / L[i][i];
            for (j = i + 1; j < nm; j++) {
                h = 0;
                tempVec = L[j];
                for (ii = i; ii <= j - 1; ii++) {
                    h += tempVec[ii] * x[ii];
                }

                x[j] = -h / tempVec[j];
            }

            for (j = i; j < nm; j++) {
                L[i][j] = x[j];
            }
        }

        //In L ist jetzt die Inverse vom urspr�nglichen L gespeichert.
        //(oberhalb der Hauptdiagonalen)
        //Berechnung des Matrizenprodukts inv(L) * Kg * inv(L^t):
        //(gespeichert wird dies in K; beachte Symmetrie)
        for (i = 0; i < nm; i++) {
            for (j = 0; j <= i; j++) {
                h = 0;
                for (ii = 0; ii <= i; ii++) {
                    h += L[ii][i] * Kg[ii][j];
                }

                K[i][j] = h;
                K[j][i] = h;
            }
        }

        for (i = 0; i < nm; i++) {
            tempVec = K[i];
            for (j = 0; j <= i; j++) {
                h = 0;
                for (ii = 0; ii <= j; ii++) {
                    h += K[ii][i] * L[ii][j];
                }

                tempVec[j] = h;
            }
        }

        //K ist jetzt unsere symmetrische Matrix, von der wir alle Eigenwerte bestimmen wollen.
        //(richtige Werte sind nur unterhalb der Hauptdiagonalen gespeichert)
        //In Kg sollen nun die Eigenvektoren gespeichert werden.
        //Berechnung aller Eigenwerte und Eigenvektoren mittels Jacobi-Verfahren.
        w = 0;
        for (i = 0; i < nm; i++) {
            Kg[i][i] = 1;
        }

        for (i = 1; i < nm; i++) {
            for (j = 0; j <= i - 1; j++) {
                Kg[i][j] = 0;
                Kg[j][i] = 0;
                w += K[i][j] * K[i][j];
            }
        }

        eps = 1.0e-10;
        zaehler = 0;
        zeiger = 0;
        p = q = 0;
        while ((w > eps) && (zeiger == 0) && (zaehler < 100000)) {
            zaehler += 1;
            for (i = 0; i < nm; i++) {
                x[i] = K[i][i];
            }

            h = 0;
            for (i = 1; i < nm; i++) {
                for (j = 0; j <= i - 1; j++) {
                    if (h < Math.abs(K[i][j])) {
                        h = Math.abs(K[i][j]);
                        p = i;
                        q = j;
                    }
                }
            }

            delta = (K[q][q] - K[p][p]) / 2 / K[p][q];
            rho = Math.abs(delta) + Math.sqrt(1 + delta * delta);
            if (delta >= 0) {
                t = 1 / rho;
            } else {
                t = -1 / rho;
            }
            c = 1 / Math.sqrt(1 + t * t);
            s = c * t;
            tau = s / (1 + c);
            w -= K[p][q] * K[p][q];

            K[p][p] -= t * K[p][q];
            K[q][q] += t * K[p][q];
            K[p][q] = 0;

            for (i = 0; i <= q - 1; i++) {
                z[i] = K[p][i];
                K[p][i] -= s * (K[q][i] + tau * K[p][i]);
            }
            for (i = q + 1; i <= p - 1; i++) {
                z[i] = K[p][i];
                K[p][i] -= s * (K[i][q] + tau * K[p][i]);
            }
            for (i = p + 1; i < nm; i++) {
                z[i] = K[i][p];
                K[i][p] -= s * (K[i][q] + tau * K[i][p]);
            }
            for (i = 0; i <= q - 1; i++) {
                K[q][i] += tau * (z[i] + K[p][i]);
            }

            for (i = q + 1; i <= p - 1; i++) {
                K[i][q] += tau * (z[i] + K[p][i]);
            }

            for (i = p + 1; i < nm; i++) {
                K[i][q] += tau * (z[i] + K[i][p]);
            }

            for (i = 0; i < nm; i++) {
                z[i] = Kg[i][p];
                Kg[i][p] -= s * (Kg[i][q] + tau * Kg[i][p]);
            }
            for (i = 0; i < nm; i++) {
                Kg[i][q] += tau * (z[i] + Kg[i][p]);
            }

            zeiger = 1;
            for (i = 0; i < nm; i++) {
                if (Math.abs(x[i] - K[i][i]) > eps) {
                    zeiger = 0;
                    break;
                }
            }
        }

        for (i = 0; i < number; i++) {
            h = 0;
            p = 0;
            for (j = 0; j < nm; j++) {
                if (Math.abs(K[j][j]) > Math.abs(h)) {
                    h = K[j][j];
                    p = j;
                }
            }
            eigenvalues[i] = - 1 / h;
            K[p][p] = 0;
            for (j = 0; j < nm; j++) {
                eigvec[j][i] = Kg[j][p];
            }

            for (ii = 0; ii < nm; ii++) {
                h = 0;
                for (j = ii; j < nm; j++) {
                    h += L[ii][j] * eigvec[j][i];
                }
                eigvec[ii][i] = h;
            }

            skp1 = 0;
            for (ii = 0; ii < nm; ii++) {
                skp1 = skp1 + eigvec[ii][i] * eigvec[ii][i];
            }

            skp1 = Math.sqrt(skp1);
            int iTemp, mm, nn;
            for (mm = 0; mm < m; mm++) {
                iTemp = mm * n;
                for (nn = 0; nn < n; nn++) {
                    eigenvecs[i][mm][nn] = eigvec[iTemp + nn][i] / skp1;
                }
            }

        }

        return eigenvalues;
    }

    /**
     * Berechnung Matrix mal Vektor (Ab).
     *
     * @param A eingabewerte der Matrix A
     * @param b eingabewerte des Vektors
     * @return {@link double} array mit berechneten hygrokoeffizienten
     */
    public static double[] MatVecMult(double[][] A, double[] b) {
        double[] rs = new double[A.length];

        for (int ii = 0; ii < A.length; ii++) {
            rs[ii] = 0.0;
            for (int jj = 0; jj < A[0].length; jj++) {
                rs[ii] += A[ii][jj] * b[jj];
            }
        }

        return rs;
    }

    /**
     * Berechnung Matrix mal Matrix (AB).
     *
     * @param A eingabewerte der Matrix A
     * @param B eingabewerte der Matrix B
     * @return multiplizierte Matrix
     */
    public static double[][] MatMult(double[][] A, double[][] B) {
        // Initialisierung der Ergebnismatrix
        // Achtung: An dieser Stelle wäre in anderen Programmiersprachen ein Nullsetzen
        // der Matrixeinträge notwendig. Java macht dies intern automatrisch.
        double[][] rs = new double[A.length][B[0].length];

        // Führe Matrixmultiplikation aus
        for (int ii = 0; ii < rs.length; ii++) {
            for (int jj = 0; jj < rs[0].length; jj++) {
                for (int kk = 0; kk < A[0].length; kk++) {
                    rs[ii][jj] += A[ii][jj] * B[jj][kk];
                }
            }
        }

        return rs;
    }

    /**
     * Berechnung der Transponierten Matrix transp(A).
     *
     * @param A eingabewerte der Matrix A
     * @return transponierte Matrix
     */
    public static double[][] MatTransp(double[][] A) {
        double[][] rs = new double[A[0].length][A.length];

        for (int ii = 0; ii < rs.length; ii++) {
            for (int jj = 0; jj < rs[0].length; jj++) {
                rs[ii][jj] = A[jj][ii];
            }
        }

        return rs;
    }

    /**
     * interne hilfmethode welche jedes array value mit einem faktor
     * multipliziert.
     *
     * @param vektor array mit {@link double} Werten welche einzeln mit dem
     * Faktor multipliziert werden.
     * @param scalar Faktor fuer Multiplikation
     * @return array mit multpilizierten werten
     */
    public static double[] multiply(double[] vektor, double scalar) {
        double[] rs = new double[vektor.length];

        for (int i = 0; i < vektor.length; i++) {
            rs[i] = vektor[i] * scalar;
        }

        return rs;
    }

    /**
     * interne hilfsmethode zu addieren von arrays
     *
     * @param a array 1 (summand)
     * @param b array 2 (summand)
     * @return array array 1 + array 2
     */
    public static double[] add(double[] a, double[] b) {
        double[] rs = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            rs[i] = a[i] + b[i];
        }
        return rs;
    }

    public static void writeMatrix(double[][] mat) {
        for (double[] mat1 : mat) {
            for (int jj = 0; jj < mat[0].length; jj++) {
                System.out.print("" + mat1[jj] + "; ");
            }
            System.out.println("");
        }
    }

    /**
     * Liefert einen Verlauf der A-Matrix bei rotierendem Koordinatensystem.Das
     * Koordinatensystem wird zwischen 0° und 360° um den Winkel
     * <code>deltaAngle</code> gedreht. Der Winkel <code>deltaAngle</code> wird
     * in ° übergeben. Das zurückgegeben Array ist wiefolgt aufgebaut.<br>
     * 1. Index : Die entsprechende A-Matrixkomponente. Diese sollte über die
     * statischen Variaben <code>A11</code>, <code>A12</code>, <code>A22</code>,
     * <code>A66</code> angesprochen werden<br>
     * 2. Index : Nummer des Winkels. Diese berechnet sich aus
     * i*<code>deltaAngle</code> + &alpha;, wobei &alpha; der Winkel der Lage
     * ist.
     *
     * @param mat
     * @param deltaAngle Winkeldifferenz, um die gedreht wird.
     * @return A-Matrixkomponenten für alle Winkel
     */
    public static double[][] getMatrixComponentsOverAngle(double[][] mat, double deltaAngle) {

        int number = (int) (360 / deltaAngle);

        double[][] distribution = new double[5][number];
        double angle, c, c2, c3, c4, s, s2, s3, s4;
        double temp = Math.PI / 180.0;

        double a11 = mat[0][0];
        double a12 = mat[0][1];
        double a16 = mat[0][2];
        double a22 = mat[1][1];
        double a26 = mat[1][2];
        double a66 = mat[2][2];

        for (int i = 0; i < number; i++) {

            angle = deltaAngle * i;

            c = Math.cos(angle * temp);
            c2 = c * c;
            c3 = c2 * c;
            c4 = c3 * c;

            s = Math.sin(angle * temp);
            s2 = s * s;
            s3 = s2 * s;
            s4 = s3 * s;

            distribution[0][i] = deltaAngle * i;
            // A_11
            distribution[1][i] = c4 * a11 + 2 * c2 * s2 * a12 - 4 * c3 * s * a16 + s4 * a22 - 4 * s3 * c * a26 + 4 * c2 * s2 * a66;
            // A_12
            distribution[2][i] = c2 * s2 * a11 + (c4 + s4) * a12 + 2 * (s * c3 - c * s3) * a16 + c2 * s2 * a22 + 2 * (c * s3 - s * c3) * a26 - 4 * c2 * s2 * a66;
            // A_22
            distribution[3][i] = s4 * a11 + 2 * c2 * s2 * a12 + 4 * s3 * c * a16 + c4 * a22 + 4 * c3 * s * a26 + 4 * c2 * s2 * a66;
            // A_66
            distribution[4][i] = c2 * s2 * a11 - 2 * c2 * s2 * a12 + 2 * (s * c3 - c * s3) * a16 + c2 * s2 * a22 - 2 * (c3 * s - s3 * c) * a26 + (c2 - s2) * (c2 - s2) * a66;
        }
        return distribution;
    }
}
