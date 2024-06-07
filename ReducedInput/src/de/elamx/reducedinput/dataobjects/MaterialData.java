/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

    private Double FMCmuesp = null;

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

    public Double getFMCmuesp() {
        return FMCmuesp;
    }

    public void setFMCmuesp(Double FMCmuesp) {
        this.FMCmuesp = FMCmuesp;
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

}
