package model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import DAO.*;
import bean.*;

public class Model {
	private UserDAO userDAO;
	private BookDAO bookDAO;

	private boolean errorStatus;
	private String errorMessage;

	public Model() {

		this.errorStatus = false;
		this.errorMessage = null;

		try {
			userDAO = new UserDAO();
			bookDAO = new BookDAO();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************
	DATABASE USER OPERATIONS
    ****************************************************************/
	
	public UserBean retrieveUser(String username) throws Exception {
		return userDAO.retrieveUser(username);
	}

	public void addUser(String username, String email, String password) {
		try {
			userDAO.addUser(username, email, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updatePassword(String username, String newPassword) {
		try {
			userDAO.updatePassword(username, newPassword);
		} catch (SQLException e) {
			System.out.println("THERE WAS AN ERROR");
			e.printStackTrace();
		}
	}
	
	/***************************************************************
	DATABASE BOOK OPERATIONS
    ****************************************************************/
	
	public BookBean retrieveBook(String bid){
		
			try {
				return bookDAO.retrieveBook(bid);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	}
	
	public Map<String, BookBean> retrieveByAuthor(String author){
		Map<String, BookBean> books = new HashMap<String, BookBean>();
		try {
			return bookDAO.retrieveByAuthor(author);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/***************************************************************
		ERROR CHECKING METHODS
	 ****************************************************************/
	
	public void checkLoginError(String username, String password) {
		this.errorMessage = null;
		this.errorStatus = false;
		if (username == "" || password == "") {
			this.errorStatus = true;
			if(username == "") {
				this.errorMessage = "BLANKUSERNAME";
			}
			else {
				this.errorMessage = "BLANKPASSWORD";
			}
		}
		else if (!checkUserExists(username)) {
			this.errorStatus = true;
			this.errorMessage = "USERNOTFOUND";
		}
		else if (!passwordValidation(username, password)){
			this.errorStatus = true;
			this.errorMessage = "WRONGPASSWORD";
		}

	}
	
	public void checkSignUpError(String username, String email, String password) {
		this.errorMessage = null;
		this.errorStatus = false;
		if (username == "" || email == "" || password == "") {
			this.errorStatus = true;
			if(username == "") {
				this.errorMessage = "BLANKUSERNAME";
			}else if (email == ""){
				this.errorMessage = "BLANKEMAIL";
			}else {
				this.errorMessage = "BLANKPASSWORD";
			}
			return;
		}
		else if (!email.contains("@") || email.length() < 3) {
			this.errorStatus = true;
			this.errorMessage = "EMAILFORMAT";
		}
		else if (password.length() < 6) {
			this.errorStatus = true;
			this.errorMessage = "SHORTPASSWORD";
		}
	}
	
	public boolean passwordValidation(String username, String password) {
		UserBean ub = new UserBean();
		try {
			ub = retrieveUser(username);
			System.out.println(ub.getPassword());
			return ub.getPassword().equals(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean checkUserExists(String username) {
		try {
			return userDAO.checkUserExists(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean getErrorStatus() {
		return this.errorStatus;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}















}