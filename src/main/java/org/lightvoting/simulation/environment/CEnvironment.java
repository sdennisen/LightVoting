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

package org.lightvoting.simulation.environment;

import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.rule.CMinisumApproval;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerArray;


/**
 * Created by sophie on 22.02.17.
 * Environment class
 */
public final class CEnvironment
{

    // TODO set variables via config file or commando line
    // TODO use m_protocol?

    private String m_protocol = "BASIC";

 // private String m_grouping = "RANDOM";

    private String m_grouping = "COORDINATED";

    private final HashMap<CChairAgent, int[]> m_groupResults = new HashMap<>();
    /**
     * group capacity
     */

    private final int m_capacity = 3;

    // private final AtomicReferenceArray<CVotingAgent> m_group;

    /**
     * Set of voting agents
     */

    private final Set<CVotingAgent> m_agents;

    /**
     * Map for storing agent groups
     */

    private final Map<CChairAgent, List<CVotingAgent>> m_chairgroup;

    private final Map<CChairAgent, List> m_voteSets;

    /**
     * List for storing current active chairs, i.e. chairs who accept more agents
     */

    private final List<CChairAgent> m_activechairs;

    /**
     * maximum size
     */
    private final int m_size;


    /**
     * constructor
     *
     * @param p_size number of agents
     */
    public CEnvironment( final int p_size )
    {
        m_size = p_size;

        //  m_group = new AtomicReferenceArray<CVotingAgent>( new CVotingAgent[m_size] );
        m_agents = new HashSet<>();
        m_chairgroup = new HashMap<>();
        m_activechairs = new LinkedList<>();
        m_voteSets = new HashMap<CChairAgent, List>();

    }

    /**
     * initialize groups
     *
     * @param p_votingAgent agent
     *
     */
    public final void initialset( final CVotingAgent p_votingAgent )
    {
        m_agents.add( p_votingAgent );
    }

    /**
     * open a new group
     *
     * @param p_votingAgent voting opening the group
     */

    public final void openNewGroup( final CVotingAgent p_votingAgent )
    {
        final ITrigger l_triggerChair = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "myGroup",
                CLiteral.from( p_votingAgent.name() )
            )
        );

        p_votingAgent.getChair().trigger( l_triggerChair );


        final List l_list = new LinkedList<CVotingAgent>();
        l_list.add( p_votingAgent );

        m_chairgroup.put( p_votingAgent.getChair(), l_list );
        m_activechairs.add( p_votingAgent.getChair() );

        final List l_initalList = new LinkedList<AtomicIntegerArray>();
        m_voteSets.put( p_votingAgent.getChair(), l_initalList );

