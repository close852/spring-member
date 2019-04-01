package com.cjhm.member.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cjhm.member.entity.User;

@Repository
public interface MemberRepository extends CrudRepository<User, Long>{


	public User findByEmail(String email);

	public User findByEmailAndPrincipal(String email,String principal);
}
