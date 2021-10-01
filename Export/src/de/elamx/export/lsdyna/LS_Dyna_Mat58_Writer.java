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
package de.elamx.export.lsdyna;

import de.elamx.laminate.Laminat;
import de.elamx.laminate.Material;
import java.util.List;
import java.util.Locale;

/**
 *  
 * @author s5110955
 * @author Frank Schneider
 */
public class LS_Dyna_Mat58_Writer extends LSDynaExport{
    
    /** Creates a new instance of LS_Dyna_Mat58_Writer */
    public LS_Dyna_Mat58_Writer(Laminat laminate, double ms,double ls,double ts) {
        super(laminate, ms, ls, ts);
    }
    
    @Override
    public String exportMaterials(){
        
        String exportString = "";
        
        List<Material> materials = getMaterialsList();

        int matNum = 0;
        materialNums.clear();

        for (Material m : materials) {
            
            matNum++;
            materialNums.put(m, "" + matNum);
            
            String format1 = "%10d%10.4e%10.4e%10.4e          %10.4e";
            String format2 = "%10.4e      ----%10.4e";
            String format3 = "%10.4e%10.4e%10.4e%10.4e%10.4e";
            String format4 = "%10.4e%10.4e%10.4e";
            
            exportString += "*MAT_LAMINATED_COMPOSITE_FABRIC" + "\n";
            exportString += "$      mid        ro        EA        EB      (EC)      PRBA      TAU1    GAMMA1" + "\n";
            exportString += String.format(Locale.ENGLISH, format1, matNum,
                                                     m.getRho()*ms_/(ls_*ls_*ls_),           //hier wird in passenden einheiten umgerechnet
                                                     m.getEpar()*ms_/(ts_*ts_*ls_),
                                                     m.getEnor()*ms_/(ts_*ts_*ls_),
                                                     m.getNue21()) + "\n";
            exportString += "$      GAB       GBC       GCA    SLIMT1    SLIMC1    SLIMT2    SLIMC2     SLIMS" + "\n";
            if (m.getG13() != 0.0 && m.getG23() != 0.0){
                exportString += String.format(Locale.ENGLISH, format4, m.getG()*ms_/(ts_*ts_*ls_),
                                                         m.getG13()*ms_/(ts_*ts_*ls_),
                                                         m.getG23()*ms_/(ts_*ts_*ls_)) + "\n";
            }else{
                exportString += String.format(Locale.ENGLISH, format2, m.getG()*ms_/(ts_*ts_*ls_),
                                                         m.getG13()*ms_/(ts_*ts_*ls_)) + "\n";
            }
            exportString += "$     AOPT     TSIZE     ERODS      SOFT        FS" + "\n";
            exportString += "" + "\n";
            exportString += "$       XP        YP        ZP        A1        A2        A3" + "\n";
            exportString += "" + "\n";
            exportString += "$       V1        V2        V3        D1        D2        D3      BETA" + "\n";
            exportString += "" + "\n";
            exportString += "$     E11C      E11T      E22C      E22T       GMS" + "\n";
            exportString += "" + "\n";
            exportString += "$       XC        XT        YC        YT        SC" + "\n";
            exportString += String.format(Locale.ENGLISH, format3, m.getRParCom()*ms_*ls_/(ts_*ts_),
                                                     m.getRParTen()*ms_*ls_/(ts_*ts_),
                                                     m.getRNorCom()*ms_*ls_/(ts_*ts_),
                                                     m.getRNorTen()*ms_*ls_/(ts_*ts_),
                                                     m.getRShear() *ms_*ls_/(ts_*ts_)) + "\n";
            exportString += "$\n";
        }
        
        return exportString;
    }
}
