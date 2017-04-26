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


//import java.util.Arrays;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicIntegerArray;



/**
 * Created by sophie on 10.01.17.
 * Computes result of election according to Minisum Approval voting rule.
 */

public class CMinisumApproval
{

    /* m_alternatives list */
    private List<String> m_alternatives;
    /* list of values*/
    private List<AtomicIntegerArray> m_votes;
    /* committee size */
    private int m_comSize;
    /* committee */
    private int[] m_comVect;


    /**
     * compute the winning committee according to Minisum Approval
     *
     * @param p_alternatives available alternatives
     * @param p_votes submitted votes
     * @param p_comSize size of committee to be elected
     * @return elected committee
     * re-used code from http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
     */

    public int[] applyRule( final List<String> p_alternatives, final List<AtomicIntegerArray> p_votes, final int p_comSize )
    {
        m_alternatives = p_alternatives;
        m_votes = p_votes;
        m_comSize = p_comSize;
        m_comVect = new int[m_alternatives.size()];

        final int[] l_valuesVect = new int[m_alternatives.size()];

        Map<Integer, Integer> l_valuesMap = new HashMap<Integer, Integer>();
        for ( int i = 0; i < m_alternatives.size(); i++ )
        {
            for ( int j = 0; j < m_votes.size(); j++ )
            {
                if ( ( m_votes.get( j ) ).get( i ) == 1 )
                {
                    l_valuesVect[i]++;
                }
            }

            /* create HashMap with index of alternative as key and score of alternative as value */

            l_valuesMap.put( i, l_valuesVect[i] );
        }

        /* test print of HashMap  */

        for ( int index : l_valuesMap.keySet() )
        {
            final int l_key = index;
            final String l_value = l_valuesMap.get( index ).toString();
        }

        /* sort the HashMap in descending order according to values */

        l_valuesMap = this.sortMapDESC( l_valuesMap );

        /* create committee vector according to sorted HashMap: For the first k entries, put a "1" in the position according to the index, i.e. the key.*/

        int l_occupied = 0;

        for ( final Entry<Integer, Integer> l_entry : l_valuesMap.entrySet() )
        {
            if ( l_occupied < m_comSize )
            {
                m_comVect[l_entry.getKey()] = 1;
                l_occupied++;
            }
            else
                break;
        }

        return m_comVect;
    }

    /**
     * sort HashMap according to its values in descending order
     *
     * @param p_valuesMap HashMap with Approval scores
     * @return sorted HashMap
     */
    public Map<Integer, Integer> sortMapDESC( final Map<Integer, Integer> p_valuesMap )

    {

        final List<Entry<Integer, Integer>> l_list = new LinkedList<>( p_valuesMap.entrySet() );

        /* Sorting the list based on values in descending order */
        Collections.sort( l_list, ( p_first, p_second ) ->
            p_second.getValue().compareTo( p_first.getValue() ) );

        /* Maintaining insertion order with the help of LinkedList */
        final Map<Integer, Integer> l_sortedMap = new LinkedHashMap<Integer, Integer>();
        for ( final Entry<Integer, Integer> l_entry : l_list )
        {
            l_sortedMap.put( l_entry.getKey(), l_entry.getValue() );
        }

        return l_sortedMap;
    }

}

// XXXXXXXXXX Old Code XXXXXX
// ---- was in applyRule() ----

// System.out.println( "traveller" + ( l_key + 1 ) + " " + l_value );
// System.out.println( "comVect:" + Arrays.toString( m_comVect ) );
