/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview.nodefactories;

import de.elamx.fileview.nodes.LaminateNode;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.eLamXLookup;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Andreas Hauffe
 */
public class LaminatesNodeFactory extends ChildFactory<Laminat> implements LookupListener{

    private Lookup.Result<Laminat> result = null;
    
    public LaminatesNodeFactory() {
        result = eLamXLookup.getDefault().lookupResult(Laminat.class);
        result.addLookupListener(this);
    }
    
    @Override
    protected boolean createKeys(List<Laminat> list) {
        list.addAll(result.allInstances());
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(list, new Comparator<Laminat>(){
            @Override
            public int compare(Laminat o1, Laminat o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return true;
    }

    @Override
    protected Node createNodeForKey(Laminat key) {
        return new LaminateNode(key);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        this.refresh(true);
    }
    
}
