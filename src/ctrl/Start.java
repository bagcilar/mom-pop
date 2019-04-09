package ctrl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.*;
import bean.*;


/**
 * Servlet implementation class Start
 */
@WebServlet({"/Start", "/Start/*"})
public class Start extends HttpServlet {
	private static final long serialVersionUID = 1L;
	boolean loggedIn;
	boolean adminLoggedIn;	
	String target;
	boolean error;
	ArrayList <BookBean> books;
	ArrayList <CartBean> cart;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Start() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = getServletContext();
		
		loggedIn = Boolean.parseBoolean(this.getServletContext().getInitParameter("loggedIn"));
		adminLoggedIn = Boolean.parseBoolean(this.getServletContext().getInitParameter("adminLoggedIn"));
		
		target = "/Home.jspx";
		error = false;
		books = new ArrayList<BookBean>();
		cart = new ArrayList<CartBean>();
		Model model = new Model();
		model.clearVisitorCart();
		
		try {
			context.setAttribute("myModel", new Model());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			context.setAttribute("errorChecking", new ErrorChecking());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			context.setAttribute("query", new QueryConstructor());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/***************************************************************
			INITIALIZATION OF MODEL AND QUERY
		 ****************************************************************/
		Model myModel = (Model) this.getServletContext().getAttribute("myModel");
		ErrorChecking errorChecking = (ErrorChecking) this.getServletContext().getAttribute("errorChecking");
		QueryConstructor queryObject = (QueryConstructor) this.getServletContext().getAttribute("query");
		request.getSession().setAttribute("query", queryObject);
		
		/***************************************************************
			PAGE REDIRECTIONS
		 ****************************************************************/
		this.redirector(request, response, myModel, queryObject);
		
		/***************************************************************
			SIGN UP
		 ****************************************************************/
		if (request.getParameter("signUpButton") != null) {
			this.signUp(request, response, myModel, errorChecking);
		}
		
		/***************************************************************
			LOGIN
		****************************************************************/
		if (request.getParameter("loginButton") != null) {
			this.logIn(request, response, myModel, errorChecking);
		}
		
		/***************************************************************
			SIGN OUT
	    ****************************************************************/
		if (request.getParameter("signoutButton") != null) {
			this.signOut(request, response);
		}
		
		/***************************************************************
			BOOK LISTINGS
		****************************************************************/
		
		// Calls method for listing all books
		if (request.getParameter("booksPageButton") != null) {
			this.listAllBooks(request, response, myModel, queryObject);
		}
		
		// Calls method for listing books by category
		if (request.getParameter("headerCategory") != null) {
			this.listBooksByCategory(request, response, myModel, queryObject);
		}
		
		/***************************************************************
			BOOK SORTINGS
		****************************************************************/
		if (request.getParameter("sortButton") != null) {
			this.sortBooks(request, response, myModel, queryObject);	
		}
		
		/***************************************************************
			FILTER
		****************************************************************/
		if (request.getParameter("filterButton") != null || request.getParameter("resetFilterButton") != null ) {
			this.filter(request, response, myModel, queryObject);
		}
	
		/***************************************************************
			SINGLE BOOK PAGE
		****************************************************************/
		if (request.getParameter("title") != null ) {
			int title = (Integer.parseInt(request.getParameter("title")));
			this.openBook(request, response, myModel, title);
		}
		
		if (request.getParameter("addReviewButton") != null) {
			this.addReview(request, response, myModel);
		}
		
		
		/***************************************************************
			SEARCH BAR
		****************************************************************/
		if (request.getParameter("searchButton") != null) {
			this.searchStore(request, response, myModel, queryObject);
		}
		
		/***************************************************************
			PRODUCT CATALOG SERVICES
		 ****************************************************************/
		if (request.getParameter("PCSGenerateButton") != null) {
			this.catalogService(request, response, myModel, errorChecking);
		}
		
		
		/***************************************************************
			ADD TO SHOPPING CART
		 ****************************************************************/
		if (request.getParameter("addToCart") != null) {
			this.addToCart(request, response, myModel);
		}
		
		/***************************************************************
			REMOVE FROM SHOPPING CART
		 ****************************************************************/
		if (request.getParameter("removeItem") != null) {
			this.removeFromCart(request, response, myModel);
		}
		
		/***************************************************************
			UPDATE SHOPPING CART
		 ****************************************************************/
		if (request.getParameter("updateCart") != null) {
			this.updateCart(request, response, myModel);
		}
		
		
		
		/***************************************************************
			TESTING BLOCK
		 ****************************************************************/
	
		
		if (loggedIn) {
			
			String username = (String) request.getSession().getAttribute("loggedInUser");
			AddressBean shippingAB = myModel.retrieveAddress(username, "shipping");
			AddressBean billingAB = myModel.retrieveAddress(username, "billing");
			System.out.println("USER: " + username);
			
			System.out.println("PHONE NUMBER: "+ billingAB.getPhoneNumber());
			
			System.out.println("SHIPPING ADDRESS LINE1: " + shippingAB.getAddressLine1());
			System.out.println("SHIPPING ADDRESS CITY: "+ shippingAB.getCity());
			System.out.println("SHIPPING ADDRESS PROVINCE: "+ shippingAB.getProvince());

			
			System.out.println("BILLING ADDRESS LINE1: " + billingAB.getAddressLine1());
			System.out.println("BILLING ADDRESS CITY: "+ billingAB.getCity());
			System.out.println("BILLING ADDRESS PROVINCE: "+ billingAB.getProvince());
		}
		
		/***************************************************************
			TESTING BLOCK
		****************************************************************/
		
		
		
		request.getRequestDispatcher(target).forward(request, response);
		
		
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	protected void redirector(HttpServletRequest request, HttpServletResponse response, Model myModel, QueryConstructor queryObject) throws ServletException, IOException {
						
		//checks if a 'Single Book' has been clicked on
		if (request.getParameter("title") != null) {
			target = "/SingleBook.jspx";
		}
		//once a review has been submitted, the page is reloaded
		if (request.getParameter("addReviewButton") != null) {
			target = "/SingleBook.jspx";
		}

		//checks if 'Sign Up' button was pressed, sets target to the Sign Up page if true
		if (request.getParameter("signUpPageButton") != null) {
			request.setAttribute("differentAddressTypes", true);
			target = "/SignUp.jspx";
		}
		//checks if 'Login' button was pressed, sets target to the Login page if true
		if (request.getParameter("loginPageButton") != null) {
			target = "/Login.jspx";
		}
		//checks if 'List Books', 'Sort', or a search was submitted, sets target to the Login page if true
		if (request.getParameter("booksPageButton") != null || (request.getParameter("searchButton") != null) || (request.getParameter("sortButton") != null) || (request.getParameter("filterButton") != null) || (request.getParameter("headerCategory") != null)) {
			target = "/Books.jspx";
			//Retrieves unique categories and sets the filter display
			ArrayList <String>categories = new ArrayList<String>();
			categories = myModel.retrieveUniqueCategories();
			request.setAttribute("categoriesFilterList", categories);
		}
				
		//checks if services access is requested
		//checks if user is logged in as admin
		if (request.getParameter("servicesButton") != null) {
			if (!adminLoggedIn) {
				target = "/Login.jspx";
			}
			else if (adminLoggedIn){
				target = "/Services.jspx";
			}		
		}
		
		//checks if analytics access is requested
		//checks if user is logged in as admin
		if (request.getParameter("analyticsButton") != null) {
			if (!adminLoggedIn) {
				target = "/Login.jspx";
			}
			else if (adminLoggedIn){
				target = "/Analytics.jspx";
			}		
		}
		
		//checks if PCS was requested
		if (request.getParameter("PCSRequestButton") != null) {
			target = "/ProductCatalogService.jspx";	
		}
		
		//checks if 'Home' button was pressed, sets target to the Sign Up page if true
		if (request.getParameter("homeButton") != null) {
			if (adminLoggedIn) {
				adminLoggedIn = false;
				loggedIn = false;
				queryObject.reset();
				queryObject.resetFilter();
				queryObject.resetSort();
				request.getSession().setAttribute("loggedInSession", loggedIn);				
			}
			target = "/Home.jspx";
		}
		
		//checks if Shopping Cart was requested
		if (request.getParameter("showShoppingCart") != null) {
			target = "/ShoppingCart.jspx";	
		}
		
	}
		
	protected void logIn(HttpServletRequest request, HttpServletResponse response, Model myModel, ErrorChecking errorChecking) throws ServletException, IOException {
		
		String username = request.getParameter("loginName");
		String password = request.getParameter("loginPassword"); 
		
		// Sets errors, if any
		errorChecking.checkLoginError(username, password);
		
		// No errors, user is successfully logged in
		if (!errorChecking.getErrorStatus()) {		
			loggedIn = true;
			request.getSession().setAttribute("loggedInSession", loggedIn);
			request.getSession().setAttribute("loggedInUser", username);
			
			//if admin logs in, redirects to the admin page
			if (request.getSession().getAttribute("loggedInUser").equals("admin")) {
				this.adminLoggedIn = true;
				this.target = "/Admin.jspx";
			}else {
				this.target = "/Home.jspx";
			}		
			
			//clears the 'visitor' shopping cart
			myModel.clearVisitorCart();
			
			//sets the user's cart
			this.setCart(request, response, myModel, username);
		}
		
		else {
			String loginErrorMessage = errorChecking.getErrorMessage();
			error = true;
			this.target = "/Login.jspx";
			request.setAttribute("error", loginErrorMessage);
		}
	}
	
	protected void signUp(HttpServletRequest request, HttpServletResponse response, Model myModel, ErrorChecking errorChecking) throws ServletException, IOException {
		
		String firstName = request.getParameter("signUpFirstName");
		String lastName = request.getParameter("signUpLastName");
		String username = request.getParameter("signUpUsername");
		String email = request.getParameter("signUpEmail");
		String password = request.getParameter("signUpPassword");
		String passwordConf = request.getParameter("signUpPasswordConf");
		
		AddressBean shippingAB = new AddressBean();
		AddressBean billingAB = new AddressBean();
		shippingAB.setType("shipping");
		shippingAB.setUsername(username);
		shippingAB.setAddressLine1(request.getParameter("shippingLine1"));
		shippingAB.setAddressLine2(request.getParameter("shippingLine2"));
		shippingAB.setCountry(request.getParameter("shippingCountry"));
		shippingAB.setProvince(request.getParameter("shippingProvince"));
		shippingAB.setCity(request.getParameter("shippingCity"));
		shippingAB.setZip(request.getParameter("shippingZip"));
		shippingAB.setPhoneNumber(request.getParameter("addressPhone"));
		
		if (request.getParameter("sameTypesCheckbox") != null) {
			billingAB.setType("billing");
			billingAB.setUsername(username);
			billingAB.setAddressLine1(shippingAB.getAddressLine1());
			billingAB.setAddressLine2(shippingAB.getAddressLine2());
			billingAB.setCountry(shippingAB.getCountry());
			billingAB.setProvince(shippingAB.getProvince());
			billingAB.setCity(shippingAB.getCity());
			billingAB.setZip(shippingAB.getZip());
			billingAB.setPhoneNumber(shippingAB.getPhoneNumber());
			errorChecking.checkSignUpError(username, email, password, passwordConf, shippingAB, shippingAB);
		}
		else {
			billingAB.setType("billing");
			billingAB.setUsername(username);
			billingAB.setAddressLine1(request.getParameter("billingLine1"));
			billingAB.setAddressLine2(request.getParameter("billingLine2"));
			billingAB.setCountry(request.getParameter("billingCountry"));
			billingAB.setProvince(request.getParameter("billingProvince"));
			billingAB.setCity(request.getParameter("billingCity"));
			billingAB.setZip(request.getParameter("billingZip"));
			billingAB.setPhoneNumber(request.getParameter("billingPhone"));
			errorChecking.checkSignUpError(username, email, password, passwordConf, shippingAB, billingAB);
		}
		
		//Sets errors, if any
		if (!errorChecking.getErrorStatus()) {
			myModel.addUser(username, firstName, lastName, email, password);
			myModel.addAddress(shippingAB);
			myModel.addAddress(billingAB);
			loggedIn = true;
			request.getSession().setAttribute("loggedInSession", loggedIn);
			request.getSession().setAttribute("loggedInUser", username);
			this.target = "/Home.jspx";
			
			//clears the 'visitor' shopping cart
			myModel.clearVisitorCart();
			
			//sets the user's cart
			this.setCart(request, response, myModel, username);

		}else {
			
			String signUpErrorMessage = errorChecking.getErrorMessage();
			error = true;
			target = "/SignUp.jspx";
			request.setAttribute("error", signUpErrorMessage);
		}
	}
	
	protected void signOut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		loggedIn = false;
		request.getSession().setAttribute("loggedInSession", loggedIn);
		request.getSession().setAttribute("loggedInUser", null);
	}
	

