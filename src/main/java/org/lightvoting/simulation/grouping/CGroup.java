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

package org.lightvoting.simulation.grouping;

import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;

import java.util.HashMap;


/**
 * Created by sophie on 23.02.17.
 */
public class CGroup
{
    private int m_id;
    private CVotingAgent m_initialAgent;
    private HashMap<String, CVotingAgent> m_votingAgents;
    private CChairAgent m_chair;

    /**
     * constructor
     * @param p_id group id
     * @param p_chair chair agent
     */
    public CGroup( final int p_id, final CVotingAgent p_initialAgent, final CChairAgent p_chair )
    {
        m_id = p_id;
        m_votingAgents = new HashMap<>();
        m_votingAgents.put( p_initialAgent.name(), p_initialAgent );
        m_chair = p_chair;
    }

    /**
     * add agent to group
     * @param p_votingAgent voting agent
     */
    public void addAgent( final CVotingAgent p_votingAgent )
    {
        m_votingAgents.put( p_votingAgent.name(), p_votingAgent );
    }

    /**
     * remove agent from group
     * @param p_name name of agent
     */
    public void removeAgent( final String p_name )
    {
        m_votingAgents.remove( p_name );
        // if the agent removed choose another agent as "initial agent"
        if ( ( p_name.equals( m_initialAgent.name() ) ) && ( m_votingAgents.entrySet().iterator().hasNext() ) )
        {
            m_initialAgent = m_votingAgents.entrySet().iterator().next().getValue();
            m_chair = m_initialAgent.getChair();
        }

    }

    /**
     * get ID of group
     * @return group id
     */
    public int getId()
    {
        return m_id;
    }

}
