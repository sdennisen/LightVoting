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

package org.lightvoting;

import com.google.common.collect.Sets;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightvoting.simulation.action.message.CSend;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.environment.CEnvironment;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Main, providing runtime of LightVoting.
 */
public final class CMain
{
    private static CEnvironment s_environment;
    private static int s_altnum;

    private static int s_runs;
    private static int s_configs;
    private static String s_grouping;
    private static String s_protocol;


    /**
     * Hidden constructor
     */
    private CMain()
    {
    }

    /**
     * Main
     * @param p_args Passed command line args: [ASL File] [Number of Agents] [Cycles]
     * @throws Exception Throws exception, e.g. on reading ASL file
     */
    public static void main( final String[] p_args ) throws Exception
    {
        // Example code taken from
        // https://lightjason.github.io/tutorial/tutorial-agentspeak-in-fifteen-minutes/
        //
        // parameter of the command-line arguments:
        // 1. ASL file
        // 2. number of agents
        // 3. number of iterations (if not set maximum)

        readYaml();

        for ( int r = 0; r < s_runs; r++ )
        {


            final Set<CVotingAgent> l_agents;
            final CVotingAgent.CVotingAgentGenerator l_votingagentgenerator;
            final String l_name = "results.h5";

            try
            {
                final FileInputStream l_stream = new FileInputStream( p_args[0] );
                final FileInputStream l_chairstream = new FileInputStream( p_args[1] );

                s_environment = new CEnvironment( Integer.parseInt( p_args[2] ), l_name );

                l_votingagentgenerator = new CVotingAgent.CVotingAgentGenerator( new CSend(), l_stream, s_environment, s_altnum, s_grouping, l_name );
                l_agents = l_votingagentgenerator
                    .generatemultiple(
                        Integer.parseInt( p_args[2] ), new CChairAgent.CChairAgentGenerator( l_chairstream, s_environment, s_grouping, s_protocol, l_name, r ) )
                    .collect( Collectors.toSet() );


                System.out.println( " Numbers of agents: " + l_agents.size() );
            }
            catch ( final Exception l_exception )
            {
                l_exception.printStackTrace();
                throw new RuntimeException();
            }


            for ( int c = 0; c < s_configs; c++ )
            {
                // generate empty set of active agents

                final Set<CVotingAgent> l_activeAgents = Sets.newConcurrentHashSet();

                System.out.println( " Numbers of active agents: " + l_activeAgents.size() );

                System.out.println( " Numbers of active agents: " + l_activeAgents.size() );
                System.out.println( " Will run " + p_args[3] + " cycles." );

                IntStream
                    // define cycle range, i.e. number of cycles to run sequentially
                    .range(
                        0,
                        p_args.length < 4
                        ? Integer.MAX_VALUE
                        : Integer.parseInt( p_args[3] )
                    )
                    .forEach( j ->
                    {
                        System.out.println( "Global cycle: " + j );
                        l_agents.parallelStream().forEach( i ->
                        {
                            try
                            {
                                // check if the conditions for triggering a new cycle are fulfilled in the environment
                                // call each agent, i.e. trigger a new agent cycle
                                i.call();
                                //   i.getChair().sleep( 0 );
                                i.getChair().call();
                            }
                            catch ( final Exception l_exception )
                            {
                                l_exception.printStackTrace();
                                throw new RuntimeException();
                            }
                        } );
                    } );

                System.out.println( " Next configuration " );

                // reset properties for next configuration

                s_environment.reset();

                l_agents.parallelStream().forEach( i ->
                {
                    i.sleep( Integer.MAX_VALUE );
                    i.getChair().beliefbase().beliefbase().clear();
                    i.getChair().sleep( Integer.MAX_VALUE );
                    i.beliefbase().beliefbase().clear();
                    i.storage().put( "chair", i.getChair().raw() );
                    i.beliefbase().add(
                        CLiteral.from(
                            "chair",
                            CRawTerm.from( i.getChair() )
                        )
                    );
                    s_environment.initialset( i );
                    i.reset();
                    i.getChair().reset();
                } );
            }

            System.out.println( "Next simulation run " );
        }
    }

    @SuppressWarnings( "unchecked" )
    private static void readYaml() throws FileNotFoundException
    {
        final Yaml l_yaml = new Yaml();

        System.out.println( l_yaml.dump( l_yaml.load( new FileInputStream( "src/main/resources/org/lightvoting/configuration.yaml" ) ) ) );

        final Map<String, Map<String, String>> l_values = (Map<String, Map<String, String>>) l_yaml
            .load( new FileInputStream( "src/main/resources/org/lightvoting/configuration.yaml" ) );

        for ( final String l_key : l_values.keySet() )
        {
            final Map<String, String> l_subValues = l_values.get( l_key );
            System.out.println( l_key );

            for ( final String l_subValueKey : l_subValues.keySet() )
            {
                System.out.println( String.format( "\t%s = %s",
                                                   l_subValueKey, l_subValues.get( l_subValueKey ) ) );

                // parse input
                if ( "runs".equals( l_subValueKey ) )
                    s_runs = Integer.parseInt( l_subValues.get( l_subValueKey ) );
                if ( "configs".equals( l_subValueKey ) )
                    s_configs = Integer.parseInt( l_subValues.get( l_subValueKey ) );
                if ( "grouping".equals( l_subValueKey ) )
                    s_grouping = l_subValues.get( l_subValueKey );
                if ( "protocol".equals( l_subValueKey ) )
                    s_protocol = l_subValues.get( l_subValueKey );
                if ( "altnum".equals( l_subValueKey ) )
                    s_altnum = Integer.parseInt( l_subValues.get( l_subValueKey ) );
            }
        }
    }

    private static void addAgents( final Collection<CVotingAgent> p_activeAgents, final int p_newAgNum, final Iterator<CVotingAgent> p_agentIterator  )
    {

        for ( int i = 0; i < p_newAgNum; i++ )
        {
            if ( p_agentIterator.hasNext() )
            {
                final CVotingAgent l_curAg = p_agentIterator.next();
                p_activeAgents.add( l_curAg );
                System.out.println( "added Agent " + l_curAg.name() );
                p_agentIterator.remove();

            }
        }

    }


}
