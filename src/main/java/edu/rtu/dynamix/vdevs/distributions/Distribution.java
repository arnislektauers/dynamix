/*
 * @(#)Dist.java Apr 3, 2003 Copyright (c) 2002-2005 Delft University of
 * Technology Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * This software is proprietary information of Delft University of Technology
 * The code is published under the Lesser General Public License
 */
package edu.rtu.dynamix.vdevs.distributions;


/**
 * The Distribution class forms the basis for all statistical distributions.
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
 * @version $Revision: 1.8 $ $Date: 2005/08/11 05:47:56 $
 * @since 1.5
 */
public abstract class Distribution implements java.io.Serializable
{

    /** stream is the random number generator from which to draw */
    protected IStream stream;

    /**
     * Constructs a new Distribution.
     * 
     * @param stream the stream for this mathematical distribution.
     */
    public Distribution(final IStream stream)
    {
        this.stream = stream;
    }
}