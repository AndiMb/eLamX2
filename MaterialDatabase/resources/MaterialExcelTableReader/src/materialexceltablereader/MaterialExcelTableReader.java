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
package materialexceltablereader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Andreas Hauffe
 */
public class MaterialExcelTableReader {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidFormatException {
        InputStream inp = new FileInputStream("Materialdaten.xls");

        Workbook wb = WorkbookFactory.create(inp);
        readLaminaeData(wb);
        //eadFibreData(wb);
        //readMatrixData(wb);

        inp.close();
    }
    
    private static void readLaminaeData(Workbook wb){
        
        Sheet sheet = wb.getSheet("Laminaedaten");

        System.out.println("" + sheet.getLastRowNum());

        int matCount = 0;

                
        int type = -1;
        for (int ii = 0; ii < sheet.getLastRowNum(); ii++) {
            Row row = sheet.getRow(ii);
            if (row != null) {
                
                if (row.getCell(0) != null && wb.getFontAt(row.getCell(0).getCellStyle().getFontIndex()).getFontHeightInPoints() == 12){
                    String heading = getStringValue(row.getCell(0));
                    if (heading == null){
                        continue;
                    }else{
                        type = -1;
                        
                        if (heading.equals("Kohlenstofffaser - Epoxidharz, UD")){
                            type = 0;
                        }else if (heading.equals("Kohlenstofffaser - Epoxidharz, Gewebe")){
                            type = 1;
                        }else if (heading.equals("Kohlenstofffaser - Epoxidharz, Gelege")){
                            type = 2;
                        }else if (heading.equals("Kohlenstofffaser - Thermoplastische Matrix, UD")){
                            type = 0;
                        }else if (heading.equals("Kohlenstofffaser - andere Matrixsysteme, UD")){
                            type = 0;
                        }else if (heading.equals("Glasfaser - Epoxidharz, UD")){
                            type = 0;
                        }else if (heading.equals("Glasfaser - Epoxidharz, Gewebe")){
                            type = 1;
                        }else if (heading.equals("Glasfaser - Epoxidharz, Gelege")){
                            type = 2;
                        }else if (heading.equals("Glasfaser - andere Harzsysteme")){
                            type = -1;
                        }else if (heading.equals("Aramidfaser - Epoxidharz, UD")){
                            type = 0;
                        }else if (heading.equals("Aramidfaser - Epoxidharz, Gewebe")){
                            type = 1;
                        }else if (heading.equals("Borfaser - alle Harze, UD")){
                            type = 0;
                        }else if (heading.equals("Unbekannte Faserart")){
                            type = -1;
                        }else{
                            System.err.println("Fehler: Überschrift nicht erkannt.");
                        }
                    }
                }
                
                double ExZ = getDoubleValue(row.getCell(12));
                if (Double.isNaN(ExZ)) {
                    continue;
                }

                double EyZ = getDoubleValue(row.getCell(14));
                if (Double.isNaN(EyZ)) {
                    continue;
                }

                double nuexy = getDoubleValue(row.getCell(18));
                if (Double.isNaN(nuexy)) {
                    continue;
                }

                double Gxy = getDoubleValue(row.getCell(21));
                if (Double.isNaN(Gxy)) {
                    continue;
                }

                double phi = getDoubleValue(row.getCell(11));
                if (Double.isNaN(phi)) {
                    phi = 0.0;
                }

                double RxZ = getDoubleValue(row.getCell(30));
                if (Double.isNaN(RxZ)) {
                    RxZ = 0.0;
                }

                double RxD = getDoubleValue(row.getCell(31));
                if (Double.isNaN(RxD)) {
                    RxD = 0.0;
                }

                double RyZ = getDoubleValue(row.getCell(32));
                if (Double.isNaN(RyZ)) {
                    RyZ = 0.0;
                }

                double RyD = getDoubleValue(row.getCell(33));
                if (Double.isNaN(RyD)) {
                    RyD = 0.0;
                }

                double Rxy = getDoubleValue(row.getCell(36));
                if (Double.isNaN(Rxy)) {
                    Rxy = 0.0;
                }

                double alphax = getDoubleValue(row.getCell(24));
                if (Double.isNaN(alphax)) {
                    alphax = 0.0;
                }

                double alphay = getDoubleValue(row.getCell(25));
                if (Double.isNaN(alphay)) {
                    alphay = 0.0;
                }

                double betax = getDoubleValue(row.getCell(27));
                if (Double.isNaN(betax)) {
                    betax = 0.0;
                }

                double betay = getDoubleValue(row.getCell(28));
                if (Double.isNaN(betay)) {
                    betay = 0.0;
                }

                double rho = getDoubleValue(row.getCell(47));
                if (Double.isNaN(rho)) {
                    rho = 0.0;
                }

                String fibreType = getStringValue(row.getCell(1));
                if (fibreType == null){
                    fibreType = "-";
                }else{
                    fibreType = fibreType.trim();
                }
                String fibreName = getStringValue(row.getCell(2));
                if (fibreName == null){
                    fibreName = "-";
                }else{
                    fibreName = fibreName.trim();
                }
                String matrixType = getStringValue(row.getCell(5));
                if (matrixType == null){
                    matrixType = "-";
                }else{
                    matrixType = matrixType.trim();
                }
                String matrixName = getStringValue(row.getCell(6));
                if (matrixName == null){
                    matrixName = "-";
                }else{
                    matrixName = matrixName.trim();
                }
                
                /*
                  Verwende nur Daten, die vollständig sind.
                */
                if (phi == 0 ||
                    ExZ == 0 ||
                    ExZ == 0 ||
                    nuexy == 0 ||
                    Gxy == 0 ||
                    RxZ == 0 ||
                    RxD == 0 ||
                    RyZ == 0 ||
                    RyD == 0 ||
                    Rxy == 0 ||
                    rho == 0 ||
                    fibreName.equals("-") ||
                    fibreType.equals("???") || 
                    fibreName.equals("Richtwert für Kevlarfaser-UD")){
                    continue;
                }

                System.out.println("materials[" + matCount + "] = new ExtendedDefaultMaterial(UUID.randomUUID().toString(), \"New Material\", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);");
                System.out.println("materials[" + matCount + "].setEpar(" + ExZ*1000.0 + ");");
                System.out.println("materials[" + matCount + "].setEnor(" + EyZ*1000.0 + ");");
                System.out.println("materials[" + matCount + "].setNue12(" + nuexy + ");");
                System.out.println("materials[" + matCount + "].setG(" + Gxy*1000.0 + ");");
                System.out.println("materials[" + matCount + "].setName(\"" + fibreType + "-'" + fibreName + "' | " + matrixType + "-'" + matrixName + "'\");");
                System.out.println("materials[" + matCount + "].setAlphaTPar(" + alphax*1.0E-6 + ");");
                System.out.println("materials[" + matCount + "].setAlphaTNor(" + alphay*1.0E-6 + ");");
                System.out.println("materials[" + matCount + "].setBetaPar(" + betax + ");");
                System.out.println("materials[" + matCount + "].setBetaNor(" + betay + ");");
                System.out.println("materials[" + matCount + "].setRho(" + rho*1.0E-9 + ");");
                System.out.println("materials[" + matCount + "].setRParTen(" + RxZ + ");");
                System.out.println("materials[" + matCount + "].setRParCom(" + RxD + ");");
                System.out.println("materials[" + matCount + "].setRNorTen(" + RyZ + ");");
                System.out.println("materials[" + matCount + "].setRNorCom(" + RyD + ");");
                System.out.println("materials[" + matCount + "].setRShear(" + Rxy + ");");
                System.out.println("materials[" + matCount + "].setPhi(" + phi + ");");
                System.out.println("materials[" + matCount + "].setFibreType(\"" + fibreType + "\");");
                System.out.println("materials[" + matCount + "].setFibreName(\"" + fibreName + "\");");
                System.out.println("materials[" + matCount + "].setMatrixType(\"" + matrixType + "\");");
                System.out.println("materials[" + matCount + "].setMatrixName(\"" + matrixName + "\");");
                
                String strType;
                switch(type){
                    case -1:
                        strType = "TYPE_UNKNOWN"; break;
                    case 0:
                        strType = "TYPE_UD"; break;
                    case 1:
                        strType = "TYPE_FABRIC"; break;
                    case 2:
                        strType = "TYPE_GELEGE"; break;
                    default:
                        strType = "!Mist!";
                }
                System.out.println("materials[" + matCount + "].setType(ExtendedDefaultMaterial." + strType + ");");
                
                
                matCount++;
            }
        }
    }
    
