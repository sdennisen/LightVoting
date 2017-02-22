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

package org.lightvoting.simulation.agent;

import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;


/**
 * BDI agent with voting capabilities.
 */
// annotation to mark the class that actions are inside
@IAgentAction
public final class CVotingAgent extends IBaseAgent<CVotingAgent>
{

    /**
     * name of the agent
     */
    private final String m_name;

    /**
     * constructor of the agent
     * @param p_name name of the agent
     * @param p_configuration agent configuration of the agent generator
     */
    public CVotingAgent( final String p_name, final IAgentConfiguration<CVotingAgent> p_configuration )
    {
        super( p_configuration );
        m_name = p_name;
    }

    /**
     * constructor of the agent
     * @param p_name name of the agent
     * @param p_configuration agent configuration of the agent generator
     * @param p_chairagent corresponding chair agent
     */

    public CVotingAgent( final String p_name, final IAgentConfiguration<CVotingAgent> p_configuration, final IBaseAgent<CChairAgent> p_chairagent )
    {
        super( p_configuration );
        m_name = p_name;

        m_storage.put( "chair", p_chairagent.raw() );

        m_beliefbase.add(
            CLiteral.from(
                "chair",
                CRawTerm.from( p_chairagent )
            )
        );

        // sleep chair, Long.MAX_VALUE -> inf
        p_chairagent.sleep( Long.MAX_VALUE );


    }

    // overload agent-cycle
    @Override
    public final CVotingAgent call() throws Exception
    {
        // run default cycle
        return super.call();
    }


    @IAgentActionFilter
    @IAgentActionName( name = "open/new/group" )
    private void myaction( final IBaseAgent<CChairAgent> p_chairagent )
    {
        // wake up in next cycle
        p_chairagent.sleep( 0 );

        // hier könnten dann auch gleich die nötigen trigger in den chair-agent gepusht werden wenn die gruppe aufgemacht wird

    }



    /**
     * Get agent's name
     * @return name of agent
     */
    public final String name()
    {
        return m_name;
    }
}
