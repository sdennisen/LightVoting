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

import cern.colt.bitvector.BitVector;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by sophie on 24.04.17.
 */
public class CGroup
{
    private final List<CVotingAgent> m_agentList;

    private final int m_capacity = 3;

    private final CChairAgent m_chair;

    private boolean m_open;

    private BitVector m_result;

    private boolean m_readyForElection;
    private boolean m_inProgress;
    private int m_ID;

    /**
     * constructor
     * @param p_votingAgent voting agent creating the group
     * @param p_grouping grouping algorithm
     * @param p_groupNum group number
     */
    public CGroup( final CVotingAgent p_votingAgent, final String p_grouping, final int p_groupNum )
    {
        m_agentList = new LinkedList<>();
        m_agentList.add( p_votingAgent );
        m_chair = p_votingAgent.getChair();
        m_open = true;
        m_result = null;
        if ( "RANDOM".equals( p_grouping ) )
            m_readyForElection = false;
        else
            m_readyForElection = true;
        m_inProgress = false;
        m_ID = p_groupNum;
    }

    /**
     * returns literal representation for voting agent
     * @param p_votingAgent voting agent
     * @return literal with chair, status of group, curret voting result and info whether the group contains the agent
     */

    public ILiteral literal( final CVotingAgent p_votingAgent )
    {
        return CLiteral.from( "group", CRawTerm.from( m_chair ), CRawTerm.from( this.open() ), CRawTerm.from( m_result ),
                              CRawTerm.from( m_agentList.contains( p_votingAgent ) ) );
    }

    /**
     * returns literal representation for chair agent, is null if the chair is not the chair of the group
     * @param p_chairAgent chair agent
     * @return literal with group reference, agents in group, status of group, current voting result )
     */
    public ILiteral literal( final CChairAgent p_chairAgent )
    {
        final List<ITerm> l_terms = new LinkedList<>();

        for ( int i = 0; i < m_agentList.size(); i++ )
            l_terms.add( CRawTerm.from( m_agentList.get( i ) ) );


        if ( ( this.m_chair ).equals( p_chairAgent ) )
            return CLiteral.from( "group", CRawTerm.from( this ) );
        else return null;
    }

    public boolean readyForElection()
    {
        return m_readyForElection;
    }

    /**
     * add voting agent (for random grouping)
     * @param p_votingAgent joining voting agent
     */
    public void addRandom( final CVotingAgent p_votingAgent )
    {
        System.out.println( "Adding agent, old size is " + m_agentList.size() );
        m_agentList.add( p_votingAgent );
        System.out.println( " ==================  Group: " + m_chair.name() + this.printAgList( m_agentList ) + " " + m_chair.sleeping() );

        if ( m_agentList.size() >= m_capacity )
        {
            m_open = false;
            m_readyForElection = true;
        }
    }

    private String printAgList( final List<CVotingAgent> p_agentList )
    {
        String l_string = " ";
        for ( int i = 0; i < p_agentList.size(); i++ )
            l_string = l_string.concat( p_agentList.get( i ).name() + " " );

        return l_string;
    }

    /**
     * add voting agent (for coordinated grouping)
     * @param p_votingAgent joining voting agent
     */

    public void addCoordinated( final CVotingAgent p_votingAgent )
    {

        System.out.println( "Adding agent, old size is " + m_agentList.size() );
        m_agentList.add( p_votingAgent );
        m_open = false;
        m_readyForElection = true;
    }

    public void remove( final CVotingAgent p_votingAgent )
    {
        m_agentList.remove( p_votingAgent );
    }

    public boolean open()
    {
        return m_open;
    }

    public boolean electionInProgress()
    {
        return m_inProgress;
    }

    public void startProgress()
    {
        m_inProgress = true;
    }

    /**
     * trigger agents in group
     * @param p_chairAgent chair agent
     */
    public void triggerAgents( final CChairAgent p_chairAgent )
    {
        m_agentList.forEach( i ->
            i.trigger(
                CTrigger.from(
                    ITrigger.EType.ADDGOAL,
                    CLiteral.from(
                        "submit/vote",
                        CRawTerm.from( p_chairAgent ) )
                )
            )
        );
    }

    /**
     * return size
     * @return size of agent list
     */
    public int size()
    {
        return m_agentList.size();
    }

    /**
     * update group literal for chair agent ( for random grouping )
     * @param p_chairAgent chair agent
     * @param p_result election result
     * @return group literal for chair agent
     */

    public ILiteral updateBasic( final CChairAgent p_chairAgent, final BitVector p_result )
    {
        // send result of election to all agents in the group
        m_agentList.stream().forEach( i ->
            i.trigger( CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from( "election/result",
                               CRawTerm.from( p_chairAgent ),
                               CRawTerm.from( p_result ) )
                       )
            )
        );

        m_result = p_result;
        return this.literal( p_chairAgent );
    }

    /**
     * ask agents in group to submit diss for
     * @param p_chairAgent chair agent
     * @param p_comResultBV election result
     * @param p_iteration iteration number
     * @return group literal for chair agent
     */

    public ILiteral submitDiss( final CChairAgent p_chairAgent, final BitVector p_comResultBV, final int p_iteration )
    {
        // send result of election to all agents in the group
        m_agentList.stream().forEach( i ->
                                          i.trigger( CTrigger.from(
                                              ITrigger.EType.ADDGOAL,
                                              CLiteral.from( "submit/final/diss",
                                                             CRawTerm.from( p_chairAgent ),
                                                             CRawTerm.from( p_comResultBV ),
                                                             CRawTerm.from( p_iteration ) )
                                                     )
                                          )
        );

        m_result = p_comResultBV;
        return this.literal( p_chairAgent );
    }

    /**
     * update group literal for chair agent ( for random grouping )
     * @param p_chairAgent chair agent
     * @param p_result election result
     * @param p_iteration current iteration
     * @return group literal for chair agent
     */

    public ILiteral updateIterative( final CChairAgent p_chairAgent, final BitVector p_result, final int p_iteration )
    {
        // send result of election to all agents in the group
        m_agentList.stream().forEach( i ->
        {
            i.trigger( CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from( "election/result",
                               CRawTerm.from( p_chairAgent ),
                               CRawTerm.from( p_result  ),
                               CRawTerm.from( p_iteration ) )
                       )
            );
            System.out.println( "triggering agent " + i.name() );
        } );

        m_result = p_result;
        return this.literal( p_chairAgent );

    }

    /**
     * reset group
     */
    public void reset()
    {
        m_inProgress = false;
        m_readyForElection = false;
    }

    /**
     * reopen group unless capacity is reached
     */
    public void reopen()
    {
        if ( m_agentList.size() < m_capacity )
        {
            m_open = true;
        }
    }

    /**
     * return current result
     * @return election result
     */

    public BitVector result()
    {
        return m_result;
    }


    public boolean finale()
    {
        return m_agentList.size() >= m_capacity;
    }

    public void makeReady()
    {
        m_readyForElection = true;
    }

    /**
     * determine agent for given name
     * @param p_agentName name
     * @return corresponding voting agent
     */
    public CVotingAgent determineAgent( final String p_agentName )
    {
        for ( int i = 0; i < m_agentList.size(); i++ )
            if ( p_agentName.equals( m_agentList.get( i ).name() ) )
                return m_agentList.get( i );
        return null;
    }

    public int getID()
    {
        return m_ID;
    }
}
