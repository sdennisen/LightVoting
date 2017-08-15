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

import com.google.common.util.concurrent.AtomicDoubleArray;
import org.apache.commons.io.FileUtils;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightvoting.simulation.action.message.random_basic.CSendRB;
import org.lightvoting.simulation.agent.random_basic.CBrokerAgentRB;
import org.lightvoting.simulation.agent.random_basic.CChairAgentRB;
import org.lightvoting.simulation.agent.random_basic.CVotingAgentRB;
import org.lightvoting.simulation.environment.random_basic.CEnvironmentRB;
import org.lightvoting.simulation.environment.random_iterative.CEnvironmentRI;
import org.lightvoting.simulation.statistics.EDataWriter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Main, providing runtime of LightVoting.
 */
public final class CMain
{
    private static CEnvironmentRB s_environmentRB;
    private static CEnvironmentRI s_environmentRI;

    private static int s_altnum;

    private static int s_runs;
    private static List<String> s_settingStrs = new ArrayList<>();
    private static List<String> s_groupings = new ArrayList<>();
    private static List<String> s_protocols = new ArrayList<>();
    private static String s_settingStr = "";
    private static double s_dissthr;
    private static int s_capacity;
    private static double s_joinThr;

    private static List<String> s_paths = new ArrayList<>();
    private static List<Object> s_data = new ArrayList<>();

    private static HashMap<String, Object> s_map =  new HashMap<>();
    private static List<AtomicDoubleArray> s_prefList = new ArrayList<>();
    private static int s_agNum;
    private static int s_comsize;
    // TODO restructure generation of broker instances
    private static CBrokerAgentRB s_brokerRandomBasic;
    private static CBrokerAgentRB.CBrokerAgentGenerator s_brokerGenerator;
    private static String s_dis;
    private static  String s_nameShort;
    private static String s_parameters;
    private static boolean s_randomBasic;

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
        // 2. number of iterations (if not set maximum)

    //    s_agNum = Integer.parseInt( p_args[2] );

        // creater BrokerAgent

        readYaml();

        s_parameters = "_ags:" + s_agNum + "_alts:" + s_altnum + "_capacity:" + s_capacity + "_comsize:" + s_comsize + "_runs:" + s_runs + "_dis:" + s_dis;

        s_nameShort = "target/results/" + new Date() + s_parameters;

        final String l_name = s_nameShort + "_results.h5";

        final String l_path = "runs" + "/" + "run num";
        s_map.put( l_path, s_runs );

        // clear file

        final File l_preferenceFile;

//        l_preferenceFile = new File( "src/main/resources/org/lightvoting/preferences.yaml" );
//        final PrintWriter l_writer = new PrintWriter( l_preferenceFile );
//        l_writer.print( "runs:" );
//        l_writer.close();

       // create preferences for all runs before conducting the runs
//        for ( int r = 0; r < s_runs; r++ )
//        {
//            s_prefList.clear();
//            setPreferences( r );
//
//        }

