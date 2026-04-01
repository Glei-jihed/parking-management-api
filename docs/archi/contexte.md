C4Context
    title Diagramme de Contexte (Niveau 1) - Parking Reservation System

    Person(employee, "Employé", "Réserve des places (max 5 jours) et valide sa présence (scan QR).")
    Person(secretary, "Secrétaire", "Administre le système, gère le support et les utilisateurs.")
    Person(manager, "Manager", "Consulte les statistiques et réserve au mois (max 30 jours).")

    System(parkingSystem, "Parking Reservation System", "Gère l'attribution des places, les libérations à 11h et l'historique.")
    
    System_Ext(emailApp, "Application de Notification", "Système externe en charge d'envoyer les emails réels.")

    Rel(employee, parkingSystem, "Consulte, réserve et fait son check-in")
    Rel(secretary, parkingSystem, "Administre et modifie")
    Rel(manager, parkingSystem, "Analyse les stats et réserve")
    
    Rel(parkingSystem, emailApp, "Délègue l'envoi de confirmation", "Via file d'attente")