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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * 
 * @author Andreas Hauffe
 */
public class Laminat extends ELamXObject implements PropertyChangeListener, LookupListener{
    
    private static final int UPDATE_PRIORITY = 300;
    
    private ArrayList<DataLayer> layers = new ArrayList<>(); // Array, das alle Lagen enthält (bei Symmetrie nur eine Hälfte)
    
    // Flag, ob das Laminate symmetrisch ist
    private boolean symmetric       = false;
    public static final String PROP_SYMMETRIC = "symmetric";

    // Flag, ob die kleinste z-Koordinate bei der erste Lage liegt
    private boolean invertZ       = false;
    public static final String PROP_INVERTZ = "invertZ";
    
    // Flag, ob die symmetrie mit Mittellage oder ohne ist
    private boolean withMiddleLayer = false;
    public static final String PROP_WITHMIDDLELAYER = "withMiddleLayer";
    
    public static final String PROP_STACKING = "stackingSequence";
    
    public static final String PROP_LAYER = "layer";

    private double offset;
    public static final String PROP_OFFSET = "offset";
    
    private final Lookup.Result<Object> result;
    
    public Laminat(String uid, String name){
        this(uid, name, true);
    }
    
    public Laminat(String uid, String name, boolean addToLookup){
        super(uid, name, addToLookup);
        result = this.getLookup().lookupResult(Object.class);
        result.addLookupListener(this);
    }
    
    /**
     * Wenn true, dann keine Lagen enthalten
     * @return true, wenn keine Lagen enthalten
     */
    public boolean isEmpty(){
        return layers.isEmpty();
    }
    
    /**
     * True, wenn das Laminate symmetrisch ist
     * @return true, wenn das Laminate symmetrisch ist
     */
    public boolean isSymmetric() {
        return symmetric;
    }

    /**
     * Setzen des Symmetrieflags
     * @param symmetric true, wenn das Laminate symmetrisch ist
     */
    public void setSymmetric(boolean symmetric) {
        boolean oldSymmetric = this.symmetric;
        this.symmetric = symmetric;
        checkEmbedded();
        firePropertyChange(PROP_SYMMETRIC, oldSymmetric, this.symmetric);
    }

    /**
     * True, wenn ein symmetrisches Laminat eine Mittelschicht hat. Dieser 
     * Parameter ist unabhängig vom Symmetrieflag.
     * @return true, wen ein symmetrisches Laminat eine Mittelschicht hat
     */
    public boolean isWithMiddleLayer() {
        return withMiddleLayer;
    }

    /** 
     * Setzen des Mittellagenflags. True, wenn ein symmetrisches Laminat eine Mittelschicht hat. Dieser 
     * Parameter ist unabhängig vom Symmetrieflag.
     * @param withMiddleLayer true, wenn ein symmetrisches Laminat eine Mittelschicht hat
     */
    public void setWithMiddleLayer(boolean withMiddleLayer) {
        boolean oldWithMiddleLayer = this.withMiddleLayer;
        this.withMiddleLayer = withMiddleLayer;
        checkEmbedded();
        firePropertyChange(PROP_WITHMIDDLELAYER, oldWithMiddleLayer, this.withMiddleLayer);
    }
    
    /**
     * True, wenn ie erste Lage die kleinste z-Koordinate besitzt
     * @return true, wenn die erste Lage die kleinste z-Koordinate besitzt
     */
    public boolean isInvertZ() {
        return invertZ;
    }

    /** 
     * Setzen des z-Achsen Flags. True, die erste Lage die kleinste z-Koordinate besitzt.
     * @param invertZ true, wenn die erste Lage die kleinste z-Koordinate besitzt
     */
    public void setInvertZ(boolean invertZ) {
        boolean oldInvertZ = this.invertZ;
        this.invertZ = invertZ;
        checkEmbedded();
        firePropertyChange(PROP_INVERTZ, oldInvertZ, this.invertZ);
    }

    /**
     * Gibt den Versatz der Referenzebene zurück.
     *
     * @return Versatz der Referenzebene
     */
    public double getOffset() {
        return offset;
    }

    /**
     * Setzen des Offsets der Referenzebene des Laminats. Die Referenzebene wird
     * um den gegebenen Betrag verschoben.
     *
     * @param offset Versatz der Referenzebene
     */
    public void setOffset(double offset) {
        double oldOffset = this.offset;
        this.offset = offset;
        firePropertyChange(PROP_OFFSET, oldOffset, offset);
    }

