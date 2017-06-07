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
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.statistics.CDataWriter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by sophie on 22.02.17.
 * Environment class
 */
public final class CEnvironment
{
    private List<CGroup> m_groups;
    private int m_groupNum;

    private List<CVotingAgent> m_agentList;

    // Index of the last activated agent
    private int m_currentIndex;

    private boolean m_firstActivated;
    private final String m_fileName;
    private int m_run;
    private String m_config;

    /**
     * constructor
     * @param p_size number of agents
     * @param p_fileName HDF5 file
     */
    public CEnvironment( final int p_size, final String p_fileName )
    {
        m_fileName = p_fileName;
        m_groups = Collections.synchronizedList( new LinkedList<>() );
        m_agentList = new LinkedList<>();
        m_groupNum = 0;
    }

    /**
     * initialize groups
     *
     * @param p_votingAgent agent
     *
     */
    public final void initialset( final CVotingAgent p_votingAgent )
    {
        m_agentList.add( p_votingAgent );

        if  ( !m_firstActivated )
        {
            final CVotingAgent l_firstAgent = m_agentList.get( 0 );

            l_firstAgent.sleep( 0 );
            System.out.println( "waking up agent " + l_firstAgent.name() );
            l_firstAgent.getChair().sleep( 0 );
            System.out.println( "waking up chair " + l_firstAgent.getChair().name() );
            m_firstActivated = true;
        }
    }

    /**
     * returns literal representation of existing groups
     * @param p_votingAgent voting agent
     * @return literal with references to existing groups
     */
    public ILiteral literal( final CVotingAgent p_votingAgent )
    {
        return CLiteral.from( "groups", CRawTerm.from( m_groups ) );
    }

    /**
     * open new group (for random grouping)
     * @param p_votingAgent voting agent opening group
     * @return new group
     */
    public CGroup openNewGroupRandom( final CVotingAgent p_votingAgent )
    {
        final CGroup l_group = new CGroup( p_votingAgent, "RANDOM", m_groupNum );
        m_groups.add( l_group );
        System.out.println( "Created Group " + l_group );
        this.wakeUpAgent();

        CDataWriter.setGroup( m_run, m_config, m_fileName, m_groupNum );
        m_groupNum++;

        return l_group;
    }

    /**
     * open new group (for coordinated grouping)
     * @param p_votingAgent voting agent opening group
     * @return new group
     */
    public CGroup openNewGroupCoordinated( final CVotingAgent p_votingAgent )
    {
        final CGroup l_group = new CGroup( p_votingAgent, "COORDINATED", m_groupNum );
        m_groups.add( l_group );
        System.out.println( "Created Group " + l_group );

        CDataWriter.setGroup( m_run, m_config, m_fileName, m_groupNum );
        m_groupNum++;
        return l_group;
    }

    /**
     * add agent to group (for random grouping)
     * @param p_randomGroup random group to join
     * @param p_votingAgent joining agent
     */

    public void addAgentRandom( final CGroup p_randomGroup, final CVotingAgent p_votingAgent )
    {
        p_randomGroup.addRandom( p_votingAgent );
        this.wakeUpAgent();
    }


    /**
     * add agent to group (for coordinated grouping)
     * @param p_group group to join
     * @param p_votingAgent joining agent
     */


    public void addAgentCoordinated( final CGroup p_group, final CVotingAgent p_votingAgent )
    {
        p_group.addCoordinated( p_votingAgent );
        this.wakeUpAgent();
    }

    /**
     * detect group of chair agent
     * @param p_chairAgent chair agent
     * @return literal representation of group if chair is assigned to a group
     */
    public ILiteral detectGroup( final CChairAgent p_chairAgent )
    {
        for ( final CGroup l_group : m_groups )
        {
            if ( !( ( l_group.literal( p_chairAgent ) ) == null ) )
                return l_group.literal( p_chairAgent );
        }
        return null;
    }

    // open group for further elections, unless the capacity is reached
    // also, wake up the next agent

    /**
     * open group for further elections unless the capacity is reached. Also, wake up the next agent
     * @param p_group group to be reopened
     */
    public void reopen( final CGroup p_group )
    {
        p_group.reopen();
        this.wakeUpAgent();
    }

    /**
     * reset environment for next simulation run
     */

    public void reset()
    {
        m_groups = Collections.synchronizedList( new LinkedList<>() );
        m_agentList = new LinkedList<>();
        m_firstActivated = false;
        m_agentList = new LinkedList<>();
        m_currentIndex = 0;
    }

    /**
     * set config
     * @param p_run run number
     * @param p_config config number
     */

    public void setConf( final int p_run, final String p_config )
    {
        m_run = p_run;
        m_config = p_config;
        m_groupNum = 0;
    }

    private void wakeUpAgent()
    {
        m_currentIndex++;
        final CVotingAgent l_wakingAgent =  m_agentList.get( m_currentIndex );
        l_wakingAgent.sleep( 0 );
        l_wakingAgent.getChair().sleep( 0 );

        l_wakingAgent.trigger(
            CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "main"
                )
            )
        );
        System.out.println( "Waking up agent " + l_wakingAgent.name() );
        System.out.println( "Waking up chair " + l_wakingAgent.getChair().name() );

    }
}
