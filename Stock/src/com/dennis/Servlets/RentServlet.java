package com.dennis.Servlets;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dennis.db.DB;

import com.dennis.models.Rent;
import com.dennis.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@WebServlet(name = "RentServlet", urlPatterns = "/rents")
public class RentServlet extends HttpServlet {

	private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
        }
    }).create();
	
	public RentServlet() {
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		
		

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		Rent r = gson.fromJson(request.getReader(), Rent.class);
		
		if(r.paid_by_individual_id!=0) {
			if(Rent.insertRentHistory(Util.getUserInSession(request),r)>1) {
				List results=Rent.getRentHistory(Util.getUserInSession(request),r.apartment_id);
				Util.sendResponseToClient(response, gson.toJson(results));
			}
		}
		

	}

}
