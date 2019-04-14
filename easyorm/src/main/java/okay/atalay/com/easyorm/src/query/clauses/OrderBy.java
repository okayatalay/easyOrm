package okay.atalay.com.easyorm.src.query.clauses;

/**
 * Created by 1 on 22.03.2018.
 */

public class OrderBy {
    private String name;
    private String describe;

    public OrderBy(String name, String describe) {
        if (name == null || name.equals("")) {
            name = "?";
        }
        this.name = name;
        if ("desc".equals(describe)) {
            this.describe = "desc";
        } else {
            this.describe = "asc";
        }
    }

    public OrderBy() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    @Override
    public String toString() {
        return "name:" + name + ", describe:" + describe;
    }
}