        for ( int c = 0; c < s_settingStrs.size(); c++ )
        {
            System.out.println( "setting: " + s_settingStrs.get( c ) );

            for ( int r = 0; r < s_runs; r++ )
            {
                readPreferences( r );
                System.out.println( s_prefList );

                // TODO store grouping, protocol and rule
                s_map.put( s_settingStrs.get( c ) + "/" + String.valueOf( r ) + "/config", "ags:" + s_agNum + "_alts:" + s_altnum + "_runs:" + s_runs + "_dis:" + s_dis );

                final Set<CVotingAgentRB> l_agents;
                final CVotingAgentRB.CVotingAgentGenerator l_votingagentgenerator;

                // TODO reinsert?
                //            final String l_pathConfNr = r + "/" + "configs" + "/" + "config num";
                //            s_map.put( l_pathConfNr, s_settingStrs.size() );
                //            final String l_pathConfStr = r + "/" + "confignames" + "/" + "config names";
                //
                //            s_map.put( l_pathConfStr, s_settingStr );

                try
                {
//                    final FileInputStream l_stream = new FileInputStream( p_args[0] );
//                    final FileInputStream l_chairstream = new FileInputStream( p_args[1] );


                    s_environmentRB = new CEnvironmentRB( Integer.parseInt( p_args[0] ), l_name, s_capacity );

                    // TODO separate creation of broker and setting of parameters
                    if  ( s_settingStrs.get( c ).contains( "RANDOM_BASIC" ) && s_randomBasic )
                    {
                        final FileInputStream l_stream = new FileInputStream( "src/main/resources/org/lightvoting/traveller_rb.asl" );
                        final FileInputStream l_chairstream = new FileInputStream( "src/main/resources/org/lightvoting/chair_rb.asl" );

                        final String l_brokerRB = "src/main/resources/org/lightvoting/broker_rb.asl";

                        createBrokerRandomBasic( l_brokerRB, l_chairstream, s_agNum, new CSendRB(), l_stream, s_environmentRB, s_altnum, l_name, s_joinThr, s_prefList );

                        l_stream.close();
                        l_chairstream.close();
                    }
                    //    l_votingagentgenerator = new CVotingAgentRB.CVotingAgentGenerator( new CSendRB(), l_stream, s_environment, s_altnum, l_name, s_joinThr, s_prefList );
                    //    l_agents = l_votingagentgenerator
                    //        .generatemultiplenew(
                    //    Integer.parseInt( p_args[2] ), new CChairAgent.CChairAgentGenerator( l_chairstream, s_environment, l_name, r, s_dissthr, s_comsize, s_altnum ) )
                    //        .collect( Collectors.toSet() );


                }
                catch ( final Exception l_exception )
                {
                    l_exception.printStackTrace();
                    throw new RuntimeException();
                }

                //            for ( int c = 0; c < s_settingStrs.size(); c++ )
                //            {
                //
                //                s_environment.setConf( r, s_settingStrs.get( c ) );
                //
                //                final int l_finalC = c;


                //                l_agents.parallelStream().forEach( i ->
                //                {
                //                    i.setConf( s_groupings.get( l_finalC ) );
                //                    i.getChair().setConf( s_settingStrs.get( l_finalC ), s_groupings.get( l_finalC ), s_protocols.get( l_finalC ) );
                //                } );

                IntStream
                    // define cycle range, i.e. number of cycles to run sequentially
                    .range(
                        0,
                        p_args.length < 1
                        ? Integer.MAX_VALUE
                        : Integer.parseInt( p_args[0] )
                    )
                    .forEach( j ->
                    {
                        System.out.println( "Cycle " + j );
                        try
                        {
                            s_brokerRandomBasic.call();
                            s_brokerRandomBasic.agentstream().forEach( k ->
                            {
                                try
                                {
                                    k.call();
                                }
                                catch ( final Exception l_ex )
                                {
                                    l_ex.printStackTrace();
                                }
                            } );
                        }
                        catch ( final Exception l_ex )
                        {
                            l_ex.printStackTrace();
                        }

                                  //                        l_agents.parallelStream().forEach( i ->
                                  //                        {
                                  //                            try
                                  //                            {
                                  //                                // check if the conditions for triggering a new cycle are fulfilled in the environment
                                  //                                // call each agent, i.e. trigger a new agent cycle
                                  //                                i.call();
                                  //                                //   i.getChair().sleep( 0 );
                                  //                                i.getChair().call();
                                  //                            }
                                  //                            catch ( final Exception l_exception )
                                  //                            {
                                  //                                l_exception.printStackTrace();
                                  //                                throw new RuntimeException();
                                  //                            }
                                  //                        } );
                    } );

                // reset properties for next configuration

                final int l_finalR = r;
                final int l_finalC = c;
                s_brokerRandomBasic.agentstream().forEach( k ->
                {
                    if ( k instanceof CVotingAgentRB ) append( s_map, ( (CVotingAgentRB) k ).map(), s_settingStrs.get( l_finalC ), l_finalR );
                    if ( k instanceof CChairAgentRB ) append( s_map, ( (CChairAgentRB) k ).map(), s_settingStrs.get( l_finalC ), l_finalR );
                } );
                // TODO necessary?
                s_environmentRB.reset();
            }
            //   System.out.println( "Next simulation run " );
        }

