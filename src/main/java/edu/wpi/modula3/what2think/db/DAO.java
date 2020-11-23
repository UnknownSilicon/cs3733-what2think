package edu.wpi.modula3.what2think.db;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import edu.wpi.modula3.what2think.model.Feedback;
import edu.wpi.modula3.what2think.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;

public class DAO {
	LambdaLogger logger;

	Connection conn;

	final String CHOICES_TABLE = "CHOICES";
	final String ALTERNATIVES_TABLE = "ALTERNATIVES";
	final String USERS_TABLE = "USERS";
	final String VOTES_TABLE = "VOTES";
	final String FEEDBACKS_TABLE = "FEEDBACKS";

	public DAO(LambdaLogger logger) {
		this.logger = logger;
		try  {
			conn = DatabaseUtil.connect();
		} catch (Exception e) {
			conn = null;
			logger.log("Error creating DAO");
		}
	}

	public boolean addChoice(Choice choice) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO " + CHOICES_TABLE +
					" (choiceID,description,maxParticipants,creationTime) values(?,?,?,?);");
			ps.setString(1, choice.getId());
			ps.setString(2, choice.getDescription());
			ps.setInt(3, choice.getMaxUsers());
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			ps.setTimestamp(4, ts);
			ps.executeUpdate();

			for (Alternative a : choice.getAlternatives()) {
				ps = conn.prepareStatement("INSERT INTO " + ALTERNATIVES_TABLE +
						" (alternativeID, choiceID, description) values(?,?,?)");
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

	public boolean addUser(String choiceId, User user){
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO " + USERS_TABLE +
					" (userID,choiceID,name,password) values(?,?,?,?);");
			ps.setString(1, UUID.randomUUID().toString());
			ps.setString(2, choiceId);
			ps.setString(3, user.getName());
			ps.setString(4, user.getPassword());
			ps.executeUpdate();

			return true;
		}
		catch(Exception e){
			logger.log("Error in addUser!\n" + e.getMessage() + "\n");
		}
		return false;
	}

	public boolean validateUser(String choiceId, User user){
		try{
			// check choiceID, name, password
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + USERS_TABLE +
					" WHERE choiceID=? AND name=? AND password=?;");
			ps.setString(1, choiceId);
			ps.setString(2, user.getName());
			ps.setString(3, user.getPassword());
			ResultSet resultSet = ps.executeQuery();

			return resultSet.next();
		}
		catch(Exception e){
			logger.log("Error in getUser!\n" + e.getMessage() + "\n");
		}
		return false;
	}

	public boolean getUser(String choiceId, User user){
		try{
			// check choiceID, name, password
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + CHOICES_TABLE +
					" WHERE choiceID=? AND name=?;");
			ps.setString(1, choiceId);
			ps.setString(2, user.getName());
			ResultSet resultSet = ps.executeQuery();

			return resultSet.next();
		}
		catch(Exception e){
			logger.log("Error in getUser!\n" + e.getMessage() + "\n");
		}
		return false;
	}

	public Choice getChoice(String choiceId){
		Choice choice = new Choice();
		try{
			// check choiceID, name, password
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + CHOICES_TABLE +
					" WHERE choiceID=?;");
			ps.setString(1, choiceId);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				choice.setId(choiceId);
				choice.setDescription(resultSet.getString("description"));
				choice.setMaxUsers(resultSet.getInt("maxParticipants"));
				String chosenAlternativeID = resultSet.getString("chosenAlternativeID");
				choice.setAlternatives(getAlternatives(choiceId));
				choice.setUsers(getUsers(choiceId));
				choice.setChosenAlternative(getAlternative(chosenAlternativeID));
				choice.setCreationTime(resultSet.getTimestamp("creationTime").toString());

				if (choice.getChosenAlternative() == null) {
					choice.setCompleted(false);
					choice.setCompletionTime(null);
				}
				else {
					choice.setCompleted(true);
					choice.setCompletionTime(resultSet.getTimestamp("completionTime").toString());
				}
			}

			if (choice.getId() == null) {
				logger.log("No choice found for the given ID: " + choiceId + "\n");
				return null;
			}
			return choice;
		}
		catch(Exception e){
			logger.log("Error in getChoice!\n" + e.getMessage() + "\n");
		}
		return null;
	}

	public Alternative[] getAlternatives(String choiceId){
		Alternative[] alternatives = new Alternative[5];
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + ALTERNATIVES_TABLE +
					" WHERE choiceID=?;");
			ps.setString(1, choiceId);
			ResultSet resultSet = ps.executeQuery();

			int index = 0;
			while (resultSet.next()) {
				String alternativeId = (resultSet.getString("alternativeID"));
				alternatives[index] = getAlternative(alternativeId);
				index++;
			}
			if (alternatives[0] == null) return null;
			return alternatives;
		}
		catch(Exception e){
			logger.log("Error in getAlternatives!\n" + e.getMessage() + "\n");
		}
		return null;
	}

	public Alternative getAlternative(String alternativeId){
		Alternative alternative = new Alternative();
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + ALTERNATIVES_TABLE +
					" WHERE alternativeID=?;");
			ps.setString(1, alternativeId);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				alternative.setId(alternativeId);
				alternative.setContent(resultSet.getString("description"));
				alternative.setApprovers(getApprovers(alternativeId));
				alternative.setDisapprovers(getDisapprovers(alternativeId));
				//alternative.setVoters(getVoters(alterativeId)); //what are voters?
				alternative.setFeedback(getFeedbacks(alternativeId));
			}
			if (alternativeId != null) return alternative;
		}
		catch(Exception e){
			logger.log("Error in getAlternative!\n" + e.getMessage() + "\n");
		}
		return null;
	}

	public User[] getUsers(String choiceId){
		User[] users = new User[99]; //start with list then transfer to array if needs to be unlimited
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + USERS_TABLE +
					" WHERE choiceID=?;");
			ps.setString(1, choiceId);
			ResultSet resultSet = ps.executeQuery();

			int index = 0;
			while (resultSet.next()) {
				String userId = (resultSet.getString("userID"));
				users[index] = getUser(userId);
				index++;
			}
			if (users[0] == null) return null;
			return users;

		}
		catch(Exception e){
			logger.log("Error in getUsers!\n" + e.getMessage() + "\n");
		}
		return null;
	}

	public User getUser(String userId){
		User user = new User();
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + USERS_TABLE +
					" WHERE userID=?;");
			ps.setString(1, userId);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				user.setName(resultSet.getString("name"));
				user.setPassword(resultSet.getString("password")); //omit if poor security
			}
			if (user.getName() != null) return user;
		}
		catch(Exception e){
			logger.log("Error in getUser!\n" + e.getMessage() + "\n");
		}
		return null;
	}

	public User[] getApprovers(String alternativeId){
		User[] users = new User[99]; //start with list then transfer to array if needs to be unlimited
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + VOTES_TABLE +
					" WHERE alternativeId=? AND approve=?;");
			ps.setString(1, alternativeId);
			ps.setBoolean(2, true);
			ResultSet resultSet = ps.executeQuery();

			int index = 0;
			while (resultSet.next()) {
				String userId = (resultSet.getString("userId"));
				users[index] = getUser(userId);
				index++;
			}
			if (users[0] == null) return null;
			if (users[0].getName() != null) return users;
		}
		catch(Exception e){
			logger.log("Error in getApprovers!\n" + e.getMessage() + "\n");
		}
		return null;
	}

	public User[] getDisapprovers(String alternativeId){
		User[] users = new User[99]; //start with list then transfer to array if needs to be unlimited
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + VOTES_TABLE +
					" WHERE alternativeId=? AND approve=?;");
			ps.setString(1, alternativeId);
			ps.setBoolean(2, false);
			ResultSet resultSet = ps.executeQuery();

			int index = 0;
			while (resultSet.next()) {
				String userId = (resultSet.getString("userId"));
				users[index] = getUser(userId);
				index++;
			}
			if (users[0] == null) return null;
			if (users[0].getName() != null) return users;
		}
		catch(Exception e){
			logger.log("Error in getDisapprovers!\n" + e.getMessage() + "\n");
		}
		return null;
	}

	public Feedback[] getFeedbacks(String alternativeId){
		Feedback[] feedbacks = new Feedback[99]; //start with list then transfer to array if needs to be unlimited
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + FEEDBACKS_TABLE +
					" WHERE alternativeId=?;");
			ps.setString(1, alternativeId);
			ResultSet resultSet = ps.executeQuery();

			int index = 0;
			while (resultSet.next()) {
				Feedback feedback = new Feedback();
				feedback.setUser(getUser(resultSet.getString("creatorID")));
				feedback.setContent(resultSet.getString("content"));
				feedback.setTimestamp(resultSet.getTimestamp("timestamp").toString());

				feedbacks[index] = feedback;
				index++;
			}
			if (feedbacks[0] == null) return null;

			if (feedbacks[0].getContent() != null) return feedbacks;
		}
		catch(Exception e){
			logger.log("Error in getFeedbacks!\n" + e.getMessage() + "\n");
		}
		return null;
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
