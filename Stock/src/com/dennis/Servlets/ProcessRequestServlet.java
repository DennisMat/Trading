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

import com.dennis.models.Address;
import com.dennis.models.Apartment;
import com.dennis.models.ApartmentOccupant;
import com.dennis.models.FloorPlan;
import com.dennis.models.History;
import com.dennis.models.Individual;
import com.dennis.models.Lease;
import com.dennis.models.Parking;
import com.dennis.models.Property;
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

		downloadFile(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Map<String, String> headers = Util.getHeaders(request);

		String action = headers.get("action");

		authMethods(request, response, headers, action);

		exportData(request, response, headers, action);

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

				Individual.insertUpdateUserRecord(0, null, ind);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void exportData(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {
		try {

			if (action.equals("export_data")) {
				byte[] contents = Util.exportData(Util.getUserInSession(request));

				response.setContentType("application/zip");
				response.addHeader("Content-Disposition", "attachment; filename=" + "ExportedData.zip");
				response.setContentLength((int) contents.length);

				OutputStream out = response.getOutputStream();

				out.write(contents, 0, contents.length);

				out.flush();
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

	void leaseMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {
		try {
			if (action.equals("get_lease")) {
				int days = Util.getIntValue(headers, "days");
				if (days == 0 & action.equals("getLease")) {
					long apartment_id = Util.getLongValue(headers, "apartment_id");
					Util.sendResponseToClient(response,
							gson.toJson(Lease.getLeases(Util.getUserInSession(request), apartment_id)));

				} else if (days != 0 && action.equals("getLease")) {

					Util.sendResponseToClient(response,
							gson.toJson(Lease.getExpiringLeases(Util.getUserInSession(request), days)));

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void historyMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {
		try {

			if (action.equals("get_history")) {
				Util.sendResponseToClient(response, gson.toJson(History.getHistory(Util.getUserInSession(request))));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void propertyMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {
		try {

			if (action.equals("get_property")) {

				long userId = Util.getUserInSession(request);
				String resultStr = gson.toJson(Property.getProperties(userId));
				Util.sendResponseToClient(response, resultStr);

			}

			if (action.equals("post_property")) {
				JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);

				Property p = gson.fromJson(jsonObject.get("property"), Property.class);
				Address a = gson.fromJson(jsonObject.get("address"), Address.class);

				if (Property.insertUpdatePropertyRecord(Util.getUserInSession(request), p, a) > 0) {
					long userId = Util.getUserInSession(request);
					String resultStr = gson.toJson(Property.getProperties(userId));
					Util.sendResponseToClient(response, resultStr);
				}
			}

			if (action.equals("change_property_status")) {
				JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);

				Property p = gson.fromJson(jsonObject.get("property"), Property.class);
				Address a = gson.fromJson(jsonObject.get("address"), Address.class);

				if (Property.changeActiveStatus(Util.getUserInSession(request), p, a) > 0) {
					Util.sendResponseToClient(response, "{\"change_property_status\":\" success\"}");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static String extractPostRequestBody(HttpServletRequest request) throws IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
		return "";
	}

	void apartmentMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {
		try {

			if (action.equals("get_apartment")) {
				long property_id = Util.getLongValue(headers, "property_id");
				long apartment_id = Util.getLongValue(headers, "apartment_id");
				String flag = headers.get("flag");
				long userId = Util.getUserInSession(request);
				String resultStr =null;
				
				if (property_id != 0 && apartment_id == 0) {
					 resultStr = gson.toJson(Apartment.getApartments(userId, property_id));
					
				} else if (apartment_id != 0) {
					if(flag!=null && flag.equals("get_details")){
						resultStr = gson.toJson(Apartment.getApartmentDetails(userId, apartment_id,false));
					}else {
						resultStr = gson.toJson(Apartment.getApartmentDetails(userId, apartment_id,true));
					}
					
					
				}
				Util.sendResponseToClient(response, resultStr);
			}

			if (action.equals("post_apartment")) {

				Apartment a = gson.fromJson(request.getReader(), Apartment.class);

				Apartment.insertUpdateApartmentRecord(Util.getUserInSession(request), a);
				String resultStr = gson.toJson(Apartment.getApartments(Util.getUserInSession(request), a.property_id));
				Util.sendResponseToClient(response, resultStr);
			}
			
			
			if (action.equals("change_apartment_status")) {
				
				Apartment a = gson.fromJson(request.getReader(), Apartment.class);

				if (Apartment.changeActiveStatus(Util.getUserInSession(request), a) > 0) {
					Util.sendResponseToClient(response, "{\"change_apartment_status\":\" success\"}");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void individualMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {
		try {

			long userId = Util.getUserInSession(request);
			String type = headers.get("user_type");
			if (action.equals("get_individuals")) {
				List l = null;

				if (type != null && type.equals("employee")) {
					l = Individual.getFellowEmployees(userId);
				} else if (type != null && type.equals("occupants")) {
					l = Individual.getOccupants(userId);

				}

				String individualsJsonString = gson.toJson(l);

				Util.sendResponseToClient(response, individualsJsonString);
			}

			if (action.equals("post_individuals")) {

				JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
				ApartmentOccupant apartmentOccupant = null;
				try {
					apartmentOccupant = gson.fromJson(jsonObject.get("apartmentoccupant"), ApartmentOccupant.class);
				} catch (Exception e) {
				}
				Individual individual = gson.fromJson(jsonObject.get("individual"), Individual.class);

				if (!individual.first_name.isEmpty()) {
					individual.individual_id=Individual.insertUpdateUserRecord(Util.getUserInSession(request), apartmentOccupant, individual);
				}

				if(individual.individual_id>0) {
				List l = null;

				if (apartmentOccupant == null && type != null && type.equals("employee")) {
					l = Individual.getFellowEmployees(userId);
				} else if (apartmentOccupant == null && type != null && type.equals("occupants")) {
					l = Individual.getOccupants(userId);
				} else {
					l = Individual.getOccupants(Util.getUserInSession(request), apartmentOccupant.apartment_id, false);

				}

				Util.sendResponseToClient(response, gson.toJson(l));
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void floorPlanMethods(HttpServletRequest request, HttpServletResponse response, Map<String, String> headers,
			String action) {
		try {

			long userId = Util.getUserInSession(request);
			String floor_plan_name1 = request.getParameter("floor_plan_name");
			if (action.equals("get_floorplan")) {
				long property_id = Util.getLongValue(headers, "property_id");
				List<FloorPlan> l = FloorPlan.getFloorPlans(userId, property_id);
				String resultStr = this.gson.toJson(l);
				Util.sendResponseToClient(response, resultStr);
			}

			if (action.equals("post_floorplan")) {
				String floor_plan_name = request.getParameter("floor_plan_name");
				String notes = request.getParameter("notes");

				long property_id = Util.getLongValue(request, "property_id");
				long floor_plan_id = Util.getLongValue(request, "floor_plan_id");
				float area = Util.getFloatValue(request, "area");
				int area_unit_of_measure_id = Util.getIntValue(request, "area_unit_of_measure_id");

				String image_path = null;
				if (FloorPlan.hasImageInRequest(request)) {		
					Part part =request.getPart("floor_plan_image_upload");
					FloorPlan fp = new FloorPlan(floor_plan_id, property_id, floor_plan_name, area, area_unit_of_measure_id,
							notes, image_path);

					floor_plan_id = FloorPlan.insertOrUpdateFloorPlan(userId, property_id, fp);
					
					
					String image_name = "floorplan_" + String.format("%05d", property_id) + "_"
							+ String.format("%05d", floor_plan_id);
					
					String relativeImagePath= FloorPlan.imagefolder + File.separator + image_name+FloorPlan.getFileExtension(part);
					 FloorPlan.saveImage(request, relativeImagePath);
					 
					 fp.floor_plan_id=floor_plan_id;
					 fp.image_path=relativeImagePath.replace(File.separator, "/");
					 
					//we need the floor_plan_id to name the image, hence the second update
					 FloorPlan.insertOrUpdateFloorPlan(userId, property_id, fp);
					 
					 
				}
				

				Util.sendResponseToClient(response, "{\"floor_plan_id\":" + floor_plan_id + "}");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	void downloadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		byte[] contents = Util.exportData(Util.getUserInSession(request));

		response.setContentType("application/zip");
		response.addHeader("Content-Disposition", "attachment; filename=" + "ExportedData.zip");

		response.setContentLength(contents.length);

		// obtains response's output stream
		OutputStream outStream = response.getOutputStream();

		outStream.write(contents, 0, contents.length);

		// inStream.close();
		outStream.close();
	}

}
