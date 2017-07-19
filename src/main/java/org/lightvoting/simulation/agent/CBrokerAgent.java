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

import com.google.common.util.concurrent.AtomicDoubleArray;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.action.message.CSend;
import org.lightvoting.simulation.agent.CVotingAgent.CVotingAgentGenerator;
import org.lightvoting.simulation.environment.CEnvironment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by sophie on 18.07.17.
 */
@IAgentAction
public class CBrokerAgent extends IBaseAgent<CBrokerAgent>
{
    private HashSet<CVotingAgent> m_voters = new HashSet<>();
    private HashSet<CChairAgent> m_chairs = new HashSet<>();
    private final String m_name;
    private final int m_agNum;
    private int m_count;
    private IAgentConfiguration<CVotingAgent> m_configuration;
    private final InputStream m_stream;
    private CEnvironment m_environment;
    private int m_altnum;
    private double m_joinThr;
    private List<AtomicDoubleArray> m_prefList;
    private final String m_broker;
    private CVotingAgentGenerator m_votingagentgenerator;

    /**
     * ctor
     * @param p_broker broker name
     * @param p_configuration agent configuration
     * @param p_agNum number of voters
     * @param p_stream agent stream
     * @param p_environment enviroment
     * @param p_name name
     * @param p_joinThr join threshold
     * @param p_prefList preference list
     * @throws Exception exception
     */
    public CBrokerAgent( final String p_broker,
                         @Nonnull final IAgentConfiguration p_configuration,
                         final int p_agNum,
                         final InputStream p_stream,
                         final CEnvironment p_environment,
                         final String p_name,
                         final double p_joinThr,
                         final List<AtomicDoubleArray> p_prefList
    ) throws Exception
    {
        super( p_configuration );
        m_broker = p_broker;
        m_agNum = p_agNum;
        m_stream = p_stream;
        m_environment = p_environment;
        m_name = p_name;
        m_joinThr = p_joinThr;
        m_prefList = p_prefList;

        m_votingagentgenerator = new CVotingAgentGenerator( new CSend(), m_stream, m_environment, m_altnum, m_name,
                                                                                  m_joinThr, m_prefList );

        this.trigger( CTrigger.from(
            ITrigger.EType.ADDBELIEF,
            CLiteral.from(
                "agnum",
                CRawTerm.from( m_agNum ) )
                      )
        );

//        this.beliefbase().beliefbase().add( CLiteral.from(
//
//            "agnum",
//            CRawTerm.from( m_agNum )  ) );
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

    @IAgentActionFilter
    @IAgentActionName( name = "create/ag" )
    private void createAgent( final Number p_createdNum ) throws Exception
    {

        System.out.println( "voters generated so far: " + p_createdNum );

        final CVotingAgent l_testvoter = m_votingagentgenerator.generatesinglenew();

        System.out.println( "new voter:" + l_testvoter.name() );

        m_voters.add( l_testvoter );

    }


    /**
     * Class CBrokerAgentGenerator
     */
    public static class CBrokerAgentGenerator extends IBaseAgentGenerator<CBrokerAgent>
    {

        /**
         * Store reference to send action to registered agents upon creation.
         */
        private final CSend m_send;
        private final int m_agNum;
        private int m_count;
        private final InputStream m_stream;
        private final CEnvironment m_environment;
        private final String m_name;
        private final double m_joinThr;
        private final List<AtomicDoubleArray> m_prefList;

        /**
         * constructor of CBrokerAgentGenerator
         * @param p_send external actions
         * @param p_brokerStream broker stream
         * @param p_agNum number of voters
         * @param p_stream input stream
         * @param p_environment environment
         * @param p_altnum number of alternatives
         * @param p_name name
         * @param p_joinThr join threshold
         * @param p_prefList preference list
         * @throws Exception exception
         * */
        public CBrokerAgentGenerator( final CSend p_send,
                                      final FileInputStream p_brokerStream,
                                      final int p_agNum,
                                      @Nonnull final InputStream p_stream,
                                      final CEnvironment p_environment,
                                      final int p_altnum, final String p_name,
                                      final double p_joinThr,
                                      final List<AtomicDoubleArray> p_prefList
        ) throws Exception
        {
            super(
                    // input ASL stream
                    p_brokerStream,

                    // a set with all possible actions for the agent
                    Stream.concat(
                        // we use all build-in actions of LightJason
                        CCommon.actionsFromPackage(),
                        Stream.concat(
                            // use the actions which are defined inside the agent class
                            CCommon.actionsFromAgentClass( CBrokerAgent.class ),
                            // add VotingAgent related external actions
                            Stream.of(
                                p_send
                            )
                        )
                        // build the set with a collector
                    ).collect( Collectors.toSet() ) );

            System.out.println( "actions defined in broker class: " + CCommon.actionsFromAgentClass( CBrokerAgent.class ).collect( Collectors.toSet() ) );


            // aggregation function for the optimization function, here
            // we use an empty function
            //         IAggregation.EMPTY,
            m_send = p_send;
            m_agNum = p_agNum;
            m_stream = p_stream;
            m_environment = p_environment;
            m_name = p_name;
            m_joinThr = p_joinThr;
            m_prefList = p_prefList;
        }

        @Nullable
        @Override
        public CBrokerAgent generatesingle( @Nullable final Object... p_data )
        {
            CBrokerAgent l_broker = null;
            try
            {
                l_broker = new CBrokerAgent(

                    // create a string with the agent name "agent <number>"
                    // get the value of the counter first and increment, build the agent
                    // name with message format (see Java documentation)
                    MessageFormat.format( "broker", 0 ),

                    // add the agent configuration
                    m_configuration,
                    m_agNum,
                    m_stream,
                    m_environment,
                    m_name,
                    m_joinThr,
                    m_prefList
                );
            }
            catch ( final Exception l_ex )
            {
                l_ex.printStackTrace();
            }

            return l_broker;
        }


    }

}
