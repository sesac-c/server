package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.repository.search.NoticeSearch;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeSearch {

}
