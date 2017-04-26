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

    private int[] m_result;

    private boolean m_readyForElection;
    private boolean m_inProgress;

    /**
     * constructor
     * @param p_votingAgent voting agent creating the group
     */
    public CGroup( final CVotingAgent p_votingAgent )
    {
        m_agentList = new LinkedList<>();
        m_agentList.add( p_votingAgent );
        m_chair = p_votingAgent.getChair();
        m_open = true;
        m_result = null;
        m_readyForElection = false;
        m_inProgress = false;
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
     * add voting agent
     * @param p_votingAgent joining voting agent
     */
    public void add( final CVotingAgent p_votingAgent )
    {
        System.out.println( "Adding agent, old size is " + m_agentList.size() );
        m_agentList.add( p_votingAgent );
        if ( m_agentList.size() >= m_capacity )
        {
            m_open = false;
            m_readyForElection = true;
        }
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

        final ITrigger l_trigger = CTrigger.from(
            ITrigger.EType.ADDGOAL,
            CLiteral.from(
                "submit/vote",
                CRawTerm.from( p_chairAgent ) )
        );

        m_agentList.forEach( i -> i.trigger( l_trigger ) );
    }

}

// XXXXXXXXXXXXX Old code and TODOS XXXXXXXXXXXXXXXXXXXXXXX
// TODO set m_capacity via config file
// TODO if necessary, reinsert in literal( CVotingAgent p_ag ) m_agentList.parallelStream().forEach( i -> System.out.println( " Added " + CRawTerm.from( i ) ) );
// TODO reinsert the following in literal( CChairAgent p_chairAg ) ?
       /* Collection<ITerm> l_rawTerms = new LinkedList<>();
        for ( int i = 0; i < m_agentList.size(); i++ )
            l_rawTerms.add( CRawTerm.from( m_agentList.get( i ) ) );


        ILiteral l_literal = CLiteral.from( "agents", l_rawTerms ) ;*/
//   return CLiteral.from( "group", CRawTerm.from( this ), l_literal, CRawTerm.from( this.open() ), CRawTerm.from( m_result ) );

// TODO Hinweis Malte

/*
https://lightjason.github.io/AgentSpeak/sources/d2/dd3/classorg_1_1lightjason_1_1agentspeak_1_1action_1_1buildin_1_1collection_1_1list_1_1CCreate.html
    das ganze ist dann ein List<ITerm> objekt

    sophie
    11:50 AM
    mh ja ok dann müsste ich in java eine List<ITerm> erzeugen und dann CRawTerm.from( list) machen
    11:50 AM
    meine nur das hätte ich vorhin schon mal versucht

    malte
    11:54 AM
    kannst ja mal in der methode schauen. da macht phil am ende
    Java
    p_return.add( CRawTerm.from(
    p_parallel
    ? Collections.synchronizedList( l_list )
    : l_list
    ) );
    d.h. wenn du das parallel zusammen schieben willst, erzeug halt per
    Java
    CRawTerm.from( Collections.synchronizedList( new <deine liste> ) );*/
