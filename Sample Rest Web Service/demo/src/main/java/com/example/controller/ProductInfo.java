package com.example.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dao.EcommerceDao;
import com.example.beans.Cart;
import com.example.beans.Checkout;
import com.example.beans.Item;
import com.example.beans.Product;
import com.example.beans.ServiceRequest;
import com.ecommerce.util.Constants;

@CrossOrigin(origins = "http://localhost:8085")
@RestController

public class ProductInfo {
	
	static Object[] keySet;
	static Map<String, String[]> products;
	
	
	static {
		products = EcommerceDao.loadProducts();
		keySet = products.keySet().toArray();
 }

	
	@RequestMapping(method = RequestMethod.GET, value = "/product")
	@ResponseBody
	public String getProductDetail(@RequestParam String productId) {

		//String wineInfo = "<div class=\"container\"><h1>#WINE_NAME_SHORT_DESC#</h1></div>###<div class=\"col-lg-6\"><div class=\"wine_v_1 text-center pb-4\"><img src=\"#WINE_IMG#\" alt=\"Image\" class=\"img-fluid\"></div></div><div class=\"col-lg-5 ml-auto\"><h2 class=\"text-primary\">#WINE_NAME_COST#</h2><p>#WINE_DESCRIPTION#</p><div class=\"mb-5\"><div class=\"input-group mb-3\" style=\"max-width: 200px;\"><div class=\"input-group-prepend\"><button class=\"btn btn-outline-primary js-btn-minus\" type=\"button\" style=\"visibility:hidden\">&minus;</button></div><input id=\"wineQuantity\" type=\"text\" class=\"form-control text-center border mr-0\" value=\"1\" placeholder=\"\" aria-label=\"Example text with button addon\" aria-describedby=\"button-addon1\"><div class=\"input-group-append\"><button class=\"btn btn-outline-primary js-btn-plus\" type=\"button\" style=\"visibility:hidden\">&plus;</button></div></div></div><p><a href=\"cart.html\" class=\"buy-now btn btn-sm height-auto px-4 py-3 btn-primary\" onclick=\"storeCart()\">Add To Cart</a></p></div>";
		String wineInfo = "<div class=\"container\"><h1>#WINE_NAME_SHORT_DESC#</h1></div>###<div class=\"col-lg-6\"><div class=\"wine_v_1 text-center pb-4\"><img src=\"#WINE_IMG#\" alt=\"Image\" class=\"img-fluid\"></div></div><div class=\"col-lg-5 ml-auto\"><h2 class=\"text-primary\">#WINE_NAME_COST#</h2><p>#WINE_DESCRIPTION#</p><div class=\"mb-5\"><div class=\"input-group mb-3\" style=\"max-width: 200px;\"><div class=\"input-group-prepend\"><button class=\"btn btn-outline-primary js-btn-minus\" type=\"button\" style=\"visibility:hidden\">&minus;</button></div><input id=\"wineQuantity\" data-availableQty=\"#AVAILABLE_QTY#\" data-item=\"#WINE_NAME#\" type=\"text\" class=\"form-control text-center border mr-0\" value=\"1\" placeholder=\"\" aria-label=\"Example text with button addon\" aria-describedby=\"button-addon1\"><div class=\"input-group-append\"><button class=\"btn btn-outline-primary js-btn-plus\" type=\"button\" style=\"visibility:hidden\">&plus;</button></div></div></div><p><a style=\"color: white\" class=\"buy-now btn btn-sm height-auto px-4 py-3 btn-primary\" onclick=\"storeCart()\">Add To Cart</a></p></div>";
		String availableQty = EcommerceDao.getQty(productId);
		
		for (int i = 0; i < keySet.length; i++) {
			
			if ((products.get(keySet[i]))[0].equalsIgnoreCase(productId)){				
				String wineLongDesc = "Dried blueberry, black plum, cassis, wild sage, dried flower, coffee bean, leather.";
				wineInfo = wineInfo.replace("#WINE_NAME_SHORT_DESC#", (products.get(keySet[i]))[1]+"\n"+(products.get(keySet[i]))[4]);
				wineInfo = wineInfo.replace("#WINE_NAME_COST#", (products.get(keySet[i]))[1]+ "\n" + (products.get(keySet[i]))[3]);
				wineInfo = wineInfo.replace("#WINE_DESCRIPTION#", wineLongDesc);
				wineInfo = wineInfo.replace("#PRODUCT_ID#", productId);
				wineInfo = wineInfo.replace("#WINE_IMG#", (products.get(keySet[i]))[2]);
				wineInfo = wineInfo.replace("#AVAILABLE_QTY#", availableQty);
				wineInfo = wineInfo.replace("#WINE_NAME#", (products.get(keySet[i]))[1]);
				
				break;
			}				
		}		
		return wineInfo;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/cart")
	@ResponseBody
	public String getCartDetails(@RequestBody Cart cart) {

		StringBuilder cartInfo = new StringBuilder("<tr id=\"#TR_ID#\"><td class=\"product-thumbnail\"><a href=\"http://localhost:8085/Wines/shop-single.html?productId=#PRODUCT_ID#\" ><img src=\"#WINE_IMG#\" alt=\"Image\" class=\"img-fluid\"></a></td><td class=\"product-name\"><h2 class=\"h5 cart-product-title text-black\"><a href=\"http://localhost:8085/Wines/shop-single.html?productId=#PRODUCT_ID#\">#WINE_NAME#</a></h2></td><td>#WINE_COST#</td><td><input id=\"#WINE_QUANTITY_ID#\" type=\"text\" data-availableQty=\"#AVAILABLE_QTY#\" data-item=\"#WINE_NAME#\" class=\"form-control text-center \" value=\"#WINE_QUANTITY#\" placeholder=\"\"></td><td>#TOTAL_COST#</td><td><input id=\"#REMOVE_BUTTON_ID#\" type=\"button\" value=\"X\" onclick=\"deleteRow(this, this.id)\"></td></tr>");
		StringBuilder totalCostInfo = new StringBuilder("<div class=\"col-md-6\"><div class=\"row mb-5\"><div class=\"col-md-6 mb-3 mb-md-0\"><a style=\"color: white\" class=\"btn btn-primary btn-md btn-block\" onclick=\"updateCart()\">Update Cart</a></div><div class=\"col-md-6\"><a style=\"color: white\" class=\"btn btn-primary btn-md btn-block\" href=\"shop.html\">Continue Shopping</a></div></div><div class=\"row\"></div></div><div class=\"col-md-6 pl-5\" id=\"#CHECKOUT_ID#\"><div class=\"row justify-content-end\"><div class=\"col-md-7\"><div class=\"row\"><div class=\"col-md-12 text-right border-bottom mb-5\"><h3 class=\"text-black h4 text-uppercase\">Cart Totals</h3></div></div><div class=\"row mb-3\"><div class=\"col-md-6\"><span class=\"text-black\">Subtotal</span></div><div class=\"col-md-6 text-right\"><strong class=\"text-black\">#SUB_TOTAL#</strong></div></div><div class=\"row mb-5\"><div class=\"col-md-6\"><span class=\"text-black\">Total</span></div><div class=\"col-md-6 text-right\"><strong id=\"#CART_AMT_ID#\" class=\"text-black\">#CART_TOTAL#</strong></div></div><div class=\"row\"><div class=\"col-md-12\"><a class=\"btn btn-primary btn-lg btn-block\" href=\"checkout.html\" onclick=\"checkout()\">Proceed To Checkout</a></div></div></div></div></div>");
		String finalCart = "";
		String tempCart="";
		String tempCartupdated="";
		//Double cartCost = 0.00;
		//Double tax = 0.00;
		Double subCartTotal = 0.00;
		for (Integer i=0; i<cart.getItem().size(); i++) {
			Item item = cart.getItem().get(i);
			String productId = item.getProductId();
				System.out.println("product ID "+ item.getProductId());	
				
			String [] productInfo = products.get(productId);
			String avilableQty = EcommerceDao.getQty(productId);
			Integer quantity = Integer.parseInt(item.getQuantity());
			System.out.println("product ID price "+ productInfo[3]);
			String price = productInfo[3];
			price = price.substring(1);
			Double cost = Double.valueOf(price);
			subCartTotal = subCartTotal+(cost*quantity);
			String totalCost = Double.toString(quantity*cost);	
								
			String updatedCart = cartInfo.toString().replace("#TR_ID#", "tr"+i.toString());
			updatedCart = updatedCart.replace("#WINE_IMG#", productInfo[2]);
			updatedCart = updatedCart.replace("#WINE_NAME#", productInfo[1]);
			updatedCart = updatedCart.replace("#WINE_COST#", productInfo[3]);
			updatedCart = updatedCart.replace("#WINE_QUANTITY#", item.getQuantity());
			updatedCart = updatedCart.replace("#TOTAL_COST#", "$"+totalCost);
			updatedCart = updatedCart.replace("#REMOVE_BUTTON_ID#", "rb"+i.toString()+productInfo[0]);
			updatedCart = updatedCart.replace("#WINE_QUANTITY_ID#", "wq"+i.toString()+productInfo[0]);
			updatedCart = updatedCart.replace("#AVAILABLE_QTY#", avilableQty);
			updatedCart = updatedCart.replace("#PRODUCT_ID#", productId);
			tempCart = updatedCart+tempCart;
			
		}
		//tax = (7*subCartTotal)/100;
		//cartCost=tax+subCartTotal;
		
		tempCartupdated = totalCostInfo.toString().replace("#SUB_TOTAL#", "$"+Double.toString(subCartTotal));
		//finalCart = finalCart.replace("#TAX_AMT#", "$"+Double.toString(tax));
		tempCartupdated = tempCartupdated.replace("#CART_TOTAL#", "$"+Double.toString(subCartTotal));
		tempCartupdated = tempCartupdated.replace("#CHECKOUT_ID#", "checkoutId");
		tempCartupdated = tempCartupdated.replace("#CART_AMT_ID#", "cartCostId");
		finalCart = finalCart+tempCart+"###"+tempCartupdated;
		return finalCart;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/preCheckout")
	@ResponseBody
	public String getCheckoutOrder(@RequestBody Cart cart) {

		StringBuilder productRow = new StringBuilder("<tr><td>#WINE_NAME#<strong class=\"mx-2\">x</strong>#WINE_QUANTITY#</td><td>#WINE_COST#</td></tr>");
		StringBuilder totalsRow = new StringBuilder("<tr><td class=\"text-black font-weight-bold\"><strong>Cart Subtotal</strong></td><td class=\"text-black\">#CART_SUB_TOTAL#</td></tr><tr><td class=\"text-black font-weight-bold\"><strong>Order Total</strong></td><td class=\"text-black font-weight-bold\"><strong id=\"orderTotal\">#ORDER_TOTAL#</strong></td></tr>");
		String finalProductsRow="";	
		String finalRows="";
		
		Double subCartTotal = 0.00;
		for (Integer i=0; i<cart.getItem().size(); i++) {
			Item item = cart.getItem().get(i);
			String productId = item.getProductId();
						
			String [] productInfo = products.get(productId);			
			Integer quantity = Integer.parseInt(item.getQuantity());
			String price = productInfo[3];
			price = price.substring(1);
			Double cost = Double.valueOf(price);
			subCartTotal = subCartTotal+(cost*quantity);
			String totalCost = Double.toString(quantity*cost);	
								
			String updatedProductsRow = productRow.toString().replace("#WINE_NAME#", productInfo[1]);
			updatedProductsRow = updatedProductsRow.replace("#WINE_QUANTITY#", item.getQuantity());
			updatedProductsRow = updatedProductsRow.replace("#WINE_COST#", totalCost);
			finalProductsRow = finalProductsRow+updatedProductsRow;
		}
		
		String total = totalsRow.toString().replace("#CART_SUB_TOTAL#", "$"+Double.toString(subCartTotal));
		total = total.replace("#ORDER_TOTAL#", "$"+Double.toString(subCartTotal));
		finalRows = finalRows+finalProductsRow+total;
		
		return finalRows;
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/checkout")
	@ResponseBody
	public String getOrderConfirmation(@RequestBody Checkout cart) throws SQLException {
		
		EcommerceDao ec= new EcommerceDao();
		ec.updateCheckoutTables(cart);
						
		return "Thank You!";
	}
	
	
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/products")
	@ResponseBody
	public String getAllProducts() {

		String displayTemp = "<div class=\"col-lg-4 mb-5 col-md-6\"><div class=\"wine_v_1 text-center pb-4\"><a href=\"shop-single.html?productId=#PRODUCT_ID#\" class=\"thumbnail d-block mb-4\"><img src=\"#WINE_IMG#\" alt=\"Image\" class=\"img-fluid\"></a><div><h3 class=\"heading mb-1\"><a href=\"shop-single.html?productId=#PRODUCT_ID#\">#WINE_NAME#</a></h3><span class=\"price\">#WINE_COST#</span></div><div class=\"wine-actions\"><h3 class=\"heading-2\"><a href=\"shop-single.html?productId=#PRODUCT_ID#\">#WINE_SHORT_DESC#</a></h3><span class=\"price d-block\">#WINE_COST#</span><div class=\"rating\"><span class=\"icon-star\"></span><span class=\"icon-star\"></span><span class=\"icon-star\"></span><span class=\"icon-star\"></span><span class=\"icon-star-o\"></span></div><a href=\"shop-single.html?productId=#PRODUCT_ID#\" class=\"btn add\"><span class=\"icon-shopping-bag mr-3\"></span> Add To Cart</a></div></div></div>";

		String finalDisplay = "";
		for (int i = 0; i < keySet.length; i++) {
			String localWine = displayTemp.replace("#WINE_COST#", (products.get(keySet[i]))[3]);
			localWine = localWine.replace("#WINE_NAME#", (products.get(keySet[i]))[1]);
			localWine = localWine.replace("#WINE_IMG#", (products.get(keySet[i]))[2]);
			localWine = localWine.replace("#PRODUCT_ID#", (products.get(keySet[i]))[0]);
			localWine = localWine.replace("#WINE_SHORT_DESC#", (products.get(keySet[i]))[4]);
			finalDisplay = localWine + finalDisplay;
		}
		return finalDisplay;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/service-request")
	@ResponseBody
	public String submitRequest(@RequestBody ServiceRequest req) throws SQLException {

	//logic to insert record in feedback table	
		

		EcommerceDao.updateServiceRequestTable(req);
		
		return "Request submitted successfully!";
	}
	
	
	
	public String sendEmail(String senderMail) {
		try {
			//Scanner to get user input
			String status = "success";
			String sendersmail = senderMail;
		    String host = "smtp.gmail.com";
		 // Sender's email ID 
		    String from = Constants.USERNAME;
		    Properties props = System.getProperties();
		    props.put("mail.smtp.host", host);
		    props.put("mail.smtp.user", from);
		    props.put("mail.smtp.password", Constants.PASSWORD);
		    props.put("mail.smtp.port", "587"); 
		    props.put("mail.smtp.auth", "true");
		    props.put("mail.smtp.starttls.enable", "true");
		    	 // Get the Session object.
		        Session session = Session.getDefaultInstance(props, null);
		        MimeMessage message = new MimeMessage(session);
		        message.setFrom(new InternetAddress(from));
		        message.addRecipients(Message.RecipientType.TO, sendersmail);
		     // Set Subject: header field
		        message.setSubject("Order Confirmation");
		     // Set Content: text field
		        message.setText(Constants.MESSAGE);
		        String filename = "FileToMail.txt";
		        //Attach File: text field
		         DataSource source = new FileDataSource(filename);
		         message.setDataHandler(new DataHandler(source));
		         message.setFileName(filename);
		        Transport transport = session.getTransport("smtp");
		     // Connecting with username and password
		        transport.connect("smtp.gmail.com", Constants.USERNAME, Constants.PASSWORD);
		        status = "success";
		        transport.sendMessage(message, message.getAllRecipients());
		        return status;
		} catch(MessagingException e){
			        System.out.println(e.getMessage());
			        return "Error";
			    }
	}
}
