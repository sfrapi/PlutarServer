PlutarServer est le serveur de l'application Plutar, il illustre :
- l’usage de l’API SFR de dépôt de message (class postMessage)
- la gestion des consentements de dépot (class sfrEvent)
- l’usage des API SFR en mode REST depuis un environnement java (class RestCall)
- l’execution de taches programmées sur le Google App Engine (GAE) (class crontask)
- l’usage de la base de données du GAE au travers du framework Objectify (class DAO)
- la manipulation de données au format JSON (librairie jackson)

Son installation nécessite,
 1. La création d'une application sur le google app engine
 2. le paramétrage des variables
 3. La récupération d'un token sur SFR API pour pouvoir utiliser
 
 La première version de ce projet est issue du travail d'une équipe pluridiscplinaire :
 - Dorian Herlory
 - Aurdrey Martel
 - Hervé Hoareau
 - ...
  