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


import cern.colt.matrix.tbit.BitVector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;


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
     * test Minisum Approval for small instance
     */

    public void testCMinisumApproval()
    {

        final CMinisumApproval l_tester = new CMinisumApproval();

        final List<String> l_testAlternatives = new ArrayList<>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );

        final List<BitVector> l_testVotes = new ArrayList<>();

        final BitVector l_vote1 = new BitVector( 3 );
        l_vote1.put( 0, true );

        final BitVector l_vote2 = new BitVector( 3 );

        l_vote2.put( 1, true );
        l_vote2.put( 2, true );

        final BitVector l_vote3 = new BitVector( 3 );

        l_vote3.put( 1, true );
        l_vote3.put( 2, true );

        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );

        final int l_testComSize = 2;
        final BitVector l_result = l_tester.applyRuleBV( l_testAlternatives, l_testVotes, l_testComSize );

        assertFalse( l_result.get( 0 ) );
        assertTrue( l_result.get( 1 ) );
        assertTrue( l_result.get( 2 ) );
    }

   /**
     * test MinisumApproval with tie-breaking for small instance
     */

    public void testCMinisumApproval1()
    {

        final CMinisumApproval l_tester = new CMinisumApproval();

        final List<String> l_testAlternatives = new ArrayList<>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );

        final List<BitVector> l_testVotes = new ArrayList<>();
        final BitVector l_vote1 = new BitVector( 3 );
        l_vote1.put( 0, true );
        l_vote1.put( 2, true );

        final BitVector l_vote2 = new BitVector( 3 );
        l_vote2.put( 1, true );

        final BitVector l_vote3 = new BitVector( 3 );

        l_vote3.put( 0, true );
        l_vote3.put( 1, true );
        l_vote3.put( 2, true );

        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );

        final int l_testComSize = 2;
        final BitVector l_result = l_tester.applyRuleBV( l_testAlternatives, l_testVotes, l_testComSize );
        assertTrue( l_result.get( 0 ) );
        assertTrue( l_result.get( 1 ) );
        assertFalse( l_result.get( 2 ) );
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

        final List<BitVector> l_testVotes = new ArrayList<>( );
        final BitVector l_vote1 = new BitVector( 6 );

        l_vote1.put( 0, true );
        l_vote1.put( 2, true );
        l_vote1.put( 3, true );
        l_vote1.put( 5, true );

        final BitVector l_vote2 = new BitVector( 6 );

        l_vote2.put( 0, true );
        l_vote2.put( 2, true );
        l_vote2.put( 3, true );
        l_vote2.put( 5, true );

        final BitVector l_vote3 = new BitVector( 6 );

        l_vote3.put( 0, true );
        l_vote3.put( 2, true );
        l_vote3.put( 3, true );
        l_vote3.put( 5, true );

        final BitVector l_vote4 = new BitVector( 6 );

        l_vote4.put( 2, true );

        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );
        l_testVotes.add( l_vote3 );
        l_testVotes.add( l_vote4 );

        final int l_testComSize = 3;
        final BitVector l_result = l_tester.applyRuleBV( l_testAlternatives, l_testVotes, l_testComSize );
        assertTrue( l_result.get( 0 ) );
        assertFalse( l_result.get( 1 ) );
        assertTrue( l_result.get( 2 ) );
        assertTrue( l_result.get( 3 ) );
        assertFalse( l_result.get( 4 ) );
        assertFalse( l_result.get( 5 ) );
    }
}

