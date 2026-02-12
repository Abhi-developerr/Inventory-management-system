package com.example.demo.repository;

import com.example.demo.entity.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {

    List<SupplierProduct> findByProductId(Long productId);

    List<SupplierProduct> findBySupplierId(Long supplierId);

    Optional<SupplierProduct> findBySupplierIdAndProductId(Long supplierId, Long productId);

    boolean existsBySupplierIdAndProductId(Long supplierId, Long productId);

    void deleteBySupplierIdAndProductId(Long supplierId, Long productId);
}
