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
import org.lightjason.agentspeak.agent.IAgent;
import org.lightvoting.simulation.action.message.random_basic.CSendRB;
import org.lightvoting.simulation.agent.random_basic.CChairAgentRB;
import org.lightvoting.simulation.agent.random_basic.CVotingAgentRB;
import org.lightvoting.simulation.environment.random_basic.CEnvironmentRB;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Unit test for CVotingAgentGenerator.
 */
public final class CVotingAgentRandomBasicGeneratorTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param p_testName name of the test case
     */
    public CVotingAgentRandomBasicGeneratorTest( final String p_testName )
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
        return new TestSuite( CVotingAgentRandomBasicGeneratorTest.class );
    }

    /**
     * Testing CVotingAgentRB Class
     */
    public void testCVotingAgent()
    {
        try
        {
            final InputStream l_aslstream = new ByteArrayInputStream(
                    "!main.\n+!main.".getBytes( "UTF-8" )
            );

            final CSendRB l_sendaction = new CSendRB();

            /* TODO Check test */
            final Set<CVotingAgentRB> l_agents = new

                CVotingAgentRB.CVotingAgentGenerator( l_sendaction, l_aslstream, new CEnvironmentRB( 23, "foo.h5", 3 ), 10, "foo.h5", 5, new ArrayList(), "MINISUM_APPROVAL", 1, 1 )
                    .generatemultiple( 23, new CChairAgentRB.CChairAgentGenerator( l_aslstream, new CEnvironmentRB( 23, "foo.h5", 3 ),
                                                                                   "foo.h5", 0,
                                                                                   3, 1, 5,
                            1 ) )
                    .collect( Collectors.toSet() );

            assertEquals( 23, l_agents.size() );

            l_agents.forEach( i ->
            {
                assertTrue( i instanceof CVotingAgentRB );
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

