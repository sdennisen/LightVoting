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

import cern.colt.Arrays;
import cern.colt.bitvector.BitVector;
import com.google.common.util.concurrent.AtomicDoubleArray;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.beliefbase.IBeliefbase;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.environment.CEnvironment;
import org.lightvoting.simulation.environment.CGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;


/**
 * BDI agent with voting capabilities.
 */
// annotation to mark the class that actions are inside
@IAgentAction
public final class CVotingAgent extends IBaseAgent<CVotingAgent>
{

    /**
     * name of the agent
     */
    private final String m_name;

    /**
     * environment
     */
    private final CEnvironment m_environment;

    /**
     * associated chair agent;
     */
    private CChairAgent m_chair;

    /**
     * agent's vote
     */

    private AtomicIntegerArray m_vote;

    /**
     * number of alternatives
     */
    private final int m_altNum;

    /**
     * agent's preferences
     */
    private final AtomicDoubleArray m_atomicPrefValues;

    // TODO define via config file
    /**
     * grouping algorithm: "RANDOM" or "COORDINATED"
     */
    private String m_grouping;

    /**
     * variable indicating if agent already submitted its vote
     */

    private boolean m_voted;

    // TODO define via config file

    private Integer m_joinThreshold;

    /**
     * constructor of the agent
     *
     * @param p_name name of the agent
     * @param p_configuration agent configuration of the agent generator
     * @param p_chairagent corresponding chair agent
     * @param p_environment environment reference
     * @param p_altNum number of alternatives
     * @param p_grouping grouping algorithm
     */

    public CVotingAgent( final String p_name, final IAgentConfiguration<CVotingAgent> p_configuration, final IBaseAgent<CChairAgent> p_chairagent,
                         final CEnvironment p_environment,
                         final int p_altNum,
                         final String p_grouping
    )
    {
        super( p_configuration );
        m_name = p_name;
        m_environment = p_environment;

        m_chair = (CChairAgent) p_chairagent;
        m_storage.put( "chair", p_chairagent.raw() );

        m_beliefbase.add(
            CLiteral.from(
                "chair",
                CRawTerm.from( p_chairagent )
            )
        );

        // sleep chair, Long.MAX_VALUE -> inf
        p_chairagent.sleep( Long.MAX_VALUE );

        m_altNum = p_altNum;

        m_atomicPrefValues = this.generatePreferences( m_altNum );
        m_vote = this.convertPreferences( m_atomicPrefValues );
        m_voted = false;
        m_joinThreshold = 5;
        m_grouping = p_grouping;
    }

    public CEnvironment getEnvironment()
    {
        return m_environment;
    }

    private AtomicIntegerArray convertPreferences( final AtomicDoubleArray p_atomicPrefValues )
    {
        final int[] l_voteValues = new int[m_altNum];
        for ( int i = 0; i < m_altNum; i++ )
            if ( p_atomicPrefValues.get( i ) > 0.5 )
                l_voteValues[i] = 1;
            else
                l_voteValues[i] = 0;
        System.out.println( "Vote: " + Arrays.toString( l_voteValues ) );
        return new AtomicIntegerArray( l_voteValues );
    }

    private AtomicDoubleArray generatePreferences( final int p_altNum )
    {
        final Random l_random = new Random();
        final double[] l_prefValues = new double[m_altNum];
        for ( int i = 0; i < m_altNum; i++ )
            l_prefValues[i] = this.sigmoidValue( l_random.nextDouble() - 0.5 );
        System.out.println( "Preference Values: " + Arrays.toString( l_prefValues ) );
        return new AtomicDoubleArray( l_prefValues );
    }

    private double sigmoidValue( double p_var )
    {
        return 1 / ( 1 + Math.pow( Math.E, -1 * p_var ) );
    }

    // overload agent-cycle
    @Override
    public final CVotingAgent call() throws Exception
    {
        // run default cycle
        return super.call();
    }

