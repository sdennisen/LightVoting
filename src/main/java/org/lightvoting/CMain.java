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

import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightvoting.simulation.action.message.CSend;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.agent.CVotingAgentGenerator;
import org.lightvoting.simulation.rule.CMinimaxApproval;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* TODO: each possibility for drawing active agentd from the pool of agents needs to be a own class  */

/**
 * Main, providing runtime of LightVoting.
 */
public final class CMain
{
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

        // we need to use a single send action instance to (un)register, i.e. keeping track of, agents.
        final CSend l_sendaction = new CSend();

        try
                (
                        final FileInputStream l_stream = new FileInputStream( p_args[0] );
                )
        {

            l_votingagentgenerator = new CVotingAgentGenerator( l_sendaction, l_stream );
            l_agents = l_votingagentgenerator
                    .generatemultiple( Integer.parseInt( p_args[1] ) )
                    .collect( Collectors.toSet() );
            System.out.println( " Numbers of agents: " + l_agents.size() );

        } catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
            throw new RuntimeException();
        }

        // add name as a belief to each agent
        l_agents
            .parallelStream()
            .forEach(
                    i -> i.beliefbase().add(
                            CLiteral.from(
                                    "myname", CRawTerm.from( i.name() )
                            )
                    )
            );

        // generate empty set of active agents

        final Set<CVotingAgent> l_activeAgents = l_votingagentgenerator
            .generatemultiple( 0 )
            .collect( Collectors.toSet() );

        System.out.println( " Numbers of active agents: " + l_activeAgents.size() );

        final Iterator<CVotingAgent>  l_agentIterator = l_agents.iterator();

        /* TODO reproduce example: draw three agents in each cycle */

        addAgents( 3, l_agentIterator, l_activeAgents );

        System.out.println( " Numbers of active agents: " + l_activeAgents.size() );

        /* TODO use IntStream.range(int inclusiveStartIndex, int exclEndIndex) to define behaviour for the corresponding cycles */

        // runtime call (with parallel execution)

        intstream( l_activeAgents );

        IntStream
            // define cycle range, i.e. number of cycles to run sequentially
            .range(
                0,
                p_args.length < 3
                ? Integer.MAX_VALUE
                : Integer.parseInt( p_args[2] )
            )
            .forEach( j -> l_activeAgents.parallelStream().forEach( i ->
            {
                try
                {
                    // call each agent, i.e. trigger a new agent cycle
                    i.call();

                }
                catch ( final Exception l_exception )
                {
                    l_exception.printStackTrace();
                    throw new RuntimeException();
                }
            } ) );

        /*
        // runtime call (with parallel execution)
        IntStream
            // define cycle range, i.e. number of cycles to run sequentially
                .range(
                        0,
                        p_args.length < 3
                                ? Integer.MAX_VALUE
                                : Integer.parseInt( p_args[2] )
                )
                .forEach( j -> l_agents.parallelStream().forEach( i ->
                {
                    try
                    {
                        // call each agent, i.e. trigger a new agent cycle
                        i.call();
                    }
                    catch ( final Exception l_exception )
                    {
                        l_exception.printStackTrace();
                        throw new RuntimeException();
                    }
                } ) );
          */



        final CMinimaxApproval l_minimaxApproval = new CMinimaxApproval();

        final List<String> l_alternatives = new ArrayList<String>();

        l_alternatives.add( "POI1" );
        l_alternatives.add( "POI2" );
        l_alternatives.add( "POI3" );
        l_alternatives.add( "POI4" );
        l_alternatives.add( "POI5" );
        l_alternatives.add( "POI6" );

        final List<int[]> l_votes = new ArrayList<int[]>();

        final int[] l_vote1 = {1, 1, 1, 1, 1, 0};
        final int[] l_vote2 = {1, 1, 1, 1, 1, 0};
        final int[] l_vote3 = {1, 1, 1, 1, 1, 0};
        final int[] l_vote4 = {1, 1, 1, 1, 1, 0};
        final int[] l_vote5 = {1, 1, 1, 1, 1, 0};
        final int[] l_vote6 = {0, 0, 0, 1, 1, 1};

        l_votes.add( l_vote1 );
        l_votes.add( l_vote2 );
        l_votes.add( l_vote3 );
        l_votes.add( l_vote4 );
        l_votes.add( l_vote5 );
        l_votes.add( l_vote6 );

        final int l_comSize = 3;

        l_minimaxApproval.applyRule( l_alternatives, l_votes, l_comSize );
    }

    private static void intstream( final Collection<CVotingAgent> p_activeAgents )
    {
        IntStream
            // define cycle range, i.e. number of cycles to run sequentially
            .range( 0, 1 )
            .forEach( j ->
            {
                /* TODO if you want to do something in cycle 0, put it here - in this case, activate three new agents */
                System.out.println( "Test" );
                p_activeAgents.parallelStream().forEach( i ->
                {
                    try
                    {
                        // call each agent, i.e. trigger a new agent cycle
                        i.call();
                    }
                    catch ( final Exception l_exception )
                    {
                        l_exception.printStackTrace();
                        throw new RuntimeException();
                    }
                } );
            } );
    }

    private static void addAgents( final int p_newAgNum, final Iterator<CVotingAgent> p_agentIterator, final Set<CVotingAgent> p_activeAgents )
    {

        for ( int i = 0; i < 3; i++ )
        {
            if ( p_agentIterator.hasNext() )
            {
                final CVotingAgent l_curAg = p_agentIterator.next();
                p_activeAgents.add( l_curAg );
            }

        }

    }
}
