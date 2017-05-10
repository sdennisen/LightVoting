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

package org.lightvoting.simulation.rule;

/* TODO add test further cases for lexicographic tie-breaking */

import junit.framework.TestCase;


/**
 * Created by sophie on 01.02.17.
 */

public class CMinisumApprovalTest extends TestCase
{


/**
     * Create the test case
     *
     * @param p_testName name of the test case
     *//*

    public CMinisumApprovalTest( final String p_testName )
    {
        super( p_testName );
    }

    */
/**
     * Testsuite
     *
     * @return the suite of tests being tested
     *//*

    public static Test suite()
    {
        return new TestSuite( CMinisumApprovalTest.class );
    }

    */
/**
     * test MinisumApproval
     *//*

    public void testCMinisumApproval()
    {
        final CMinisumApproval l_tester = new CMinisumApproval();

        final List<String> l_testAlternatives = new ArrayList<>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );

        final List<AtomicIntegerArray> l_testVotes = new ArrayList<>();
        final AtomicIntegerArray l_vote1 = new AtomicIntegerArray( new int[]{1, 0, 1} );
        final AtomicIntegerArray l_vote2 = new AtomicIntegerArray( new int[]{0, 1, 1} );
        final AtomicIntegerArray l_vote3 = new AtomicIntegerArray( new int[]{0, 1, 1} );
        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );

        final int l_testComSize = 2;
        final int[] l_result = l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize );
        assertEquals( l_result[0], 0 );
        assertEquals( l_result[1], 1 );
        assertEquals( l_result[2], 1 );
    }

    */
/**
     * test MinisumApproval with tie-breaking for small instance
     *//*


    public void testCMinisumApproval1()
    {

        final CMinisumApproval l_tester = new CMinisumApproval();

        final List<String> l_testAlternatives = new ArrayList<>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );

        final List<AtomicIntegerArray> l_testVotes = new ArrayList<>();
        final AtomicIntegerArray l_vote1 = new AtomicIntegerArray( new int[]{1, 0, 1} );
        final AtomicIntegerArray l_vote2 = new AtomicIntegerArray( new int[]{0, 1, 0} );
        final AtomicIntegerArray l_vote3 = new AtomicIntegerArray( new int[]{1, 1, 1} );
        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );

        final int l_testComSize = 2;
        final int[] l_result = l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize );
        assertEquals( l_result[0], 1 );
        assertEquals( l_result[1], 1 );
        assertEquals( l_result[2], 0 );

    }

    */
/**
     * test MinisumApproval including tie-break for larger instance
     *//*


    public void testCMinisumApproval3()
    {

        final CMinisumApproval l_tester = new CMinisumApproval();

        List<String> l_testAlternatives;
        l_testAlternatives = new ArrayList<String>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );
        l_testAlternatives.add( "POI4" );
        l_testAlternatives.add( "POI5" );
        l_testAlternatives.add( "POI6" );

        final List<AtomicIntegerArray> l_testVotes = new ArrayList<>( );
        final AtomicIntegerArray l_vote1 = new AtomicIntegerArray(  new int[]{1, 0, 1, 1, 0, 1} );
        final AtomicIntegerArray l_vote2 = new AtomicIntegerArray( new int[]{1, 0, 1, 1, 0, 1} );
        final AtomicIntegerArray l_vote3 = new AtomicIntegerArray( new int[]{1, 0, 1, 1, 0, 1} );
        final AtomicIntegerArray l_vote4 = new AtomicIntegerArray( new int[]{0, 0, 1, 0, 0, 0} );

        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );
        l_testVotes.add( l_vote4 );

        final int l_testComSize = 3;
        final int[] l_result = l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize );
        assertEquals( l_result[0], 1 );
        assertEquals( l_result[1], 0 );
        assertEquals( l_result[2], 1 );
        assertEquals( l_result[3], 1 );
        assertEquals( l_result[4], 0 );
        assertEquals( l_result[5], 0 );
    }

*/
}