	protected void listAllBooks(HttpServletRequest request, HttpServletResponse response, Model myModel, QueryConstructor queryObject) throws ServletException, IOException {
	
		queryObject.setCategory(null);
		queryObject.setSearchTerm(null);
		queryObject.resetFilter();
		
		queryObject.setAllBooks(true);
		books = myModel.queryConstructor(queryObject);
		request.setAttribute("booksList", books);
	}
	
	protected void listBooksByCategory(HttpServletRequest request, HttpServletResponse response, Model myModel, QueryConstructor queryObject) throws ServletException, IOException {
		
		queryObject.setAllBooks(false);
		queryObject.setSearchTerm(null);
		
		String category = request.getParameter("headerCategory");
		queryObject.setCategory(category);
		books = myModel.queryConstructor(queryObject);
		request.setAttribute("booksList", books);
		
	}
	
	protected void sortBooks(HttpServletRequest request, HttpServletResponse response, Model myModel, QueryConstructor queryObject) throws ServletException, IOException {
		
		//obtains the sort option
		String sortOption = request.getParameter("sortOption");
		queryObject.resetSort();

		queryObject.setSort(true);

		if (sortOption.equals("Newest to Oldest")) {
			queryObject.setSortNewestToOldest(true);
		}
		else if (sortOption.equals("Oldest to Newest")) {
			queryObject.setSortOldestToNewest(true);
		}
		else if (sortOption.equals("Rating")) {
			queryObject.setSortReviewHighToLow(true);
		}
		else if (sortOption.equals("Price - Low to High")) {
			queryObject.setSortPriceLowtoHigh(true);
		}
		else if (sortOption.equals("Price - High to Low")) {
			queryObject.setSortPriceHighToLow(true);
		}
		
		books = myModel.queryConstructor(queryObject);
		request.setAttribute("booksList", books);
		
	}
	
