/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview.nodefactories;

import de.elamx.fileview.ELamXNodeProvider;
import de.elamx.laminate.eLamXLookup;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Andreas Hauffe
 */
public class eLamXNodeFactory extends ChildFactory<ELamXNodeProvider> implements PropertyChangeListener{

    public eLamXNodeFactory() {
        eLamXLookup.getDefault().addPropertyChangeListener(this);
    }

    @Override
    protected boolean createKeys(List<ELamXNodeProvider> list) {
        if (eLamXLookup.getDefault().getFileObject() == null) return true;
        for (ELamXNodeProvider eLamXNodeProvider : Lookup.getDefault().lookupAll(ELamXNodeProvider.class)){
            list.add(eLamXNodeProvider);
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(list, new Comparator<ELamXNodeProvider>(){
            @Override
            public int compare(ELamXNodeProvider o1, ELamXNodeProvider o2) {
                return Integer.compare(o1.getPosition(), o2.getPosition());
            }
        });
        return true;
    }   
    
    @Override
    protected Node createNodeForKey(ELamXNodeProvider key) {
        if (key == null){
            return null;
        }
        return key.getNode();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(eLamXLookup.PROP_FILEOBJECT)){
            this.refresh(true);
        }
    }
}
