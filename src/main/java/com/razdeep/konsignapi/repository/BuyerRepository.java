package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.BuyerEntity;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends JpaRepository<BuyerEntity, String> {
    List<BuyerEntity> findAllBuyerByBuyerNameAndTenantId(@NonNull String buyerName, @NonNull String tenantId);

    List<BuyerEntity> findAllByTenantId(@NonNull String tenantId);

    Optional<BuyerEntity> findByBuyerIdAndTenantId(@NonNull String buyerId, @NonNull String tenantId);

    @Query("""
        select b.buyerName
        from BuyerEntity b
        where b.buyerId = :buyerId
            and b.tenantId = :tenantId
    """)
    String findBuyerNameByBuyerId(String buyerId, String tenantId);

    @Query(value = """
with collection_joined as (
select
	*
from
	collection_voucher
join collection_voucher_item on
	voucher_no = fk_collection_voucher_id
where buyer_buyer_id = :buyerId
and collection_voucher.tenant_id = :tenantId),
bill_joined as (
select
	*
from
	bill
join supplier on
	bill.fk_supplier_id = supplier.supplier_id
join buyer on
	bill.fk_buyer_id = buyer.buyer_id
)
select
	bill_joined.bill_no as billNo,
	bill_joined.bill_date as billDate,
	bill_joined.bill_amount as billAmount,
	bill_joined.supplier_name as supplierName,
	collection_joined.voucher_no as voucherNo,
	collection_joined.amount_collected as amountCollected,
	collection_joined.bank as bank,
	collection_joined.dd_no as ddNo,
	collection_joined.dd_date as ddDate
from
	bill_joined
join collection_joined
on
	bill_joined.bill_no = collection_joined.bill_bill_no;
            """, nativeQuery = true)
    List<BillCollectionProjection> computeBuyerLedger(@NonNull String buyerId, @NonNull String tenantId);
}
