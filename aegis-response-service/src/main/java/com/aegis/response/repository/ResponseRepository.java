package com.aegis.response.repository;

import com.aegis.response.entity.ResponseEntityModel;
import com.aegis.response.entity.ResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<ResponseEntityModel, String> {
    List<ResponseEntityModel> findByIncidentId(String incidentId);
    List<ResponseEntityModel> findByResponseStatus(ResponseStatus status);
}
