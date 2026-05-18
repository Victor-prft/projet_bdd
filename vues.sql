USE artconnect;

-- =============================================
-- VUE 1 : Œuvres disponibles à la vente
-- Objectif : simplification de requête + masquage des œuvres vendues/exposées.
-- Utilisée pour le catalogue public.
-- =============================================

CREATE OR REPLACE VIEW v_artwork_for_sale AS
SELECT
    a.id_artwork,
    a.title,
    a.medium,
    a.price,
    a.creationYear,
    a.description,
    ar.name          AS artist_name,
    ar.contactEmail  AS artist_email,
    c.name           AS artist_city
FROM   artwork a
JOIN   artist  ar ON a.id_artiste = ar.id_artiste
LEFT JOIN city c  ON ar.id_city   = c.id_city
WHERE  a.status = 'FOR_SALE'
ORDER  BY a.price;


-- =============================================
-- VUE 2 : Disponibilité des workshops (places restantes)
-- Objectif : calcul automatique des places disponibles, simplifie les requêtes
--            d'affichage pour l'inscription des membres.
-- =============================================

CREATE OR REPLACE VIEW v_workshop_availability AS
SELECT
    w.id_workshop,
    w.title,
    w.dateTime,
    w.max_participants,
    w.price,
    w.level,
    l.name AS location_name,
    COUNT(CASE WHEN b.paymentStatus != 'CANCELLED' THEN 1 END)                                AS confirmed_bookings,
    w.max_participants - COUNT(CASE WHEN b.paymentStatus != 'CANCELLED' THEN 1 END)           AS available_spots
FROM   workshop  w
JOIN   location  l ON w.id_location = l.id_location
LEFT JOIN booking b ON w.id_workshop = b.id_workshop
GROUP  BY w.id_workshop, w.title, w.dateTime,
          w.max_participants, w.price, w.level, l.name;


-- =============================================
-- VUE 3 : Expositions actuellement en cours
-- Objectif : sécurité / filtrage — seules les expositions actives sont visibles
--            via cette vue. Masque les expositions passées ou futures.
-- =============================================

CREATE OR REPLACE VIEW v_active_exhibitions AS
SELECT
    e.id_exhibition,
    e.title,
    e.description,
    e.startDate,
    e.endDate,
    e.curatorName,
    e.theme,
    g.name     AS gallery_name,
    l.address  AS gallery_address,
    c.name     AS gallery_city
FROM   exhibition e
JOIN   gallery    g ON e.id_gallery  = g.id_gallery
JOIN   location   l ON g.id_location = l.id_location
JOIN   city       c ON l.id_city     = c.id_city
WHERE  CURDATE() BETWEEN e.startDate AND e.endDate
ORDER  BY e.endDate;


-- =============================================
-- VUE 4 (bonus) : Profil complet des artistes actifs
-- Objectif : vue consolidée pour l'affichage dans l'interface, évite
--            les jointures répétées dans le code applicatif.
-- =============================================

CREATE OR REPLACE VIEW v_active_artists AS
SELECT
    ar.id_artiste,
    ar.name,
    ar.bio,
    ar.birthYear,
    ar.website,
    ar.contactEmail,
    ar.phone,
    c.name         AS city,
    c.country      AS country,
    GROUP_CONCAT(d.name ORDER BY d.name SEPARATOR ', ') AS disciplines
FROM   artist      ar
LEFT JOIN city     c  ON ar.id_city       = c.id_city
LEFT JOIN pratique p  ON ar.id_artiste    = p.id_artiste
LEFT JOIN discipline d ON p.id_discipline = d.id_discipline
WHERE  ar.isActive = TRUE
GROUP  BY ar.id_artiste, ar.name, ar.bio, ar.birthYear,
          ar.website, ar.contactEmail, ar.phone, c.name, c.country;