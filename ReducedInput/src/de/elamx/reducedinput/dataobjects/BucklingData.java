/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.elamx.reducedinput.dataobjects;

/**
 *
 * @author Florian Dexl
 */
public class BucklingData extends ReducedInputDataObject {

    private String loadcase = null;

    private Double length;
    private Double width;
    private Integer bcx;
    private Integer bcy;
    private Integer m;
    private Integer n;
    private Boolean wholeD;
    private Boolean dTilde;

    public BucklingData(String name) {
        super(name);
    }

    public String getLoadcase() {
        return loadcase;
    }

    public void setLoadcase(String loadcase) {
        this.loadcase = loadcase;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Integer getBcx() {
        return bcx;
    }

    public void setBcx(Integer bcx) {
        this.bcx = bcx;
    }

    public Integer getBcy() {
        return bcy;
    }

    public void setBcy(Integer bcy) {
        this.bcy = bcy;
    }

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Boolean getWholeD() {
        return wholeD;
    }

    public void setWholeD(Boolean wholeD) {
        this.wholeD = wholeD;
    }

    public Boolean getdTilde() {
        return dTilde;
    }

    public void setdTilde(Boolean dTilde) {
        this.dTilde = dTilde;
    }
}