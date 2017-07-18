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

import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightvoting.simulation.action.message.CSend;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by sophie on 18.07.17.
 */
public class CBrokerAgent extends IBaseAgent
{
    private HashSet<CVotingAgent> m_voters = new HashSet<>();
    private HashSet<CChairAgent> m_chairs = new HashSet<>();

    /**
     * ctor
     *
     * @param p_broker broker name
     * @param p_configuration agent configuration
     */
    public CBrokerAgent( final String p_broker, @Nonnull final IAgentConfiguration p_configuration )
    {
        super( p_configuration );
    }

    /**
     * return stream over agents
     * @return agent stream
     */
    public Stream<IBaseAgent> agentstream()
    {
        return Stream.concat(
            m_voters.stream(),
            m_chairs.stream()
        );
    }

    /**
     * Class CBrokerAgentGenerator
     */
    public static class CBrokerAgentGenerator extends IBaseAgentGenerator
    {

        /**
         * Store reference to send action to registered agents upon creation.
         */
        private final CSend m_send;

        /**
         * constructor of CBrokerAgentGenerator
         * @param p_send external actions
         * @param p_stream input stream
         * @throws Exception exception
         */
        public CBrokerAgentGenerator( final CSend p_send, @Nonnull final InputStream p_stream ) throws Exception
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
                            // add VotingAgent related external actions
                            Stream.of(
                                p_send
                            )
                        )
                        // build the set with a collector
                    ).collect( Collectors.toSet() ) );

            // aggregation function for the optimization function, here
            // we use an empty function
            //         IAggregation.EMPTY,
            m_send = p_send;

        }

        @Nullable
        @Override
        public CBrokerAgent generatesingle( @Nullable final Object... p_data )
        {
            final CBrokerAgent l_broker = new CBrokerAgent(

                // create a string with the agent name "agent <number>"
                // get the value of the counter first and increment, build the agent
                // name with message format (see Java documentation)
                MessageFormat.format( "broker", 0 ),

                // add the agent configuration
                m_configuration
            );

            return l_broker;
        }
    }

}
