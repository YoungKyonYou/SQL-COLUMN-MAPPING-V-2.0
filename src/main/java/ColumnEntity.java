import java.util.Objects;

public class ColumnEntity {
    private String ToBeLogicalColName;
    private String ToBePhysicalColName;
    private String ToBeDataType;
    private String ToBeDataLen;
    private String AsIsLogicalColName;
    private String AsIsPhysicalColName;
    private String ToBeTableName;
    private String AsIsTableName;

    public void setToBeLogicalColName(String toBeLogicalColName) {
        ToBeLogicalColName = toBeLogicalColName;
    }

    public String getToBePhysicalColName() {
        return ToBePhysicalColName;
    }

    public void setToBePhysicalColName(String toBePhysicalColName) {
        ToBePhysicalColName = toBePhysicalColName;
    }

    public String getAsIsLogicalColName() {
        return AsIsLogicalColName;
    }

    public void setAsIsLogicalColName(String asIsLogicalColName) {
        AsIsLogicalColName = asIsLogicalColName;
    }

    public String getAsIsPhysicalColName() {
        return AsIsPhysicalColName;
    }


    public String getToBeDataType() {
        return ToBeDataType;
    }

    public void setToBeDataType(String toBeDataType) {
        ToBeDataType = toBeDataType;
    }

    public String getToBeDataLen() {
        return ToBeDataLen;
    }

    public void setToBeDataLen(String toBeDataLen) {
        ToBeDataLen = toBeDataLen;
    }

    public void setAsIsPhysicalColName(String asIsPhysicalColName) {

        AsIsPhysicalColName = asIsPhysicalColName;
    }

    public String getToBeLogicalColName() {
        return ToBeLogicalColName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnEntity that = (ColumnEntity) o;
        return Objects.equals(ToBeLogicalColName, that.ToBeLogicalColName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ToBeLogicalColName);
    }
}
