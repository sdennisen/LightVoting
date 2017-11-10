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


import cern.colt.bitvector.BitVector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sophie on 01.02.17.
 */

public class CMinisumRanksumTest extends TestCase
{

/**
     * Create the test case
     *
     * @param p_testName name of the test case
     */

    public CMinisumRanksumTest( final String p_testName )
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
        return new TestSuite( CMinisumRanksumTest.class );
    }

    /**
     * test Minisum Ranksum for small instance
     */

    public void testCMinisumRanksum()
    {

        final CMinisumRanksum l_tester = new CMinisumRanksum();

        final List<String> l_testAlternatives = new ArrayList<>();
        l_testAlternatives.add( "POI1" );
        l_testAlternatives.add( "POI2" );
        l_testAlternatives.add( "POI3" );

        final List<List<Long>> l_testVotes = new ArrayList<>();

        final List<Long> l_vote1 = new ArrayList<>();
        l_vote1.add( 0, (long) 0 );
        l_vote1.add( 1, (long) 1 );
        l_vote1.add( 2, (long) 2 );


        final List<Long> l_vote2 = new ArrayList<>();

        l_vote2.add( 0, (long) 1 );
        l_vote2.add( 1, (long) 0 );
        l_vote2.add( 2, (long) 2 );

        l_testVotes.add( l_vote1 );
        l_testVotes.add( l_vote2 );

        final int l_testComSize = 2;
        final BitVector l_result = l_tester.applyRuleBV( l_testAlternatives, l_testVotes, l_testComSize );

        assertTrue( l_result.get( 0 ) );
        assertTrue( l_result.get( 1 ) );
        assertFalse( l_result.get( 2 ) );
    }

}