    /**
     * compute dissatisfaction of voter with given committee
     *
     * @param p_resultValues committee
     * @return dissatisfaction with committee
     */

    public double computeDiss( final int[] p_resultValues )
    {
        double l_diss = 0;

        for ( int i = 0; i < p_resultValues.length; i++ )
        {
            if ( p_resultValues[i] == 1 )
                l_diss = l_diss + ( 1 - m_atomicPrefValues.get( i ) );
        }
        return l_diss;
    }


    @IAgentActionFilter
    @IAgentActionName( name = "perceive/env" )
    private void perceiveEnv()
    {
        this.beliefbase().add( m_environment.literal( this ) );
        System.out.println( this.name() + " perceived environment " );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "join/group" )
    private void joinGroup()
    {
        if ( "RANDOM".equals( m_grouping ) )
            this.joinGroupRandom();

        if ( "COORDINATED".equals( m_grouping ) )
            this.joinGroupCoordinated();
    }

    private List<CGroup> determineActiveGroups()
    {
        final IBeliefbase l_bb = m_beliefbase.beliefbase();
        final AtomicReference<List<CGroup>> l_groupList = new AtomicReference<>();

        final Collection l_groups = l_bb.literal( "groups" );
        l_groups.stream().forEach( i ->
                                       l_groupList.set( ( (ILiteral) i ).values().findFirst().get().raw() ) );

        final List<CGroup> l_activeGroups = new LinkedList<>();

        for ( int i = 0; i < l_groupList.get().size(); i++ )
            if ( l_groupList.get().get( i ).open() )
            {
                l_activeGroups.add( l_groupList.get().get( i ) );
            }

        return l_activeGroups;
    }

    private void openNewGroup()
    {
        final CGroup l_group;

        if ( "RANDOM".equals( m_grouping ) )
            l_group = m_environment.openNewGroupRandom( this );

        else
            l_group = m_environment.openNewGroupCoordinated( this );

        this.beliefbase().add( l_group.literal( this ) );
        System.out.println( "opened new group " + l_group );
    }

    private void joinGroupRandom()
    {

        final List<CGroup> l_activeGroups = this.determineActiveGroups();

        if ( l_activeGroups.isEmpty() )
        {
            this.openNewGroup();
            return;
        }

        final Random l_rand = new Random();

        final CGroup l_randomGroup = l_activeGroups.get( l_rand.nextInt( l_activeGroups.size() ) );
        m_environment.addAgentRandom( l_randomGroup, this );
        this.beliefbase().add( l_randomGroup.literal( this ) );

    }

    private void joinGroupCoordinated()
    {
        System.out.println( "join group according to coordinated grouping algorithm" );

        final List<CGroup> l_activeGroups = this.determineActiveGroups();

        if ( l_activeGroups.isEmpty() )
        {
            this.openNewGroup();
            return;
        }

        else
            this.determineGroupCoordinated( l_activeGroups );
    }

