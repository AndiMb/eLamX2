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

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class Material extends ELamXObject{
    
    // Eine HashMap für zusätzliche Werte. Hauptsächlich für die Versagenskriterien!
    private static final HashMap<String, AdditionalValue> defaultAddValues = new HashMap<String, AdditionalValue>();
    private final HashMap<String, Double> additionalValues = new HashMap<>();
    
    public Material(String uid, String name, boolean addToLookup){
        super(uid, name, addToLookup);
        for (String key : defaultAddValues.keySet()){
            additionalValues.put(key, defaultAddValues.get(key).getDefaultValue());
        }
    }
    
    public static void putDefaultAdditionlValue(String name, Double defaultValue, String displayName, String htmlName, String description, Double minValue, Double maxValue){
        defaultAddValues.put(name, new AdditionalValue(defaultValue, displayName, htmlName, description, minValue, maxValue));
    }

    @Override
    public String toString(){return getName();}

    /**
     * Liefert den E-Modul in Faserrichtung E<sub>||</sub> des Materials.
     * @return E-Modul in Faserrichtung E<sub>||</sub>
     */
    public abstract double getEpar();
    
    /**
     * Liefert den E-Modul quer zur Faserrichtung E<sub>&perp;</sub> des Materials.
     * @return E-Modul quer zur Faserrichtung E<sub>&perp;</sub>
     */
    public abstract double getEnor();

    /**
     * Liefert die Querkontraktionszahl &nu;<sub>12</sub> des Materials. Dabei gilt folgende
     * Beziehung<br />
     * &nu;<sub>12</sub> * E<sub>&perp;</sub> = &nu;<sub>21</sub> * E<sub>||</sub>
     * @return die Querkontraktionszahl &nu;<sub>12</sub> des Materials
     */
    public abstract double getNue12();
    /**
     * Liefert die Querkontraktionszahl &nu;<sub>21</sub> des Materials. Dabei gilt folgende
     * Beziehung<br />
     * &nu;<sub>12</sub> * E<sub>&perp;</sub> = &nu;<sub>21</sub> * E<sub>||</sub>
     * @return Querkontraktionszahl &nu;<sub>21</sub> des Materials
     */
    public double getNue21(){return getNue12()*getEnor()/getEpar();}

    /**
     * Liefert den Schubmodul G<sub>||&perp;</sub> des Materials.
     * @return Schubmodul G<sub>||&perp;</sub> des Materials
     */
    public abstract double getG();
    
    /**
     * Liefert die transversale Schubsteifigkeit G<sub>||&perp;</sub> des Materials.
     * @return transversale Schubsteifigkeit G<sub>||&perp;</sub> des Materials
     */
    public abstract double getG13();
    
    /**
     * Liefert die transversale Schubsteifigkeit G<sub>&perp;&perp;</sub> des Materials.
     * @return transversale Schubsteifigkeit G<sub>&perp;&perp;</sub> des Materials
     */
    public abstract double getG23();
    
    /**
     * Liefert die Dichte &rho des Materials
     * @return Dichte &rho des Materials
     */
    public abstract double getRho();

    /**
     * Liefert den Wärmeausdehnungskoeffizient in Faserrichtung &alpha;<sub>||</sub>
     * @return Wärmeausdehnungskoeffizient in Faserrichtung &alpha;<sub>||</sub>
     */
    public abstract double getAlphaTPar();

    /**
     * Liefert den Wärmeausdehnungskoeffizient quer zur Faserrichtung &alpha;<sub>&perp;</sub>
     * @return Wärmeausdehnungskoeffizient quer zur Faserrichtung &alpha;<sub>&perp;</sub>
     */
    public abstract double getAlphaTNor();

    /**
     * Liefert den Quellausdehnungskoeffizient in Faserrichtung &beta;<sub>||</sub>
     * @return Quellausdehnungskoeffizient in Faserrichtung &beta;<sub>||</sub>
     */
    public abstract double getBetaPar();

    /**
     * Liefert den Quellausdehnungskoeffizient quer zur Faserrichtung &beta;<sub>&perp;</sub>
     * @return Quellausdehnungskoeffizient quer zur Faserrichtung &beta;<sub>&perp;</sub>
     */
    public abstract double getBetaNor();
    
    /**
     * Liefert die Zugfestigkeit in Faserrichtung R<sub>||,z</sub> der Schicht.
     * @return Zugfestigkeit in Faserrichtung R<sub>||,z</sub> der Schicht.
     */
    public abstract double getRParTen();

    /**
     * Liefert die Druckfestigkeit in Faserrichtung R<sub>||,d</sub> der Schicht.
     * @return Druckfestigkeit in Faserrichtung R<sub>||,d</sub> der Schicht. (ist positiv)
     */
    public abstract double getRParCom();

    /**
     * Liefert die Zugfestigkeit quer zur Faserrichtung R<sub>&perp;,z</sub> der Schicht.
     * @return Zugfestigkeit quer zur Faserrichtung R<sub>&perp;,z</sub> der Schicht. (ist positiv)
     */
    public abstract double getRNorTen();

    /**
     * Liefert die Druckfestigkeit quer zur Faserrichtung R<sub>&perp;,d</sub> der Schicht.
     * @return Druckfestigkeit quer zur Faserrichtung R<sub>&perp;,d</sub> der Schicht. (ist positiv)
     */
    public abstract double getRNorCom();

    /**
     * Liefert die Schubfestigkeit R<sub>||&perp;</sub> der Schicht.
     * @return Schubfestigkeit R<sub>||&perp;</sub> der Schicht.
     */
    public abstract double getRShear();

    /**
     * Vergleicht die Eigenschaften des übergegeben Material-Objekts mit den eigenen Werten.
     * @param material Material-Objekt mit dem verglichen werden soll.
     * @return Falls alle Eigenschaften gleich sind <CODE>true</CODE> sonst <CODE>false</CODE>
     */
    public abstract boolean isEqual(Material material);

    /**
     * Erzeugt ein Kopie des Materialobjektes. Auch die das Strengthobjekt wird
     * als Kopie hinzugefügt. Damit sind alle Daten vollkommen unabhängig von
     * den alten Daten.
     * @return Kopie des Materials
     */
    public abstract Material getCopy();
    
    public void putAdditionalValue(String name, Double value){
        Double oldValue = additionalValues.put(name, value);
        firePropertyChange(name, oldValue, value);
    }
    
    /*public void putAllAdditionalValues(Map<String,Double> values){
        additionalValues.putAll(values);
    }*/
    
    public Set<String> getAdditionalValueKeySet(){
        return additionalValues.keySet();
    }
    
    public Double getAdditionalValue(String name){
        return additionalValues.get(name);
    }
    
    public String getAdditionalValueDisplayName(String key){
        return defaultAddValues.get(key).displayName;
    }
    
    public String getAdditionalValueHtmlName(String key){
        return defaultAddValues.get(key).htmlName;
    }
    
    public String getAdditionalValueDescription(String key){
        return defaultAddValues.get(key).description;
    }
    
    public Double getAdditionalValueMinValue(String key){
        return defaultAddValues.get(key).minValue;
    }
    
    public Double getAdditionalValueMaxValue(String key){
        return defaultAddValues.get(key).maxValue;
    }
    
    public static class AdditionalValue{
        
        private final Double defaultValue;
        private final String displayName;
        private final String htmlName;
        private final String description;
        private final Double maxValue;
        private final Double minValue;

        public AdditionalValue(Double defaultValue, String displayName, String htmlName, String description, Double minValue, Double maxValue) {
            this.defaultValue = defaultValue;
            this.displayName = displayName;
            this.htmlName    = htmlName;
            this.description = description;
            this.minValue    = minValue;
            this.maxValue    = maxValue;
        }

        public Double getDefaultValue() {
            return defaultValue;
        }

        public String getDescription() {
            return description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getHtmlName() {
            return htmlName;
        }

        public Double getMaxValue() {
            return maxValue;
        }

        public Double getMinValue() {
            return minValue;
        }
    }
}
