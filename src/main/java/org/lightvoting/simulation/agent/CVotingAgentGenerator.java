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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.score.IAggregation;
import org.lightvoting.simulation.action.message.voter.CDissatisfaction;
import org.lightvoting.simulation.action.message.voter.CVote;
import org.lightvoting.simulation.action.group.CInitiate;
import org.lightvoting.simulation.action.group.CJoin;
import org.lightvoting.simulation.action.group.CLeave;
import org.lightvoting.simulation.action.group.CPreferred;
import org.lightvoting.simulation.action.rules.minmaxapproval.CCommittee;

import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Code from https://lightjason.github.io/tutorial/tutorial-agentspeak-in-fifteen-minutes/
 */
public class CVotingAgentGenerator extends IBaseAgentGenerator<CVotingAgent>
{

    private int m_nextfreeid;

    /**
     * constructor of the generator
     * @param p_stream ASL code as any stream e.g. FileInputStream
     * @throws Exception Thrown if something goes wrong while generating agents.
     */
    public CVotingAgentGenerator( final InputStream p_stream ) throws Exception
    {
        super(
                // input ASL stream
                p_stream,

                // a set with all possible actions for the agent
                Stream.concat(
                        // we use all build-in actions of LightJason
                        CCommon.actionsFromPackage(),
                        Stream.concat(
                                // use the actions which are defined inside the agent class
                                CCommon.actionsFromAgentClass( CVotingAgent.class ),
                                // add VotingAgent related external action
                                Stream.of(
                                        new CCommittee(),
                                        new CInitiate(),
                                        new CJoin(),
                                        new CLeave(),
                                        new CPreferred(),
                                        new CDissatisfaction(),
                                        new CVote()
                                )
                        )
                        // build the set with a collector
                ).collect( Collectors.toSet() ),

                // aggregation function for the optimization function, here
                // we use an empty function
                IAggregation.EMPTY
        );
    }

    // generator method of the agent
    // @param p_data any data which can be put from outside to the generator method
    // @return returns an agent
    @Override
    public final CVotingAgent generatesingle( final Object... p_data )
    {
        return new CVotingAgent( m_configuration );
    }

    /**
     * Generate a map of multiple CVotingAgents
     * @param p_number amount of agents to create
     * @param p_data data passed to the singleagent generator
     * @return Synchronised BiMap with Id -> Agent mapping
     */
    public final BiMap<Integer, CVotingAgent> generatemultiplemap( final int p_number, final Object... p_data )
    {
        final int l_startid = this.m_nextfreeid;
        this.m_nextfreeid += p_number;

        return Maps.synchronizedBiMap(
                HashBiMap.create(
                        IntStream.range( l_startid, l_startid + p_number )
                                .parallel()
                                .boxed()
                                .collect( Collectors.toMap( i -> i, i -> this.generatesingle( p_data ) ) )
                )
        );
    }
}
