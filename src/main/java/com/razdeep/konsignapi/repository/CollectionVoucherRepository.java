package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.CollectionVoucherEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionVoucherRepository extends JpaRepository<CollectionVoucherEntity, String> {

    @Query(value = """
                    select * \
                    from collection_voucher join collection_voucher_item \
                    on collection_voucher.voucher_no = collection_voucher_item.fk_collection_voucher_id \
                    where collection_voucher.buyer_buyer_id = ?1\
                    """, nativeQuery = true)
    List<CollectionVoucherEntity> getCollectedAmountInfoForBuyerId(String buyerId);

    @Query(
            value = "select sum(amount_collected) " + "from collection_voucher_item where bill_bill_no = ?1",
            nativeQuery = true)
    BigDecimal getCollectedAmountForBillNo(String BillNo);

    CollectionVoucherEntity getCollectionVoucherByVoucherNo(String voucherNo);
}
