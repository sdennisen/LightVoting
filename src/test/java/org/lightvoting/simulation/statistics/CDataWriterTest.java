/**
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of LightVoting by Sophie Dennisen.                               #
 * # Copyright (c) 2017, Sophie Dennisen (sophie.dennisen@tu-clausthal.de)              #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightvoting.simulation.statistics;

import com.google.common.util.concurrent.AtomicDoubleArray;
import junit.framework.TestCase;


/**
 * Created by sophie on 16.05.17.
 */
public class CDataWriterTest extends TestCase
{
    /**
     * test writing of dissatisfaction values
     */

    public static void testWriteDissVals()
    {
        CDataWriter.createHDF5( "test.h5" );

        final AtomicDoubleArray l_testDissVals = new AtomicDoubleArray( new double[]{0.1, 0.5, 0.6} );

        CDataWriter.createGroup( "test.h5", "testchair" );
        CDataWriter.writeDissVals( "test.h5", l_testDissVals, "testchair" );
    }
}
