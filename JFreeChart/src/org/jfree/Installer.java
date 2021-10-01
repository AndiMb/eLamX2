/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfree;

import org.jfree.chart.ChartFactory;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    public void restored() {
        ChartFactory.setChartTheme(eLamXChartTheme.getInstance());
    }

}
