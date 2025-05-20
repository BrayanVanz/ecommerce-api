package br.com.compass.ecommerce_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.compass.ecommerce_api.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