	protected void searchStore(HttpServletRequest request, HttpServletResponse response, Model myModel, QueryConstructor queryObject) throws ServletException, IOException {
		
		queryObject.setAllBooks(false);
		queryObject.setCategory(null);

		//obtains the searched term
		String searchTerm = request.getParameter("searchBar").toUpperCase();
		
		queryObject.setSearchTerm(searchTerm);
			
		books = myModel.queryConstructor(queryObject);
		request.setAttribute("booksList", books);	
	}
	
	protected void filter(HttpServletRequest request, HttpServletResponse response, Model myModel, QueryConstructor queryObject) throws ServletException, IOException {		
		
		
		//if user chose to reset the filter, queryObject's filter attributes are reset
		//if not, queryObject's filter attributes are initialized
		if (request.getParameter("resetFilterButton") != null) {
			queryObject.resetFilter();
			books = myModel.queryConstructor(queryObject);
			request.setAttribute("booksList", books);			
		}
		else {
		
			// checks if any filters were selected
			if (request.getParameter("categoryFilter") != null || request.getParameter("ratingFilter") != null || !request.getParameter("priceLowFilter").equals("") || !request.getParameter("priceHighFilter").equals("")) {
				
				queryObject.setFilter(true);
	
				// obtains the selected category filter, adds it to queryObject
				if (request.getParameter("categoryFilter") != null) {
					String categoryFilter = request.getParameter("categoryFilter");
					queryObject.setCategoryFilter(categoryFilter);
				}
	
				// obtains the selected review filter, adds it to queryObject
				if (request.getParameter("ratingFilter") != null) {
				
					String ratingFilter = request.getParameter("ratingFilter");
					
					if (ratingFilter.equals("above1")) {
						queryObject.setRatingFilter(1);
					}
					else if (ratingFilter.equals("above2")) {
						queryObject.setRatingFilter(2);
					}
					else if (ratingFilter.equals("above3")) {
						queryObject.setRatingFilter(3);
					}			
					else {
						queryObject.setRatingFilter(4);
					}
					
				}
				
				// obtains the selected price range filter
				if (!request.getParameter("priceLowFilter").equals("") || !request.getParameter("priceHighFilter").equals("")) {
					
					double priceLowFilter;
					double priceHighFilter;
					
					if (!request.getParameter("priceLowFilter").equals("") && !request.getParameter("priceHighFilter").equals("")) {
						priceLowFilter = Double.parseDouble(request.getParameter("priceLowFilter"));
						priceHighFilter = Double.parseDouble(request.getParameter("priceHighFilter"));
						queryObject.setPriceFilterHigh(priceHighFilter);
						queryObject.setPriceFilterLow(priceLowFilter);
					}
					else if (!request.getParameter("priceLowFilter").equals("") && request.getParameter("priceHighFilter").equals("")) {
						priceLowFilter = Double.parseDouble(request.getParameter("priceLowFilter"));
						queryObject.setPriceFilterLow(priceLowFilter);
						queryObject.setPriceFilterHigh(Double.MAX_VALUE);
					}
					else{
						priceHighFilter = Double.parseDouble(request.getParameter("priceHighFilter"));
						queryObject.setPriceFilterHigh(priceHighFilter);
						queryObject.setPriceFilterLow(0);

					}
				}
							
			}
				
			books = myModel.queryConstructor(queryObject);
			request.setAttribute("booksList", books);
		}
	}

	
	protected void addToCart(HttpServletRequest request, HttpServletResponse response, Model myModel) throws ServletException, IOException {
		
		String username;
		
		//obtains bid of the book being added to cart
		int bid = Integer.parseInt(request.getParameter("addToCart"));
		
		//obtains the username if the user is logged in. If not, sets username as "visitor"
		if (loggedIn) {
			username = (String) request.getSession().getAttribute("loggedInUser");
		}
		else {
			username = "visitor";
		}
		
		myModel.addToCart(bid, username);
		this.setCart(request, response, myModel, username);

	}
	
