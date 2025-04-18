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
package de.elamx.fileview.nodefactories;

import de.elamx.fileview.MaterialNodeProvider;
import de.elamx.fileview.nodefactories.MaterialsNodeFactory.MaterialProviderProxy;
import de.elamx.laminate.Material;
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
 * Diese NodeFactory erzeugt die Knoten für alle Materialien, die im 
 * MaterialContainer enthalten sind. Dafür werden alle <CODE>MaterialNodeProvider</CODE>
 * ausgewertet bzw. verwendet. Dementsprechend muss für einen neuen Materialtyp
 * nur der <CODE>MaterialNodeProvider</CODE> erweitert und als Service angeboten 
 * werden.
 * 
 * @author Andreas Hauffe
 */
public class MaterialsNodeFactory extends ChildFactory<MaterialProviderProxy> implements LookupListener{
    
    private Lookup.Result<Material> result = null;

    @SuppressWarnings("this-escape")
    public MaterialsNodeFactory() {
        result = eLamXLookup.getDefault().lookupResult(Material.class);
        result.addLookupListener(this);
    }
    
    /**
     * Hier werden mit Hilfe aller <CODE>MaterialNodeProvider</CODE> die entsprechende
     * Keys erzeugt. Dabei wird für jedes Material ein Key generiert. Eine Verwendung der
     * <CODE>MaterialNodeProvider</CODE> an sich ist nicht möglich, da die createNodesForKey
     * Methode nur aufgerufen wird, wenn der Key sich ändert. Somit würden Änderungen
     * in der Anzahl der Materialen nicht bemerkt werden.
     * @param list
     * @return 
     */
    @Override
    protected boolean createKeys(List<MaterialProviderProxy> list) {
        for (MaterialNodeProvider matProv : Lookup.getDefault().lookupAll(MaterialNodeProvider.class)) {
            Material[] mats = matProv.getMaterials();
            for (Material material : mats) {
                list.add(new MaterialProviderProxy(matProv, material));
            }
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(list, new Comparator<MaterialProviderProxy>(){
            @Override
            public int compare(MaterialProviderProxy o1, MaterialProviderProxy o2) {
                return o1.material.getName().compareToIgnoreCase(o2.material.getName());
            }
        });
        return true;
    }

    @Override
    protected Node createNodeForKey(MaterialProviderProxy key) {
        return key.getNode();
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        this.refresh(true);
    }
    
    /**
     * Diese Klasse wird als Key für die <CODE>Node</CODE> Generierung verwendet.
     * Darin werden die <CODE>Material</CODE>-Objekte gespeichert und die dazu
     * gehörigen <CODE>MaterialNodeProvider</CODE>. Diese können dann beim Aufruf
     * der createNodeForKey Methode den entsprechenden Knoten erzeugen. Das ganze 
     * ist vielleicht nicht ganz sauber, aber somit ist eine einfach Erweiterung
     * mit neuen Materialimplementierungen möglich.
     */
    public class MaterialProviderProxy{
        private final MaterialNodeProvider provider;
        private final Material material;

        public MaterialProviderProxy(MaterialNodeProvider provider, Material material) {
            this.provider = provider;
            this.material = material;
        }

        public Node getNode() {
            return provider.getNodes(material);
        }
    }
}
