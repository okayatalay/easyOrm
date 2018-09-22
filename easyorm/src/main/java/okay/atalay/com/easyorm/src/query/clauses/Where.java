package okay.atalay.com.easyorm.src.query.clauses;


import okay.atalay.com.easyorm.src.exception.SqlNotFoundException;
import okay.atalay.com.easyorm.src.query.subQuery.Select;

public class Where {
    private String name;
    private String value;
    private String process;
    private Select select;
    private boolean _static = true;
    private String type;
    private boolean like;
    private boolean equals;
    private boolean less;
    private boolean greater;

    public Where(String name, String value, String process, String _static, String type, String like, String equals) throws SqlNotFoundException {
        super();
        this.name = name;
        if (value.equals(""))
            value = "?";
        this.value = value;
        if (process.equals("or") || process.equals("and")) {
            this.process = process;
        } else if (process.equals("")) {
            this.process = "and";
        } else {
            throw new SqlNotFoundException("Invalid process type for " + name);
        }
        if (_static.equals("false")) {
            this._static = false;
        }
        if (type.equals("")) {
            this.type = "varchar";
        } else {
            this.type = type;
        }
        if ("true".equals(like)) {
            this.type = "varchar";
            this.like = true;
        } else {
            this.like = false;
        }
        if ("no".equals(equals)) {
            this.equals = false;
        } else {
            this.equals = true;
        }

    }

    public boolean isLess() {
        return less;
    }

    public void setLess(boolean less) {
        this.less = less;
    }

    public boolean isGreater() {
        return greater;
    }

    public void setGreater(boolean greater) {
        this.greater = greater;
    }

    public boolean isEquals() {
        return equals;
    }

    public void setEquals(boolean equals) {
        this.equals = equals;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean is_static() {
        return _static;
    }

    public void set_static(boolean _static) {
        this._static = _static;
    }

    public Select getSelect() {
        return select;
    }

    public void setSelect(Select select) {
        this.select = select;
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

    public void setValue(String value) {
        this.value = value;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
