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

import cern.colt.Arrays;
import com.google.common.util.concurrent.AtomicDoubleArray;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by sophie on 03.08.17.
 */
public final class CMainPreferences
{

    private static boolean s_manualPref;
    private static boolean s_sigmoidPref;
    private static int s_runs;
    private static List<AtomicDoubleArray> s_prefList = new ArrayList<>();
    private static int s_agNum;
    private static int s_altnum;
    private static String s_dis;
    private static boolean s_uniformPref;

    /**
     * Hidden constructor
     */
    private CMainPreferences()
    {
    }

    /**
     * Main
     * create preferences
     * @param p_args Passed command line args
     * @throws Exception exception
     */
    public static void main( final String[] p_args ) throws Exception
    {
        readYaml();

        final File l_preferenceFile = new File( "src/main/resources/org/lightvoting/preferences.yaml" );
        final PrintWriter l_writer = new PrintWriter( l_preferenceFile );
        l_writer.print( "runs:" );
        l_writer.close();

        for ( int r = 0; r < s_runs; r++ )
        {
            s_prefList.clear();
            setPreferences( r );

        }


        final File l_backupFile = new File( "target/backups/" + new Date() + "_ags:" + s_agNum + "_alts:" + s_altnum + "_runs:" + s_runs + "_dis:" + s_dis
                                            + "_" + "_preferences.yaml" );

        FileUtils.copyFile( l_preferenceFile, l_backupFile );


    }


    private static void setPreferences( final int p_run ) throws FileNotFoundException
    {
        if ( s_sigmoidPref )
        {
            s_dis = "sigmoid";
            s_prefList = createPreferencesSigmoid();
            writePreferences( p_run, s_prefList );
        }
        if ( s_uniformPref )
        {
            s_dis = "uniform";
            s_prefList = createPreferencesUniform();
            writePreferences( p_run, s_prefList );
        }

        if ( s_manualPref )
        {
            s_dis = "manually";
        }
    }

    private static List<AtomicDoubleArray> createPreferencesUniform()
    {
        final List<AtomicDoubleArray> l_arrays = new ArrayList<>();

        for ( int i = 0; i < s_agNum; i++ )
            l_arrays.add( generatePreferencesUniform() );
        return l_arrays;
    }

    private static AtomicDoubleArray generatePreferencesUniform()
    {
        final Random l_random = new Random();
        final double[] l_prefValues = new double[s_altnum];
        for ( int i = 0; i < s_altnum; i++ )
            l_prefValues[i] = Math.round( l_random.nextDouble() * 100.0 ) / 100.0;

        System.out.println( "Preference Values: " + Arrays.toString( l_prefValues ) );
        return new AtomicDoubleArray( l_prefValues );
    }

    private static List<AtomicDoubleArray> createPreferencesSigmoid()
    {
        final List<AtomicDoubleArray> l_arrays = new ArrayList<>();

        for ( int i = 0; i < s_agNum; i++ )
            l_arrays.add( generatePreferencesSigmoid() );
        return l_arrays;

    }

    private static AtomicDoubleArray generatePreferencesSigmoid()
    {
        final Random l_random = new Random();
        final double[] l_prefValues = new double[s_altnum];
        for ( int i = 0; i < s_altnum; i++ )
            l_prefValues[i] = sigmoidValue( l_random.nextDouble() - 0.5 );
        System.out.println( "Preference Values: " + Arrays.toString( l_prefValues ) );
        return new AtomicDoubleArray( l_prefValues );
    }

    private static double sigmoidValue( double p_var )
    {
        return Math.round( ( 1 / ( 1 + Math.pow( Math.E, -1 * p_var ) ) ) * 100.0 ) / 100.0;
    }


    /**
     * write preferences to yaml file
     * @param p_run run number
     * @param p_prefList preference list
     */
    private static void writePreferences( final int p_run, final List<AtomicDoubleArray> p_prefList )
    {
        final FileWriter l_file;
        try
        {
            l_file = new FileWriter( "src/main/resources/org/lightvoting/preferences.yaml", true );

            BufferedWriter writer = null;

            writer = new BufferedWriter( l_file );
            writer.write( "\n number_" + p_run + ":" );

            for ( int i = 0; i < s_prefList.size(); i++ )
            {
                writer.write( "\n   - " );
                writer.write( s_prefList.get( i ).toString() );
            }
            writer.close();
        }
        catch ( final IOException l_ex )
        {
            l_ex.printStackTrace();
        }
    }


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

                if ( "runs".equals( l_subValueKey ) )
                    s_runs = Integer.parseInt( l_subValues.get( l_subValueKey ) );
                else if ( "agnum".equals( l_subValueKey ) )
                    s_agNum = Integer.parseInt( l_subValues.get( l_subValueKey ) );
                else if ( "altnum".equals( l_subValueKey ) )
                    s_altnum = Integer.parseInt( l_subValues.get( l_subValueKey ) );

                else if ( ( "preferences".equals( l_subValueKey  ) ) && ( l_subValues.get( l_subValueKey ).equals( "manually" ) ) )
                    s_manualPref = true;
                else if ( ( "preferences".equals( l_subValueKey  ) ) && ( l_subValues.get( l_subValueKey ).equals( "sigmoid" ) ) )
                    s_sigmoidPref = true;
                else if ( ( "preferences".equals( l_subValueKey  ) ) && ( l_subValues.get( l_subValueKey ).equals( "uniform" ) ) )
                    s_uniformPref = true;

            }
        }
    }

}