    private void determineGroupCoordinated( final List<CGroup> p_activeGroups )
    {
        final CGroup l_group;
        // choose group to join
        final Map<CGroup, Integer> l_groupDistances = new HashMap<>();
        final AtomicIntegerArray l_vote = this.getVote();
        System.out.println( "Vote: " + l_vote );
        for ( int i = 0; i < p_activeGroups.size(); i++ )
        {
            final AtomicIntegerArray l_com = new AtomicIntegerArray( p_activeGroups.get( i ).result() );
            System.out.println( "Committee: " + l_com );

            // TODO own class for Hamming distance computation
            final BitVector l_bitVote = new BitVector( l_vote.length() );
            final BitVector l_bitCom = new BitVector( l_vote.length() );
            for ( int j = 0; j < l_vote.length(); j++ )
                l_bitVote.put( j, l_vote.get( j ) == 1 );
            System.out.println( "Vote: " + l_bitVote );
            for ( int j = 0; j < l_vote.length(); j++ )
                l_bitCom.put( j, l_com.get( j ) == 1 );
            System.out.println( "Committee: " + l_bitCom );
            l_bitCom.xor( l_bitVote );
            final int l_HD = l_bitCom.cardinality();
            System.out.println( "Hamming distance: " + l_HD );
            l_groupDistances.put( p_activeGroups.get( i ), l_HD );
        }
        final Map l_sortedDistances = this.sortMapDESC( l_groupDistances );
        final Map.Entry<CGroup, Integer> l_entry = (Map.Entry<CGroup, Integer>) l_sortedDistances.entrySet().iterator().next();
        l_group = l_entry.getKey();

        // if Hamming distance is above the threshold, do not join the chair but create a new group
        if ( l_entry.getValue() > m_joinThreshold )
        {
            this.openNewGroup();
            return;
        }
        m_environment.addAgentCoordinated( l_group, this );
        this.beliefbase().add( l_group.literal( this ) );
        System.out.println( this.name() + " joins group " + l_group );
    }


    private Map sortMapDESC( final Map<CGroup, Integer> p_valuesMap )
    {
        final List<Map.Entry<CGroup, Integer>> l_list = new LinkedList<>( p_valuesMap.entrySet() );

        /* Sorting the list based on values in descending order */

        Collections.sort( l_list, ( p_first, p_second ) ->
            p_second.getValue().compareTo( p_first.getValue() ) );

        /* Maintaining insertion order with the help of LinkedList */

        final Map<CGroup, Integer> l_sortedMap = new LinkedHashMap<>();
        for ( final Map.Entry<CGroup, Integer> l_entry : l_list )
        {
            l_sortedMap.put( l_entry.getKey(), l_entry.getValue() );
        }

        return l_sortedMap;
    }



    /**
     * Get agent's name
     *
     * @return name of agent
     */
    public final String name()
    {
        return m_name;
    }

    /**
     * get associated chair agent
     *
     * @return chair agent
     */
    public CChairAgent getChair()
    {
        return m_chair;
    }


    public AtomicIntegerArray getVote()
    {
        return m_vote;
    }


    @IAgentActionFilter
    @IAgentActionName( name = "submit/vote" )
    private void submitVote( final CChairAgent p_chairAgent )
    {
        if ( !m_voted )
        {
            final ITrigger l_trigger = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "vote/received",
                    CLiteral.from( this.name() ),
                    CRawTerm.from( this.getVote() )
                )
            );

            p_chairAgent.trigger( l_trigger );

            m_voted = true;
        }
    }

    @IAgentActionFilter
    @IAgentActionName( name = "submit/dissatisfaction" )
    private void submitDiss( final CChairAgent p_chairAgent, final Integer p_iteration, final int[] p_result )
    {

        final Double l_diss = this.computeDiss( p_result );

        //        final ITrigger l_trigger = CTrigger.from(
        //                ITrigger.EType.ADDGOAL,
        //                CLiteral.from( "diss/received",
        //                               CRawTerm.from( this ),
        //                               CRawTerm.from( l_diss ),
        //                               CRawTerm.from( p_iteration )
        //                )
        //            );
        //   p_chairAgent.trigger( l_trigger );


        final ILiteral l_literal = CLiteral.from(
            "diss/received",
            CRawTerm.from( this ),
            CRawTerm.from( l_diss ),
            CRawTerm.from( p_iteration )

        );

        p_chairAgent.beliefbase().add( l_literal );

        System.out.println( this.name() + " submitted diss" );
    }




}

// XXXXXXXXXXX Old code XXXXXXXXXXXXXXXXXXXXX

// m_vote = new AtomicIntegerArray( new int[] {1, 1, 1, 0, 0, 0} );

// ---- Test in computeDiss() ------