        EDataWriter.INSTANCE.storeMap( l_name, s_map );

    }

    private static void append( final HashMap<String, Object> p_map, final HashMap<String, Object> p_agentmap, final String p_setting, final int p_run )
    {
        for ( final String l_key : p_agentmap.keySet() )

            p_map.put( p_setting + "/" + p_run + "/" + l_key, p_agentmap.get( l_key ) );

    }

    private static void createBrokerRandomBasic( final String p_arg, final FileInputStream p_chrStream, final int p_agNum, final CSendRB p_send,
                                      final FileInputStream p_stream,
                                      final CEnvironmentRB p_environment,
                                      final int p_altnum, final String p_name,
                                      final double p_joinThr,
                                      final List<AtomicDoubleArray> p_prefList
    ) throws Exception
    {
        final FileInputStream l_bkStr = new FileInputStream( p_arg );

        // TODO modify parameters
        s_brokerGenerator = new CBrokerAgentRB.CBrokerAgentGenerator( p_send,
                                                                      l_bkStr,
                                                                      p_agNum,
                                                                      p_stream,
                                                                      p_chrStream,
                                                                      s_environmentRB,
                                                                      s_altnum,
                                                                      p_name,
                                                                      s_joinThr,
                                                                      s_prefList,
                                                                      s_comsize
        );

        s_brokerRandomBasic = s_brokerGenerator.generatesingle();
    }

    private static void storeResults( final Set<CVotingAgentRB> p_agents )
    {
        p_agents.parallelStream().forEach( i ->
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
            s_environmentRB.initialset( i );
            i.reset();
            i.getChair().reset();
            s_map.putAll( i.getChair().map() );
        } );
        s_map.putAll( s_environmentRB.map() );
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
                else if ( "agnum".equals( l_subValueKey ) )
                    s_agNum = Integer.parseInt( l_subValues.get( l_subValueKey ) );
                else if ( "altnum".equals( l_subValueKey ) )
                    s_altnum = Integer.parseInt( l_subValues.get( l_subValueKey ) );
                else if ( "comsize".equals( l_subValueKey ) )
                    s_comsize = Integer.parseInt( l_subValues.get( l_subValueKey ) );

                else if ( l_subValueKey.contains( "setting" ) )
                {
                    s_settingStrs.add( l_subValues.get( l_subValueKey ) );
                    s_settingStr = s_settingStr.concat( " " + l_subValues.get( l_subValueKey ) );
                    final String[] l_confStr = l_subValues.get( l_subValueKey ).split( "_" );
                    s_groupings.add( l_confStr[0] );
                    System.out.println( l_confStr[0] );
                    s_protocols.add( l_confStr[1] );
                    System.out.println( l_confStr[1] );

                    if ( l_subValues.get( l_subValueKey ).contains( "RANDOM_BASIC" ) )
                        s_randomBasic = true;


                }

                else if ( "dissthr".equals( l_subValueKey  ) )
                    s_dissthr = Double.parseDouble( l_subValues.get( l_subValueKey ) );

                else if ( "capacity".equals( l_subValueKey ) )
                    s_capacity = Integer.parseInt( l_subValues.get( l_subValueKey ) );

                else if ( "jointhr".equals( l_subValueKey  ) )
                    s_joinThr = Double.parseDouble( l_subValues.get( l_subValueKey ) );
                else if ( ( "preferences".equals( l_subValueKey  ) ) && ( l_subValues.get( l_subValueKey ).equals( "manually" ) ) )
                    s_dis = "manually";
                else if ( ( "preferences".equals( l_subValueKey  ) ) && ( l_subValues.get( l_subValueKey ).equals( "sigmoid" ) ) )
                    s_dis = "sigmoid";
                else if ( ( "preferences".equals( l_subValueKey  ) ) && ( l_subValues.get( l_subValueKey ).equals( "uniform" ) ) )
                    s_dis = "uniform";
                else if ( ( "preferences".equals( l_subValueKey  ) ) && ( l_subValues.get( l_subValueKey ).equals( "FScluster" ) ) )
                    s_dis = "FSCluster";

            }
        }

    }

    private static void readPreferences( final int p_run ) throws IOException
    {
        final Yaml l_yaml = new Yaml();
        final ArrayList<ArrayList<Double>> l_list = new ArrayList<>();

        final Map<String, Map<String, Map<String, Map<String, ArrayList<Double>>>>> l_values = (Map<String, Map<String, Map<String, Map<String, ArrayList<Double>>>>>) l_yaml
            .load( new FileInputStream( "src/main/resources/org/lightvoting/preferences.yaml" ) );

        // store preferences yaml for reproducing results

        final File l_preferenceFile = FileUtils.getFile( "src/main/resources/org/lightvoting/preferences.yaml" );

        final File l_backupFile = new File( s_nameShort + "_preferences.yaml" );

        FileUtils.copyFile( l_preferenceFile, l_backupFile );

        for ( final String l_key : l_values.keySet() )
        {
            final Map<String, Map<String, Map<String, ArrayList<Double>>>> l_numbers = l_values.get( l_key );
            System.out.println( l_numbers );

            final Map<String, Map<String, ArrayList<Double>>> l_agents = l_numbers.get( "number_" + p_run );

            System.out.println( l_agents );
            // System.out.println( "run " +  l_subkey.split( "_" )[1] );

            System.out.println( "run " + p_run );

            final HashMap<Integer, ArrayList<Double>> l_preferencesMap = new HashMap<>();

            for ( final String l_agent : l_agents.keySet()  )
            {
                final Map<String, ArrayList<Double>> l_preferences = l_agents.get( l_agent );
                ArrayList<Double> l_preferenceVector = new ArrayList<>();
                System.out.println( l_agent + ":" + l_preferences );
                // determine agent id
                final Integer l_id = Integer.parseInt( l_agent.split( "_" )[1] );

                for ( final String l_preference: l_preferences.keySet() )
                {
                    if ( l_preference.contains( "poi_preferences" ) )
                    {
                        l_preferenceVector = l_preferences.get( l_preference );
                        l_preferencesMap.put( l_id, l_preferenceVector );
                        // l_list.add( l_preferenceVector );
                    }
                }
            }

            // transfer HashMap to list

            for ( int i = 0; i < s_agNum; i++ )
                l_list.add( l_preferencesMap.get( i ) );

            readPreferenceList( l_list );

        }

    }


