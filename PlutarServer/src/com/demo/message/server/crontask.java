/*
 * Copyright (C) 2012 SFR API - Herv� Hoareau

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
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.demo.message.shared.Message;


/**
 * Servlet s'ex�cutant lorsqu'il est n�cessaire de d�poser un message 
 * Le principe consiste � analyser la base des messages � envoyer et extraire tous ceux dont
 * la date de d�pot programm� et d�passer et dont le champs "result" est vide, ce qui indique 
 * que le d�pot n'a pas encore eu lieu.
 * 
 * l'algorithme est embarqu� dans la fonction postMessage de l'objet dao
 * 
 * @author Herv� Hoareau
 */
@SuppressWarnings("serial")
public class crontask extends BaseServlet {
	
	private static final Logger log = Logger.getLogger("appliMessage");
	
	public void doPost(HttpServletRequest req, final HttpServletResponse resp) throws IOException {	
		resp.setContentType("text/plain");
		
		String idMessage=req.getParameter("id");
		List<Message> lm=dao.getMessagesToPost(idMessage,null);
		
		log.warning("Message a traiter : "+lm.size());
		
		dao.PostMessage(lm);
		
		resp.getWriter().print("");
	}
}
