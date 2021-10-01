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
package de.elamx.export.Nastran;

import de.elamx.export.Export;
import de.elamx.export.ExportOptions;
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
public class NastranExport extends Export<NastranExportOptions> {

    public NastranExport(Laminat laminat) {
        super(laminat, new NastranExportOptions());
    }

    private final HashMap<Material, String> materialNums = new HashMap<>();

    @Override
    public String exportMaterials() {
        String exportString = "";

        List<Material> materials = getMaterialsList();

        int matNum = 0;
        boolean isIso;
        materialNums.clear();

        NastranCardCreator ncc;

        for (Material m : materials) {

            matNum++;
            materialNums.put(m, "" + matNum);

            isIso = false;

            if (!getOptions().isHygrothermal()) {
                if (m.getEpar() == m.getEnor() && Math.abs(1.0 - m.getEpar() / (2.0 * (1.0 + m.getNue12()) * m.getG())) <= 0.01) {
                    ncc = new NastranCardCreator("MAT1");
                    ncc.addInt(1, 2, matNum);                                   // MID
                    ncc.addReal(1, 3, m.getEpar());                             // E
                    //ncc.addReal(1, 4, m.getShearModulus());                     // G
                    ncc.addReal(1, 5, m.getNue12());                        // NU
                    ncc.addReal(1, 6, m.getRho());
                    isIso = true;
                } else {
                    ncc = new NastranCardCreator("MAT8");
                    ncc.addInt(1, 2, matNum);                                  // MID
                    ncc.addReal(1, 3, m.getEpar());                             // E1
                    ncc.addReal(1, 4, m.getEnor());                             // E2
                    ncc.addReal(1, 5, m.getNue12());                        // NU12
                    ncc.addReal(1, 6, m.getG());                     // G12
                    if (m.getG13() != 0.0){
                        ncc.addReal(1, 7, m.getG13());
                    }
                    if (m.getG23() != 0.0){
                        ncc.addReal(1, 8, m.getG23());
                    }
                    ncc.addReal(1, 9, m.getRho());                          // RHO
                }
            } else {
                if (m.getEpar() == m.getEnor()
                        && Math.abs(1.0 - m.getEpar() / (2.0 * (1.0 + m.getNue12()) * m.getG())) <= 0.01
                        && m.getAlphaTPar() == m.getAlphaTNor()
                        && m.getBetaPar() == m.getBetaNor()) {

                    ncc = new NastranCardCreator("MAT1");
                    ncc.addInt(1, 2, matNum);                                  // MID
                    ncc.addReal(1, 3, m.getEpar());                             // E
                    //ncc.addReal(1, 4, m.getShearModulus());                     // G
                    ncc.addReal(1, 5, m.getNue12());                        // NU
                    ncc.addReal(1, 6, m.getRho());                          // RHO
                    ncc.addReal(1, 7, m.getAlphaTPar());                   // A
                    isIso = true;
                } else {
                    ncc = new NastranCardCreator("MAT8");
                    ncc.addInt(1, 2, matNum);                                  // MID
                    ncc.addReal(1, 3, m.getEpar());                             // E1
                    ncc.addReal(1, 4, m.getEnor());                             // E2
                    ncc.addReal(1, 5, m.getNue12());                        // NU12
                    ncc.addReal(1, 6, m.getG());                     // G12
                    if (m.getG13() != 0.0){
                        ncc.addReal(1, 7, m.getG13());
                    }
                    if (m.getG23() != 0.0){
                        ncc.addReal(1, 8, m.getG23());
                    }
                    ncc.addReal(1, 9, m.getRho());                          // RHO
                    ncc.addReal(2, 2, m.getAlphaTPar());                   // A1
                    ncc.addReal(2, 3, m.getAlphaTNor());                   // A2
                }

                if (getOptions().isStrength()) {
                    if (isIso) {
                        ncc.addReal(2, 2, m.getRParTen());
                        ncc.addReal(2, 3, m.getRParCom());
                        ncc.addReal(2, 4, m.getRShear());
                    } else {
                        ncc.addReal(2, 5, m.getRParTen());
                        ncc.addReal(2, 6, m.getRParCom());
                        ncc.addReal(2, 7, m.getRNorTen());
                        ncc.addReal(2, 8, m.getRNorCom());
                        ncc.addReal(2, 9, m.getRShear());
                    }
                }

            }

            ncc.setFormat(getOptions().getFormatType());

            exportString += ncc.getCard() + "\n";
        }

        return exportString;
    }

    @Override
    public String exportLaminate() {
        // Property
        NastranCardCreator ncc = new NastranCardCreator(getOptions().getFormatType(), "PCOMP");
        ncc.addInt(1, 2, 1);                                                    // PID
        if (getOptions().getOffset() == ExportOptions.OFFSET_BOT) {
            ncc.addReal(1, 3, 0.0);
        } // Z0 
        else if (getOptions().getOffset() == ExportOptions.OFFSET_TOP) {
            ncc.addReal(1, 3, -getLaminate().getThickness());
        }

        int line = 2;
        int column = 2;

        ArrayList<Layer> layers = getAllLayers();
        Layer layer;

        for (int ii = 0; ii < layers.size(); ii++) {
            layer = layers.get(ii);
            if (column == 10) {
                line++;
                column = 2;
            }
            //ncc.addInt(line, column++ , (ii+1));
            ncc.addInt(line, column++, Integer.parseInt(materialNums.get(layer.getMaterial())));
            ncc.addReal(line, column++, layer.getThickness());
            ncc.addReal(line, column++, layer.getAngle());
            column++;
        }

        return ncc.getCard() + "\n";
    }
}
