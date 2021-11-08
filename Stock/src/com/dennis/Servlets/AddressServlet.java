package com.dennis.Servlets;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;


import com.google.gson.Gson;


@WebServlet(name = "PopertyServlet", urlPatterns = "/addresses")
public class AddressServlet extends HttpServlet {
	private static final long  serialVersionUID = 1L;

	private Gson gson = new Gson();
	
    public AddressServlet() {
        // TODO Auto-generated constructor stub
    }

//
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//		String address_id= request.getParameter("address_id");
//		long  addressId = long .parselong (address_id.trim());
//		
//		Address ad=DB.getAddress(addressId);
//		
//		
//		
//		
//        String resultStr = this.gson.toJson(ad);
//        
//        Util.sendResponseToClient( response, resultStr) ;
// 
//      
//		
//	}
//
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		doGet(request, response);
//	}

}
