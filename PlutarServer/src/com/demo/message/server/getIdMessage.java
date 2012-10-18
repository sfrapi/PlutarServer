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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.demo.message.shared.Message;

/**
 * Servlet de r�cup�ration de l'id du message d�pose par loadmessage
 * apr�s le processus d'encodage. Cet id est inscrit dans la base
 * et sera utilis� par pushMessage pour envoyer le message sur plusieur
 * boites vocales
 * 
 * ce web service est donc appell� par le serveur SFR API
 * son adresse ainsi qu'un identifiant interne du message � d�poser 
 * a �t� fourni � SFR API via la m�thode loadmessage (voir servlet postMessage)
 * 
 * @see https://api.sfr.fr/api pour les explications sur le fonctionnement du d�pot de message
 *  
 * @author SFR API - Herv� Hoareau
 */
@SuppressWarnings("serial")
public class getIdMessage extends BaseServlet {
	
	public void doPost(HttpServletRequest req, final HttpServletResponse resp) throws IOException {	
	
		String idMessage=req.getParameter("messageId"); //Id du message � utiliser par PushMessage
		String id=req.getParameter("Id");				//Id pour le stockage du message en interne
		String status=req.getParameter("status"); 		//Status de l'encodage : FAULT ou ENCODED
		String error=req.getParameter("error");   		//Information compl�mentaire sur le codage
		
		//On retrouve le message concern� par ce traitement, grace � l'identifiant
		//pass� dans l'url de callback : param�tre id
		Message m=null;
		if(id!=null)m=dao.ofy().get(Message.class,Long.parseLong(id));

		//On enrichi le message avec l'identifiant interne de SFR API.
		//cet identifiant sera utilis� pour envoyer le message sur les boites vocales
		//via la m�thode pushMessage
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
