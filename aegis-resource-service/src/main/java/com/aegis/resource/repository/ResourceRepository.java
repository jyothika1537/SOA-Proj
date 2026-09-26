package com.aegis.resource.repository;

import com.aegis.resource.entity.Resource;
import com.aegis.resource.entity.ResourceStatus;
import com.aegis.resource.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {
    List<Resource> findByResourceType(ResourceType type);
    List<Resource> findByStatus(ResourceStatus status);

    @Modifying
    @Query(value = "UPDATE resources SET available_quantity = available_quantity - :quantity WHERE resource_id = :resourceId AND available_quantity >= :quantity", nativeQuery = true)
    int allocateQuantity(@Param("resourceId") String resourceId, @Param("quantity") Integer quantity);

    @Modifying
    @Query(value = "UPDATE resources SET available_quantity = available_quantity + :quantity WHERE resource_id = :resourceId", nativeQuery = true)
    int releaseQuantity(@Param("resourceId") String resourceId, @Param("quantity") Integer quantity);
}
