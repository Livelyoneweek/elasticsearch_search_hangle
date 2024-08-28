package kr.co.choi.elastic.elasticsearch.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "msds")  // Elasticsearch에서 사용하는 인덱스 이름
public class Msds {

    @Id
    private String id;
    private String name;
    private String description;
    private String chem_id;
    private String cas_no;
    private String en_no;
    private String ke_no;
    private String last_date;
    private String un_no;

    private String decomposedName;
    private String chosung;

    public void update(String decomposedName, String chosung) {
        this.decomposedName = decomposedName;
        this.chosung = chosung;
    }
}
