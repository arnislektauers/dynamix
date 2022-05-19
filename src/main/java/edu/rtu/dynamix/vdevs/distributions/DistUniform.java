/*
 * @(#)DistUniform.java Apr 3, 2003 Copyright (c) 2002-2005 Delft University of
 * Technology Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package edu.rtu.dynamix.vdevs.distributions;

/**
 * The Uniform distribution. For more information on this distribution see <a
 * href="http://mathworld.wolfram.com/UniformDistribution.html">
 * http://mathworld.wolfram.com/UniformDistribution.html </a>
 * <p>
 * (c) copyright 2002-2004 <a href="http://www.simulation.tudelft.nl">Delft
 * University of Technology </a>, the Netherlands. <br>
 * See for project information <a href="http://www.simulation.tudelft.nl">
 * www.simulation.tudelft.nl </a> <br>
 * License of use: <a href="http://www.gnu.org/copyleft/lesser.html">Lesser
 * General Public License (LGPL) </a>, no warranty.
 * 
 * @author <a href="mailto:a.verbraeck@tbm.tudelft.nl">
 *         Alexander Verbraeck </a> <br>
 *         <a href="http://www.peter-jacobs.com/index.htm"> Peter Jacobs </a>
 * @version $Revision: 1.9 $ $Date: 2005/08/11 05:47:56 $
 * @since 1.5
 */
public class DistUniform extends DistContinuous
{
    /** a is the minimum */
    private double a;

    /** b is the maximum */
    private double b;

    /**
     * constructs a new uniform distribution. a and b are real numbers with a
     * less than b. a is a location parameter, b-a is a scale parameter.
     * 
     * @param stream the numberstream
     * @param a the minimum value
     * @param b the maximum value
     */
    public DistUniform(final IStream stream, final double a,
            final double b)
    {
        super(stream);
        this.a = a;
        if (b > a)
        {
            this.b = b;
        } else
        {
            throw new IllegalArgumentException("Error Uniform - a >= b");
        }
    }

    /**
     * @see DistContinuous#draw()
     */
    @Override
    public double draw()
    {
        return this.a + (this.b - this.a) * this.stream.nextDouble();
    }

    /**
     * @see nl.tudelft.simulation.jstats.distributions.DistContinuous
     *      #probDensity(double)
     */
    @Override
    public double probDensity(final double observation)
    {
        if (observation >= this.a && observation <= this.b)
        {
            return 1.0 / (this.b - this.a);
        }
        return 0.0;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return "Uniform(" + this.a + "," + this.b + ")";
    }
}