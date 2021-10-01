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
package de.elamx.laminate;

import java.util.ArrayList;

/**
 *
 * @author raedel
 */
public class LaminateSummary {
    
    private Laminat             laminat_ = null;
    private ArrayList<double[]> summary_ = null;
    
    /**
     * Konstruktor zur Berechnung einer Zusammenfassung eines Laminats.
     * Es werden alle vorhandenen Lagenwinkel gefunden und sortiert und die
     * entsprechenden Dicken und prozentualen Dickenanteile berechnet.
     * 
     * Das Objekt summary_ beinhaltet alle gefundenen Lagenwinkel sowie zu jedem
     * Lagenwinkel ein Array mit den Daten: Winkel, Dicke, prozentualer Anteil der
     * Dicke am Gesamtlaminat.
     * 
     * @param laminat Laminat dessen Zusammenfassung berechnet werden soll.
     */
    public LaminateSummary (Laminat laminat){
        laminat_ = laminat;
        summary_ = new ArrayList<>();
        calcSummary();
    }
    
    private void calcSummary(){
        
        int idx = -1;
        double tGes = 0.0;
        boolean f = false;
        
        for (Layer l : laminat_.getAllLayers()){
            tGes += l.getThickness();
            // bereits vorhandene durchsuchen
            if (summary_.isEmpty()) summary_.add(new double[]{l.getAngle(),l.getThickness(),0.0});
            else {
                for (int ii = 0; ii < summary_.size(); ii++){
                    if (l.getAngle() == summary_.get(ii)[0]){                   // Lagenwinkel schon vorhanden
                        summary_.get(ii)[1] += l.getThickness();
                        f = true;
                        break;
                    }
                }
                // Lagenwinkel neu
                if (f == false){
                    // Zeile zum Einsortieren raussuchen
                    for (int ii = 0; ii < summary_.size(); ii++){
                        if      (Math.abs(l.getAngle()) > Math.abs(summary_.get(ii)[0])){
                            idx = ii+1;
                            //break;
                        }
                        else if (Math.abs(l.getAngle()) < Math.abs(summary_.get(ii)[0])){
                            idx = ii;
                            break;
                        }
                        else{
                            if (l.getAngle() >= 0) idx = ii;
                            else                   idx = ii+1;
                            break;
                        }
                    }
                    // Einsortieren
                    summary_.add(idx,new double[]{l.getAngle(),l.getThickness(),0.0});
                }
            }
            // zurücksetzen
            f = false;
            idx = -1;
        }
        
        // prozentualer Anteil
        for (double[] a : summary_){a[2] = a[1]/tGes*100.0;}
        
    }
    
    public int getNumberOfRows(){return summary_ == null ? 0 : summary_.size();}
    
    public double get(int row, int col){return summary_ == null ? 0.0 : summary_.get(row)[col];}
    
    private void printSummary(){
        for (int ii = 0; ii < summary_.size(); ii++){
            System.out.println("Typ " + ii + ": " + summary_.get(ii)[0] + " " + summary_.get(ii)[1] + " " + summary_.get(ii)[2]);
        }
        System.out.println("");
    }
    
}

