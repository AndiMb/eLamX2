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
package de.elamx.materialdb;

import de.elamx.filesupport.DefaultMaterialLoadSaveImpl;
import de.elamx.laminate.DefaultMaterial;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Andreas Hauffe
 */
public class MaterialDataBase {

    public static ExtendedDefaultMaterial[] getMaterials() {
        ExtendedDefaultMaterial[] materials = new ExtendedDefaultMaterial[33];
        materials[0] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[0].setEpar(230000.0);
        materials[0].setEnor(6600.0);
        materials[0].setNue12(0.25);
        materials[0].setG(4800.0);
        materials[0].setName("C-'(HM)' | EP-'-'");
        materials[0].setAlphaTPar(-7.0E-7);
        materials[0].setAlphaTNor(2.8E-5);
        materials[0].setBetaPar(0.0);
        materials[0].setBetaNor(0.0);
        materials[0].setRho(1.63E-9);
        materials[0].setRParTen(1100.0);
        materials[0].setRParCom(620.0);
        materials[0].setRNorTen(21.0);
        materials[0].setRNorCom(170.0);
        materials[0].setRShear(65.0);
        materials[0].setPhi(70.0);
        materials[0].setFibreType("C");
        materials[0].setFibreName("(HM)");
        materials[0].setMatrixType("EP");
        materials[0].setMatrixName("-");
        materials[0].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[1] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[1].setEpar(138000.0);
        materials[1].setEnor(8960.0);
        materials[1].setNue12(0.3);
        materials[1].setG(7100.0);
        materials[1].setName("C-'AS' | EP-'3501'");
        materials[1].setAlphaTPar(0.0);
        materials[1].setAlphaTNor(0.0);
        materials[1].setBetaPar(0.0);
        materials[1].setBetaNor(0.0);
        materials[1].setRho(1.6000000000000003E-9);
        materials[1].setRParTen(1447.0);
        materials[1].setRParCom(1447.0);
        materials[1].setRNorTen(51.7);
        materials[1].setRNorCom(206.0);
        materials[1].setRShear(93.0);
        materials[1].setPhi(63.0);
        materials[1].setFibreType("C");
        materials[1].setFibreName("AS");
        materials[1].setMatrixType("EP");
        materials[1].setMatrixName("3501");
        materials[1].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[2] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[2].setEpar(142000.0);
        materials[2].setEnor(10300.0);
        materials[2].setNue12(0.27);
        materials[2].setG(7200.0);
        materials[2].setName("C-'AS4' | EP-'3501-6'");
        materials[2].setAlphaTPar(-9.0E-7);
        materials[2].setAlphaTNor(2.7E-5);
        materials[2].setBetaPar(0.01);
        materials[2].setBetaNor(0.2);
        materials[2].setRho(1.5800000000000001E-9);
        materials[2].setRParTen(2280.0);
        materials[2].setRParCom(1440.0);
        materials[2].setRNorTen(57.0);
        materials[2].setRNorCom(228.0);
        materials[2].setRShear(71.0);
        materials[2].setPhi(63.0);
        materials[2].setFibreType("C");
        materials[2].setFibreName("AS4");
        materials[2].setMatrixType("EP");
        materials[2].setMatrixName("3501-6");
        materials[2].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[3] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[3].setEpar(138000.0);
        materials[3].setEnor(8960.0);
        materials[3].setNue12(0.3);
        materials[3].setG(7100.0);
        materials[3].setName("C-'AS4' | EP-'-'");
        materials[3].setAlphaTPar(-3.0E-7);
        materials[3].setAlphaTNor(2.81E-5);
        materials[3].setBetaPar(0.0);
        materials[3].setBetaNor(0.4);
        materials[3].setRho(1.6000000000000003E-9);
        materials[3].setRParTen(1447.0);
        materials[3].setRParCom(1447.0);
        materials[3].setRNorTen(51.7);
        materials[3].setRNorCom(206.0);
        materials[3].setRShear(93.0);
        materials[3].setPhi(66.0);
        materials[3].setFibreType("C");
        materials[3].setFibreName("AS4");
        materials[3].setMatrixType("EP");
        materials[3].setMatrixName("-");
        materials[3].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[4] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[4].setEpar(294000.0);
        materials[4].setEnor(6400.0);
        materials[4].setNue12(0.23);
        materials[4].setG(4900.0);
        materials[4].setName("C-'GY70' | EP-'-'");
        materials[4].setAlphaTPar(-1.0E-7);
        materials[4].setAlphaTNor(2.6E-5);
        materials[4].setBetaPar(0.0);
        materials[4].setBetaNor(0.3);
        materials[4].setRho(1.59E-9);
        materials[4].setRParTen(589.0);
        materials[4].setRParCom(491.0);
        materials[4].setRNorTen(29.4);
        materials[4].setRNorCom(98.1);
        materials[4].setRShear(49.1);
        materials[4].setPhi(57.0);
        materials[4].setFibreType("C");
        materials[4].setFibreName("GY70");
        materials[4].setMatrixType("EP");
        materials[4].setMatrixName("-");
        materials[4].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[5] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[5].setEpar(138000.0);
        materials[5].setEnor(8500.0);
        materials[5].setNue12(0.29);
        materials[5].setG(4500.0);
        materials[5].setName("C-'HTS' | EP-'RTM6'");
        materials[5].setAlphaTPar(0.0);
        materials[5].setAlphaTNor(0.0);
        materials[5].setBetaPar(0.0);
        materials[5].setBetaNor(0.0);
        materials[5].setRho(1.5120000000000002E-9);
        materials[5].setRParTen(1600.0);
        materials[5].setRParCom(800.0);
        materials[5].setRNorTen(25.0);
        materials[5].setRNorCom(110.0);
        materials[5].setRShear(45.0);
        materials[5].setPhi(55.0);
        materials[5].setFibreType("C");
        materials[5].setFibreName("HTS");
        materials[5].setMatrixType("EP");
        materials[5].setMatrixName("RTM6");
        materials[5].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[6] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[6].setEpar(177000.0);
        materials[6].setEnor(10800.0);
        materials[6].setNue12(0.27);
        materials[6].setG(7600.0);
        materials[6].setName("C-'IM6' | EP-'SC1081'");
        materials[6].setAlphaTPar(-3.0E-7);
        materials[6].setAlphaTNor(2.9999999999999997E-5);
        materials[6].setBetaPar(0.0);
        materials[6].setBetaNor(0.2);
        materials[6].setRho(1.6000000000000003E-9);
        materials[6].setRParTen(2860.0);
        materials[6].setRParCom(1875.0);
        materials[6].setRNorTen(49.0);
        materials[6].setRNorCom(246.0);
        materials[6].setRShear(83.0);
        materials[6].setPhi(65.0);
        materials[6].setFibreType("C");
        materials[6].setFibreName("IM6");
        materials[6].setMatrixType("EP");
        materials[6].setMatrixName("SC1081");
        materials[6].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[7] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[7].setEpar(203000.0);
        materials[7].setEnor(11200.0);
        materials[7].setNue12(0.32);
        materials[7].setG(8400.0);
        materials[7].setName("C-'IM6' | EP-'Generic'");
        materials[7].setAlphaTPar(0.0);
        materials[7].setAlphaTNor(0.0);
        materials[7].setBetaPar(0.0);
        materials[7].setBetaNor(0.0);
        materials[7].setRho(1.6000000000000003E-9);
        materials[7].setRParTen(3500.0);
        materials[7].setRParCom(1540.0);
        materials[7].setRNorTen(56.0);
        materials[7].setRNorCom(150.0);
        materials[7].setRShear(98.0);
        materials[7].setPhi(66.0);
        materials[7].setFibreType("C");
        materials[7].setFibreName("IM6");
        materials[7].setMatrixType("EP");
        materials[7].setMatrixName("Generic");
        materials[7].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[8] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[8].setEpar(188000.0);
        materials[8].setEnor(7100.0);
        materials[8].setNue12(0.1);
        materials[8].setG(6200.0);
        materials[8].setName("C-'Modmor-L' | EP-'ERLA4617'");
        materials[8].setAlphaTPar(0.0);
        materials[8].setAlphaTNor(3.3299999999999996E-5);
        materials[8].setBetaPar(0.0);
        materials[8].setBetaNor(0.0);
        materials[8].setRho(1.5500000000000002E-9);
        materials[8].setRParTen(836.0);
        materials[8].setRParCom(877.0);
        materials[8].setRNorTen(41.6);
        materials[8].setRNorCom(195.0);
        materials[8].setRShear(61.0);
        materials[8].setPhi(45.0);
        materials[8].setFibreType("C");
        materials[8].setFibreName("Modmor-L");
        materials[8].setMatrixType("EP");
        materials[8].setMatrixName("ERLA4617");
        materials[8].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[9] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[9].setEpar(155000.0);
        materials[9].setEnor(7310.0);
        materials[9].setNue12(0.345);
        materials[9].setG(4190.0);
        materials[9].setName("C-'MR50' | EP-'LTM25'");
        materials[9].setAlphaTPar(-4.2999999999999996E-7);
        materials[9].setAlphaTNor(3.7399999999999994E-5);
        materials[9].setBetaPar(0.0);
        materials[9].setBetaNor(0.0);
        materials[9].setRho(1.5200000000000001E-9);
        materials[9].setRParTen(2020.0);
        materials[9].setRParCom(1138.0);
        materials[9].setRNorTen(20.5);
        materials[9].setRNorCom(145.0);
        materials[9].setRShear(88.9);
        materials[9].setPhi(60.9);
        materials[9].setFibreType("C");
        materials[9].setFibreName("MR50");
        materials[9].setMatrixType("EP");
        materials[9].setMatrixName("LTM25");
        materials[9].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[10] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[10].setEpar(128500.0);
        materials[10].setEnor(8070.0);
        materials[10].setNue12(0.321);
        materials[10].setG(3790.0);
        materials[10].setName("C-'NAS-S 12K' | EP-'NCT 321'");
        materials[10].setAlphaTPar(0.0);
        materials[10].setAlphaTNor(0.0);
        materials[10].setBetaPar(0.0);
        materials[10].setBetaNor(0.0);
        materials[10].setRho(1.49E-9);
        materials[10].setRParTen(39.0);
        materials[10].setRParCom(21.0);
        materials[10].setRNorTen(0.9);
        materials[10].setRNorCom(4.0);
        materials[10].setRShear(2.9);
        materials[10].setPhi(64.4);
        materials[10].setFibreType("C");
        materials[10].setFibreName("NAS-S 12K");
        materials[10].setMatrixType("EP");
        materials[10].setMatrixName("NCT 321");
        materials[10].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[11] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[11].setEpar(125000.0);
        materials[11].setEnor(8000.0);
        materials[11].setNue12(0.3);
        materials[11].setG(5000.0);
        materials[11].setName("C-'T300' | EP-'-'");
        materials[11].setAlphaTPar(-6.0E-7);
        materials[11].setAlphaTNor(3.9999999999999996E-5);
        materials[11].setBetaPar(0.0);
        materials[11].setBetaNor(0.0);
        materials[11].setRho(1.5800000000000001E-9);
        materials[11].setRParTen(1450.0);
        materials[11].setRParCom(1400.0);
        materials[11].setRNorTen(55.0);
        materials[11].setRNorCom(170.0);
        materials[11].setRShear(90.0);
        materials[11].setPhi(60.0);
        materials[11].setFibreType("C");
        materials[11].setFibreName("T300");
        materials[11].setMatrixType("EP");
        materials[11].setMatrixName("-");
        materials[11].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[12] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[12].setEpar(180000.0);
        materials[12].setEnor(10000.0);
        materials[12].setNue12(0.28);
        materials[12].setG(6900.0);
        materials[12].setName("C-'T300' | EP-'5208'");
        materials[12].setAlphaTPar(2.0E-8);
        materials[12].setAlphaTNor(2.2499999999999998E-5);
        materials[12].setBetaPar(0.0);
        materials[12].setBetaNor(0.0);
        materials[12].setRho(1.6060000000000002E-9);
        materials[12].setRParTen(1494.0);
        materials[12].setRParCom(1693.0);
        materials[12].setRNorTen(40.1);
        materials[12].setRNorCom(245.0);
        materials[12].setRShear(67.2);
        materials[12].setPhi(70.0);
        materials[12].setFibreType("C");
        materials[12].setFibreName("T300");
        materials[12].setMatrixType("EP");
        materials[12].setMatrixName("5208");
        materials[12].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[13] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[13].setEpar(148000.0);
        materials[13].setEnor(9650.0);
        materials[13].setNue12(0.3);
        materials[13].setG(4550.0);
        materials[13].setName("C-'T300' | EP-'-'");
        materials[13].setAlphaTPar(0.0);
        materials[13].setAlphaTNor(0.0);
        materials[13].setBetaPar(0.0);
        materials[13].setBetaNor(0.0);
        materials[13].setRho(1.5000000000000002E-9);
        materials[13].setRParTen(1314.0);
        materials[13].setRParCom(1220.0);
        materials[13].setRNorTen(43.0);
        materials[13].setRNorCom(168.0);
        materials[13].setRShear(48.0);
        materials[13].setPhi(60.0);
        materials[13].setFibreType("C");
        materials[13].setFibreName("T300");
        materials[13].setMatrixType("EP");
        materials[13].setMatrixName("-");
        materials[13].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[14] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[14].setEpar(181000.0);
        materials[14].setEnor(10300.0);
        materials[14].setNue12(0.28);
        materials[14].setG(7170.0);
        materials[14].setName("C-'T300' | EP-'-'");
        materials[14].setAlphaTPar(2.0E-8);
        materials[14].setAlphaTNor(2.2499999999999998E-5);
        materials[14].setBetaPar(0.0);
        materials[14].setBetaNor(0.6);
        materials[14].setRho(1.5000000000000002E-9);
        materials[14].setRParTen(1314.0);
        materials[14].setRParCom(1220.0);
        materials[14].setRNorTen(43.0);
        materials[14].setRNorCom(168.0);
        materials[14].setRShear(48.0);
        materials[14].setPhi(70.0);
        materials[14].setFibreType("C");
        materials[14].setFibreName("T300");
        materials[14].setMatrixType("EP");
        materials[14].setMatrixName("-");
        materials[14].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[15] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[15].setEpar(109000.0);
        materials[15].setEnor(7700.0);
        materials[15].setNue12(0.28);
        materials[15].setG(4500.0);
        materials[15].setName("C-'T600SC' | EP-'-'");
        materials[15].setAlphaTPar(0.0);
        materials[15].setAlphaTNor(0.0);
        materials[15].setBetaPar(0.0);
        materials[15].setBetaNor(0.0);
        materials[15].setRho(1.5100000000000002E-9);
        materials[15].setRParTen(2128.0);
        materials[15].setRParCom(1160.0);
        materials[15].setRNorTen(27.0);
        materials[15].setRNorCom(200.0);
        materials[15].setRShear(52.0);
        materials[15].setPhi(51.0);
        materials[15].setFibreType("C");
        materials[15].setFibreName("T600SC");
        materials[15].setMatrixType("EP");
        materials[15].setMatrixName("-");
        materials[15].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[16] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[16].setEpar(129000.0);
        materials[16].setEnor(7380.0);
        materials[16].setNue12(0.319);
        materials[16].setG(4480.0);
        materials[16].setName("C-'T700 24K' | EP-'E765'");
        materials[16].setAlphaTPar(0.0);
        materials[16].setAlphaTNor(0.0);
        materials[16].setBetaPar(0.0);
        materials[16].setBetaNor(0.0);
        materials[16].setRho(1.5600000000000002E-9);
        materials[16].setRParTen(2553.0);
        materials[16].setRParCom(1239.0);
        materials[16].setRNorTen(42.0);
        materials[16].setRNorCom(199.0);
        materials[16].setRShear(138.0);
        materials[16].setPhi(57.0);
        materials[16].setFibreType("C");
        materials[16].setFibreName("T700 24K");
        materials[16].setMatrixType("EP");
        materials[16].setMatrixName("E765");
        materials[16].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[17] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[17].setEpar(123000.0);
        materials[17].setEnor(8300.0);
        materials[17].setNue12(0.3);
        materials[17].setG(4800.0);
        materials[17].setName("C-'T700' | EP-'-'");
        materials[17].setAlphaTPar(0.0);
        materials[17].setAlphaTNor(0.0);
        materials[17].setBetaPar(0.0);
        materials[17].setBetaNor(0.0);
        materials[17].setRho(1.5500000000000002E-9);
        materials[17].setRParTen(1400.0);
        materials[17].setRParCom(850.0);
        materials[17].setRNorTen(18.0);
        materials[17].setRNorCom(96.0);
        materials[17].setRShear(16.0);
        materials[17].setPhi(57.0);
        materials[17].setFibreType("C");
        materials[17].setFibreName("T700");
        materials[17].setMatrixType("EP");
        materials[17].setMatrixName("-");
        materials[17].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[18] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[18].setEpar(78000.0);
        materials[18].setEnor(78000.0);
        materials[18].setNue12(0.07);
        materials[18].setG(6500.0);
        materials[18].setName("C-'AS4' | EP-'AGP370-5H/3501-6S'");
        materials[18].setAlphaTPar(0.0);
        materials[18].setAlphaTNor(0.0);
        materials[18].setBetaPar(0.0);
        materials[18].setBetaNor(0.0);
        materials[18].setRho(1.6000000000000003E-9);
        materials[18].setRParTen(963.0);
        materials[18].setRParCom(873.0);
        materials[18].setRNorTen(838.0);
        materials[18].setRNorCom(873.0);
        materials[18].setRShear(70.0);
        materials[18].setPhi(60.0);
        materials[18].setFibreType("C");
        materials[18].setFibreName("AS4");
        materials[18].setMatrixType("EP");
        materials[18].setMatrixName("AGP370-5H/3501-6S");
        materials[18].setType(ExtendedDefaultMaterial.TYPE_FABRIC);
        materials[19] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[19].setEpar(53600.0);
        materials[19].setEnor(55200.0);
        materials[19].setNue12(0.042);
        materials[19].setG(2850.0);
        materials[19].setName("C-'CFS003' | EP-'LTM25'");
        materials[19].setAlphaTPar(3.83E-6);
        materials[19].setAlphaTNor(3.7999999999999996E-6);
        materials[19].setBetaPar(0.0);
        materials[19].setBetaNor(0.0);
        materials[19].setRho(1.45E-9);
        materials[19].setRParTen(618.0);
        materials[19].setRParCom(642.0);
        materials[19].setRNorTen(652.0);
        materials[19].setRNorCom(556.0);
        materials[19].setRShear(84.1);
        materials[19].setPhi(46.9);
        materials[19].setFibreType("C");
        materials[19].setFibreName("CFS003");
        materials[19].setMatrixType("EP");
        materials[19].setMatrixName("LTM25");
        materials[19].setType(ExtendedDefaultMaterial.TYPE_FABRIC);
        materials[20] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[20].setEpar(134000.0);
        materials[20].setEnor(8900.0);
        materials[20].setNue12(0.28);
        materials[20].setG(5100.0);
        materials[20].setName("C-'AS4' | PEEK-'APC2'");
        materials[20].setAlphaTPar(0.0);
        materials[20].setAlphaTNor(0.0);
        materials[20].setBetaPar(0.0);
        materials[20].setBetaNor(0.0);
        materials[20].setRho(1.6000000000000003E-9);
        materials[20].setRParTen(2130.0);
        materials[20].setRParCom(1100.0);
        materials[20].setRNorTen(80.0);
        materials[20].setRNorCom(200.0);
        materials[20].setRShear(160.0);
        materials[20].setPhi(66.0);
        materials[20].setFibreType("C");
        materials[20].setFibreName("AS4");
        materials[20].setMatrixType("PEEK");
        materials[20].setMatrixName("APC2");
        materials[20].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[21] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[21].setEpar(131000.0);
        materials[21].setEnor(8700.0);
        materials[21].setNue12(0.28);
        materials[21].setG(5000.0);
        materials[21].setName("C-'AS4' | PEEK-'APC2'");
        materials[21].setAlphaTPar(-2.0E-7);
        materials[21].setAlphaTNor(2.4E-5);
        materials[21].setBetaPar(0.0);
        materials[21].setBetaNor(0.3);
        materials[21].setRho(1.5700000000000002E-9);
        materials[21].setRParTen(2060.0);
        materials[21].setRParCom(1080.0);
        materials[21].setRNorTen(78.0);
        materials[21].setRNorCom(196.0);
        materials[21].setRShear(157.0);
        materials[21].setPhi(58.0);
        materials[21].setFibreType("C");
        materials[21].setFibreName("AS4");
        materials[21].setMatrixType("PEEK");
        materials[21].setMatrixName("APC2");
        materials[21].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[22] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[22].setEpar(216000.0);
        materials[22].setEnor(5000.0);
        materials[22].setNue12(0.25);
        materials[22].setG(4500.0);
        materials[22].setName("C-'Mod I' | PI-'WRD9371'");
        materials[22].setAlphaTPar(0.0);
        materials[22].setAlphaTNor(2.53E-5);
        materials[22].setBetaPar(0.0);
        materials[22].setBetaNor(0.2);
        materials[22].setRho(1.54E-9);
        materials[22].setRParTen(807.0);
        materials[22].setRParCom(655.0);
        materials[22].setRNorTen(15.0);
        materials[22].setRNorCom(71.0);
        materials[22].setRShear(22.0);
        materials[22].setPhi(45.0);
        materials[22].setFibreType("C");
        materials[22].setFibreName("Mod I");
        materials[22].setMatrixType("PI");
        materials[22].setMatrixName("WRD9371");
        materials[22].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[23] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[23].setEpar(215000.0);
        materials[23].setEnor(4900.0);
        materials[23].setNue12(0.25);
        materials[23].setG(4500.0);
        materials[23].setName("C-'Modmor-I' | PI-'WRD9371'");
        materials[23].setAlphaTPar(0.0);
        materials[23].setAlphaTNor(2.5399999999999997E-5);
        materials[23].setBetaPar(0.0);
        materials[23].setBetaNor(0.0);
        materials[23].setRho(1.5500000000000002E-9);
        materials[23].setRParTen(802.0);
        materials[23].setRParCom(648.0);
        materials[23].setRNorTen(14.7);
        materials[23].setRNorCom(69.9);
        materials[23].setRShear(21.6);
        materials[23].setPhi(45.0);
        materials[23].setFibreType("C");
        materials[23].setFibreName("Modmor-I");
        materials[23].setMatrixType("PI");
        materials[23].setMatrixName("WRD9371");
        materials[23].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[24] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[24].setEpar(60000.0);
        materials[24].setEnor(25000.0);
        materials[24].setNue12(0.23);
        materials[24].setG(12000.0);
        materials[24].setName("G-'Scotchply' | EP-'1009-26-5901'");
        materials[24].setAlphaTPar(3.7999999999999996E-6);
        materials[24].setAlphaTNor(1.67E-5);
        materials[24].setBetaPar(0.0);
        materials[24].setBetaNor(0.0);
        materials[24].setRho(2.132E-9);
        materials[24].setRParTen(1282.0);
        materials[24].setRParCom(816.0);
        materials[24].setRNorTen(45.7);
        materials[24].setRNorCom(173.0);
        materials[24].setRShear(44.6);
        materials[24].setPhi(72.0);
        materials[24].setFibreType("G");
        materials[24].setFibreName("Scotchply");
        materials[24].setMatrixType("EP");
        materials[24].setMatrixName("1009-26-5901");
        materials[24].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[25] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[25].setEpar(39000.0);
        materials[25].setEnor(8600.0);
        materials[25].setNue12(0.28);
        materials[25].setG(3800.0);
        materials[25].setName("E-G-'E-Glas' | EP-'-'");
        materials[25].setAlphaTPar(7.0E-6);
        materials[25].setAlphaTNor(2.1E-5);
        materials[25].setBetaPar(0.0);
        materials[25].setBetaNor(0.2);
        materials[25].setRho(2.1E-9);
        materials[25].setRParTen(1080.0);
        materials[25].setRParCom(620.0);
        materials[25].setRNorTen(39.0);
        materials[25].setRNorCom(128.0);
        materials[25].setRShear(89.0);
        materials[25].setPhi(55.0);
        materials[25].setFibreType("E-G");
        materials[25].setFibreName("E-Glas");
        materials[25].setMatrixType("EP");
        materials[25].setMatrixName("-");
        materials[25].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[26] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[26].setEpar(43000.0);
        materials[26].setEnor(8900.0);
        materials[26].setNue12(0.27);
        materials[26].setG(4500.0);
        materials[26].setName("S-G-'S-Glas' | EP-'-'");
        materials[26].setAlphaTPar(4.9999999999999996E-6);
        materials[26].setAlphaTNor(2.6E-5);
        materials[26].setBetaPar(0.0);
        materials[26].setBetaNor(0.2);
        materials[26].setRho(2.0E-9);
        materials[26].setRParTen(1280.0);
        materials[26].setRParCom(690.0);
        materials[26].setRNorTen(49.0);
        materials[26].setRNorCom(158.0);
        materials[26].setRShear(69.0);
        materials[26].setPhi(50.0);
        materials[26].setFibreType("S-G");
        materials[26].setFibreName("S-Glas");
        materials[26].setMatrixType("EP");
        materials[26].setMatrixName("-");
        materials[26].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[27] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[27].setEpar(29700.0);
        materials[27].setEnor(29700.0);
        materials[27].setNue12(0.17);
        materials[27].setG(5300.0);
        materials[27].setName("E-G-'E-Glas DE75' | EP-'-'");
        materials[27].setAlphaTPar(9.999999999999999E-6);
        materials[27].setAlphaTNor(9.999999999999999E-6);
        materials[27].setBetaPar(0.06);
        materials[27].setBetaNor(0.06);
        materials[27].setRho(2.2000000000000003E-9);
        materials[27].setRParTen(367.0);
        materials[27].setRParCom(549.0);
        materials[27].setRNorTen(367.0);
        materials[27].setRNorCom(549.0);
        materials[27].setRShear(97.1);
        materials[27].setPhi(45.0);
        materials[27].setFibreType("E-G");
        materials[27].setFibreName("E-Glas DE75");
        materials[27].setMatrixType("EP");
        materials[27].setMatrixName("-");
        materials[27].setType(ExtendedDefaultMaterial.TYPE_FABRIC);
        materials[28] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[28].setEpar(4408.0);
        materials[28].setEnor(4408.0);
        materials[28].setNue12(0.412);
        materials[28].setG(1879.0);
        materials[28].setName("E-G-'E-Glas' | PP-'-'");
        materials[28].setAlphaTPar(3.6015E-5);
        materials[28].setAlphaTNor(3.6015E-5);
        materials[28].setBetaPar(0.0);
        materials[28].setBetaNor(0.0);
        materials[28].setRho(1.121365E-9);
        materials[28].setRParTen(68.476);
        materials[28].setRParCom(98.346);
        materials[28].setRNorTen(68.48);
        materials[28].setRNorCom(98.346);
        materials[28].setRShear(51.3);
        materials[28].setPhi(12.81);
        materials[28].setFibreType("E-G");
        materials[28].setFibreName("E-Glas");
        materials[28].setMatrixType("PP");
        materials[28].setMatrixName("-");
        materials[28].setType(ExtendedDefaultMaterial.TYPE_UNKNOWN);
        materials[29] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[29].setEpar(76000.0);
        materials[29].setEnor(5500.0);
        materials[29].setNue12(0.34);
        materials[29].setG(2300.0);
        materials[29].setName("A-'Kevlar' | EP-'-'");
        materials[29].setAlphaTPar(0.0);
        materials[29].setAlphaTNor(0.0);
        materials[29].setBetaPar(0.0);
        materials[29].setBetaNor(0.0);
        materials[29].setRho(1.4600000000000001E-9);
        materials[29].setRParTen(1400.0);
        materials[29].setRParCom(235.0);
        materials[29].setRNorTen(12.0);
        materials[29].setRNorCom(53.0);
        materials[29].setRShear(34.0);
        materials[29].setPhi(60.0);
        materials[29].setFibreType("A");
        materials[29].setFibreName("Kevlar");
        materials[29].setMatrixType("EP");
        materials[29].setMatrixName("-");
        materials[29].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[30] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[30].setEpar(84000.0);
        materials[30].setEnor(4800.0);
        materials[30].setNue12(0.32);
        materials[30].setG(2800.0);
        materials[30].setName("A-'Kevlar-49' | EP-'CE-3305'");
        materials[30].setAlphaTPar(-2.8999999999999998E-6);
        materials[30].setAlphaTNor(5.629999999999999E-5);
        materials[30].setBetaPar(0.0);
        materials[30].setBetaNor(0.0);
        materials[30].setRho(1.357E-9);
        materials[30].setRParTen(1179.0);
        materials[30].setRParCom(288.0);
        materials[30].setRNorTen(11.0);
        materials[30].setRNorCom(64.4);
        materials[30].setRShear(27.4);
        materials[30].setPhi(54.0);
        materials[30].setFibreType("A");
        materials[30].setFibreName("Kevlar-49");
        materials[30].setMatrixType("EP");
        materials[30].setMatrixName("CE-3305");
        materials[30].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[31] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[31].setEpar(87000.0);
        materials[31].setEnor(5500.0);
        materials[31].setNue12(0.34);
        materials[31].setG(5500.0);
        materials[31].setName("A-'Kevlar 149' | EP-'Generic'");
        materials[31].setAlphaTPar(-2.0E-6);
        materials[31].setAlphaTNor(5.9999999999999995E-5);
        materials[31].setBetaPar(0.0);
        materials[31].setBetaNor(0.0);
        materials[31].setRho(1.38E-9);
        materials[31].setRParTen(1280.0);
        materials[31].setRParCom(335.0);
        materials[31].setRNorTen(30.0);
        materials[31].setRNorCom(158.0);
        materials[31].setRShear(49.0);
        materials[31].setPhi(60.0);
        materials[31].setFibreType("A");
        materials[31].setFibreName("Kevlar 149");
        materials[31].setMatrixType("EP");
        materials[31].setMatrixName("Generic");
        materials[31].setType(ExtendedDefaultMaterial.TYPE_UD);
        materials[32] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
        materials[32].setEpar(204000.0);
        materials[32].setEnor(18500.0);
        materials[32].setNue12(0.23);
        materials[32].setG(5590.0);
        materials[32].setName("B-'B(4)' | EP-'5505'");
        materials[32].setAlphaTPar(0.0);
        materials[32].setAlphaTNor(0.0);
        materials[32].setBetaPar(0.0);
        materials[32].setBetaNor(0.0);
        materials[32].setRho(2.0E-9);
        materials[32].setRParTen(1260.0);
        materials[32].setRParCom(2500.0);
        materials[32].setRNorTen(61.0);
        materials[32].setRNorCom(202.0);
        materials[32].setRShear(67.0);
        materials[32].setPhi(50.0);
        materials[32].setFibreType("B");
        materials[32].setFibreName("B(4)");
        materials[32].setMatrixType("EP");
        materials[32].setMatrixName("5505");
        materials[32].setType(ExtendedDefaultMaterial.TYPE_UD);
        return materials;
    }

