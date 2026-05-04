use artconnect; 

-- VUE 1 : v_artist_public
-- OBJECTIF : Sécurité / masquage de données sensibles
--
-- Expose uniquement les informations publiques d'un artiste.
-- Le téléphone et l'e-mail de contact (données personnelles RGPD)
-- sont exclus. Cette vue peut être accordée à des rôles "lecture
-- publique" sans exposer les coordonnées privées.
-- ---------------------------------------------
 
CREATE OR REPLACE VIEW v_artist_public AS
SELECT
    a.id_artiste,
    a.name,
    a.bio,
    a.birthYear,
    a.website,
    a.isActive,
    c.name        AS city,
    c.country
FROM artist a
LEFT JOIN city c ON a.id_city = c.id_city;

-- VUE 2 : v_member_public
-- OBJECTIF : Sécurité / masquage de données sensibles
--
-- Similaire à v_artist_public mais pour les membres.
-- Masque e-mail, téléphone et année de naissance.
-- Expose seulement le nom, le type d'abonnement et la ville.
-- ---------------------------------------------
 
CREATE OR REPLACE VIEW v_member_public AS
SELECT
    m.id_member,
    m.name,
    m.membershipType,
    c.name      AS city,
    c.country
FROM community_member m
LEFT JOIN city c ON m.id_city = c.id_city;

-- VUE 3 : v_exhibition_program
-- OBJECTIF : Simplification de requête
--
-- Programme complet des expositions : galerie, ville,
-- adresse, dates, commissaire, statut temporel calculé
-- (UPCOMING / ONGOING / PAST) et nombre d'œuvres présentées.
-- Remplace une jointure à 5 tables + sous-requête de comptage.
-- ---------------------------------------------
 
CREATE OR REPLACE VIEW v_exhibition_program AS
SELECT
    e.id_exhibition,
    e.title                             AS exhibition_title,
    e.startDate,
    e.endDate,
    e.curatorName,
    e.theme,
    CASE
        WHEN CURDATE() < e.startDate THEN 'UPCOMING'
        WHEN CURDATE() > e.endDate   THEN 'PAST'
        ELSE                              'ONGOING'
    END                                 AS temporal_status,
    g.name                              AS gallery_name,
    g.rating                            AS gallery_rating,
    l.address,
    c.name                              AS city,
    COUNT(ex.id_artwork)                AS artwork_count
FROM exhibition e
JOIN gallery  g  ON e.id_gallery    = g.id_gallery
JOIN location l  ON g.id_location   = l.id_location
JOIN city     c  ON l.id_city       = c.id_city
LEFT JOIN exhibited ex ON e.id_exhibition = ex.id_exhibition
GROUP BY
    e.id_exhibition, e.title, e.startDate, e.endDate,
    e.curatorName, e.theme,
    g.name, g.rating, l.address, c.name;
