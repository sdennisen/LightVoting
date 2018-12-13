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

package org.lightvoting.simulation.agent.coordinated_basic;

import cern.colt.Arrays;
import cern.colt.matrix.tbit.BitVector;
import com.google.common.util.concurrent.AtomicDoubleArray;
import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.agent.IBaseAgent;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.lightvoting.simulation.action.message.coordinated_basic.CSendCB;
import org.lightvoting.simulation.constants.CVariableBuilder;
import org.lightvoting.simulation.environment.coordinated_basic.CEnvironmentCB;
import org.lightvoting.simulation.environment.coordinated_basic.CGroupCB;
import org.lightvoting.simulation.statistics.EDataDB;

import java.io.InputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import org.lightjason.agentspeak.language.score.IAggregation;


/**
 * BDI agent with voting capabilities.
 * re-used code from http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
 */
// annotation to mark the class that actions are inside
@IAgentAction
public final class CVotingAgentCB extends IBaseAgent<CVotingAgentCB>
{

    /**
     * serialUID
     */

    private static final long serialVersionUID = 8515283969360402946L;
    private final Boolean m_ndiss;

    /**
     * name of the agent
     */
    private String m_name;

    /**
     * environment
     */
    private CEnvironmentCB m_environment;

    /**
     * associated chair agent;
     */
    private CChairAgentCB m_chair;

    /**
     * agent's vote
     */

    private AtomicIntegerArray m_vote;

    /**
     * number of alternatives
     */
    private int m_altNum;

    /**
     * agent's preferences
     */
    private AtomicDoubleArray m_atomicPrefValues;

    private final Map<Long, Double> m_atomicPrefMap = new ConcurrentHashMap<>(  );

    /**
     * grouping algorithm: "RANDOM" or "COORDINATED"
     */
    private String m_grouping;

    /**
     * variable indicating if agent already submitted its vote
     */

    private boolean m_voted;

    /**
     * threshold for joining a group in the case of coordinated grouping
     */
    private double m_joinThreshold;
    private BitVector m_bitVote;
    private List<Long> m_cLinearOrder;
    private HashMap<String, Object> m_map = new HashMap<>();
    private AtomicLong m_liningCounter = new AtomicLong();
    private boolean m_hasDiss;
    private AtomicLong m_cycle = new AtomicLong();

    // TODO set via config
    private final String m_rule;
    private int m_run;
    private int m_sim;
    private int m_comsize;

    // TODO refactor ctors

    /**
     * constructor of the agent
     * @param p_name name of the agent
     * @param p_configuration agent configuration of the agent generator
     * @param p_chairagent corresponding chair agent
     * @param p_environment environment reference
     * @param p_altNum number of alternatives
     * @param p_joinThr join threshold
     * @param p_preferences preferences
     * @param p_sim
     * @param p_run
     * @param p_ndiss
     */

    public CVotingAgentCB(final String p_name, final IAgentConfiguration<CVotingAgentCB> p_configuration, final IBaseAgent<CChairAgentCB> p_chairagent,
                          final CEnvironmentCB p_environment,
                          final int p_altNum,
                          final double p_joinThr,
                          final AtomicDoubleArray p_preferences, final String p_rule,
                          int p_sim, int p_run, Boolean p_ndiss, int p_comsize )
    {
        super( p_configuration );
        m_name = p_name;
        m_environment = p_environment;

        m_chair = (CChairAgentCB) p_chairagent;

        m_storage.put( "chair", p_chairagent.raw() );

        m_beliefbase.add(
            CLiteral.from(
                "chair",
                CRawTerm.from( p_chairagent )
            )
        );

        // sleep chair, Long.MAX_VALUE -> inf
        p_chairagent.sleep( Long.MAX_VALUE );

        m_altNum = p_altNum;

        m_atomicPrefValues = p_preferences;
        System.out.println( p_preferences );
     //   m_atomicPrefValues = this.generatePreferences( m_altNum );
        m_vote = this.convertPreferences( m_atomicPrefValues );
        m_bitVote = this.convertPreferencesToBits( m_atomicPrefValues );
        m_voted = false;
        m_joinThreshold = p_joinThr;
        m_rule = p_rule;
        m_sim = p_sim;
        m_run = p_run;
        m_ndiss = p_ndiss;
        m_comsize = p_comsize;
    }

