package kr.co.choi.elastic.elasticsearch.repository;

import kr.co.choi.elastic.elasticsearch.entity.Msds;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface MsdsRepository extends ElasticsearchRepository<Msds, String> {

    List<Msds> findByDecomposedNameContaining(String decomposedName, Pageable pageable);

    @Query("{" +
            "  \"bool\": {" +
            "    \"should\": [" +
            "      {\"match\": {\"chosung\": {\"query\": \"?0\", \"boost\": 2}}}, " +
            "      {\"match\": {\"decomposedName\": \"?1\"}}" +
            "    ]" +
            "  }" +
            "}")
    List<Msds> findByNameSimilar(String chosung, String decomposedName, Pageable pageable);
}
