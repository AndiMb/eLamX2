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
package de.elamx.laminate.failure;

/**
 * Diese Klasse stellt den Rückgabewert der Versagenskriterien dar. Es dient zum
 * Speichern der Ergebnisse.
 * 
 * @author Andreas Hauffe
 */
public class ReserveFactor {
    
    public static final int UNDAMAGED = 0;
    public static final int FIBER_FAILURE = 1;
    public static final int MATRIX_FAILURE = 2;
    public static final int GENERAL_MATERIAL_FAILURE = 4;
    
    private String failureName = "";
    private double minimalReserveFactor = 0.0;
    private int failureType = UNDAMAGED;

    /**
     * Legt eines neues, leeres ReserveFactor-Objekt an.
     */
    public ReserveFactor(){
    }

    /**
     * Liegt ein neues ReserveFactor-Objekt an, dass mit Daten initialisiert wird.
     * @param name Name des Versagenstyps
     * @param factor Reservefaktor
     */
    public ReserveFactor(String name, double factor){
        failureName = name;
        minimalReserveFactor = factor;
    }

    /**
     * Mit dieser Methode kann der Name des Versagenstyps gesetzt werden.
     * @param name Name des Versagenstyps.
     */
    public void   setFailureName(String name){failureName = name;}
    /**
     * Liefert den Namen des Versagenstyps zurück.
     * @return Name des Versagenstyps.
     */
    public String getFailureName(){return failureName;}

    /**
     * Mit dieser Methode kann der Reservefaktor gesetzt werden.
     * @param factor Reservefaktor
     */
    public void   setMinimalReserveFactor(double factor){minimalReserveFactor = factor;}
    /**
     * Liefert den Reservefaktor zurück.
     * @return Reservefaktor
     */
    public double getMinimalReserveFactor(){return minimalReserveFactor;}
    
    /**
     * Mit dieser Methode kann der Versagenstyps gesetzt werden.
     * @param failureType Name des Versagenstyps.
     */
    public void   setFailureType(int failureType){this.failureType = failureType;}
    /**
     * Liefert den Versagenstyp zurück.
     * @return Versagenstyp.
     */
    public int getFailureType(){return failureType;}
}
