package com.example.accessingdatajpa.Models.Repository;

import com.example.accessingdatajpa.Models.Entity.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Integer> {
    Optional<Queue> findByName(String name);
}

