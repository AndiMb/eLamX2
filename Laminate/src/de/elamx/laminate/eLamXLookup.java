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
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Andreas Hauffe
 */
public class eLamXLookup extends AbstractLookup implements PropertyChangeListener {

    public final static String PROP_FILEOBJECT = "fileObject";
    private static eLamXLookup lookup = new eLamXLookup();
    private final InstanceContent content;
    private boolean notifyDataObject = false;
    private boolean changable = true;

    private eLamXLookup() {
        this(new InstanceContent());
    }

    private eLamXLookup(InstanceContent ic) {
        super(ic);
        this.content = ic;
    }

    public boolean isChangable() {
        return changable;
    }

    public void setChangable(boolean changable) {
        this.changable = changable;
    }

    public void clear() {
        if (!changable){
            return;
        }
        for (ELamXObject o : lookupAll(ELamXObject.class)) {
            remove(o);
        }
    }

    public void add(ELamXObject instance) {
        if (!changable || fileObject == null) {
            return;
        }
        instance.addPropertyChangeListener(this);
        content.add(instance);
        if (notifyDataObject && !dataOb.isModified()) {
            dataOb.setModified(true);
        }
    }

    public void remove(ELamXObject instance) {
        if (!changable || fileObject == null) {
            return;
        }
        instance.delete();
        instance.removePropertyChangeListener(this);
        content.remove(instance);
        if (notifyDataObject && !dataOb.isModified()) {
            dataOb.setModified(true);
        }
    }

    public static eLamXLookup getDefault() {
        return lookup;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChanged(evt);
        if (notifyDataObject && !dataOb.isModified()) {
            dataOb.setModified(true);
        }
    }
    private final ArrayList<PropertyChangeListener> listeners = new ArrayList<>();

    public void addPropertyChangeListener(PropertyChangeListener l) {
        listeners.add(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        listeners.remove(l);
    }

    protected final void firePropertyChanged(PropertyChangeEvent e) {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(e);
        }
    }

    private FileObject fileObject;
    private DataObject dataOb;
    
    public void setFileObject(FileObject fo) {
        if (fo != null && fo != fileObject) {
            try {
                notifyDataObject = false;
                if (dataOb != null) {
                    dataOb.setValid(false);
                }
                dataOb = null;
                //fileObject = null;
                clear();
                fileObject = fo;
                dataOb = DataObject.find(fo);
                firePropertyChanged(new PropertyChangeEvent(this, PROP_FILEOBJECT, null, fileObject));
                notifyDataObject = true;
            } catch (DataObjectNotFoundException | PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public DataObject getDataObject() {
        return dataOb;
    }
    
    public void setDataObject(DataObject dataOb){
        notifyDataObject = false;
        this.dataOb = dataOb;
        this.fileObject = this.dataOb.getPrimaryFile();
        firePropertyChanged(new PropertyChangeEvent(this, PROP_FILEOBJECT, null, fileObject));
        notifyDataObject = true;
    }
    
    public void setModified(boolean value){
        if (dataOb != null){
            dataOb.setModified(value);
        }
    }
}
