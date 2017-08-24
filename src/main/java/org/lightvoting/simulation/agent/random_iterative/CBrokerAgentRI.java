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

package org.lightvoting.simulation.agent.random_iterative;

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
import org.lightvoting.simulation.action.message.random_iterative.CSendRI;
import org.lightvoting.simulation.environment.random_iterative.CEnvironmentRI;
import org.lightvoting.simulation.environment.random_iterative.CGroupRI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by sophie on 18.07.17.
 */
@IAgentAction
public class CBrokerAgentRI extends IBaseAgent<CBrokerAgentRI>
{
    private static final long serialVersionUID = -5422798855948273749L;

    private List<CVotingAgentRI> m_voters = new ArrayList<>();
    private HashSet<CChairAgentRI> m_chairs = new HashSet<>();
    private HashSet<CGroupRI> m_groups = new HashSet<>();
    private final String m_name;
    private final int m_agNum;
    private int m_count;
    private IAgentConfiguration<CVotingAgentRI> m_configuration;
    private final InputStream m_stream;
    private CEnvironmentRI m_environment;
    private int m_altnum;
    private double m_joinThr;
    private List<AtomicDoubleArray> m_prefList;
    private final String m_broker;
    private CVotingAgentRI.CVotingAgentGenerator m_votingagentgenerator;
    private int m_groupNum;
    // TODO read via yaml
    private int m_capacity;
    // TODO read via yaml
    private long m_timeout;
    private Object m_fileName;
    private int m_chairNum;
    private CChairAgentRI.CChairAgentGenerator m_chairagentgenerator;
    private final InputStream m_chairstream;
    private final int m_comsize;
    // TODO lining limit for allowing agents to drive alone
    // HashMap for storing how often an agent had to leave a group
    private final HashMap<CVotingAgentRI, Long> m_lineHashMap = new HashMap<>();
    private final double m_dissthr;
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
     * @param p_dissthr dissatisfaction threshold
     * @throws Exception exception
     */
    public CBrokerAgentRI( final String p_broker,
                           @Nonnull final IAgentConfiguration p_configuration,
                           final int p_agNum,
                           final InputStream p_stream,
                           final InputStream p_chairstream,
                           final CEnvironmentRI p_environment,
                           final int p_altnum,
                           final String p_name,
                           final double p_joinThr,
                           final List<AtomicDoubleArray> p_prefList,
                           final int p_comsize,
                           final double p_dissthr
    ) throws Exception
    {
        super( p_configuration );
        m_broker = p_broker;
        m_agNum = p_agNum;
        m_stream = p_stream;
        m_chairstream = p_chairstream;
        m_environment = p_environment;
        m_altnum = p_altnum;
        m_name = p_name;
        m_joinThr = p_joinThr;
        m_prefList = p_prefList;
        m_capacity = 5;
        m_timeout = 10;
        m_comsize = p_comsize;
        m_dissthr = p_dissthr;

        System.out.println( "Broker: dissthr: " + m_dissthr );

        m_votingagentgenerator = new CVotingAgentRI.CVotingAgentGenerator( new CSendRI(), m_stream, m_environment, m_altnum, m_name,
                                                                           m_joinThr, m_prefList );
        m_chairagentgenerator = new CChairAgentRI.CChairAgentGenerator( m_chairstream, m_environment, m_name, m_altnum, m_comsize, m_dissthr, this );

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

    public HashMap<String, Object> map()
    {
        return m_map;
    }

    @IAgentActionFilter
    @IAgentActionName( name = "create/ag" )
    private CVotingAgentRI createAgent( final Number p_createdNum ) throws Exception
    {

        System.out.println( "voters generated so far: " + p_createdNum );

        final CVotingAgentRI l_testvoter = m_votingagentgenerator.generatesinglenew();

        System.out.println( "new voter:" + l_testvoter.name() );

        m_voters.add( l_testvoter );

        return l_testvoter;

    }

    @IAgentActionFilter
    @IAgentActionName( name = "assign/group" )
    private synchronized void assignGroup( final CVotingAgentRI p_votingAgent ) throws Exception
    {
        System.out.println( "Assigning group to " + p_votingAgent.name() );
        for ( final CGroupRI l_group : m_groups )
        {
            System.out.println( "group " + l_group.id() + " open: " + l_group.open() );
            System.out.println( "group " + l_group.id() + " timeout " + l_group.chair().cycle() );
            // only add agents to group if group is open and chair did not reach its timeout
            if ( l_group.open() && !l_group.chair().timedout() && p_votingAgent.unknownGroup( l_group ) )
            {
                l_group.add( p_votingAgent, this.cycle() );
                System.out.println( "Adding agent " + p_votingAgent.name() + " to existing group" + ", ID " + l_group.id() );
                p_votingAgent.beliefbase().add( CLiteral.from( "mygroup", CRawTerm.from( l_group ) ) );
                p_votingAgent.addGroupID( l_group );
                p_votingAgent.beliefbase().add( CLiteral.from( "mychair", CRawTerm.from( l_group.chair() ) ) );
                m_chairs.add( l_group.chair() );
                return;
            }
        }

        final CChairAgentRI l_chairAgent = m_chairagentgenerator.generatesinglenew();

        // if there was no available group, create a new group

        final CGroupRI l_group = new CGroupRI( p_votingAgent, l_chairAgent, m_groupNum++, m_capacity, this.cycle(), m_timeout );
        m_groups.add( l_group );
        System.out.println( "Creating new group with agent " + p_votingAgent.name() + ", ID " + l_group.id() );

        p_votingAgent.beliefbase().add( CLiteral.from( "mygroup", CRawTerm.from( l_group ) ) );
        p_votingAgent.beliefbase().add( CLiteral.from( "mychair", CRawTerm.from( l_chairAgent ) ) );
        p_votingAgent.addGroupID( l_group );

        m_chairs.add( l_chairAgent );

        m_map.put( "chairs", this.asString( m_chairs ) );

    }

    private String asString( final HashSet<CChairAgentRI> p_chairs )
    {
        final Iterator<CChairAgentRI> l_chairIterator = p_chairs.iterator();

        String l_string = "{";

        CChairAgentRI l_current = l_chairIterator.next();

        while ( l_chairIterator.hasNext() )
        {
            l_string = l_string.concat( l_current.name() + ", " );
            l_current = l_chairIterator.next();
        }

        l_string = l_string.concat( l_current.name() + "}" );

        return l_string;
    }

    @IAgentActionFilter
    @IAgentActionName( name = "update/groups" )
    private synchronized void updateGroups() throws Exception
    {
        try
        {

            final CopyOnWriteArrayList<CGroupRI> l_cleanGroups = new CopyOnWriteArrayList<>();

            for ( final CGroupRI l_group : m_groups )
            {

//                if ( l_group.areVotesSubmitted() )
//                    l_cleanGroups.add( l_group );
//
//                else if ( l_group.chair().timedout() )

                if ( l_group.areVotesSubmitted() || l_group.chair().timedout() )

                {
                    // TODO test
                    // remove voters from group who didn't vote/whose votes didn't reach the chair
                    final CopyOnWriteArrayList<String> l_toRemoveList = new CopyOnWriteArrayList();
                    final CopyOnWriteArrayList<CVotingAgentRI> l_toRemoveAgents = new CopyOnWriteArrayList();
                    l_group.agents().filter( i -> !l_group.chair().voters().contains( i ) )
                           .forEach( j ->
                           {
                               l_toRemoveList.add( j.name() );
                               l_toRemoveAgents.add( j );
                               m_lineHashMap.put( j, j.liningCounter() );
                           } );
                    System.out.println( "toRemoveList:" + l_toRemoveList );

                    l_group.removeAll( l_toRemoveList );

                    // "re-queue" removed voters

                    l_toRemoveAgents.parallelStream().forEach( i ->
                        this.removeAndAddAg( i )   );

                                                               //this.beliefbase().add(

//                        CLiteral.from(
//                            "newag",
//                            CRawTerm.from( i ),
//                            CRawTerm.from( m_lineHashMap.get( i ) )
//                        )
                                                           //    )

                    l_cleanGroups.add( l_group );
                }







                    // set belief in chair that group was "cleaned up"

                l_cleanGroups.parallelStream().forEach(
                    i -> i.chair().beliefbase().add(
                        CLiteral.from(
                            "cleangroup",
                            CRawTerm.from( 1 ) )
                        )
                    );

                if ( l_group.areDissValsSubmitted() )
                {
                    System.out.println( "All diss vals are submitted" );
                }
                // if there are agents whose diss vals were not stored by the chair, remove them
                else if ( l_group.chair().dissTimedOut() && l_group.chair().waitingforDiss() )
                {
                    final CopyOnWriteArrayList<String> l_toRemoveList = new CopyOnWriteArrayList();
                    final CopyOnWriteArrayList<CVotingAgentRI> l_toRemoveAgents = new CopyOnWriteArrayList();
                    l_group.agents().filter( i -> !l_group.chair().dissvoters().contains( i ) )
                           .forEach(
                               j ->
                               {
                                   l_toRemoveList.add( j.name() );
                                   l_toRemoveAgents.add( j );
                                   m_lineHashMap.put( j, j.liningCounter() );
                               } );
                    System.out.println( "toRemoveList:" + l_toRemoveList );

                    l_group.removeAll( l_toRemoveList );

                    // "re-queue" removed voters

                    l_toRemoveAgents.parallelStream().forEach(
                        i -> this.removeAndAddAg( i )
                    );

                    l_group.chair().endWaitForDiss();

                }
            }

        }
        catch ( final ConcurrentModificationException l_ex )
        {
            System.out.println( "ConcurrentModificationException" );
            System.exit( 1 );
        }
    }

    /**
     * add Ag
     * @param p_Ag agent
     */

    public void removeAndAddAg( final CVotingAgentRI p_Ag )
    {
        p_Ag.trigger( CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "leftgroup" )
                             )
        );


