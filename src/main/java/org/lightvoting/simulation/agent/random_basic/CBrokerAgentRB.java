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

package org.lightvoting.simulation.agent.random_basic;

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
import org.lightvoting.simulation.action.message.random_basic.CSendRB;
import org.lightvoting.simulation.agent.random_basic.CVotingAgentRB.CVotingAgentGenerator;
import org.lightvoting.simulation.environment.random_basic.CEnvironmentRB;
import org.lightvoting.simulation.environment.random_basic.CGroupRB;
import org.lightvoting.simulation.statistics.EDataDB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by sophie on 18.07.17.
 */
@IAgentAction
public class CBrokerAgentRB extends IBaseAgent<CBrokerAgentRB>
{
    private final int m_sim;
    private final int m_run;
    private final Boolean m_ndiss;
    private List<CVotingAgentRB> m_voters = new ArrayList<>();
    private HashSet<CChairAgentRB> m_chairs = new HashSet<>();
    private HashSet<CGroupRB> m_groups = new HashSet<>();
    private final String m_name;
    private final int m_agNum;
    private int m_count;
    private IAgentConfiguration<CVotingAgentRB> m_configuration;
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
    private final AtomicLong m_timeout;
    private Object m_fileName;
    private int m_chairNum;
    private CChairAgentRB.CChairAgentGenerator m_chairagentgenerator;
    private final InputStream m_chairstream;
    private final int m_comsize;
    private final String m_rule;
    // TODO lining limit for allowing agents to drive alone
    // HashMap for storing how often an agent had to leave a group
    private final HashMap<CVotingAgentRB, Long> m_lineHashMap = new HashMap<CVotingAgentRB, Long>();
    private final CEnvironmentRB m_environmentRB;
    private HashMap<String, Object> m_map = new HashMap<>();

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
     * @param p_sim
     * @throws Exception exception
     */
    public CBrokerAgentRB(final String p_broker,
                          @Nonnull final IAgentConfiguration p_configuration,
                          final int p_agNum,
                          final InputStream p_stream,
                          final InputStream p_chairstream,
                          final CEnvironmentRB p_environment,
                          final int p_altnum,
                          final String p_name,
                          final double p_joinThr,
                          final List<AtomicDoubleArray> p_prefList,
                          final int p_comsize, final String p_rule,
                          int p_sim, int p_run, Boolean p_ndiss ) throws Exception
    {
        super( p_configuration );
        m_broker = p_broker;
        m_agNum = p_agNum;
        m_stream = p_stream;
        m_chairstream = p_chairstream;
        m_environmentRB = p_environment;
        m_altnum = p_altnum;
        m_name = p_name;
        m_joinThr = p_joinThr;
        m_prefList = p_prefList;
        // TODO set via yaml
        m_capacity = 20;
        m_timeout = new AtomicLong( 30 );
        m_comsize = p_comsize;
        m_rule = p_rule;
        m_sim = p_sim;
        m_run = p_run;
        m_ndiss = p_ndiss;

        m_votingagentgenerator = new CVotingAgentRB.CVotingAgentGenerator( new CSendRB(), m_stream, m_environmentRB, m_altnum, m_name,
                                                                           m_joinThr, m_prefList, m_rule, m_sim, m_run, m_ndiss, m_comsize );
        m_chairagentgenerator = new CChairAgentRB.CChairAgentGenerator( m_chairstream, m_environmentRB, m_name, m_altnum, m_comsize, m_rule, m_sim, m_run);

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

    public HashMap map()
    {
        return m_map;
    }

    public synchronized void cleanUpChairs()
    {
        HashSet<CChairAgentRB> m_removeChairs = new HashSet<>();

        final Iterator<CChairAgentRB> l_iterator = m_chairs.iterator();

        while ( l_iterator.hasNext() )
        {
            final CChairAgentRB l_chair = l_iterator.next();
            if ( l_chair.empty() )
                m_removeChairs.add( l_chair );


        }

        // remove chairs with empty groups

        final Iterator<CChairAgentRB> l_removeIterator = m_removeChairs.iterator();

        while ( l_removeIterator.hasNext() )
        {
            m_chairs.remove( l_removeIterator.next() );
        }

        m_map.put( "chairNum", m_chairs.size() );

    }

    @IAgentActionFilter
    @IAgentActionName( name = "create/ag" )
    private CVotingAgentRB createAgent( final Number p_createdNum ) throws Exception
    {
        System.out.println( "voters generated so far: " + p_createdNum );

        final CVotingAgentRB l_testvoter = m_votingagentgenerator.generatesinglenew();

        System.out.println( "new voter:" + l_testvoter.name() );

        m_voters.add( l_testvoter );

        String l_name = l_testvoter.name();

        // add voter entity to database

        EDataDB.INSTANCE.addVoter(l_name, m_run, m_sim);

        return l_testvoter;

    }

    @IAgentActionFilter
    @IAgentActionName( name = "assign/group" )
    private synchronized void assignGroup( final CVotingAgentRB p_votingAgent ) throws Exception
    {
        System.out.println( "Assigning group to " + p_votingAgent.name() );
        for ( final CGroupRB l_group : m_groups )
        {
            System.out.println( "group " + l_group.id() + " open: " + l_group.open() );
            // only add agents to group if group is open and chair did not reach its timeout
            if ( l_group.open() && !l_group.timedout() )
            {
                l_group.add( p_votingAgent );
                // increase lining counter of ag
                m_lineHashMap.put( p_votingAgent, p_votingAgent.liningCounter() );

                p_votingAgent.storeLC();
                System.out.println( "Adding agent " + p_votingAgent.name() + " to existing group" + ", ID " + l_group.id() );

                // add new group entity to database

                l_group.setDB( EDataDB.INSTANCE.newGroup(l_group.chair().name(), l_group.getDB(), l_group.getVoters(), m_run, m_sim ) );

                p_votingAgent.beliefbase().add( CLiteral.from( "mygroup", CRawTerm.from( l_group ) ) );
                p_votingAgent.beliefbase().add( CLiteral.from( "mychair", CRawTerm.from( l_group.chair() ) ) );
           //     m_chairs.add( l_group.chair() );
                return;
            }
        }

        final CChairAgentRB l_chairAgent = m_chairagentgenerator.generatesinglenew();

        // if there was no available group, create a new group

        final CGroupRB l_group = new CGroupRB( p_votingAgent, l_chairAgent, m_groupNum++, m_capacity, m_timeout );
        // increase lining counter of ag
        m_lineHashMap.put( p_votingAgent, p_votingAgent.liningCounter() );
        p_votingAgent.storeLC();

        m_groups.add( l_group );
        System.out.println( "Creating new group with agent " + p_votingAgent.name() + ", ID " + l_group.id() + ", timeout " + m_timeout );

        p_votingAgent.beliefbase().add( CLiteral.from( "mygroup", CRawTerm.from( l_group ) ) );
        p_votingAgent.beliefbase().add( CLiteral.from( "mychair", CRawTerm.from( l_chairAgent ) ) );

        m_chairs.add( l_chairAgent );

        // create group in database
        l_group.setDB( EDataDB.INSTANCE.addGroup( l_chairAgent.name(), p_votingAgent.name(), m_run, m_sim ) );
      //  m_map.put( "chairNum", m_chairs.size() );


    }


    @IAgentActionFilter
    @IAgentActionName( name = "decrement/counters" )
    private synchronized void decrementCounters() throws Exception
    {

        for ( final CGroupRB l_group : m_groups )
        {
            l_group.decrementCounter();
        }

    }

    @IAgentActionFilter
    @IAgentActionName( name = "update/groups" )
    private synchronized void updateGroups() throws Exception
    {

        for ( final CGroupRB l_group : m_groups )
        {
            // if all voters have submitted their votes, there is nothing to check, group is clean

            if ( l_group.areVotesSubmitted() )
                l_group.chair().beliefbase().add(
                    CLiteral.from(
                        "cleangroup",
                        CRawTerm.from( 1 )
                    )
                );

            else  if ( l_group.timedout() )

                // remove voters from group who didn't vote/whose votes didn't reach the chair
            {

                final CopyOnWriteArrayList<String> l_toRemoveList = new CopyOnWriteArrayList();
                final CopyOnWriteArrayList<CVotingAgentRB> l_toRemoveAgents = new CopyOnWriteArrayList();
                l_group.agents().filter( i -> !l_group.chair().voters().contains( i ) )
                       .forEach( j ->
                       {
                           l_toRemoveList.add( j.name() );
                           l_toRemoveAgents.add( j );
                           m_lineHashMap.put( j, j.liningCounter() );
                           System.out.println( j.name() + " needs to be removed" );
                       } );


                l_group.removeAll( l_toRemoveList );

                // "re-queue" removed voters

                l_toRemoveAgents.parallelStream().forEach(
                        i -> {
                            try
                            {
                                this.removeAndAddAg( i );
                            }
                            catch (SQLException e)
                            {
                                e.printStackTrace();
                            }
                        }
                );

                // set belief in chair that group was "cleaned up"

                l_group.chair().beliefbase().add(
                    CLiteral.from(
                        "cleangroup",
                        CRawTerm.from( 1 )
                    )
                );

                // if agents have been removed, add new group entry to database
                if ( l_toRemoveList.size() > 0 )
                    l_group.setDB( EDataDB.INSTANCE.newGroup(l_group.chair().name(), l_group.getDB(), l_group.getVoters(), m_run, m_sim ) );

            }
        }
    }

    /**
     * add Ag
     * @param p_Ag agent
     */

    public void removeAndAddAg( final CVotingAgentRB p_Ag ) throws SQLException {
        p_Ag.trigger( CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                        "leftgroup" )
                )
        );


