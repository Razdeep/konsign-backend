CREATE SEQUENCE app_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- public.app_user definition

-- Drop table

-- DROP TABLE public.app_user;

CREATE TABLE public.app_user (
	id bigint NOT NULL DEFAULT nextval('app_user_seq'),
	active bool NOT NULL,
	email varchar(255) NULL,
	mobile varchar(255) NULL,
	"password" varchar(255) NULL,
	roles varchar(255) NULL,
	tenant_id varchar(255) NULL,
	username varchar(255) NULL,
	CONSTRAINT app_user_pkey PRIMARY KEY (id),
	CONSTRAINT uk_3k4cplvh82srueuttfkwnylq0 UNIQUE (username)
);


-- public.buyer definition

-- Drop table

-- DROP TABLE public.buyer;

CREATE TABLE public.buyer (
	buyer_id varchar(255) NOT NULL,
	created_at timestamptz(6) NOT NULL,
	tenant_id varchar(255) NOT NULL,
	updated_at timestamptz(6) NOT NULL,
	buyer_name varchar(255) NULL,
	CONSTRAINT buyer_pkey PRIMARY KEY (buyer_id)
);


-- public.refresh_tokens definition

-- Drop table

-- DROP TABLE public.refresh_tokens;

CREATE TABLE public.refresh_tokens (
	id uuid NOT NULL,
	created_at timestamptz(6) NULL,
	device_id varchar(255) NULL,
	expires_at timestamptz(6) NOT NULL,
	revoked bool NOT NULL,
	tenant_id varchar(255) NOT NULL,
	"token" varchar(255) NOT NULL,
	user_id int8 NOT NULL,
	CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id),
	CONSTRAINT uk_ghpmfn23vmxfu3spu3lfg4r2d UNIQUE (token)
);


-- public.supplier definition

-- Drop table

-- DROP TABLE public.supplier;

CREATE TABLE public.supplier (
	supplier_id varchar(255) NOT NULL,
	created_at timestamptz(6) NOT NULL,
	tenant_id varchar(255) NOT NULL,
	updated_at timestamptz(6) NOT NULL,
	supplier_name varchar(255) NULL,
	CONSTRAINT supplier_pkey PRIMARY KEY (supplier_id)
);


-- public.transport definition

-- Drop table

-- DROP TABLE public.transport;

CREATE TABLE public.transport (
	transport_id varchar(255) NOT NULL,
	created_at timestamptz(6) NOT NULL,
	tenant_id varchar(255) NOT NULL,
	updated_at timestamptz(6) NOT NULL,
	transport_name varchar(255) NULL,
	CONSTRAINT transport_pkey PRIMARY KEY (transport_id)
);


-- public.bill definition

-- Drop table

-- DROP TABLE public.bill;

CREATE TABLE public.bill (
	bill_no varchar(255) NOT NULL,
	created_at timestamptz(6) NOT NULL,
	tenant_id varchar(255) NOT NULL,
	updated_at timestamptz(6) NOT NULL,
	bill_amount numeric(15, 2) NOT NULL,
	bill_date date NULL,
	lr_date date NULL,
	fk_buyer_id varchar(255) NOT NULL,
	fk_supplier_id varchar(255) NOT NULL,
	fk_transport_id varchar(255) NOT NULL,
	CONSTRAINT bill_pkey PRIMARY KEY (bill_no),
	CONSTRAINT fk8dbradkkmu6cma9iapnr4jq5w FOREIGN KEY (fk_supplier_id) REFERENCES public.supplier(supplier_id),
	CONSTRAINT fkfh275s9c96g41fp8sjus06hcx FOREIGN KEY (fk_transport_id) REFERENCES public.transport(transport_id),
	CONSTRAINT fkjym3dsq7gnraqyg4y2sr5hwwk FOREIGN KEY (fk_buyer_id) REFERENCES public.buyer(buyer_id)
);


-- public.collection_voucher definition

-- Drop table

-- DROP TABLE public.collection_voucher;

CREATE TABLE public.collection_voucher (
	voucher_no varchar(255) NOT NULL,
	created_at timestamptz(6) NOT NULL,
	tenant_id varchar(255) NOT NULL,
	updated_at timestamptz(6) NOT NULL,
	voucher_date date NULL,
	buyer_buyer_id varchar(255) NULL,
	CONSTRAINT collection_voucher_pkey PRIMARY KEY (voucher_no),
	CONSTRAINT fkbnfam849qftyex61g3l21ybmk FOREIGN KEY (buyer_buyer_id) REFERENCES public.buyer(buyer_id)
);


-- public.collection_voucher_item definition

-- Drop table

-- DROP TABLE public.collection_voucher_item;

CREATE TABLE public.collection_voucher_item (
	collection_voucher_item_id varchar(255) NOT NULL,
	created_at timestamptz(6) NOT NULL,
	tenant_id varchar(255) NOT NULL,
	updated_at timestamptz(6) NOT NULL,
	amount_collected numeric(15, 2) NOT NULL,
	bank varchar(255) NULL,
	dd_date date NULL,
	dd_no varchar(255) NULL,
	bill_bill_no varchar(255) NULL,
	fk_collection_voucher_id varchar(255) NULL,
	CONSTRAINT collection_voucher_item_pkey PRIMARY KEY (collection_voucher_item_id),
	CONSTRAINT fkdqvohoxw5qlcx40839qp2j3ip FOREIGN KEY (fk_collection_voucher_id) REFERENCES public.collection_voucher(voucher_no),
	CONSTRAINT fkkve213c3ua7ctayegtuf4c80c FOREIGN KEY (bill_bill_no) REFERENCES public.bill(bill_no)
);


-- public.lrpm definition

-- Drop table

-- DROP TABLE public.lrpm;

CREATE TABLE public.lrpm (
	lr_pm_id varchar(255) NOT NULL,
	created_at timestamptz(6) NOT NULL,
	tenant_id varchar(255) NOT NULL,
	updated_at timestamptz(6) NOT NULL,
	lr varchar(255) NULL,
	pm varchar(255) NULL,
	fk_bill_no varchar(255) NOT NULL,
	CONSTRAINT lrpm_pkey PRIMARY KEY (lr_pm_id),
	CONSTRAINT fk6guy60m0gcijopxlpbsi81etu FOREIGN KEY (fk_bill_no) REFERENCES public.bill(bill_no)
);