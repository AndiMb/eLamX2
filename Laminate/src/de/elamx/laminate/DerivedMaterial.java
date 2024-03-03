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

/**
 *
 * @author Andreas Hauffe
 */
public class DerivedMaterial extends LayerMaterial {
    
    private static final int UPDATE_PRIORITY = 100;

    private boolean useOwnEPar;

    public static final String PROP_USEOWNEPAR = "useOwnEPar";
    
    private final Material parentMaterial;
    private final DefaultMaterial baseMaterial;
    
    public DerivedMaterial(String uid, String name, Material parentMaterial, boolean addToLookup) {
        super(uid, name, addToLookup);
        this.parentMaterial = parentMaterial;
        this.baseMaterial = new DefaultMaterial("", "", Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, false);
    }

    public boolean isUseOwnEPar() {
        return useOwnEPar;
    }

    public void setUseOwnEPar(boolean useOwnEPar) {
        boolean oldUseOwnEPar = this.useOwnEPar;
        this.useOwnEPar = useOwnEPar;
        firePropertyChange(PROP_USEOWNEPAR, oldUseOwnEPar, useOwnEPar);
    }

    @Override
    public double getRShear() {
        return parentMaterial.getRShear();
    }

    @Override
    public double getRNorCom() {
        return parentMaterial.getRNorCom();
    }

    @Override
    public double getRNorTen() {
        return parentMaterial.getRNorTen();
    }

    @Override
    public double getRParTen() {
        return parentMaterial.getRParTen();
    }

    @Override
    public double getBetaNor() {
        return parentMaterial.getBetaNor();
    }

    @Override
    public double getBetaPar() {
        return parentMaterial.getBetaPar();
    }

    @Override
    public double getAlphaTNor() {
        return parentMaterial.getAlphaTNor();
    }

    @Override
    public double getAlphaTPar() {
        return parentMaterial.getAlphaTPar();
    }

    @Override
    public double getRho() {
        return parentMaterial.getRho();
    }

    @Override
    public double getG23() {
        return parentMaterial.getG23();
    }

    @Override
    public double getG13() {
        return parentMaterial.getG13();
    }

    @Override
    public double getG() {
        return parentMaterial.getG();
    }

    @Override
    public double getNue12() {
        return parentMaterial.getNue12();
    }

    @Override
    public double getEnor() {
        return parentMaterial.getEnor();
    }

    @Override
    public double getEpar() {
        return useOwnEPar ? baseMaterial.getEpar() : parentMaterial.getEpar();
    }
    
    public void setEpar(double Epar){
        baseMaterial.setEpar(Epar);
    }

    @Override
    public double getRParCom() {
        return parentMaterial.getRParCom();
    }

    @Override
    public boolean isEqual(Material material) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Material getCopy() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getUpdatePriority() {
        return UPDATE_PRIORITY;
    }
}
