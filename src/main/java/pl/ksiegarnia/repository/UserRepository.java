package pl.ksiegarnia.repository;



import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.ksiegarnia.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>
{
public User findByEmail(String email);

}
