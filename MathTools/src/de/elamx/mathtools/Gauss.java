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

/*
 * Klasse zum Gauss-Eliminations-Verfahren
 **/
public class Gauss {

    double[][] A;
    private int n;
    private double[][] L;
    private double[][] U;

    /**
     * Konstruktor A: Matrix initialisiert
     *
     * @param A Matrix*
     */
    public Gauss(double[][] A) {
        factor(A);
    }

    /**
     * Konstruktor B: Matrix uninitialisiert
     */
    public Gauss() {
    }

    /**
     * Hauptmethode Eliminationsverfahren
     */
    private void factor(double[][] B) {
        // assert isSquare(B) ;
        n = B.length;
        A = new double[n][];
        for (int i = 0; i < n; i++) {
            A[i] = new double[n];
            // i-te Zeile von B kopieren
            System.arraycopy(B[i], 0, A[i], 0, n);
        }
        L = new double[n][n];
        U = new double[n][n];
        step(0);
    }

    /**
     * Hilfsmethode: k-ter Zerlegungsschritt
     */
    private void step(int k) {
        if (k < n) {
            // k-te Zeile+Spalte von L+U berechnen
            double u = A[k][k];
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
            // nächster Schritt
            step(k + 1);
        }
        // else: fertig!
    }

    /**
     * Matrix A zerlegen und mit rechter Seite b lösen
     *
     * @param A Matrix
     * @param b Vektor der rechten Seite
     * @return Lösungsvektor von A*x = b
     */
    public double[] solve(double[][] A, double[] b) {
        // assert isSquare(A) ;
        // assert A.length == b.length ;
        factor(A);
        return solve(b);
    }

    /**
     * Letzte zerlegte Matrix mit rechter Seite b lösen
     *
     * @param b Vektor der rechten Seite
     * @return Lösungsvektor von A*x = b
     */
    public double[] solve(double[] b) {
        // assert b.length == n ;
        double[] y = solveLower(b);
        double[] x = solveUpper(y);
        return x;
    }

    /**
     * Matrix A invertieren durch Lösung mit Einheitsvektoren
     *
     * @param A Matrix
     * @return invertierte Matrix
     */
    public double[][] invert(double[][] A) {
        // assert isSquare(A) ;
        factor(A);
        double[][] Ainv = new double[n][n];
        for (int j = 0; j < n; j++) {
            double[] x = solve(base(n, j));
            // x ist j-te Spalte von Ainv
            for (int i = 0; i < n; i++) {
                Ainv[i][j] = x[i];
            }
        }
        return Ainv;
    }

    /**
     * Hilfsmethode: löse L * y = b
     */
    private double[] solveLower(double[] b) {
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            // i-te Zeile auflösen nach y[i]
            y[i] = b[i];
            // dazu alle y[j] mit j<i einsetzen
            for (int j = 0; j < i; j++) {
                y[i] -= L[i][j] * y[j];
            }
        }
        return y;
    }

    /**
     * Hilfsmethode: löse U * x = y
     */
    private double[] solveUpper(double[] y) {
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            // i-te Zeile auflösen nach U[i][i]*x[i]
            x[i] = y[i];
            // dazu alle x[j] mit j>i einsetzen
            for (int j = i + 1; j < n; j++) {
                x[i] -= U[i][j] * x[j];
            }
            // jetzt auflösen nach x[i]
            x[i] /= U[i][i];
        }
        return x;
    }

    /**
     * Test ob M (n×m)-Matrix
     *
     * @param M Matrix, die geprüft werden soll
     * @param n Zeilenanzahl
     * @param m Spaltenanzahl
     * @return true, wenn die Matrix n Zeilen hat und jede Zeile m Spalten
     */
    public boolean isShape(double[][] M, int n, int m) {
        if (M.length != n) {
            return false;
        } else {
            for (int i = 0; i < n; i++) {
                if (M[i].length != m) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Test, ob M quadratisch
     *
     * @param M Matrix, die geprüft werden soll
     * @return true, wenn die Matrix quadratisch ist
     */
    public boolean isSquare(double[][] M) {
        return isShape(M, M.length, M.length);
    }

    /**
     * Erzeugt i-ten Einheitsvektor der Länge n
     *
     * @param n
     * @param i
     * @return 
     */
    public double[] base(int n, int i) {
        double[] b = new double[n];
        for (int j = 0; j < n; j++) {
            b[j] = 0.0;
        }
        b[i] = 1.0;
        return b;
    }

    /**
     * Erzeugt Einheitsmatrix der Größe n
     *
     * @param n
     * @return 
     */
    public double[][] unit(int n) {
        double[][] M = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                M[i][j] = 0.0;
            }
            M[i][i] = 1.0;
        }
        // oder:
        M = new double[n][];
        for (int i = 0; i < n; i++) {
            M[i] = base(n, i);
        }
        return M;
    }

}
