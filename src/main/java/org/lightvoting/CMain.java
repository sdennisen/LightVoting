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

package org.lightvoting;

import com.google.common.collect.Sets;
import org.lightvoting.simulation.action.message.CSend;
import org.lightvoting.simulation.agent.CChairAgentGenerator;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.agent.CVotingAgentGenerator;
import org.lightvoting.simulation.environment.CEnvironment;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* TODO: each possibility for drawing active agentd from the pool of agents needs to be a own class  */

/**
 * Main, providing runtime of LightVoting.
 */
public final class CMain
{
    private static Iterator<CVotingAgent> s_agentIterator;

    /**
     * Hidden constructor
     */
    private CMain()
    {
    }

    /**
     * Main
     * @param p_args Passed command line args: [ASL File] [Number of Agents] [Cycles]
     * @throws Exception Throws exception, e.g. on reading ASL file
     */
    public static void main( final String[] p_args ) throws Exception
    {
        // Example code taken from
        // https://lightjason.github.io/tutorial/tutorial-agentspeak-in-fifteen-minutes/
        //
        // parameter of the command-line arguments:
        // 1. ASL file
        // 2. number of agents
        // 3. number of iterations (if not set maximum)
        final Set<CVotingAgent> l_agents;
        final CVotingAgentGenerator l_votingagentgenerator;

        try
        {
            final FileInputStream l_stream = new FileInputStream( p_args[0] );
            final FileInputStream l_chairstream = new FileInputStream( p_args[1] );

            final CEnvironment l_environment = new CEnvironment( Integer.parseInt( p_args[2] ) );

            l_votingagentgenerator = new CVotingAgentGenerator( new CSend(), l_stream, l_environment );
            l_agents = l_votingagentgenerator
                    .generatemultiple( Integer.parseInt( p_args[2] ), new CChairAgentGenerator( l_chairstream, l_environment  )  )
                    .collect( Collectors.toSet() );
            System.out.println( " Numbers of agents: " + l_agents.size() );
            s_agentIterator = l_agents.iterator();

        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
            throw new RuntimeException();
        }

        // generate empty set of active agents

        final Set<CVotingAgent> l_activeAgents = Sets.newConcurrentHashSet();

        System.out.println( " Numbers of active agents: " + l_activeAgents.size() );

        System.out.println( " Numbers of active agents: " + l_activeAgents.size() );

        // runtime call (with parallel execution)

        // TODO fix failing agent calls

        IntStream
            // define cycle range, i.e. number of cycles to run sequentially
            .range( 0,
                    p_args.length < 3
                    ? Integer.MAX_VALUE
                    : Integer.parseInt( p_args[2] ) )
            .forEach( j ->
            {
                // if you want to do something in cycle j, put it here - in this case, activate three new agents
                addAgents( l_activeAgents, 3, s_agentIterator );
                System.out.println( "After Cycle " + j + ": Numbers of active agents: " + l_activeAgents.size() );
                l_activeAgents.parallelStream().forEach( i ->
                {
                    try
                    {
                        // call each agent, i.e. trigger a new agent cycle
                        i.call();
                        i.getChair().call();
                    }
                    catch ( final Exception l_exception )
                    {
                        l_exception.printStackTrace();
                        throw new RuntimeException();
                    }
                } );
            } );

    }

    private static void addAgents( final Collection<CVotingAgent> p_activeAgents, final int p_newAgNum, final Iterator<CVotingAgent> p_agentIterator  )
    {

        for ( int i = 0; i < 3; i++ )
        {
            if ( p_agentIterator.hasNext() )
            {
                final CVotingAgent l_curAg = p_agentIterator.next();
                p_activeAgents.add( l_curAg );
                System.out.println( "added Agent " + l_curAg.name() );
                p_agentIterator.remove();

            }
        }

    }

}

