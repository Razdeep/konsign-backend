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
    List<BuyerEntity> findAllBuyerByBuyerNameAndAgencyId(@NonNull String buyerName, @NonNull String agencyId);

    List<BuyerEntity> findAllByAgencyId(@NonNull String agencyId);

    Optional<BuyerEntity> findByBuyerIdAndAgencyId(@NonNull String buyerId, @NonNull String agencyId);

    @Query("""
        select b.buyerName
        from BuyerEntity b
        where b.buyerId = :buyerId
            and b.agencyId = :agencyId
    """)
    String findBuyerNameByBuyerId(String buyerId, String agencyId);

    //    @Query(value = """
    //        SELECT
    //            b.bill_no                      AS billNo,
    //            b.bill_date                    AS billDate,
    //            b.bill_amount                  AS billAmount,
    //            s.supplier_name                AS supplierName,
    //            cv.voucher_no                  AS voucherNo,
    //            cvi.amount_collected           AS amountCollected,
    //            cv.bank                        AS bank,
    //            cv.dd_no                       AS ddNo,
    //            cv.dd_date                     AS ddDate
    //        FROM bill b
    //        JOIN supplier s
    //            ON s.supplier_id = b.supplier_id
    //        JOIN collection_voucher cv
    //            ON b.bill_no = cv.bill_bill_no
    //        JOIN collection_voucher_item cvi
    //            ON cv.voucher_no = cvi.fk_collection_voucher_id
    //        """,
    //            nativeQuery = true
    //    )
    //    List<BillCollectionProjection> computeBuyerLedger();

}
