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

package org.lightvoting.simulation.agent.coordinated_basic;

import cern.colt.bitvector.BitVector;
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
import org.lightvoting.simulation.action.message.coordinated_basic.CSendCB;
import org.lightvoting.simulation.agent.coordinated_basic.CVotingAgentCB.CVotingAgentGenerator;
import org.lightvoting.simulation.environment.coordinated_basic.CEnvironmentCB;
import org.lightvoting.simulation.environment.coordinated_basic.CGroupCB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by sophie on 18.07.17.
 */
@IAgentAction
public class CBrokerAgentCB extends IBaseAgent<CBrokerAgentCB>
{
    private List<CVotingAgentCB> m_voters = new ArrayList<>();
    private HashSet<CChairAgentCB> m_chairs = new HashSet<>();
    private HashSet<CGroupCB> m_groups = new HashSet<>();
    private final String m_name;
    private final int m_agNum;
    private int m_count;
    private IAgentConfiguration<CVotingAgentCB> m_configuration;
    private final InputStream m_stream;
    private int m_altnum;
    private double m_joinThr;
    private List<AtomicDoubleArray> m_prefList;
    private final String m_broker;
    private CVotingAgentGenerator m_votingagentgenerator;
    private int m_groupNum;
    // TODO read via yaml
    private int m_capacity;
    // TODO read via yaml
    private AtomicLong m_timeout;
    private Object m_fileName;
    private int m_chairNum;
    private CChairAgentCB.CChairAgentGenerator m_chairagentgenerator;
    private final InputStream m_chairstream;
    private final int m_comsize;
    // TODO lining limit for allowing agents to drive alone
    // HashMap for storing how often an agent had to leave a group
    private final HashMap<CVotingAgentCB, Long> m_lineHashMap = new HashMap<CVotingAgentCB, Long>();
    private final CEnvironmentCB m_environmentCB;
    private HashMap<String, Object> m_map = new HashMap<>();
    private String m_rule = "MINISUM_APPROVAL";

