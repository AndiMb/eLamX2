/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview.nodefactories;

import de.elamx.fileview.nodes.DerivedMaterialNode;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.DerivedMaterial;
import de.elamx.laminate.Material;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 * Diese NodeFactory erzeugt die Knoten für alle Materialien, die im
 * MaterialContainer enthalten sind. Dafür werden alle
 * <CODE>MaterialNodeProvider</CODE> ausgewertet bzw. verwendet. Dementsprechend
 * muss für einen neuen Materialtyp nur der <CODE>MaterialNodeProvider</CODE>
 * erweitert und als Service angeboten werden.
 *
 * @author Andreas Hauffe
 */
public class DerivedMaterialsNodeFactory extends ChildFactory<DerivedMaterial> implements PropertyChangeListener {

    private final Material material;

    public DerivedMaterialsNodeFactory(DefaultMaterial material) {
        this.material = material;
        material.addPropertyChangeListener(WeakListeners.propertyChange(this, material));
    }

    /**
     * Hier werden mit Hilfe aller <CODE>MaterialNodeProvider</CODE> die
     * entsprechende Keys erzeugt. Dabei wird für jedes Material ein Key
     * generiert. Eine Verwendung der <CODE>MaterialNodeProvider</CODE> an sich
     * ist nicht möglich, da die createNodesForKey Methode nur aufgerufen wird,
     * wenn der Key sich ändert. Somit würden Änderungen in der Anzahl der
     * Materialen nicht bemerkt werden.
     *
     * @param list
     * @return
     */
    @Override
    protected boolean createKeys(List<DerivedMaterial> list) {
        for (DerivedMaterial dMat : material.getDerivedMaterials()) {
            list.add(dMat);
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(list, new Comparator<DerivedMaterial>() {
            @Override
            public int compare(DerivedMaterial o1, DerivedMaterial o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return true;
    }

    @Override
    protected Node createNodeForKey(DerivedMaterial key) {
        return new DerivedMaterialNode(key);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Material.PROP_DERIVEDMATERIAL)) {
            this.refresh(true);
        }
    }
}
