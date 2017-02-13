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
import edu.memphis.ccrg.lida.episodicmemory.sdm.BitVectorUtils;
import org.lightvoting.simulation.combinations.CCombination;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* TODO later, compute possible committees independently from used voting rule */
/* TODO compute Hamming distance without BitVectorUtils */

/**
 * Created by sophie on 10.01.17.
 */
public class CMinimaxApproval
{

    /* m_alternatives list */
    private List<String> m_alternatives;
    /* list of values*/
    private List<int[]> m_votes;
    /* committee size */
    private int m_comSize;
    /* committee */
    private int[] m_comVect;


    /**
     * compute the winning committee according to Minimax Approval
     *
     * @param p_alternatives available alternatives
     * @param p_votes submitted votes
     * @param p_comSize size of committee to be elected
     * @return elected committee
     */

    public int[] applyRule( final List<String> p_alternatives, final List<int[]> p_votes, final int p_comSize )
    {
        m_alternatives = p_alternatives;
        m_votes = p_votes;
        m_comSize = p_comSize;
        m_comVect = new int[m_alternatives.size()];

        // compute all possible committees, i.e. all {0,1}^m vectors with exactly k ones
        final int[][] l_committees = this.computeComittees( m_votes.size(), m_alternatives.size(), m_comSize );

        return new int[0];
    }

    /**
     * compute all possible committees for given number of alternatives and committee size
     *
     * @param p_votNum number of votes
     * @param p_altNum number of alternatives
     * @param p_comSize size of committee to be elected
     * @return all possible committees
     */

    private int[][] computeComittees( final int p_votNum, final int p_altNum, final int p_comSize )
    {
        final CCombination l_combination = new CCombination();
        final int[] l_arr = new int[p_altNum];

        for ( int i = 0; i < p_altNum; i++ )
            l_arr[i] = i;

        l_combination.combinations( l_arr, p_comSize, 0, new int[p_comSize] );

        final List<int[]> l_resultList = l_combination.getResultList();
        l_combination.clearList();

        for ( int i = 0; i < l_resultList.size(); i++ )
        {
            System.out.println( Arrays.toString( l_resultList.get( i ) ) );
        }

        System.out.println( "Number of committees: " + l_resultList.size() );

        final int[][] l_comVects = new int[l_resultList.size()][l_arr.length];

        for ( int i = 0; i < l_resultList.size(); i++ )
        {

            for ( int j = 0; j < 3; j++ )
            {
                //System.out.println( " i: " + i + " j: " + j + " l_index: " + l_index + " value: " + l_resultList.get( i )[j]);
                l_comVects[i][l_resultList.get( i )[j]] = 1;
            }
            System.out.println( "Committee " + i + ": " + Arrays.toString( l_comVects[i] ) );
        }

        /* Hashmap for storing the maximal hamming distance to any vote for all committees */

        final Map<int[], Integer> l_maxMap = new HashMap<int[], Integer>();
        for ( int i = 0; i < l_comVects.length; i++ )
        {
            final int l_maxHD = this.determineMaxHD( m_votes, l_comVects[i] );
        }


        return new int[0][0];
    }

    /* TO DO write toBitString */

    private int determineMaxHD( final List<int[]> p_votes, final int[] p_comVect )
    {

        /* hamming(cern.colt.bitvector.BitVector vector1, cern.colt.bitvector.BitVector vector2) */

        final Boolean[] l_booleanCom = new Boolean[m_alternatives.size()];

        for (  int i = 0; i < m_alternatives.size(); i++ )
            if ( p_comVect[i] == 1 )
                l_booleanCom[i] = true;
            else l_booleanCom[i] = false;

        final BitVector l_bitCom = new BitVector( m_alternatives.size() );

        for ( int i = 0;  i < m_alternatives.size(); i++ )
        {
            l_bitCom.put( i, l_booleanCom[i] );
        }

        System.out.println( "Committee: " + this.toBitString( l_bitCom ) );

        for ( int i = 0; i < p_votes.size(); i++ )
        {
            final Boolean[] l_booleanVote = new Boolean[m_alternatives.size()];

            for (  int j = 0; j < m_alternatives.size(); j++ )
                if ( p_votes.get( i )[j] == 1 )
                    l_booleanVote[j] = true;
                else l_booleanVote[j] = false;

            final BitVector l_bitVote = new BitVector( m_alternatives.size() );

            for ( int j = 0;  j < m_alternatives.size(); j++ )
            {
                l_bitVote.put( j, l_booleanVote[j] );
            }

            System.out.println( "com " + Arrays.toString( p_comVect ) + " v " + Arrays.toString( p_votes.get( i ) ) + " hd " + BitVectorUtils.hamming( l_bitCom, l_bitVote ) );

        }

        return -1;
    }

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

}




