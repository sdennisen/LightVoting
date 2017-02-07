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



/**
 * Created by sophie on 10.01.17.
 * Computes result of election according to Minisum Approval voting rule.
 */

public class CMinisumApproval
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
     * compute the winning committee according to Minisum Approval
     *
     * @param p_alternatives available alternatives
     * @param p_votes submitted votes
     * @param p_comSize size of committee to be elected
     * @return elected committee
     * re-used code from http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
     */

    public int[] applyRule( final List<String> p_alternatives, final List<int[]> p_votes, final int p_comSize )
    {
        try
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
                    if ( ( m_votes.get( j ) )[i] == 1 )
                    {
                        l_valuesVect[i]++;
                    }
                }

                // create HashMap with index of alternative as key and score of alternative as value

                l_valuesMap.put( i, l_valuesVect[i] );
            }
            // test print of HashMap

            for ( int index : l_valuesMap.keySet() )
            {
                final int l_key = index;
                final String l_value = l_valuesMap.get( index ).toString();
                //System.out.println( "traveller" + ( l_key + 1 ) + " " + l_value );
            }

            // sort the HashMap in descending order according to values

            l_valuesMap = this.sortMap( l_valuesMap );


            // create committee vector according to sorted HashMap: For the first k entries, put a "1" in the position according to the index, i.e. the key.

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

            //System.out.println( "comVect:" + Arrays.toString( m_comVect ) );
        }
        catch ( final Exception l_exep )
        {
            l_exep.printStackTrace();
        }

        return m_comVect;
    }

    /**
     * sort HashMap according to its values in descending order
     * @param p_valuesMap HashMap with Approval scores
     * @return sorted HashMap
     */
    public Map<Integer, Integer> sortMap( final Map<Integer, Integer> p_valuesMap )

    {
        //System.out.println( "Before sorting......" );
        this.printMap( p_valuesMap );

        //System.out.println( "After sorting in descending order......" );
        final boolean l_DESC = false;
        final Map<Integer, Integer> l_sortedMapDesc = this.sortByComparator( p_valuesMap, l_DESC );
        this.printMap( l_sortedMapDesc );

        return l_sortedMapDesc;

    }

    /**
     * print out map
     * @param p_map map to be printed
     */

    public void printMap( final Map<Integer, Integer> p_map )
    {
//        for ( final Entry<Integer, Integer> l_entry : p_map.entrySet() )
//        {
//            //System.out.println( "Key : " + l_entry.getKey() + " Value : " + l_entry.getValue() );
//        }
    }

            /* TODO include lexicographic tie-breaking */

    private Map<Integer, Integer> sortByComparator( final Map<Integer, Integer> p_unsortMap, final boolean p_order )
    {

        final List<Entry<Integer, Integer>> l_list = new LinkedList<>( p_unsortMap.entrySet() );

        // Sorting the list based on values
        Collections.sort( l_list, ( p_first, p_second ) ->
        {
            if ( p_order )
            {
                return p_first.getValue().compareTo( p_second.getValue() );
            }
            else
            {
                return p_second.getValue().compareTo( p_first.getValue() );

            }
        } );

        // Maintaining insertion order with the help of LinkedList
        final Map<Integer, Integer> l_sortedMap = new LinkedHashMap<Integer, Integer>();
        for ( final Entry<Integer, Integer> l_entry : l_list )
        {
            l_sortedMap.put( l_entry.getKey(), l_entry.getValue() );
        }

        return l_sortedMap;
    }
}

/* TODO: Re-use old code? */

