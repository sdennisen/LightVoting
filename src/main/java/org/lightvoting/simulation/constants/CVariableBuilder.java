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

package org.lightvoting.simulation.constants;

import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.language.execution.IVariableBuilder;
import org.lightjason.agentspeak.language.instantiable.IInstantiable;
import org.lightjason.agentspeak.language.variable.CConstant;
import org.lightjason.agentspeak.language.variable.IVariable;
import org.lightvoting.simulation.agent.random_basic.CVotingAgentRB;
import org.lightvoting.simulation.environment.random_basic.CEnvironmentRB;
import org.lightvoting.simulation.environment.random_iterative.CEnvironmentRI;

import java.util.stream.Stream;


/**
 * Created by sophie on 21.02.17.
 */

public final class CVariableBuilder implements IVariableBuilder
{

    /**
     * environment reference
     */

    private CEnvironmentRI m_environmentRI;
    private CEnvironmentRB m_environmentRB;

    /**
     * constructor
     *
     * @param p_environment environment
     */
    public CVariableBuilder( final CEnvironmentRB p_environment )
    {
        m_environmentRB = p_environment;
    }

    public CVariableBuilder( final CEnvironmentRI p_environment )
    {
        m_environmentRI = p_environment;
    }

    @Override
    public final Stream<IVariable<?>> apply( final IAgent<?> p_agent, final IInstantiable p_runningcontext )
    {
        return Stream.of(
            new CConstant<>( "MyName", p_agent.<CVotingAgentRB>raw().name() )
        );
    }

}