    /**
     * Hinzufügen einer Schicht zum Laminat. Die Schicht wird ans Ende angehängt.
     * @param layer Schicht, die angehängt werden soll
     */
    public void addLayer(DataLayer layer){
        layers.add(layer);
        layer.addPropertyChangeListener(this);
        checkEmbedded();
        firePropertyChange(PROP_STACKING, null, this);
    }
    
    public void addLayer(int index, DataLayer layer){
        layers.add(index, layer);
        layer.addPropertyChangeListener(this);
        checkEmbedded();
        firePropertyChange(PROP_STACKING, null, this);
    }
    
    /**
     * Ersetzen einer Schicht. Die übergebene Schicht wird an die Stelle index
     * gesetzt. Die alte Schicht wird überschrieben.
     * @param index Index, an den die Schicht eingefügt werden soll
     * @param layer Schicht
     */
    public void setLayer(int index, DataLayer layer){
        layers.get(index).removePropertyChangeListener(this);
        layers.set(index, layer);
        layers.get(index).addPropertyChangeListener(this);
        checkEmbedded();
        firePropertyChange(PROP_STACKING, null, this);
    }
    
    /**
     * Löscht die übergebene Schicht aus dem Lagenaufbau. Alle danach vorhandenen
     * Schichten rücken nach.
     * @param layer Schicht, die gelöscht werden soll.
     */
    public void removeLayer(Layer layer){
        layer.removePropertyChangeListener(this);
        layers.remove(layer);
        checkEmbedded();
        firePropertyChange(PROP_STACKING, null, this);
    }
    
    /**
     * Löscht die übergebene Schicht aus dem Lagenaufbau. Alle danach vorhandenen
     * Schichten rücken nach.
     * @param index Index der Schicht, die gelöscht werden soll.
     */
    public void removeLayer(int index){
        layers.get(index).removePropertyChangeListener(this);
        layers.remove(index);
        checkEmbedded();
        firePropertyChange(PROP_STACKING, null, this);
    }
    
    public void removeLayers(List<DataLayer> layer){
        for (Layer l : layer){
            if (layers.remove(l)){
                l.removePropertyChangeListener(this);
            }
        }
        checkEmbedded();
        firePropertyChange(PROP_STACKING, null, this);
    }
    
    /**
     * Hinzufügen mehrere Schichten zum Laminat. Diese werden ans Ende des 
     * Schichtaufbaus angehängt.
     * @param layer 
     */
    public void addLayers(List<DataLayer> layer){
        for (DataLayer l : layer) {
            l.addPropertyChangeListener(this);
        }
        layers.addAll(layer);
        checkEmbedded();
        firePropertyChange(PROP_STACKING, null, this);
    }
    
    /**
     * Löschen aller Schichten des Laminats.
     * Dabei wird kein Event ausgelöst!!
     */
    public void clear(){
        for (Layer layer : layers) {
            layer.removePropertyChangeListener(this);
        }
        layers.clear();
    }
    
    /**
     * Gibt die Dicke des Laminats zurück. Dies erfolgt inklusive der symmetrischen
     * Schichten
     * @return Dicke des Laminats
     */
    public double getThickness(){
        double thick = 0.0;
        for (Layer layer : layers) {
            thick += layer.getThickness();
        }
        if (symmetric){
            thick *= 2.0;
            if (withMiddleLayer) {
                thick -= layers.get(layers.size()-1).getThickness();
            }
        }
        return thick;
    }
    
    /**
     * Gibt das Flächengewicht des Laminates zurück. Dies erfolgt inklusive der
     * symmetrischen Schichten
     * @return Flächengewicht des Laminats
     */
    public double getAreaWeight(){
        double areaWeight = 0.0;
        for (Layer layer : layers) {
            areaWeight += layer.getMaterial().getRho()*layer.getThickness();
        }
        if (symmetric){
            areaWeight *= 2.0;
            if (withMiddleLayer) {
                areaWeight -= layers.get(layers.size()-1).getMaterial().getRho()*layers.get(layers.size()-1).getThickness();
            }
        }
        return areaWeight;
    }
    
