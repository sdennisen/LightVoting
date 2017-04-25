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

import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.score.IAggregation;
import org.lightvoting.simulation.environment.CEnvironment;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by sophie on 21.02.17.
 */

public final class CChairAgentGenerator extends IBaseAgentGenerator<CChairAgent>
{

    /**
     * environment
     */
    private final CEnvironment m_environment;

    /**
     * Current free agent id, needs to be thread-safe, therefore using AtomicLong.
     */
    private final AtomicLong m_agentcounter = new AtomicLong();

    /**
     * constructor of the generator
     * @param p_stream ASL code as any stream e.g. FileInputStream
     * @throws Exception Thrown if something goes wrong while generating agents.
     */
    public CChairAgentGenerator( final InputStream p_stream, final CEnvironment p_environment ) throws Exception
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
                    CCommon.actionsFromAgentClass( CChairAgent.class ),
                    // add VotingAgent related external actions
                    Stream.of(

                    )
                )
                // build the set with a collector
            ) .collect( Collectors.toSet() ),

            // aggregation function for the optimisation function, here
            // we use an empty function
            IAggregation.EMPTY
        );
        m_environment = p_environment;
    }

    /**
     * generator method of the agent
     * @param p_data any data which can be put from outside to the generator method
     * @return returns an agent
     */

    @Override
    public final CChairAgent generatesingle( final Object... p_data )
    {
        final CChairAgent l_chairAgent = new CChairAgent(
            // create a string with the agent name "chair <number>"
            // get the value of the counter first and increment, build the agent
            // name with message format (see Java documentation)
            MessageFormat.format( "chair {0}", m_agentcounter.getAndIncrement() ), m_configuration, m_environment );
        l_chairAgent.sleep( Integer.MAX_VALUE );
        return l_chairAgent;
    }

}

// XXXXXXXXXXXXXXXXXXXX Old code XXXXXXXXXXXXXX
// TODO if necessary, reinsert in constructor new CCommittee()

