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
package de.elamx.clt.springin.additionalmodels;

import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.springin.SpringInInput;
import de.elamx.clt.springin.SpringInModel;
import de.elamx.clt.springin.SpringInResult;
import de.elamx.core.propertyeditor.HygrothermCoeffPropertyEditorSupport;
import java.beans.PropertyEditorSupport;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service = SpringInModel.class)
public class EnhancedRadfordSpringInModel extends SpringInModel {

    public static final String PROP_EPS_CR = "eps_cr";
    public static final String PROP_EPS_CU = "eps_cu";

    private static Property[] props;

    private double eps_cr;
    private double eps_cu;

    public EnhancedRadfordSpringInModel() {
        this(NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "EnhancedRadfordSpringInModel.description"), 0.0, 0.0);
    }

    public EnhancedRadfordSpringInModel(String name, double eps_cr, double eps_cu) {
        super(name);
        this.eps_cr = eps_cr;
        this.eps_cu = eps_cu;
        if (props == null) {
            initProperties();
        }
    }

    public void setEps_cr(double eps_cr) {
        double oldEps_cr = this.eps_cr;
        this.eps_cr = eps_cr;
        propertyChangeSupport.firePropertyChange(PROP_EPS_CR, oldEps_cr, eps_cr);
    }

    public double getEps_cr() {
        return eps_cr;
    }

    public void setEps_cu(double eps_cu) {
        double oldEps_cu = this.eps_cu;
        this.eps_cu = eps_cu;
        propertyChangeSupport.firePropertyChange(PROP_EPS_CU, oldEps_cu, eps_cu);
    }

    public double getEps_cu() {
        return eps_cu;
    }

    private void initProperties() {
        props = new Property[3];

        props[0] = new Property(PROP_NAME, String.class,
                NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "EnhancedRadfordSpringInModel.name"),
                NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "EnhancedRadfordSpringInModel.name.shortDescription"),
                PropertyEditorSupport.class);
        props[1] = new Property(PROP_EPS_CR, double.class,
                NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "EnhancedRadfordSpringInModel.eps_cr"),
                NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "EnhancedRadfordSpringInModel.eps_cr.shortDescription"),
                HygrothermCoeffPropertyEditorSupport.class);
        props[2] = new Property(PROP_EPS_CU, double.class,
                NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "EnhancedRadfordSpringInModel.eps_cu"),
                NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "EnhancedRadfordSpringInModel.eps_cu.shortDescription"),
                HygrothermCoeffPropertyEditorSupport.class);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "EnhancedRadfordSpringInModel.displayname");
    }

    @Override
    public Property[] getPropertyDefinitions() {
        return props;
    }

    @Override
    public SpringInResult getResult(CLT_Laminate laminate, SpringInInput input) {
        double alphaT_cirum = input.isZeroDegAsCircumDir() ? laminate.getAlphaGlobal()[0] : laminate.getAlphaGlobal()[1];

        double deltaT = input.getBaseTemp() - input.getHardeningTemp();

        double temp = (alphaT_cirum - input.getAlphat_thick()) * deltaT / (1.0 + input.getAlphat_thick() * deltaT);
        temp += (eps_cu - eps_cr) / (1.0 + eps_cr);
        temp *= Math.toRadians(input.getAngle());

        return new SpringInResult(laminate, input, Math.toDegrees(temp));
    }

    @Override
    public SpringInModel getCopy() {
        return new EnhancedRadfordSpringInModel(getName(), eps_cr, eps_cu);
    }

    @Override
    public String checkInput(CLT_Laminate laminat) {
        if (! laminat.isSymmetric()){
            return NbBundle.getMessage(EnhancedRadfordSpringInModel.class, "Warning.unsymmetriclaminate");
        }
        return null;
    }

}
