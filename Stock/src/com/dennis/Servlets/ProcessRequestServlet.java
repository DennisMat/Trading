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
import javax.servlet.http.Part;

import com.dennis.models.Individual;
import com.dennis.models.Parking;
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

	private static final Gson gson = new GsonBuilder()
			.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
				@Override
				public LocalDate deserialize(JsonElement json, Type type,
						JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
					return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
				}
			}).create();

	public ProcessRequestServlet() {
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Map<String, String> headers = Util.getHeaders(request);

		String action = headers.get("action");

		authMethods(request, response, headers, action);



		parkingMethods(request, response, headers, action);



	}// end of do post

	void authMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {

		String user_name = headers.get("user_name");
		String user_password = headers.get("user_password");

		try {
			if (action.equals("login")) {

				boolean isloggedIn = Individual.verifyUser(user_name, user_password);

				Util.sendResponseToClient(response, "{\"logged_in\":" + isloggedIn + "}");

				HttpSession session = request.getSession();

				session.setAttribute("user_name", user_name);

			} else if (action.equals("logout")) {

				HttpSession session = request.getSession();
				session.invalidate();
				Util.sendResponseToClient(response, "{\"logged_out\":true}");

			} else if (action.equals("createUser")) {

				Individual ind = new Individual(0, "", "", "", "");
				ind.user_name = user_name;
				ind.password = user_password;

				Individual.insertUpdateUserRecord(0, ind);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	void parkingMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {
		try {

			if (action.equals("get_stock_data")) {
				

				Map results = Parking.getLines();
				Util.sendResponseToClient(response, gson.toJson(results));

			}





		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
