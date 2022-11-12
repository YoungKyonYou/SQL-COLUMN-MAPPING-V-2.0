import java.util.*;

/**
 * executeAliasColumnMappingAndNonAliasColumnMapping()를 거친후 다시 extractword 한 것이다.
 */
public class WordEntity {
    private String word;
    private int startIdx;
    private int endIdx;
    //매핑되기 전 AS-IS 컬럼 명
    private String asIsNm;

    //key : to-be 컬럼명 value:TableEntity
    private List<TableEntity> tableEntity = new ArrayList<>();

    private Set<ColumnEntity> columnEntitySet = new HashSet<>();

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getStartIdx() {
        return startIdx;
    }

    public void setStartIdx(int startIdx) {
        this.startIdx = startIdx;
    }

    public String getAsIsNm() {
        return asIsNm;
    }

    public void setAsIsNm(String asIsNm) {
        this.asIsNm = asIsNm;
    }

    public int getEndIdx() {
        return endIdx;
    }

    public void setEndIdx(int endIdx) {
        this.endIdx = endIdx;
    }


    public void addTableEntity(TableEntity tableEntity){
        this.tableEntity.add(tableEntity);
    }
    public void addColumnEntitySet(ColumnEntity columnEntity){
        this.columnEntitySet.add(columnEntity);
    }

    public List<TableEntity> getTableEntity() {
        return tableEntity;
    }

    public void setTableEntity(List<TableEntity> tableEntity) {
        this.tableEntity = tableEntity;
    }

    public Set<ColumnEntity> getColumnEntitySet() {
        return columnEntitySet;
    }

    public void setColumnEntitySet(Set<ColumnEntity> columnEntitySet) {
        this.columnEntitySet = columnEntitySet;
    }
}
