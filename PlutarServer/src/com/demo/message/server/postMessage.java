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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.demo.message.shared.Message;
import com.google.api.client.util.Base64;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.Queue;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet principale permettant la r�cup�ration des fichiers son � d�poser sur
 * les boites vocales
 * 
 * @author Herv� Hoareau
 * 
 */ 
@SuppressWarnings("serial")
public class postMessage extends BaseServlet {
	
	private static final Logger log = Logger.getLogger(postMessage.class.getName());
	private static final Integer NB_MESSAGES_PARJOUR = 3;
	
	private String rc="";
	private Queue queue = QueueFactory.getDefaultQueue();
	
	/**
	 * Recup�re le fichier d'une URL donn�e sous forme d'un tableau de bytes
	 * 
	 * @param sUrl contient l'url du fichier � r�cup�rer
	 * @return le fichier sous forme binaire dans un tableau de byte
	 */
	public static byte[] getBytesFromUrl(String sUrl)  {
		 URL url;
		try {
			url = new URL(sUrl);
		
	     url.openConnection();
	     InputStream is = url.openStream();	 

	    // Create the byte array to hold the data
	    byte[] bytes = new byte[400000];

	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    // Close the input stream and return bytes
	    is.close();
	    return Arrays.copyOf(bytes, offset-1);
	    
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
	
	
	/**
	 * Fonction principale
	 * cette classe fait appel � de nombreuses API SFR :
	 * - attribution de num�ro court SFR
	 * - demande de consentement pour le d�pot de message
	 * - 
	 * il est recommend� de consulter la page SFR API 
	 * 
	 * @see https://api.sfr.fr/api
	 * @author Herv� Hoareau
	 */
	public void doPost(HttpServletRequest req, final HttpServletResponse resp) throws IOException {	
		
		String file=req.getParameter("messageFile");//contient le fichier encoder en binary64
		
		//il est possible de recevoir un lien web vers un fichier
		//dans ce cas, c'est le serveur qui se charge de r�cup�rer le
		//fichier avant de la d�poser
		String filename=req.getParameter("filename");
		if(filename==null)filename=req.getParameter("urlFile"); 
		
		String fileformat=req.getParameter("fileformat"); //Contient le format du fichier via son extension
		String numbers=req.getParameter("userIdentifier");//Contient la liste des destinataires
		final String date=req.getParameter("date");		  //Date de d�pot du message sous forme de TimeStamp
		Long sendDate=0L;
		
		String device=req.getParameter("device"); //Identifiant de l'�meteur
		
		Message m=null;		
		
		//Si le device est null ou browser cela signifie que l'�meteur n'est
		//pas l'application Android Plutar mais un formulaire web
		if(device==null || device.equals("browser")){
			
			//L'emeteur n'�tant pas un mobile, on lui substitue sont
			//adresse ip comme identifiant
			device=req.getRemoteAddr();
			
			//Recup�ration d'un fichier envoy� depuis le formulaire
			//d'une application web (usage de l'HTML5)
			ServletFileUpload upload = new ServletFileUpload(); 
			FileItemIterator iterator;
			byte[] data = new byte[100000];
				try {
					iterator = upload.getItemIterator(req);
				 
					while (iterator.hasNext()) {
						FileItemStream item = iterator.next();
						InputStream stream = item.openStream();

				        if (item.isFormField()){
				        	int nb=stream.read(data);
			        		String s=new String(Arrays.copyOfRange(data, 0,nb),"UTF-8");
				        	
			        		if(item.getFieldName().equals("userIdentifier"))numbers=s;
			        		if(item.getFieldName().equals("nbMinutes"))
			        			sendDate=new Date().getTime()+Long.parseLong(s)*60*1000L;
			        		
				        } else {
				        	log.info("Got an uploaded file: " + item.getFieldName() +", name = " + item.getName());
					        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
							int nRead;
							
							while ((nRead = stream.read(data, 0, data.length)) != -1){
								buffer.write(data, 0, nRead);	
							}
							buffer.flush();
							file=URLEncoder.encode(new String(Base64.encode(buffer.toByteArray()),"UTF-8"));
				        }      
					}
				} catch (FileUploadException e) {
					e.printStackTrace();
					rc="error : File exception";
				}
			
				if(file==null){
					//on tente de r�cup�rer le fichier en local
					file=URLEncoder.encode(new String(Base64.encode(getBytesFromUrl(filename))));
				}
		}
		else{
			//Le device �tant non nul, il s'agit de la r�c�ption d'un message depuis l'application Android
			sendDate=Long.parseLong(date);
			
			//si file est null c'est qu'on a recu un lien et que le serveur doit
			//r�cup�rer le fichier
			if(file==null)        
		        file=URLEncoder.encode(new String(Base64.encode(getBytesFromUrl(filename))));
			
			//Attribution d'un num�ro court SFR � chaque destinataire de message
			//cette attribution permet de simplifier la gestion des consentements
			//https://api.sfr.fr/api/sms#api-navtab
			for(final String number:numbers.split(";")){
				RestCall r=new RestCall("https://ws.red.sfr.fr/red-ws/red-b2c/resources/shortcode/generateSmsShortcode?responseType=json&msisdn="+number,null){
					@Override public void onSuccess(String rep){
						
						//Si l'attribution du num�ro court est correcte
						//c'est que le num�ro est bien un num�ro SFR
						
						if(!rep.contains("errorCode\":\"0\""))rc=rc+number+",";

						//Lecture de consentement pour savoir si le destinataire
						//accepte de recevoir des messages sur sa boite voir :
						//https://api.sfr.fr/api/agreement
						RestCall r=new RestCall("https://ws.red.sfr.fr/red-ws/red-b2c/resources/agreement/read?responseType=json&userIdentifier="+number+"&type=PhoneNumber",null){
							@Override public void onSuccess(String rep){
								JsonNode j=null;
								try {j=new ObjectMapper().readTree(rep);
								} catch (JsonProcessingException e) {e.printStackTrace();
								} catch (IOException e) {e.printStackTrace();}
								
								if(j.get("agreement")==null){
									//Si le concentement n'a pas �t� recu
									//on d�clenche une demande de consentement pour recevoir des messages
									RestCall r=new RestCall("https://ws.red.sfr.fr/red-ws/red-b2c/resources/agreement/get?responseType=json&userIdentifier="+number+"&type=PhoneNumber&agreementTypeId=4",null);							
								}
							}
						};	
					}
				};
			}
		}
		
		if(rc.length()==0){
			//V�rification des quotas pour le device emeteur : nb de messages dans les 24 derni�res heures
			if(dao.getNbMessage(device, 24)<=NB_MESSAGES_PARJOUR)	
				m=new Message(file,numbers,sendDate,device);//Finalement on fabrique le message � d�poser
			else
				rc="Error : Quota de "+NB_MESSAGES_PARJOUR+" messages d�pos�s/jours d�pass�s";			
		}

		//chargement du message dans la BDD du Google App Engine
		//On utilise le framework objectify pour enregistrer les messages voir
		//http://code.google.com/p/objectify-appengine/wiki/IntroductionToObjectify
		if(m!=null){
			dao.ofy().put(m);
			Long delay=m.getSendDate()-(new Date().getTime());
			if(delay<0)delay=30*1000L; //Delay minimum n�cessaire � l'encodage pour r�cup�rer l'id de SFR API
			queue.add(withUrl("/api/crontask").param("id", String.valueOf(m.Id)).countdownMillis(delay));
										
			//Les messages sont control�s par le serveur SFR, qui notifie
			//notre serveur de la fin de l'encodage via une url de callback
			//Cette url contient l'identifiant interne du message, ainsi on 
			//pourra le retrouver dans le servlet getIdMessage
			
			String pushURL=URLEncoder.encode(SERVER_MESSAGE+"/getidmessage?Id="+m.Id);
			String params="messageName="+URLEncoder.encode(m.Id+"."+fileformat)+"&pushUrl="+pushURL+"&message="+file;
			RestCall r1=new RestCall("https://ws.red.sfr.fr/red-ws/red-b2c/resources/depotmsg/load",params){
				@Override public void onFailure(int rep){log.warning("Failure de loadmessage="+rep);}
				@Override public void onSuccess(String rep){log.warning("Resultat du depot="+rep);}
			};
		}
	
		resp.setContentType("text/plain");
		
		//est retourn� la liste des num�ros pour lesquels on va tenter de d�poser le message
		resp.getWriter().print(rc);
	}
}
