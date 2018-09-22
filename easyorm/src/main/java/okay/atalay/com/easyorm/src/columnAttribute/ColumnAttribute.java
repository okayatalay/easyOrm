package okay.atalay.com.easyorm.src.columnAttribute;

public class ColumnAttribute {

    private String name;
    private String value;

    public ColumnAttribute(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

}
