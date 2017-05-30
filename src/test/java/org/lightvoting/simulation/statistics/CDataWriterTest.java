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
import org.lightjason.agentspeak.configuration.CDefaultAgentConfiguration;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.environment.CEnvironment;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by sophie on 16.05.17.
 */
public class CDataWriterTest extends TestCase
{
    /**
     * simple test for writing of dissatisfaction values
     */

//    public static void testWriteDissVals()
//    {
//        CDataWriter.createHDF5( "test_simple.h5" );
//
//        final AtomicDoubleArray l_testDissVals = new AtomicDoubleArray( new double[]{0.1, 0.5, 0.6} );
//
//        CDataWriter.createGroup( "test_simple.h5", "testchair" );
//        CDataWriter.writeDissVals( "test_simple.h5", l_testDissVals, "testchair" );
//    }

    /**
     * test for writing of data
     */

    public static void testWriteData()
    {
        final String l_fileName = "test_data.h5";
        CDataWriter.createHDF5( l_fileName );
        final List<CVotingAgent> l_agentList = new LinkedList<>();

        for ( int i = 0; i < 3; i++ )
        {
            final CChairAgent l_chairAgent = new CChairAgent( "chair" + String.valueOf( i ), new CDefaultAgentConfiguration<>(), new CEnvironment( 3, l_fileName ),
                                                          "RANDOM",
                                                          "BASIC",
                                                          l_fileName );
            l_agentList.add( new CVotingAgent( "agent" + String.valueOf( i ), new CDefaultAgentConfiguration<>(), l_chairAgent, new CEnvironment( 3, l_fileName ), 10,
                                           "RANDOM",
                                           l_fileName )
            );
        }

        final AtomicDoubleArray l_testDissVals = new AtomicDoubleArray( new double[]{0.1, 0.5, 0.6} );

        final int l_run = 0;
        final int l_iteration = 0;
        final String l_config = "RANDOM_BASIC";

        CDataWriter.writeData( l_fileName, l_run, l_config, l_agentList.get( 0 ).getChair(), l_iteration, l_agentList, l_testDissVals );
    }



}
