USE artconnect;

-- PROCÉDURE 1 : Créer un workshop et inscrire automatiquement l'instructeur (artiste) comme premier participant, Atomicité – si l'insertion du workshop échoue,l'inscription de l'instructeur n'a pas lieu.

CREATE PROCEDURE sp_create_workshop_with_instructor(
    IN p_title           VARCHAR(255),
    IN p_date_time       DATETIME,
    IN p_duration        INT,
    IN p_max_part        INT,
    IN p_price           DECIMAL(10,2),
    IN p_location        VARCHAR(255),
    IN p_description     TEXT,
    IN p_level           VARCHAR(50),
    IN p_instructor_id   INT,   -- FK → Artist.id_artist
    IN p_member_id       INT    -- FK → CommunityMember.id_member (même personne)
)
BEGIN
    DECLARE v_new_workshop_id INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

        INSERT INTO Workshop
            (title, date_time, duration_minutes, max_participants,
             price, location, description, level, id_instructor)
        VALUES
            (p_title, p_date_time, p_duration, p_max_part,
             p_price, p_location, p_description, p_level, p_instructor_id);

        SET v_new_workshop_id = LAST_INSERT_ID();

        -- Inscription automatique de l'instructeur comme participant
        INSERT INTO Booking (booking_date, payment_status, id_workshop, id_member)
        VALUES (CURDATE(), 'CONFIRMED', v_new_workshop_id, p_member_id);

    COMMIT;

    SELECT v_new_workshop_id AS id_workshop_created;
END$$

-- PROCÉDURE 2 : Inscrire un membre à un workshop, vérifier la disponibilité, créer la réservation et retourner un message de confirmation ou d'erreur sans utiliser SIGNAL (gestion douce).

CREATE PROCEDURE sp_register_member_to_workshop(
    IN  p_member_id   INT,
    IN  p_workshop_id INT,
    OUT p_message     VARCHAR(255)
)
BEGIN
    DECLARE v_max   INT;
    DECLARE v_count INT;

    SELECT max_participants INTO v_max
    FROM   Workshop
    WHERE  id_workshop = p_workshop_id;

    IF v_max IS NULL THEN
        SET p_message = 'Erreur : workshop introuvable.';
    ELSE
        SELECT COUNT(*) INTO v_count
        FROM   Booking
        WHERE  id_workshop = p_workshop_id;

        IF v_count >= v_max THEN
            SET p_message = 'Erreur : workshop complet.';
        ELSE
            INSERT INTO Booking (booking_date, payment_status, id_workshop, id_member)
            VALUES (CURDATE(), 'PENDING', p_workshop_id, p_member_id);

            SET p_message = CONCAT('Succès : réservation créée (id=', LAST_INSERT_ID(), ').');
        END IF;
    END IF;
END$$


-- PROCÉDURE 3 : Ajouter une œuvre à une exposition en vérifiant que l'exposition est encore ouverte, simplifier l'opération courante d'association Artwork ↔ Exhibition avec contrôle métier.

CREATE PROCEDURE sp_add_artwork_to_exhibition(
    IN  p_artwork_id    INT,
    IN  p_exhibition_id INT,
    OUT p_message       VARCHAR(255)
)
BEGIN
    DECLARE v_end_date DATE;

    SELECT end_date INTO v_end_date
    FROM   Exhibition
    WHERE  id_exhibition = p_exhibition_id;

    IF v_end_date IS NULL THEN
        SET p_message = 'Erreur : exposition introuvable.';
    ELSEIF v_end_date < CURDATE() THEN
        SET p_message = 'Erreur : l\'exposition est déjà terminée.';
    ELSE
        -- INSERT IGNORE évite le doublon (PK composite)
        INSERT IGNORE INTO ExhibitionArtwork (id_exhibition, id_artwork)
        VALUES (p_exhibition_id, p_artwork_id);

        IF ROW_COUNT() = 0 THEN
            SET p_message = 'Info : œuvre déjà présente dans cette exposition.';
        ELSE
            SET p_message = 'Succès : œuvre ajoutée à l\'exposition.';
        END IF;
    END IF;
END$$

DELIMITER ;
