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
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.environment.CEnvironment;
import org.lightvoting.simulation.environment.CGroup;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Created by sophie on 21.02.17.
 */

// annotation to mark the class that actions are inside
@IAgentAction
public final class CChairAgent extends IBaseAgent<CChairAgent>
{

    /**
     * name of chair
     */
    private final String m_name;

    /**
     * environment
     */

    private final CEnvironment m_environment;

    /**
     * constructor of the agent
     *
     * @param p_configuration agent configuration of the agent generator
     */

    public CChairAgent( final String p_name, final IAgentConfiguration<CChairAgent> p_configuration, final CEnvironment p_environment )
    {
        super( p_configuration );
        m_name = p_name;
        m_environment = p_environment;

    }

    // overload agent-cycle
    @Override
    public final CChairAgent call() throws Exception
    {
        // run default cycle
        return super.call();
    }

    public String name()
    {
        return m_name;
    }


    /**
     * perceive group
     */
    @IAgentActionFilter
    @IAgentActionName( name = "perceive/group" )
    /**
     * add literal for group of chair agent if it exists
     */
    public void perceiveGroup()
    {
        if ( !( m_environment.detectGroup( this ) == null ) )
        this.beliefbase().add( m_environment.detectGroup( this ) );
    }
    /**
     * check conditions
     */
    @IAgentActionFilter
    @IAgentActionName( name = "check/conditions" )
    /**
     * add literal for group of chair agent if it exists
     */
    public void checkConditions()
    {
        final AtomicReference<CGroup> l_groupAtomic = new AtomicReference<>();
        final Collection l_groups = this.beliefbase().beliefbase().literal( "group" );
        l_groups.stream().forEach( i-> l_groupAtomic.set( ( (ILiteral) i ).values().findFirst().get().raw() ) );

        // if conditions for election are fulfilled, trigger goal start/criterion/fulfilled

        final ITrigger l_trigger;

        if ( l_groupAtomic.get().readyForElection() && ( !( l_groupAtomic.get().electionInProgress() ) ) )
        {
            l_groupAtomic.get().startProgress();

            l_trigger = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from( "start/criterion/fulfilled" )
            );

            this.trigger( l_trigger );
        }
    }


}

// XXXXXXXXXXXXXXXXXXXXXXXX Old code XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// TODO if necessary, reinsert into checkConditions()

/*            System.out.println( ".................. print group  " + i );
            System.out.println( "Contents of group " + ( (ILiteral) i ).values().findFirst().get().raw() );
            System.out.println( "Class " + ( (ILiteral) i ).values().findFirst().get().raw().getClass() );*/


/*    @IAgentActionFilter
    @IAgentActionName( name = "start/election" )
    public void startElection( )
    {
        m_environment.startElection( this );
    }*/

  /*  @IAgentActionFilter
    @IAgentActionName( name = "store/vote" )
    private void storeVote( final Object p_votingAgent, final AtomicIntegerArray p_vote )
    {

        System.out.println( " trying to add vote from agent " + p_votingAgent + ": " + p_vote );
        m_environment.storeVote( this, p_votingAgent, p_vote );
        System.out.println( " added vote from agent " + p_votingAgent );

    }*/

/*    @IAgentActionFilter
    @IAgentActionName( name = "compute/result" )
    private void computeResult( )
    {

        System.out.println( " compute result " );
        m_environment.computeResult( this );
//        System.out.println( " computed result " );

    }*/

  /*  @IAgentActionFilter
    @IAgentActionName( name = "store/diss" )
    private void storeDiss( final Object p_votingAgent, final Double p_diss, final int p_iteration )
    {

        System.out.println( " trying to add diss from agent " + p_votingAgent + ": " + p_diss + " next iteration " + p_iteration );
        m_environment.storeDiss( this, p_diss, p_iteration );
        System.out.println( " added diss from agent " + p_votingAgent );

    }*/

/*    @IAgentActionFilter
    @IAgentActionName( name = "recompute/result" )
    private void recomputeResult( final int p_iteration )
    {

        System.out.println( " recompute result " );
        m_environment.recomputeResult( this, p_iteration );
        //        System.out.println( " computed result " );

    }*/

