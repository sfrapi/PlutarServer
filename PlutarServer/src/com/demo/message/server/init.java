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
import java.net.URLEncoder;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//Servlet permettant de vérifier un lot de numéros de téléphone
//compatible avec le dépot de message
@SuppressWarnings("serial")
public class init extends BaseServlet {
	private static final Logger log = Logger.getLogger(init.class.getName());

	public void doGet(HttpServletRequest req, final HttpServletResponse resp) throws IOException {	
		
		String rc="";
		String operation=req.getParameter("operation");
		
		if(operation.contains("raz"))dao.raz(null);
		
		String urlEvent=URLEncoder.encode(SERVER_MESSAGE+"/sfrevent");

		//Positionne le service de notification des consentements
		new RestCall("https://ws.red.sfr.fr/red-ws/red-b2c/resources/event/setAgrSubscription?responseType=json&pushUrl="+urlEvent,null);
		
		resp.setContentType("text/plain");
		resp.getWriter().print(rc);
	}
}
