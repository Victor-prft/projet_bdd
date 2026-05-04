USE artconnect;

-- On supprime la procédure si elle existe déjà pour éviter les conflits
DROP PROCEDURE IF EXISTS inscrire_workshops;

DELIMITER $$

CREATE PROCEDURE inscrire_workshops(IN p_member INT)
BEGIN
    -- Variables pour stocker le nombre d'inscriptions et les capacités max
    DECLARE nb1 INT;
    DECLARE max1 INT;
    DECLARE nb2 INT;
    DECLARE max2 INT;

    -- Début de la transaction pour garantir l'atomicité
    START TRANSACTION;

    -- =========================
    -- Vérification workshop 1
    -- =========================

    -- Nombre de participants déjà inscrits (hors annulés)
    SELECT COUNT(*) INTO nb1
    FROM booking
    WHERE id_workshop = 1 AND paymentStatus != 'CANCELLED';

    -- Capacité maximale du workshop 1
    SELECT max_participants INTO max1
    FROM workshop
    WHERE id_workshop = 1;

    -- =========================
    -- Vérification workshop 2
    -- =========================

    -- Nombre de participants déjà inscrits (hors annulés)
    SELECT COUNT(*) INTO nb2
    FROM booking
    WHERE id_workshop = 2 AND paymentStatus != 'CANCELLED';

    -- Capacité maximale du workshop 2
    SELECT max_participants INTO max2
    FROM workshop
    WHERE id_workshop = 2;

    -- =========================
    -- Vérification globale (condition d'inscription)
    -- =========================
    IF nb1 < max1
       AND nb2 < max2
       -- Vérifie que le membre n'est pas déjà inscrit au workshop 1
       AND NOT EXISTS (
            SELECT 1 FROM booking
            WHERE id_member = p_member AND id_workshop = 1
       )
       -- Vérifie que le membre n'est pas déjà inscrit au workshop 2
       AND NOT EXISTS (
            SELECT 1 FROM booking
            WHERE id_member = p_member AND id_workshop = 2
       )
    THEN

        -- =========================
        -- Insertion des deux inscriptions
        -- =========================

        INSERT INTO booking (id_member, id_workshop, paymentStatus)
        VALUES (p_member, 1, 'PENDING');

        INSERT INTO booking (id_member, id_workshop, paymentStatus)
        VALUES (p_member, 2, 'PENDING');

        -- Validation de la transaction si tout est OK
        COMMIT;

    ELSE
        -- Annulation de toutes les opérations si une condition échoue
        ROLLBACK;
    END IF;

END $$

DELIMITER ;

-- =========================
-- TEST DE LA PROCÉDURE
-- =========================

CALL inscrire_workshops(8);

-- Vérification des inscriptions du membre 8
SELECT * FROM booking WHERE id_member = 8;