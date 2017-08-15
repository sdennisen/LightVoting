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

package org.lightvoting.simulation.statistics;

import cern.colt.bitvector.BitVector;
import com.google.common.util.concurrent.AtomicDoubleArray;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.LongPointer;
import org.bytedeco.javacpp.hdf5;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by sophie on 21.06.17.
 */
public enum EDataWriter
{
    INSTANCE;

    private static hdf5.H5File s_file;
    private static int s_groups;

    /**
     * write data from data list to h5
     * @param p_name name of h5 file
     * @param p_map map to be written to h5
     */
    public void storeMap( final String p_name, final HashMap<String, Object> p_map )
    {
        s_file = new hdf5.H5File( p_name, hdf5.H5F_ACC_TRUNC );
        s_file.openFile( p_name, hdf5.H5F_ACC_RDWR );

        final Iterator l_iterator = p_map.entrySet().iterator();

        while ( l_iterator.hasNext() )
        {
            final Map.Entry l_entry = (Map.Entry)l_iterator.next();
            this.writeDataSet( (String) l_entry.getKey(), l_entry.getValue() );
        }

        s_file.close();
    }

    private void writeDataSet( final String p_path, final Object p_data )
    {
        String[] l_path;

        System.out.println( "String: " + p_path );
        l_path = p_path.split( "/" );
        // last name in path is dataset name
        final String l_datasetName = l_path[l_path.length - 1];

        hdf5.Group l_group = null;

        try
        {
            l_group = s_file.asCommonFG().openGroup( l_path[0] );
        }
        catch ( final Exception l_ex )
        {
        }
        finally
        {
            if ( l_group == null )
                l_group = s_file.asCommonFG().createGroup( l_path[0] );
        }


        for ( int i = 1; i < l_path.length - 1; i++ )
        {
            hdf5.Group l_newGroup = null;

            try
            {
                System.out.println( "trying to open " + l_path[i] );
                l_newGroup = l_group.asCommonFG().openGroup( l_path[i] );
            }
            catch ( final Exception l_ex )
            {
            }
            finally
            {
                if ( l_newGroup == null )
                    l_newGroup = l_group.asCommonFG().createGroup( l_path[i] );
            }


            l_group = l_newGroup;

        }

        this.writeData( l_group, l_datasetName, p_data );

    }

    private void writeData( final hdf5.Group p_group, final String p_datasetName, final Object p_data )
    {

        if ( p_data instanceof BitVector )
            this.writeBitVector( p_group, p_datasetName, (BitVector) p_data );


        if ( p_data instanceof AtomicDoubleArray )
            this.writeAtomicDoubleArray( p_group, p_datasetName, (AtomicDoubleArray) p_data );

        if ( p_data instanceof Integer )
            this.writeInteger( p_group, p_datasetName, (Integer) p_data );

        if ( p_data instanceof Double )
            this.writeDouble( p_group, p_datasetName, (Double) p_data );

        if ( p_data instanceof Long )
            this.writeLong( p_group, p_datasetName, (Long) p_data );

        if ( p_data instanceof String )
            this.writeString( p_group, p_datasetName, (String) p_data );
    }

    private void writeAtomicDoubleArray( final hdf5.Group p_group, final String p_datasetName, final AtomicDoubleArray p_data )
    {

        final hdf5.PredType l_predType = hdf5.PredType.C_S1();
        l_predType.setSize( 256 );

        final int l_length = p_data.length();
        final AtomicDoubleArray l_array = p_data;

        final hdf5.DataSet l_dataSet =  p_group.asCommonFG().createDataSet( p_datasetName, new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ),
                                                                            new hdf5.DataSpace( 2, new long[]{1, l_length,} ), new hdf5.DSetCreatPropList()

        );

        final double[] l_buf = new double[l_length];

        for ( int i = 0; i < l_length; i++ )
        {
            System.out.println( " Setting buf[" + i + "] to " + l_array.get( i ) );
            l_buf[i] = l_array.get( i );
        }

