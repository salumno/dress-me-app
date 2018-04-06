package ru.kpfu.itis.dressmeapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.dressmeapp.model.User;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
