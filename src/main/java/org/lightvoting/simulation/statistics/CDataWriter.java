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

import com.google.common.util.concurrent.AtomicDoubleArray;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.hdf5;
import org.lightvoting.simulation.agent.CChairAgent;
import org.lightvoting.simulation.agent.CVotingAgent;

import java.util.List;


/**
 * Created by sophie on 15.05.17.
 * source: https://github.com/bytedeco/javacpp-presets/tree/master/hdf5#the-srcmainjavah5tutrcmprssjava-source-file
 */
public final class CDataWriter
{
    private static int s_groups;

    public CDataWriter()
    {

    }

    /**
     * create HDF5 file
     * @return this
     */
    public final CDataWriter createHDF5( final String p_filename )
    {

        // Create a new file.
        try
        {
            final hdf5.H5File l_h5file = new hdf5.H5File( p_filename, org.bytedeco.javacpp.hdf5.H5F_ACC_TRUNC );
            l_h5file.close();
        }
        catch ( final Exception l_ex )
        {
            l_ex.printStackTrace();
        }

        return this;
    }

//    /**
//     * test for h5 file
//     * @param p_name name of h5 file
//     */
//
//    public final void test( final String p_name )
//    {
//        final hdf5.H5File l_file = new hdf5.H5File();
//        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );
//
//        final String l_DATASETNAME = "test0";
//        final int l_DIM0 = 100;
//        final int l_DIM1 = 20;
//
//        // dataset dimensions
//        final long[] l_dims = {l_DIM0, l_DIM1};
//        // chunk dimensions
//        final long[] l_chunkDims = {20, 20};
//        final int[] l_buf = new int[l_DIM0 * l_DIM1];
//
//
//
//        // Create the data space for the dataset.
//        final hdf5.DataSpace l_dataSpace = new hdf5.DataSpace( 2, l_dims );
//
//        // Modify dataset creation property to enable chunking
//        final hdf5.DSetCreatPropList l_plist = new hdf5.DSetCreatPropList();
//        l_plist.setChunk( 2, l_chunkDims );
//
//        final hdf5.DataSet l_dataset = new hdf5.DataSet( l_file.asCommonFG().createDataSet( l_DATASETNAME,
//                                                                                              new hdf5.DataType( hdf5.PredType.NATIVE_INT()
//                                                                                                  // hdf5.PredType.STD_I32BE()
//                                                                                                                 ), l_dataSpace, l_plist ) );
//        for ( int i = 0; i <  l_DIM0; i++ )
//            for ( int j = 0; j < l_DIM1; j++ )
//                l_buf[i * l_DIM1 + j] = i + j;
//
//        // Write data to dataset.
//        l_dataset.write( new IntPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
//
//        l_dataSpace.close();
//        l_dataset.close();
//        l_plist.close();
//        l_file._close();
//    }

//    /**
//     * test for h5 file
//     * 1 x 20
//     * @param p_name name of h5 file
//     */
//
//    public final void test1( final String p_name )
//    {
//        final hdf5.H5File l_file = new hdf5.H5File();
//        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );
//
//        final String l_DATASETNAME = "test1";
//
//        final int[] l_buf = new int[20];
//
//        final hdf5.DataSet l_dataset
//            = new hdf5.DataSet( l_file.asCommonFG().createDataSet(
//                "test1", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
//                new hdf5.DataSpace( 2, new long[]{1, 20} ), new hdf5.DSetCreatPropList() ) );
//
//        for ( int i = 0; i <  20; i++ )
//            l_buf[i] = i;
//
//        // Write data to dataset.
//        l_dataset.write( new IntPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
//
//        l_dataset.close();
//        l_file._close();
//    }
//
//    /**
//     * test for h5 file
//     * 2 x 20
//     * int[2][20]
//     * @param p_name name of h5 file
//     */
//
//    public final void test2( final String p_name )
//    {
//        final hdf5.H5File l_file = new hdf5.H5File();
//        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );
//
//        final String l_DATASETNAME = "test2";
//
//        final int[][] l_buf = new int[2][20];
//
//        final hdf5.DataSet l_dataset
//            = new hdf5.DataSet( l_file.asCommonFG().createDataSet(
//            "test2", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
//            new hdf5.DataSpace( 2, new long[]{1, 20} ), new hdf5.DSetCreatPropList() ) );
//
//        for ( int i = 0; i <  2; i++ )
//            for ( int j = 0; j < 20; j++ )
//            l_buf[i][j] = 1;
//
//        // Write data to dataset.
//        l_dataset.write( new PointerPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );
//
//        l_dataset.close();
//        l_file._close();
//    }
//
//    /**
//     * test for h5 file
//     * colt matrix
//     *
//     * @param p_name name of h5 file
//     */
//
//    public final void test3( final String p_name )
//    {
//        final DoubleMatrix2D l_cmatrix = DoubleFactory2D.dense.random( 300, 100 );
//        // Try block to detect exceptions raised by any of the calls inside it
//        try
//        {
//            // Turn off the auto-printing when failure occurs so that we can
//            // handle the errors appropriately
//            org.bytedeco.javacpp.hdf5.Exception.dontPrint();
//
//            // Create a new file using the default property lists.
//            final hdf5.H5File l_file = new hdf5.H5File( p_name, hdf5.H5F_ACC_RDWR );
//            final hdf5.Group l_group = l_file.asCommonFG().createGroup( "foo" ).asCommonFG().createGroup( "bar" );
//
//            // Create the data space for the dataset.
//            final hdf5.DataSpace l_dataSpace = new hdf5.DataSpace( 2, new long[]{l_cmatrix.columns(), l_cmatrix.rows()} );
//
//            // Modify dataset creation property to enable chunking
//            final hdf5.DSetCreatPropList l_propList = new hdf5.DSetCreatPropList();
//            l_propList.setChunk( 2, new long[]{l_cmatrix.columns(), l_cmatrix.rows()} );
//
//
//            l_propList.setDeflate( 9 );
//            l_propList.setFletcher32();
//
//            // Create the dataset.
//            final hdf5.DataSet l_dataset = new hdf5.DataSet(
//                l_group.asCommonFG().createDataSet(
//                    "test3", new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ), l_dataSpace, l_propList
//                )
//            );
//
//            // Write data to dataset.
//            l_dataset.write(
//                new DoublePointer(
//                    Doubles.concat(  l_cmatrix.toArray() )
//                ),
//                new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() )
//            );
//
//            // Close objects and file.  Either approach will close the HDF5 item.
//            l_dataSpace.close();
//            l_dataset.close();
//            l_propList.close();
//            l_file.close();
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace( System.out );
//        }
//    }
//
//    /**
//     * test for h5 file
//     * colt matrix (3D)
//     *
//     * @param p_name name of h5 file
//     */
//
//    public final void test4( final String p_name )
//    {
//        final DoubleMatrix3D l_cmatrix = DoubleFactory3D.dense.random( 300, 300, 300 );
//        // Try block to detect exceptions raised by any of the calls inside it
//        try
//        {
//            // Turn off the auto-printing when failure occurs so that we can
//            // handle the errors appropriately
//            org.bytedeco.javacpp.hdf5.Exception.dontPrint();
//
//            // Create a new file using the default property lists.
//            final hdf5.H5File l_file = new hdf5.H5File( p_name, hdf5.H5F_ACC_RDWR );
//            final hdf5.Group l_group = l_file.asCommonFG().createGroup( "foo" ).asCommonFG().createGroup( "bar" );
//
//            // Create the data space for the dataset.
//            final hdf5.DataSpace l_dataSpace = new hdf5.DataSpace( 3, new long[] {l_cmatrix.rows(), l_cmatrix.columns(),  l_cmatrix.slices()} );
//
//            // Modify dataset creation property to enable chunking
//            final hdf5.DSetCreatPropList l_propList = new hdf5.DSetCreatPropList();
//            l_propList.setChunk( 3, new long[] {l_cmatrix.rows(), l_cmatrix.columns(),  l_cmatrix.slices()} );
//
//
//            l_propList.setDeflate( 9 );
//            l_propList.setFletcher32();
//
//            // Create the dataset.
//            final hdf5.DataSet l_dataset = new hdf5.DataSet(
//                l_group.asCommonFG().createDataSet(
//                    "test4", new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ), l_dataSpace, l_propList
//                )
//            );
//
//            // Write data to dataset.
//
//            l_dataset.write(
//                new DoublePointer(
//                    Doubles.concat(
//                    Arrays.stream( l_cmatrix.toArray() )
//                          .flatMap( Arrays::stream )
//                          .flatMapToDouble( Arrays::stream )
//                          .toArray() )
//                ),
//                new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() )
//            );
//
//
//
//            // Close objects and file.  Either approach will close the HDF5 item.
//            l_dataSpace.close();
//            l_dataset.close();
//            l_propList.close();
//            l_file.close();
//        }
//        catch ( final Exception l_ex )
//        {
//            l_ex.printStackTrace( System.out );
//        }
//    }

