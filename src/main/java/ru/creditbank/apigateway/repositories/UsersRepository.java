package ru.creditbank.apigateway.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.apigateway.core.UserModel;

import java.util.Optional;

@Repository
public interface UsersRepository extends PagingAndSortingRepository<UserModel, Long> {

    boolean existsByEmail(String email);

    void save(UserModel user);

    Optional<UserModel> findByEmail(String email);
}
