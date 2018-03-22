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

package org.lightvoting.simulation.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

/**
 * Created by sophie on 15.03.18.
 * https://www.mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/
 */
public enum EDataDB {
    INSTANCE;

    static Connection s_con;
    static PreparedStatement s_stmt_conf;
    static PreparedStatement s_stmt_sim;
    static PreparedStatement s_stmt_run;
    static PreparedStatement s_stmt_voter;
    static PreparedStatement s_stmt_setTime;
    static PreparedStatement s_stmt_group;
    static PreparedStatement s_stmt_addVoterToGroup;
    static PreparedStatement s_stmt_newGroup;
    static PreparedStatement s_stmt_Result;
    static PreparedStatement s_stmt_addVoterToResult;

    /**
     * connect to given database
     * @param p_dbName
     */
    static public void openCon( String p_dbName ) throws FileNotFoundException, SQLException {

        if ((s_con == null) || (s_con.isClosed())) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException l_ex) {
                System.out.println("PostgreSQL JDBC driver not found");
                l_ex.printStackTrace();
                return;
            }

            try {
                File l_file = new File("/home/sophie/Developer/LightVoting/src/main/java/org/lightvoting/simulation/statistics/postgres.txt");

                Scanner l_sc;
                l_sc = new Scanner(l_file);
                String l_url = l_sc.nextLine();
                String l_usr = l_sc.nextLine();
                String l_pw = l_sc.nextLine();

                l_sc.close();

                Properties l_props = new Properties();
                l_props.setProperty("ssl", "true");


                s_con = DriverManager.getConnection(
                        l_url +
                                "/" + p_dbName +
                                "?user=" + l_usr +
                                "&password=" + l_pw,
                        l_props);

            } catch (SQLException l_ex) {
                System.out.println("Connection failed");
                l_ex.printStackTrace();
                return;
            }

            if ((s_stmt_sim == null) || (s_stmt_sim.isClosed()))
                s_stmt_sim = s_con.prepareStatement("INSERT INTO simulation (configuration) VALUES (?) RETURNING number");
            if ((s_stmt_conf == null) || (s_stmt_conf.isClosed()))
                s_stmt_conf = s_con.prepareStatement("INSERT INTO configuration " +
                        "(runs, agnum, altnum, comsize, capacity, rule, setting, " +
                        "jointhr, dissthr, prefs) VALUES ( ?, ?, ?, ?, ?, CAST (? AS RULE), CAST (? AS SETTING), ?, ?, CAST (? AS PREFTYPE)) RETURNING id");
            if ((s_stmt_run == null) || (s_stmt_run.isClosed()))
                s_stmt_run = s_con.prepareStatement("INSERT INTO run (simulation, number) VALUES (?, ?) RETURNING number");
            if ((s_stmt_voter == null) || (s_stmt_voter.isClosed()))
                s_stmt_voter = s_con.prepareStatement("INSERT INTO voter (id, run, simulation) VALUES (?, ?, ?)");
            if ((s_stmt_setTime == null) || (s_stmt_setTime.isClosed()))
                s_stmt_setTime = s_con.prepareStatement("UPDATE voter SET time=? WHERE id = ? AND run = ? and simulation = ?");
            // needed for creating initial groups
            if ((s_stmt_group == null) || (s_stmt_group.isClosed()))
                s_stmt_group = s_con.prepareStatement("INSERT into group_table (chair) VALUES (?) RETURNING id");
            // needed for creating new groups
            if ((s_stmt_newGroup == null) || (s_stmt_newGroup.isClosed()))
                s_stmt_newGroup = s_con.prepareStatement("INSERT into group_table (chair, predecessor) VALUES (?,?) RETURNING id");
            // needed for creating initial groups as well as for subsequent groups
            if ((s_stmt_addVoterToGroup == null) || (s_stmt_addVoterToGroup.isClosed()))
                s_stmt_addVoterToGroup = s_con.prepareStatement("INSERT into voter_group (voter, run, simulation, group_column) VALUES (?,?,?,?)");

