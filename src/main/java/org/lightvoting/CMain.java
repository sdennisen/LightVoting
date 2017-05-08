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
import org.bytedeco.javacpp.hdf5.H5File;
import org.lightvoting.simulation.action.message.CSend;
import org.lightvoting.simulation.agent.CChairAgentGenerator;
import org.lightvoting.simulation.agent.CVotingAgent;
import org.lightvoting.simulation.agent.CVotingAgentGenerator;
import org.lightvoting.simulation.environment.CEnvironment;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* TODO: each possibility for drawing active agentd from the pool of agents needs to be a own class  */

/**
 * Main, providing runtime of LightVoting.
 */
public final class CMain
{
    private static CEnvironment s_environment;
    private static H5File s_h5file;

    // TODO later via config
    private static int s_AltNum = 6;
    private static String s_grouping = "RANDOM";
    private static String s_protocol = "BASIC";

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
        final Set<CVotingAgent> l_agents;
        final CVotingAgentGenerator l_votingagentgenerator;


        createHDF5();

        try
        {
            final FileInputStream l_stream = new FileInputStream( p_args[0] );
            final FileInputStream l_chairstream = new FileInputStream( p_args[1] );

            s_environment = new CEnvironment( Integer.parseInt( p_args[2] ), s_h5file );

            l_votingagentgenerator = new CVotingAgentGenerator( new CSend(), l_stream, s_environment, s_AltNum, s_grouping );
            l_agents = l_votingagentgenerator
                    .generatemultiple( Integer.parseInt( p_args[2] ), new CChairAgentGenerator( l_chairstream, s_environment, s_grouping, s_protocol )  )
                    .collect( Collectors.toSet() );


            System.out.println( " Numbers of agents: " + l_agents.size() );


        }
        catch ( final Exception l_exception )
        {
            l_exception.printStackTrace();
            throw new RuntimeException();
        }

        // generate empty set of active agents

        final Set<CVotingAgent> l_activeAgents = Sets.newConcurrentHashSet();

        System.out.println( " Numbers of active agents: " + l_activeAgents.size() );

        System.out.println( " Numbers of active agents: " + l_activeAgents.size() );
        System.out.println( " Will run " + p_args[3] + " cycles." );

        IntStream
            // define cycle range, i.e. number of cycles to run sequentially
            .range( 0,
                    p_args.length < 4
                    ? Integer.MAX_VALUE
                    : Integer.parseInt( p_args[3] ) )
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

    // source: https://github.com/bytedeco/javacpp-presets/tree/master/hdf5#the-srcmainjavah5tutrcmprssjava-source-file

    private static void createHDF5()
    {
        final String l_fileName = "results.h5";

        // Create a new file.
        try
        {
            s_h5file = new H5File( l_fileName, org.bytedeco.javacpp.hdf5.H5F_ACC_TRUNC );
            s_h5file.close();
        }
        catch ( final Exception l_ex )
        {
            l_ex.printStackTrace();
        }

    }


}


// XXXXXXX Old Code XXXXXXX
//     private static Iterator<CVotingAgent> s_agentIterator;

// ---- was in constructor ----
//           s_agentIterator = l_agents.iterator();


// runtime call (with parallel execution)

//        // set cycle number in environment
//        s_environment.setCycles( p_args.length < 4
//                               ? Integer.MAX_VALUE
//                                : Integer.parseInt( p_args[3] ) );

// wake up first agent
//        final CVotingAgent l_firstAgent = l_agents.iterator().next();
//
//        l_firstAgent.sleep( 0 );
//        l_firstAgent.getChair().sleep( 0 );

//<<<<<<< HEAD
//                // if you want to do something in cycle j, put it here - in this case, activate three new agents
//                // addAgents( l_activeAgents, 3, s_agentIterator );
//
//                addAgents( l_activeAgents, 1, s_agentIterator );
//                s_environment.setReady( false );
//                System.out.println( "After Cycle " + j + ": Numbers of active agents: " + l_activeAgents.size() );
//=======

// add first agent

//        addAgents( l_activeAgents, 1, s_agentIterator );
//
//        // call agent until first Result is computed
//        while ( !( s_environment.getResultComputed() ) )
//
//            try
//            {
//                // check if the conditions for triggering a new cycle are fulfilled in the environment
//
//
//                // call each agent, i.e. trigger a new agent cycle
//                final CVotingAgent l_votingAgent = l_activeAgents.iterator().next();
//
//                l_votingAgent.call();
//                l_votingAgent.getChair().sleep( 0 );
//                l_votingAgent.getChair().call();
//
//            }
//            catch ( final Exception l_exception )
//            {
//                l_exception.printStackTrace();
//                throw new RuntimeException();
//            }

/*
        IntStream
       //  define cycle range, i.e. number of cycles to run sequentially
                   .range( 0,
                           p_args.length < 4
                           ? Integer.MAX_VALUE
                           : Integer.parseInt( p_args[3] ) )
                   .forEach( j ->

    //    while ( s_environment.getReady() )
        {
            System.out.println( "Cycle " + j );
            addAgents( l_activeAgents, 1, s_agentIterator );
            s_environment.setResultComputed( false );

            while ( !( s_environment.getResultComputed() ) )
            {

                l_activeAgents.parallelStream().forEach( i ->
                {
                    try
                    {
                        // check if the conditions for triggering a new cycle are fulfilled in the environment


                        // call each agent, i.e. trigger a new agent cycle

                        i.call();
                        i.getChair().sleep( 0 );
                        i.getChair().call();

                    }
                    catch ( final Exception l_exception )
                    {
                        l_exception.printStackTrace();
                        throw new RuntimeException();
                    }
                    System.out.println( " called " + i.name() );
                } );
            }
        });

*/

       /* // TODO fix failing agent calls

        IntStream
            // define cycle range, i.e. number of cycles to run sequentially
            .range( 0,
                    p_args.length < 4
                    ? Integer.MAX_VALUE
                    : Integer.parseInt( p_args[3] ) )
            .forEach( j ->
                      {
                          // if you want to do something in cycle j, put it here - in this case, activate three new agents
                          // addAgents( l_activeAgents, 3, s_agentIterator );

                          if ( s_environment.getReady() )
                          {
                              addAgents( l_activeAgents, 1, s_agentIterator );
                              s_environment.setReady( false );
                              System.out.println( "After Cycle " + j + ": Numbers of active agents: " + l_activeAgents.size() );
                          }
                          l_activeAgents.parallelStream().forEach( i ->
                                                                   {
                                                                       try
                                                                       {
                                                                           // check if the conditions for triggering a new cycle are fulfilled in the environment


                                                                           // call each agent, i.e. trigger a new agent cycle

                                                                           i.call();
                                                                           i.getChair().sleep( 0 );
                                                                           i.getChair().call();

                                                                       }
                                                                       catch ( final Exception l_exception )
                                                                       {
                                                                           l_exception.printStackTrace();
                                                                           throw new RuntimeException();
                                                                       }
                                                                   } );
                      } );*/


