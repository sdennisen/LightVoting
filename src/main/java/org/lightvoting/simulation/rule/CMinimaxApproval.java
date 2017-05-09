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

import cern.colt.bitvector.BitVector;
import org.lightvoting.simulation.combinations.CCombination;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;


/**
 * Created by sophie on 10.01.17.
 * re-used code from http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
 */
public class CMinimaxApproval
{

    /**
     * compute the winning committee according to Minimax Approval
     *
     * @param p_alternatives available alternatives
     * @param p_votes submitted votes
     * @param p_comSize size of committee to be elected
     * @return elected committee
     */

    public int[] applyRule( final List<String> p_alternatives, final List<AtomicIntegerArray> p_votes, final int p_comSize )
    {
        /* compute all possible committees, i.e. all {0,1}^m vectors with exactly k ones */
        final int[][] l_committees = this.computeComittees( p_alternatives.size(), p_comSize );

        /* Hashmap for storing the maximal hamming distance to any vote for all committees */

        Map<Integer, Integer> l_maxMap = new HashMap<Integer, Integer>();

        for ( int i = 0; i < l_committees.length; i++ )
        {
            /* Key: Committee ID, Value: maximal Hamming distance to any voter */
            l_maxMap.put( i, this.determineMaxHD( p_votes, l_committees[i], p_alternatives.size() ) );
        }

        l_maxMap = this.sortMapASC( l_maxMap );

        final Map.Entry<Integer, Integer> l_entry = l_maxMap.entrySet().iterator().next();

        final int l_winnerIndex = l_entry.getKey();

        return l_committees[l_winnerIndex];

    }

    /**
     * compute all possible committees for given number of alternatives and committee size
     *
     * @param p_altNum number of alternatives
     * @param p_comSize size of committee to be elected
     * @return all possible committees
     */

    private int[][] computeComittees( final int p_altNum, final int p_comSize )
    {
        final CCombination l_combination = new CCombination();
        final int[] l_arr = new int[p_altNum];

        for ( int i = 0; i < p_altNum; i++ )
            l_arr[i] = i;

        l_combination.combinations( l_arr, p_comSize, 0, new int[p_comSize] );

        final List<int[]> l_resultList = l_combination.getResultList();
        l_combination.clearList();

        final int[][] l_comVects = new int[l_resultList.size()][l_arr.length];

        for ( int i = 0; i < l_resultList.size(); i++ )
        {

            for ( int j = 0; j < p_comSize; j++ )
            {
                l_comVects[i][l_resultList.get( i )[j]] = 1;
            }
        }

        return l_comVects;
    }



    private int determineMaxHD( final List<AtomicIntegerArray> p_votes, final int[] p_comVect, final int p_altNum )
    {
        /* determine BitVector for committee */

        final Boolean[] l_booleanCom = new Boolean[p_altNum ];

        for ( int i = 0; i < p_altNum; i++ )
            if ( p_comVect[i] == 1 )
                l_booleanCom[i] = true;
            else
                l_booleanCom[i] = false;

        final BitVector l_bitCom = new BitVector( p_altNum );

        for ( int i = 0; i < p_altNum; i++ )
        {
            l_bitCom.put( i, l_booleanCom[i] );
        }

        /* compute Hamming distances to all votes and determine the maximum */

        int l_maxHD = -1;

        for ( int i = 0; i < p_votes.size(); i++ )
        {
            final Boolean[] l_booleanVote = new Boolean[p_altNum];

            for ( int j = 0; j < p_altNum; j++ )
                if ( p_votes.get( i ).get( j ) == 1 )
                    l_booleanVote[j] = true;
                else
                    l_booleanVote[j] = false;

            final BitVector l_bitVote = new BitVector( p_altNum );

            for ( int j = 0; j < p_altNum; j++ )
            {
                l_bitVote.put( j, l_booleanVote[j] );
            }

            final BitVector l_curBitCom = l_bitCom.copy();

            l_curBitCom.xor( l_bitVote );

            final int l_curHD = l_curBitCom.cardinality();

            if ( l_curHD > l_maxHD )
                l_maxHD = l_curHD;

        }

        return l_maxHD;
    }

    /**
     * convert BitVector to (0,1) vector as String
     *
     * @param p_bitVector input BitVector
     * @return (0, 1) vector representation as String
     */

    private String toBitString( final BitVector p_bitVector )
    {
        final int[] l_bitInt = new int[p_bitVector.size()];
        for ( int i = 0; i < p_bitVector.size(); i++ )
        {
            if ( p_bitVector.get( i ) )
                l_bitInt[i] = 1;
        }

        return Arrays.toString( l_bitInt );
    }


    /**
     * sort HashMap according to its values in ascending order
     *
     * @param p_valuesMap HashMap with Approval scores
     * @return sorted HashMap
     */


    public Map<Integer, Integer> sortMapASC( final Map<Integer, Integer> p_valuesMap )

    {
        final List<Map.Entry<Integer, Integer>> l_list = new LinkedList<>( p_valuesMap.entrySet() );

        // Sorting the list based on values in ascending order
        Collections.sort( l_list, Comparator.comparing( Map.Entry::getValue ) );

        /* Maintaining insertion order with the help of LinkedList */
        final Map<Integer, Integer> l_sortedMap = new LinkedHashMap<Integer, Integer>();
        for ( final Map.Entry<Integer, Integer> l_entry : l_list )
        {
            l_sortedMap.put( l_entry.getKey(), l_entry.getValue() );
        }

        return l_sortedMap;
    }

}
