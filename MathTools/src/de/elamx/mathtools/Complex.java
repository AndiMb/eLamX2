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

import java.util.Locale;

/**
 * Diese Klasse repräsentiert eine Komplexe Zahl. Sie besteht aus einem Real-
 * und Imaginärteil.
 *
 * @author Franziska Sommerfeld 12.03.2013
 * @author raedel
 * @author Andreas Hauffe
 */
public class Complex implements Comparable<Complex> {

    private double re_;
    private double im_;

    /**
     * KOnstruktor einer Komplexen Zahl. Real- und Imaginärteil werden 0
     * gesetzt.
     */
    public Complex() {
        this(0.0, 0.0);
    }

    /**
     * Konstruktor einer Komplexen Zahl. Übergeben wird der Realteil. Der
     * Imaginärteil wird 0 gesetzt.
     *
     * @param re Realteil
     */
    public Complex(double re) {
        this(re, 0.0);
    }

    /**
     * Konstruktor einer Komplexen Zahl. Übergeben wird der Realteil und der
     * Imaginärteil.
     *
     * @param re Realteil
     * @param im Imaginärteil
     */
    public Complex(double re, double im) {
        re_ = re;
        im_ = im;
    }

    /**
     * Setzt den Realteil der komplexen Zahl.
     *
     * @param re Realteil
     */
    public void setRe(double re) {
        re_ = re;
    }

    /**
     * Gibt den Realteil der komplexen Zahl wieder.
     *
     * @return Realteil
     */
    public double getRe() {
        return re_;
    }

    /**
     * Setzt den Imagimärteil der komplexen Zahl.
     *
     * @param im Imaginärteil
     */
    public void setIm(double im) {
        im_ = im;
    }

    /**
     * Gibt den Imaginärteil der komplexen Zahl wieder.
     *
     * @return Imaginärteil
     */
    public double getIm() {
        return im_;
    }

    /**
     * Addiert eine komplexe Zahl (zahl) zur komplexen Zahl.
     *
     * @param zahl Summand
     * @return Summe
     */
    public Complex add(Complex zahl) {
        return new Complex(re_ + zahl.getRe(), im_ + zahl.getIm());
    }

    /**
     * Addiert eine komplexe Zahl (zahl) zur komplexen Zahl.
     *
     * @param zahl Summand
     * @return Summe
     */
    public Complex add(double zahl) {
        return new Complex(re_ + zahl, im_);
    }

    /**
     * Addiert beliebig viele komplexe Zahlen.
     *
     * @param zahl Summanden
     * @return Summe
     */
    public static Complex addmultiple(Complex... zahl) {
        Complex tmp = new Complex();
        for (Complex z : zahl) {
            tmp = tmp.add(z);
        }
        return tmp;
    }

    /**
     * Subtrahiert eine komplexe Zahl (zahl) von der komplexen Zahl.
     *
     * @param zahl Subtrahend
     * @return Differenz
     */
    public Complex subtract(Complex zahl) {
        return new Complex(re_ - zahl.getRe(), im_ - zahl.getIm());
    }

    /**
     * Multipliziert eine komplexe Zahl (zahl) mit der komplexen Zahl.
     *
     * @param zahl Faktor
     * @return Produkt
     */
    public Complex multiply(Complex zahl) {
        double re2 = zahl.getRe();
        double im2 = zahl.getIm();
        return new Complex(re_ * re2 - im_ * im2, re_ * im2 + im_ * re2);
    }

    /**
     * Multipliziert eine Zahl (zahl) mit der komplexen Zahl.
     *
     * @param zahl Faktor
     * @return Produkt
     */
    public Complex multiply(Double zahl) {
        return new Complex(re_ * zahl, im_ * zahl);
    }

    /**
     * Multipliziert beliebig viele komplexe Zahlen.
     *
     * @param zahl Faktoren
     * @return Produkt
     */
    public static Complex multiplymultiple(Complex... zahl) {
        Complex tmp = new Complex(1, 0);
        for (Complex z : zahl) {
            tmp = tmp.multiply(z);
        }
        return tmp;
    }

