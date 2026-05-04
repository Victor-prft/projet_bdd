-- =============================================
-- CRÉATION DE LA BASE DE DONNÉES
-- =============================================

CREATE DATABASE artconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE artconnect;

-- =============================================
-- Entite
-- =============================================

CREATE TABLE City (
    id_city     INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    region      VARCHAR(100),
    country     VARCHAR(100) NOT NULL
);

CREATE TABLE Location (
    id_location INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    address     VARCHAR(200) NOT NULL,
    id_city     INT NOT NULL,
    CONSTRAINT fk_location_city FOREIGN KEY (id_city) REFERENCES city(id_city)
);

CREATE TABLE discipline (
    id_discipline   INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE artwork_tag (
    id_tag  INT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(50) NOT NULL UNIQUE
);


CREATE TABLE artist (
    id_artiste      INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(50)     NOT NULL,
    bio             TEXT,
    birthYear       INT,
    website         VARCHAR(300),
    isActive        BOOLEAN         NOT NULL DEFAULT TRUE,
    contactEmail    VARCHAR(200)    NOT NULL UNIQUE,
    phone           VARCHAR(30),
    id_city         INT,
    CONSTRAINT fk_artist_city FOREIGN KEY (id_city) REFERENCES city(id_city)
);

CREATE TABLE artist_social (
    id_social   INT PRIMARY KEY AUTO_INCREMENT,
    platform    VARCHAR(50)     NOT NULL,
    url         VARCHAR(300)    NOT NULL,
    id_artiste  INT             NOT NULL,
    CONSTRAINT fk_social_artist FOREIGN KEY (id_artiste) REFERENCES artist(id_artiste)
        ON DELETE CASCADE
);


CREATE TABLE community_member (
    id_member       INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(150)    NOT NULL,
    email           VARCHAR(200)    NOT NULL UNIQUE,
    birthYear       INT,
    phone           VARCHAR(30),
    membershipType  ENUM('free', 'premium') NOT NULL DEFAULT 'free',
    id_city         INT,
    CONSTRAINT fk_member_city FOREIGN KEY (id_city) REFERENCES city(id_city)
);


CREATE TABLE artwork (
    id_artwork      INT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(200)    NOT NULL,
    creationYear    INT,
    medium          VARCHAR(100),
    width           DECIMAL(8,2),
    height          DECIMAL(8,2),
    depth           DECIMAL(8,2),
    description     TEXT,
    price           DECIMAL(12,2)   NOT NULL CHECK (price >= 0),
    status          ENUM('FOR_SALE', 'SOLD', 'EXHIBITED') NOT NULL DEFAULT 'FOR_SALE',
    id_artiste      INT             NOT NULL,
    CONSTRAINT fk_artwork_artist FOREIGN KEY (id_artiste) REFERENCES artist(id_artiste)
);

-- =============================================
-- GALLERY
-- =============================================

CREATE TABLE gallery (
    id_gallery      INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(200)    NOT NULL,
    ownerName       VARCHAR(100),
    contactPhone    VARCHAR(30),
    rating          DECIMAL(3,2)    CHECK (rating >= 0 AND rating <= 5),
    website         VARCHAR(200),
    id_location     INT             NOT NULL,
    CONSTRAINT fk_gallery_location FOREIGN KEY (id_location) REFERENCES location(id_location)
);

CREATE TABLE gallery_hours (
    id_hours    INT PRIMARY KEY AUTO_INCREMENT,
    dayOfWeek   ENUM('MON','TUE','WED','THU','FRI','SAT','SUN') NOT NULL,
    openTime    TIME NOT NULL,
    closeTime   TIME NOT NULL,
    id_gallery  INT  NOT NULL,
    CONSTRAINT fk_hours_gallery FOREIGN KEY (id_gallery) REFERENCES gallery(id_gallery)
        ON DELETE CASCADE,
    CONSTRAINT chk_hours CHECK (closeTime > openTime)
);

-- =============================================
-- EXHIBITION
-- =============================================

CREATE TABLE exhibition (
    id_exhibition   INT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(200)    NOT NULL,
    description     TEXT,
    startDate       DATE            NOT NULL,
    endDate         DATE            NOT NULL,
    curatorName     VARCHAR(150),
    theme           VARCHAR(200),
    id_gallery      INT             NOT NULL,
    CONSTRAINT fk_exhibition_gallery FOREIGN KEY (id_gallery) REFERENCES gallery(id_gallery),
    CONSTRAINT chk_dates CHECK (endDate > startDate)
);


CREATE TABLE workshop (
    id_workshop         INT PRIMARY KEY AUTO_INCREMENT,
    title               VARCHAR(100)    NOT NULL,
    dateTime            DATETIME        NOT NULL,
    max_participants    INT             NOT NULL CHECK (max_participants > 0),
    price               DECIMAL(15,2)   NOT NULL CHECK (price >= 0),
    duration_minutes    INT             NOT NULL CHECK (duration_minutes > 0),
    description         TEXT,
    level               ENUM('beginner', 'intermediate', 'advanced') NOT NULL,
    id_location         INT             NOT NULL,
    CONSTRAINT fk_workshop_location FOREIGN KEY (id_location) REFERENCES location(id_location)
);

-- =============================================
-- TABLES DE JOINTURE (many-to-many)
-- =============================================

CREATE TABLE exhibited (
    id_artwork      INT NOT NULL,
    id_exhibition   INT NOT NULL,
    PRIMARY KEY (id_artwork, id_exhibition),
    CONSTRAINT fk_exhibited_artwork   FOREIGN KEY (id_artwork)    REFERENCES artwork(id_artwork),
    CONSTRAINT fk_exhibited_exhibition FOREIGN KEY (id_exhibition) REFERENCES exhibition(id_exhibition)
);

CREATE TABLE tagged (
    id_artwork  INT NOT NULL,
    id_tag      INT NOT NULL,
    PRIMARY KEY (id_artwork, id_tag),
    CONSTRAINT fk_tagged_artwork FOREIGN KEY (id_artwork) REFERENCES artwork(id_artwork),
    CONSTRAINT fk_tagged_tag    FOREIGN KEY (id_tag)     REFERENCES artwork_tag(id_tag)
);

CREATE TABLE pratique (
    id_artiste      INT NOT NULL,
    id_discipline   INT NOT NULL,
    PRIMARY KEY (id_artiste, id_discipline),
    CONSTRAINT fk_pratique_artist     FOREIGN KEY (id_artiste)    REFERENCES artist(id_artiste),
    CONSTRAINT fk_pratique_discipline FOREIGN KEY (id_discipline) REFERENCES discipline(id_discipline)
);

CREATE TABLE prefere (
    id_member       INT NOT NULL,
    id_discipline   INT NOT NULL,
    PRIMARY KEY (id_member, id_discipline),
    CONSTRAINT fk_prefere_member     FOREIGN KEY (id_member)     REFERENCES community_member(id_member),
    CONSTRAINT fk_prefere_discipline FOREIGN KEY (id_discipline) REFERENCES discipline(id_discipline)
);

-- =============================================
-- REVIEW & BOOKING (entités-associations)
-- =============================================

CREATE TABLE review (
    id_artwork  INT             NOT NULL,
    id_member   INT             NOT NULL,
    rating      INT             NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment     VARCHAR(150),
    reviewDate  DATE            NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY (id_artwork, id_member),
    CONSTRAINT fk_review_artwork FOREIGN KEY (id_artwork) REFERENCES artwork(id_artwork),
    CONSTRAINT fk_review_member  FOREIGN KEY (id_member)  REFERENCES community_member(id_member)
);

CREATE TABLE booking (
    id_member       INT             NOT NULL,
    id_workshop     INT             NOT NULL,
    bookingDate     DATE            NOT NULL DEFAULT (CURRENT_DATE),
    paymentStatus   ENUM('PENDING', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id_member, id_workshop),
    CONSTRAINT fk_booking_member   FOREIGN KEY (id_member)   REFERENCES community_member(id_member),
    CONSTRAINT fk_booking_workshop FOREIGN KEY (id_workshop) REFERENCES workshop(id_workshop)
);

-- =============================================
-- Insertion
-- =============================================
 
INSERT INTO City (name, region, country) VALUES
  ('Paris',     'Île-de-France',    'France'),
  ('Lyon',      'Auvergne-Rhône-Alpes', 'France'),
  ('Marseille', 'Provence-Alpes-Côte d\'Azur', 'France'),
  ('Bordeaux',  'Nouvelle-Aquitaine','France'),
  ('Bruxelles', 'Région de Bruxelles-Capitale', 'Belgique'),
  ('Montréal',  'Québec',           'Canada');
 
 
INSERT INTO Location (name, address, id_city) VALUES
  ('Galerie du Marais',         '12 rue de Bretagne, 75003',    1),  -- Paris
  ('Centre d\'Art Confluence',  '25 quai Perrache, 69002',      2),  -- Lyon
  ('Espace Joliette',           '3 avenue Robert Schuman, 13002',3), -- Marseille
  ('Atelier Chartrons',         '87 quai des Chartrons, 33000', 4),  -- Bordeaux
  ('Palais des Beaux-Arts',     'Rue Ravenstein 23, 1000',      5),  -- Bruxelles
  ('Studio Plateau-Mont-Royal', '4840 boul. Saint-Laurent, H2T',6);  -- Montréal
 
 
INSERT INTO discipline (name) VALUES
  ('Peinture'),
  ('Sculpture'),
  ('Photographie'),
  ('Art numérique'),
  ('Gravure'),
  ('Installation'),
  ('Dessin');
 
 
INSERT INTO artwork_tag (name) VALUES
  ('abstrait'),
  ('figuratif'),
  ('urbain'),
  ('nature'),
  ('portrait'),
  ('politique'),
  ('lumineux'),
  ('sombre'),
  ('minimaliste'),
  ('expressionniste');
 
 
INSERT INTO artist (name, bio, birthYear, website, isActive, contactEmail, phone, id_city) VALUES
  ('Sophie Marchand',
   'Peintre abstraite parisienne, formée aux Beaux-Arts de Paris. Son travail explore la couleur comme langage.',
   1985, 'https://sophiemarchand.art', TRUE, 'sophie@marchand.art', '+33 6 11 22 33 44', 1),
 
  ('Karim Bouras',
   'Sculpteur et installateur d\'origine algérienne basé à Lyon. Utilise des matériaux de récupération.',
   1978, 'https://karimbouras.com', TRUE, 'karim@bouras.com', '+33 6 55 66 77 88', 2),
 
  ('Elena Vasquez',
   'Photographe documentaire installée à Marseille. Spécialisée dans les portraits urbains et la migration.',
   1990, NULL, TRUE, 'elena.vasquez@photo.fr', '+33 6 99 00 11 22', 3),
 
  ('Thomas Girard',
   'Artiste numérique et graveur bordelais. Mêle techniques traditionnelles et outils digitaux.',
   1983, 'https://tgirard.digital', TRUE, 'thomas@girard.digital', '+33 5 56 78 90 12', 4),
 
  ('Nadia Okonkwo',
   'Plasticienne belge d\'origine nigériane. Son œuvre questionne identité, mémoire et diaspora.',
   1975, 'https://nadiaokonkwo.be', TRUE, 'nadia@okonkwo.be', '+32 489 12 34 56', 5),
 
  ('Marc Lefebvre',
   'Peintre figuratif et dessinateur montréalais, connu pour ses grandes compositions urbaines.',
   1969, NULL, FALSE, 'marc.lefebvre@atelier.ca', '+1 514 555 0199', 6);
 
 
INSERT INTO artist_social (platform, url, id_artiste) VALUES
  ('Instagram', 'https://instagram.com/sophie.marchand.art', 1),
  ('LinkedIn',  'https://linkedin.com/in/sophiemarchand',    1),
  ('Instagram', 'https://instagram.com/karim_bouras',        2),
  ('Facebook',  'https://facebook.com/karimbouras.sculpture', 2),
  ('Instagram', 'https://instagram.com/elena_vasquez_photo', 3),
  ('Twitter',   'https://twitter.com/elena_vasquez',         3),
  ('Instagram', 'https://instagram.com/tgirard_digital',     4),
  ('Behance',   'https://behance.net/thomasgirard',          4),
  ('Instagram', 'https://instagram.com/nadiaokonkwo',        5),
  ('LinkedIn',  'https://linkedin.com/in/nadiaokonkwo',      5);
 
 
INSERT INTO pratique (id_artiste, id_discipline) VALUES
  (1, 1), -- Sophie   → Peinture
  (1, 7), -- Sophie   → Dessin
  (2, 2), -- Karim    → Sculpture
  (2, 6), -- Karim    → Installation
  (3, 3), -- Elena    → Photographie
  (4, 4), -- Thomas   → Art numérique
  (4, 5), -- Thomas   → Gravure
  (5, 6), -- Nadia    → Installation
  (5, 1), -- Nadia    → Peinture
  (6, 1), -- Marc     → Peinture
  (6, 7); -- Marc     → Dessin
 
 
INSERT INTO artwork (title, creationYear, medium, width, height, depth, description, price, status, id_artiste) VALUES
  -- Sophie Marchand (id=1)
  ('Éclats de lumière I',    2021, 'Huile sur toile',       120.00, 100.00, NULL,
   'Composition abstraite jouant sur les contrastes chaud/froid.', 3500.00, 'FOR_SALE', 1),
  ('Éclats de lumière II',   2022, 'Acrylique sur toile',   90.00,  90.00, NULL,
   'Suite de la série Éclats, palette plus sombre.', 2800.00, 'EXHIBITED', 1),
  ('Fragment #7',            2023, 'Huile et collage',       60.00,  80.00, NULL,
   'Exploration de la fragmentation de l\'identité.', 1900.00, 'FOR_SALE', 1),
 
  -- Karim Bouras (id=2)
  ('Mémoire des ruines',     2019, 'Acier et béton recyclé', 50.00, 180.00, 50.00,
   'Sculpture monumentale évoquant l\'histoire des banlieues industrielles.', 12000.00, 'EXHIBITED', 2),
  ('Archipel',               2022, 'Bois flotté et résine',  200.00, 60.00, 40.00,
   'Installation au sol, morceaux de bois liés par de la résine transparente.', 8500.00, 'SOLD', 2),
 
  -- Elena Vasquez (id=3)
  ('Frontières #12',         2020, 'Tirage argentique',      60.00,  90.00, NULL,
   'Portrait en noir et blanc d\'une famille de réfugiés.', 950.00, 'SOLD', 3),
  ('Frontières #23',         2021, 'Tirage pigmentaire',     80.00, 120.00, NULL,
   'Regard croisé entre deux générations migrantes.', 1200.00, 'FOR_SALE', 3),
  ('Quartiers #5',           2022, 'Tirage pigmentaire',     50.00,  70.00, NULL,
   'Série documentaire sur les métropoles du Sud.', 850.00, 'FOR_SALE', 3),
 
  -- Thomas Girard (id=4)
  ('Matrice 01',             2021, 'Impression numérique sur aluminium', 100.00, 70.00, NULL,
   'Superposition de données visuelles et de gravure numérique.', 2200.00, 'FOR_SALE', 4),
  ('Matrice 02',             2023, 'Gravure et impression jet d\'encre', 50.00,  70.00, NULL,
   'Hybridation entre gravure sur cuivre et retouche numérique.', 1600.00, 'EXHIBITED', 4),
 
  -- Nadia Okonkwo (id=5)
  ('Racines inversées',      2018, 'Installation textile et vidéo', 300.00, 250.00, 150.00,
   'Tissus brodés suspendus accompagnés d\'une projection de témoignages oraux.', 18000.00, 'EXHIBITED', 5),
  ('Cartographie intime',    2023, 'Peinture sur cartes géographiques', 80.00,  60.00, NULL,
   'Altération de cartes coloniales avec des pigments naturels africains.', 4500.00, 'FOR_SALE', 5);
 
 
INSERT INTO tagged (id_artwork, id_tag) VALUES
  (1, 1), (1, 7),   -- Éclats I      : abstrait, lumineux
  (2, 1), (2, 8),   -- Éclats II     : abstrait, sombre
  (3, 1), (3, 9),   -- Fragment #7   : abstrait, minimaliste
  (4, 3), (4, 8),   -- Mémoire       : urbain, sombre
  (5, 4), (5, 9),   -- Archipel      : nature, minimaliste
  (6, 5), (6, 8),   -- Frontières 12 : portrait, sombre
  (7, 5), (7, 6),   -- Frontières 23 : portrait, politique
  (8, 2), (8, 3),   -- Quartiers #5  : figuratif, urbain
  (9, 1), (9, 4),   -- Matrice 01    : abstrait, nature
  (10, 1),(10, 9),  -- Matrice 02    : abstrait, minimaliste
  (11, 6),(11, 8),  -- Racines       : politique, sombre
  (12, 6),(12, 2);  -- Cartographie  : politique, figuratif
 
 
INSERT INTO gallery (name, ownerName, contactPhone, rating, website, id_location) VALUES
  ('Galerie Lumière',      'Claire Fontaine',  '+33 1 42 78 56 90', 4.70,
   'https://galerielumiere.fr',      1),  -- Marais, Paris
  ('Espace Confluence Art','Henri Duval',      '+33 4 72 41 20 10', 4.20,
   'https://confluenceart.fr',       2),  -- Lyon
  ('Joliette Contemporain',NULL,               '+33 4 91 55 67 89', 3.90,
   NULL,                             3),  -- Marseille
  ('Chartrons Galerie',    'Isabelle Morin',   '+33 5 56 44 21 32', 4.50,
   'https://chartronsart.fr',        4);  -- Bordeaux
 
 
-- Galerie Lumière : mar-sam 10h-19h, dim 14h-18h
INSERT INTO gallery_hours (dayOfWeek, openTime, closeTime, id_gallery) VALUES
  ('TUE','10:00:00','19:00:00',1),
  ('WED','10:00:00','19:00:00',1),
  ('THU','10:00:00','19:00:00',1),
  ('FRI','10:00:00','19:00:00',1),
  ('SAT','10:00:00','19:00:00',1),
  ('SUN','14:00:00','18:00:00',1),
-- Espace Confluence : lun-ven 9h-18h, sam 10h-17h
  ('MON','09:00:00','18:00:00',2),
  ('TUE','09:00:00','18:00:00',2),
  ('WED','09:00:00','18:00:00',2),
  ('THU','09:00:00','18:00:00',2),
  ('FRI','09:00:00','18:00:00',2),
  ('SAT','10:00:00','17:00:00',2),
-- Joliette Contemporain : mer-dim 11h-19h
  ('WED','11:00:00','19:00:00',3),
  ('THU','11:00:00','19:00:00',3),
  ('FRI','11:00:00','19:00:00',3),
  ('SAT','11:00:00','19:00:00',3),
  ('SUN','11:00:00','19:00:00',3),
-- Chartrons Galerie : jeu-lun 10h-18h30
  ('THU','10:00:00','18:30:00',4),
  ('FRI','10:00:00','18:30:00',4),
  ('SAT','10:00:00','18:30:00',4),
  ('SUN','10:00:00','18:30:00',4),
  ('MON','10:00:00','18:30:00',4);
 
 
INSERT INTO exhibition (title, description, startDate, endDate, curatorName, theme, id_gallery) VALUES
  ('Lumières Intérieures',
   'Exposition collective autour de l\'abstraction lyrique et de la lumière comme matière.',
   '2024-03-01', '2024-05-31', 'Marie-Laure Tissot', 'Lumière et abstraction', 1),
 
  ('Corps & Territoire',
   'Dialogue entre sculpture, installation et photographie sur la notion de frontière corporelle.',
   '2024-04-15', '2024-07-14', 'Jean-Baptiste Coste', 'Corps, mémoire, espace', 2),
 
  ('Mémoires vives',
   'Regards croisés sur la mémoire collective à travers différentes pratiques artistiques.',
   '2024-06-01', '2024-09-30', NULL, 'Mémoire et histoire', 3),
 
  ('Digital/Analogique',
   'L\'hybridation entre les techniques numériques et les pratiques traditionnelles.',
   '2024-09-01', '2024-11-30', 'Sylvie Arnaud', 'Hybridation technologique', 4),
 
  ('Identités multiples',
   'Exposition solo de Nadia Okonkwo, parcours à travers dix ans de création.',
   '2024-10-10', '2025-01-12', 'Pierre Demaret', 'Diaspora et identité', 1);

 
INSERT INTO exhibited (id_artwork, id_exhibition) VALUES
  -- Lumières Intérieures (expo 1, galerie Paris) : Sophie + Nadia
  (1,  1),  -- Éclats I        → Lumières Intérieures
  (2,  1),  -- Éclats II       → Lumières Intérieures
  (12, 1),  -- Cartographie    → Lumières Intérieures
 
  -- Corps & Territoire (expo 2, Lyon) : Karim + Elena + Nadia
  (4,  2),  -- Mémoire ruines  → Corps & Territoire
  (7,  2),  -- Frontières #23  → Corps & Territoire
  (11, 2),  -- Racines         → Corps & Territoire
 
  -- Mémoires vives (expo 3, Marseille) : Elena + Karim + Thomas
  (6,  3),  -- Frontières #12  → Mémoires vives
  (8,  3),  -- Quartiers #5    → Mémoires vives
  (4,  3),  -- Mémoire ruines  → Mémoires vives  (même œuvre dans 2 expos)
  (10, 3),  -- Matrice 02      → Mémoires vives
 
  -- Digital/Analogique (expo 4, Bordeaux) : Thomas + Sophie
  (9,  4),  -- Matrice 01      → Digital/Analogique
  (10, 4),  -- Matrice 02      → Digital/Analogique  (2 expos simultanément)
  (3,  4),  -- Fragment #7     → Digital/Analogique
 
  -- Identités multiples (expo 5, solo Nadia)
  (11, 5),  -- Racines         → Identités multiples
  (12, 5);  -- Cartographie    → Identités multiples
 

 
INSERT INTO workshop (title, dateTime, max_participants, price, duration_minutes, description, level, id_location) VALUES
  ('Introduction à la peinture abstraite',
   '2024-04-06 10:00:00', 12, 65.00, 180,
   'Découverte des techniques de base de la peinture abstraite avec Sophie Marchand.',
   'beginner', 1),
 
  ('Sculpture sur matériaux recyclés',
   '2024-05-18 14:00:00', 8, 90.00, 240,
   'Atelier de sculpture avec Karim Bouras, utilisation de matériaux de récupération.',
   'intermediate', 2),
 
  ('Photographie documentaire urbaine',
   '2024-07-13 09:00:00', 10, 75.00, 300,
   'Sortie terrain avec Elena Vasquez dans les quartiers de Marseille.',
   'intermediate', 3),
 
  ('Gravure et impression numérique',
   '2024-10-05 10:00:00', 6, 120.00, 360,
   'Atelier avancé combinant gravure sur cuivre et retouche numérique avec Thomas Girard.',
   'advanced', 4),
 
  ('Art textile et broderie contemporaine',
   '2024-11-09 13:00:00', 15, 55.00, 150,
   'Introduction aux techniques textiles utilisées dans l\'œuvre de Nadia Okonkwo.',
   'beginner', 1);
 
 
INSERT INTO community_member (name, email, birthYear, phone, membershipType, id_city) VALUES
  ('Alice Renaud',      'alice.renaud@email.fr',    1992, '+33 6 10 20 30 40', 'premium', 1),
  ('Bruno Petit',       'bruno.petit@email.fr',     1988, '+33 6 20 30 40 50', 'free',    1),
  ('Camille Dubois',    'camille.dubois@email.fr',  1995, '+33 6 30 40 50 60', 'premium', 2),
  ('David Moreau',      'david.moreau@email.fr',    1980, '+33 6 40 50 60 70', 'free',    2),
  ('Emma Leroy',        'emma.leroy@email.fr',      1999, NULL,                'premium', 3),
  ('Fabien Garnier',    'fabien.garnier@email.fr',  1975, '+33 6 60 70 80 90', 'free',    4),
  ('Grace Nkemdirim',   'grace.nk@email.be',        1993, '+32 478 00 11 22',  'premium', 5),
  ('Hugo Lambert',      'hugo.lambert@email.fr',    2000, '+33 6 80 90 00 10', 'free',    1),
  ('Inès Beaumont',     'ines.beaumont@email.fr',   1985, '+33 6 90 00 10 20', 'premium', 3),
  ('Julien Clément',    'julien.clement@email.fr',  1971, '+33 6 00 10 20 30', 'free',    6);
 
 
INSERT INTO prefere (id_member, id_discipline) VALUES
  (1, 1), (1, 3),   -- Alice   : Peinture, Photographie
  (2, 2), (2, 6),   -- Bruno   : Sculpture, Installation
  (3, 1), (3, 4),   -- Camille : Peinture, Art numérique
  (4, 3), (4, 6),   -- David   : Photographie, Installation
  (5, 3), (5, 5),   -- Emma    : Photographie, Gravure
  (6, 4), (6, 5),   -- Fabien  : Art numérique, Gravure
  (7, 1), (7, 6),   -- Grace   : Peinture, Installation
  (8, 7), (8, 1),   -- Hugo    : Dessin, Peinture
  (9, 3), (9, 2),   -- Inès    : Photographie, Sculpture
  (10, 1),(10, 7);  -- Julien  : Peinture, Dessin
 
 
INSERT INTO review (id_artwork, id_member, rating, comment, reviewDate) VALUES
  (1,  1, 5, 'Un équilibre lumineux saisissant, j\'ai adoré.',           '2024-03-15'),
  (1,  2, 4, 'Belle maîtrise de la couleur, un peu répétitif.',          '2024-03-20'),
  (1,  3, 5, 'Sophie Marchand confirme tout son talent.',                '2024-04-01'),
  (2,  1, 4, 'Plus sombre mais tout aussi puissant.',                    '2024-04-10'),
  (4,  2, 5, 'La sculpture est impressionnante en vrai, immense.',       '2024-05-02'),
  (4,  4, 3, 'Intéressant mais difficile à appréhender.',                '2024-07-18'),
  (4,  9, 4, 'Un travail de mémoire très touchant.',                     '2024-07-20'),
  (6,  5, 5, 'Bouleversant. La photo documentaire à son meilleur.',      '2024-06-10'),
  (7,  4, 4, 'Regard juste et humain sur une réalité difficile.',        '2024-05-30'),
  (7,  5, 5, 'Elena Vasquez me touche à chaque fois.',                   '2024-06-05'),
  (9,  6, 4, 'L\'hybridation numérique/gravure est très réussie.',       '2024-10-12'),
  (10, 6, 5, 'Matrice 02 dépasse encore Matrice 01, époustouflant.',     '2024-10-13'),
  (11, 7, 5, 'Racines inversées m\'a profondément touché.',              '2024-04-20'),
  (12, 7, 4, 'La Cartographie est subtile et politique à la fois.',      '2024-03-25'),
  (12, 1, 5, 'Nadia Okonkwo est une artiste indispensable.',             '2024-03-18');
 
 
INSERT INTO booking (id_member, id_workshop, bookingDate, paymentStatus) VALUES
  (1, 1, '2024-03-20', 'PAID'),       -- Alice   → Intro peinture abstraite
  (2, 1, '2024-03-22', 'PAID'),       -- Bruno   → Intro peinture abstraite
  (3, 1, '2024-03-25', 'PAID'),       -- Camille → Intro peinture abstraite
  (8, 1, '2024-03-28', 'CANCELLED'),  -- Hugo    → Intro peinture abstraite (annulé)
  (2, 2, '2024-04-30', 'PAID'),       -- Bruno   → Sculpture recyclé
  (4, 2, '2024-05-01', 'PAID'),       -- David   → Sculpture recyclé
  (9, 2, '2024-05-03', 'PENDING'),    -- Inès    → Sculpture recyclé (en attente)
  (5, 3, '2024-06-20', 'PAID'),       -- Emma    → Photo urbaine
  (4, 3, '2024-06-21', 'PAID'),       -- David   → Photo urbaine
  (1, 3, '2024-06-25', 'CANCELLED'),  -- Alice   → Photo urbaine (annulé)
  (6, 4, '2024-09-15', 'PAID'),       -- Fabien  → Gravure numérique
  (3, 4, '2024-09-17', 'PAID'),       -- Camille → Gravure numérique
  (7, 5, '2024-10-20', 'PAID'),       -- Grace   → Textile Nadia
  (1, 5, '2024-10-21', 'PAID'),       -- Alice   → Textile Nadia
  (10,5, '2024-10-22', 'PENDING');    -- Julien  → Textile Nadia (en attente)
