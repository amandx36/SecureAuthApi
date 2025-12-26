package com.SecureAuth.SecureAuth.Api.repository;

import com.SecureAuth.SecureAuth.Api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// long become primary key
public interface UserRepository extends JpaRepository<User, Long> {

    // Optional :) Kisi value ke present ya absent hone ko safely handle karna
    // finding the user using mail id
    Optional<User> findByEmail(String email);

    // check if email exist or not
    boolean existsByEmail(String email);

    // find the user by )Auth2 provider
    Optional<User> findByProviderAndProviderId(User.AuthProvider provider, String providerId);

    // find user by provider id
    Optional<User> findByProviderId(String providerId);
}
