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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by sophie on 15.03.18.
 * https://www.mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/
 */
public enum EDataDB {
    INSTANCE;

    static Connection m_con;

    /**
     * connect to given database
     * @param p_dbName
     */
    static public void openCon( String p_dbName ) throws FileNotFoundException {
        try
        {
            Class.forName( "org.postgresql.Driver" );
        }
        catch ( ClassNotFoundException l_ex )
        {
            System.out.println( "PostgreSQL JDBC driver not found" );
            l_ex.printStackTrace();
            return;
        }

        try
        {
            File l_file = new File( "/home/sophie/Developer/LightVoting/src/main/java/org/lightvoting/simulation/statistics/postgres.txt");

            Scanner l_sc;
            l_sc = new Scanner( l_file );
            String l_url = l_sc.nextLine();
            String l_usr = l_sc.nextLine();
            String l_pw = l_sc.nextLine();

            l_sc.close();

            Properties l_props = new Properties();
            l_props.setProperty("ssl","true");


            m_con = DriverManager.getConnection(
                    l_url +
                            "/" + p_dbName +
                            "?user=" + l_usr +
                            "&password=" + l_pw,
                    l_props );

        }
        catch (SQLException l_ex )
        {
            System.out.println( "Connection failed" );
            l_ex.printStackTrace();
            return;
        }

    }

    public static void closeCon() throws SQLException {
        m_con.close();
        System.out.println( "Closed connection" );
    }
}
