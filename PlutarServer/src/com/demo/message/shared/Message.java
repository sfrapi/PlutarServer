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

package com.demo.message.shared;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;


/**
 * Structure des messages programm�s pour la BDD de google
 * On s'appuit sur le framework Objectify
 * 
 * @see http://code.google.com/p/objectify-appengine/wiki/Concepts
 * @author Herv� Hoareau
 *
 */
@Unindexed 
public class Message {
	private static final long serialVersionUID = -5352407197990187064L;
	
	@Id public Long Id; 					//Id interne des messages
	public String file=null;				//Contenu des messages encod� par l'appli Android en Base64
	@Indexed public String idMessage=null;	//Identifiant des messages interne � SFR API
	public String destinataires=null;		//liste des destinataires avec ";" en s�parateur		
	@Indexed public String device=null; 	//Id du device ayant post� les messages
	@Indexed public Long sendDate=null; 	//Date en format TIMESTAMP d'exp�dition des messages
	@Indexed public String resultat=null;	//Resultat de l'exp�dition

	
	public Message() {};  //Construteur vide n�cessaire au fonctionnement d'Objectify
	
	public Message(String file,String dest,Long date,String device){
		this.setFile(file);
		this.setDestinataires(dest);
		this.setSendDate(date);
		this.setDevice(device);
		this.setResultat(null);
	}
	
	//Getters et Setters
	public String getResultat() {return resultat;}
	public void setResultat(String resultat) {this.resultat = resultat;}
	public String getDevice() {return device;}
	public void setDevice(String device) {this.device = device;}
	public String getIdMessage() {return idMessage;}
	public void setIdMessage(String idMessage) {this.idMessage = idMessage;}
	public String getFile() {return file;}
	public void setFile(String file) {this.file = file;}
	public String getDestinataires() {return destinataires;}
	public void setDestinataires(String destinataires) {this.destinataires = destinataires;}
	public Long getSendDate() {return sendDate;}
	public void setSendDate(Long sendDate) {this.sendDate = sendDate;}

}