    /**
     * constructor
     * @param p_name name
     * @param p_configuration configuration
     * @param p_environment environment
     * @param p_altNum number of alternatives
     * @param p_joinThr join threshold
     * @param p_atomicDoubleArray preferences
     * @param p_sim simulation id
     * @param p_run run id
     * @param p_ndiss
     * @param p_comsize
     */
    public CVotingAgentCB(final String p_name, final IAgentConfiguration<CVotingAgentCB> p_configuration, final CEnvironmentCB p_environment, final int p_altNum,
                          final double p_joinThr,
                          final AtomicDoubleArray p_atomicDoubleArray,
                          final String p_rule,
                          int p_sim, int p_run, Boolean p_ndiss, int p_comsize)
    {
        super( p_configuration );
        m_name = p_name;
        m_altNum = p_altNum;
        m_comsize = p_comsize;
        m_atomicPrefValues = p_atomicDoubleArray;
        m_sim = p_sim;
        m_run = p_run;
        m_ndiss = p_ndiss;

        for ( int i=0; i < m_altNum; i++ )
            m_atomicPrefMap.put( (long) i, m_atomicPrefValues.get( i ) );

        System.out.println( "AtomicPrefMap: " + m_atomicPrefMap.entrySet() );

        // store preferences in map
        m_map.put( this.name() + "/preferences", m_atomicPrefValues );

        m_rule = p_rule;

        if ( m_rule.equals( "MINISUM_APPROVAL") || m_rule.equals( "MINIMAX_APPROVAL" ) )
            m_bitVote = this.convertPreferencesToBits( m_atomicPrefValues );
        else if ( m_rule.equals( "K_MINISUM_APPROVAL" ) )
            m_bitVote = this.convertPreferencesToBitsKAV( m_atomicPrefValues );
        else
            // if ( m_rule.equals( "MINISUM_RANKSUM") )
            m_cLinearOrder = this.convertPreferencestoCLO();
        System.out.println( this.name() + " Vote as complete linear order " + m_cLinearOrder );

        System.out.println( this );
    }

    // overload agent-cycle
    @Override
    public final CVotingAgentCB call() throws Exception
    {
        // run default cycle
        return super.call();
    }

    // public methods

    /**
     * Get agent's name
     *
     * @return name of agent
     */
    public final String name()
    {
        return m_name;
    }

    /**
     * get associated chair agent
     *
     * @return chair agent
     */
    public CChairAgentCB getChair()
    {
        return m_chair;
    }

    public AtomicIntegerArray getVote()
    {
        return m_vote;
    }

    public BitVector getBitVote()
    {
        return m_bitVote;
    }

    public List<Long> getCLOVote()
    {
        return m_cLinearOrder;
    }

    /**
     * reset voting agent for next simulation run
     */

    public void reset()
    {
        m_voted = false;

        this.trigger(
            CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "main"
                )
            )
        );
    }

    public void setConf( final String p_grouping )
    {
        m_grouping = p_grouping;
    }

    public long liningCounter()
    {
        return m_liningCounter.incrementAndGet();
    }

    public void setChair( final CChairAgentCB p_chair )
    {
        m_chair = p_chair;
    }

    // agent actions

    @IAgentActionFilter
    @IAgentActionName( name = "update/cycle" )
    private void updateCycle()
    {
        m_cycle.incrementAndGet();
        System.out.println( "cycle counter incremented to " + m_cycle );
    }

    @IAgentActionFilter
    @IAgentActionName( name = "perceive/env" )
    private void perceiveEnv()
    {
        this.beliefbase().add( m_environment.literal( this ) );
        System.out.println( this.name() + " perceived environment " );
    }

    public boolean hasDiss()
    {
        return m_hasDiss;
    }

