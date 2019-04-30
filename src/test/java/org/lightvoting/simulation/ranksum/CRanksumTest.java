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

package org.lightvoting.simulation.ranksum;

import cern.colt.matrix.tbit.BitVector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;


/**
 * Unit test for CRanksum.
 */
public class CRanksumTest extends TestCase
{

    /**
     * Create the test case
     *
     * @param p_testName name of the test case
     */
    public CRanksumTest(final String p_testName )
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
        return new TestSuite( CRanksumTest.class );
    }

    public void testRanksum()
    {

        final CRanksum l_tester = new CRanksum();

        final List<Long> l_testVote = new ArrayList<>();
        l_testVote.add( ( long ) 0 );
        l_testVote.add( ( long ) 1 );
        l_testVote.add( ( long ) 2 );
        l_testVote.add( ( long ) 3 );
        l_testVote.add( ( long ) 4 );
        l_testVote.add( ( long ) 5 );

        final BitVector l_testResult = new BitVector( 6 );

        l_testResult.put( 0, true );
        l_testResult.put( 1, true );
        l_testResult.put( 2, true );

        int l_testValue = l_tester.ranksum( l_testVote, l_testResult, 6, 3 );

        assertEquals( 0, l_testValue );

    }

    public void testRanksum2()
    {

        final CRanksum l_tester = new CRanksum();

        final List<Long> l_testVote = new ArrayList<>();
        l_testVote.add( ( long ) 5 );
        l_testVote.add( ( long ) 4 );
        l_testVote.add( ( long ) 3 );
        l_testVote.add( ( long ) 2 );
        l_testVote.add( ( long ) 1 );
        l_testVote.add( ( long ) 0 );


        final BitVector l_testResult = new BitVector( 6 );

        l_testResult.put( 0, true );
        l_testResult.put( 1, true );
        l_testResult.put( 2, true );

        System.out.println( l_testResult );

        int l_testValue = l_tester.ranksum( l_testVote, l_testResult, 6, 3 );

        assertEquals( 9, l_testValue );

    }

    public void testRanksum3()
    {

        final CRanksum l_tester = new CRanksum();

        final List<Long> l_testVote = new ArrayList<>();
        l_testVote.add( ( long ) 0 );
        l_testVote.add( ( long ) 5 );
        l_testVote.add( ( long ) 1 );
        l_testVote.add( ( long ) 4 );
        l_testVote.add( ( long ) 2 );
        l_testVote.add( ( long ) 3 );


        final BitVector l_testResult = new BitVector( 6 );

        l_testResult.put( 0, true );
        l_testResult.put( 1, true );
        l_testResult.put( 2, true );

        System.out.println( l_testResult );

        int l_testValue = l_tester.ranksum( l_testVote, l_testResult, 6, 3 );

        assertEquals( 3, l_testValue );

    }

}