//    /* m_alternatives list */
//    private List<String> m_alternatives;
//    /* list of values*/
//    private List<int[]> m_valuesList;
//    /* committee size */
//    private int m_comSize;
//    /* committee */
//    private String[] m_committee;
//    private int[] m_comInt;
//    /* class logger */
//    private Logger m_logger = Logger.getLogger( CMinisumApproval.class.getName() );
//    private double m_average;
//    private long m_max;
//
//    //    private final HDF5Logger m_hdf5Org = HDF5Logger.INSTANCE;
//
//    /**
//     * prints name of the voting rule
//     */
//
//    public void hello()
//    {
//        m_logger.info( "Hello there. I'm an election rule and my name is Minisum Approval." );
//
//    }
//
//    /**
//     * reads parameters for the voting rule and prints them
//     */
//
//    public void readParameters( final List<String> p_alts, final List<int[]> p_valuesList, final int p_comSize )
//    {
//
//        m_alternatives = new ArrayList<String>();
//        m_valuesList = p_valuesList;
//        m_comSize = p_comSize;
//        m_committee = new String[m_comSize];
//
//        for ( int i = 0; i < p_alts.size(); i++ )
//        {
//            m_alternatives.add( p_alts.get( i ) );
//        }
//
//        // logger.info("I received m_alternatives " + m_alternatives);
//
//        //   Iterator<int[]> valIterator = valuesList.iterator();
//     /*   while ( valIterator.hasNext() )
//        {
//            int vals[] = valIterator.next();
//            // logger.info("I received  " + Arrays.toString(vals));
//        }*/
//
//        ////logger.info("I received committee size " + k);
//    }
//
//    /**
//     * computes result for voting rule
//     *
//     * @return committee with k m_alternatives with highest Approval scores
//     *
//     * @throws Exception Throws exception if calculation goes wrong
//     */
//
//    public String computeResult() throws Exception
//    {
//
//        //   this.arch = arch;
//        final int l_altNum = m_alternatives.size();
//        ////logger.info("m: " + m);
//        final int l_votNum = m_valuesList.size();
//        ////logger.info("n: " + n);
//        final Integer[] l_approvalScore = new Integer[l_altNum];
//
//        for ( int i = 0; i < l_altNum; i++ )
//        {
//            int l_sum = 0;
//            for ( int j = 0; j < l_votNum; j++ )
//            {
//                final int[] l_vote = m_valuesList.get( j );
//                l_sum = l_sum + l_vote[i];
//            }
//            l_approvalScore[i] = l_sum;
//        }
//
//        ////logger.info("Approval scores: " + Arrays.toString(approvalScore));
//
//        //   Integer[] sortedScores = doInsertionSortScores( approvalScore );
//
//        ////logger.info("Sorted Approval scores: " + Arrays.toString(sortedScores));
//        ////logger.info("m_alternatives sorted according to Approval scores" + m_alternatives);
//
//        // new
//        m_committee = new String[m_comSize];
//        m_comInt = new int[m_comSize];
//
//        for ( int i = 0; i < m_comSize; i++ )
//        {
//
//            m_logger.info( "i+1 : " + ( i + 1 ) + " Member of winning committee: " + m_alternatives.get( i ) + " with Approval score " + l_approvalScore[i] );
//            m_committee[i] = m_alternatives.get( i );
//            m_comInt[i] = Integer.parseInt( m_committee[i] );
//
//        }
//
//        m_logger.info( "comInt: " + Arrays.toString( m_comInt ) );
//
//        final int[] l_comVect = new int[l_altNum];
//
//        for ( int y = 0; y < l_altNum; y++ )
//            l_comVect[y] = 0;
//
//
//        for ( int i = 0; i < m_comSize; i++ )
//        {
//            // this only works if m_alternatives are given with the enumeration 1,...,m, not with the enumeration 0,...,m-1
//
//            for ( int j = 1; j < l_altNum + 1; j++ )
//            {
//
//                if ( m_comInt[i] == j )
//                {
//                    l_comVect[j - 1] = 1;
//                }
//                // else comVect[j-1] = 0; // if you comment this out, this does not help (of course)
//            }
//        }
//
//        int l_indexL = 0;
//
//        for ( int x = 0; x < l_altNum; x++ )
//        {
//            if ( l_comVect[x] == l_indexL )
//            {
//                l_indexL++;
//                m_logger.info( "l: " + l_indexL );
//            }
//            if ( l_indexL > m_comSize )
//                m_logger.info( "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX Too many ones! XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" );
//        }
//
//        if ( l_indexL < m_comSize )
//            m_logger.info( "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX Too few ones! XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" );
//
//
//        m_logger.info( "comVect: " + Arrays.toString( l_comVect ) );
//
//        // determine average dissatisfaction regarding selected committee
//
//        final Integer[] l_hds = new Integer[l_votNum];
//
//        for ( int i = 0; i < l_votNum; i++ )
//        {
//            l_hds[i] = this.hammingDist( m_valuesList.get( i ), l_comVect );
//            // logger.info("hd :" + Arrays.toString(valuesList.get(i)) + " " + Arrays.toString(comVect) + " " + p_hds[i]);
//        }
//
//        // determine maximal dissatisfaction regarding selected committee
//
//        final Integer[] l_maxs = this.doInsertionSort( l_hds );
//
//        m_average = this.average( l_hds );
//        m_max = l_maxs[0];
//
//        //logger.info("max hd: " + m_max);
//
//        // final String HDF5_FILE = "test.hdf5";
//
//
//        //
//
//        //logger.info("com " + Arrays.toString(comVect) + "avg " + average + " max " + m_max + " run " + arch.getRunId() + " it " + arch.getIt());
//
//        //REINSERT m_hdf5Org.writeRuleOutputData(HDF5_FILE, arch.getRunId(), arch.getRuleId(), arch.getIt(), average, maxs[0], comVect);
//
//        return Arrays.toString( l_comVect );
//
//    }
//
//    /**
//     * Computes average value
//     *
//     * @param p_vals input values
//     * @return average
//     */
//
//    public double average( final Integer[] p_vals )
//    {
//
//        // calculate sum
//        int l_sum = 0;
//        for ( int i = 0; i < p_vals.length; i++ )
//        {
//            l_sum = l_sum + p_vals[i];
//        }
//        // calculate average
//        final double l_average = ( (double) l_sum ) / ( (double) p_vals.length );
//        return l_average;
//    }
//
//    /**
//     * Computes Hamming distance between committee and Approval vote
//     *
//     * @param p_com Committee
//     * @param p_vec Approval vote
//     * @return Hamming distance between p_com and p_vec
//     */
//
//    public int hammingDist( final int[] p_com, final int[] p_vec )
//    {
//
//        // length of the arrays equals the number of m_alternatives
//        final int l_altNum = p_vec.length;
//        int dist = 0;
//
//        for ( int i = 0; i < l_altNum; i++ )
//        {
//            if ( p_com[i] != p_vec[i] )
//                dist++;
//        }
//        return dist;
//    }
//
//    /**
//     * Sorts the candidates in decreasing order according to Approval Scores
//     *
//     * @param p_scores Approval scores for all alternatives
//     * @return the Approval scores of the alternatives in decreasing order as <tt> int[]</tt>
//     */
//
//    public Integer[] doInsertionSortScores( final Integer[] p_scores )
//    {
//
//        int l_temp;
//        String l_tempAlt;
//        for ( int i = 1; i < p_scores.length; i++ )
//        {
//            for ( int j = i; j > 0; j-- )
//            {
//                if ( p_scores[j] > p_scores[j - 1] )
//                {
//                    l_temp = p_scores[j];
//                    l_tempAlt = m_alternatives.get( j );
//                    p_scores[j] = p_scores[j - 1];
//                    m_alternatives.set( j, m_alternatives.get( j - 1 ) );
//                    p_scores[j - 1] = l_temp;
//                    m_alternatives.set( j - 1, l_tempAlt );
//                }
//            }
//        }
//        return p_scores;
//    }
//
//    /**
//     * Sorts Hamming distances in decreasing order
//     *
//     * @param p_hds Hamming distances
//     * @return the ordered Hamming distances
//     */
//
//    public Integer[] doInsertionSort( final Integer[] p_hds )
//    {
//
//        int l_temp;
//        for ( int i = 1; i < p_hds.length; i++ )
//        {
//            for ( int j = i; j > 0; j-- )
//            {
//                if ( p_hds[j] > p_hds[j - 1] )
//                {
//                    l_temp = p_hds[j];
//                    p_hds[j] = p_hds[j - 1];
//                    p_hds[j - 1] = l_temp;
//                }
//            }
//        }
//        return p_hds;
//    }
//
//    public String toString()
//    {
//        return "MinisumApproval";
//    }
//
//    // @Override
//    public double getAvg()
//    {
//        return m_average;
//    }
//
//    // @Override
//    public double getMax()
//    {
//        return (double) m_max;
//    }



