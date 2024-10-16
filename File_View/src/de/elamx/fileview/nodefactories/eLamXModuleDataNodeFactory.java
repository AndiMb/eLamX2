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

import de.elamx.fileview.eLamXModuleDataNodeProvider;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.modules.eLamXModuleData;
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
public class eLamXModuleDataNodeFactory extends ChildFactory<eLamXModuleDataNodeFactory.eLamXModuleDataProviderProxy> implements LookupListener{

    private final Laminat laminat;
    
    private Lookup.Result<eLamXModuleData> result = null;
    
    @SuppressWarnings("this-escape")
    public eLamXModuleDataNodeFactory(Laminat laminate) {
        this.laminat = laminate;
        result = laminat.getLookup().lookupResult(eLamXModuleData.class);
        result.addLookupListener(this);
    }
    
    /**
     * Hier werden mit Hilfe aller <CODE>eLamXModuleDataNodeProvider</CODE> die entsprechende
     * Keys erzeugt. Dabei wird für jedes Material ein Key generiert. Eine Verwendung der
     * <CODE>MaterialNodeProvider</CODE> an sich ist nicht möglich, da die createNodesForKey
     * Methode nur aufgerufen wird, wenn der Key sich ändert. Somit würden Änderungen
     * in der Anzahl der Materialen nicht bemerkt werden.
     * @param list
     * @return 
     */
    @Override
    protected boolean createKeys(List<eLamXModuleDataProviderProxy> list) {
        for (eLamXModuleDataNodeProvider dataProv : Lookup.getDefault().lookupAll(eLamXModuleDataNodeProvider.class)) {
            eLamXModuleData[] datas = dataProv.geteLamXModuleData(laminat);
            for (eLamXModuleData data : datas) {
                list.add(new eLamXModuleDataProviderProxy(dataProv, data));
            }
        }
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(list, new Comparator<eLamXModuleDataProviderProxy>(){
            @Override
            public int compare(eLamXModuleDataProviderProxy o1, eLamXModuleDataProviderProxy o2) {
                return o1.moduleData.getName().compareToIgnoreCase(o2.moduleData.getName());
            }
        });
        return true;
    }

    @Override
    protected Node createNodeForKey(eLamXModuleDataProviderProxy key) {
        return key.getNode();
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        this.refresh(true);
//        eLamXLookup.getDefault().getDataObject().setModified(true);
    }
    
    /**
     * Diese Klasse wird als Key für die <CODE>Node</CODE> Generierung verwendet.
     * Darin werden die <CODE>Material</CODE>-Objekte gespeichert und die dazu
     * gehörigen <CODE>MaterialNodeProvider</CODE>. Diese können dann beim Aufruf
     * der createNodeForKey Methode den entsprechenden Knoten erzeugen. Das ganze 
     * ist vielleicht nicht ganz sauber, aber somit ist eine einfach Erweiterung
     * mit neuen Materialimplementierungen möglich.
     */
    public class eLamXModuleDataProviderProxy{
        private final eLamXModuleDataNodeProvider provider;
        private final eLamXModuleData moduleData;

        public eLamXModuleDataProviderProxy(eLamXModuleDataNodeProvider provider, eLamXModuleData moduleData) {
            this.provider = provider;
            this.moduleData = moduleData;
        }

        public Node getNode() {
            return provider.getNodes(moduleData);
        }
    }
    
}
