ALTER TABLE tb_product ADD COLUMN title VARCHAR(255);
ALTER TABLE tb_product ADD COLUMN category_id BIGINT;
ALTER TABLE tb_product ADD COLUMN seller_id BIGINT;
ALTER TABLE tb_product RENAME COLUMN image TO image_url;

-- Copiar dados antigos para os novos campos
UPDATE tb_product SET title = brand || ' ' || model;
UPDATE tb_product SET category_id = 1, seller_id = 1;

-- Remover colunas antigas
ALTER TABLE tb_product DROP COLUMN brand;
ALTER TABLE tb_product DROP COLUMN model;
ALTER TABLE tb_product DROP COLUMN currency;