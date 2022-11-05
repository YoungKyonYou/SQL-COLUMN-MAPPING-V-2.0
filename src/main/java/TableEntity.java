import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TableEntity {

    private String ToBeTableName;
    private String AsIsTableName;

    //key로 As-Is 컬럼 명 value로 As-Is 컬럼의 논리명과 물리명 그리고 To-Be 컬럼의 논리명과 물리명
    private HashMap<String, ColumnEntity> columnMappingMap=new HashMap<>();

    public HashMap<String, ColumnEntity> getColumnMappingMap() {
        return columnMappingMap;
    }

    public void insertMap(String key, ColumnEntity value){
        columnMappingMap.put(key, value);
    }

    public String getToBeTableName() {
        return ToBeTableName;
    }

    public void setToBeTableName(String toBeTableName) {
        ToBeTableName = toBeTableName;
    }

    public String getAsIsTableName() {
        return AsIsTableName;
    }

    public void setAsIsTableName(String asIsTableName) {
        AsIsTableName = asIsTableName;
    }

    public Set<String> alias = new HashSet<>();

    public Set<Integer> leftBracketCount = new HashSet<>();

    public void setColumnMappingMap(HashMap<String, ColumnEntity> columnMappingMap) {
        this.columnMappingMap = columnMappingMap;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    public void putAliasInSet(String al){
        this.alias.add(al);
    }

    public boolean hasAlias(String al){
        return this.alias.contains(al);
    }

    public Set<Integer> getLeftBracketCount() {
        return leftBracketCount;
    }

    public void setLeftBracketCount(Set<Integer> leftBracketCount) {
        this.leftBracketCount = leftBracketCount;
    }

    public void putLeftBracketCount(int count){
        this.leftBracketCount.add(count);
    }
}
