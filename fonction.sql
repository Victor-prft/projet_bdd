USE artconnect;

-- FONCTION 1 : Retourner le nombre de réservations confirmées pour un workshop donné (utile dans les requêtes)

DELIMITER $$

CREATE FUNCTION fn_get_participant_count(p_workshop_id INT)
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_count INT;

    SELECT COUNT(*) INTO v_count
    FROM   Booking
    WHERE  id_workshop = p_workshop_id;

    RETURN v_count;
END$$


-- Fonction 2  : Retourner la moyenne des notes (Review.rating) pour une œuvre donnée, ou NULL si aucune note.

CREATE FUNCTION fn_avg_artwork_rating(p_artwork_id INT)
RETURNS DECIMAL(4,2)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_avg DECIMAL(4,2);

    SELECT AVG(rating) INTO v_avg
    FROM   Review
    WHERE  id_artwork = p_artwork_id;

    RETURN v_avg;
END$$


-- FONCTION 3 : Nombre d'œuvres exposées dans une galerie, retourner le total d'œuvres distinctes associées à toutes les expositions d'une galerie donnée.

CREATE FUNCTION fn_count_artworks_in_gallery(p_gallery_id INT)
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_count INT;
 
    SELECT COUNT(DISTINCT ea.id_artwork) INTO v_count
    FROM   ExhibitionArtwork ea
    JOIN   Exhibition e ON e.id_exhibition = ea.id_exhibition
    WHERE  e.id_gallery = p_gallery_id;
 
    RETURN IFNULL(v_count, 0);
END$$
