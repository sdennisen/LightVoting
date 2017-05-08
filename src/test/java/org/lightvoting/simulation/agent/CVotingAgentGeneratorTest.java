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

package org.lightvoting.simulation.agent;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.bytedeco.javacpp.hdf5;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightvoting.simulation.action.message.CSend;
import org.lightvoting.simulation.environment.CEnvironment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Unit test for CVotingAgentGenerator.
 */
public final class CVotingAgentGeneratorTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param p_testName name of the test case
     */
    public CVotingAgentGeneratorTest( final String p_testName )
    {
        super( p_testName );
    }

    /**
     * Testsuite
     *
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CVotingAgentGeneratorTest.class );
    }

    /**
     * Testing CVotingAgent Class
     */
    public void testCVotingAgent()
    {
        try
        {
            final InputStream l_aslstream = new ByteArrayInputStream(
                    "!main.\n+!main.".getBytes( "UTF-8" )
            );

            final CSend l_sendaction = new CSend();

            /* TODO Check test */
            final Set<CVotingAgent> l_agents = new

                CVotingAgent.CVotingAgentGenerator( l_sendaction, l_aslstream, new CEnvironment( 23, new hdf5.H5File() ), 10, "RANDOM" )
                    .generatemultiple( 23, new CChairAgent.CChairAgentGenerator( l_aslstream, new CEnvironment( 23, new hdf5.H5File() ), "RANDOM", "BASIC" ) )
                    .collect( Collectors.toSet() );

            assertEquals( 23, l_agents.size() );

            l_agents.forEach( i ->
            {
                assertTrue( i instanceof CVotingAgent );
                assertTrue( i instanceof IAgent );
            } );
        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
            return;
        }
    }
}

