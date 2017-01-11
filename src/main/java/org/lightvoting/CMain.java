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

import com.google.common.collect.BiMap;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.agent.CVotingAgentGenerator;

import java.io.FileInputStream;
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
        final BiMap<Integer, CVotingAgent> l_agentmap;
        final CVotingAgentGenerator l_votingagentgenerator;

        try
                (
                        final FileInputStream l_stream = new FileInputStream( p_args[0] );
                )
        {

            l_votingagentgenerator = new CVotingAgentGenerator( l_stream );
            l_agentmap = l_votingagentgenerator
                    .generatemultiplemap( Integer.parseInt( p_args[1] ) );

        } catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
            throw new RuntimeException();
        }

        // add id as a belief myid to each agent
        l_agentmap.entrySet()
            .parallelStream()
            .forEach(
                    i -> i.getValue().beliefbase().add(
                            CLiteral.from(
                                    "myid", CRawTerm.from( i.getKey() )
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
                .forEach( j -> l_agentmap.entrySet().parallelStream().forEach( i ->
                {
                    try
                    {
                        // call each agent, i.e. trigger a new agent cycle
                        i.getValue().call();
                    }
                    catch ( final Exception l_exception )
                    {
                        l_exception.printStackTrace();
                        throw new RuntimeException();
                    }
                } ) );
    }
}
