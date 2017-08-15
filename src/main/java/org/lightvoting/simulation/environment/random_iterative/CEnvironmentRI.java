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

package org.lightvoting.simulation.environment.random_iterative;

import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.agent.random_iterative.CVotingAgentRI;
import org.lightvoting.simulation.environment.random_basic.CGroupRB;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

// import org.lightvoting.simulation.statistics.CDataWriter;


/**
 * Created by sophie on 22.02.17.
 * Environment class
 */
public final class CEnvironmentRI
{
    private List<CGroupRB>  m_groups = Collections.synchronizedList( new LinkedList<>() );
    private int m_groupNum;
    private List<CVotingAgentRI> m_agentList = new LinkedList<>();

    // Index of the last activated agent
    private int m_currentIndex;

    private boolean m_firstActivated;
    private final String m_fileName;
    private int m_run;
    private String m_config;
    private int m_capacity;
    private HashMap<String, Object> m_map = new HashMap<>();
    private int m_finalGroupNum;

    /**
     * constructor
     * @param p_size number of agents
     * @param p_fileName HDF5 file
     * @param p_capacity group capacity
     */
    public CEnvironmentRI( final int p_size, final String p_fileName, final int p_capacity )
    {
        m_fileName = p_fileName;
        m_capacity = p_capacity;
    }

    /**
     * initialize groups
     *
     * @param p_votingAgent agent
     *
     */
    public final void initialset( final CVotingAgentRI p_votingAgent )
    {
        m_agentList.add( p_votingAgent );

        if  ( !m_firstActivated )
        {
            final CVotingAgentRI l_firstAgent = m_agentList.get( 0 );

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
    public ILiteral literal( final CVotingAgentRI p_votingAgent )
    {
        System.out.println( "xxxxxxxxxxxxxx m_groups: " + m_groups );
        return CLiteral.from( "groups", CRawTerm.from( m_groups ) );
    }

//    /**
//     * open new group (for random grouping)
//     * @param p_votingAgent voting agent opening group
//     * @return new group
//     */
//    public CGroupRB openNewGroupRandom( final CVotingAgentRB p_votingAgent )
//    {
//        final CGroupRB l_group = new CGroupRB( p_votingAgent, "RANDOM", m_groupNum, m_capacity );
//        m_groups.add( l_group );
//        System.out.println( "Created Group " + l_group );
//        this.wakeUpAgent();
//
//   //     EDataWriter.INSTANCE.setGroup( m_run, m_config, m_groupNum );
//   //     EDataWriter.INSTANCE.writeDefaultLastIteration( m_run, m_config, m_groupNum );
////        new CDataWriter().setGroup( m_run, m_config, m_fileName, m_groupNum );
////        new CDataWriter().writeDefaultLastIteration( m_fileName, m_run, m_config, m_groupNum );
//        m_groupNum++;
//
//        return l_group;
//    }

//    /**
//     * open new group (for coordinated grouping)
//     * @param p_votingAgent voting agent opening group
//     * @return new group
//     */
//    public CGroupRB openNewGroupCoordinated( final CVotingAgentRB p_votingAgent )
//    {
//        final CGroupRB l_group = new CGroupRB( p_votingAgent, "COORDINATED", m_groupNum, m_capacity );
//        m_groups.add( l_group );
//        System.out.println( "Created Group " + l_group );
//
//   //      EDataWriter.INSTANCE.setGroup( m_run, m_config, m_groupNum );
//  //      EDataWriter.INSTANCE.writeDefaultLastIteration( m_run, m_config, m_groupNum );
////        new CDataWriter().setGroup( m_run, m_config, m_fileName, m_groupNum );
////        new CDataWriter().writeDefaultLastIteration( m_fileName, m_run, m_config, m_groupNum );
//
//        m_groupNum++;
//
//        return l_group;
//    }

//    /**
//     * add agent to group (for random grouping)
//     * @param p_randomGroup random group to join
//     * @param p_votingAgent joining agent
//     */
//
//    public void addAgentRandom( final CGroupRB p_randomGroup, final CVotingAgentRB p_votingAgent )
//    {
//        p_randomGroup.addRandom( p_votingAgent );
//        this.wakeUpAgent();
//    }


//    /**
//     * add agent to group (for coordinated grouping)
//     * @param p_group group to join
//     * @param p_votingAgent joining agent
//     */
//
//
//    public void addAgentCoordinated( final CGroupRB p_group, final CVotingAgentRB p_votingAgent )
//    {
//        p_group.addCoordinated( p_votingAgent );
//        this.wakeUpAgent();
//    }

//    /**
//     * detect group of chair agent
//     * @param p_chairAgent chair agent
//     * @return literal representation of group if chair is assigned to a group
//     */
//    public ILiteral detectGroup( final CChairAgent p_chairAgent )
//    {
//        for ( final CGroupRB l_group : m_groups )
//        {
//            if ( !( l_group.literal( p_chairAgent ).emptyValues() ) )
//                return l_group.literal( p_chairAgent );
//        }
//        return CLiteral.from( "" );
//    }

    // open group for further elections, unless the capacity is reached
    // also, wake up the next agent

//    /**
//     * open group for further elections unless the capacity is reached. Also, wake up the next agent
//     * @param p_group group to be reopened
//     */
//    public void reopen( final CGroupRB p_group )
//    {
//        p_group.reopen();
//        this.wakeUpAgent();
//    }

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
        m_finalGroupNum = 0;
    }

    public HashMap<String, Object> map()
    {
        return m_map;
    }

    /**
     * count group if final diss values are stored
     * @param p_run run nr
     * @param p_conf config name
     */

    public void incrementGroupCount( final int p_run, final String p_conf )
    {
        m_finalGroupNum++;

        final String l_slash = "/";

        final String l_path = p_run + l_slash  + p_conf + l_slash + "groups" + l_slash + "group count";

        m_map.put( l_path, m_finalGroupNum );
    }

    private void wakeUpAgent()
    {
        m_currentIndex++;
        final CVotingAgentRI l_wakingAgent =  m_agentList.get( m_currentIndex );
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
