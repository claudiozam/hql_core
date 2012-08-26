package edu.palermo.hql.general;

import java.sql.*;
import java.util.*;

public class GeneralUtils {

	public static List<List<Object>> resultSetToObjectList(ResultSet resultSet)
			throws SQLException {
		ArrayList<List<Object>> table;
		int columnCount = resultSet.getMetaData().getColumnCount();

		if (resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY)
			table = new ArrayList<List<Object>>();
		else {
			resultSet.last();
			table = new ArrayList<List<Object>>(resultSet.getRow());
			resultSet.beforeFirst();
		}

		for (ArrayList<Object> row; resultSet.next(); table.add(row)) {
			row = new ArrayList<Object>(columnCount);

			for (int c = 1; c <= columnCount; ++c)
				row.add(resultSet.getObject(c));
		}

		return table;
	}

}
