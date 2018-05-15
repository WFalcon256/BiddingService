package testproject.biddingservice.jpa;

import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, BigInteger>
{}
