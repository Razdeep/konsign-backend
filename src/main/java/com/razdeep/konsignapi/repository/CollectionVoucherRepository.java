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
                    select cv.* \
                    from collection_voucher cv join collection_voucher_item cvi \
                    on cv.voucher_no = cvi.fk_collection_voucher_id \
                    where cv.buyer_buyer_id = ?1\
                    """, nativeQuery = true)
    List<CollectionVoucherEntity> getCollectedAmountInfoForBuyerId(String buyerId);

    @Query(
            value = "select sum(amount_collected) " + "from collection_voucher_item where bill_bill_no = ?1",
            nativeQuery = true)
    BigDecimal getCollectedAmountForBillNo(String BillNo);

    CollectionVoucherEntity getCollectionVoucherByVoucherNo(String voucherNo);
}
