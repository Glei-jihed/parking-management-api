C4Component
    title Diagramme de Composants (Niveau 3) - API Backend (Spring Boot)

    Container_Ext(spa, "Web Application", "Angular", "Appelle l'API")
    ContainerDb_Ext(db, "Base de données", "PostgreSQL", "Stocke les données")
    ContainerQueue_Ext(broker, "Message Broker", "RabbitMQ", "File de notifications")

    Container_Boundary(api, "API Backend (Spring Boot)") {
        
        Component(security, "Security Filter", "Spring Security / JWT", "Filtre les requêtes entrantes, valide le token et les rôles (RBAC).")
        
        Component(restControllers, "REST Controllers", "Spring Web (Primary Adapters)", "Expose les endpoints pour le Front (ex: /api/reservations).")
        
        Component(cronJob, "Cancellation Scheduler", "Spring @Scheduled", "Tâche planifiée exécutée à 11h00 tous les jours.")

        Component(domainService, "Domaine Métier (Hexagone)", "Java Vanilla", "Contient toute la logique métier pure (règle des 5 jours, profils). Ne dépend d'aucun framework technique.")

        Component(dbAdapter, "Database Adapters", "Spring Data JPA (Secondary Adapters)", "Implémente les interfaces du domaine pour interagir avec la base.")
        
        Component(mqAdapter, "Message Publisher", "Spring AMQP (Secondary Adapters)", "Publie les événements 'ReservationCreated' dans la file.")
    }

    Rel(spa, security, "Requêtes HTTP avec JWT", "JSON/HTTPS")
    Rel(security, restControllers, "Passe la requête si autorisé")
    Rel(restControllers, domainService, "Invoque les cas d'usage métiers")
    Rel(cronJob, domainService, "Déclenche l'annulation des places non validées")
    
    Rel(domainService, dbAdapter, "Demande la sauvegarde/lecture via interfaces (Ports)")
    Rel(domainService, mqAdapter, "Demande l'envoi d'événement via interfaces (Ports)")
    
    Rel(dbAdapter, db, "Exécute les requêtes SQL", "JDBC")
    Rel(mqAdapter, broker, "Envoie les messages", "AMQP")