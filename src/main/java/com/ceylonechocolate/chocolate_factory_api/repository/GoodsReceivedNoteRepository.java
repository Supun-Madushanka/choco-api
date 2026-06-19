package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoodsReceivedNoteRepository
        extends JpaRepository<GoodsReceivedNote, Long> {

    List<GoodsReceivedNote> findAllByOrderByCreatedAtDesc();

    List<GoodsReceivedNote> findByPurchaseOrderId(Long purchaseOrderId);

    List<GoodsReceivedNote> findByStatus(
            GoodsReceivedNote.GrnStatus status);

    @Query("SELECT g.grnNumber FROM GoodsReceivedNote g " +
            "ORDER BY g.id DESC LIMIT 1")
    Optional<String> findLastGrnNumber();
}