//            for ( final String l_number : l_numbers.keySet() )
//            {
//                // System.out.println( String.valueOf( l_subValues.get( l_subkey ) ) );
//                if ( l_number.contains( "number" ) )
//                {
//                    final Map<String, Map<String, ArrayList<Double>>> l_agents = l_numbers.get( l_number );
//                    System.out.println( l_agents );
//                    // System.out.println( "run " +  l_subkey.split( "_" )[1] );
//
//                    if ( Integer.parseInt( l_number.split( "_" )[1] ) == p_run )
//                    {
//                        System.out.println( "run " +  l_number.split( "_" )[1] );
//
//                        for ( final String l_agent : l_agents.keySet()  )
//                        {
//                            final Map<String, ArrayList<Double>> l_preferences = l_agents.get( l_agent );
//                            ArrayList<Double> l_preferenceVector = new ArrayList<>();
//                            System.out.println( l_preferences );
//
//                            for ( final String l_preference: l_preferences.keySet() )
//                            {
//                                if ( l_preference.contains( "poi_preferences" ) )
//                                {
//                                    l_preferenceVector = l_preferences.get( l_preference );
//                                    l_list.add( l_preferenceVector );
//                                }
//                            }
//
//                            readPreferenceList( l_list );
//                        }
//
//                    }
//                }
//
//            }
//        }
//    }

    // old method
//    private static void readPreferences( final int p_run ) throws FileNotFoundException
//    {
//        final Yaml l_yaml = new Yaml();
//
//        final Map<String, Map<String, Object>> l_values = (Map<String, Map<String, Object>>) l_yaml
//            .load( new FileInputStream( "src/main/resources/org/lightvoting/preferences.yaml" ) );
//
//        for ( final String l_key : l_values.keySet() )
//        {
//            final Map<String, Object> l_subValues = l_values.get( l_key );
//            System.out.println( l_subValues );
//
//
//            for ( final String l_subkey : l_subValues.keySet() )
//            {
//                // System.out.println( String.valueOf( l_subValues.get( l_subkey ) ) );
//                if ( l_subkey.contains( "number" ) )
//                {
//                    // System.out.println( "run " +  l_subkey.split( "_" )[1] );
//
//                    if ( Integer.parseInt( l_subkey.split( "_" )[1] ) == p_run )
//                    {
//                        System.out.println( "run " +  l_subkey.split( "_" )[1] );
//                        final ArrayList<ArrayList<Double>> l_list = (ArrayList<ArrayList<Double>>) l_subValues.get( l_subkey );
//
//                        readPreferenceList( l_list );
//
//                    }
//                }
//            }
//        }
//    }

    private static void readPreferenceList( final ArrayList<ArrayList<Double>> p_list )
    {
        s_prefList.clear();

        for ( int i = 0; i < p_list.size(); i++ )
        {
            System
                .out.println( p_list.get( i ) );
            final double[] l_double = new double[p_list.get( i ).size()];

            for ( int j = 0; j < p_list.get( i ).size(); j++ )
                l_double[j] = p_list.get( i ).get( j );

            s_prefList.add( new AtomicDoubleArray( l_double ) );
        }

        System.out.println( "new preference list:" + s_prefList );
    }
}
