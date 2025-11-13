package com.example.quiz_1140818.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.quiz_1140818.entity.Account;

@Repository
public interface AccountDao extends JpaRepository<Account, String> {

	@Modifying
	@Transactional
	@Query(value = "insert into account (account, password, is_admin, name, phone, email, age) " //
			+ " values(?1, ?2, ?3, ?4, ?5, ?6, ?7)", nativeQuery = true)
	public void addInfo(String account, String password, boolean admin, String name, 
			 String phone, String email, int age);

	@Query(value = " select count(account) from account where account = ?1 ", //
			nativeQuery = true)
	public int selectCountByAccount(String account);

	@Query(value = " select * from account where account = ?1 ", //
			nativeQuery = true)
	public Account selectByAccount(String account);
}
