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

package org.lightvoting.simulation.agent;
//
//
//import cern.colt.bitvector.BitVector;
//import com.google.common.util.concurrent.AtomicDoubleArray;
//import org.lightjason.agentspeak.action.binding.IAgentAction;
//import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
//import org.lightjason.agentspeak.action.binding.IAgentActionName;
//import org.lightjason.agentspeak.agent.IBaseAgent;
//import org.lightjason.agentspeak.common.CCommon;
//import org.lightjason.agentspeak.configuration.IAgentConfiguration;
//import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
//import org.lightjason.agentspeak.language.CLiteral;
//import org.lightjason.agentspeak.language.CRawTerm;
//import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
//import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
//import org.lightvoting.simulation.environment.randomIterative.CEnvironmentRI;
//import org.lightvoting.simulation.environment.randomBasic.CGroupRB;
//import org.lightvoting.simulation.rule.CMinisumApproval;
//
//import java.io.InputStream;
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//// import org.lightvoting.simulation.statistics.CDataWriter;
//
//// import org.lightjason.agentspeak.language.score.IAggregation;
//
//
///**
// * Created by sophie on 21.02.17.
// */
//
//// annotation to mark the class that actions are inside
//@IAgentAction
//public final class CChairAgent_old extends IBaseAgent<CChairAgent_old>
//{
//    /**
//     * serialUID
//     */
//    private static final long serialVersionUID = -4459675039048514445L;
//
//    /**
//     * name of chair
//     */
//    private final String m_name;
//
//    /**
//     * environment
//     */
//
//    private final CEnvironmentRI m_environment;
//
//    /**
//     * grouping algorithm: "RANDOM" or "COORDINATED"
//     */
//
//    private String m_grouping;
//
//    private List<BitVector> m_bitVotes = Collections.synchronizedList( new LinkedList<>() );
//    private List<Double> m_dissList = Collections.synchronizedList( new LinkedList<>() );
//    private List<CVotingAgentRB> m_dissVoters = Collections.synchronizedList( new LinkedList<>() );
//    private int m_iteration;
//    private List<CVotingAgentRB> m_agents = Collections.synchronizedList( new LinkedList<>() );
//    private boolean m_iterative;
//    private String m_protocol;
//    private double m_dissThreshold;
//    private final String m_fileName;
//    private final int m_run;
//    private String m_conf;
//    private boolean m_dissStored;
//
//    // counter for intermediate elections in coordinated grouping
//    private int m_coorNum;
//
//    private List<String> m_paths = new ArrayList();
//    private List<Object> m_data = new ArrayList();
//    private Map<String, Object> m_map = new HashMap<>();
//    private final int m_comsize;
//    private int m_altnum;
//    private int m_groupNum;
//
//    /**
//     * constructor of the agent
//     * @param p_configuration agent configuration of the agent generator
//     * @param p_fileName h5 file
//     * @param p_run run number
//     * @param p_dissthr dissatisfaction threshold
//     * @param p_comsize size of committee to be elected
//     */
//
//
//    public CChairAgent_old( final String p_name, final IAgentConfiguration<CChairAgent_old> p_configuration, final CEnvironmentRI p_environment,
//                            final String p_fileName,
//                            final int p_run,
//                            final double p_dissthr,
//                            final int p_comsize,
//                            final int p_altnum
//    )
//    {
//        super( p_configuration );
//        m_name = p_name;
//        m_environment = p_environment;
//        m_fileName = p_fileName;
//        m_run = p_run;
//        m_dissThreshold = p_dissthr;
//        m_comsize = p_comsize;
//        m_altnum = p_altnum;
//    }
//
//    // overload agent-cycle
//    @Override
//    public final CChairAgent_old call() throws Exception
//    {
//        // run default cycle
//        this.checkConditions();
//        return super.call();
//    }
//
//    // public methods
//
//    public String name()
//    {
//        return m_name;
//    }
//
//    /**
//     * set configuration
//     * @param p_conf config id
//     * @param p_grouping grouping method
//     * @param p_protocol protocol
//     */
//
//    public void setConf( final String p_conf, final String p_grouping, final String p_protocol )
//    {
//        m_conf = p_conf;
//        m_grouping = p_grouping;
//        m_protocol = p_protocol;
//    }
//
//    /**
//     * reset chair agent for next simulation run
//     */
//
//    public void reset()
//    {
//
//        m_bitVotes = Collections.synchronizedList( new LinkedList<>() );
//        m_dissList = Collections.synchronizedList( new LinkedList<>() );
//        m_dissVoters = Collections.synchronizedList( new LinkedList<>() );
//        m_agents = Collections.synchronizedList( new LinkedList<>() );
//        m_iteration = 0;
//        m_iterative = false;
//        m_dissStored = false;
//        m_groupNum = 0;
//
//        this.trigger( CTrigger.from(
//            ITrigger.EType.ADDGOAL,
//            CLiteral.from(
//                "main" )
//                      )
//        );
//    }
//
//    /**
//     * return path list
//     * @return m_paths
//     */
//    public List<String> pathList()
//    {
//        return m_paths;
//    }
//
//
//    /**
//     * return data list
//     * @return m_data
//     */
//    public List<Object> dataList()
//    {
//        return m_data;
//    }
//
//
//    // agent actions
//
//    /**
//     * perceive group
//     */
//    @IAgentActionFilter
//    @IAgentActionName( name = "perceive/group" )
//    /**
//     * add literal for group of chair agent if it exists
//     */
//    private void perceiveGroup()
//    {
//        if ( !( m_environment.detectGroup( this ).emptyValues() ) )
//            this.beliefbase().add( m_environment.detectGroup( this ) );
//    }
//
//    public Map<String, Object> map()
//    {
//        return m_map;
//    }
//
//    // private methods
//
//    private void checkConditions()
//    {
//      //  System.out.println( this.name() + " checking conditions " );
//        final CGroupRB l_group = this.determineGroup();
//
//        if ( l_group != null )
//
//        {
//            // if conditions for election are fulfilled, trigger goal start/criterion/fulfilled
//
//            final ITrigger l_trigger;
//
//            // if m_iterative is true, we have the case of iterative voting, i.e. we already have the votes
//            // we only need to repeat the computation of the result
//
//            if ( m_iterative && ( l_group.readyForElection() && !( l_group.electionInProgress() ) ) )
//            {
//                m_iteration++;
//                this.computeResult();
//                return;
//            }
//
//
//            if ( l_group.readyForElection() && ( !( l_group.electionInProgress() ) ) )
//            {
//                l_group.startProgress();
//
//                l_trigger = CTrigger.from(
//                    ITrigger.EType.ADDGOAL,
//                    CLiteral.from( "start/criterion/fulfilled" )
//                );
//
//                this.trigger( l_trigger );
//            }
//        }
//    }
//
//    private CGroupRB determineGroup()
//    {
//        final AtomicReference<CGroupRB> l_groupAtomic = new AtomicReference<>();
//        this.beliefbase().beliefbase().literal( "group" ).stream().forEach(
//            i -> l_groupAtomic.set( i .values().findFirst().get().raw() ) );
//        return l_groupAtomic.get();
//    }
//
//
//    /**
//     * start election
//     */
//    @IAgentActionFilter
//    @IAgentActionName( name = "start/election" )
//
//    public void startElection()
//    {
//        final CGroupRB l_group = this.determineGroup();
//        l_group.triggerAgents( this );
//    }
//
//    /**
//     * store vote
//     *
//     * @param p_vote vote
//     */
//    @IAgentActionFilter
//    @IAgentActionName( name = "store/vote" )
//    public void storeVote( final String p_agentName, final BitVector p_vote )
//    {
//        final CGroupRB l_group = this.determineGroup();
//
//        m_agents.add( l_group.determineAgent( p_agentName ) );
//        m_bitVotes.add( p_vote );
//
//        System.out.println( " --------------------- " + this.name() + " received vote from " + p_agentName );
//
//        if ( m_bitVotes.size() != l_group.size() )
//            return;
//
//        final ITrigger l_trigger = CTrigger.from(
//            ITrigger.EType.ADDGOAL,
//            CLiteral.from(
//                "all/votes/received" )
//
//        );
//
//        System.out.println( " xxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + this.name() + " all votes received " );
//
//        this.trigger( l_trigger );
//    }
//
//    /**
//     * compute result of election
//     */
//
//    @IAgentActionFilter
//    @IAgentActionName( name = "compute/result" )
//
//    public void computeResult()
//    {
//        final CGroupRB l_group = this.determineGroup();
//
//        final CMinisumApproval l_minisumApproval = new CMinisumApproval();
//
//        final List<String> l_alternatives = new LinkedList<>();
//
//     //   for ( char l_char : "ABCD".toCharArray() )
//
//        for ( int i = 0; i < m_altnum; i++ )
//            l_alternatives.add( "POI" + i );
//
//         //   l_alternatives.add( String.valueOf( l_char ) );
//
//        System.out.println( " Alternatives: " + l_alternatives );
//
//        System.out.println( " Votes: " + m_bitVotes );
//
//        final BitVector l_comResultBV = l_minisumApproval.applyRuleBV( l_alternatives, m_bitVotes, m_comsize );
//
//        System.out.println( " ------------------------ " + this.name() + " Result of election as BV: " + l_comResultBV );
//
//        // set inProgress and readyForElection to false in group
//        l_group.reset();
//
//        // write resulting committee for coordinated grouping
//        if ( "COORDINATED".equals( m_grouping ) )
//        {
//            final String l_slash = "/";
//
//            final String l_path = m_run + l_slash  + m_conf + l_slash + "group " + this.getGroupID() + l_slash + "im_" + m_coorNum + l_slash + "committee";
//
//            m_map.put( l_path, l_comResultBV );
//
//            // old code
//            // EDataWriter.INSTANCE.writeCommitteeCoordinated( m_run, m_conf, this, l_comResultBV, m_coorNum );
//            // new CDataWriter().writeCommitteeCoordinated( m_fileName, m_run, m_conf, this, l_comResultBV, m_coorNum );
//            m_coorNum++;
//        }
//        if ( "BASIC".equals( m_protocol ) )
//        {
//            this.beliefbase().add( l_group.updateBasic( this, l_comResultBV ) );
//
//            if ( "RANDOM".equals( m_grouping ) )
//            {
//                // ask all agents in group to submit their dissatisfaction value
//                System.out.println( "Ask agents to submit final diss " );
//                this.beliefbase().add( l_group.submitDiss( this, l_comResultBV, m_iteration ) );
//            }
//
//            if ( "COORDINATED".equals( m_grouping ) && l_group.finale() )
//            {
//                System.out.println( "Ask agents to submit final diss " );
//                this.beliefbase().add( l_group.submitDiss( this, l_comResultBV, m_iteration ) );
//            }
//        }
//
//        // if grouping is coordinated, reopen group for further voters
//        if ( "COORDINATED".equals( m_grouping ) && !l_group.finale() && !m_iterative )
//        {
//            System.out.println( " reopening group " );
//            m_environment.reopen( l_group );
//        }
//
//        // for the iterative case, you need to differentiate between the final election and intermediate elections.
//        if ( "ITERATIVE".equals( m_protocol ) && ( l_group.finale() ) || m_iterative )
//        {
//            System.out.println( " Update iterative " );
//
//            this.beliefbase().add( l_group.updateIterative( this,  l_comResultBV, m_iteration ) );
//            return;
//        }
//
//        if ( "ITERATIVE".equals( m_protocol ) && !l_group.finale() )
//        {
//            System.out.println( " Update basic " );
//            this.beliefbase().add( l_group.updateBasic( this,  l_comResultBV ) );
//        }
//
//        m_dissStored = false;
//
//        // TODO test all cases
//    }
//
//    /**
//     * store dissatisfaction value
//     *
//     * @param p_diss dissatisfaction value
//     * @param p_iteration iteration number
//     */
//    @IAgentActionFilter
//    @IAgentActionName( name = "store/diss" )
//
//    public void storeDiss( final String p_name, final Double p_diss, final Integer p_iteration )
//    {
//        final CGroupRB l_group = this.determineGroup();
//
//        m_dissList.add( p_diss );
//        final CVotingAgentRB l_dissAg = l_group.determineAgent( p_name );
//        m_dissVoters.add( l_dissAg );
//
//
//        System.out.println( "Storing diss " + p_diss );
//
//        if ( m_dissList.size() == l_group.size() )
//            // && ( !m_dissStored ) )
//        {
//            //     m_dissStored = true;
//            System.out.println( this.name() + " Size of group " + m_dissVoters.size() );
//
//            final ITrigger l_trigger = CTrigger.from(
//                ITrigger.EType.ADDGOAL,
//                CLiteral.from(
//                    "all/dissValues/received",
//                    CRawTerm.from( p_iteration )
//                )
//
//            );
//
//            this.trigger( l_trigger );
//
//            System.out.println( p_iteration + " All " + m_dissList.size() + " voters submitted their dissatisfaction value" );
//            System.out.println( Arrays.toString( m_dissList.toArray() ) );
//
//            final AtomicDoubleArray l_dissVals = new AtomicDoubleArray( new double[m_dissList.size()] );
//            for ( int i = 0; i < m_dissList.size(); i++ )
//                l_dissVals.set( i, m_dissList.get( i ) );
//
//            // final String l_config = "RANDOM_BASIC";
//
//            // TODO refactor string building
//            final String l_slash = "/";
//
//            final String l_path = m_run + l_slash + m_conf + l_slash + "group " + this.getGroupID() + l_slash + p_iteration + l_slash + "dissVals";
//
//            m_map.put( l_path, l_dissVals );
//
//            // TODO write data to list instead
//        //    EDataWriter.INSTANCE.writeDataVector( m_run, m_conf, this, p_iteration, l_dissVals );
//        //    new CDataWriter().writeDataVector( m_fileName, m_run, m_conf, this, p_iteration, l_dissVals );
//        }
//    }
//
//    /**
//     * store final dissatisfaction value
//     *
//     * @param p_diss dissatisfaction value
//     */
//    @IAgentActionFilter
//    @IAgentActionName( name = "store/final/diss" )
//
//    public void storeFinalDiss( final String p_name, final Double p_diss, final Integer p_iteration )
//    {
//        final CGroupRB l_group = this.determineGroup();
//
//        m_dissList.add( p_diss );
//        final CVotingAgentRB l_dissAg = l_group.determineAgent( p_name );
//        m_dissVoters.add( l_dissAg );
//
//        System.out.println( "Storing diss " + p_diss );
//
//        if ( ( m_dissList.size() == l_group.size() ) && ( !m_dissStored ) )
//        {
//            m_dissStored = true;
//            System.out.println( p_iteration + " All " + m_dissList.size() + " voters submitted their dissatisfaction value" );
//            System.out.println( Arrays.toString( m_dissList.toArray() ) );
//
//            final AtomicDoubleArray l_dissVals = new AtomicDoubleArray( new double[m_dissList.size()] );
//            for ( int i = 0; i < m_dissList.size(); i++ )
//                l_dissVals.set( i, m_dissList.get( i ) );
//
//            final String l_slash = "/";
//
//            final String l_groupStr = "group ";
//
//            final String l_path = m_run + l_slash + m_conf + l_slash + l_groupStr + this.getGroupID() + l_slash + p_iteration + l_slash + "dissVals";
//
//            m_map.put( l_path, l_dissVals );
//
//            final String l_pathIt = m_run + l_slash + m_conf + l_slash + l_groupStr + this.getGroupID() +  l_slash + "lastIt";
//
//            m_map.put( l_pathIt, p_iteration );
//
//            m_environment.incrementGroupCount( m_run, m_conf );
//
//            // TODO write data to list instead
//        //    EDataWriter.INSTANCE.writeDataVector( m_run, m_conf, this, p_iteration, l_dissVals );
//        //    EDataWriter.INSTANCE.writeLastIteration( m_run, m_conf, this, p_iteration );
//        //    new CDataWriter().writeDataVector( m_fileName, m_run, m_conf, this, p_iteration, l_dissVals );
//        //    new CDataWriter().writeLastIteration( m_fileName, m_run, m_conf, this, p_iteration );
//        }
//
//    }
//
//    /**
//     * remove most dissatisfied voter
//     */
//    @IAgentActionFilter
//    @IAgentActionName( name = "remove/voter" )
//    public void removeVoter( final Integer p_iteration )
//    {
//        System.out.println( "removing voter " );
//        final CGroupRB l_group = this.determineGroup();
//
//        final int l_maxIndex = this.getMaxIndex( m_dissList );
//        final double l_max = m_dissList.get( l_maxIndex );
//        System.out.println( " max diss is " + l_max );
//
//        if ( l_max <= m_dissThreshold )
//        {
//            System.out.println( " No dissatisfied voter left, we are done " );
//
//            final String l_slash = "/";
//
//            final String l_groupStr = "group ";
//
//            final String l_path = m_run + l_slash + m_conf + l_slash + l_groupStr + this.getGroupID() +  l_slash + "lastIt";
//
//            m_map.put( l_path, p_iteration );
//
//            m_environment.incrementGroupCount( m_run, m_conf );
//
//
//           // EDataWriter.INSTANCE.writeLastIteration( m_run, m_conf, this, p_iteration );
//            //    new CDataWriter().writeLastIteration( m_fileName, m_run, m_conf, this, p_iteration );
//            return;
//        }
//        System.out.println( " Determining most dissatisfied voter " );
//        final CVotingAgentRB l_maxDissAg = m_dissVoters.get( l_maxIndex );
//        System.out.println( " Most dissatisfied voter is " + l_maxDissAg.name() );
//        // remove vote of most dissatisfied voter from list
//        m_bitVotes.remove( l_maxDissAg.getBitVote() );
//        l_group.remove( l_maxDissAg );
//
//        System.out.println( "Removing " + l_maxDissAg.name() );
//        // System.out.println( this.name() + ":Size of List after removing " + m_dissVoters.size() );
//        System.out.println( this.name() + ":Size of Group after removing " + l_group.size() );
//
//        if ( l_group.size() == 0 )
//        {
//            System.out.println( " Voter list is empty, we are done " );
//            // TODO write data to list instead
//
//            final String l_slash = "/";
//
//            final String l_groupStr = "group ";
//
//            final String l_path = m_run + l_slash + m_conf + l_slash + l_groupStr + this.getGroupID() +  l_slash + "lastIt";
//
//            m_map.put( l_path, p_iteration );
//
//            m_environment.incrementGroupCount( m_run, m_conf );
//        }
//
//        // remove diss Values for next iteration
//        m_dissVoters.clear();
//        m_dissList.clear();
//
//        m_iterative = true;
//        l_group.makeReady();
//    }
//
//    private int getMaxIndex( final List<Double> p_dissValues )
//    {
//        int l_maxIndex = 0;
//        for ( int i = 0; i < p_dissValues.size(); i++ )
//        {
//            if ( p_dissValues.get( i ) > p_dissValues.get( l_maxIndex ) )
//            {
//                System.out.println( " changed max index to " + i + " diss: " + p_dissValues.get( i ) );
//                l_maxIndex = i;
//            }
//        }
//        return l_maxIndex;
//    }
//
//    public int getGroupID()
//    {
//        return this.determineGroup().getID();
//    }
//
//
//    /**
//     * Class CChairAgentGenerator
//     */
//
//    public static final class CChairAgentGenerator extends IBaseAgentGenerator<CChairAgent_old>
//    {
//
//        /**
//         * environment
//         */
//        private final CEnvironmentRI m_environment;
//
//        /**
//         * Current free agent id, needs to be thread-safe, therefore using AtomicLong.
//         */
//        private final AtomicLong m_agentcounter = new AtomicLong();
//
//        private final String m_fileName;
//        private final int m_run;
//        private double m_dissthr;
//        private int m_comsize;
//        private int m_altnum;
//
//        /**
//         * constructor of the generator
//         * @param p_stream ASL code as any stream e.g. FileInputStream
//         * @param p_fileName h5 file
//         * @param p_run run number
//         * @param p_dissthr dissatisfaction threshold
//         * @param p_comsize size of committee to be elected
//         * @throws Exception Thrown if something goes wrong while generating agents.
//         */
//        public CChairAgentGenerator( final InputStream p_stream, final CEnvironmentRI p_environment,
//                                     final String p_fileName,
//                                     final int p_run,
//                                     final double p_dissthr, final int p_comsize, final int p_altnum
//        ) throws Exception
//        {
//            super(
//                // input ASL stream
//                p_stream,
//
//                // a set with all possible actions for the agent
//                Stream.concat(
//                    // we use all build-in actions of LightJason
//                    CCommon.actionsFromPackage(),
//                    Stream.concat(
//                        // use the actions which are defined inside the agent class
//                        CCommon.actionsFromAgentClass( CChairAgent_old.class ),
//                        // add VotingAgent related external actions
//                        Stream.of(
//
//                        )
//                    )
//                    // build the set with a collector
//                ).collect( Collectors.toSet() )
//                //,
//
//                // aggregation function for the optimisation function, here
//                // we use an empty function
//         //       IAggregation.EMPTY
//            );
//            m_environment = p_environment;
//            m_fileName = p_fileName;
//            m_run = p_run;
//            m_dissthr = p_dissthr;
//            m_comsize = p_comsize;
//            m_altnum = p_altnum;
//        }
//
//        /**
//         * generator method of the agent
//         * @param p_data any data which can be put from outside to the generator method
//         * @return returns an agent
//         */
//
//        @Override
//        public final CChairAgent_old generatesingle( final Object... p_data )
//        {
//
//            final CChairAgent_old l_chairAgent = new CChairAgent_old(
//                // create a string with the agent name "chair <number>"
//                // get the value of the counter first and increment, build the agent
//                // name with message format (see Java documentation)
//                MessageFormat.format( "chair {0}", m_agentcounter.getAndIncrement() ), m_configuration, m_environment, m_fileName, m_run, m_dissthr, m_comsize, m_altnum );
//            l_chairAgent.sleep( Integer.MAX_VALUE );
//            System.out.println( "Creating chair " + l_chairAgent.name() );
//            return l_chairAgent;
//        }
//
//    }
//}