//    @IAgentActionFilter
//    @IAgentActionName( name = "join/group" )
//    private void joinGroup()
//    {
//        if ( "RANDOM".equals( m_grouping ) )
//            this.joinGroupRandom();
//
//        if ( "COORDINATED".equals( m_grouping ) )
//            this.joinGroupCoordinated();
//    }

    @IAgentActionFilter
    @IAgentActionName( name = "submit/vote" )
    private void submitVote( final CChairAgentCB p_chairAgent )
    {

        // for MS-AV and MM-AV, the votes are 01-vectors
        if ( m_rule.equals( "MINISUM_APPROVAL") || m_rule.equals( "MINIMAX_APPROVAL" ) || m_rule.equals( "K_MINISUM_APPROVAL") )

            this.submitAV( p_chairAgent );

        else
            // if ( m_rule.equals( "MINISUM_RANKSUM") )
            // for MS-RS, the votes are complete linear orders
            this.submitCLO( p_chairAgent );

        //       p_chairAgent.beliefbase().beliefbase().add( CLiteral.from( "vote", CRawTerm.from( this ), CRawTerm.from( this.getBitVote() ) ) );

    }

    // submit Approval vote to chair
    private void submitAV( final CChairAgentCB p_chairAgent )
    {
        System.out.println( "my name is " + this.name() );
        System.out.println( "my vote is " + this.getBitVote() );
        System.out.println( "my chair: " + p_chairAgent.name() );

        p_chairAgent.trigger(
            CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "stored/vote",
                    CRawTerm.from( this.name() ),
                    CRawTerm.from( this.getBitVote() )
                )
            )
        );
    }

    // submit complete linear order to chair
    private void submitCLO( final CChairAgentCB p_chairAgent )
    {
        System.out.println( "my name is " + this.name() );
        System.out.println( "my vote is " + this.getCLOVote() );
        System.out.println( "my chair: " + p_chairAgent.name() );

        List<Long> l_vote = this.getCLOVote();

        p_chairAgent.trigger(
            CTrigger.from(
                ITrigger.EType.ADDGOAL,
                CLiteral.from(
                    "stored/vote",
                    CRawTerm.from( this ),
                    CRawTerm.from( l_vote )
                )
            )
        );
    }


    @IAgentActionFilter
    @IAgentActionName( name = "submit/diss" )
    private void submitDiss(final CChairAgentCB p_chairAgent, final BitVector p_result, final int p_imNum, int p_dbGroup ) throws InterruptedException, SQLException {
        // store dissatisfaction with election result in map
        m_map.put( this.name() + "/diss", this.computeDissBV( p_result ) );
        // store waiting time in map
        System.out.println( "cycle " + this.cycleCounter() );
        m_map.put( this.name() + "/waiting time", this.cycleCounter().longValue() );

        // store waiting time in database

        EDataDB.INSTANCE.setTime( this.cycleCounter().intValue(), this.name(), m_run, m_sim );

        m_hasDiss = true;

        p_chairAgent.trigger(
                CTrigger.from(
                        ITrigger.EType.ADDGOAL,
                        CLiteral.from(
                                "stored/diss",
                                CRawTerm.from( this.name() ),
                                CRawTerm.from( this.computeDissBV( p_result ) ),
                                CRawTerm.from( p_imNum ),
                                CRawTerm.from( p_dbGroup )
                        )
                )
        );
    }

 /*   @IAgentActionFilter
    @IAgentActionName( name = "compute/diss" )
    private void computeDiss( final CChairAgentCB p_chairAgent, final BitVector p_result ) throws InterruptedException
    {
        System.out.println( "computing diss " );

        final double l_diss = this.computeDissBV( p_result );
        // store dissatisfaction with election result in map
        m_map.put( this.name() + "/diss", l_diss );
        // store waiting time in map
        System.out.println( "cycle " + this.cycleCounter() );
        m_map.put( this.name() + "/waiting time", this.cycleCounter().longValue() );
        // store lining counter in map
        System.out.println( "lining counter " + m_liningCounter );
        m_map.put( this.name() + "/lining counter", m_liningCounter );

        m_map.put( m_chair.name() + "/" + this.name(), l_diss );

        m_hasDiss = true;


    }*/

    private AtomicLong cycleCounter()
    {
        return m_cycle;
    }


    //    @IAgentActionFilter
