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
 * Diese Klasse repräsentiert ein Polynom, welches reelle Koeffizienten besitzt.
 *
 * @author Martin Rädel
 * @author Franziska Sommerfeld
 */
public class Polynom {

    //private static final int    maxIterations = 100;                          // Anzahl an Iterationsschritten nach denen automatisch abgebrochen wird.
    private static final double EPS = 1.0E-6;                                   //Genauigkeit der Nullstellen

    private final int n_;                                                       // Grad des Polynoms
    private double[] a_;                                                        // Koeffizienten des Polynoms p(x)=a0+a1*x+a2*x^2+...+an*x^n
    private final Complex[] roots_;                                             // Nullstellen

    /**
     * Konstruktor, für ein Polynom mit reellen Koeffizienten.<br>
     * f(x) = f_n*x^n + f_(n-1)*x^(n-1) + ... + f_1*x + f_0
     *
     * @param f Koeffizienten in der Reihenfolge f_0, f_1, f_2, ...
     */
    public Polynom(double[] f) {
        a_ = f;
        n_ = a_.length - 1;
        roots_ = new Complex[n_];
    }

    public Complex[] getRoots() {
        return roots_;
    }

    //--------------------------------------------------------------------------
    // andere Methoden
    //--------------------------------------------------------------------------
    /**
     * Gibt die Nullstellen des Polynoms an. Bei einem Polynom größer 2. Grade
     * wird das Verfahren nach Bairstow angewendet
     */
    public void calcRoots() {
        int idx = 0;

        if (n_ > 2) {
            bairstow();
        } else if (n_ == 2) {
            idx = quadrGl(idx, a_[1] / a_[2], a_[0] / a_[2]);
        } else {
            idx = linGl(idx, a_[0] / a_[1]);
        }
    }

    /**
     * Berechnung der Nullstellen eines Polynoms p(x)
     *
     * p(x)=a0+a1*x+a2*x^2+...+an*x^n
     *
     * mit reellen Koeffizienten ai mit der Methode nach Bairstow
     *
     * http://de.wikipedia.org/wiki/Bairstow-Verfahren
     * http://en.wikipedia.org/wiki/Bairstow%27s_method
     */
    private void bairstow() {

        double a0, a1;                                                          //a=x^2+a_1*x+a_0
        double da0, da1;                                                        //Anpassung a1, a0
        double[] b;                                                             //b=b_(n-2)*x^(n-2)+b_(n-1)*x^(n-1)+...
        double b1, b2;

        double[] q;
        double q1, q2;

        int dim = n_;
        int idx = 0;

        while (dim > 2) {
            a1 = a_[dim - 1] / a_[dim];
            a0 = a_[dim - 2] / a_[dim];

            if (a1 == 0) {
                a1 += EPS;
            }
            if (a0 == 0) {
                a0 += EPS;
            }

            da1 = 0;
            da0 = 0;

            b = new double[dim + 1];
            b[dim] = 0;
            b[dim - 1] = 0;

            q = new double[dim - 1];
            q[dim - 2] = 0;
            q[dim - 3] = 0;

            do {
                a1 = a1 - da1;
                a0 = a0 - da0;

                for (int j = dim - 2; j >= 0; j--) {
                    b[j] = a_[j + 2] - a1 * b[j + 1] - a0 * b[j + 2];
                    if (j < dim - 3) {
                        q[j] = b[j + 2] - a1 * q[j + 1] - a0 * q[j + 2];
                    }
                }

                b1 = a_[1] - a1 * b[0] - a0 * b[1];
                b2 = a_[0] - a1 * b1 - a0 * b[0];

                q1 = b[1] - a1 * q[0] - a0 * q[1];
                q2 = b[0] - a1 * q1 - a0 * q[0];

                da1 = (q1 * b2 - q2 * b1) / (Math.pow(q2, 2) - (-a0 * q1 - a1 * q2) * q1);
                da0 = ((-a0 * q1 - a1 * q2) * b1 - q2 * b2) / (Math.pow(q2, 2) - (-a0 * q1 - a1 * q2) * q1);

            } while (Math.abs(b1) > EPS || Math.abs(b2) > EPS);

            idx = quadrGl(idx, a1, a0);

            dim = dim - 2;

            if (dim == 2) {
                idx = quadrGl(idx, b[1] / b[2], b[0] / b[2]);
                break;
            } else if (dim == 1) {
                idx = linGl(idx, b[0] / b[1]);
                break;
            } else {
                a_ = new double[dim + 1];
                System.arraycopy(b, 0, a_, 0, dim + 1);
            }
        }
    }

    private int quadrGl(int idx, double p, double q) {
        //x^2 + p*x + q = 0
        roots_[idx] = new Complex();
        roots_[idx + 1] = new Complex();

        roots_[idx].setRe(-p / 2);
        roots_[idx + 1].setRe(-p / 2);

        double root = Math.pow(p / 2, 2) - q;
        if (root >= 0) {
            roots_[idx] = roots_[idx].add(Math.sqrt(root));
            roots_[idx + 1] = roots_[idx + 1].add(-Math.sqrt(root));
        } else {
            roots_[idx].setIm(Math.sqrt(-root));
            roots_[idx + 1].setIm(-Math.sqrt(-root));
        }

        return (idx + 2);
    }

    private int linGl(int idx, double m) {
        //x + m = 0
        roots_[idx] = new Complex(-m);
        return (idx++);
    }
}