    private static final int ACTELAMXFILEVERSION = 1;

    public static ExtendedDefaultMaterial[] getMaterialsFromFile(File file) {

        ExtendedDefaultMaterial[] edMaterials = null;

        if (file != null && file.exists()) {
            FileObject fo = FileUtil.toFileObject(file);
            try {
                Document doc;
                //Use the NetBeans org.openide.xml.XMLUtil class to create an org.w3c.dom.Document:
                try (InputStream is = fo.getInputStream()) {
                    //Use the NetBeans org.openide.xml.XMLUtil class to create an org.w3c.dom.Document:
                    doc = XMLUtil.parse(new InputSource(is), false, false, null, null);
                }

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                //Find the car node:
                org.w3c.dom.Node n1Node = doc.getElementsByTagName("elamx").item(0);
                String versionString = ((Element) n1Node).getAttribute("version");
                int version = 1;
                if (!versionString.isEmpty()) {
                    version = Integer.parseInt(versionString);
                }
                if (version <= ACTELAMXFILEVERSION) {
                    if (n1Node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element eLamXElement = (Element) n1Node;
                        ArrayList<DefaultMaterial> tempMatList = DefaultMaterialLoadSaveImpl.readDefaultMaterials(eLamXElement, false);

                        HashMap<String, ExtendedDefaultMaterial> matMap = new HashMap<>(tempMatList.size());
                        edMaterials = new ExtendedDefaultMaterial[tempMatList.size()];

                        int index = 0;
                        for (DefaultMaterial m : tempMatList) {
                            edMaterials[index] = (ExtendedDefaultMaterial) m.copyValues(new ExtendedDefaultMaterial(m.getUUID(), "", 0.0, 0.0, 0.0, 0.0, 0.0, false));
                            matMap.put(edMaterials[index].getUUID(), edMaterials[index]);
                            index++;
                        }

                        org.w3c.dom.Node nNode = eLamXElement.getElementsByTagName("materials").item(0);
                        if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element materials = (Element) nNode;
                            NodeList list = materials.getElementsByTagName("material");
                            if (list.getLength() > 0) {
                                for (int ii = 0; ii < list.getLength(); ii++) {
                                    org.w3c.dom.Node materialNode = list.item(ii);
                                    if (materialNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                        Element materialElem = (Element) materialNode;
                                        String uuid = materialElem.getAttribute("uuid");
                                        String classname = materialElem.getAttribute("class");
                                        if (classname.equals(DefaultMaterial.class.getName())) {
                                            String fibreName = DefaultMaterialLoadSaveImpl.getTagValue("fibreName", materialElem);
                                            String fibreType = DefaultMaterialLoadSaveImpl.getTagValue("fibreType", materialElem);
                                            String matrixName = DefaultMaterialLoadSaveImpl.getTagValue("matrixName", materialElem);
                                            String matrixType = DefaultMaterialLoadSaveImpl.getTagValue("matrixType", materialElem);
                                            String phi = DefaultMaterialLoadSaveImpl.getTagValue("phi", materialElem);
                                            String type = DefaultMaterialLoadSaveImpl.getTagValue("type", materialElem);
                                            
                                            if (fibreName != null){
                                                matMap.get(uuid).setFibreName(fibreName);
                                            }else{
                                                matMap.get(uuid).setFibreName("");
                                            }
                                            if (fibreType != null){
                                                matMap.get(uuid).setFibreType(fibreType);
                                            }else{
                                                matMap.get(uuid).setFibreType("");
                                            }
                                            if (matrixName != null){
                                                matMap.get(uuid).setMatrixName(matrixName);
                                            }else{
                                                matMap.get(uuid).setMatrixName("");
                                            }
                                            if (matrixType != null){
                                                matMap.get(uuid).setMatrixType(matrixType);
                                            }else{
                                                matMap.get(uuid).setMatrixType("");
                                            }
                                            if (phi != null){
                                                matMap.get(uuid).setPhi(Double.parseDouble(phi));
                                            }else{
                                                matMap.get(uuid).setPhi(0.0);
                                            }
                                            if (type != null){
                                                matMap.get(uuid).setType(Integer.parseInt(type));
                                            }else{
                                                matMap.get(uuid).setType(0);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            } catch (IOException | SAXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        if (edMaterials == null) {
            return new ExtendedDefaultMaterial[0];
        }

        return edMaterials;
    }

}
