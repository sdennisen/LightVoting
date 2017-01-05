package org.lightvoting;

import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.agent.CVotingAgentGenerator;

import java.io.FileInputStream;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CMain
{
    private CMain()
    {
    }

    public static void main( final String[] p_args ) throws Exception
    {
        // parameter of the command-line arguments:
        // 1. ASL file
        // 2. number of agents
        // 3. number of iterations (if not set maximum)
        final Set<CVotingAgent> l_agents;
        try
                (
                        final FileInputStream l_stream = new FileInputStream( p_args[0] );
                )
        {
            l_agents = new CVotingAgentGenerator( l_stream )
                    .generatemultiple( Integer.parseInt(p_args[1]) )
                    .collect( Collectors.toSet() );
        } catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
            return;
        }

        // runtime call (with parallel execution)
        IntStream
                .range( // cycle range
                        0,
                        p_args.length < 3
                                ? Integer.MAX_VALUE
                                : Integer.parseInt( p_args[2] )
                )
                .forEach( j -> l_agents.parallelStream().forEach( i -> {
                    try
                    {
                        // call agent
                        i.call();
                    } catch ( final Exception l_exception )
                    {
                        l_exception.printStackTrace();
                    }
                } ) );
    }
}