//        final double l_random = ThreadLocalRandom.current().nextDouble( 0, 10 );
//        return l_random;
//        return 1;

// TODO if necessary, reinsert in joinGroupRandom()

/*  System.out.println( ".................. print group reference " + i );
    System.out.println( "Contents of group literal" + ( (ILiteral) i ).values().findFirst().get().raw() );
    System.out.println( "Class " + ( (ILiteral) i ).values().findFirst().get().raw().getClass() );*/

// TODO tip from Malte for filtering

/*m_groups.parallelStream()
    .filter( i -> i.open() )
    .min( (g1, g2) -> Double.compare( g1.satisfaction(this), g2.satisfaction(this)) )
    .get();*/

// TODO tip from Malte for filtering
// m_groups.get( l_random.nextInt( m_groups.size() ) );

//


        /*if ( m_activechairs.size() == 0 )
        {
            this.openNewGroup( p_votingAgent );
            this.wakeUpAgent();
            return p_votingAgent.getChair();

        }

        // choose random group to join

        final Random l_rand = new Random();
        final CChairAgent l_randomChair = m_activechairs.get( l_rand.nextInt( m_activechairs.size() ) );

        if ( this.containsnot( l_randomChair, p_votingAgent ) )
        {

            m_chairgroup.get( l_randomChair ).add( p_votingAgent );
            System.out.println( p_votingAgent.name() + " joins group with ID " + m_groupIds.get( l_randomChair ) );
            // chair " + l_randomChair );

            this.wakeUpAgent();

            if ( m_chairgroup.get( l_randomChair ).size() == m_capacity )
            {

                m_activechairs.remove( l_randomChair );

                for ( int i = 0; i < m_capacity; i++ )
                    System.out.println( m_chairgroup.get( l_randomChair ).get( i ).name() + " with chair " + l_randomChair );

                System.out.println( "trigger election " );

                final ITrigger l_triggerStart = CTrigger.from(
                    ITrigger.EType.ADDGOAL,
                    CLiteral.from(
                        "start/criterion/fulfilled" )

                );

                l_randomChair.trigger( l_triggerStart );

            }

            return l_randomChair;
        }*/



/*    @IAgentActionFilter
    @IAgentActionName( name = "env/open/new/group" )
    private void envOpenNewGroup( final IBaseAgent<CChairAgent> p_chairagent )
    {
        // wake up in next cycle
     //   p_chairagent.sleep( 0 );
     //   p_chairagent.wakeup();
        m_environment.openNewGroup( this );
        System.out.println( this.name() + " opened new group with chair " + p_chairagent );

        // hier könnten dann auch gleich die nötigen trigger in den chair-agent gepusht werden wenn die gruppe aufgemacht wird

    }*/

/*    @IAgentActionFilter
    @IAgentActionName( name = "env/submit/vote" )
    private void submitVote( final IBaseAgent<CChairAgent> p_chairAgent )
    {
        m_environment.submitVote( this, p_chairAgent );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "env/join/group" )
    private void envJoinGroup( )
    {
        final CChairAgent l_chairAgent =  m_environment.joinGroup( this );
   //     System.out.println( this.name() + " joined group with chair " + l_chairAgent );
    }*/

/*    @IAgentActionFilter
    @IAgentActionName( name = "env/submit/dissatisfaction" )
    private void submitDiss( final IBaseAgent<CChairAgent> p_chairAgent, final int p_iteration )
    {
        System.out.println( "Trying to submit diss for iteration " + p_iteration );
        m_environment.submitDiss( this, p_chairAgent, p_iteration );
    }*/

//    /**
//     * perceive result
//     */
//    @IAgentActionFilter
//    @IAgentActionName( name = "perceive/result" )
//    /**
//     * update literal for result
//     */
//    public void perceiveResult()
//    {
//        if ( !( m_environment.detectResult( this ) == null ) )
//            this.beliefbase().add( m_environment.detectResult( this ) );
//    }
