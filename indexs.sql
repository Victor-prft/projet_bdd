use artconnect; 

-- Filtrage par statut de paiement (PAID / PENDING / CANCELLED)
-- Utilisé dans v_workshop_availability et les exports comptables
CREATE INDEX idx_booking_payment_status
    ON booking(paymentStatus);

-- Filtrage fréquent par statut (FOR_SALE, SOLD, EXHIBITED)
-- Requêtes : v_artwork_for_sale, pages catalogue, recherches
CREATE INDEX idx_artwork_status
    ON artwork(status);

-- Jointure exhibition → gallery très fréquente
CREATE INDEX idx_exhibition_gallery
    ON exhibition(id_gallery);