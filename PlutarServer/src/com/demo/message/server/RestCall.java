/*
 * Copyright (C) 2012 SFR API - Hervé Hoareau

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.demo.message.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * Classe utilisée pour effectuer les appels REST en mode GET ou POST aux API SFR
 * depuis le Google App Engine.
 * 
 * L'utilisation des API SFR est nécessaire pour déposer les messages sur les boites vocale
 * ainsi que d'autres fonctions annexes
 * 
 * L'inscription est gratuite et permet de récupérer un token valide
 * @see http://api.sfr.fr
 * 
 * Remarques
 * - la fonction est largement optimisable
 * - la gestion des exceptions n'est pas faite
 * 
 * @author Hervé Hoareau
 *
 */
public class RestCall  {
	
	//Inscrire dans la variable tokenSFRAPI, le token de votre service récupérer 
	//sur http://api.sfr.fr/ ou directement dans l'API test tool
	//https://api.sfr.fr/index.php?q=apitesttool/
	private String tokenSFRAPI=URLEncoder.encode("<inscrire ici votre token>"); 
	private String ident="token="+tokenSFRAPI+"&reponseType=json";
	
	private static final Logger log = Logger.getLogger(RestCall.class.getName());
	
	//Execution des requetes REST en mode POST si params!=null ou GET si param==null
	//NDA : fonction largement optimisable
	private String ExecuteRest(String urlServer,String params){	
			try {			
				URL url = new URL(urlServer);
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	                        
	            if(params!=null){
	            	log.warning("Requete post="+params);
		            connection.setRequestProperty("Content-Length", ""+params.getBytes().length);
		            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		            connection.setRequestProperty("Content-Language", "en-US"); 
		            connection.setUseCaches(false);
		            connection.setDoOutput(true);
		            connection.setDoInput(true);
		            connection.setRequestMethod("POST");
		            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
		            writer.writeBytes(params);
		            writer.flush();
		            writer.close();
	            } else {
	            	connection.setRequestMethod("GET");
	            }
	            
	            log.warning("Appel de "+url+" avec param="+params);
	            	
	            //Delai de réponse fixé à 30 secondes
	            connection.setReadTimeout(30*1000);
	            connection.connect();

	            //Récuperation de la réponse
		        BufferedReader buffer=new BufferedReader(new InputStreamReader(connection.getInputStream()));
		        String line="";
				StringWriter writer=new StringWriter();
				while ( null!=(line=buffer.readLine()))writer.write(line);
		        
				int reponseCode=connection.getResponseCode();
		        if (reponseCode == HttpURLConnection.HTTP_OK) {     	
					if(reponseCode==200)					
						onSuccess(writer.toString());       		
					else 
						onFailure(reponseCode);
		        }
		        else onFailure(reponseCode);        
	    } 
			catch (MalformedURLException e) {} 
			catch (IOException e) {}	
				
		return null;	
	}    
		  

	//Constructeur
	public RestCall(String url,String params) {				
		if(tokenSFRAPI.length()!=32)
			log.warning("Votre token SFR API est invalide. Rendez-vous sur api.sfr.fr pour en obtenir un valide.");
		else
		{
			ident=ident+"&nonce="+(new Date().getTime()+"84324324324328"+new Date().getTime()).substring(0,32);
			String rep=null;
			if(params==null)
				rep=this.ExecuteRest(url+"&"+ident,params);
			else
				rep=this.ExecuteRest(url,ident+"&"+params);
			
			if(rep!=null)onSuccess(rep);			
		}
	}
	
	//Est appellée quand le serveur retourn 200, méthode doit être surchargée
	public void onSuccess(String rep) {}; 
	
	//Idem que onSuccess pour les cas d'échec
	public void onFailure(int reponseCode) {}	
}
