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
package de.elamx.export.Ansys;

import de.elamx.export.Export;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.Material;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Andreas Hauffe
 */
public class AnsysExport extends Export<AnsysExportOptions>{

    public AnsysExport(Laminat laminat) {
        super(laminat, new AnsysExportOptions());
    }

    @Override
    public String exportLaminate() {

        String exportString = "";

        switch (getOptions().getExportType()) {
            case AnsysExportOptions.TYPE_REAL:
                exportString += exportAsReal();
                break;
            case AnsysExportOptions.TYPE_SECTION:
                exportString += exportAsSection();
                break;
        }

        return exportString;
    }

    private String exportAsReal() {

        String exportString = "";
        
        ArrayList<Layer> layers = getAllLayers();
        Layer layer;

        exportString += "R," + 1 + "," + layers.size() + "\n";
        for (int ii = 0; ii < layers.size(); ii++) {
            layer = layers.get(ii);
            exportString += "RMODIF," + 1 + "," + (13 + 3 * ii) + "," + materialNums.get(layer.getMaterial()) + "," + layer.getAngle() + "," + layer.getThickness() + "\n";
        }
        if (getOptions().getOffset() != AnsysExportOptions.OFFSET_MID) {
            exportString += "! Offset must be defined via element KEYOPTS" + "\n";
        }

        return exportString;
    }

    private String exportAsSection() {

        String exportString = "";
        
        ArrayList<Layer> layers = getAllLayers();
        Layer layer;

        exportString += "SECTYPE, " + 1 + " , SHELL" + "\n";
        for (int ii = 0; ii < layers.size(); ii++) {
            layer = layers.get(ii);
            exportString += "SECDATA, " + layer.getThickness() + " , " + materialNums.get(layer.getMaterial()) + ", " + layer.getAngle() + "\n";
        }
        
        if (getOptions().getOffset() != AnsysExportOptions.OFFSET_MID) {
            String offset;
            switch(getOptions().getOffset()){
                case AnsysExportOptions.OFFSET_TOP:
                    offset = "TOP"; break;
                case AnsysExportOptions.OFFSET_BOT:
                    offset = "BOT"; break;
                default:
                    offset = "MID";
            }
            exportString += "SECOFFSET, " + offset + "\n";
        }

        return exportString;
    }
    
    private final HashMap<Material, String> materialNums = new HashMap<>();

    @Override
    public String exportMaterials() {
        String exportString = "";

        List<Material> materials = getMaterialsList();

        int matNum = 0;
        materialNums.clear();

        for (Material m : materials) {

            matNum++;
            materialNums.put(m, "" + matNum);

            if (!getOptions().isHygrothermal()) {
                if (m.getEpar() == m.getEnor() && Math.abs(1.0 - m.getEpar() / (2.0 * (1.0 + m.getNue12()) * m.getG())) <= 0.01) {
                    exportString += "MP, EX," + matNum + "," + m.getEpar() + "\n";
                    exportString += "MP, PRXY," + matNum + "," + m.getNue12() + "\n";
                } else {
                    exportString += "MP, EX," + matNum + "," + m.getEpar() + "\n";
                    exportString += "MP, EY," + matNum + "," + m.getEnor() + "\n";
                    exportString += "MP, PRXY," + matNum + "," + m.getNue12() + "\n";
                    exportString += "MP, GXY," + matNum + "," + m.getG() + "\n";
                    if (m.getG13() != 0.0){
                        exportString += "MP, GXZ," + matNum + "," + m.getG13() + "\n";
                    }
                    if (m.getG23() != 0.0){
                        exportString += "MP, GYZ," + matNum + "," + m.getG23() + "\n";
                    }
                }
            } else {
                if (m.getEpar() == m.getEnor()
                        && Math.abs(1.0 - m.getEpar() / (2.0 * (1.0 + m.getNue12()) * m.getG())) <= 0.01
                        && m.getAlphaTPar() == m.getAlphaTNor()
                        && m.getBetaPar() == m.getBetaNor()) {
                    exportString += "MP, EX," + matNum + "," + m.getEpar() + "\n";
                    exportString += "MP, PRXY," + matNum + "," + m.getNue12() + "\n";
                    exportString += "MP, BTEX," + matNum + "," + m.getBetaPar() + "\n";
                    exportString += "MP, CTEX," + matNum + "," + m.getAlphaTPar() + "\n";
                } else {
                    exportString += "MP, EX," + matNum + "," + m.getEpar() + "\n";
                    exportString += "MP, EY," + matNum + "," + m.getEnor() + "\n";
                    exportString += "MP, PRXY," + matNum + "," + m.getNue12() + "\n";
                    exportString += "MP, GXY," + matNum + "," + m.getG() + "\n";
                    if (m.getG13() != 0.0){
                        exportString += "MP, GXZ," + matNum + "," + m.getG13() + "\n";
                    }
                    if (m.getG23() != 0.0){
                        exportString += "MP, GYZ," + matNum + "," + m.getG23() + "\n";
                    }
                    exportString += "MP, BTEX," + matNum + "," + m.getBetaPar() + "\n";
                    exportString += "MP, BTEY," + matNum + "," + m.getBetaNor() + "\n";
                    exportString += "MP, CTEX," + matNum + "," + m.getAlphaTPar() + "\n";
                    exportString += "MP, CTEY," + matNum + "," + m.getAlphaTNor() + "\n";
                }
            }

            if (!Double.isNaN(m.getRho()) && m.getRho() > 0.0) {
                exportString += "MP, DENS," + matNum + "," + m.getRho() + "\n";
            }

            if (getOptions().isStrength()) {
                exportString += "FC, " + matNum + ", S, XTEN,  " + m.getRParTen() + "\n";
                exportString += "FC, " + matNum + ", S, XCMP, -" + m.getRParCom() + "\n";
                exportString += "FC, " + matNum + ", S, YTEN,  " + m.getRNorTen() + "\n";
                exportString += "FC, " + matNum + ", S, YCMP, -" + m.getRNorCom() + "\n";
                exportString += "FC, " + matNum + ", S, XY, " + m.getRShear() + "\n";
            }
        }

        return exportString;
    }
}
