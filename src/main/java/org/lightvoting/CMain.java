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
import org.lightvoting.simulation.combinations.CCombination;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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

        /* TODO move call to CMinimaxApproval.java */

        final CCombination l_tester = new CCombination();
        final int[] l_arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        l_tester.combinations( l_arr, 3, 0, new int[3] );

        final  List<int[]> l_resultList = l_tester.getResultList();
        l_tester.clearList();

        for ( int i = 0; i < l_resultList.size(); i++ )
        {
            System.out.println( Arrays.toString( l_resultList.get( i ) ) );
        }

        System.out.println( "Number of committees: " + l_resultList.size() );

        final int[][] l_comVects = new int[l_resultList.size()][l_arr.length];

        for ( int i = 0; i < l_resultList.size(); i++ )
        {

            for ( int j = 0; j < 3; j++ )
            {
                //System.out.println( " i: " + i + " j: " + j + " l_index: " + l_index + " value: " + l_resultList.get( i )[j]);
                l_comVects[i][l_resultList.get( i )[j]] = 1;
            }
            System.out.println( "Committee " + i + ": " + Arrays.toString( l_comVects[i] ) );
        }

    }
}
