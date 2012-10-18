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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet permettant de vérifier la compatibilité avec le service de dépot de messages un lot de numéros de téléphone
 * Test par https://votre_domaine/googlemessage/checksfr?numbers=0612345678,0612345679
 * 
 * @see https://api.sfr.fr/api
 * @author Hervé Hoareau
 */
//compatible avec le dépot de message
//
@SuppressWarnings("serial")
public class checkSFR extends BaseServlet {

	private String incompatibles="";	//Liste des numéros analysés comme incompatibles avec le service
	private String compatibles="";		//Liste des numéros analysés comme compatibles avec le service
	
	public void doGet(HttpServletRequest req, final HttpServletResponse resp) throws IOException {	
		
		incompatibles="";
		compatibles="";
		
		//numbers contient la liste des numéros à tester séparés par ","
		String numbers=req.getParameter("numbers");
		if(numbers!=null)numbers=numbers.replace(",",";");
		
		for(final String number:numbers.split(";")){
			new RestCall("https://ws.red.sfr.fr/red-ws/red-b2c/resources/depotmsg/isSFRVoiceMail?responseType=json&userIdentifier="+number+"&type=PhoneNumber",null){
				@Override public void onSuccess(String rep){
					if(rep.contains("\"ko\""))
						incompatibles+=number+",";
					else
						compatibles+=number+",";
				}
			};
		}
		
		if(incompatibles.length()>0)incompatibles=incompatibles.substring(0, incompatibles.length()-1);
		if(compatibles.length()>0)compatibles=compatibles.substring(0, compatibles.length()-1);
	
		resp.setContentType("text/plain");
		
		//On retourne la liste des numéros compatibles et incompatibles au format compatibles=<...>&incompatibles=<...>
		resp.getWriter().print("compatibles="+compatibles+"&incompatibles="+incompatibles);
	}
}
