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
package de.elamx.laminate.addFailureCriteriaMetal;

import de.elamx.laminate.Material;
import de.elamx.laminate.failure.Criterion;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public abstract class IsotropCriterion extends Criterion implements PropertyChangeListener{
    
    private HashMap<Material, Boolean> checkedMats = new HashMap<>();

    public IsotropCriterion(FileObject obj) {
        super(obj);
    }
    
    protected boolean checkMaterial(Material material) {
        
        Boolean checked = checkedMats.get(material);
        
        if (checked == null){        
            double eps = 0.01;

            boolean isIsotrop = true;

            if (material.getRParTen() != material.getRParCom()) {
                isIsotrop = false;
            }
            if (material.getRNorTen() != material.getRNorCom()) {
                isIsotrop = false;
            }
            if (material.getRParTen() != material.getRNorTen()) {
                isIsotrop = false;
            }
            if (material.getEpar() != material.getEnor()){
                isIsotrop = false;
            }
            if (material.getNue12() != material.getNue21()){
                isIsotrop = false;
            }
            double ShearStiff = material.getEpar() / (2.0 * (1.0 + material.getNue12()));
            if (material.getG() > (1.0+eps)*ShearStiff || material.getG() < (1.0-eps)*ShearStiff){
                isIsotrop = false;
            }

            if (!isIsotrop) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(Tresca.class, "Warning.nonisotropMaterial", material.getName()), NotifyDescriptor.ERROR_MESSAGE));
                checked = false;
            }else{
                checked = true;
            }
            checkedMats.put(material, checked);
            material.addPropertyChangeListener(this);
        }
        return checked;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Material){
            checkedMats.remove((Material)evt.getSource());
        }
    }
}
