package com.hzuhelper.Control.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hzuhelper.Domain.errorMsg;
import com.hzuhelper.Utility.MySqlHelper;

public class errorMsgDAL {
	private String TABLE_NAME;
	private String name;

	public int add(errorMsg model) throws SQLException {
		ResultSet rs = null;
		PreparedStatement pst = null;
		Connection connection = null;
		MySqlHelper msh = MySqlHelper.getInstance();
		try {
			connection = msh.getConnection();
			String sql = "insert into " + TABLE_NAME + " (" + name
					+ ") values (?)";
			pst = connection.prepareStatement(sql);
			//pst.setString(1, model.getName());
			pst.executeUpdate();
			sql = "SELECT LAST_INSERT_ID()";
			pst = connection.prepareStatement(sql);
			rs = pst.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (SQLException ex) {
			throw ex;
		} finally {
			msh.free(rs, connection, pst);
		}
	}
}
