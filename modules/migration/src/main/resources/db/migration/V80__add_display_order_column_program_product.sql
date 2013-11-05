ALTER TABLE program_products
	ADD displayOrder INT null;

-- migrate the existing data
UPDATE program_products AS pp
		SET displayOrder = pr.displayOrder
	FROM  products pr
	WHERE
		pr.id = pp.productid;