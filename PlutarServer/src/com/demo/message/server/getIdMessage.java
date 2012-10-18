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
import com.demo.message.shared.Message;

/**
 * Servlet de récupération de l'id du message dépose par loadmessage
 * après le processus d'encodage. Cet id est inscrit dans la base
 * et sera utilisé par pushMessage pour envoyer le message sur plusieur
 * boites vocales
 * 
 * ce web service est donc appellé par le serveur SFR API
 * son adresse ainsi qu'un identifiant interne du message à déposer 
 * a été fourni à SFR API via la méthode loadmessage (voir servlet postMessage)
 * 
 * @see https://api.sfr.fr/api pour les explications sur le fonctionnement du dépot de message
 *  
 * @author SFR API - Hervé Hoareau
 */
@SuppressWarnings("serial")
public class getIdMessage extends BaseServlet {
	
	public void doPost(HttpServletRequest req, final HttpServletResponse resp) throws IOException {	
	
		String idMessage=req.getParameter("messageId"); //Id du message à utiliser par PushMessage
		String id=req.getParameter("Id");				//Id pour le stockage du message en interne
		String status=req.getParameter("status"); 		//Status de l'encodage : FAULT ou ENCODED
		String error=req.getParameter("error");   		//Information complémentaire sur le codage
		
		//On retrouve le message concerné par ce traitement, grace à l'identifiant
		//passé dans l'url de callback : paramètre id
		Message m=null;
		if(id!=null)m=dao.ofy().get(Message.class,Long.parseLong(id));

		//On enrichi le message avec l'identifiant interne de SFR API.
		//cet identifiant sera utilisé pour envoyer le message sur les boites vocales
		//via la méthode pushMessage
		if(m!=null){
			m.setIdMessage(idMessage);
			//On enregistre le message enrichie (avec l'identifiant SFR) dans
			//la BDD de Google
			dao.ofy().put(m);
		}
		
		resp.setContentType("text/plain");
		resp.getWriter().print("IdMessage a jour");
	}
}
