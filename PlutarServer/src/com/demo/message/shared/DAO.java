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

package com.demo.message.shared;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.demo.message.server.RestCall;
import com.demo.message.server.checkSFR;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;


/**
 * Cette classe regroupe l'ensemble des méthodes utilisées
 * par les servlet du serveur pour manipuler la base de données de google
 * @see http://code.google.com/p/objectify-appengine/wiki/BestPractices#Utilisez_un_DAO
 *  
 * @author Hervé Hoareau
 */
public class DAO extends DAOBase {	
	private static final Logger log = Logger.getLogger(checkSFR.class.getName());
	
	static {ObjectifyService.register(Message.class);}

	/**
	 * Méthode permettant l'effacement de tout les messages postés pour un device donné
	 * 
	 * @param device contient l'identifiant pour lequel les messages doivent être supprimé, si null tous les messages de la base sont supprimés
	 * @return le nombre de message effectivement supprimés
	 */
	public Integer raz(String device) {
		Query<Message> im=null;
		if(device==null){
			im=ofy().query(Message.class);
		}else{
			im=ofy().query(Message.class).filter("device", device);
		}
		int rc=im.count();
		ofy().delete(im);
		return(rc);
	}
	
	/**
	 * Calcul le nombre de message posté depuis les dernières x heures
	 */
	public Integer getNbMessage(String deviceid,Integer nbHours){
		Long dateLimite=new Date().getTime()-nbHours*60*1000*60;
		return(ofy().query(Message.class).filter("device", deviceid).filter("sendDate >",dateLimite).count());
	}
	

	/**
	 * Retourne la liste des messages à poster
	 * Le principe consiste à analyser la base des messages à envoyer et extraire tous ceux dont
	 * la date de dépot programmé et dépasser et dont le champs "result" est vide, ce qui indique 
	 * que le dépot n'a pas encore eu lieu.
	 * 
	 * @param idMessage si idMessage est non null seul le message de l'id est ajouté à la liste
	 * @return la liste des messages à poster
	 */
	public List<Message> getMessagesToPost(String idMessage,String number){
		Long now=new Date().getTime();
		List<Message> lm=null;
		if(idMessage==null){
			if(number==null)
				lm=ofy().query(Message.class).filter("sendDate <", now).filter("resultat",null).list();
			else
				lm=ofy().query(Message.class).filter("sendDate <", now).filter("resultat",null).list();
		}else{
			lm=new ArrayList<Message>();
			lm.add(ofy().get(Message.class,Long.parseLong(idMessage)));
		}
		return lm;	
	}
	
	
	
	

	/**
	 * Expédition via l'API SFR pushMessage, de l'ensemble des messages contenu dans la liste
	 * passée en paramètre
	 *	 
	 * @param lm contient la liste des messages à poster compte tenu de la date @see function getMessagesToPost 
	 */
	public void PostMessage(List<Message> lm) {
		if(lm!=null){		
			for(int k=0;k<lm.size();k++){
				final Message m=lm.get(k);
				if(m.getIdMessage()!=null){
					for(final String dest:(m.getDestinataires()+";").split(";"))
						if(dest.length()==10){
							
							String params="userIdentifier="+dest+"&type=PhoneNumber&messageId="+m.getIdMessage();
							String url="https://ws.red.sfr.fr/red-ws/red-b2c/resources/depotmsg/push?"+params;
							
							//Appel de la méthode PushMessage pour déposer les messages
							//sur les boites vocales SFR
							//Si le consentement n'a pas été obtenu, le dépot n'a pas lieu
							new RestCall(url,null){
								@Override public void onSuccess(String rep){
									if(rep.contains("\"ok\"")){
										//On supprime le destinataire pour lesquel le message a marché
										m.setDestinataires(m.getDestinataires().replace(dest, "").replace(",,",","));
										
										if(m.getDestinataires().length()<=1){
											//S'il ne reste plus de destinataire pour le message
											m.setResultat(m.getResultat()+rep+";");	
										}
									
										//les champs destinataires et résultat ayant été modifié
										//on enregistre le message dans la base de données
										ofy().put(m);										
									} 
									else log.warning("PushMessage failure"+rep);
								}
							};
						}
				}
			}
		}
	}
}
