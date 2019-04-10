package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.sun.javafx.collections.MappingChange.Map;

import bean.*;
import model.*;
import DAO.*;


public class OrderDAO {

	private DataSource ds;
	
	public OrderDAO() throws ClassNotFoundException{
		try {
		  this.ds = (DataSource)(new InitialContext()).lookup("java:/comp/env/jdbc/EECS");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	
	public void addtoOrders(ArrayList<CartBean> shoppingCart) throws SQLException {
		
		LocalDate date = LocalDate.now();
		String user = shoppingCart.get(0).getUsername();
		String query = "insert into ORDERS(odate, username) values('" + date + "', '" + user + "')";

		Connection con = this.ds.getConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate(query);
		stmt.close();
		con.close();
		
		this.addOrderDetails(shoppingCart);
	}
	
	public void addOrderDetails(ArrayList<CartBean> shoppingCart) throws SQLException {
		int lastOID = this.getLastOrderId();
		
		for (CartBean cartItem : shoppingCart) {
			this.addSingleOrderDetails(lastOID, cartItem.getBid());
		}
	}
	
	public void addSingleOrderDetails(int oid, int bid) throws SQLException {
		
		String query = "insert into ORDERDETAILS(oid, bid) values(" + oid + ", " + bid + ")";
		Connection con = this.ds.getConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate(query);
		stmt.close();
		con.close();
	}
	

	public int getOrderCount() throws SQLException {
		
		String query = "select count(*) as totalCount from ORDERS";
		int count = -1;
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		
		if (r.next()) {
			count = r.getInt("totalCount");
		}
		
		p.close();
		con.close();
		r.close();
		
		return count;
	}
	
	public int getLastOrderId() throws SQLException {
		String query = "select max(oid) as lastOrderId from orders";
		int lastOrderId = -1;
		Connection con = this.ds.getConnection();
		PreparedStatement p = con.prepareStatement(query);
		ResultSet r = p.executeQuery();
		if (r.next()) {
			lastOrderId = r.getInt("lastOrderId");
		}
		
		p.close();
		con.close();
		r.close();
		
		return lastOrderId;
	}

}

















