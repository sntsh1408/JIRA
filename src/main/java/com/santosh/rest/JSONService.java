package com.santosh.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Path("/json/product")
public class JSONService {

	@POST
	@Path("/post")
	@Consumes("application/json")
	@LogJiraError(enabled = false)
	public Response getProductInJSON(String json) {

		JSONParser parser = new JSONParser(); 
		try {
			System.out.println("Started...!");
			JSONObject jsonObject = (JSONObject) parser.parse(json);
			int a = Integer.parseInt((String)jsonObject.get("value1"));
			int b = Integer.parseInt((String)jsonObject.get("value2"));
			int c = a/b;
			String result = "Answer is : " + c;
			System.out.println("Returning...!");
			return Response.status(200).entity(result).build();
		} catch (Exception e) {
			try {
				System.out.println("Start Logging error in jira...!");
				JiraErrorLoggingUtil.logError(e.toString());
			} catch (Exception e1) {
				System.out.println("Jira Logging Exception...!" +  e1.getMessage());
				e1.printStackTrace();
			}
			System.out.println("Logging Exception...!" + e.getMessage());
			e.printStackTrace();
		} 

		return Response.status(200).entity("Error Occured!").build();
	}

	@GET
	@Path("/get/{param}")
	
	public Response createProductInJSON(@PathParam("param") String msg) {

		String result = "You Typed : " + msg;
		return Response.status(201).entity(result).build();
		
	}
	
}