/*        final ITrigger l_trigger = CTrigger.from(

        final List l_initalList = new LinkedList<AtomicIntegerArray>();
        m_voteSets.put( p_votingAgent.getChair(), l_initalList );


        final ITrigger l_trigger = CTrigger.from(

            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "new/group/opened",
                CLiteral.from( p_votingAgent.name() ),
                CLiteral.from( ( p_votingAgent.getChair() ).toString() )
            )
        );


        // trigger all agents and tell them that the group was opened

        m_agents
            .parallelStream()

            .forEach( i -> i.trigger( l_trigger ) );*/

       //     .forEach( i -> i.trigger( l_trigger ) );

  /*      // TODO this is only for testing, here each agent looks for a group as soon as agent 0 opened its group. Needs to be rewritten for general case
        if ( m_chairgroup.size() == 1 )
        {
            final ITrigger l_triggerJoin = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "lookforgroup" )

            );


            // trigger all agents and tell them to choose one of the available groups

            m_agents
                .parallelStream()
                .forEach( i -> i.trigger( l_triggerJoin ) );
        }*/


    }

    /**
     * join a group
     *
     * @param p_votingAgent voting agent joining a group
     * @return the chair of the group
     */

    public final CChairAgent joinGroup( final CVotingAgent p_votingAgent )
    {

        if ( "RANDOM".equals( m_grouping ) )
            return this.joinRandomGroup( p_votingAgent );

        if ( "COORDINATED".equals( m_grouping ) )
            return this.joinGroupCoordinated( p_votingAgent );
        return null;
    }


    private final CChairAgent joinRandomGroup( final CVotingAgent p_votingAgent )
    {
        if ( m_activechairs.size() == 0 )
        {
            this.openNewGroup( p_votingAgent );
            System.out.println( p_votingAgent.name() + " opened group with chair " + p_votingAgent.getChair() );
            return p_votingAgent.getChair();
        }

        // choose random group to join

        final Random l_rand = new Random();
        final CChairAgent l_randomChair = m_activechairs.get( l_rand.nextInt( m_activechairs.size() ) );

        if ( this.containsnot( l_randomChair, p_votingAgent ) )
        {

            m_chairgroup.get( l_randomChair ).add( p_votingAgent );
            System.out.println( p_votingAgent.name() + " joins group with chair " + l_randomChair );

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
        }

        // if the agent is already in the group, just return the chair agent
        else
        {
            return l_randomChair;
        }

            //            final ITrigger l_trigger = CTrigger.from(
            //                ITrigger.EType.ADDGOAL,
            //                CLiteral.from(
            //                    "joined/group",
            //                    CLiteral.from( p_votingAgent.name() ),
            //                    CLiteral.from( l_randomChair.toString() )
            //                )
            //            );
            //
            //            // trigger all agents and tell them that the agent joined a group
            //            m_agents
            //                .parallelStream()
            //                .forEach( i -> i.trigger( l_trigger ) );


            // if it was not possible to join a group, open a new group

//        this.openNewGroup( p_votingAgent );
//        System.out.println( p_votingAgent.name() + " opened group with chair " + p_votingAgent.getChair() );
//        return p_votingAgent.getChair();

    }

    private final CChairAgent joinGroupCoordinated( final CVotingAgent p_votingAgent )
    {
        if ( m_activechairs.size() == 0 )
        {
            this.openNewGroup( p_votingAgent );
            System.out.println( p_votingAgent.name() + " opened group with chair " + p_votingAgent.getChair() );
            return p_votingAgent.getChair();
        }

        // choose group to join

        final HashMap<CChairAgent, Integer> l_groupDistances = new HashMap<>();

      //  for ( int i = 0; i < m_activechairs.size(); i++ )

      /*      final BitVector l_bitVote = new BitVector( p_altNum );

        for ( int j = 0; j < p_altNum; j++ )
        {
            l_bitVote.put( j, l_booleanVote[j] );
        }

        final BitVector l_curBitCom = l_bitCom.copy();

        l_curBitCom.xor( l_bitVote );

        final int l_curHD = l_curBitCom.cardinality();*/

//        final CChairAgent l_randomChair = m_activechairs.get( l_rand.nextInt( m_activechairs.size() ) );
//
//
//
//        if ( this.containsnot( l_randomChair, p_votingAgent ) )
//        {
//
//            m_chairgroup.get( l_randomChair ).add( p_votingAgent );
//            System.out.println( p_votingAgent.name() + " joins group with chair " + l_randomChair );
//
//            if ( m_chairgroup.get( l_randomChair ).size() == m_capacity )
//            {
//
//                m_activechairs.remove( l_randomChair );
//
//                for ( int i = 0; i < m_capacity; i++ )
//                    System.out.println( m_chairgroup.get( l_randomChair ).get( i ).name() + " with chair " + l_randomChair );
//
//                System.out.println( "trigger election " );
//
//                final ITrigger l_triggerStart = CTrigger.from(
//                    ITrigger.EType.ADDGOAL,
//                    CLiteral.from(
//                        "start/criterion/fulfilled" )
//
//                );
//
//                l_randomChair.trigger( l_triggerStart );
//
//            }
//            return l_randomChair;
//        }

        this.openNewGroup( p_votingAgent );
        System.out.println( p_votingAgent.name() + " opened group with chair " + p_votingAgent.getChair() );
        return p_votingAgent.getChair();

    }


    private boolean containsnot( final CChairAgent p_randomChair, final CVotingAgent p_votingAgent )
    {
        for ( int i = 0; i < m_chairgroup.get( p_randomChair ).size(); i++ )
            if ( m_chairgroup.get( p_randomChair ).get( i ).name().equals( p_votingAgent.name() ) )
            {
                return false;
            }

        return true;
    }


    public final int size()
    {
        return m_size;
    }

    /**
     * start the election, i.e. ask the travellers
     * @param p_chairAgent the chair starting the election
     */
    public void startElection( final CChairAgent p_chairAgent )
    {
        final ITrigger l_trigger = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "submit/your/vote", CRawTerm.from( p_chairAgent  ) )
        );

        final List<CVotingAgent> l_agents = m_chairgroup.get( p_chairAgent );

        l_agents.forEach( i -> i.trigger( l_trigger ) );

    }

    // TODO migrate to CVote ?

    /**
     * submit vote to chair
     * @param p_votingAgent voting agent
     * @param p_chairAgent chair agent
     */
    public void submitVote( final CVotingAgent p_votingAgent, final IBaseAgent<CChairAgent> p_chairAgent )
    {
        final AtomicIntegerArray l_vote = new AtomicIntegerArray( new int[] {1, 1, 1, 0, 0, 0} );
        System.out.println( "Agent " + p_votingAgent.name() + " sends vote " + l_vote + " to " + p_chairAgent.toString() );

        final ITrigger l_trigger = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "vote/received",
                CLiteral.from( p_votingAgent.name() ),
                //   CLiteral.from( p_chairAgent.toString() ),
                CRawTerm.from( l_vote )
            )
        );

        p_chairAgent.trigger( l_trigger );

    }

    /**
     * store vote submitted to chair
     * @param p_chairAgent chair agent to whom vote is submitted
     * @param p_votingAgent voting agent submitting the vote
     * @param p_vote submitted vote
     */

    public void storeVote( final CChairAgent p_chairAgent, final Object p_votingAgent, final AtomicIntegerArray p_vote )
    {

        m_voteSets.get( p_chairAgent ).add( p_vote );
        if ( ( m_voteSets.get( p_chairAgent ) ).size() == m_capacity )
        {
            System.out.println( " All voters submitted their votes" );
            final ITrigger l_trigger = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "all/votes/received" )

            );

            p_chairAgent.trigger( l_trigger );
        }
    }

    // TODO move computeResult() to chair class or chair action

    /**
     * compute result of election
     * @param p_chairAgent responsible chair
     */

    public void computeResult( final CChairAgent p_chairAgent )
    {
        System.out.println( "Computing result " );
        final CMinisumApproval l_minisumApproval = new CMinisumApproval();

        final List<String> l_alternatives = new LinkedList<>();

        // TODO remove ugly hack

        for ( char l_char: "ABCDEF".toCharArray() )

            l_alternatives.add( String.valueOf( l_char ) );

        // TODO specify comsize via config file

        System.out.println( " Alternatives: " + l_alternatives );

        System.out.println( " Votes: " + m_voteSets.get( p_chairAgent ) );

        final int[] l_comResult = l_minisumApproval.applyRule( l_alternatives, m_voteSets.get( p_chairAgent ), 3 );

        m_groupResults.put( p_chairAgent, l_comResult );

        System.out.println( " Result of election: " + Arrays.toString( l_comResult ) );

        // broadcast result

        final ITrigger l_trigger = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "election/result",
                CLiteral.from( p_chairAgent.toString() ),
                CRawTerm.from( Arrays.toString( l_comResult ) )
            )
        );

        m_agents.stream().forEach( i -> i.trigger( l_trigger ) );

    }
}
