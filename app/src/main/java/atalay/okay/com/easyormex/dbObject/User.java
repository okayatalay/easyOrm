package atalay.okay.com.easyormex.dbObject;

/**
 * Created by 1 on 22.09.2018.
 */

public class User extends Person {
    private Integer ID;
    private Integer number, infoID;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getInfoID() {
        return infoID;
    }

    public void setInfoID(Integer infoID) {
        this.infoID = infoID;
    }
}
