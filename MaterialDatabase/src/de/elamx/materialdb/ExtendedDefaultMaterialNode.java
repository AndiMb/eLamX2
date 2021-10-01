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
package de.elamx.materialdb;

import de.elamx.core.GlobalProperties;
import de.elamx.fileview.nodes.DefaultMaterialNode;
import de.elamx.laminate.Material;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class ExtendedDefaultMaterialNode extends DefaultMaterialNode {

    public ExtendedDefaultMaterialNode(ExtendedDefaultMaterial material) {
        super(material);
    }

    @Override
    public String getName() {
        return this.getLookup().lookup(Material.class).getName();
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();

        Sheet.Set matProp = Sheet.createPropertiesSet();
        matProp.setName("Materialdata");
        matProp.setDisplayName(NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.Materialdata"));

        PropertySupport.ReadOnly<String> fibreTypeProp = new PropertySupport.ReadOnly<String>("fibreType", String.class, NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.fibreType"), NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.fibreType.short")) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return ExtendedDefaultMaterialNode.this.getLookup().lookup(ExtendedDefaultMaterial.class).getFibreType();
            }
        };
        fibreTypeProp.setValue("suppressCustomEditor", Boolean.TRUE);
        matProp.put(fibreTypeProp);

        PropertySupport.ReadOnly<String> fibreNameProp = new PropertySupport.ReadOnly<String>("fibreName", String.class, NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.fibreName"), NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.fibreName.short")) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return ExtendedDefaultMaterialNode.this.getLookup().lookup(ExtendedDefaultMaterial.class).getFibreName();
            }
        };
        fibreNameProp.setValue("suppressCustomEditor", Boolean.TRUE);
        matProp.put(fibreNameProp);

        PropertySupport.ReadOnly<String> matrixTypeProp = new PropertySupport.ReadOnly<String>("matrixType", String.class, NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.matrixType"), NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.matrixType.short")) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return ExtendedDefaultMaterialNode.this.getLookup().lookup(ExtendedDefaultMaterial.class).getMatrixType();
            }
        };
        matrixTypeProp.setValue("suppressCustomEditor", Boolean.TRUE);
        matProp.put(matrixTypeProp);

        PropertySupport.ReadOnly<String> matrixNameProp = new PropertySupport.ReadOnly<String>("matrixName", String.class, NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.matrixName"), NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.matrixName.short")) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return ExtendedDefaultMaterialNode.this.getLookup().lookup(ExtendedDefaultMaterial.class).getMatrixName();
            }
        };
        matrixNameProp.setValue("suppressCustomEditor", Boolean.TRUE);
        matProp.put(matrixNameProp);
        
        PropertySupport.ReadOnly<String> phiProp = new PropertySupport.ReadOnly<String>("phi", String.class, NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.Phi"), NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.Phi.short")) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                NumberFormat nf = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_DOUBLE);
                return nf.format(ExtendedDefaultMaterialNode.this.getLookup().lookup(ExtendedDefaultMaterial.class).getPhi());
            }
        };
        phiProp.setValue("suppressCustomEditor", Boolean.TRUE);
        matProp.put(phiProp);
        
        PropertySupport.ReadOnly<String> typeProp = new PropertySupport.ReadOnly<String>("type", String.class, NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.Type"), NbBundle.getMessage(ExtendedDefaultMaterialNode.class, "ExtendedDefaultMaterialNode.Type.short")) {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                int type = ExtendedDefaultMaterialNode.this.getLookup().lookup(ExtendedDefaultMaterial.class).getType();
                String strType;
                switch(type){
                    case -1:
                        strType = "TYPE_UNKNOWN"; break;
                    case 0:
                        strType = "TYPE_UD"; break;
                    case 1:
                        strType = "TYPE_FABRIC"; break;
                    case 2:
                        strType = "TYPE_GELEGE"; break;
                    default:
                        strType = "TYPE_ERROR";
                }
                return NbBundle.getMessage(ExtendedDefaultMaterialNode.class, strType);
            }
        };
        typeProp.setValue("suppressCustomEditor", Boolean.TRUE);
        matProp.put(typeProp);
        
        sheet.put(matProp);

        Sheet old = super.createSheet(); //To change body of generated methods, choose Tools | Templates.

        for (String setName : this.getAllPropertySets()) {
            Sheet.Set set = old.get(setName);
            for (Node.Property<?> p : set.getProperties()) {
                if (p instanceof myPropRef) {
                    ((myPropRef) p).setCanWrite(false);
                }else if (p instanceof MaterialProperty) {
                    ((MaterialProperty) p).setCanWrite(false);
                }
                p.setValue("suppressCustomEditor", Boolean.TRUE);
            }
            sheet.put(set);
        }

        return sheet;
    }

    @Override
    public Action getPreferredAction() {
        return null;
    }
}
