USE artconnect;

-- Table d'audit pour les modifications d'expositions
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
);

DELIMITER $$

-- TRIGGER 1 : Empêcher la modification d'une exposition si endDate <= startDate.
CREATE TRIGGER trg_exhibition_dates_update
BEFORE UPDATE ON exhibition
FOR EACH ROW
BEGIN
    IF NEW.endDate <= NEW.startDate THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erreur : la date de fin doit être postérieure à la date de début.';
    END IF;
END$$

-- TRIGGER 2 : Refuser une réservation si le workshop a atteint son nombre maximum de participants.
CREATE TRIGGER trg_booking_capacity_check
BEFORE INSERT ON booking
FOR EACH ROW
BEGIN
    DECLARE v_max   INT;
    DECLARE v_count INT;

    SELECT max_participants INTO v_max
    FROM   workshop
    WHERE  id_workshop = NEW.id_workshop;

    SELECT COUNT(*) INTO v_count
    FROM   booking
    WHERE  id_workshop    = NEW.id_workshop
      AND  paymentStatus != 'CANCELLED';

    IF v_count >= v_max THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Erreur : le workshop est complet, aucune place disponible.';
    END IF;
END$$

-- TRIGGER 3 : Tracer toute modification d'une exposition dans la table d'audit.
CREATE TRIGGER trg_exhibition_audit
AFTER UPDATE ON exhibition
FOR EACH ROW
BEGIN
    IF OLD.title     <> NEW.title     OR
       OLD.startDate <> NEW.startDate OR
       OLD.endDate   <> NEW.endDate   THEN
        INSERT INTO ExhibitionAudit
            (id_exhibition, old_title,  new_title,
             old_start_date, new_start_date,
             old_end_date,   new_end_date)
        VALUES
            (OLD.id_exhibition, OLD.title, NEW.title,
             OLD.startDate, NEW.startDate,
             OLD.endDate,   NEW.endDate);
    END IF;
END$$

DELIMITER ;