        System.out.println( "adding Agent " + p_Ag.name() );
        // increase lining counter of ag
        m_lineHashMap.put( p_Ag, p_Ag.liningCounter() );

        this.beliefbase().add(
            CLiteral.from(
                "newag",
                CRawTerm.from( p_Ag ),
                CRawTerm.from( m_lineHashMap.get( p_Ag ) )
            )
        );
    }

    /**
     * Class CBrokerAgentGenerator
     */
    public static class CBrokerAgentGenerator extends IBaseAgentGenerator<CBrokerAgentRI>
    {

        /**
         * Store reference to send action to registered agents upon creation.
         */
        private final CSendRI m_send;
        private final int m_agNum;
        private int m_count;
        private final InputStream m_stream;
        private final CEnvironmentRI m_environment;
        private final String m_name;
        private final double m_joinThr;
        private final List<AtomicDoubleArray> m_prefList;
        private final InputStream m_chairstream;
        private int m_altnum;
        private int m_comsize;
        private double m_dissthr;

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
         * @param p_dissthr dissatisfaction threshold
         * @throws Exception exception
         */
        public CBrokerAgentGenerator( final CSendRI p_send,
                                      final FileInputStream p_brokerStream,
                                      final int p_agNum,
                                      final InputStream p_stream,
                                      final InputStream p_chairStream,
                                      final CEnvironmentRI p_environment,
                                      final int p_altnum,
                                      final String p_name,
                                      final double p_joinThr,
                                      final List<AtomicDoubleArray> p_prefList,
                                      final int p_comsize,
                                      final double p_dissthr
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
                            CCommon.actionsFromAgentClass( CBrokerAgentRI.class ),
                            // add VotingAgent related external actions
                            Stream.of(
                                p_send
                            )
                        )
                        // build the set with a collector
                    ).collect( Collectors.toSet() ) );

            System.out.println( "actions defined in broker class: " + CCommon.actionsFromAgentClass( CBrokerAgentRI.class ).collect( Collectors.toSet() ) );


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
            m_dissthr = p_dissthr;
        }

        @Nullable
        @Override
        public CBrokerAgentRI generatesingle( @Nullable final Object... p_data )
        {
            CBrokerAgentRI l_broker = null;
            try
            {
                l_broker = new CBrokerAgentRI(

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
                    m_dissthr
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
