/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.elamx.reducedinput.dataobjects;

/**
 *
 * @author Florian Dexl
 */
public class CalculationData extends ReducedInputDataObject {

    private String loadcase;

    public CalculationData(String name) {
        super(name);
    }

    public String getLoadcase() {
        return loadcase;
    }

    public void setLoadcase(String loadcase) {
        this.loadcase = loadcase;
    }
}
