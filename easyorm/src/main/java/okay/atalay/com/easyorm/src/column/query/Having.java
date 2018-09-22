package okay.atalay.com.easyorm.src.column.query;

import okay.atalay.com.easyorm.src.exception.SqlNotFoundException;

/**
 * Created by 1 on 23.03.2018.
 */

public class Having {
    private String name;
    private String value;
    private boolean avg;
    private boolean sum;
    private boolean count;
    private boolean less;
    private boolean greater;
    private boolean equals;
    private String process;

    public Having(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isAvg() {
        return avg;
    }

    public void setAvg(boolean avg) {
        this.avg = avg;
    }

    public boolean isSum() {
        return sum;
    }

    public void setSum(boolean sum) {
        this.sum = sum;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
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

    public String getProcess() {
        return process;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setProcess(String process) throws SqlNotFoundException {
        if (process.equals("or") || process.equals("and")) {
            this.process = process;
        } else if (process.equals("")) {
            this.process = "and";
        } else {
            throw new SqlNotFoundException("Invalid process type for " + name);
        }
    }
}
