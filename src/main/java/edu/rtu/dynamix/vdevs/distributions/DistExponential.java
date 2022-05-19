/*
 * @(#)DistExponential.java Apr 3, 2003 Copyright (c) 2002-2005 Delft University
 * of Technology Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights
 * reserved. This software is proprietary information of Delft University of
 * Technology The code is published under the Lesser General Public License
 */
package edu.rtu.dynamix.vdevs.distributions;


/**
 * The Exponential distribution. For more information on this distribution see
 * <a href="http://mathworld.wolfram.com/ExponentialDistribution.html">
 * http://mathworld.wolfram.com/ExponentialDistribution.html </a>
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
public class DistExponential extends DistContinuous
{
    /** mean is the mean value of the exponential distribution */
    private double mean;

    /**
     * constructs a new exponential function. The exponential distribution
     * describes the interarrival times of "cutomers" to a system that occur at
     * a constant rate.
     * 
     * @param stream the numberstream
     * @param mean the mean (mean>0)
     */
    public DistExponential(final IStream stream, final double mean)
    {
        super(stream);
        if (mean > 0.0)
        {
            this.mean = mean;
        } else
        {
            throw new IllegalArgumentException("Error Exponential - mean<=0");
        }
    }

    /**
     * @see DistContinuous#draw()
     */
    @Override
    public double draw()
    {
        return -this.mean * Math.log(this.stream.nextDouble());
    }

    /**
     * @see nl.tudelft.simulation.jstats.distributions.DistContinuous
     *      #probDensity(double)
     */
    @Override
    public double probDensity(final double observation)
    {
        if (observation >= 0)
        {
            return (1 / this.mean) * Math.exp(-observation / this.mean);
        }
        return 0.0;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return "Exponential(" + this.mean + ")";
    }
}