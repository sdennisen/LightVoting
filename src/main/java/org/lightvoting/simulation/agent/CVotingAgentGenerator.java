package org.lightvoting.simulation.agent;

import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.score.IAggregation;
import org.lightvoting.simulation.action.CId;

import java.util.stream.Stream;
import java.io.InputStream;
import java.util.stream.Collectors;


public final class CVotingAgentGenerator extends IBaseAgentGenerator<CVotingAgent>
{
    // constructor of the generator
    // @param p_stream ASL code as any stream e.g. FileInputStream
    public CVotingAgentGenerator( final InputStream p_stream ) throws Exception
    {
        super(
                // input ASL stream
                p_stream,

                // a set with all possible actions for the agent
                Stream.concat(
                        // we use all build-in actions of LightJason
                        CCommon.actionsFromPackage(),
                        Stream.concat(
                                // use the actions which are defined inside the agent class
                                CCommon.actionsFromAgentClass( CVotingAgent.class ),
                                // add an own external action
                                Stream.of(
                                        new CId(new Integer(0))
                                )
                        )
                        // build the set with a collector
                ).collect( Collectors.toSet() ),

                // aggregation function for the optimization function, here
                // we use an empty function
                IAggregation.EMPTY
        );
    }

    // generator method of the agent
    // @param p_data any data which can be put from outside to the generator method
    // @return returns an agent
    @Override
    public final CVotingAgent generatesingle( final Object... p_data )
    {
        return new CVotingAgent( m_configuration );
    }
}