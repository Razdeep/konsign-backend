ALTER TABLE bill FORCE ROW LEVEL SECURITY;
ALTER TABLE buyer FORCE ROW LEVEL SECURITY;
ALTER TABLE supplier FORCE ROW LEVEL SECURITY;
ALTER TABLE transport FORCE ROW LEVEL SECURITY;
ALTER TABLE lrpm FORCE ROW LEVEL SECURITY;
ALTER TABLE collection_voucher FORCE ROW LEVEL SECURITY;
ALTER TABLE collection_voucher_item FORCE ROW LEVEL SECURITY;

CREATE POLICY bill_tenant_isolation
    ON bill
    USING (
    tenant_id = current_setting('app.tenant_id')::varchar
    );

CREATE POLICY supplier_tenant_isolation
    ON supplier
    USING (
    tenant_id = current_setting('app.tenant_id')::varchar
    );