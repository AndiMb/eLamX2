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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Diese Objekt enthält alle Daten, die ein Objekt braucht, das innerhalb von
 * eLamX definiert wird. Zudem stellt es den PropertyChangeSupport bereit.
 * 
 * @author Andreas Hauffe
 */
public abstract class ELamXObject {
    
    private final String uuid;                  // Universal Unique ID, um das Object auch nach dem Speicher in Dateien eindeutig zuordnen zu können
    
    private       String name;                  // Name des Objekts
    public static final String PROP_NAME = "name";
    
    private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    private final DynamicLookup lookup = new DynamicLookup();
    
    private boolean changed = false;

    public ELamXObject(String uuid, String name, boolean addToLookup) {
        this.uuid = uuid;
        this.name = name;
        if (addToLookup){
            eLamXLookup.getDefault().add(this);
        }
    }
    
    public abstract int getUpdatePriority();
    
    public void setChanged(boolean changed){
        boolean oldChanged = this.changed;
        this.changed = changed;
        if (this.changed && !oldChanged) {
            ELamXObjectUpdateObserver.getActual().addELamXObject(this);
        }
    }
    
    public boolean hasChanged(){
        return changed;
    }

    public DynamicLookup getLookup() {
        return lookup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        firePropertyChange(PROP_NAME, oldName, this.name);
    }

    public String getUUID() {
        return uuid;
    }
    
    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        if (ELamXObjectUpdateObserver.getActual() == null){
            ELamXObjectUpdateObserver.setActual(new ELamXObjectUpdateObserver(this));
        }
        setChanged(true);
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        if (ELamXObjectUpdateObserver.getActual().getSource() == this){
            ELamXObjectUpdateObserver.getActual().informAllELamXObjects();
        }
    }
            
    public void firePropertyChange(String propertyName, int oldValue, int newValue){
        if (ELamXObjectUpdateObserver.getActual() == null){
            ELamXObjectUpdateObserver.setActual(new ELamXObjectUpdateObserver(this));
        }
        setChanged(true);
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        if (ELamXObjectUpdateObserver.getActual().getSource() == this){
            ELamXObjectUpdateObserver.getActual().informAllELamXObjects();
        }
    }
    
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue){
        if (ELamXObjectUpdateObserver.getActual() == null){
            ELamXObjectUpdateObserver.setActual(new ELamXObjectUpdateObserver(this));
        }
        setChanged(true);
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        if (ELamXObjectUpdateObserver.getActual().getSource() == this){
            ELamXObjectUpdateObserver.getActual().informAllELamXObjects();
            changed = false;
        }
    }
    
    /*
     * Diese Methode informiert alle im Lookup des eLamX-Objekts vorhandenen
     * DependingObjects, also Objekte, deren Daten von denen des eLmaX-Objekts
     * abhängen.
     */
    public void informDependingObjects(){
        for (DependingObject d : lookup.lookupAll(DependingObject.class)){
            d.update();
        }
    }
    
    public void update(){
        informDependingObjects();
        changed = false;
    }
    
    public void delete(){
        lookup.clear();
    }
}
