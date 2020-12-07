package edu.wpi.modula3.what2think.db;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import edu.wpi.modula3.what2think.model.*;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

	public boolean addChoice(Choice choice) throws Exception {
		if (choice.getMaxUsers() == null || choice.getMaxUsers() < 1) throw new Exception("Invalid number of participants");
		if (choice.getAlternatives() == null || choice.getAlternatives().length < 2) {
			throw new Exception("Invalid number of alternatives");
		}

		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO " + CHOICES_TABLE +
					" (choiceID,description,maxParticipants,creationTime) values(?,?,?,?);");
			ps.setString(1, choice.getId());
			ps.setString(2, choice.getDescription());
			ps.setInt(3, choice.getMaxUsers());
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			ps.setTimestamp(4, ts);
			ps.executeUpdate();

			int index = 0;
			for (Alternative a : choice.getAlternatives()) {
				ps = conn.prepareStatement("INSERT INTO " + ALTERNATIVES_TABLE +
						" (`alternativeID`, `choiceID`, `description`, `order`) values(?,?,?,?)");
				ps.setString(1, a.getId());
				ps.setString(2, choice.getId());
				ps.setString(3, a.getContent());
				ps.setInt(4, index++);
				ps.executeUpdate();
			}

			return true;
		} catch (Exception e) {
			logger.log("Error in addChoice!\n" + e.getMessage() + "\n");
			throw e;
		}
	}

	public boolean addFeedback(Feedback feedback) throws Exception {
		if (feedback.getContent() == null) throw new Exception("No feedback content");

		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO " + FEEDBACKS_TABLE +
					" (alternativeID,creatorID,content,timestamp) values(?,?,?,?);");
			ps.setString(1, feedback.getAlternativeId());
			ps.setString(2, getUserID(getChoiceId(feedback.getAlternativeId()), feedback.getUser().getName()));
			ps.setString(3, feedback.getContent());
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			ps.setTimestamp(4, ts);
			ps.executeUpdate();

			return true;
		} catch (Exception e) {
			logger.log("Error in addFeedback!\n" + e.getMessage() + "\n");
			throw e;
		}
	}

	public boolean addUser(String choiceId, User user) throws Exception {
		Choice c = getChoice(choiceId);
		if(c.getUsers().length < c.getMaxUsers()) {
			try {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO " + USERS_TABLE +
						" (userID,choiceID,name,password) values(?,?,?,?);");
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, choiceId);
				ps.setString(3, user.getName());
				ps.setString(4, user.getPassword());
				ps.executeUpdate();

				return true;

			} catch (Exception e) {
				logger.log("Error in addUser!\n" + e.getMessage() + "\n");
			}
		}
		else{
			throw new Exception("Participants is full");
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
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + USERS_TABLE +
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

	public boolean validateAlternativeAction(String choiceId, AlternativeAction act) throws Exception{
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + CHOICES_TABLE +
				" WHERE choiceID=?;");
		ps.setString(1, choiceId);
		ResultSet resultSet = ps.executeQuery();
		if(resultSet.next()){
			ps = conn.prepareStatement("SELECT * FROM " + ALTERNATIVES_TABLE +
					" WHERE choiceID=? AND alternativeID=?;");
			ps.setString(1, choiceId);
			ps.setString(2, act.getAlternative().getId());
			resultSet = ps.executeQuery();
			if(resultSet.next()){
				ps = conn.prepareStatement("SELECT * FROM " + USERS_TABLE+
						" WHERE choiceID=? AND name=?;");
				ps.setString(1, choiceId);
				ps.setString(2, act.getUser().getName());
				resultSet = ps.executeQuery();
				if(resultSet.next()){
					return true;
				}
				else{
					throw new Exception("No user with this name in given choice");
				}
			}
			else{
				throw new Exception("No alternative with this ID in given choice");
			}
		}
		else {
			throw new Exception("No choice with this ID");
		}
	}

	public boolean voteExists(String choiceId, AlternativeAction act, boolean approve){
		try {
			String userID = getUserID(choiceId, act.getUser().getName());
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + VOTES_TABLE +
					" WHERE alternativeID=? AND userID=? AND approve=?;");
			ps.setString(1, act.getAlternative().getId());
			ps.setString(2, userID);
			ps.setBoolean(3, approve);
			ResultSet resultSet = ps.executeQuery();

			return resultSet.next();
		}
		catch (Exception e){
			logger.log("Error in voteExists!\n" + e.getMessage() + "\n");
		}
		return false;
	}

	public boolean deleteVote(String choiceId, AlternativeAction act, boolean approve){
		try{
            String userID = getUserID(choiceId, act.getUser().getName());
			PreparedStatement ps = conn.prepareStatement("DELETE FROM " + VOTES_TABLE +
					" WHERE alternativeID=? AND userID=? AND approve=?;");
			ps.setString(1, act.getAlternative().getId());
			ps.setString(2, userID);
			ps.setBoolean(3, approve);
			ps.executeUpdate();
			return true;
		}
		catch(Exception e){
            logger.log("Error in deleteVote!\n" + e.getMessage() + "\n");
		}
		return false;
	}

	public boolean addVote(String choiceId, AlternativeAction act, boolean approve){
        try {
            String userID = getUserID(choiceId, act.getUser().getName());
            PreparedStatement ps = conn.prepareStatement("INSERT INTO " + VOTES_TABLE +
                    " (alternativeID,userID,approve) values(?,?,?);");
            ps.setString(1, act.getAlternative().getId());
            ps.setString(2, userID);
            ps.setBoolean(3, approve);
            ps.executeUpdate();

            return true;

        } catch (Exception e) {
            logger.log("Error in addUser!\n" + e.getMessage() + "\n");
        }
	    return false;
    }

	public Choice getChoice(String choiceId){
		Choice choice = new Choice();
		try{
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
				choice.setCreationTime(resultSet.getString("creationTime"));

				if (choice.getChosenAlternative() == null) {
					choice.setCompleted(false);
					choice.setCompletionTime(null);
				}
				else {
					choice.setCompleted(true);
					choice.setCompletionTime(resultSet.getString("completionTime"));
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
					" WHERE choiceID=? ORDER BY `order`;");
			ps.setString(1, choiceId);
			ResultSet resultSet = ps.executeQuery();

			int index = 0;
			while (resultSet.next()) {
				String alternativeId = (resultSet.getString("alternativeID"));
				alternatives[index] = getAlternative(alternativeId);
				index++;
			}
			if (alternatives[0] == null) return new Alternative[0];
			return alternatives;
		}
		catch(Exception e){
			logger.log("Error in getAlternatives!\n" + e.getMessage() + "\n");
		}
		return new Alternative[0];
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
				User[] approvers = getApprovers(alternativeId);
				User[] disapprovers = getDisapprovers(alternativeId);
				alternative.setApprovers(approvers);
				alternative.setDisapprovers(disapprovers);
				ArrayList<User> voters = new ArrayList<>();
				if (approvers != null) voters.addAll(Arrays.asList(approvers));
				if (disapprovers != null) voters.addAll(Arrays.asList(disapprovers));
				alternative.setVoters(voters.toArray(new User[0]));
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
		ArrayList<User> users = new ArrayList<>();
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + USERS_TABLE +
					" WHERE choiceID=?;");
			ps.setString(1, choiceId);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				String userId = (resultSet.getString("userID"));
				users.add(getUser(userId));
			}
			if (users.size() == 0) return new User[0];

			return users.toArray(new User[0]);

		}
		catch(Exception e){
			logger.log("Error in getUsers!\n" + e.getMessage() + "\n");
		}
		return new User[0];
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

	public String getUserID(String choiceID, String username){
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + USERS_TABLE +
					" WHERE choiceID=? AND name=?;");
			ps.setString(1, choiceID);
			ps.setString(2, username);
			ResultSet resultSet = ps.executeQuery();

			if(resultSet.next()){
				return resultSet.getString("userId");
			}
			else{
				return null;
			}
		}
		catch(Exception e){
			logger.log("Error in getUserID!\n" + e.getMessage() + "\n");
		}
		return null;
	}

	public String getChoiceId(String alternativeId){
		try{
			// check choiceID, name, password
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + ALTERNATIVES_TABLE +
					" WHERE alternativeID=?;");
			ps.setString(1, alternativeId);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				return resultSet.getString("choiceID");
			}
		}
		catch(Exception e){
			logger.log("Error in getChoiceId!\n" + e.getMessage() + "\n");
		}
		return "";
	}

	public User[] getApprovers(String alternativeId){
		ArrayList<User> users = new ArrayList<>();
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + VOTES_TABLE +
					" WHERE alternativeId=? AND approve=?;");
			ps.setString(1, alternativeId);
			ps.setBoolean(2, true);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				String userId = (resultSet.getString("userId"));
				users.add(getUser(userId));
			}
			if (users.size() == 0) return new User[0];
			return users.toArray(new User[0]);
		}
		catch(Exception e){
			logger.log("Error in getApprovers!\n" + e.getMessage() + "\n");
		}
		return new User[0];
	}

	public User[] getDisapprovers(String alternativeId){
		ArrayList<User> users = new ArrayList<>();
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + VOTES_TABLE +
					" WHERE alternativeId=? AND approve=?;");
			ps.setString(1, alternativeId);
			ps.setBoolean(2, false);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				String userId = (resultSet.getString("userId"));
				users.add(getUser(userId));
			}
			if (users.size() == 0) return new User[0];
			return users.toArray(new User[0]);
		}
		catch(Exception e){
			logger.log("Error in getDisapprovers!\n" + e.getMessage() + "\n");
		}
		return new User[0];
	}

	public Feedback[] getFeedbacks(String alternativeId){
		ArrayList<Feedback> feedbacks = new ArrayList<>();
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + FEEDBACKS_TABLE +
					" WHERE alternativeId=?;");
			ps.setString(1, alternativeId);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Feedback feedback = new Feedback();
				feedback.setUser(getUser(resultSet.getString("creatorID")));
				feedback.setContent(resultSet.getString("content"));
				feedback.setTimestamp(resultSet.getString("timestamp"));
				feedback.setAlternativeId(resultSet.getString("alternativeId"));
				feedbacks.add(feedback);
			}
			if (feedbacks.size() == 0) return new Feedback[0];

			return feedbacks.toArray(new Feedback[0]);
		}
		catch(Exception e){
			logger.log("Error in getFeedbacks!\n" + e.getMessage() + "\n");
		}
		return new Feedback[0];
	}

	public boolean completeChoice(String choiceId, Alternative alternative) throws Exception{
		try {
			String alternativeId = alternative.getId();
			if (getAlternative(alternativeId) == null) {
				logger.log("Alternative: " + alternativeId + " does not exist!\n");
				return false;
			}
			Choice choice = getChoice(getChoiceId(alternativeId));
			boolean validAlt = false;
			for (Alternative alt : choice.getAlternatives()) {
				if (alt.getId().equals(alternativeId)) {
					validAlt = true;
					break;
				}
			}
			if (!validAlt) {
				logger.log("Chosen alternative is not part of choice " + choice.getId() + "!\n");
				return false;
			}


			PreparedStatement ps = conn.prepareStatement("UPDATE " + CHOICES_TABLE +
					" SET completionTime=?,chosenAlternativeID=?" +
					" WHERE choiceID=?;");

			Timestamp ts = new Timestamp(System.currentTimeMillis());
			ps.setTimestamp(1, ts);
			ps.setString(2, alternativeId);
			ps.setString(3, choiceId);
			ps.executeUpdate();

			return true;

		} catch (Exception e) {
			logger.log("Error in completeChoice!\n" + e.getMessage() + "\n");
		}
		return false;
	}