    private static void readFibreData(Workbook wb){
        
        Sheet sheet = wb.getSheet("Faserdaten");

        System.out.println("" + sheet.getLastRowNum());

        int matCount = 0;

        for (int ii = 0; ii < sheet.getLastRowNum(); ii++) {
            Row row = sheet.getRow(ii);
            if (row != null) {                
                double ExZ = getDoubleValue(row.getCell(7));
                if (Double.isNaN(ExZ)) {
                    continue;
                }

                double EyZ = getDoubleValue(row.getCell(8));
                if (Double.isNaN(EyZ)) {
                    continue;
                }

                double nuexy = getDoubleValue(row.getCell(9));
                if (Double.isNaN(nuexy)) {
                    continue;
                }

                double Gxy = getDoubleValue(row.getCell(11));
                if (Double.isNaN(Gxy)) {
                    continue;
                }

                double RxZ = getDoubleValue(row.getCell(17));
                if (Double.isNaN(RxZ)) {
                    RxZ = 0.0;
                }

                double RxD = getDoubleValue(row.getCell(18));
                if (Double.isNaN(RxD)) {
                    RxD = 0.0;
                }

                double RyZ = getDoubleValue(row.getCell(19));
                if (Double.isNaN(RyZ)) {
                    RyZ = 0.0;
                }

                double RyD = getDoubleValue(row.getCell(20));
                if (Double.isNaN(RyD)) {
                    RyD = 0.0;
                }

                double Rxy = getDoubleValue(row.getCell(21));
                if (Double.isNaN(Rxy)) {
                    Rxy = 0.0;
                }

                double alphax = getDoubleValue(row.getCell(13));
                if (Double.isNaN(alphax)) {
                    alphax = 0.0;
                }

                double alphay = getDoubleValue(row.getCell(14));
                if (Double.isNaN(alphay)) {
                    alphay = 0.0;
                }

                double betax = getDoubleValue(row.getCell(15));
                if (Double.isNaN(betax)) {
                    betax = 0.0;
                }

                double betay = getDoubleValue(row.getCell(16));
                if (Double.isNaN(betay)) {
                    betay = 0.0;
                }

                double rho = getDoubleValue(row.getCell(26));
                if (Double.isNaN(rho)) {
                    rho = 0.0;
                }

                String fibreType = getStringValue(row.getCell(1));
                if (fibreType == null){
                    fibreType = "-";
                }else{
                    fibreType = fibreType.trim();
                }
                String fibreName = getStringValue(row.getCell(2));
                if (fibreName == null){
                    fibreName = "-";
                }else{
                    fibreName = fibreName.trim();
                }
                
                /*
                  Verwende nur Daten, die vollständig sind.
                */
                if (ExZ == 0 ||
                    EyZ == 0 ||
                    nuexy == 0 ||
                    Gxy == 0 ||
                    rho == 0){
                    continue;
                }

                System.out.println("fibres[" + matCount + "] = new Fiber(UUID.randomUUID().toString(), \"New Material\", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);");
                System.out.println("fibres[" + matCount + "].setEpar(" + ExZ*1000.0 + ");");
                System.out.println("fibres[" + matCount + "].setEnor(" + EyZ*1000.0 + ");");
                System.out.println("fibres[" + matCount + "].setNue12(" + nuexy + ");");
                System.out.println("fibres[" + matCount + "].setG(" + Gxy*1000.0 + ");");
                System.out.println("fibres[" + matCount + "].setName(\"" + fibreType + "-'" + fibreName + "'\");");
                System.out.println("fibres[" + matCount + "].setAlphaTPar(" + alphax*1.0E-6 + ");");
                System.out.println("fibres[" + matCount + "].setAlphaTNor(" + alphay*1.0E-6 + ");");
                System.out.println("fibres[" + matCount + "].setBetaPar(" + betax + ");");
                System.out.println("fibres[" + matCount + "].setBetaNor(" + betay + ");");
                System.out.println("fibres[" + matCount + "].setRho(" + rho*1.0E-9 + ");");
                /*System.out.println("fibres[" + matCount + "].setRParTen(" + RxZ + ");");
                System.out.println("fibres[" + matCount + "].setRParCom(" + RxD + ");");
                System.out.println("fibres[" + matCount + "].setRNorTen(" + RyZ + ");");
                System.out.println("fibres[" + matCount + "].setRNorCom(" + RyD + ");");
                System.out.println("fibres[" + matCount + "].setRShear(" + Rxy + ");");
                System.out.println("fibres[" + matCount + "].setFibreType(\"" + fibreType + "\");");
                System.out.println("fibres[" + matCount + "].setFibreName(\"" + fibreName + "\");");*/
                
                matCount++;
            }
        }
    }
    
