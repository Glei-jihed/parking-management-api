C4Container
    title Diagramme de Conteneurs (Niveau 2) - Parking Reservation System

    Person(user, "Utilisateur", "Employé, Secrétaire ou Manager")

    Container_Boundary(system_boundary, "Parking Reservation System") {
        
        Container(spa, "Web Application", "Angular, TypeScript", "Fournit l'interface responsive et capte les URL profondes des QR codes.")
        
        Container(api, "API Backend", "Java, Spring Boot", "Expose l'API REST, applique la logique métier (Hexagonale), sécurise via JWT et gère l'annulation CRON de 11h.")
        
        ContainerDb(db, "Base de données", "PostgreSQL", "Stocke les utilisateurs, les 60 places de parking et l'historique (Soft Delete).")
        
        ContainerQueue(broker, "Message Broker", "RabbitMQ", "File d'attente stockant les événements de création de réservation.")
    }
    
    System_Ext(emailApp, "Application de Notification", "Système externe (hors scope).")

    Rel(user, spa, "Navigue et scanne les QR codes", "HTTPS")
    Rel(spa, api, "Appelle les endpoints métiers", "JSON/HTTPS")
    Rel(api, db, "Lit et écrit les données", "TCP / JDBC")
    Rel(api, broker, "Publie l'événement 'ReservationCreated'", "AMQP")
    Rel(broker, emailApp, "Consomme les messages en attente", "AMQP")