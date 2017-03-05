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

import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;


/**
 * Created by sophie on 22.02.17.
 * Environment class
 */
public final class CEnvironment
{
    /**
     * thread-safe structure for group-to-agent mapping
     */
    private final AtomicReferenceArray<CVotingAgent> m_group;

    /**
     * map with agent-to-group-id mapping
     */
    private final Map<CVotingAgent, Integer> m_agentgroup = new ConcurrentHashMap<>();

    /**
     * map with chair-to-group mapping
     */

    private final Map<CChairAgent, List<CVotingAgent>> m_chairgroup = new ConcurrentHashMap<>();

    /**
     * maximum size
     */
    private final int m_size;

    /**
     * group capacity
     */

    private final int m_capacity = 3;

    /**
     * Map to indicate if joining a group is allowed
     */

    private final Map<Integer, Boolean> m_joiningAllowed;

    /**
     *constructor
     * @param p_size number of agents
     */
    public CEnvironment( final int p_size )
    {
        m_size = p_size;
        m_group = new AtomicReferenceArray<CVotingAgent>( new CVotingAgent[(int) m_size] );
        m_joiningAllowed = new HashMap<>();
    }

    /**
     * initialize groups
     *
     * @param p_votingAgent agent
     * @return boolean value
     */
    public final boolean initialset( final CVotingAgent p_votingAgent, final int p_group )
    {
        if ( m_group.compareAndSet( p_group, null, p_votingAgent ) )
        {
            m_agentgroup.put( p_votingAgent, p_group );
            // TODO only for testing -> needs to be moved to open/new/group
            m_joiningAllowed.put(  m_agentgroup.get( p_votingAgent ), true );

            System.out.println( "Joining group " + m_agentgroup.get( p_votingAgent ) + " allowed" );
            return true;


        }

        return false;

    }

    /**
     * open a new group
     * @param p_votingAgent voting opening the group
     * @param p_chairAgent corresponding chair
     */

    public final void openNewGroup( final CVotingAgent p_votingAgent, final CChairAgent p_chairAgent )
    {
        final List<CVotingAgent> l_agentList = new LinkedList<>();
        l_agentList.add(  p_votingAgent );
        m_chairgroup.put( p_votingAgent.getChair(), l_agentList );
        System.out.println( " Agent " + p_votingAgent.name() + " group id " + ( m_agentgroup.get( p_votingAgent ) ).toString() );

        final ITrigger l_triggerChair = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "myGroup",
                CLiteral.from( p_votingAgent.name() ),
                CLiteral.from( ( m_agentgroup.get( p_votingAgent ) ).toString() ) )
        );

        p_votingAgent.getChair().trigger( l_triggerChair );

        final ITrigger l_trigger = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "new/group/opened",
                CLiteral.from( p_votingAgent.name() ),
                CLiteral.from( p_chairAgent.toString() ),
                CLiteral.from( ( m_agentgroup.get( p_votingAgent ) ).toString() ) )
            );


           // trigger all agents and tell them that the group was opened
        m_agentgroup
            .keySet()
            .parallelStream()
            .forEach( i -> i.trigger( l_trigger ) );

    //    m_joiningAllowed.put(  m_agentgroup.get( p_votingAgent ), true);

    }

    /**
     * join a group
     * @param p_votingAgent voting agent joining a group
     */

    public final void joinGroup( final CVotingAgent p_votingAgent, final Number p_testID )
    {
        // we only do something if it is allowed to join the group
        // TODO later: we need means to ensure that the agent knows that she has to look for another group
        // TODO doesn't work because of problem with format of group IDs -> reinsert later
        // if (m_joiningAllowed.get(p_testID))
        //  {
        //    System.out.println( "Joining allowed ");
        final int l_oldSize = m_chairgroup.get( p_votingAgent.getChair() ).size();
        if ( ( l_oldSize + 1 ) < m_capacity )
        {
            m_chairgroup.get( p_votingAgent.getChair() ).add( p_votingAgent );

            final ITrigger l_triggerChair = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "my/group/new/agent",
                    CLiteral.from( p_votingAgent.name() ),
                    CLiteral.from( ( m_agentgroup.get( p_votingAgent ) ).toString() )
                )
            );

            p_votingAgent.getChair().trigger( l_triggerChair );

            System.out.println( "name of joining agent " + p_votingAgent.name() + " ID: " + String.valueOf( Math.round( p_testID.doubleValue() ) ) );
            final ITrigger l_trigger = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "joined/group",
                    CLiteral.from( p_votingAgent.name() ),
                    CLiteral.from( String.valueOf( (int) ( p_testID.doubleValue() ) ) )
                )
            );


            // trigger all agents and tell them that the agent joined a group
            m_agentgroup
                .keySet()
                .parallelStream()
                .forEach( i -> i.trigger( l_trigger ) );
        }

        // TODO if the capacity is reached, the joining of further agents must be disabled
        // TODO and the election has to be triggered

        else
        {
            m_chairgroup.get( p_votingAgent.getChair() ).add( p_votingAgent );

            final ITrigger l_triggerChair = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "my/group/new/agent",
                    CLiteral.from( p_votingAgent.name() ),
                    CLiteral.from( ( m_agentgroup.get( p_votingAgent ) ).toString() )
                )
            );

            p_votingAgent.getChair().trigger( l_triggerChair );

            System.out.println( "name of joining agent " + p_votingAgent.name() + " ID: " + String.valueOf( Math.round( p_testID.doubleValue() ) ) );
            final ITrigger l_trigger = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "joined/group",
                    CLiteral.from( p_votingAgent.name() ),
                    CLiteral.from( String.valueOf( (int) ( p_testID.doubleValue() ) ) )
                )
            );


            // trigger all agents and tell them that the agent joined a group
            m_agentgroup
                .keySet()
                .parallelStream()
                .forEach( i -> i.trigger( l_trigger ) );

            // tell chair that she needs to start the election

            final ITrigger l_triggerStart = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "start/election" )

            );

            p_votingAgent.getChair().trigger( l_triggerStart );


        }
      //  }

    }



    public final int size()
    {
        return m_size;
    }

}
