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

import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * Created by sophie on 22.02.17.
 * Environment class
 */
public final class CEnvironment
{

    // private final AtomicReferenceArray<CVotingAgent> m_group;

    /**
     * Set of voting agents
     */

    private final Set<CVotingAgent> m_agents;

    /**
     * Map for storing agent groups
     */

    private final Map<CChairAgent, List<CVotingAgent>> m_chairgroup;

    /**
     * List for storing current active chairs, i.e. chairs who accept more agents
     */

    private final List<CChairAgent> m_activechairs;

    /**
     * maximum size
     */
    private final int m_size;


    private int m_capacity = 3;

    /**
     *constructor
     * @param p_size number of agents
     */
    public CEnvironment( final int p_size )
    {
        m_size = p_size;
      //  m_group = new AtomicReferenceArray<CVotingAgent>( new CVotingAgent[(int) m_size] );
        m_agents = new HashSet<>();
        m_chairgroup = new HashMap<>();
        m_activechairs = new LinkedList<>();
    }

    /**
     * initialize agents
     *
     * @param p_votingAgent agent
     * @return boolean value
     */
    public final void initialset( final CVotingAgent p_votingAgent )
    {
        m_agents.add( p_votingAgent );
    }

    /**
     * open a new group
     * @param p_votingAgent voting opening the group
     */

    public final void openNewGroup( final CVotingAgent p_votingAgent )
    {

        final List l_list = new LinkedList<CVotingAgent>();
        l_list.add( p_votingAgent );

        m_chairgroup.put( p_votingAgent.getChair(), l_list );
        m_activechairs.add( p_votingAgent.getChair() );

/*        final ITrigger l_trigger = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "new/group/opened",
                CLiteral.from( p_votingAgent.name() ),
                CLiteral.from( ( p_votingAgent.getChair() ).toString() ) )
        );


        // trigger all agents and tell them that the group was opened

        m_agents
            .parallelStream()
            .forEach( i -> i.trigger( l_trigger ) );*/

    }

    /**
     * join a group
     * @param p_votingAgent voting agent joining a group
     * @return the chair of the group
     */

    public final CChairAgent joinGroup( final CVotingAgent p_votingAgent )
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


        if   ( this.containsnot( l_randomChair, p_votingAgent ) )
        {

            m_chairgroup.get( l_randomChair ).add( p_votingAgent );
            System.out.println( p_votingAgent.name() + " joins group with chair " + l_randomChair );

            if ( m_chairgroup.get( l_randomChair ).size() == m_capacity )
            {
                m_activechairs.remove( l_randomChair );
                for ( int i = 0; i < m_capacity; i++ )
                    System.out.println( m_chairgroup.get( l_randomChair ).get( i ).name() + " with chair " + l_randomChair );
            }

/*            final ITrigger l_trigger = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "joined/group",
                    CLiteral.from( p_votingAgent.name() ),
                    CLiteral.from( l_randomChair.toString() )
                )
            );

            // trigger all agents and tell them that the agent joined a group
            m_agents
                .parallelStream()
                .forEach( i -> i.trigger( l_trigger ) );*/

            return l_randomChair;
        }


       // if it was not possible to join a group, open a new group

        else
        {
            this.openNewGroup( p_votingAgent );
            System.out.println( p_votingAgent.name() + " opened group with chair " + p_votingAgent.getChair() );
            return p_votingAgent.getChair();
        }

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

}
