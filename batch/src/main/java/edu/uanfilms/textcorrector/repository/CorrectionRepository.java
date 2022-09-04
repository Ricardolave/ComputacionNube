package edu.uanfilms.textcorrector.repository;

import edu.uanfilms.textcorrector.domain.BadWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public
interface CorrectionRepository extends JpaRepository<BadWord, Long> {

}
