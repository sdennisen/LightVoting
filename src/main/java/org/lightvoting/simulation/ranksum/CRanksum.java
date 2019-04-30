package org.lightvoting.simulation.ranksum;

import cern.colt.matrix.tbit.BitVector;

import java.util.List;

public class CRanksum
{
    public static int ranksum(final List<Long> p_voteCLO, final BitVector p_result, final int p_altnum, final int p_comsize)
    {
        System.out.println( "Vote: " + p_voteCLO );
        System.out.println( "Result: " + p_result );

        int l_sum = 0;

        for ( int i=0; i < p_altnum; i++ )
        {
            if ( p_result.get( i ) )
            {
                l_sum = l_sum + getPos( i, p_voteCLO );
            }
        }

        // normalisation: subtract (k*(k+1))/2
        l_sum = l_sum -( p_comsize * ( p_comsize + 1 ) )/2;

        return l_sum;
    }

    private static int getPos(final int p_i, final List<Long> p_voteCLO)
    {
        for ( int j = 0; j < p_voteCLO.size(); j++ )
            if ( p_voteCLO.get( j ) == p_i )
                return ( j+1 );
        return 0;
    }
}
