package edu.wpi.modula3.what2think.db;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class DAO {
	LambdaLogger logger;

	Connection conn;

	final String CHOICES_TABLE = "CHOICES";
	final String ALTERNATIVES_TABLE = "ALTERNATIVES";

	public DAO(LambdaLogger logger) {
		this.logger = logger;
		try  {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
		}
	}

	public boolean addChoice(Choice choice) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO " + CHOICES_TABLE + " (choiceID,description,maxParticipants,creationTime) values(?,?,?,?);");
			ps.setString(1, choice.getId());
			ps.setString(2, choice.getDescription());
			ps.setInt(3, choice.getMaxUsers());
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			ps.setTimestamp(4, ts);
			ps.executeUpdate();

			for (Alternative a : choice.getAlternatives()) {
				ps = conn.prepareStatement("INSERT INTO " + ALTERNATIVES_TABLE + " (alternativeID, choiceID, description) values(?,?,?)");
				ps.setString(1, a.getId());
				ps.setString(2, choice.getId());
				ps.setString(3, a.getContent());
				ps.executeUpdate();
			}

			return true;
		} catch (Exception e) {
			logger.log("Error in addChoice!\n" + e.getMessage() + "\n");
		}

		return false;
	}

	/*public Constant getConstant(String name) throws Exception {

		try {
			Constant constant = null;
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE name=?;");
			ps.setString(1,  name);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				constant = generateConstant(resultSet);
			}
			resultSet.close();
			ps.close();

			return constant;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed in getting constant: " + e.getMessage());
		}
	}

	public boolean updateConstant(Constant constant) throws Exception {
		try {
			String query = "UPDATE " + tblName + " SET value=? WHERE name=?;";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setDouble(1, constant.value);
			ps.setString(2, constant.name);
			int numAffected = ps.executeUpdate();
			ps.close();

			return (numAffected == 1);
		} catch (Exception e) {
			throw new Exception("Failed to update report: " + e.getMessage());
		}
	}

	public boolean deleteConstant(Constant constant) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tblName + " WHERE name = ?;");
			ps.setString(1, constant.name);
			int numAffected = ps.executeUpdate();
			ps.close();

			return (numAffected == 1);

		} catch (Exception e) {
			throw new Exception("Failed to insert constant: " + e.getMessage());
		}
	}


	public boolean addConstant(Constant constant) throws Exception {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE name = ?;");
			ps.setString(1, constant.name);
			ResultSet resultSet = ps.executeQuery();

			// already present?
			while (resultSet.next()) {
				Constant c = generateConstant(resultSet);
				resultSet.close();
				return false;
			}

			ps = conn.prepareStatement("INSERT INTO " + tblName + " (name,value) values(?,?);");
			ps.setString(1,  constant.name);
			ps.setDouble(2,  constant.value);
			ps.execute();
			return true;

		} catch (Exception e) {
			throw new Exception("Failed to insert constant: " + e.getMessage());
		}
	}

	public List<Constant> getAllConstants() throws Exception {

		List<Constant> allConstants = new ArrayList<>();
		try {
			Statement statement = conn.createStatement();
			String query = "SELECT * FROM " + tblName + ";";
			ResultSet resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				Constant c = generateConstant(resultSet);
				allConstants.add(c);
			}
			resultSet.close();
			statement.close();
			return allConstants;

		} catch (Exception e) {
			throw new Exception("Failed in getting books: " + e.getMessage());
		}
	}

	private Constant generateConstant(ResultSet resultSet) throws Exception {
		String name  = resultSet.getString("name");
		Double value = resultSet.getDouble("value");
		return new Constant (name, value);
	}*/
}
