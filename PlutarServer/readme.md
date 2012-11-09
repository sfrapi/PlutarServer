PlutarServer est le serveur de l'application Plutar, il illustre :
- l�usage de l�API SFR de d�p�t de message (class postMessage)
- la gestion des consentements de d�pot (class sfrEvent)
- l�usage des API SFR en mode REST depuis un environnement java (class RestCall)
- l�execution de taches programm�es sur le Google App Engine (GAE) (class crontask)
- l�usage de la base de donn�es du GAE au travers du framework Objectify (class DAO)
- la manipulation de donn�es au format JSON (librairie jackson)

Son installation n�cessite,
 1. La cr�ation d'une application sur le google app engine
 2. le param�trage des variables
 3. La r�cup�ration d'un token sur SFR API pour pouvoir utiliser
 
 La premi�re version de ce projet est issue du travail d'une �quipe pluridiscplinaire :
 - Dorian Herlory
 - Aurdrey Martel
 - Herv� Hoareau
 - ...
  