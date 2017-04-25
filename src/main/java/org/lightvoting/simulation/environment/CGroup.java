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
import org.lightvoting.simulation.agent.CVotingAgent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by sophie on 24.04.17.
 */
public class CGroup
{
    private final List<CVotingAgent> m_agentList;

    /**
     * constructor
     * @param p_votingAgent voting agent creating the group
     */
    public CGroup( final CVotingAgent p_votingAgent )
    {
        m_agentList = Collections.synchronizedList( new LinkedList<>() );
        m_agentList.add( p_votingAgent );
    }

    /**
     * returns literal representation for voting agent
     * @param p_votingAgent voting agent
     * @return literal with agents in the group
     */

    public ILiteral literal( final CVotingAgent p_votingAgent )
    {
        m_agentList.parallelStream().forEach( i -> System.out.println( " Added " + CRawTerm.from( i ) ) );

        return CLiteral.from( "group", CRawTerm.from( m_agentList ) );
    }

    public void add( final CVotingAgent p_votingAgent )
    {
        m_agentList.add( p_votingAgent );
    }
}
