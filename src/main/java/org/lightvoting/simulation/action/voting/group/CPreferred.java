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

package org.lightvoting.simulation.action.voting.group;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;

import java.text.MessageFormat;
import java.util.List;

/**
 * Action to calculate the preferred of current available groups based on agent's preferences and POIs' attributes.
 */
public class CPreferred extends IBaseAction
{
    @Override
    public final IPath name()
    {
        return CPath.from( "voting/group/find-preferred" );
    }

    @Override
    public final int minimalArgumentNumber()
    {
        return 0;
    }

    @Override
    public final IFuzzyValue<Boolean> execute(final IContext p_context, final boolean p_parallel,
                                              final List<ITerm> p_argument, final List<ITerm> p_return,
                                              final List<ITerm> p_annotation )
    {
        System.out.println(
                MessageFormat.format(
                        "{0} action is called from agent {1}.", this.name(), p_context.agent()
                )
        );

        // the action should return a value, you can wrap any Java object into LightJason
        //p_return.add( CRawTerm.from( p_context.agent().hashCode() ) );


        // the actions returns a fuzzy-boolean for successful or failing execution
        // the optional second parameter is a fuzzy-value in [0,1] on default it is 1
        return CFuzzyValue.from( true );
    }
}
