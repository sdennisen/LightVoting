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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/* TODO add test further cases for lexicographic tie-breaking */

/**
 * Created by sophie on 01.02.17.
 */
public class CMinisumApprovalTest extends TestCase
{

    /**
     * Create the test case
     *
     * @param p_testName name of the test case
     */
    public CMinisumApprovalTest( final String p_testName )
    {
        super( p_testName );
    }

    /**
     * Testsuite
     *
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CMinisumApprovalTest.class );
    }

    /**
     * test MinisumApproval
     */
    public void testCMinisumApproval()
    {
        final CMinisumApproval l_tester = new CMinisumApproval();

        final List<String> l_testAlternatives = new ArrayList<>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );

        final List<int[]> l_testVotes = new ArrayList<>( );
        final int[] l_vote1 = {1, 0, 1};
        final int[] l_vote2 = {0, 1, 1};
        final int[] l_vote3 = {0, 1, 1};
        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );

        final int l_testComSize = 2;
        final int[] l_result = l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize );
        assertEquals( l_result[0], 0 );
        assertEquals( l_result[1], 1 );
        assertEquals( l_result[2], 1 );

//        l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize );
//        l_testAlternatives = new ArrayList<String>();
//        l_testAlternatives.add( "POI1" );
//        l_testAlternatives.add( "POI2" );
//        l_testAlternatives.add( "POI3" );
//
//        l_testVotes = new ArrayList<int[]>( );
//        final int[] l_vote4 = {1, 0, 1};
//        final int[] l_vote5 = {0, 1, 0};
//        final int[] l_vote6 = {1, 1, 1};
//        l_testVotes.add( l_vote4 );
//        l_testVotes.add( l_vote5 );
//        l_testVotes.add( l_vote6 );
//
//        // assertArrayEquals( new int[]{1, 1, 0}, l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize ) );

    }

//    /**
//     * test MinisumApproval including tie-break for larger instance
//     */
//
//    public void testCMinisumApproval3()
//    {
//
//        final CMinisumApproval l_tester = new CMinisumApproval();
//
//        List<String> l_testAlternatives;
//        l_testAlternatives = new ArrayList<String>();
//        l_testAlternatives.add( "POI1" );
//        l_testAlternatives.add( "POI2" );
//        l_testAlternatives.add( "POI3" );
//        l_testAlternatives.add( "POI4" );
//        l_testAlternatives.add( "POI5" );
//        l_testAlternatives.add( "POI6" );
//
//        List<int[]> l_testVotes;
//        l_testVotes = new ArrayList<int[]>( );
//        final int[] l_vote1 = {1, 0, 1, 1, 0, 1};
//        final int[] l_vote2 = {1, 0, 1, 1, 0, 1};
//        final int[] l_vote3 = {1, 0, 1, 1, 0, 1};
//        final int[] l_vote4 = {0, 0, 1, 0, 0, 0};
//
//        l_testVotes.add( l_vote1 );
//        l_testVotes.add( l_vote2 );
//        l_testVotes.add( l_vote3 );
//        l_testVotes.add( l_vote4 );
//
//        final int l_testComSize = 3;
//
//        assertArrayEquals( new int[]{1, 0, 1, 1, 0, 0}, l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize ) );
//
//    }



}

