package kr.co.choi.elastic.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import kr.co.choi.elastic.elasticsearch.entity.Msds;
import kr.co.choi.elastic.elasticsearch.repository.MsdsRepository;
import kr.co.choi.elastic.elasticsearch.util.HangulUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class MsdsService {

    private final MsdsRepository msdsRepository;
    private final HangulUtil hangulUtil;
    private final ElasticsearchOperations elasticsearchOperations;


    /**
     * 단일 업로드
     */
    public Msds save(Msds msds) {
        log.info("### MsdsService.save");
        String[] strings = hangulUtil.decomposeHangul(msds.getName());
        msds.update(strings[0], strings[1]);
        return msdsRepository.save(msds);
    }

    /**
     * 리스트 업로드
     */
    public List<Msds> saveAll(List<Msds> msdsList) {
        log.info("### MsdsService.saveAll");
        msdsList.forEach(msds -> {
            String[] decomposedAndChosung = hangulUtil.decomposeHangul(msds.getName());
            msds.update(decomposedAndChosung[0], decomposedAndChosung[1]);
        });

        Iterable<Msds> msdsIterable = msdsRepository.saveAll(msdsList);
        return StreamSupport.stream(msdsIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * 검색 메소드
     */
    public List<Msds> findByNameSimilar(String name) {
        log.info("### MsdsService.findByNameSimilar");
        String[] decomposed = hangulUtil.decomposeHangul(name);
        String decomposedName = decomposed[0];
        String chosung = decomposed[1];

        // 검색 가중치 적용
        BoolQuery boolQuery = BoolQuery.of(b -> b
                .should(MatchQuery.of(m -> m.field("chosung").query(chosung).boost(3.0f))._toQuery())
                .should(MatchQuery.of(m -> m.field("chosung.ngram").query(chosung).boost(2.5f))._toQuery())
                .should(MatchQuery.of(m -> m.field("decomposedName").query(decomposedName).boost(2.0f))._toQuery())
                .should(MatchQuery.of(m -> m.field("decomposedName.ngram").query(decomposedName).boost(1.5f))._toQuery())
                .should(MatchQuery.of(m -> m.field("name.ngram").query(name))._toQuery())
                .should(WildcardQuery.of(w -> w.field("name").wildcard("*" + name + "*"))._toQuery())
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolQuery))
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<Msds> searchHits = elasticsearchOperations.search(searchQuery, Msds.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * csv 파일 업로드 메소드
     */
    public List<Msds> uploadCsv(MultipartFile file) {
        log.info("### MsdsService.uploadCsv");

        List<Msds> msdsList = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            for (CSVRecord csvRecord : csvParser) {
                Msds msds = new Msds();
                msds.setChem_id(csvRecord.get("chem_id"));
                msds.setCas_no(csvRecord.get("cas_no"));
                msds.setEn_no(csvRecord.get("en_no"));
                msds.setKe_no(csvRecord.get("ke_no"));
                msds.setLast_date(csvRecord.get("last_date"));
                msds.setUn_no(csvRecord.get("un_no"));
                msds.setName(csvRecord.get("chem_name_kor"));

                String[] strings = hangulUtil.decomposeHangul(msds.getName());
                msds.update(strings[0], strings[1]);

                msdsList.add(msds);
            }

            Iterable<Msds> msdsIterable = msdsRepository.saveAll(msdsList);
            return StreamSupport.stream(msdsIterable.spliterator(), false)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage());
            return msdsList;
        }
    }

    public Optional<Msds> findById(String id) {
        return msdsRepository.findById(id);
    }

    public Iterable<Msds> findAll() {
        return msdsRepository.findAll();
    }

    public void deleteById(String id) {
        msdsRepository.deleteById(id);
    }
}
