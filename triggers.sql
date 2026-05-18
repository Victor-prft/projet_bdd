USE artconnect;

DELIMITER $$

-- TRIGGER 1 : Vérifier la capacité maximale avant toute nouvelle réservation
-- Empêche l'insertion si le workshop est déjà complet (hors annulations).

CREATE TRIGGER trg_check_booking_capacity
BEFORE INSERT ON booking
FOR EACH ROW
BEGIN
    DECLARE v_max     INT;
    DECLARE v_current INT;

    SELECT max_participants INTO v_max
    FROM   workshop
    WHERE  id_workshop = NEW.id_workshop;

    SELECT COUNT(*) INTO v_current
    FROM   booking
    WHERE  id_workshop    = NEW.id_workshop
      AND  paymentStatus != 'CANCELLED';

    IF v_current >= v_max THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erreur : le workshop est complet, inscription refusée.';
    END IF;
END$$


-- TRIGGER 2 : Passer une œuvre au statut EXHIBITED dès qu'elle est ajoutée à une exposition
-- Ne modifie que les œuvres dont le statut est FOR_SALE (les œuvres SOLD ne changent pas).

CREATE TRIGGER trg_set_artwork_exhibited
AFTER INSERT ON exhibited
FOR EACH ROW
BEGIN
    UPDATE artwork
    SET    status = 'EXHIBITED'
    WHERE  id_artwork = NEW.id_artwork
      AND  status     = 'FOR_SALE';
END$$


-- TRIGGER 3 : Restaurer le statut FOR_SALE quand une œuvre quitte toutes les expositions
-- Si l'œuvre n'est plus présente dans aucune exposition, elle repasse à FOR_SALE.

CREATE TRIGGER trg_restore_artwork_status
AFTER DELETE ON exhibited
FOR EACH ROW
BEGIN
    DECLARE v_remaining INT;

    SELECT COUNT(*) INTO v_remaining
    FROM   exhibited
    WHERE  id_artwork = OLD.id_artwork;

    IF v_remaining = 0 THEN
        UPDATE artwork
        SET    status = 'FOR_SALE'
        WHERE  id_artwork = OLD.id_artwork
          AND  status     = 'EXHIBITED';
    END IF;
END$$


-- TRIGGER 4 (bonus) : Contrôler la cohérence des dates d'exposition avant insertion
-- Assure que la date de fin est strictement postérieure à la date de début.

CREATE TRIGGER trg_exhibition_date_check
BEFORE INSERT ON exhibition
FOR EACH ROW
BEGIN
    IF NEW.endDate <= NEW.startDate THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erreur : la date de fin doit être postérieure à la date de début.';
    END IF;
END$$

DELIMITER ;