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
     * map with agent-to-group mapping
     */
    private final Map<CVotingAgent, Integer> m_agentgroup = new ConcurrentHashMap<>();

    /**
     * maximum size
     */

    /**
     * maximum size
     */
    private final int m_size;

    /**
     *constructor
     * @param p_size number of agents
     */
    public CEnvironment( final int p_size )
    {
        m_size = p_size;
        m_group = new AtomicReferenceArray<CVotingAgent>( new CVotingAgent[(int) m_size] );
    }

    /**
     * initialize agents
     *
     * @param p_votingAgent agent
     * @return boolean value
     */
    public final boolean initialset( final CVotingAgent p_votingAgent, final int p_group )
    {
        if ( m_group.compareAndSet( p_group, null, p_votingAgent ) )
        {
            m_agentgroup.put( p_votingAgent, p_group );
            System.out.println( " Agent " + p_votingAgent.name() + " group id " + p_group );
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
        final int l_number = m_agentgroup.get( p_votingAgent );
        final String l_numberString = String.valueOf(l_number);

        System.out.println( "Number is " + l_number);
        System.out.println( "String for number is " + l_numberString);

        final ITrigger l_trigger = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "new/group/opened",
                CLiteral.from( p_votingAgent.name() ),
                CLiteral.from( p_chairAgent.toString() ),
                // TODO re-insert with correct form
             //   CLiteral.from( ( m_agentgroup.get( p_votingAgent ) ).toString() ) )
             //   CLiteral.from ( (m_agentgroup.get( p_votingAgent).toString() ) ))
                CLiteral.from ( l_numberString ) )
            );


           // trigger all agents and tell them that the group was opened
        m_agentgroup
            .keySet()
            .parallelStream()
            .forEach( i -> i.trigger( l_trigger ) );

    }

    /**
     * join a group
     * @param p_votingAgent voting agent joining a group
     */

    public final void joinGroup( final CVotingAgent p_votingAgent, final Number p_testID )
    {

                System.out.println( "name of joining agent " + p_votingAgent.name() + " ID: " + ((Integer) p_testID).toString()  );

             //   String l_idString= (p_testID.toString()).replace("[][]","");

                //   System.out.println( "name of joining agent " + p_votingAgent.name() + " ID ohne Annotationen: " + l_id  );

                final ITrigger l_trigger = CTrigger.from(
                    ITrigger.EType.ADDGOAL,
                    CLiteral.from(
                        "joined/group",
                        CLiteral.from( p_votingAgent.name() ),
                        CLiteral.from( ((Integer) p_testID).toString() ) )
                    );


//        System.out.println( "name of joining agent " + p_votingAgent.name() + " ID: " + String.valueOf( Math.round( p_testID.doubleValue() ) )  );
//        final ITrigger l_trigger = CTrigger.from(
//            ITrigger.EType.ADDGOAL,
//            CLiteral.from(
//                "joined/group",
//                CLiteral.from( p_votingAgent.name() ),
//                CLiteral.from( String.valueOf( (int) ( p_testID.doubleValue() ) )  ) )
//            );


        // trigger all agents and tell them that the agent joined a group
        m_agentgroup
            .keySet()
            .parallelStream()
            .forEach( i -> i.trigger( l_trigger ) );

    }

    public final int size()
    {
        return m_size;
    }

}
