package com.project.artconnect.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to manage JDBC connections.
 * TODO: Students must implementation the getConnection logic.
 */
public class ConnectionManager {

   // ---------------------------------------------------------------
    // renseignez les paramètres de connexion
    // ---------------------------------------------------------------
    private static final String DB_URL = "jdbc:mysql://localhost:3306/?user=root";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Tigrou@MySQL5";
    // ---------------------------------------------------------------

    /** Constructeur privé : classe utilitaire, pas d'instanciation. */
    private ConnectionManager() {
    }

    /**
     * Retourne une nouvelle connexion JDBC vers la base Ecole.
     *
     * @return une {@link Connection} ouverte
     * @throws SQLException si la connexion échoue
     *
     *                      complétez cette méthode (elle est déjà
     *                      fonctionnelle si
     *                      les constantes ci-dessus sont correctement renseignées).
     */
    public static Connection getConnection() throws SQLException {
        // retourner une connexion JDBC en utilisant DriverManager
        // throw new UnsupportedOperationException("ConnectionManager.getConnection()
        // non implémenté
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
        
    }
