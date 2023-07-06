package com.contactManager.repository;

import com.contactManager.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Integer> {

//    @Query("from Contact as c where c.user.id =:userId")
//    List<Contact> findContactsByUser(@Param("userId") int userId);

//    pagination
    @Query("from Contact as c where c.user.id =:userId")
    Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
}
