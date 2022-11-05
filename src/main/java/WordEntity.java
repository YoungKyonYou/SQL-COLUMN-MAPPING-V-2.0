/**
 * executeAliasColumnMappingAndNonAliasColumnMapping()를 거친후 다시 extractword 한 것이다.
 */
public class WordEntity {
    private String word;
    private int startIdx;
    //매핑되기 전 AS-IS 컬럼 명
    private String asIsNm;

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
}
