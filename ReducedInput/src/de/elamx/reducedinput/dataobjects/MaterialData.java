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
package de.elamx.reducedinput.dataobjects;

import de.elamx.laminate.failure.Criterion;

/**
 *
 * @author Florian Dexl
 */
public class MaterialData extends ReducedInputDataObject {

    private Double thickness = null;
    private Criterion Criterion = null;

    private Double Epar = null;
    private Double Enor = null;
    private Double nue12 = null;
    private Double G = null;
    private Double G13 = null;
    private Double G23 = null;
    private Double rho = null;

    private Double RParTen = null;
    private Double RParCom = null;
    private Double RNorTen = null;
    private Double RNorCom = null;
    private Double RShear = null;

    private Double FMC_muesp = null;
    private Double FMC_m = null;

    private Double Puck_a0 = null;
    private Double Puck_pspz = null;
    private Double Puck_lambda_min = null;
    private Double Puck_pspd = null;

    private Double TsaiWu_f12star = null;

    public MaterialData(String name) {
        super(name);
    }

    public Double getThickness() {
        return thickness;
    }

    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    public Double getEpar() {
        return Epar;
    }

    public void setEpar(Double Epar) {
        this.Epar = Epar;
    }

    public Double getEnor() {
        return Enor;
    }

    public void setEnor(Double Enor) {
        this.Enor = Enor;
    }

    public Double getNue12() {
        return nue12;
    }

    public void setNue12(Double nue12) {
        this.nue12 = nue12;
    }

    public Double getG() {
        return G;
    }

    public void setG(Double G) {
        this.G = G;
    }

    public Double getRho() {
        return rho;
    }

    public void setRho(Double rho) {
        this.rho = rho;
    }

    public Double getRParTen() {
        return RParTen;
    }

    public void setRParTen(Double RParTen) {
        this.RParTen = RParTen;
    }

    public Double getRParCom() {
        return RParCom;
    }

    public void setRParCom(Double RParCom) {
        this.RParCom = RParCom;
    }

    public Double getRNorTen() {
        return RNorTen;
    }

    public void setRNorTen(Double RNorTen) {
        this.RNorTen = RNorTen;
    }

    public Double getRNorCom() {
        return RNorCom;
    }

    public void setRNorCom(Double RNorCom) {
        this.RNorCom = RNorCom;
    }

    public Double getRShear() {
        return RShear;
    }

    public void setRShear(Double RShear) {
        this.RShear = RShear;
    }

    public Criterion getCriterion() {
        return Criterion;
    }

    public void setCriterion(Criterion Criterion) {
        this.Criterion = Criterion;
    }

    public Double getG13() {
        return G13;
    }

    public void setG13(Double G13) {
        this.G13 = G13;
    }

    public Double getG23() {
        return G23;
    }

    public void setG23(Double G23) {
        this.G23 = G23;
    }

    public Double getFMC_muesp() {
        return FMC_muesp;
    }

    public void setFMC_muesp(Double FMC_muesp) {
        this.FMC_muesp = FMC_muesp;
    }

    public Double getFMC_m() {
        return FMC_m;
    }

    public void setFMC_m(Double FMC_m) {
        this.FMC_m = FMC_m;
    }

    public Double getPuck_a0() {
        return Puck_a0;
    }

    public void setPuck_a0(Double Puck_a0) {
        this.Puck_a0 = Puck_a0;
    }

    public Double getPuck_pspz() {
        return Puck_pspz;
    }

    public void setPuck_pspz(Double Puck_pspz) {
        this.Puck_pspz = Puck_pspz;
    }

    public Double getPuck_lambda_min() {
        return Puck_lambda_min;
    }

    public void setPuck_lambda_min(Double Puck_lambda_min) {
        this.Puck_lambda_min = Puck_lambda_min;
    }

    public Double getPuck_pspd() {
        return Puck_pspd;
    }

    public void setPuck_pspd(Double Puck_pspd) {
        this.Puck_pspd = Puck_pspd;
    }

    public Double getTsaiWu_f12star() {
        return TsaiWu_f12star;
    }

    public void setTsaiWu_f12star(Double TsaiWu_f12star) {
        this.TsaiWu_f12star = TsaiWu_f12star;
    }

}
