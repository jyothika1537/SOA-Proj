package com.aegis.incident.repository;

import com.aegis.incident.entity.Incident;
import com.aegis.incident.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, String> {
    List<Incident> findByIncidentStatus(IncidentStatus status);
}
