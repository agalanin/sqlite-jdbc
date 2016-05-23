package org.sqlite;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * Created by Alexander Galanin <al@galanin.nnov.ru>
 */

public class DateTimeTest
{
    // Mon May 23 06:06:21 MSK 2016 = Mon May 23 03:06:21 GMT 2016
    private static final long DATETIME_UNIX = 1463972781L;
    private static final Date DATETIME = new Date(DATETIME_UNIX * 1000);

    private Connection conn;
    private PreparedStatement stat;

    @After
    public void close() throws SQLException {
        stat.close();
        conn.close();
    }

    @Test
    public void setDateUnix() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();

        config.setReadOnly(true);
        config.setDateClass("INTEGER");
        config.setDatePrecision("SECONDS");

        conn = DriverManager.getConnection("jdbc:sqlite:", config.toProperties());
        stat = conn.prepareStatement("select strftime('%s', ?, 'unixepoch')");
        stat.setDate(1, DATETIME);

        ResultSet rs = stat.executeQuery();
        assertTrue(rs.next());
        assertEquals(rs.getLong(1), DATETIME_UNIX);
        rs.close();
    }

    @Test
    public void setDateJulian() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();

        config.setReadOnly(true);
        config.setDateClass("REAL");

        conn = DriverManager.getConnection("jdbc:sqlite:", config.toProperties());
        stat = conn.prepareStatement("select strftime('%s', ?)");
        stat.setDate(1, DATETIME);

        ResultSet rs = stat.executeQuery();
        assertTrue(rs.next());
        assertEquals(rs.getLong(1), DATETIME_UNIX);
        rs.close();
    }

    /**
     * Driver MUST format date/time in UTC because SQLite's internal date/time format is UTC.
     *
     * To test this in UTC timezone use java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("GMT+3"));
     */
    @Test
    public void setDateText() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();

        config.setReadOnly(true);
        config.setDateClass("TEXT");

        conn = DriverManager.getConnection("jdbc:sqlite:", config.toProperties());
        stat = conn.prepareStatement("select strftime('%s', ?)");
        stat.setDate(1, DATETIME);

        ResultSet rs = stat.executeQuery();
        assertTrue(rs.next());
        assertEquals(rs.getLong(1), DATETIME_UNIX);
        rs.close();
    }

}