    /**
     * Liefert alle Schichten des Laminats in einer ArrayList zurück. Darin 
     * sind auch die symmetrischen Schichten enthalten. Zurückgegeben wird
     * eine separate ArrayList. Somit hat eine Änderung darin keinen Einfluss
     * auf den Laminataufbau des Laminates.
     * @return ArrayList mit allen Lagen.
     */
    public ArrayList<Layer> getAllLayers(){
        ArrayList<Layer> layTemp = new ArrayList<>(layers.size());
        
        layTemp.addAll(layers);
        
        if (symmetric){
            int start = layers.size()-1;
            if (withMiddleLayer) {
                start--;
            }
            for (int ii = start; ii >= 0; ii--){
                DataLayer dataLayer = layers.get(ii);
                layTemp.add(new SymmetricLayer(UUID.randomUUID().toString(), dataLayer.getName(), dataLayer));
            }
        }

        for (int ind=0; ind<layTemp.size();  ind++) {
            layTemp.get(ind).setNumber(ind + 1);
        }

        if (invertZ){
            Collections.reverse(layTemp);
        }

        return layTemp;
    }
    
    /**
     * Liefert die Schichten des Laminats in einer ArrayList zurück. Darin 
     * sind die symmetrischen Schichten NICHT enthalten. Zurückgegeben wird
     * eine Kopie der originalen Arraylist.
     * @return Kopie der ArrayList mit allen Lagen.
     */
    public ArrayList<Layer> getLayers(){
        ArrayList<Layer> layTemp = new ArrayList<>();
        layTemp.addAll(layers);

        for (int ind=0; ind<layTemp.size();  ind++) {
            layTemp.get(ind).setNumber(ind + 1);
        }

        if (invertZ){
            Collections.reverse(layTemp);
        }

        return layTemp;
    }
    
    /**
     * Liefert die Schichten des Laminats in einer ArrayList zurück. Darin 
     * sind die symmetrischen Schichten NICHT enthalten. Zurückgegeben wird
     * die originale Arraylist. Somit haben Änderungen darin DIREKTEN Einfluss
     * auf den Laminataufbau des Laminates.
     * @return Originale ArrayList mit allen Lagen.
     */
    public ArrayList<DataLayer> getOriginalLayers(){
        return layers;
    }
    
    public int getNumberofLayers(){
        int num = layers.size();
        if (symmetric) {
            num *= 2;
            if (withMiddleLayer) {
                num--;
            }
        }
        return num;
    }
    
    /**
     * Berechnung einer Zusammenfassung der prozentualen Anteile von Lagenwinkeln und -dicken eines Laminats
     * 
     * @return Zusammenfassung des Laminats
     */
    public LaminateSummary getLaminateSummary(){return new LaminateSummary(this);}
    
    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.firePropertyChange(PROP_LAYER, null, null);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        this.firePropertyChange("Lookup", null, null);
    }
    
    public Laminat getCopy(){
        return getCopy(true);
    }
    
    public Laminat getCopy(boolean addToLookup){
        Laminat lam = new Laminat(UUID.randomUUID().toString(), this.getName(), addToLookup);
        for(DataLayer l : layers){
            lam.addLayer(l.getCopy());
        }
        lam.setSymmetric(symmetric);
        lam.setWithMiddleLayer(withMiddleLayer);
        lam.setInvertZ(invertZ);
        
        return lam;
    }
    
    public Laminat getCopyWithoutListener(boolean addToLookup){
        Laminat lam = new Laminat(UUID.randomUUID().toString(), this.getName(), addToLookup);
        for(DataLayer l : layers){
            lam.addLayer(l.getCopyWithoutListeners(l.getAngle()));
        }
        lam.setSymmetric(symmetric);
        lam.setWithMiddleLayer(withMiddleLayer);
        
        return lam;
    }
    
    /*
    * Überprüft mit Rücksicht auf Symmetrie, welche Lagen eingebettet sind
    */
    private void checkEmbedded() {
        if (symmetric) {
            int i = 0;
            for (DataLayer l : layers) {
                if (i == 0) {
                    l.setEmbedded(false);
                } else {
                    l.setEmbedded(true);
                }
                i++;
            }
        } else {
            int i = 0;
            for (DataLayer l : layers) {
                if (i == 0 | i == (layers.size() - 1)) {
                    l.setEmbedded(false);
                } else{
                    l.setEmbedded(true);
                }
                i++;
            }    
        }
    }
}
