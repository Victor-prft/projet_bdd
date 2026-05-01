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