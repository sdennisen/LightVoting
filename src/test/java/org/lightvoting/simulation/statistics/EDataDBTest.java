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

import junit.framework.TestCase;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EDataDBTest extends TestCase
{

    public void testAddConfig() throws SQLException
    {
        try
        {
            EDataDB.INSTANCE.openCon("playground");
        } catch (FileNotFoundException l_ex)
        {
            l_ex.printStackTrace();
        }

        Statement l_stmt = EDataDB.INSTANCE.getCon().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY );

        int l_oldMax = this.getMaxID( l_stmt );

        int l_newMax = EDataDB.INSTANCE.addConfig(10, 10, 10, 10,
                10, "MINISUM_APPROVAL", "RANDOM_BASIC",
                (float) 3.5, (float) 3.5, "uniform", "testname" );

        EDataDB.INSTANCE.closeCon();

        assertTrue( l_newMax > l_oldMax );

    }


    public void testAddSim() throws SQLException
    {
        try
        {
            EDataDB.INSTANCE.openCon("playground");
        } catch (FileNotFoundException l_ex)
        {
            l_ex.printStackTrace();
        }

        Statement l_stmt = EDataDB.INSTANCE.getCon().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY );

        int l_oldSim = this.getMaxNumber( l_stmt );

        int l_newSim = EDataDB.INSTANCE.addSim(1);

        EDataDB.INSTANCE.closeCon();

        assertTrue( l_newSim > l_oldSim );

    }

    public void testAddRun() throws SQLException
    {
        try
        {
            EDataDB.INSTANCE.openCon( "playground" );
        } catch (FileNotFoundException l_ex)
        {
            l_ex.printStackTrace();
        }

        Statement l_stmt = EDataDB.INSTANCE.getCon().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY );

        EDataDB.INSTANCE.addRun( 3, 1 );

        ResultSet l_rs = l_stmt.executeQuery( "SELECT * from run WHERE number = 1 AND simulation = 3" );

        l_rs.next();
        assertEquals( 3, l_rs.getInt( "simulation" ) );
        assertEquals( 1, l_rs.getInt( "number" ) );

        l_stmt.execute( "DELETE from run WHERE number = 1 AND simulation = 3" );

        EDataDB.INSTANCE.closeCon();

    }

    public void testVoter() throws SQLException
    {
        try
        {
            EDataDB.INSTANCE.openCon( "playground" );
        } catch (FileNotFoundException l_ex)
        {
            l_ex.printStackTrace();
        }

        Statement l_stmt = EDataDB.INSTANCE.getCon().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY );

        EDataDB.INSTANCE.addVoter( "agent 1", 1, 7 );

        ResultSet l_rs = l_stmt.executeQuery( "SELECT * from voter WHERE id = 'agent 1' AND run = 1 AND simulation = 7" );

        l_rs.next();
        assertEquals( "agent 1", l_rs.getString( "id" ) );
        assertEquals( 7, l_rs.getInt( "simulation" ) );
        assertEquals( 1, l_rs.getInt( "run" ) );

        l_stmt.execute( "DELETE from voter * WHERE id = 'agent 1' AND run = 1 AND simulation = 7" );
        EDataDB.INSTANCE.closeCon();

    }

    public void testSetTime() throws SQLException {
        try
        {
            EDataDB.INSTANCE.openCon( "playground" );
        } catch (FileNotFoundException l_ex)
        {
            l_ex.printStackTrace();
        }

        Statement l_stmt = EDataDB.INSTANCE.getCon().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY );

        EDataDB.INSTANCE.setTime( 10, "agent 1", 2, 3 );

        ResultSet l_rs = l_stmt.executeQuery( "SELECT * from voter WHERE id = 'agent 1' AND run = 2 AND simulation = 3" );

        l_rs.next();
        assertEquals( 10, l_rs.getInt( "time" ) );
        EDataDB.INSTANCE.closeCon();

    }

    public void testAddGroup() throws SQLException
    {
        try
        {
            EDataDB.INSTANCE.openCon( "playground" );
        } catch (FileNotFoundException l_ex)
        {
            l_ex.printStackTrace();
        }

        Statement l_stmt = EDataDB.INSTANCE.getCon().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY );

        int l_groupID = this.getLastGroup( l_stmt );

        int l_newgroupID = EDataDB.INSTANCE.addGroup( "chair 1", "agent 1", 2, 3);

        assertTrue( l_newgroupID > l_groupID );

        EDataDB.INSTANCE.closeCon();
    }

    public void testExtendGroup() throws SQLException
    {
        try
        {
            EDataDB.INSTANCE.openCon( "playground" );
        } catch (FileNotFoundException l_ex)
        {
            l_ex.printStackTrace();
        }

        List<String> l_ags = new ArrayList<>();
        l_ags.add( "agent 1" );
        l_ags.add( "agent 2" );

        int l_newgroupID = EDataDB.INSTANCE.newGroup( "chair 1", 7, l_ags, 2, 3 );

        Statement l_stmt = EDataDB.INSTANCE.getCon().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY );

        int l_pred = this.getPred( l_stmt, l_newgroupID );
        assertEquals( l_pred, 7 );

        EDataDB.INSTANCE.closeCon();
    }

    public void testAddResult() throws SQLException
    {
        try
        {
            EDataDB.INSTANCE.openCon( "playground" );
        } catch (FileNotFoundException l_ex)
        {
            l_ex.printStackTrace();
        }



        HashMap<String, Float> l_voterDiss = new HashMap<>();

        l_voterDiss.put( "agent 1", (float) 3.2 );

        Statement l_stmt = EDataDB.INSTANCE.getCon().createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY );

        EDataDB.INSTANCE.addResult( 7, "{0,1,0}", "BASIC",
       true, 0, -1, l_voterDiss, 2, 3 );

        ResultSet l_rs = l_stmt.executeQuery( "SELECT * from election_result WHERE group_column = 7" );
        l_rs.first();

        assertEquals( l_rs.getInt( "group_column"), 7 );

        l_stmt.execute( "DELETE from election_result * WHERE group_column = 7" );
        l_stmt.execute( "DELETE from elects * WHERE electionresult = 7" );

        EDataDB.INSTANCE.closeCon();
    }

    private int getPred(Statement p_stmt, int p_id ) throws SQLException
    {
        ResultSet l_rs = p_stmt.executeQuery( "SELECT * from group_table WHERE id = " + p_id );
        l_rs.first();
        return l_rs.getInt( "predecessor" );
    }

    private int getLastGroup(Statement p_stmt) throws SQLException
    {
        ResultSet l_rs = p_stmt.executeQuery( "SELECT * from group_table WHERE id = (SELECT MAX(ID) FROM group_table)" );
        l_rs.first();
        return l_rs.getInt( "id" );
    }

    private int getMaxID(Statement p_stmt) throws SQLException
    {
        ResultSet l_rs = p_stmt.executeQuery( "SELECT * from configuration WHERE id = (SELECT MAX(ID) FROM configuration)" );
        l_rs.first();
        return l_rs.getInt( "id" );
    }

    private int getMaxNumber(Statement p_stmt) throws SQLException
    {
        ResultSet l_rs = p_stmt.executeQuery( "SELECT * from simulation WHERE number = (SELECT MAX(number) FROM simulation)" );
        l_rs.first();
        return l_rs.getInt( "number" );
    }

}
