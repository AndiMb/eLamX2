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
package de.elamx.clt.plate.Boundary;

/**
 * Diese Klasse berechnet oder enthält die notwendigen Ableitungen und
 * Integrale des Verschiebungsansatzes für den Ritzansatz.
 * Alle Rückgabewerte basieren auf den Vibrationseigenformen eines Bernoullibalkens
 * mit einer zusätzlichen Konstante c5, um die Starrkörperverschiebungen
 * beschreiben zu können. Abhängig von den Randbedingungen werden die fünf
 * constanten belegt. Derzeit sind die ersten 20 Nullstellen der charakterisischen
 * Gleichung und die entsprechende Anzahl aller Integrale bestimmbar. Die Konstanten
 * und Integrale selbst müssen mit einem extra Tool mit höherer Rechengenauigkeit
 * (mind. 100 signifikante Stellen) bestimmt werden. Dies ist mit 16 signifikanten
 * Stellen nicht möglich.
 *
 * @author Martin Rädel
 * @author Oliver Hennig
 * @author Andreas Hauffe
 */
public abstract class Boundary {

    protected int    m_ = 0;   // Anzahl der Halbwellen in die entsprechende Richtung
    protected double a_ = 0.0; // Länge in die entsprechende Richtung

    // Konstanten des Verschiebungsansatzes
    protected double[] c1_, c2_, c3_, c4_, c5_;

    // Nullstellen der charakteristischen Gleichung
    protected double[] cv_;

    protected double[] IX_;

    // Integrale des Verschiebungsansatzes
    protected double[][] IXX_, IXdX_, IXdX2_, IdXdX_, IdXdX2_, IdX2dX2_;

    // Zwei temporäre double Variablen, um sie nicht bei jedem Methodenaufruf neu anlegen zu müssen.
    private double temp;
    private double cva;

    /**
     * Dieser Konstruktor muss beim Generieren einen neues Boundary-Objekts aufgerufen werden.
     * Er speichert die Länge und die Anzahl der Halbwellen und ruft die Initialisierung
     * der Felder auf. Diese werden dann mit Werten der entsprechenden Randbedingung
     * gefüllt.
     * 
     * @param length Länge
     * @param m maximale Anzahl der Halbwellen
     */
    public Boundary (double length, int m){
        m_ = Math.min(m, 20);
        a_ = length;
        initArrays();
    }

    public double getA(){return a_;}

    protected abstract void initArrays();

    /**
     * Grundansatzfunktion
     * c1 * sin(cv*x/a) + c2 * cos(cv*x/a) + c3 * sinh(cv*x/a) + c4 * cosh(cv*x/a) + c5
     * @param i Nullstelle
     * @param x Position
     * @return Verschiebung an der Stelle x
     */
    public double wx(int i,double x){
        temp = cv_[i]*x/a_;
        return   c1_[i]* Math.sin(temp)
               + c3_[i]*Math.sinh(temp)
               + c2_[i]* Math.cos(temp)
               + c4_[i]*Math.cosh(temp)
               + c5_[i];
    }
    /**
     * Grundansatzfunktion | erste Ableitung
     * (c1 * cos(cv*x/a) - c2 * sin(cv*x/a) + c3 * cosh(cv*x/a) + c4 * sinh(cv*x/a) + c5)*(cv/a)
     * @param i Nullstelle
     * @param x Position
     * @return erste Ableitung der Verschiebung an der Stelle x
     */
    public double wdx(int i,double x){
        cva = cv_[i]/a_;
        temp = cva*x;
        return  (c1_[i]* Math.cos(temp)
               + c3_[i]*Math.cosh(temp)
               - c2_[i]* Math.sin(temp)
               + c4_[i]*Math.sinh(temp))*cva;
    }
    /**
     * Grundansatzfunktion | zweite Ableitung
     * (-c1 * sin(cv*x/a) - c2 * cos(cv*x/a) + c3 * sinh(cv*x/a) + c4 * cosh(cv*x/a) + c5)*(cv/a)*(cv/a)
     * @param i Nullstelle
     * @param x Position
     * @return zweite Ableitung der Verschiebung an der Stelle x
     */
    public double wdx2(int i,double x){
        cva = cv_[i]/a_;
        temp = cva*x;
        return (- c1_[i]* Math.sin(temp)
                + c3_[i]*Math.sinh(temp)
                - c2_[i]* Math.cos(temp)
                + c4_[i]*Math.cosh(temp))*cva*cva;
    }
    /**
     * Grundansatzfunktion | dritte Ableitung
     * (-c1 * cos(cv*x/a) + c2 * sin(cv*x/a) + c3 * cosh(cv*x/a) + c4 * sinh(cv*x/a) + c5)*(cv/a)*(cv/a)*(cv/a)
     * @param i Nullstelle
     * @param x Position
     * @return zweite Ableitung der Verschiebung an der Stelle x
     */
    public double wdx3(int i,double x){
        cva = cv_[i]/a_;
        temp = cva*x;
        return (- c1_[i]* Math.cos(temp)
                + c3_[i]*Math.cosh(temp)
                + c2_[i]* Math.sin(temp)
                + c4_[i]*Math.sinh(temp))*cva*cva*cva;
    }