    private static void readMatrixData(Workbook wb){
        
        Sheet sheet = wb.getSheet("Matrixdaten");

        System.out.println("" + sheet.getLastRowNum());

        int matCount = 0;

        for (int ii = 0; ii < sheet.getLastRowNum(); ii++) {
            Row row = sheet.getRow(ii);
            if (row != null) {                
                double ExZ = getDoubleValue(row.getCell(5));
                if (Double.isNaN(ExZ)) {
                    continue;
                }

                double nuexy = getDoubleValue(row.getCell(7));
                if (Double.isNaN(nuexy)) {
                    continue;
                }

                double Gxy = getDoubleValue(row.getCell(9));
                if (Double.isNaN(Gxy)) {
                    continue;
                }

                double alphax = getDoubleValue(row.getCell(11));
                if (Double.isNaN(alphax)) {
                    alphax = 0.0;
                }

                double betax = getDoubleValue(row.getCell(13));
                if (Double.isNaN(betax)) {
                    betax = 0.0;
                }

                double rho = getDoubleValue(row.getCell(28));
                if (Double.isNaN(rho)) {
                    rho = 0.0;
                }

                String fibreType = getStringValue(row.getCell(1));
                if (fibreType == null){
                    fibreType = "-";
                }else{
                    fibreType = fibreType.trim();
                }
                String fibreName = getStringValue(row.getCell(2));
                if (fibreName == null){
                    fibreName = "-";
                }else{
                    fibreName = fibreName.trim();
                }
                
                /*
                  Verwende nur Daten, die vollständig sind.
                */
                double factor = ExZ/(2.0*(nuexy+1))/Gxy;
                if (ExZ == 0 ||
                    nuexy == 0 ||
                    Gxy == 0 ||
                    rho == 0 ||
                    factor < 0.97 ||
                    factor > 1.03){
                   continue;
                }

                System.out.println("matrices[" + matCount + "] = new Matrix(UUID.randomUUID().toString(), \"New Material\", 141000.0, 0.35, 1.7, false);");
                System.out.println("matrices[" + matCount + "].setE(" + ExZ*1000.0 + ");");
                System.out.println("matrices[" + matCount + "].setNue(" + nuexy + ");");
                System.out.println("matrices[" + matCount + "].setG(" + Gxy*1000.0 + ");");
                System.out.println("matrices[" + matCount + "].setName(\"" + fibreType + "-'" + fibreName + "'\");");
                System.out.println("matrices[" + matCount + "].setAlpha(" + alphax*1.0E-6 + ");");
                System.out.println("matrices[" + matCount + "].setBeta(" + betax + ");");
                System.out.println("matrices[" + matCount + "].setRho(" + rho*1.0E-9 + ");");
                /*System.out.println("matrices[" + matCount + "].setRParTen(" + RxZ + ");");
                System.out.println("matrices[" + matCount + "].setRParCom(" + RxD + ");");
                System.out.println("matrices[" + matCount + "].setRNorTen(" + RyZ + ");");
                System.out.println("matrices[" + matCount + "].setRNorCom(" + RyD + ");");
                System.out.println("matrices[" + matCount + "].setRShear(" + Rxy + ");");
                System.out.println("matrices[" + matCount + "].setFibreType(\"" + fibreType + "\");");
                System.out.println("matrices[" + matCount + "].setFibreName(\"" + fibreName + "\");");*/
                
                matCount++;
            }
        }
    }

    public static double getDoubleValue(Cell cell) {
        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue();
        }
        return Double.NaN;
    }

    public static String getStringValue(Cell cell) {
        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        }
        return null;
    }

}
