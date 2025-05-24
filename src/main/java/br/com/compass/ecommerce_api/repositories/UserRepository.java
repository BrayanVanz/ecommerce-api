package br.com.compass.ecommerce_api.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.compass.ecommerce_api.entities.User;
import br.com.compass.ecommerce_api.enums.UserRole;
import br.com.compass.ecommerce_api.projections.UserProjection;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    @Query("SELECT u.role FROM User u WHERE u.email LIKE :email")
    UserRole findRoleByEmail(String email);

    @Query("SELECT u FROM User u")
    Page<UserProjection> findAllPageable(Pageable pageable);
}