//    @IAgentActionName( name = "submit/dissatisfaction" )
//    private void submitDiss( final CChairAgent p_chairAgent, final Integer p_iteration, final BitVector p_result ) throws InterruptedException
//    {
//        p_chairAgent.trigger(
//            CTrigger.from(
//                ITrigger.EType.ADDGOAL,
//                CLiteral.from(
//                    "diss/received",
//                    CRawTerm.from( this.name() ),
//                    CRawTerm.from( this.computeDissBV( p_result ) ),
//                    CRawTerm.from( p_iteration )
//                )
//            )
//        );
//    }

//    @IAgentActionFilter
//    @IAgentActionName( name = "submit/final/diss" )
//    private void submitFinalDiss( final CChairAgent p_chairAgent, final BitVector p_result, final Integer p_iteration ) throws InterruptedException
//    {
//        p_chairAgent.trigger(
//            CTrigger.from(
//                ITrigger.EType.ADDGOAL,
//                CLiteral.from(
//                    "final/diss/received",
//                    CRawTerm.from( this.name() ),
//                    CRawTerm.from( this.computeDissBV( p_result ) ),
//                    CRawTerm.from( p_iteration )
//                )
//            )
//        );
//    }

    private Double computeDissBV( final BitVector p_result )
    {
        if ( m_ndiss )
            return this.computeNDissBV( p_result );

        double l_diss = 0;

        for ( int i = 0; i < p_result.size(); i++ )
        {
            if ( p_result.get( i ) )
                l_diss = l_diss + ( 1 - m_atomicPrefValues.get( i ) );
        }
        return l_diss;
    }


    private Double computeNDissBV(BitVector p_result)
    {

        double l_sat = 0;

        for ( int i = 0; i < p_result.size(); i++ )
            if ( p_result.get( i ) )
                l_sat = l_sat + m_atomicPrefValues.get( i );

        /* create HashMap with index of alternative as key and preference for alternative as value */
        Map<Integer, Double> l_valuesMap = new HashMap<>();

        for ( int i = 0; i < m_atomicPrefValues.length(); i++ )
            l_valuesMap.put( i, m_atomicPrefValues.get( i ) );

        /* sort the HashMap in descending order according to values */

        l_valuesMap = this.sortDoubleMapDESC( l_valuesMap );

        /* sum up preference values for k best candidates */

        /* Compute sum of pref values for the first k entries */

        double l_prefSum = 0;
        int l_numPos = 0;

        for ( final Map.Entry<Integer, Double> l_entry : l_valuesMap.entrySet() )
        {
            if ( l_numPos < m_comsize )
            {
                l_prefSum = l_prefSum + l_entry.getValue();
                l_numPos++;
            }
            else
                break;
        }

        System.out.println( m_atomicPrefValues );
        System.out.println( "ndiss: " + ( 1 - l_sat/l_prefSum ) );

        return 1 - l_sat/l_prefSum;

    }

    private Map<Integer,Double> sortDoubleMapDESC(Map<Integer, Double> p_valuesMap)
    {
        final List<Map.Entry<Integer, Double>> l_list = new LinkedList<>( p_valuesMap.entrySet() );

        /* Sorting the list based on values in descending order */

        Collections.sort( l_list, ( p_first, p_second ) ->
                p_second.getValue().compareTo( p_first.getValue() ) );

        /* Maintaining insertion order with the help of LinkedList */

        final Map<Integer, Double> l_sortedMap = new LinkedHashMap<>();
        for ( final Map.Entry<Integer, Double> l_entry : l_list )
        {
            l_sortedMap.put( l_entry.getKey(), l_entry.getValue() );
        }

        return l_sortedMap;

    }

    public HashMap<String, Object> map()
    {
        return m_map;
    }

    // private methods

    private void setPreference( final AtomicDoubleArray p_atomicDoubleArray )
    {
        m_atomicPrefValues = p_atomicDoubleArray;
    }

    private AtomicIntegerArray convertPreferences( final AtomicDoubleArray p_atomicPrefValues )
    {
        final int[] l_voteValues = new int[m_altNum];
        for ( int i = 0; i < m_altNum; i++ )
            if ( p_atomicPrefValues.get( i ) > 0.5 )
                l_voteValues[i] = 1;
            else
                l_voteValues[i] = 0;
        System.out.println( "Vote: " + Arrays.toString( l_voteValues ) );
        return new AtomicIntegerArray( l_voteValues );
    }

    private BitVector convertPreferencesToBits( final AtomicDoubleArray p_atomicPrefValues )
    {
        final BitVector l_voteValues = new BitVector( m_altNum );
        for ( int i = 0; i < m_altNum; i++ )
            if ( p_atomicPrefValues.get( i ) > 0.5 )
                l_voteValues.put( i, true );
            else
                l_voteValues.put( i, false );
        System.out.println( "Vote as BitVector: " + l_voteValues  );
        return l_voteValues;
    }

    private BitVector convertPreferencesToBitsKAV( final AtomicDoubleArray p_atomicPrefValues )
    {
        final BitVector l_voteValues = new BitVector( m_altNum );

        /* create HashMap with index of alternative as key and preference for alternative as value */
        Map<Integer, Double> l_valuesMap = new HashMap<>();

        for ( int i = 0; i < p_atomicPrefValues.length(); i++ )
            l_valuesMap.put( i, p_atomicPrefValues.get( i ) );

        /* sort the HashMap in descending order according to values */

        l_valuesMap = this.sortDoubleMapDESC( l_valuesMap );

        /* set approval value to 1 for positions of k highest values */
        int l_numPos = 0;

        for ( final Map.Entry<Integer, Double> l_entry : l_valuesMap.entrySet() )
        {
            if ( l_numPos < m_comsize )
            {
                l_voteValues.put( l_entry.getKey(), true );
                l_numPos++;
            }
            else
                break;
        }

        System.out.println( m_atomicPrefValues );
        System.out.println( "Vote as BitVector: " + l_voteValues  );
        return l_voteValues;

    }

    private List<Long> convertPreferencestoCLO()
    {
        return m_atomicPrefMap.entrySet().stream().sorted( ( e1, e2 ) -> {
            if (  e1.getValue() > e2.getValue() )
                return -1;
            if ( e1.getValue() < e2.getValue() )
                return 1;
            return 0;
        } ).map( Map.Entry::getKey ).collect( Collectors.toList() );

    }