        l_dataSet.write( new DoublePointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ) );

    }


    private void writeBitVector( final hdf5.Group p_group, final String p_datasetName, final BitVector p_data )
    {

        final hdf5.PredType l_predType = hdf5.PredType.C_S1();
        l_predType.setSize( 256 );

        final hdf5.DataSet l_dataSet = p_group.asCommonFG().createDataSet( p_datasetName, l_predType,
                                                                           new hdf5.DataSpace( 1, new long[]{1} )
        );

        l_dataSet.write( new BytePointer( p_data.toString() ), new hdf5.DataType( hdf5.PredType.C_S1() ) );
    }

    private void writeInteger( final hdf5.Group p_group, final String p_datasetName, final Integer p_data )

    {
        final hdf5.DataSet l_dataSet = new hdf5.DataSet(
            p_group.asCommonFG().createDataSet( p_datasetName, new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
                                                new hdf5.DataSpace( 2, new long[]{1, 1} ), new hdf5.DSetCreatPropList()
            )
        );

        final int[] l_buf = new int[1];

        l_buf[0] = p_data;

        l_dataSet.write( new IntPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
    }

    private void writeDouble( final hdf5.Group p_group, final String p_datasetName, final Double p_data )
    {
        final hdf5.DataSet l_dataSet = new hdf5.DataSet(
            p_group.asCommonFG().createDataSet( p_datasetName, new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ),
                                                new hdf5.DataSpace( 2, new long[]{1, 1} ), new hdf5.DSetCreatPropList()
            )
        );

        final double[] l_buf = new double[1];

        l_buf[0] = Math.round( p_data * 100.0 ) / 100.0;

        l_dataSet.write( new DoublePointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ) );
    }

    private void writeLong( final hdf5.Group p_group, final String p_datasetName, final Long p_data )
    {
        final hdf5.DataSet l_dataSet = new hdf5.DataSet(
            p_group.asCommonFG().createDataSet( p_datasetName, new hdf5.DataType( hdf5.PredType.NATIVE_LONG() ),
                                                new hdf5.DataSpace( 2, new long[]{1, 1} ), new hdf5.DSetCreatPropList()
            )
        );

        final long[] l_buf = new long[1];

        l_buf[0] = p_data;

        l_dataSet.write( new LongPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_LONG() ) );
    }


    private void writeString( final hdf5.Group p_group, final String p_datasetName, final String p_data )
    {

        final hdf5.PredType l_predType = hdf5.PredType.C_S1();
        l_predType.setSize( 256 );
        final hdf5.DataSet l_configNamesDataSet =  p_group.asCommonFG().createDataSet( p_datasetName, l_predType,
                                                                                       new hdf5.DataSpace( 1, new long[]{1} )
        );

        l_configNamesDataSet.write( new BytePointer( p_data ), new hdf5.DataType( hdf5.PredType.C_S1() ) );

    }
    //    /**