    /**
     * ctor
     * @param p_broker broker name
     * @param p_configuration agent configuration
     * @param p_agNum number of voters
     * @param p_stream agent stream
     * @param p_chairstream chair stream
     * @param p_environment enviroment
     * @param p_altnum number of alternatives
     * @param p_name name
     * @param p_joinThr join threshold
     * @param p_prefList preference list
     * @param p_comsize committee size
     * @throws Exception exception
     */
    public CBrokerAgentCB( final String p_broker,
                           @Nonnull final IAgentConfiguration p_configuration,
                           final int p_agNum,
                           final InputStream p_stream,
                           final InputStream p_chairstream,
                           final CEnvironmentCB p_environment,
                           final int p_altnum,
                           final String p_name,
                           final double p_joinThr,
                           final List<AtomicDoubleArray> p_prefList,
                           final int p_comsize
    ) throws Exception
    {
        super( p_configuration );
        m_broker = p_broker;
        m_agNum = p_agNum;
        m_stream = p_stream;
        m_chairstream = p_chairstream;
        m_environmentCB = p_environment;
        m_altnum = p_altnum;
        m_name = p_name;
        m_joinThr = p_joinThr;
        m_prefList = p_prefList;
        // TODO set via yaml
        m_capacity = 5;
        m_timeout = new AtomicLong( 20 );
        m_comsize = p_comsize;

        m_votingagentgenerator = new CVotingAgentGenerator( new CSendCB(), m_stream, m_environmentCB, m_altnum, m_name,
                                                                           m_joinThr, m_prefList );
        m_chairagentgenerator = new CChairAgentCB.CChairAgentGenerator( m_chairstream, m_environmentCB, m_name, m_altnum, m_comsize );

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

    public HashMap<String,Object> map()
    {
        return m_map;
    }


    @IAgentActionFilter
    @IAgentActionName( name = "decrement/counters" )
    private synchronized void decrementCounters() throws Exception
    {

        for ( final CGroupCB l_group : m_groups )
        {
            l_group.decrementCounter();
        }

    }

    @IAgentActionFilter
    @IAgentActionName( name = "create/ag" )
    private CVotingAgentCB createAgent( final Number p_createdNum ) throws Exception
    {

        System.out.println( "voters generated so far: " + p_createdNum );

        final CVotingAgentCB l_testvoter = m_votingagentgenerator.generatesinglenew();

        System.out.println( "new voter:" + l_testvoter.name() );

        m_voters.add( l_testvoter );

        return l_testvoter;

    }

    @IAgentActionFilter
    @IAgentActionName( name = "assign/group" )
    private synchronized void assignGroup( final CVotingAgentCB p_votingAgent ) throws Exception
    {
        CGroupCB l_determinedGroup = null;
        int l_dist = Integer.MAX_VALUE;

        System.out.println( "Assigning group to " + p_votingAgent.name() );
        System.out.println( "join threshold: " + m_joinThr );
        for ( final CGroupCB l_group : m_groups )
        {
            System.out.println( "group " + l_group.id() + " open: " + l_group.open() );
            // you can only add an agent to group if it is still open and the result is not null
            if ( l_group.open() && !( l_group.result() == null ) )
            {
                System.out.println( "Result:" + l_group.result() );
                // use new distance if it is lower than the joint threshold and than the old distance
                final int l_newDist;
                if ( m_rule.equals( "MINISUM_APPROVAL"))
                    l_newDist = this.hammingDistance( p_votingAgent.getBitVote(), l_group.result() );
                else
                    l_newDist = this.ranksum( p_votingAgent.getCLOVote(), l_group.result() );
                System.out.println( "Distance: " + l_newDist );
                if ( l_newDist < m_joinThr && l_newDist < l_dist )
                {
                    l_dist = l_newDist;
                    l_determinedGroup = l_group;
                }
            }
        }

        // if there is no available group, create a new group
        if ( l_determinedGroup != null )
        {
            l_determinedGroup.add( p_votingAgent );
            System.out.println( "Adding agent " + p_votingAgent.name() + " to existing group" + ", ID " + l_determinedGroup.id() );
            p_votingAgent.beliefbase().add( CLiteral.from( "mygroup", CRawTerm.from( l_determinedGroup ) ) );
            p_votingAgent.beliefbase().add( CLiteral.from( "mychair", CRawTerm.from( l_determinedGroup.chair() ) ) );
            p_votingAgent.setChair( l_determinedGroup.chair() );
          //  m_chairs.add( l_determinedGroup.chair() );
            return;
        }

        final CChairAgentCB l_chairAgent = m_chairagentgenerator.generatesinglenew();

        // if there was no available group, create a new group

        final CGroupCB l_group = new CGroupCB( p_votingAgent, l_chairAgent, m_groupNum++, m_capacity, m_timeout );
        m_groups.add( l_group );
        System.out.println( "Creating new group with agent " + p_votingAgent.name() + ", ID " + l_group.id() );

        p_votingAgent.beliefbase().add( CLiteral.from( "mygroup", CRawTerm.from( l_group ) ) );
        p_votingAgent.beliefbase().add( CLiteral.from( "mychair", CRawTerm.from( l_chairAgent ) ) );
        p_votingAgent.setChair( l_chairAgent );

        m_chairs.add( l_chairAgent );
        m_map.put( "chairNum", m_chairs.size() );

    }

    private int ranksum( final List<Long> p_voteCLO, final BitVector p_result )
    {
        System.out.println( "Vote: " + p_voteCLO );
        System.out.println( "Result: " + p_result );

        int l_sum = 0;

        for ( int i=0; i < m_altnum; i++ )
        {
            if ( p_result.get( i ) )
            {
                l_sum = l_sum + this.getPos( i, p_voteCLO );
            }
        }

        // normalisation: subtract (k*(k+1))/2
        l_sum = l_sum -( m_comsize * ( m_comsize + 1 ) )/2;

        return l_sum;
    }

    private int getPos( final int p_i, final List<Long> p_voteCLO )
    {
        for ( int j = 0; j < p_voteCLO.size(); j++ )
            if ( p_voteCLO.get( j ) == p_i )
                return ( j+1 );
        return 0;
    }

    private int hammingDistance( final BitVector p_bitVote, final BitVector p_result )
    {
        System.out.println( "vote: " + p_bitVote + " committee: " + p_result );
        final BitVector l_diff = p_result.copy();
        l_diff.xor( p_bitVote );
        System.out.println( "diff: " + l_diff + " cardinality: " + l_diff.cardinality() );
        return l_diff.cardinality();
    }

    @IAgentActionFilter
    @IAgentActionName( name = "update/groups" )
    private synchronized void updateGroups() throws Exception
    {

        // TODO refactor method

        boolean l_allReady = true;

        for ( final CGroupCB l_group : m_groups )
        {

            if ( l_group.result() == null )
                l_allReady = false;
            // if all voters have submitted their votes, there is nothing to check, group is clean

//            if ( l_group.areVotesSubmitted() )
//                l_group.chair().beliefbase().add(
//                    CLiteral.from(
//                        "cleangroup",
//                        CRawTerm.from( 1 )
//                    )
//                );
//
//            else  if ( l_group.chair().timedout() )
//
//                // remove voters from group who didn't vote/whose votes didn't reach the chair
//            {
//
//                final CopyOnWriteArrayList<String> l_toRemoveList = new CopyOnWriteArrayList();
//                final CopyOnWriteArrayList<CVotingAgentCB> l_toRemoveAgents = new CopyOnWriteArrayList();
//                l_group.agents().filter( i -> !l_group.chair().voters().contains( i ) )
//                       .forEach( j ->
//                       {
//                           l_toRemoveList.add( j.name() );
//                           l_toRemoveAgents.add( j );
//                           m_lineHashMap.put( j, j.liningCounter() );
//                       } );
//                System.out.println( "XXXXXXX" + l_toRemoveList );
//
//                l_group.removeAll( l_toRemoveList );
//
//                // "re-queue" removed voters
//
//                l_toRemoveAgents.parallelStream().forEach( i -> this.beliefbase().add(
//                    CLiteral.from(
//                        "newag",
//                        CRawTerm.from( i ),
//                        CRawTerm.from( m_lineHashMap.get( i ) )
//                    )
//                                                 )
//                );
//
//                // set belief in chair that group was "cleaned up"
//
//                l_group.chair().beliefbase().add(
//                    CLiteral.from(
//                        "cleangroup",
//                        CRawTerm.from( 1 )
//                    )
//                );
//            }
        }

        if ( l_allReady )
        {
            System.out.println( "all groups ready" );

            this.beliefbase().add(
                CLiteral.from(
                    "allgroupsready",
                    CRawTerm.from( 1 )
                )
            );
        }
    }

    /**
     * Class CBrokerAgentGenerator
     */
    public static class CBrokerAgentGenerator extends IBaseAgentGenerator<CBrokerAgentCB>
    {

        /**
         * Store reference to send action to registered agents upon creation.
         */
        private final CSendCB m_send;
        private final int m_agNum;
        private int m_count;
        private final InputStream m_stream;
        private final CEnvironmentCB m_environment;
        private final String m_name;
        private final double m_joinThr;
        private final List<AtomicDoubleArray> m_prefList;
        private final InputStream m_chairstream;
        private int m_altnum;
        private int m_comsize;

        /**
         * constructor of CBrokerAgentGenerator
         * @param p_send external actions
         * @param p_brokerStream broker stream
         * @param p_agNum number of voters
         * @param p_stream input stream
         * @param p_chairStream chair stream
         * @param p_environment environment
         * @param p_altnum number of alternatives
         * @param p_name name
         * @param p_joinThr join threshold
         * @param p_prefList preference list
         * @param p_comsize committee size
         * @throws Exception exception
         */
        public CBrokerAgentGenerator( final CSendCB p_send,
                                      final FileInputStream p_brokerStream,
                                      final int p_agNum,
                                      final InputStream p_stream,
                                      final InputStream p_chairStream,
                                      final CEnvironmentCB p_environment,
                                      final int p_altnum,
                                      final String p_name,
                                      final double p_joinThr,
                                      final List<AtomicDoubleArray> p_prefList,
                                      final int p_comsize
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
                            CCommon.actionsFromAgentClass( CBrokerAgentCB.class ),
                            // add VotingAgent related external actions
                            Stream.of(
                                p_send
                            )
                        )
                        // build the set with a collector
                    ).collect( Collectors.toSet() ) );

            System.out.println( "actions defined in broker class: " + CCommon.actionsFromAgentClass( CBrokerAgentCB.class ).collect( Collectors.toSet() ) );


            // aggregation function for the optimization function, here
            // we use an empty function
            //         IAggregation.EMPTY,
            m_send = p_send;
            m_agNum = p_agNum;
            m_stream = p_stream;
            m_chairstream = p_chairStream;
            m_environment = p_environment;
            m_altnum = p_altnum;
            m_name = p_name;
            m_joinThr = p_joinThr;
            m_prefList = p_prefList;
            m_comsize = p_comsize;
        }

        @Nullable
        @Override
        public CBrokerAgentCB generatesingle( @Nullable final Object... p_data )
        {
            CBrokerAgentCB l_broker = null;
            try
            {
                l_broker = new CBrokerAgentCB(

                    // create a string with the agent name "agent <number>"
                    // get the value of the counter first and increment, build the agent
                    // name with message format (see Java documentation)
                    MessageFormat.format( "broker", 0 ),

                    // add the agent configuration
                    m_configuration,
                    m_agNum,
                    m_stream,
                    m_chairstream,
                    m_environment,
                    m_altnum,
                    m_name,
                    m_joinThr,
                    m_prefList,
                    m_comsize
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
