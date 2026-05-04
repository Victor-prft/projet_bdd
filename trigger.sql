USE artconnect;

-- TRIGGER 1 : Empêcher la création ou la modification d'une exhibition si end_date <= start_date.
DELIMITER $$

CREATE TRIGGER trg_exhibition_dates_update
BEFORE UPDATE ON Exhibition
FOR EACH ROW
BEGIN
    IF NEW.end_date <= NEW.start_date THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erreur : la date de fin doit être postérieure à la date de début.';
    END IF;
END$$

-- TRIGGER 2 : Refuser une réservation (Booking) si le workshop a déjà atteint son nombre maximum de participants.

CREATE TRIGGER trg_booking_capacity_check
BEFORE INSERT ON Booking
FOR EACH ROW
BEGIN
    DECLARE v_max   INT;
    DECLARE v_count INT;

    SELECT max_participants INTO v_max
    FROM   Workshop
    WHERE  id_workshop = NEW.id_workshop;

    SELECT COUNT(*) INTO v_count
    FROM   Booking
    WHERE  id_workshop = NEW.id_workshop;

    IF v_count >= v_max THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erreur : le workshop est complet, aucune place disponible.';
    END IF;
END$$

-- TRIGGER 3 : Tracer toute modification d'une exposition dans une table d'audit pour assurer l'historique.
-- Table d'audit créée si elle n'existe pas encore

CREATE TABLE IF NOT EXISTS ExhibitionAudit (
    id_audit        INT PRIMARY KEY AUTO_INCREMENT,
    id_exhibition   INT,
    old_title       VARCHAR(255),
    new_title       VARCHAR(255),
    old_start_date  DATE,
    new_start_date  DATE,
    old_end_date    DATE,
    new_end_date    DATE,
    changed_at      DATETIME DEFAULT NOW()
)$$

CREATE TRIGGER trg_exhibition_audit
AFTER UPDATE ON Exhibition
FOR EACH ROW
BEGIN
    IF OLD.title      <> NEW.title      OR
       OLD.start_date <> NEW.start_date OR
       OLD.end_date   <> NEW.end_date   THEN

        INSERT INTO ExhibitionAudit
            (id_exhibition, old_title, new_title,
             old_start_date, new_start_date,
             old_end_date,   new_end_date)
        VALUES
            (OLD.id_exhibition, OLD.title, NEW.title,
             OLD.start_date, NEW.start_date,
             OLD.end_date,   NEW.end_date);
    END IF;
END$$

DELIMITER ;
