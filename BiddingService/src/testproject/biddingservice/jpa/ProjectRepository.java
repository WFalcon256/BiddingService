package testproject.biddingservice.jpa;

import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, BigInteger>
{}
