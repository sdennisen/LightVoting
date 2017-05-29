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

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.AtomicDoubleArray;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.hdf5;

/**
 * Created by sophie on 15.05.17.
 */
public final class CDataWriter
{
    // source: https://github.com/bytedeco/javacpp-presets/tree/master/hdf5#the-srcmainjavah5tutrcmprssjava-source-file

    private CDataWriter()
    {

    }

    /**
     * create HDF5 file
     */
    public static void createHDF5( final String p_filename )
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
    }

    /**
     * test for h5 file
     * @param p_name name of h5 file
     */

    public static void test( final String p_name )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );

        final String l_DATASETNAME = "test0";
        final int l_DIM0 = 100;
        final int l_DIM1 = 20;

        // dataset dimensions
        final long[] l_dims = {l_DIM0, l_DIM1};
        // chunk dimensions
        final long[] l_chunkDims = {20, 20};
        final int[] l_buf = new int[l_DIM0 * l_DIM1];



        // Create the data space for the dataset.
        final hdf5.DataSpace l_dataSpace = new hdf5.DataSpace( 2, l_dims );

        // Modify dataset creation property to enable chunking
        final hdf5.DSetCreatPropList l_plist = new hdf5.DSetCreatPropList();
        l_plist.setChunk( 2, l_chunkDims );

        final hdf5.DataSet l_dataset = new hdf5.DataSet( l_file.asCommonFG().createDataSet( l_DATASETNAME,
                                                                                              new hdf5.DataType( hdf5.PredType.NATIVE_INT()
                                                                                                  // hdf5.PredType.STD_I32BE()
                                                                                                                 ), l_dataSpace, l_plist ) );
        for ( int i = 0; i <  l_DIM0; i++ )
            for ( int j = 0; j < l_DIM1; j++ )
                l_buf[i * l_DIM1 + j] = i + j;

        // Write data to dataset.
        l_dataset.write( new IntPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );

        l_dataSpace.close();
        l_dataset.close();
        l_plist.close();
        l_file._close();
    }

    /**
     * test for h5 file
     * 1 x 20
     * @param p_name name of h5 file
     */

    public static void test1( final String p_name )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );

        final String l_DATASETNAME = "test1";

        final int[] l_buf = new int[20];

        final hdf5.DataSet l_dataset
            = new hdf5.DataSet( l_file.asCommonFG().createDataSet(
                "test1", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
                new hdf5.DataSpace( 2, new long[]{1, 20} ), new hdf5.DSetCreatPropList() ) );

        for ( int i = 0; i <  20; i++ )
            l_buf[i] = i;

        // Write data to dataset.
        l_dataset.write( new IntPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );

        l_dataset.close();
        l_file._close();
    }

    /**
     * test for h5 file
     * 2 x 20
     * int[2][20]
     * @param p_name name of h5 file
     */

    public static void test2( final String p_name )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_name, hdf5.H5F_ACC_RDWR );

        final String l_DATASETNAME = "test2";

        final int[][] l_buf = new int[2][20];

        final hdf5.DataSet l_dataset
            = new hdf5.DataSet( l_file.asCommonFG().createDataSet(
            "test2", new hdf5.DataType( hdf5.PredType.NATIVE_INT() ),
            new hdf5.DataSpace( 2, new long[]{1, 20} ), new hdf5.DSetCreatPropList() ) );

        for ( int i = 0; i <  2; i++ )
            for ( int j = 0; j < 20; j++ )
            l_buf[i][j] = 1;

        // Write data to dataset.
        l_dataset.write( new PointerPointer( l_buf ), new hdf5.DataType( hdf5.PredType.NATIVE_INT() ) );

        l_dataset.close();
        l_file._close();
    }

    /**
     * test for h5 file
     * colt matrix
     *
     * @param p_name name of h5 file
     */

    public static void test3( final String p_name )
    {
        final DoubleMatrix2D l_cmatrix = DoubleFactory2D.dense.random( 300, 200 );
        // Try block to detect exceptions raised by any of the calls inside it
        try
        {
            // Turn off the auto-printing when failure occurs so that we can
            // handle the errors appropriately
            org.bytedeco.javacpp.hdf5.Exception.dontPrint();

            // Create a new file using the default property lists.
            final hdf5.H5File l_file = new hdf5.H5File( p_name, hdf5.H5F_ACC_RDWR );
            final hdf5.Group l_group = l_file.asCommonFG().createGroup( "foo" ).asCommonFG().createGroup( "bar" );

            // Create the data space for the dataset.
            final hdf5.DataSpace l_dataSpace = new hdf5.DataSpace( 2, new long[]{l_cmatrix.columns(), l_cmatrix.rows()} );

            // Modify dataset creation property to enable chunking
            final hdf5.DSetCreatPropList l_propList = new hdf5.DSetCreatPropList();
            l_propList.setChunk( 2, new long[]{l_cmatrix.columns(), l_cmatrix.rows()} );


            l_propList.setDeflate( 9 );
            l_propList.setFletcher32();

            // Create the dataset.
            final hdf5.DataSet l_dataset = new hdf5.DataSet(
                l_group.asCommonFG().createDataSet(
                    "test3", new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() ), l_dataSpace, l_propList
                )
            );

            // Write data to dataset.
            l_dataset.write(
                new DoublePointer(
                    Doubles.concat( l_cmatrix.toArray() )
                ),
                new hdf5.DataType( hdf5.PredType.NATIVE_DOUBLE() )
            );

            // Close objects and file.  Either approach will close the HDF5 item.
            l_dataSpace.close();
            l_dataset.close();
            l_propList.close();
            l_file.close();
        }
        catch ( final Exception l_ex )
        {
            l_ex.printStackTrace( System.out );
        }
    }


    /**
     * write dissatisfaction values to specified h5 file
     * @param p_name name of h5 file
     * @param p_dissVals dissatisfaction values
     * @param p_chairName chair calling the method
     */

    public static void writeDissVals( final String p_name, final AtomicDoubleArray p_dissVals, final String p_chairName )
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

    }



    /**
     * create group
     * @param p_fileName name of h5 file
     * @param p_chairName name of chair calling the method
     */
    public static void createGroup( final String p_fileName, final String p_chairName )
    {
        final hdf5.H5File l_file = new hdf5.H5File();
        l_file.openFile( p_fileName, hdf5.H5F_ACC_RDWR );
        l_file.asCommonFG().createGroup( p_chairName );
        l_file.close();
    }

}
