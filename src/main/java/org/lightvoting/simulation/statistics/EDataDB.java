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
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by sophie on 15.03.18.
 * https://www.mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/
 */
public enum EDataDB {
    INSTANCE;

    static Connection s_con;
    static PreparedStatement s_stmt_conf;
    static PreparedStatement s_stmt_sim;

    /**
     * connect to given database
     * @param p_dbName
     */
    static public void openCon( String p_dbName ) throws FileNotFoundException, SQLException {

        if ( ( s_con == null ) || ( s_con.isClosed() ) ) {
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

            } catch (SQLException l_ex)
            {
                System.out.println("Connection failed");
                l_ex.printStackTrace();
                return;
            }

            if ( ( s_stmt_sim == null ) || ( s_stmt_sim.isClosed() ) )
                s_stmt_sim = s_con.prepareStatement("INSERT into simulation (configuration) VALUES (?) RETURNING number");

            if ( ( s_stmt_conf == null ) || ( s_stmt_conf.isClosed() ) )
                s_stmt_conf = s_con.prepareStatement("INSERT into configuration " +
                        "(runs, agnum, altnum, comsize, capacity, rule, setting, " +
                        "jointhr, dissthr, prefs) VALUES ( ?, ?, ?, ?, ?, CAST (? AS rule), ?, ?, ?, CAST (? AS preftype)) RETURNING id");
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

    // TODO write method and JUnit Test

    /**
     * add new run entity to database
     * @param p_simID
     * @return run number
     */
    public int addRun( int p_simID )
    {

        return -1;
    }

    // TODO write method and JUnit Test

    /**
     * add new voter entity to database
     * @param p_voterID name of voter
     * @param p_simID simulation number
     * @param p_runID run number
     */
    public void addVoter( String p_voterID, int p_simID, int p_runID )
    {

    }

    // TODO write method and JUnit Test

    /**
     * set time for specified voter entity in database
     * @param p_id name of voter
     */
    public void setTime( String p_id )
    {

    }

    // TODO write method and JUnit Test

    /**
     * add Group
     * @param p_chairID name of chair
     * @return group id
     */
    public int addGroup( String p_chairID )
    {

        return -1;
    }

    // TODO write method and JUnit Test

    /**
     * extend Group
     * @param p_chairID name of chair
     * @param p_pred predecessor id
     * @return group id
     */
    public int extendGroup( String p_chairID, int p_pred )
    {

        return -1;
    }

    // TODO write method and JUnit Test

    /**
     * add new election result entity to database
     * @param p_groupID group id
     * @param p_com committee
     * @param p_type type of election
     * @param p_lastElection specifies if election was last election
     * @param p_itNum id of iteration if applicable
     * @param p_imNum id of intermediate election if applicable
     * @return id of election result
     */
    public int addResult( int p_groupID, String p_com, String p_type,
                          boolean p_lastElection, int p_itNum, int p_imNum )
    {

        return -1;
    }


    public static void closeCon() throws SQLException
    {
        s_stmt_conf.close();
        s_stmt_sim.close();
        s_con.close();
        System.out.println( "Closed connection" );
    }

}
