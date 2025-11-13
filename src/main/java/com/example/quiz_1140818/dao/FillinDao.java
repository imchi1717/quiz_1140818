package com.example.quiz_1140818.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz_1140818.entity.Fillin;
import com.example.quiz_1140818.entity.FillinId;

import jakarta.transaction.Transactional;

@Repository
public interface FillinDao extends JpaRepository<Fillin, FillinId>{

	@Modifying
	@Transactional
	@Query(value = "insert into fillin (quiz_id, question_id, email, name, phone, age,"
	        + "answer_str, fillin_date) values (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)", nativeQuery = true)
	public void fillin(int quizId, int questionId, String email, String name, //
	        String phone, int age, String answerStr, LocalDate fillinDate);
	
	@Query(value = "select * from fillin where quiz_id = ?1", nativeQuery = true)
	public List<Fillin> getByQuizId(int quizId);
	
}