//    private List<CGroupCB> determineActiveGroups( final String p_grouping )
//    {
//        final AtomicReference<List<CGroupCB>> l_groupList = new AtomicReference<>();
//
//        m_beliefbase.beliefbase().literal( "groups" ).stream().forEach( i ->
//        {
//            System.out.println( " ------------- Adding group " + i.values().findFirst().get().raw() );
//            l_groupList.set( ( (ILiteral) i ).values().findFirst().get().raw() );
//            System.out.println( "Size of group list: " + l_groupList.get().size() );
//        } );
//
//        return l_groupList.get()
//                   .stream()
//                   .parallel()
//                   .filter( i -> "RANDOM".equals( p_grouping ) ? i.open() : i.open() && i.result() != null )
//                   .collect( Collectors.toList() );
//
//    }

//    private void openNewGroup()
//    {
//        final CGroupCB l_group;
//
//        if ( "RANDOM".equals( m_grouping ) )
//            l_group = m_environment.openNewGroupRandom( this );
//
//        else
//            l_group = m_environment.openNewGroupCoordinated( this );
//
//        this.beliefbase().add( l_group.literal( this ) );
//        System.out.println( "opened new group " + l_group );
//    }

//    private void joinGroupRandom()
//    {
//
//        final List<CGroupCB> l_activeGroups = this.determineActiveGroups( "RANDOM" );
//
//        if ( l_activeGroups.isEmpty() )
//        {
//            this.openNewGroup();
//            return;
//        }
//
//        final Random l_rand = new Random();
//
//        final CGroupCB l_randomGroup = l_activeGroups.get( l_rand.nextInt( l_activeGroups.size() ) );
//        m_environment.addAgentRandom( l_randomGroup, this );
//        this.beliefbase().add( l_randomGroup.literal( this ) );
//
//    }

