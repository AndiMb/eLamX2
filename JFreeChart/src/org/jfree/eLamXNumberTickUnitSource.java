/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfree;

import de.elamx.core.GlobalProperties;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Objects;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;

/**
 * A tick unit source implementation that returns NumberTickUnit instances 
 * that are multiples of 1, 2 or 5 times some power of 10.
 * 
 * @since 1.0.18
 */
public class eLamXNumberTickUnitSource implements TickUnitSource, Serializable {

    private boolean integers;
    
    private int power = 0;
    
    private int factor = 1;
    
    /** The number formatter to use (an override, it can be null). */
    private NumberFormat formatter;

    /**
     * Creates a new instance.
     */
    public eLamXNumberTickUnitSource() {
        this(false);
    }
    
    /**
     * Creates a new instance.
     * 
     * @param integers  show integers only. 
     */
    public eLamXNumberTickUnitSource(boolean integers) {
        this(integers, null);
    }
    
    /**
     * Creates a new instance.
     * 
     * @param integers  show integers only?
     * @param formatter  a formatter for the axis tick labels ({@code null} 
     *         permitted).
     */
    public eLamXNumberTickUnitSource(boolean integers, NumberFormat formatter) {
        this.integers = integers;
        this.formatter = formatter;
        this.power = 0;
        this.factor = 1;
    }
    
    @Override
    public TickUnit getLargerTickUnit(TickUnit unit) {
        TickUnit t = getCeilingTickUnit(unit);
        if (t.equals(unit)) {
            next();
            t = new NumberTickUnit(getTickSize(), getTickLabelFormat(), 
                    getMinorTickCount());
        }
        return t; 
    }

    @Override
    public TickUnit getCeilingTickUnit(TickUnit unit) {
        return getCeilingTickUnit(unit.getSize());
    }

    @Override
    public TickUnit getCeilingTickUnit(double size) {
        if (Double.isInfinite(size)) {
            throw new IllegalArgumentException("Must be finite.");
        }
        this.power = (int) Math.ceil(Math.log10(size));
        if (this.integers) {
            power = Math.max(this.power, 0);
        }
        this.factor = 1;
        boolean done = false;
        // step down in size until the current size is too small or there are 
        // no more units
        while (!done) {
            done = !previous();
            if (getTickSize() < size) {
                next();
                done = true;
            }
        }
        return new NumberTickUnit(getTickSize(), getTickLabelFormat(), 
                getMinorTickCount());
    }
    
    private boolean next() {
        if (factor == 1) {
            factor = 2;
            return true;
        }
        if (factor == 2) {
            factor = 5;
            return true;
        }
        if (factor == 5) {
            if (power == 300) {
                return false;
            }
            power++;
            factor = 1;
            return true;
        } 
        throw new IllegalStateException("We should never get here.");
    }

    private boolean previous() {
        if (factor == 1) {
            if (this.integers && power == 0 || power == -300) {
                return false;
            }
            factor = 5;
            power--;
            return true;
        } 
        if (factor == 2) {
            factor = 1;
            return true;
        }
        if (factor == 5) {
            factor = 2;
            return true;
        } 
        throw new IllegalStateException("We should never get here.");
    }

    private double getTickSize() {
        return this.factor * Math.pow(10.0, this.power);
    }
    
    private final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(GlobalProperties.getDefault().getActualLocale());
    private final DecimalFormat dfNeg4 = new DecimalFormat("0.0000", symbols);
    private final DecimalFormat dfNeg3 = new DecimalFormat("0.000", symbols);
    private final DecimalFormat dfNeg2 = new DecimalFormat("0.00", symbols);
    private final DecimalFormat dfNeg1 = new DecimalFormat("0.0", symbols);
    private final DecimalFormat df0 = new DecimalFormat("#,##0", symbols);
    private final DecimalFormat df = new DecimalFormat("#.######E0", symbols);
    
    private NumberFormat getTickLabelFormat() {
        if (this.formatter != null) {
            return this.formatter;
        }
        if (power == -4) {
            return dfNeg4;
        }
        if (power == -3) {
            return dfNeg3;
        }
        if (power == -2) {
            return dfNeg2;
        }
        if (power == -1) {
            return dfNeg1;
        }
        if (power >= 0 && power <= 6) {
            return df0;
        }
        return df;
    }
    
    private int getMinorTickCount() {
        if (factor == 1) {
            return 10;
        } else if (factor == 5) {
            return 5;
        }
        return 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof eLamXNumberTickUnitSource)) {
            return false;
        }
        eLamXNumberTickUnitSource that = (eLamXNumberTickUnitSource) obj;
        if (this.integers != that.integers) {
            return false;
        }
        if (!Objects.equals(this.formatter, that.formatter)) {
            return false;
        }
        if (this.power != that.power) {
            return false;
        }
        return this.factor == that.factor;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.integers ? 1 : 0);
        hash = 89 * hash + this.power;
        hash = 89 * hash + this.factor;
        hash = 89 * hash + Objects.hashCode(this.formatter);
        return hash;
    }
}