    /**
     * write dissatisfaction values to specified h5 file
     * @param p_name name of h5 file
     * @param p_dissVals dissatisfaction values
     * @param p_chairName chair calling the method
     * @return this
     */

    public final CDataWriter writeDissVals( final String p_name, final AtomicDoubleArray p_dissVals, final String p_chairName )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );

        final double[] l_buf = new double[p_dissVals.length()];

        for ( int i = 0; i < p_dissVals.length(); i++ )
        {
            l_buf[i] = p_dissVals.get( i );
        }


        final hdf5.DataSet l_dataSet = new hdf5.DataSet(
            l_file.asCommonFG().openGroup( p_chairName ).asCommonFG().createDataSet(
                "diss values", new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ), new hdf5.DataSpace( 2, new long[]{1, p_dissVals.length()} ), new hdf5.DSetCreatPropList()
            )
        );

        l_dataSet.write( new DoublePointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ) );

        l_file._close();

        return this;

    }

    /**
     * create group
     * @param p_fileName name of h5 file
     * @param p_chairName name of chair calling the method
     * @return this
     */
    public final CDataWriter createGroup( final String p_fileName, final String p_chairName )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_fileName, hdf5.H5F_ACC_RDWR );
        l_file.asCommonFG().createGroup( p_chairName );
        l_file.close();

        return this;
    }

    /**
     * write data to file
     * @param p_fileName name of h5 file
     * @param p_run number of simulation run
     * @param p_config simulation config (grouping and protocol)
     * @param p_chair chair calling the method
     * @param p_iteration number of iteration
     * @param p_agentList agents in group of chair
     * @param p_dissVals dissatisfaction values
     * @return this
     */

    public final CDataWriter writeDataVector( final String p_fileName, final int p_run, final String p_config, final CChairAgent p_chair, final int p_iteration,
                                        final List<CVotingAgent> p_agentList,
                                        final AtomicDoubleArray p_dissVals
    )
    {

        final String l_currentGroup = "group " + p_chair.getGroupID();

        System.out.println( "CDataWriter: " + p_dissVals  );


        try
        {
            final hdf5.H5File l_file = new hdf5.H5File();
            l_file.openFile( p_fileName, hdf5.H5F_ACC_RDWR );

            System.out.println( "Name of group: " + p_run + "/" +  p_config + "/" + l_currentGroup + "/" + p_iteration );

            final hdf5.Group l_group;

            l_group = l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_config  )
                                          .asCommonFG().openGroup( l_currentGroup ).asCommonFG().createGroup( String.valueOf( p_iteration ) );

            final hdf5.DataSet l_dataSet;

            if ( l_group.asCommonFG().getNumObjs() == 0 )
             l_dataSet = new hdf5.DataSet(
                l_group.asCommonFG().createDataSet( "dissVals", new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ),
                                                    new hdf5.DataSpace( 2, new long[]{p_dissVals.length(), 1} ), new hdf5.DSetCreatPropList()
                )
            );
            else
                l_dataSet = new hdf5.DataSet(
                    l_group.asCommonFG().openDataSet( "dissVals" )
                );


            final double[] l_buf = new double[p_dissVals.length()];

            for ( int i = 0; i < p_dissVals.length(); i++ )
            {
                System.out.println( " Setting buf[" + i + "] to " + p_dissVals.get( i ) );
                l_buf[i] = p_dissVals.get( i );
            }

            l_dataSet.write( new DoublePointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ) );

            // write # groups

            final hdf5.Group l_countGroup = l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_config  ).asCommonFG().openGroup( "groups" );

            final hdf5.DataSet l_countDataSet = l_countGroup.asCommonFG().openDataSet( "group count" );

            final int[] l_countBuf = new int[1];
            l_countBuf[0] = s_groups;
            l_countDataSet.write( new IntPointer( l_countBuf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );

            l_file.close();

        }
        catch ( final Exception l_ex )
        {
        System.out.println( "Error occurred at group: " + p_run + "/" +  p_config + "/" + l_currentGroup + "/" + p_iteration );
            l_ex.printStackTrace();
        }

        return this;
    }

    /**
     * create group in hdf5 for run
     * @param p_name name of hdf5 file
     * @param p_run run id
     * @param p_configStr config string
     * @return this
     */

    public final CDataWriter setRun( final String p_name, final int p_run, final int p_configNum, final String p_configStr )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );
        l_file.asCommonFG().createGroup( String.valueOf( p_run ) );
        l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().createGroup( "configs" );
        final hdf5.DataSet l_configDataSet =  l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( "configs" ).asCommonFG()
                                              .createDataSet( "config num", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
                                                              new hdf5.DataSpace( 2, new long[]{1, 1} ) );

        final int[] l_configBuf = new int[1];
        l_configBuf[0] = p_configNum;
        l_configDataSet.write( new IntPointer( l_configBuf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );


        l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().createGroup( "confignames" );
        final hdf5.PredType l_predType = hdf5.PredType.C_S1();
        l_predType.setSize( 256 );

        final hdf5.DataSet l_configNamesDataSet = l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( "confignames" ).asCommonFG()
                                                    .createDataSet( "config names", l_predType,
                                                                    new hdf5.DataSpace( 1, new long[]{1} ) );

        l_configNamesDataSet.write( new BytePointer( p_configStr ), new hdf5.DataType( hdf5.PredType.C_S1() ) );

        s_groups = 0;

        return this;
    }

    /**
     * set group for configuration in hdf5 file
     * @param p_fileName name of hdf5 file
     * @param p_run run id
     * @param p_conf config id
     * @return this
     */

    public final CDataWriter setConf( final String p_fileName, final int p_run, final String p_conf )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_fileName, hdf5.H5F_ACC_RDWR );
        l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().createGroup( p_conf );

        s_groups = 0;
        l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_conf ).asCommonFG().createGroup( "groups" );
        l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_conf ).asCommonFG().openGroup( "groups" ).asCommonFG()
              .createDataSet( "group count", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ), new hdf5.DataSpace( 2, new long[]{1, 1}  ),
                              new hdf5.DSetCreatPropList()
        );

        return this;
    }

    /**
     * set # runs
     * @param p_name name of hdf5 file
     * @param p_runs number of runs
     * @return this
     */

    public final CDataWriter setRunNum( final String p_name, final int p_runs )
    {

        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );
        l_file.asCommonFG().createGroup( "runs" );
        final hdf5.DataSet l_runDataSet =  l_file.asCommonFG().openGroup( String.valueOf( "runs" ) ).asCommonFG()
                                                 .createDataSet( "run num", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
                                                                    new hdf5.DataSpace( 2, new long[]{1, 1} ) );

        final int[] l_runBuf = new int[1];
        l_runBuf[0] = p_runs;
        l_runDataSet.write( new IntPointer( l_runBuf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );

        return this;
    }

    /**
     * set info on new traveller group in h5
     * @param p_run run number
     * @param p_config config name
     * @param p_name file name
     * @param p_groupNum group ID
     * @return this
     */

    public final CDataWriter setGroup( final int p_run, final String p_config, final String p_name, final int p_groupNum )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );

        final String l_currentGroup = "group " + p_groupNum;
        System.out.println( "XXXXX creating new group " + l_currentGroup );

        l_file.asCommonFG().openGroup( String.valueOf( p_run ) ).asCommonFG().openGroup( p_config )
              .asCommonFG().createGroup( l_currentGroup );

        s_groups++;

        return this;

    }
}