//    private void joinGroupCoordinated()
//    {
//        System.out.println( "join group according to coordinated grouping algorithm" );
//
//        final List<CGroupCB> l_activeGroups = this.determineActiveGroups( "COORDINATED" );
//
//        if ( l_activeGroups.isEmpty() )
//        {
//            this.openNewGroup();
//            return;
//        }
//
//        else
//            this.determineGroupCoordinated( l_activeGroups );
//    }

//    private void determineGroupCoordinated( final List<CGroupCB> p_activeGroups )
//    {
//        final CGroupCB l_group;
//        // choose group to join
//        final Map<CGroupCB, Integer> l_groupDistances = new HashMap<>();
//        final BitVector l_vote = this.getBitVote();
//        System.out.println( "Vote: " + l_vote );
//        for ( int i = 0; i < p_activeGroups.size(); i++ )
//        {
//            final BitVector l_com =  p_activeGroups.get( i ).result();
//            System.out.println( "Committee: " + l_com );
//
//            l_com.xor( l_vote );
//            final int l_HD = l_com.cardinality();
//            System.out.println( "Hamming distance: " + l_HD );
//            l_groupDistances.put( p_activeGroups.get( i ), l_HD );
//        }
//        final Map l_sortedDistances = this.sortMapDESC( l_groupDistances );
//        final Map.Entry<CGroupCB, Integer> l_entry = (Map.Entry<CGroupCB, Integer>) l_sortedDistances.entrySet().iterator().next();
//        l_group = l_entry.getKey();
//
//        // if Hamming distance is above the threshold, do not join the chair but create a new group
//        if ( l_entry.getValue() > m_joinThreshold )
//        {
//            this.openNewGroup();
//            return;
//        }
//        m_environment.addAgentCoordinated( l_group, this );
//        this.beliefbase().add( l_group.literal( this ) );
//        System.out.println( this.name() + " joins group " + l_group );
//    }

    /**
     * compute dissatisfaction of voter with given committee
     *
     * @param p_resultValues committee
     * @return dissatisfaction with committee
     */

    private double computeDiss( final int[] p_resultValues )
    {
        double l_diss = 0;

        for ( int i = 0; i < p_resultValues.length; i++ )
        {
            if ( p_resultValues[i] == 1 )
                l_diss = l_diss + ( 1 - m_atomicPrefValues.get( i ) );
        }
        return l_diss;
    }



    private Map sortMapDESC( final Map<CGroupCB, Integer> p_valuesMap )
    {
        final List<Map.Entry<CGroupCB, Integer>> l_list = new LinkedList<>( p_valuesMap.entrySet() );

        /* Sorting the list based on values in descending order */

        Collections.sort( l_list, ( p_first, p_second ) ->
            p_second.getValue().compareTo( p_first.getValue() ) );

        /* Maintaining insertion order with the help of LinkedList */

        final Map<CGroupCB, Integer> l_sortedMap = new LinkedHashMap<>();
        for ( final Map.Entry<CGroupCB, Integer> l_entry : l_list )
        {
            l_sortedMap.put( l_entry.getKey(), l_entry.getValue() );
        }

        return l_sortedMap;
    }

    /**
     * Class CVotingAgentGenerator
     */
    public static final class CVotingAgentGenerator extends IBaseAgentGenerator<CVotingAgentCB>
    {

        /**
         * Store reference to send action to registered agents upon creation.
         */
        private final CSendCB m_send;

        /**
         * Current free agent id, needs to be thread-safe, therefore using AtomicLong.
         */
        private final AtomicLong m_agentcounter = new AtomicLong();

        /**
         * environment reference
         */
        private final CEnvironmentCB m_environment;

        /**
         * number of alternatives
         */
        private final int m_altNum;
        private final String m_fileName;
        private final Boolean m_ndiss;
        private double m_joinThr;
        private final List<AtomicDoubleArray> m_prefList;
        private int m_count;
        private String m_rule;
        private int m_sim;
        private int m_run;
        private int m_comsize;

        /**
         * constructor of the generator
         * @param p_stream ASL code as any stream e.g. FileInputStream
         * @param p_altNum number of alternatives
         * @param p_fileName h5 file
         * @param p_joinThr join threshold
         * @param p_preferences preferences
         * @param p_run
         * @param p_sim
         * @param p_ndiss
         * @param p_comsize
         * @throws Exception Thrown if something goes wrong while generating agents.
         */
        public CVotingAgentGenerator(final CSendCB p_send, final InputStream p_stream, final CEnvironmentCB p_environment, final int p_altNum,
                                     final String p_fileName,
                                     final double p_joinThr,
                                     final List<AtomicDoubleArray> p_preferences,
                                     final String p_rule,
                                     int p_run, int p_sim, Boolean p_ndiss, int p_comsize) throws Exception
        {

            super(
                // input ASL stream
                p_stream,

                // a set with all possible actions for the agent
                Stream.concat(
                    // we use all build-in actions of LightJason
                    CCommon.actionsFromPackage(),
                    Stream.concat(
                        // use the actions which are defined inside the agent class
                        CCommon.actionsFromAgentClass( CVotingAgentCB.class ),
                        // add VotingAgent related external actions
                        Stream.of(
                            p_send
                        )
                    )
                    // build the set with a collector
                ).collect( Collectors.toSet() ),

                // aggregation function for the optimization function, here
                // we use an empty function
       //         IAggregation.EMPTY,

                // variable builder
                new CVariableBuilder( p_environment )
            );

            m_send = p_send;
            m_environment = p_environment;
            m_altNum = p_altNum;
            m_fileName = p_fileName;
            m_joinThr = p_joinThr;
            m_prefList = p_preferences;
            m_rule = p_rule;
            m_run = p_run;
            m_sim = p_sim;
            m_ndiss = p_ndiss;
            m_comsize = p_comsize;

        }

        // unregister an agent
        // @param p_agent agent object
        public final void unregister( final CVotingAgentCB p_agent )
        {
            m_send.unregister( p_agent );
        }


        /**
         * generator method to create agents successively
         * @param p_number number of agents
         * @param p_data any data which can be put from outside to the generator method
         * @return stream of voting agents
         */
        public final Stream<CVotingAgentCB> generatemultiplenew( final int p_number, final Object... p_data )
        {
            final ArrayList<CVotingAgentCB> l_list = new ArrayList();

            for ( int i = 0; i < p_number; i++ )
            {
                l_list.add( this.generatesingle( p_data ) );
            }

            return l_list.stream().filter( Objects::nonNull );
        }


        // generator method of the agent
        // @param p_data any data which can be put from outside to the generator method
        // @return returns an agent
        @Override
        public final CVotingAgentCB generatesingle( final Object... p_data )
        {
            // register a new agent object at the send action and the register
            // method retruns the object reference

            final CVotingAgentCB l_votingAgent = new CVotingAgentCB(

                // create a string with the agent name "agent <number>"
                // get the value of the counter first and increment, build the agent
                // name with message format (see Java documentation)
                MessageFormat.format( "agent {0}", m_agentcounter.incrementAndGet() ),

                // add the agent configuration
                m_configuration,
                // add the chair agent
                ( (CChairAgentCB.CChairAgentGenerator) p_data[0] ).generatesingle(),
                m_environment,
                m_altNum,
                m_joinThr,
                m_prefList.get( m_count ),
                m_rule,
                m_sim,
                m_run,
                m_ndiss,
                m_comsize );

            m_count++;
            l_votingAgent.sleep( Integer.MAX_VALUE  );
            m_environment.initialset( l_votingAgent );
            return m_send.register( l_votingAgent );


        }

        final CVotingAgentCB generatesinglenew()
        {
            System.out.println( "creating new voter, m_count is " + m_count );
            final AtomicDoubleArray l_preferences = m_prefList.get( m_count );
            System.out.println( "Preferences: " + l_preferences );

            final CVotingAgentCB l_votingAgent = new CVotingAgentCB(

                // create a string with the agent name "agent <number>"
                // get the value of the counter first and increment, build the agent
                // name with message format (see Java documentation)
                MessageFormat.format( "agent {0}", m_count ),

                // add the agent configuration
                m_configuration,
                m_environment,
                m_altNum,
                m_joinThr,
                m_prefList.get( m_count++ ),
                m_rule,
                m_sim,
                m_run,
                m_ndiss,
                m_comsize);

            return m_send.register( l_votingAgent );
        }
    }
}


