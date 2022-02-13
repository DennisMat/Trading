package com.dennis.Servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dennis.models.User;
import com.dennis.models.Stock;
import com.dennis.models.Trade;
import com.dennis.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@MultipartConfig
@WebServlet(name = "ProcessRequestServlet", urlPatterns = "/processrequest")
public class ProcessRequestServlet extends HttpServlet {

	private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
		@Override
		public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
		}

	}).create();

	public ProcessRequestServlet() {
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Util.sendResponseToClient(response, "A gaggle of morons!");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Map<String, String> headers = Util.getHeaders(request);

		String action = headers.get("action");

		authMethods(request, response, headers, action);

		transactionMethods(request, response, headers, action);

	}// end of do post

	void authMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers, String action) {

		String user_name = headers.get("user_name").trim();
		String user_password = headers.get("user_password").trim();

		try {
			if (action.equals("login")) {

				boolean isloggedIn = User.verifyUser(user_name, user_password);

				Util.sendResponseToClient(response, "{\"logged_in\":" + isloggedIn + "}");

				HttpSession session = request.getSession();

				session.setAttribute("user_name", user_name);

			} else if (action.equals("logout")) {

				HttpSession session = request.getSession();
				session.invalidate();
				Util.sendResponseToClient(response, "{\"logged_out\":true}");

			} else if (action.equals("createUser")) {

				User ind = new User(0, user_name, "", "", "", "", user_password, "");

				if (User.checkExistingUserName(ind)) {
					Util.sendResponseToClient(response, "{\"user_created\":\"user_name_exists\"}");
				} else if (User.insertUpdateUserRecord(ind) > 0) {
					Util.sendResponseToClient(response, "{\"user_created\":true}");
				} else {
					Util.sendResponseToClient(response, "{\"user_created\":false}");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void transactionMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers, String action) {
		long userId = Util.getUserInSession(request);
		try {
			if (action.equals("get_stock_data")) {
				Map results = Trade.getLines(1);
				Util.sendResponseToClient(response, gson.toJson(results));
			}
			if (action.equals("insert_trade")) {
				Trade t = gson.fromJson(request.getReader(), Trade.class);
				if (Trade.insertUpdateTradeRecord(userId, t) > 0) {
					Map results = Trade.getLines(userId);
					Util.sendResponseToClient(response, gson.toJson(results));
				}
			}
			if (action.equals("delete_trade")) {
				Trade t = gson.fromJson(request.getReader(), Trade.class);
				if (Trade.updateTradeRecord(userId, t) > 0) {
					Map results = Trade.getLines(userId);
					Util.sendResponseToClient(response, gson.toJson(results));
				}
			}

			if (action.equals("trade_advice")) {
				Trade t = gson.fromJson(request.getReader(), Trade.class);
				Map results = Trade.generateTradeAdvice(userId, t);
				Util.sendResponseToClient(response, gson.toJson(results));
			}

			if (action.equals("get_stock")) {
				List results = Stock.getStocks(userId);
				Util.sendResponseToClient(response, gson.toJson(results));
			}

			if (action.equals("post_stock")) {
				Stock s = gson.fromJson(request.getReader(), Stock.class);
				if (Stock.insertUpdateStockRecord(userId, s) > 0) {
					List results = Stock.getStocks(userId);
					Util.sendResponseToClient(response, gson.toJson(results));
				}

			}

			if (action.equals("change_stock_status")) {
				Stock s = gson.fromJson(request.getReader(), Stock.class);
				if (Stock.changeActiveStatus(Util.getUserInSession(request), s) > 0) {
					Util.sendResponseToClient(response, "{\"change_status_status\":\" success\"}");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