	protected void removeFromCart(HttpServletRequest request, HttpServletResponse response, Model myModel) throws ServletException, IOException {
		
		String username;

		//obtains bid of the book being removed from cart
		int bid = Integer.parseInt(request.getParameter("removeItem"));
		
		//obtains the username if the user is logged in. If not, sets username as "visitor"
		if (loggedIn) {
			username = (String) request.getSession().getAttribute("loggedInUser");
		}
		else {
			username = "visitor";
		}
		
		myModel.removeFromCart(bid, username);
		this.setCart(request, response, myModel, username);
		
	}
	
	protected void updateCart(HttpServletRequest request, HttpServletResponse response, Model myModel) throws ServletException, IOException {

		String username;
		int quantity;
			
		//obtains the username if the user is logged in. If not, sets username as "visitor"
		if (loggedIn) {
			username = (String) request.getSession().getAttribute("loggedInUser");
		}
		else {
			username = "visitor";
		}
		
		ArrayList<CartBean> databaseShoppingCart = myModel.retrieveCart(username);
		
		for (CartBean cartItem : databaseShoppingCart) {
			
			//gets the bid of the item being inspected from the database
			String databaseBid = Integer.toString(cartItem.getBid()); 
			//gets the quantity that appears in the current state of cart of the same item		
			quantity = Integer.parseInt(request.getParameter(databaseBid));
			//checks if the inspected item's quantity has been changed
			//if so, updates database. if not, inspects the next item
			if (quantity != cartItem.getQuantity()) {
				myModel.updateQuantity(cartItem.getBid(), cartItem.getUsername(), quantity);
			}
		}
		
		this.setCart(request, response, myModel, username);
		
	}
	
