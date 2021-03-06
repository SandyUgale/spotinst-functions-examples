package com.serverless;

import com.google.gson.JsonObject;
import com.spotinst.functions.runtime.Context;
import com.spotinst.functions.runtime.Request;
import com.spotinst.functions.runtime.RequestHandler;
import com.spotinst.functions.runtime.Response;

import java.util.HashMap;
import java.util.Map;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Handler implements RequestHandler {
    @Override
    public Response handleRequest(Request request, Context context) {
        String group = System.getenv().get("spotGroup"); 
        String token = System.getenv().get("spotToken"); 
        String account = System.getenv().get("spotAccount");
        String adjustment = System.getenv().get("adjustment");
        String output = new String();
        int outputStatus = 0;

        try{
            URL url = new URL("https://api.spotinst.io/aws/ec2/group/"+group+"/scale/up?adjustment="+adjustment);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();       
        
            con.setRequestMethod("PUT");

            Map<String, String> parameters = new HashMap<>();
            parameters.put("accountId", account);

            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer "+token); 

            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(
              new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine + "\n");
            }

            System.out.print(content);
            in.close();

            con.disconnect();

            output = "Success";
            outputStatus = 200;

        }catch(MalformedURLException err){
            System.out.print(err);
            output = "Error";
            outputStatus = 400;
        }catch(IOException err){
            System.out.print(err);
            output = "Error";
            outputStatus = 400;
        }

        Response response = new Response(outputStatus, output);

        //Build response headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        response.setHeaders(headers);

        return response;
    }
}