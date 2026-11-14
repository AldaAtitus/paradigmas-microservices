-- Remove a constraint antiga que só aceitava 0 e 1
ALTER TABLE tb_user DROP CONSTRAINT IF EXISTS tb_user_type_check;
-- Adiciona a nova que aceita 0 (ADMIN), 1 (BUYER) e 2 (SELLER)
ALTER TABLE tb_user ADD CONSTRAINT tb_user_type_check CHECK (type IN (0, 1, 2));