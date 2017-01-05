package org.lightvoting.simulation.agent;

import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;

import java.text.MessageFormat;

@IAgentAction
public final class CVotingAgent extends IBaseAgent<CVotingAgent> {

    // constructor of the agent
    // @param p_configuration agent configuration of the agent generator
    public CVotingAgent(final IAgentConfiguration<CVotingAgent> p_configuration )
    {
        super( p_configuration );
    }

    // an inner action inside the agent class,
    // with the annotation the method is marked as action
    // and the action-name for the ASL script is set
    // @param p_value argument of the action
    // @note LightJason supports Long and Double values, so if you declare
    // every numerical value as Number you can handle both types, because
    // number has methods to convert the data
    @IAgentActionFilter
    @IAgentActionName( name = "my/new-action" )
    private void myaction()
    {
        System.out.println( MessageFormat.format( "inner action is called by agent {0}", this ) );
    }


}
