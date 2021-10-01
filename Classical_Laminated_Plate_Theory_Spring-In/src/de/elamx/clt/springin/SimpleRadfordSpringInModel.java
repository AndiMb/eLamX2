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
package de.elamx.clt.springin;

import de.elamx.clt.CLT_Laminate;
import java.beans.PropertyEditorSupport;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service=SpringInModel.class)
public class SimpleRadfordSpringInModel extends SpringInModel {
    
    public static final String PROP_ALPHAT_RAD = "alphaT_rad";
    
    private static Property[] props;

    public SimpleRadfordSpringInModel() {
        this(NbBundle.getMessage(SimpleRadfordSpringInModel.class, "SimpleRadfordSpringInModel.description"));
    }
    
    public SimpleRadfordSpringInModel(String name) {
        super(name);
        if (props == null) initProperties();
    }
    
    private void initProperties(){
        props = new Property[1];
        
        props[0] = new Property(PROP_NAME, String.class, 
                NbBundle.getMessage(SimpleRadfordSpringInModel.class, "SimpleRadfordSpringInModel.name"), 
                NbBundle.getMessage(SimpleRadfordSpringInModel.class, "SimpleRadfordSpringInModel.name.shortDescription"), 
                PropertyEditorSupport.class);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(SimpleRadfordSpringInModel.class, "SimpleRadfordSpringInModel.displayname");
    }

    @Override
    public Property[] getPropertyDefinitions() {return props;}

    @Override
    public SpringInResult getResult(CLT_Laminate laminate, SpringInInput input){
        double alphaT_cirum = input.isZeroDegAsCircumDir() ? laminate.getAlphaGlobal()[0] : laminate.getAlphaGlobal()[1];
        
        double deltaT = input.getBaseTemp() - input.getHardeningTemp();
        
        double temp = (alphaT_cirum - input.getAlphat_thick())*deltaT / (1.0 + input.getAlphat_thick() * deltaT);
        temp *= Math.toRadians(input.getAngle());
        
        return new SpringInResult(laminate, input, Math.toDegrees(temp));
    }

    @Override
    public SpringInModel getCopy() {
        return new SimpleRadfordSpringInModel(getName());
    }

    @Override
    public String checkInput(CLT_Laminate laminat) {
        if (! laminat.isSymmetric()){
            return NbBundle.getMessage(SimpleRadfordSpringInModel.class, "Warning.unsymmetriclaminate");
        }
        return null;
    }
    
}