            if ((s_stmt_Result == null) || (s_stmt_Result.isClosed()))
                s_stmt_Result = s_con.prepareStatement("INSERT into election_result ( group_column, committee, type, itNum, imNum, lastElection ) VALUES (?,CAST (? as SMALLINT[]),CAST (? as electiontype),?,?,?)");
            if ((s_stmt_addVoterToResult == null) || (s_stmt_addVoterToResult.isClosed()))
                s_stmt_addVoterToResult = s_con.prepareStatement("INSERT into elects ( voter, electionresult, diss, simulation, run ) VALUES (?,?,?,?,?)");

        }
    }

    public static Connection getCon()
    {
        return s_con;
    }

    // TODO if necessary, adapt method to include commit id

    /**
     * add new simulation entry to database
     * number and startdate are set only in postgres
     * @param p_configID config id
     * @return simulation number
     */
    public static int addSim(int p_configID) throws SQLException
    {
        s_stmt_sim.setInt(1, p_configID);

        try( final ResultSet l_rs = s_stmt_sim.executeQuery(); )
        {
            l_rs.next();

            return l_rs.getInt("number");
        }
    }


    /**
     * add new configuration entity to database
     * @param p_runs number of runs
     * @param p_agnum number of agents
     * @param p_altnum number of alternatives/candidates
     * @param p_comsize size of committee to be elected
     * @param p_capacity maximal group size
     * @param p_rule voting rule
     * @param p_setting grouping and voting protocol
     * @param p_jointhr join threshold for coordinated grouping
     * @param p_dissthr dissatisfaction threshold for iterative voting
     * @param p_prefs used preference type
     * @return configuration id
     */
    public static int addConfig( int p_runs, int p_agnum, int p_altnum, int p_comsize,
                                 int p_capacity, String p_rule, String p_setting,
                                 float p_jointhr, float p_dissthr, String p_prefs ) throws SQLException {

        s_stmt_conf.setInt(1, p_runs);
        s_stmt_conf.setInt(2, p_agnum);
        s_stmt_conf.setInt(3, p_altnum);
        s_stmt_conf.setInt(4, p_comsize);
        s_stmt_conf.setInt(5, p_capacity);
        s_stmt_conf.setString(6, p_rule);
        s_stmt_conf.setString(7, p_setting);
        s_stmt_conf.setFloat(8, p_jointhr);
        s_stmt_conf.setFloat(9, p_dissthr);
        s_stmt_conf.setString(10, p_prefs);

        try (final ResultSet l_rs = s_stmt_conf.executeQuery();) {
            l_rs.next();

            return l_rs.getInt("id");
        }
    }

    /**
     * add new run entity to database
     * @param p_simID
     * @param p_number
     */
    public void addRun( int p_simID, int p_number ) throws SQLException
    {
        s_stmt_run.setInt( 1, p_simID );
        s_stmt_run.setInt( 2, p_number );
        s_stmt_run.execute();
    }

    /**
     * add new voter entity to database
     * @param p_voterID name of voter
     * @param p_runID run number
     * @param p_simID simulation number
     */
    public void addVoter( String p_voterID, int p_runID, int p_simID ) throws SQLException
    {
        s_stmt_voter.setString( 1, p_voterID );
        s_stmt_voter.setInt( 2, p_runID );
        s_stmt_voter.setInt( 3, p_simID );
        s_stmt_voter.execute();
    }

    /**
     * set time for specified voter entit in database
     * @param p_time time
     * @param p_voterID name of voter
     * @param p_runID run number
     * @param p_simID simulation number
     * @throws SQLException
     */
    public void setTime( int p_time, String p_voterID, int p_runID, int p_simID ) throws SQLException
    {
        s_stmt_setTime.setInt( 1, p_time );
        s_stmt_setTime.setString( 2, p_voterID );
        s_stmt_setTime.setInt( 3, p_runID );
        s_stmt_setTime.setInt( 4, p_simID );
        s_stmt_setTime.execute();
    }

    /**
     * add Group
     * @param p_chairID name of chair
     * @param p_voterID name of voter creating the group
     * @return group id
     */
    public int addGroup( String p_chairID, String p_voterID, int p_run, int p_sim ) throws SQLException {

        s_stmt_group.setString( 1, p_chairID );
        int l_groupID;

        try( final ResultSet l_rs = s_stmt_group.executeQuery(); )
        {
            l_rs.next();

            l_groupID = l_rs.getInt("id");
        }
        // add entry to table voter_group
        s_stmt_addVoterToGroup.setString(1, p_voterID );
        s_stmt_addVoterToGroup.setInt( 2, p_run );
        s_stmt_addVoterToGroup.setInt( 3, p_sim );
        s_stmt_addVoterToGroup.setInt( 4, l_groupID );
        s_stmt_addVoterToGroup.execute();

        return l_groupID;

    }

    /**
     * extend Group
     * @param p_chairID name of chair
     * @param p_pred predecessor id
     * @param p_voters name of agents
     * @param p_run run number
     * @param p_sim simulation number
     * @return group id
     */
    public int newGroup(String p_chairID, int p_pred, List<String> p_voters, int p_run, int p_sim ) throws SQLException
    {
        s_stmt_newGroup.setString( 1, p_chairID );
        s_stmt_newGroup.setInt( 2, p_pred );
        int l_groupID;

        try( final ResultSet l_rs = s_stmt_newGroup.executeQuery(); )
        {
            l_rs.next();

            l_groupID = l_rs.getInt("id");
        }

        System.out.println( "id for new group: " + l_groupID );

        // add specified voters to group

        for ( int i = 0; i < p_voters.size(); i++ ) {

            // add entry to table voter_group for current voter
            s_stmt_addVoterToGroup.setString(1, p_voters.get( i ));
            s_stmt_addVoterToGroup.setInt(2, p_run);
            s_stmt_addVoterToGroup.setInt(3, p_sim);
            s_stmt_addVoterToGroup.setInt(4, l_groupID);
            s_stmt_addVoterToGroup.execute();
        }

        return l_groupID;
    }

    /**
     * add new election result entity to database
     * @param p_groupID group id
     * @param p_com committee
     * @param p_type type of election
     * @param p_lastElection specifies if election was last election
     * @param p_itNum id of iteration if applicable
     * @param p_imNum id of intermediate election if applicable
     */
    public void addResult(int p_groupID, String p_com, String p_type,
                          boolean p_lastElection, int p_itNum, int p_imNum, HashMap<String, Float> p_voterDiss, int p_run, int p_sim ) throws SQLException
    {
        s_stmt_Result.setInt( 1, p_groupID );
        s_stmt_Result.setString( 2, p_com );
        s_stmt_Result.setString( 3, p_type );
        s_stmt_Result.setInt( 4, p_itNum );
        s_stmt_Result.setInt( 5, p_imNum );
        s_stmt_Result.setBoolean( 6, p_lastElection );

        s_stmt_Result.execute();

        // add entries to elects table

        for ( String l_key: p_voterDiss.keySet() )
        {
            s_stmt_addVoterToResult.setString( 1, l_key );
            s_stmt_addVoterToResult.setInt( 2, p_groupID );
            s_stmt_addVoterToResult.setFloat( 3, p_voterDiss.get( l_key) );
            s_stmt_addVoterToResult.setInt( 4, p_sim );
            s_stmt_addVoterToResult.setInt( 5, p_run );
            s_stmt_addVoterToResult.execute();
        }
    }



    public static void closeCon() throws SQLException
    {
        s_stmt_conf.close();
        s_stmt_sim.close();
        s_stmt_run.close();
        s_stmt_voter.close();
        s_stmt_setTime.close();
        s_stmt_group.close();
        s_stmt_addVoterToGroup.close();
        s_stmt_newGroup.close();
        s_stmt_Result.close();
        s_con.close();
        System.out.println( "Closed connection" );
    }

}
