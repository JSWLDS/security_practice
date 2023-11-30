package com.example.security_practice.repository;
import com.example.security_practice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberDetailRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String username);
}