    /**
     * Integral von 0 bis a über X_i(x) dx
     * @param i i-Ansatz der Verschiebung
     * @return Integral von 0 bis a über X_i(x) dx
     */
    public double IX(int i){
        return IX_[i]*a_;
    }

    /**
     * Integral von x1 bis x2 über X_i(x) dx
     * @param i i-Ansatz der Verschiebung
     * @param x1 untere Grenze des Integrals
     * @param x2 obere Grenze des Integrals
     * @return Integral von x1 bis x2 über X_i(x) dx
     */
    /*public double IX(int i, double x1, double x2){
        return IX_[i]*(x2-x1);
    }*/
    public double IX(int i, double x2, double x1){
        return  ((c1_[i] * (- Math.cos(cv_[i]*x2/a_) +  Math.cos(cv_[i]*x1/a_))
                + c3_[i] * ( Math.cosh(cv_[i]*x2/a_) - Math.cosh(cv_[i]*x1/a_))
                + c2_[i] * (  Math.sin(cv_[i]*x2/a_) -  Math.sin(cv_[i]*x1/a_))
                + c4_[i] * ( Math.sinh(cv_[i]*x2/a_) - Math.sinh(cv_[i]*x1/a_)))*(a_/(cv_[i]))
                + c5_[i] * (x2-x1));
    }

    /**
     * Integral von 0 bis a über X_i(x)*X_p(x) dx
     * i und p sind vertauschbar, da für Verschiebung und dessen Variation
     * der gleiche Ansatz verwendet wird.
     * @param i i-Ansatz der Verschiebung
     * @param p p-Ansatz der Variation
     * @return Integral von 0 bis a über X_i(x)*X_p(x) dx
     */
    public double IXX(int i, int p){
        return IXX_[i][p]*a_;
    }

    /**
     * Integral von 0 bis a über X_i(x)*dX_p(x)/dx dx
     * i und p sind vertauschbar, da für Verschiebung und dessen Variation
     * der gleiche Ansatz verwendet wird.
     * @param i i-Ansatz der Verschiebung
     * @param p p-Ansatz der Variation
     * @return Integral von 0 bis a über X_i(x)*dX_p(x)/dx dx
     */
    public double IXdX(int i, int p){
        return IXdX_[i][p];
    }

    /**
     * Integral von 0 bis a über X_i(x)*d2X_p(x)/dx2 dx
     * i und p sind vertauschbar, da für Verschiebung und dessen Variation
     * der gleiche Ansatz verwendet wird.
     * @param i i-Ansatz der Verschiebung
     * @param p p-Ansatz der Variation
     * @return Integral von 0 bis a über X_i(x)*d2X_p(x)/dx2 dx
     */
    public double IXdX2(int i, int p){
        return IXdX2_[i][p]/a_;
    }

    /**
     * Integral von 0 bis a über dX_i(x)/dx*dX_p(x)/dx dx
     * i und p sind vertauschbar, da für Verschiebung und dessen Variation
     * der gleiche Ansatz verwendet wird.
     * @param i i-Ansatz der Verschiebung
     * @param p p-Ansatz der Variation
     * @return Integral von 0 bis a über dX_i(x)/dx*dX_p(x)/dx dx
     */
    public double IdXdX(int i, int p){
        return IdXdX_[i][p]/a_;
    }

    /**
     * Integral von 0 bis a über dX_i(x)/dx*d2X_p(x)/dx2 dx
     * i und p sind vertauschbar, da für Verschiebung und dessen Variation
     * der gleiche Ansatz verwendet wird.
     * @param i i-Ansatz der Verschiebung
     * @param p p-Ansatz der Variation
     * @return Integral von 0 bis a über dX_i(x)/dx*d2X_p(x)/dx2 dx
     */
    public double IdXdX2(int i, int p){
        return IdXdX2_[i][p]/a_/a_;
    }

    /**
     * Integral von 0 bis a über d2X_i(x)/dx2*d2X_p(x)/dx2 dx
     * i und p sind vertauschbar, da für Verschiebung und dessen Variation
     * der gleiche Ansatz verwendet wird.
     * @param i i-Ansatz der Verschiebung
     * @param p p-Ansatz der Variation
     * @return Integral von 0 bis a über d2X_i(x)/dx2*d2X_p(x)/dx2 dx
     */
    public double IdX2dX2(int i, int p){
        return IdX2dX2_[i][p]/a_/a_/a_;
    }

    public double getCv(int i){return cv_[i];}
    public double getC1(int i){return c1_[i];}
    public double getC2(int i){return c2_[i];}
    public double getC3(int i){return c3_[i];}
    public double getC4(int i){return c4_[i];}
    public double getC5(int i){return c5_[i];}
}