package ru.creditbank.apigateway.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.apigateway.entities.UserModel;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UserModel, Long> {

    boolean existsByEmail(String email);

    Optional<UserModel> findByEmail(String email);
}