        System.out.println( "adding Agent " + p_Ag.name() );

        this.beliefbase().add(
                CLiteral.from(
                        "newag",
                        CRawTerm.from( p_Ag ),
                        CRawTerm.from( m_lineHashMap.get( p_Ag ) )
                )
        );
    }

    public boolean allSleeping()
    {
        AtomicBoolean l_sleep = new AtomicBoolean( true );
        this.agentstream().forEach( i->
        {
            if ( !i.sleeping() ) l_sleep.set( false );
        });
        return l_sleep.get();
    }

    /**
     * Class CBrokerAgentGenerator
     */
    public static class CBrokerAgentGenerator extends IBaseAgentGenerator<CBrokerAgentRB>
    {

        /**
         * Store reference to send action to registered agents upon creation.
         */
        private final CSendRB m_send;
        private final int m_agNum;
        private int m_count;
        private final InputStream m_stream;
        private final CEnvironmentRB m_environment;
        private final String m_name;
        private final double m_joinThr;
        private final List<AtomicDoubleArray> m_prefList;
        private final InputStream m_chairstream;
        private int m_altnum;
        private int m_comsize;
        private String m_rule;
        private int m_sim;
        private int m_run;
        private Boolean m_ndiss;

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
         * @param p_sim
         * @param p_run
         * @param p_ndiss
         * @throws Exception exception
         */

        public CBrokerAgentGenerator(final CSendRB p_send,
                                     final FileInputStream p_brokerStream,
                                     final int p_agNum,
                                     final InputStream p_stream,
                                     final InputStream p_chairStream,
                                     final CEnvironmentRB p_environment,
                                     final int p_altnum,
                                     final String p_name,
                                     final double p_joinThr,
                                     final List<AtomicDoubleArray> p_prefList,
                                     final int p_comsize,
                                     final String p_rule,
                                     int p_sim, int p_run, Boolean p_ndiss ) throws Exception
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
                            CCommon.actionsFromAgentClass( CBrokerAgentRB.class ),
                            // add VotingAgent related external actions
                            Stream.of(
                                p_send
                            )
                        )
                        // build the set with a collector
                    ).collect( Collectors.toSet() ) );

            System.out.println( "actions defined in broker class: " + CCommon.actionsFromAgentClass( CBrokerAgentRB.class ).collect( Collectors.toSet() ) );


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
            m_rule = p_rule;
            m_sim = p_sim;
            m_run = p_run;
            m_ndiss = p_ndiss;

        }

        @Nullable
        @Override
        public CBrokerAgentRB generatesingle( @Nullable final Object... p_data )
        {
            CBrokerAgentRB l_broker = null;
            try
            {
                l_broker = new CBrokerAgentRB(

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
                    m_comsize,
                    m_rule,
                    m_sim,
                    m_run,
                    m_ndiss );
            }
            catch ( final Exception l_ex )
            {
                l_ex.printStackTrace();
            }

            return l_broker;
        }


    }

}
