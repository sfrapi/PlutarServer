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


/**
 * Servlet permettant de v�rifier un lot de num�ros de t�l�phone compatible avec le d�pot de message
 * 
 * @author Herv� Hoareau
 */
@SuppressWarnings("serial")
public class sfrEvent extends BaseServlet {

	public void doPost(HttpServletRequest req, final HttpServletResponse resp) throws IOException {	
		
		String number=req.getParameter("userIdentifier");
		String rc="";
		
		dao.PostMessage(dao.getMessagesToPost(null, number));
	
		resp.setContentType("text/plain");
		resp.getWriter().print(rc);
	}
}
