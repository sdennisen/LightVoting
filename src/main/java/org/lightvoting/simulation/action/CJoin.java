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

package org.lightvoting.simulation.action;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.fuzzy.CFuzzyValue;
import org.lightjason.agentspeak.language.execution.fuzzy.IFuzzyValue;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;

import java.text.MessageFormat;
import java.util.List;


/**
 * Action to send the vote to the chair of a group.
 */
public class CJoin extends IBaseAction
{
    private String m_grouping = "RANDOM";

    @Override
    public final IPath name()
    {
        return CPath.from( "join/group" );
    }

    @Override
    public final int minimalArgumentNumber()
    {
        return 0;
    }

    @Override
    public final IFuzzyValue<Boolean> execute( final IContext p_context, final boolean p_parallel,
                                               final List<ITerm> p_argument, final List<ITerm> p_return,
                                               final List<ITerm> p_annotation )
    {
        System.out.println(
            MessageFormat.format(
                        "{0} action is called from agent {1}.", this.name(), p_context.agent()
                )
        );

        final CChairAgent l_chairAgent;

        if ( "RANDOM".equals( m_grouping ) )
               l_chairAgent = this.joinRandomGroup( p_context );

/*        if ( "COORDINATED".equals( m_grouping ) )
                return this.joinGroupCoordinated();
            return null;*/

        /**
         * first parameter of the action is the name of the receiving chair
         */
      //  final IAgent<?> l_receiver = m_agents.get( p_argument.get( 0 ).<String>raw() );

        // TODO: determine existing groups via beliefbase
        // TODO: determine other Chair to join if other groups already exist

        final IAgent<?> l_receiver = ( (CVotingAgent) p_context.agent() ).getChair();

        // if the agent is it not found, action fails
        if ( l_receiver == null )
            return CFuzzyValue.from( false );

        // create the receiving goal-trigger of the message
        l_receiver.trigger(
            CTrigger.from(
                ITrigger.EType.ADDGOAL,

                // create the goal literal "join/group(T)" where T is the traveller joining the group
                CLiteral.from(
                    "join/group",
                CLiteral.from( ( (CVotingAgent) p_context.agent() ).name() )
                )
            )
        );


        // the action should return a value, you can wrap any Java object into LightJason
        //p_return.add( CRawTerm.from( p_context.agent().hashCode() ) );


        // the actions returns a fuzzy-boolean for successful or failing execution
        // the optional second parameter is a fuzzy-value in [0,1] on default it is 1
        return CFuzzyValue.from( true );
    }

    private final CChairAgent joinRandomGroup( final IContext p_context )
    {

        // TODO generate list of available chairs from beliefbase
        // beliefs have the form group(Chair)

        if ( !( p_context.agent().beliefbase().containsLiteral( new CPath( "group" ) ) ) )
        {
            System.out.println( "No groups available - open a new one " );
            // TODO open new group and announce this via env
            this.openNewGroup( p_context );
        }


//        if ( m_activechairs.size() == 0 )
//        {
//            this.openNewGroup( p_votingAgent );
//            this.wakeUpAgent();
//            return p_votingAgent.getChair();
//
//        }
//
//        // choose random group to join
//
//        final Random l_rand = new Random();
//        final CChairAgent l_randomChair = m_activechairs.get( l_rand.nextInt( m_activechairs.size() ) );
//
//        if ( this.containsnot( l_randomChair, p_votingAgent ) )
//        {
//
//            m_chairgroup.get( l_randomChair ).add( p_votingAgent );
//            System.out.println( p_votingAgent.name() + " joins group with ID " + m_groupIds.get( l_randomChair ) );
//            // chair " + l_randomChair );
//
//            this.wakeUpAgent();
//
//            if ( m_chairgroup.get( l_randomChair ).size() == m_capacity )
//            {
//
//                m_activechairs.remove( l_randomChair );
//
//                for ( int i = 0; i < m_capacity; i++ )
//                    System.out.println( m_chairgroup.get( l_randomChair ).get( i ).name() + " with chair " + l_randomChair );
//
//                System.out.println( "trigger election " );
//
//                final ITrigger l_triggerStart = CTrigger.from(
//                    ITrigger.EType.ADDGOAL,
//                    CLiteral.from(
//                        "start/criterion/fulfilled" )
//
//                );
//
//                l_randomChair.trigger( l_triggerStart );
//
//            }
//
//            return l_randomChair;
//        }
//
//        // if the agent is already in the group, just return the chair agent
//        else
//        {
//            return l_randomChair;
//        }

        return null;
    }

    private void openNewGroup( final IContext p_context )
    {
        final CVotingAgent l_votingAgent = (CVotingAgent) p_context.agent();

        l_votingAgent.getChair().sleep( 0 );

        l_votingAgent.beliefbase().add( CLiteral.from( "group", CRawTerm.from( l_votingAgent.getChair() ) ) );

   /*     final List l_list = new LinkedList<CVotingAgent>();
        l_list.add( l_votingAgent );

        m_chairgroup.put( l_votingAgent.getChair(), l_list );
        m_activechairs.add( l_votingAgent.getChair() );
        m_groupIds.put( l_votingAgent.getChair(), m_groupId );*/

 /*       final List l_initalList = new LinkedList<AtomicIntegerArray>();
        m_voteSets.put( l_votingAgent.getChair(), l_initalList );

        final List l_initialDissList = new LinkedList<Double>();
        m_dissSets.put( l_votingAgent.getChair(), l_initialDissList );*/

        if ( "COORDINATED".equals( m_grouping ) )
        {
            final ITrigger l_triggerStart = CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "start/criterion/fulfilled" )

            );

            l_votingAgent.getChair().trigger( l_triggerStart );

            System.out.println( "Coordinated Grouping " + l_votingAgent.name() + " opened group with chair " + l_votingAgent.getChair() );

        }

        System.out.println( l_votingAgent.name() + " opened group with chair " + l_votingAgent.getChair() );

    //    announceGroup( l_votingAgent.getChair() );

    }

}


