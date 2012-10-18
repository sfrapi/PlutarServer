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
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Ce servlet propose une liste de lien vers des fichiers MP3 identifié sur internet
 * il peut être pertinent de l'enrichir d'un point de vue contenu mais également sur le plan
 * fonctionnel en offrant par exemple la possibilité d'acheter des messages pré-enregistré
 * 
 * @author Hervé Hoareau
 */
@SuppressWarnings("serial")
public class getMP3 extends BaseServlet {
	
	public void doGet(HttpServletRequest req, final HttpServletResponse resp) throws IOException {	
		resp.setContentType("text/plain");
		ArrayList<String> lc=new ArrayList<String>();
		lc.add("Demo,https://docs.google.com/open?id=0B7Cd0zE6etFgcENfYlpEd2VjNTA");
		lc.add("Tribal mix,http://sonneries.gratos.free.fr/divers/Tribal-mix.mp3");
		lc.add("Brice,http://sonneries.gratos.free.fr/humour/Tu_Decroches_Ou_Je_Te_Casse.mp3");
		lc.add("Funk,http://sonneries.gratos.free.fr/funk/70sfunk%20demo.mp3");
		lc.add("Drum and bass,http://sonneries.gratos.free.fr/sonnerie%20electro/drum-and-Bass.mp3");
		lc.add("Minimal techno,http://sonneries.gratos.free.fr/sonnerie%20electro/07_ring_my_minimal_techno.mp3");		
		
		resp.getWriter().print(new ObjectMapper().writeValueAsString(lc));
	}
}
