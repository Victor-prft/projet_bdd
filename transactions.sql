USE artconnect;

-- =============================================
-- SCÉNARIO 1 : Inscription atomique d'un nouveau membre à plusieurs workshops
-- Atomicité : si l'une des inscriptions échoue (workshop complet, doublon, etc.),
--             toutes les opérations sont annulées (ROLLBACK), y compris la création du membre.
-- =============================================

START TRANSACTION;

    -- Étape 1 : Créer le nouveau membre
    INSERT INTO community_member (name, email, birthYear, phone, membershipType, id_city)
    VALUES ('Lucie Martin', 'lucie.martin@test.fr', 1995, '+33 6 12 34 56 78', 'premium', 1);

    SET @new_member_id = LAST_INSERT_ID();

    -- Étape 2 : Inscrire ce membre à trois workshops
    INSERT INTO booking (id_member, id_workshop, bookingDate, paymentStatus)
    VALUES (@new_member_id, 1, CURDATE(), 'PAID');

    INSERT INTO booking (id_member, id_workshop, bookingDate, paymentStatus)
    VALUES (@new_member_id, 3, CURDATE(), 'PAID');

    INSERT INTO booking (id_member, id_workshop, bookingDate, paymentStatus)
    VALUES (@new_member_id, 5, CURDATE(), 'PENDING');

COMMIT;

-- Vérification
SELECT cm.name, cm.email, w.title, b.paymentStatus
FROM   booking          b
JOIN   community_member cm ON b.id_member   = cm.id_member
JOIN   workshop         w  ON b.id_workshop = w.id_workshop
WHERE  cm.email = 'lucie.martin@test.fr';


-- =============================================
-- SCÉNARIO 2 : Démonstration du ROLLBACK
-- On insère une oeuvre et on l'annule immédiatement pour montrer l'atomicité.
-- =============================================

START TRANSACTION;

    INSERT INTO artwork (title, creationYear, medium, price, status, id_artiste)
    VALUES ('Oeuvre test rollback', 2024, 'Huile sur toile', 500.00, 'FOR_SALE', 1);

ROLLBACK; -- Annulation volontaire

-- Vérification : l'oeuvre ne doit pas exister
SELECT * FROM artwork WHERE title = 'Oeuvre test rollback';


-- =============================================
-- SCÉNARIO 3 : Transfert de réservation (annulation + nouvelle inscription)
-- Annuler la réservation d'Alice au workshop 3 et l'inscrire au workshop 2.
-- Les deux opérations doivent être atomiques.
-- =============================================

START TRANSACTION;

    -- Annuler l'ancienne réservation
    UPDATE booking
    SET    paymentStatus = 'CANCELLED'
    WHERE  id_member   = 1   -- Alice Renaud
      AND  id_workshop = 3;

    -- Créer la nouvelle réservation
    INSERT INTO booking (id_member, id_workshop, bookingDate, paymentStatus)
    VALUES (1, 2, CURDATE(), 'PENDING');

COMMIT;

-- Vérification : réservations d'Alice
SELECT w.title, b.paymentStatus, b.bookingDate
FROM   booking   b
JOIN   workshop  w ON b.id_workshop = w.id_workshop
WHERE  b.id_member = 1
ORDER  BY b.bookingDate DESC;