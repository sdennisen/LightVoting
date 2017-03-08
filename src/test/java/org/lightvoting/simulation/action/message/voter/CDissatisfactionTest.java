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

package org.lightvoting.simulation.action.message.voter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.configuration.CDefaultAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.score.IAggregation;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.environment.CEnvironment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Unit test for CDissatisfaction action.
 */
public final class CDissatisfactionTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param p_testName name of the test case
     */
    public CDissatisfactionTest( final String p_testName )
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
        return new TestSuite( CDissatisfactionTest.class );
    }

    /**
     * Testing CDissatisfaction Class
     */
    public void testCDissatisfaction()
    {
        // check for correct name and number of arguments
        final CDissatisfaction l_dissatisfaction = new CDissatisfaction();
        assertEquals( CPath.from( "voting/send/chair/dissatisfaction" ), l_dissatisfaction.name() );
        assertEquals( 1, l_dissatisfaction.minimalArgumentNumber() );

        // test action execution
        // test action execution
        final ByteArrayOutputStream l_outContent = new ByteArrayOutputStream();
        System.setOut( new PrintStream( l_outContent ) );

        try
        {
            final InputStream l_aslstream = new ByteArrayInputStream(
                    "!main.\n+!main <- voting/send/chair/dissatisfaction(1).".getBytes( "UTF-8" )
            );

            final CVotingAgent l_agent = new CTestAgentGenerator( l_aslstream ).generatesingle();

            l_agent.call();

            // check for correct printout
            assertTrue(
                    l_outContent.toString().contains(
                            "action is called from agent"
                    )
            );
        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
            // fail test if exception occurred
            assertTrue( false );
            return;
        }

        System.setOut( null );
    }

    private final class CTestAgentGenerator extends IBaseAgentGenerator<CVotingAgent>
    {

        public CTestAgentGenerator( final InputStream p_stream ) throws Exception
        {
            super(
                    p_stream,
                    Stream.concat(
                            CCommon.actionsFromPackage(),
                            Stream.concat(
                                    CCommon.actionsFromAgentClass( CVotingAgent.class ),
                                    Stream.of(
                                            new CDissatisfaction()
                                    )
                            )
                    ).collect( Collectors.toSet() ),
                    IAggregation.EMPTY
            );
        }

        /* TODO check test */
        @Override
        public final CVotingAgent generatesingle( final Object... p_data )
        {
            final CChairAgent l_chairAgent = new CChairAgent( new CDefaultAgentConfiguration<>(), new CEnvironment( 23 ) );
            return new CVotingAgent( "agent", m_configuration, l_chairAgent, new CEnvironment( 23 ) );
        }
    }
}
