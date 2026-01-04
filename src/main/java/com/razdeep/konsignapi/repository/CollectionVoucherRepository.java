package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.CollectionVoucherEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionVoucherRepository extends JpaRepository<CollectionVoucherEntity, String> {

    @Query(value = """
                    select * \
                    from collection_vouchers join collection_voucher_item \
                    on collection_vouchers.voucher_no = collection_voucher_item.fk_collection_voucher_id \
                    where collection_vouchers.buyer_buyer_id = ?1\
                    """, nativeQuery = true)
    List<CollectionVoucherEntity> getCollectedAmountInfoForBuyerId(String buyerId);

    @Query(
            value = "select sum(amount_collected) " + "from collection_voucher_item where bill_bill_no = ?1",
            nativeQuery = true)
    Double getCollectedAmountForBillNo(String BillNo);

    CollectionVoucherEntity getCollectionVoucherByVoucherNo(String voucherNo);
}