//     * create h5 file
//     * @param p_filename name of file
//     * @return this
//     */
//    public final EDataWriter createHDF5( final String p_filename )
//    {
//
//        // Create file.
//        try
//        {
//            s_file = new hdf5.H5File( p_filename, hdf5.H5F_ACC_TRUNC );
//            s_file.openFile( p_filename, hdf5.H5F_ACC_RDWR );
//
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//
//        return this;
//    }
//
//    /**
//     * set # runs
//     * @param p_runs number of runs
//     * @return this
//     */
//
//    public EDataWriter setRunNum( final int p_runs )
//    {
//        try
//        {
//
//            s_file.asCommonFG().createGroup( "runs" );
//            final hdf5.DataSet l_runDataSet = s_file.asCommonFG().openGroup( String.valueOf( "runs" ) ).asCommonFG()
//                                                    .createDataSet( "run num", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
//                                                                    new hdf5.DataSpace( 2, new long[]{1, 1} )
//                                                    );
//
//            final int[] l_runBuf = new int[1];
//            l_runBuf[0] = p_runs;
//            l_runDataSet.write( new IntPointer( l_runBuf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//
//        return this;
//    }
//
//
//    /**
//     * close h5 file
//     * @param p_name name of file
//     */
//    public void closeHDF5( final String p_name )
//    {
//        s_file.close();
//    }
//
//    /**
//     * create group in hdf5 for run
//     * @param p_run run id
//     * @param p_configNum config num
//     * @param p_configStr config string
//     * @return this
//     */
//    public EDataWriter setRun( final int p_run, final int p_configNum, final String p_configStr )
//    {
//        try
//        {
//            EDataWriter.s_file.asCommonFG().createGroup( String.valueOf( p_run ) );
//            EDataWriter.s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().createGroup( "configs" );
//            final hdf5.DataSet l_configDataSet = s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( "configs" ).asCommonFG()
//                                                       .createDataSet( "config num", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
//                                                                       new hdf5.DataSpace( 2, new long[]{1, 1} )
//                                                       );
//
//            final int[] l_configBuf = new int[1];
//            l_configBuf[0] = p_configNum;
//            l_configDataSet.write( new IntPointer( l_configBuf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
//
//
//            s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().createGroup( "confignames" );
//            final hdf5.PredType l_predType = hdf5.PredType.C_S1();
//            l_predType.setSize( 256 );
//
//            final hdf5.DataSet l_configNamesDataSet = s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( "confignames" )
//                                                            .asCommonFG()
//                                                            .createDataSet( "config names", l_predType,
//                                                                            new hdf5.DataSpace( 1, new long[]{1} )
//                                                            );
//
//            l_configNamesDataSet.write( new BytePointer( p_configStr ), new hdf5.DataType( hdf5.PredType.C_S1() ) );
//
//            s_groups = 0;
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//
//        return this;
//    }
//
//    /**
//     * set group for configuration in hdf5 file
//     * @param p_run run id
//     * @param p_conf config id
//     * @return this
//     */
//
//    public EDataWriter setConf( final int p_run, final String p_conf )
//    {
//        try
//        {
//            s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().createGroup( p_conf );
//
//            s_groups = 0;
//            s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_conf ).asCommonFG().createGroup( "groups" );
//            s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_conf ).asCommonFG().openGroup( "groups" ).asCommonFG()
//                  .createDataSet( "group count", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ), new hdf5.DataSpace( 2, new long[]{1, 1} ),
//                                  new hdf5.DSetCreatPropList()
//            );
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//
//        return this;
//    }
//
//    /**
//     * set info on new traveller group in h5
//     * @param p_run run number
//     * @param p_config config name
//     * @param p_groupNum group ID
//     * @return this
//     */
//
//    public final EDataWriter setGroup( final int p_run, final String p_config, final int p_groupNum )
//    {
//        try
//        {
//
//            final String l_currentGroup = "group " + p_groupNum;
//            System.out.println( "XXXXX creating new group " + l_currentGroup );
//
//            s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_config )
//                  .asCommonFG().createGroup( l_currentGroup );
//
//            s_groups++;
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//
//        return this;
//
//    }
//
//        /**
//         * write default number of last iteration (-1)
//         * @param p_run number of simulation run
//         * @param p_config simulation config (grouping and protocol)
//         * @param p_groupNum group number
//         * @return this
//         */
//
//    public EDataWriter writeDefaultLastIteration( final int p_run, final String p_config, final int p_groupNum )
//    {
//        try
//        {
//            final String l_currentGroup = "group " + p_groupNum;
//
//
//            final hdf5.Group l_group;
//
//            l_group = s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_config )
//                            .asCommonFG().openGroup( l_currentGroup ).asCommonFG().createGroup( "lastIt" );
//
//            final hdf5.DataSet l_dataSet = new hdf5.DataSet(
//                l_group.asCommonFG().createDataSet( "lastIt", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
//                                                    new hdf5.DataSpace( 2, new long[]{1, 1} ), new hdf5.DSetCreatPropList()
//                )
//            );
//
//            final int[] l_buf = new int[1];
//
//            l_buf[0] = -1;
//
//            l_dataSet.write( new IntPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//
//        return this;
//    }
//
//    /**
//     * write (intermediate) election results for coordinated grouping to h5 file
//     * @param p_run run number
//     * @param p_conf configuration
//     * @param p_chairAgent chair agent
//     * @param p_comResultBV election result
//     * @param p_coorNum number of (intermediate) election
//     * @return this
//     */
//    public EDataWriter writeCommitteeCoordinated( final int p_run, final String p_conf, final CChairAgent p_chairAgent,
//                                                  final BitVector p_comResultBV,
//                                                  final int p_coorNum
//    )
//    {
//        try
//        {
//            final String l_currentGroup = "group " + p_chairAgent.getGroupID();
//
//            final hdf5.Group l_group;
//
//            final hdf5.PredType l_predType = hdf5.PredType.C_S1();
//            l_predType.setSize( 256 );
//
//
//            l_group = s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_conf )
//                            .asCommonFG().openGroup( l_currentGroup ).asCommonFG().createGroup( "im_" + p_coorNum );
//
//
//            final hdf5.DataSet l_dataSet = l_group.asCommonFG().createDataSet( "committee", l_predType,
//                                                                               new hdf5.DataSpace( 1, new long[]{1} )
//            );
//
//            final int[] l_buf = new int[1];
//
//            l_dataSet.write( new BytePointer( p_comResultBV.toString() ), new hdf5.DataType( hdf5.PredType.C_S1() ) );
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//
//        return this;
//
//    }
//
//    /**
//     * write data to file
//     * @param p_run number of simulation run
//     * @param p_config simulation config (grouping and protocol)
//     * @param p_chair chair calling the method
//     * @param p_iteration number of iteration
//     * @param p_dissVals dissatisfaction values
//     * @return this
//     */
//
//    public final EDataWriter writeDataVector( final int p_run, final String p_config, final CChairAgent p_chair, final int p_iteration,
//                                              final AtomicDoubleArray p_dissVals
//    )
//    {
//        try
//        {
//
//            final String l_currentGroup = "group " + p_chair.getGroupID();
//
//            System.out.println( "CDataWriter: " + p_dissVals );
//
//            final hdf5.Group l_group;
//
//            l_group = s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_config )
//                            .asCommonFG().openGroup( l_currentGroup ).asCommonFG().createGroup( String.valueOf( p_iteration ) );
//
//            final hdf5.DataSet l_dataSet;
//
//            if ( l_group.asCommonFG().getNumObjs() == 0 )
//                l_dataSet =
//                  new hdf5.DataSet(
//                                l_group.asCommonFG().createDataSet( "dissVals", new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ),
//                                                                    new hdf5.DataSpace( 2, new long[]{p_dissVals.length(), 1} ), new hdf5.DSetCreatPropList()
//
//                                )
//                            );
//            else
//                l_dataSet = new hdf5.DataSet(
//                    l_group.asCommonFG().openDataSet( "dissVals" )
//                );
//            final double[] l_buf = new double[p_dissVals.length()];
//
//            for ( int i = 0; i < p_dissVals.length(); i++ )
//            {
//                System.out.println( " Setting buf[" + i + "] to " + p_dissVals.get( i ) );
//                l_buf[i] = p_dissVals.get( i );
//            }
//
//            l_dataSet.write( new DoublePointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ) );
//
//            // write # groups
//
//            final hdf5.Group l_countGroup = s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_config ).asCommonFG().openGroup(
//                "groups" );
//
//            final hdf5.DataSet l_countDataSet = l_countGroup.asCommonFG().openDataSet( "group count" );
//
//            final int[] l_countBuf = new int[1];
//            l_countBuf[0] = s_groups;
//            l_countDataSet.write( new IntPointer( l_countBuf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
//
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//        return this;
//    }
//
//    /**
//     * write number of last iteration
//     * @param p_run number of simulation run
//     * @param p_config simulation config (grouping and protocol)
//     * @param p_chair chair calling the method
//     * @param p_iteration number of iteration
//     * @return this
//     */
//
//    public EDataWriter writeLastIteration( final int p_run, final String p_config, final CChairAgent p_chair, final Integer p_iteration )
//    {
//        try
//        {
//            final String l_currentGroup = "group " + p_chair.getGroupID();
//
//            final hdf5.Group l_group;
//
//            l_group = s_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_config )
//                            .asCommonFG().openGroup( l_currentGroup ).asCommonFG().openGroup( "lastIt" );
//
//            final hdf5.DataSet l_dataSet = new hdf5.DataSet(
//                l_group.asCommonFG().openDataSet( "lastIt" )
//            );
//
//            final int[] l_buf = new int[1];
//
//            l_buf[0] = p_iteration;
//
//            l_dataSet.write( new IntPointer( p_iteration ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace();
//        }
//
//        return this;
//    }
}

