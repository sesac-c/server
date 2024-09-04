package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}