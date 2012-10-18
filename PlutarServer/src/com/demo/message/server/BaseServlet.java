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

import java.util.Date;

import javax.servlet.http.HttpServlet;
import com.demo.message.shared.DAO;

//Cette classe permet de partager des méthodes entre plusieurs classes filles
public class BaseServlet extends HttpServlet  {
	private static final long serialVersionUID = 1464654657498786L;
	protected static DAO dao=new DAO();
	protected static final String SERVER_MESSAGE="http://plutarserver.appspot.com/api";
	
	//le nonce est un mecanisme de sécurité utilisé par certaines API SFR : loadMessage et PushMessage
	//Le nonce doit être un token unique sur 32 caractères numériques
	protected String getNonce(){
		return((new Date().getTime())+"00000000000000000000").substring(0,32);
	}
}

