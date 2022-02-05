package eu.dronespots.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.dronespots.entities.Dronespots;

@Repository
public interface DronespotRepository extends JpaRepository<Dronespots, String> {
}
