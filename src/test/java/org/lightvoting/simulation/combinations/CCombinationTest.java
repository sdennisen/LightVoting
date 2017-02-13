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

package org.lightvoting.simulation.combinations;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.lightvoting.simulation.rule.CMinisumApprovalTest;

/* TODO write small test */

/**
 * Created by sophie on 08.02.17.
 */

public class CCombinationTest extends TestCase
{

    /**
     * Create the test case
     *
     * @param p_testName name of the test case
     */
    public CCombinationTest( final String p_testName )
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
     * test combinations function
     */
    public void testCombinations2()
    {
   /*     final CCombination l_tester = new CCombination();
        final int[] l_arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        l_tester.combinations( l_arr, 3, 0, new int[3] );

        final List<int[]> l_resultList = l_tester.getResultList();
        l_tester.clearList();*/

    }

}