public SimpleChoice[] getSimplifiedChoices() throws Exception {
		ArrayList<SimpleChoice> simpleChoices = new ArrayList<>();
		try{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + CHOICES_TABLE);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				SimpleChoice simpleChoice = new SimpleChoice();
				simpleChoice.setId(resultSet.getString("choiceID"));
				simpleChoice.setDescription(resultSet.getString("description"));
				simpleChoice.setDateCreated(resultSet.getString("creationTime"));
				if (resultSet.getString("chosenAlternativeID") != null) {
					simpleChoice.setDateCompleted(resultSet.getString("completionTime"));
				}
				simpleChoices.add(simpleChoice);
			}
			if (simpleChoices.size() == 0) return new SimpleChoice[0];

	public boolean deleteChoices(float days){
		try{
			/*PreparedStatement ps = conn.prepareStatement("DELETE FROM " + CHOICES_TABLE +
					" WHERE creationTime < DATEADD(ss,?,GETDATE());");
			*/
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + CHOICES_TABLE +
					" WHERE creationTime < DATE_SUB(NOW(),INTERVAL ? DAY);");
			ps.setFloat(1, days);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String choiceId = (resultSet.getString("choiceID"));
				String timeStamp = (resultSet.getString("creationTime"));
				System.out.println("Choice ID: " + choiceId + ", timestamp: " + timeStamp);
			}
			return true;
		}
		catch(Exception e){
			logger.log("Error in deleteChoices!\n" + e.getMessage() + "\n");
		}
		return false;
	}

	public boolean addOldChoice(Choice choice, float daysOld) throws Exception {
		if (choice.getMaxUsers() == null || choice.getMaxUsers() < 1)
			throw new Exception("Invalid number of participants");
		if (choice.getAlternatives() == null || choice.getAlternatives().length < 2) {
			throw new Exception("Invalid number of alternatives");
		}

		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO " + CHOICES_TABLE +
					" (choiceID,description,maxParticipants,creationTime) values(?,?,?,?);");
			ps.setString(1, choice.getId());
			ps.setString(2, choice.getDescription());
			ps.setInt(3, choice.getMaxUsers());
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(ts.getTime());
			cal.add(Calendar.SECOND, -1 * (int) (daysOld * 24 * 60 * 60));
			ts = new Timestamp(cal.getTime().getTime());
			System.out.println(ts);
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
			throw e;
		}
	}
			return simpleChoices.toArray(new SimpleChoice[0]);
		}
		catch(Exception e){
			logger.log("Error in getSimplifiedChoices!\n" + e.getMessage() + "\n");
		}
		return new SimpleChoice[0];

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
