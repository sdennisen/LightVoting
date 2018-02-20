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

import cern.colt.matrix.tbit.BitVector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sophie on 09.02.17.
 */
public class CMinimaxApprovalTest extends TestCase
{

    /**
     * Create the test case
     *
     * @param p_testName name of the test case
     */
    public CMinimaxApprovalTest( final String p_testName )
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
        return new TestSuite( CMinimaxApprovalTest.class );
    }

    /**
     * test application of voting rule
     */
    public void testApplyRule()
    {
        final CMinimaxApproval l_minimaxApproval = new CMinimaxApproval();

        final List<String> l_alternatives = new ArrayList<String>();

        l_alternatives.add( "POI1" );
        l_alternatives.add( "POI2" );
        l_alternatives.add( "POI3" );
        l_alternatives.add( "POI4" );
        l_alternatives.add( "POI5" );
        l_alternatives.add( "POI6" );

        final List<BitVector> l_votes = new ArrayList<>();

        final BitVector l_vote1 = new BitVector( 6 );
        l_vote1.put( 0, true );
        l_vote1.put( 1, true );
        l_vote1.put( 2, true );
        l_vote1.put( 3, true );
        l_vote1.put( 4, true );

        final BitVector l_vote2 = l_vote1.copy();
        final BitVector l_vote3 =  l_vote1.copy();
        final BitVector l_vote4 = l_vote1.copy();
        final BitVector l_vote5 = l_vote1.copy();

        final BitVector l_vote6 = new BitVector( 6 );
        l_vote6.put( 3, true );
        l_vote6.put( 4, true );
        l_vote6.put( 5, true );

        l_votes.add( l_vote1 );
        l_votes.add( l_vote2 );
        l_votes.add( l_vote3 );
        l_votes.add( l_vote4 );
        l_votes.add( l_vote5 );
        l_votes.add( l_vote6 );

        final int l_comSize = 3;

        final  BitVector l_bitVector = l_minimaxApproval.applyRuleBV( l_alternatives, l_votes, l_comSize );
        System.out.println( l_bitVector );
    }

}

// TODO add more tests