	protected void setCart(HttpServletRequest request, HttpServletResponse response, Model myModel, String username) throws ServletException, IOException {
		
		ArrayList<CartBean> shoppingCart = myModel.retrieveCart(username);
		double totalPrice = myModel.getTotalPrice(username);
		request.getSession().setAttribute("cart", shoppingCart);
		request.getSession().setAttribute("totalPrice", totalPrice);
		
	}
		
	protected void openBook(HttpServletRequest request, HttpServletResponse response, Model myModel, int bookID) throws ServletException, IOException {
		BookBean singleBook = myModel.retrieveBook(bookID);
		request.setAttribute("singleBook", singleBook);
		
		// Retrieve user review for book if it exists, otherwise give option to add review:
		if (request.getSession().getAttribute("loggedInUser") != null) {
			String username = request.getSession().getAttribute("loggedInUser").toString();
			ArrayList<String> review = myModel.retrieveReviewByUsernameAndBook(username, bookID);
			if (!review.isEmpty()) {
				request.setAttribute("userReviewExists", true);
				request.setAttribute("userReview", review.get(0));
				request.setAttribute("userRating", review.get(1));
			}
			else {
				request.setAttribute("userReviewExists", false);
				request.setAttribute("userReview", null);
				request.setAttribute("userRating", null);
			}
		}
	}
	
	protected void addReview(HttpServletRequest request, HttpServletResponse response, Model myModel) throws ServletException, IOException {
		String username = request.getSession().getAttribute("loggedInUser").toString();
		int bookID = Integer.parseInt(request.getParameter("title"));
		String review = request.getParameter("review");
		int rating = Integer.parseInt(request.getParameter("rating"));
		myModel.addReview(username, bookID, review, rating);
		int title = (Integer.parseInt(request.getParameter("title")));
		openBook(request, response, myModel, title);
	}
	
	protected void catalogService(HttpServletRequest request, HttpServletResponse response, Model myModel, ErrorChecking errorChecking) throws ServletException, IOException{
		
		int bid = (Integer.parseInt(request.getParameter("bid")));
		String f = "xmlExports/" + request.getSession().getId()+".xml";
		String filename = this.getServletContext().getRealPath("/" + f);
		request.getSession().setAttribute("filenameProductService", filename);
		request.setAttribute("fProductService", f);
		System.out.println(filename);
		try {
			myModel.exportProductServices(bid, filename);
			request.setAttribute("PCSResultReady", true);
			target = "ProductCatalogService.jspx";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