    /**
     * Dividiert die komplexe Zahl mit einer komplexen Zahl (zahl).
     *
     * @param zahl Divisor
     * @return Quotient
     */
    public Complex divide(Complex zahl) {
        double re2 = zahl.getRe();
        double im2 = zahl.getIm();
        if (re2 == 0 && im2 == 0) {
            return null;
        }
        return new Complex((re_ * re2 + im_ * im2) / zahl.getHilfsBetrag(), (im_ * re2 - re_ * im2) / zahl.getHilfsBetrag());
    }

    /**
     * Potenziert eine komplexe Zahl (zahl hoch exponent).
     *
     * @param zahl Basis (komplexe Zahl)
     * @param exponent Exponent
     * @return zahl^exponent
     */
    static public Complex pow(Complex zahl, int exponent) {
        if (exponent == 0) {
            return new Complex(1, 0);
        }

        double re, im;
        double re_neu, im_neu;
        double re_alt, im_alt;
        int maxIt;

        if (exponent < 0) {
            double tmp = zahl.getHilfsBetrag();
            re = zahl.re_ / tmp;
            im = -zahl.im_ / tmp;
            maxIt = -exponent;
        } else {
            re = zahl.re_;
            im = zahl.im_;
            maxIt = exponent;
        }

        re_neu = re;
        im_neu = im;

        for (int i = 1; i < maxIt; i++) {
            re_alt = re_neu;
            im_alt = im_neu;
            re_neu = re_alt * re - im_alt * im;
            im_neu = re_alt * im + im_alt * re;
        }

        return new Complex(re_neu, im_neu);
    }

    private double getHilfsBetrag() {
        return re_ * re_ + im_ * im_;
    }

    /**
     * Gibt den Betrag der komplexen Zahl wieder
     *
     * @return Betrag
     */
    public double getBetrag() {
        return Math.sqrt(getHilfsBetrag());
    }

    /**
     * Negiert die komplexe Zahl.
     */
    public void negative() {
        re_ = -re_;
        im_ = -im_;
    }

    /**
     * Gibt die negative komplexe Zahl zurück.
     *
     * @return negativ komplexe Zahl
     */
    public Complex getNegative() {
        return new Complex(-re_, -im_);
    }

    /**
     * Konjugiert die komplexe Zahl.
     */
    public void conjugate() {
        im_ = -im_;
    }

    /**
     * Gibt die konjugierte komplexe Zahl zurück.
     *
     * @return kunjugiert Komplexe
     */
    public Complex getConjugate() {
        return new Complex(re_, -im_);
    }

    /**
     * Vergleicht die komplexe Zahl mit einer anderen komplexen Zahl um diese
     * anhand des Realteils in absteigender Reihenfolge sortieren zu können.
     *
     * @param zahl die komplexe Zahl mit der verglichen wird
     * @return Gibt 0 zurück wenn jeweils Realteil und Imaginärteil gleich sind.
     * Gibt -1 zurück, wenn der Realteil größer als der der zu vergleichenden
     * Zahl ist, bzw. wenn die Realteile gleich sind und der Imaginärteil größer
     * als der zu vergleichenden Zahl ist. Gibt 1 zurück, wenn der Realteil
     * kleiner als der der zu vergleichenden Zahl ist, bzw. wenn die Realteile
     * gleich sind und der Imaginärteil kleiner als der zu vergleichenden Zahl
     * ist.
     */
    @Override
    public int compareTo(Complex zahl) {
        if (Math.abs(re_) > Math.abs(zahl.getRe())) {
            return -1;
        }
        if (Math.abs(re_) < Math.abs(zahl.getRe())) {
            return 1;
        }
        if (Math.abs(im_) > Math.abs(zahl.getIm())) {
            return -1;
        }
        if (Math.abs(im_) < Math.abs(zahl.getIm())) {
            return 1;
        }

        return 0;
    }

    /**
     * Gibt ein String-Object zurück, welches die komplexe Zahl repräsentiert.
     *
     * @return String
     */
    @Override
    public String toString() {
        return (String.format(Locale.ENGLISH, "%27.16E", re_) + "\t+i *" + String.format(Locale.ENGLISH, "%27.16E", im_));
    }

}
