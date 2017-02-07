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

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;


/* TODO add test further cases for lexicographic tie-breaking */

/**
 * Created by sophie on 01.02.17.
 */
public class CMinisumApprovalTest extends TestCase
{
    /**
     * test MinisumApproval
     */
    public void testCMinisumApproval1()
    {

        final CMinisumApproval l_tester = new CMinisumApproval();

        List<String> l_testAlternatives;
        l_testAlternatives = new ArrayList<String>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );

        List<int[]> l_testVotes;
        l_testVotes = new ArrayList<int[]>( );
        final int[] l_vote1 = {1, 0, 1};
        final int[] l_vote2 = {0, 1, 1};
        final int[] l_vote3 = {0, 1, 1};
        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );

        final int l_testComSize = 2;

        final int[] l_testArray1 = new int[l_testAlternatives.size()];

        l_testArray1[0] = 0;
        l_testArray1[1] = 1;
        l_testArray1[2] = 1;

        assertArrayEquals( l_testArray1, l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize ) );

    }

    /**
     * test MinisumApproval including tie-break for small instance
     */

    public void testCMinisumApproval2()
    {

        final CMinisumApproval l_tester = new CMinisumApproval();

        List<String> l_testAlternatives;
        l_testAlternatives = new ArrayList<String>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );

        List<int[]> l_testVotes;
        l_testVotes = new ArrayList<int[]>( );
        final int[] l_vote1 = {1, 0, 1};
        final int[] l_vote2 = {0, 1, 0};
        final int[] l_vote3 = {1, 1, 1};
        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );

        final int l_testComSize = 2;

        assertArrayEquals( new int[]{1, 1, 0}, l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize ) );

    }

    /**
     * test MinisumApproval including tie-break for larger instance
     */

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

        List<int[]> l_testVotes;
        l_testVotes = new ArrayList<int[]>( );
        final int[] l_vote1 = {1, 0, 1, 1, 0, 1};
        final int[] l_vote2 = {1, 0, 1, 1, 0, 1};
        final int[] l_vote3 = {1, 0, 1, 1, 0, 1};
        final int[] l_vote4 = {0, 0, 1, 0, 0, 0};

        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );
        l_testVotes.add( l_vote4 );

        final int l_testComSize = 3;

        assertArrayEquals( new int[]{1, 0, 1, 1, 0, 0}, l_tester.applyRule( l_testAlternatives, l_testVotes, l_testComSize ) );

    }



}

