package iii.com.chumeet.VO;

import java.sql.Timestamp;

/**
 * Created by sonic on 2017/10/6.
 */

public class ActMemVO {
    private Integer actID;
    private Integer memID;
    private Integer actMemStatus;
    private Timestamp actJoinDate;
    private Integer actStar;
    private Timestamp actStarDate;
    public Integer getActID() {
        return actID;
    }
    public void setActID(Integer actID) {
        this.actID = actID;
    }
    public Integer getMemID() {
        return memID;
    }
    public void setMemID(Integer memID) {
        this.memID = memID;
    }
    public Integer getActMemStatus() {
        return actMemStatus;
    }
    public void setActMemStatus(Integer actMemStatus) {
        this.actMemStatus = actMemStatus;
    }
    public Timestamp getActJoinDate() {
        return actJoinDate;
    }
    public void setActJoinDate(Timestamp actJoinDate) {
        this.actJoinDate = actJoinDate;
    }
    public Integer getActStar() {
        return actStar;
    }
    public void setActStar(Integer actStar) {
        this.actStar = actStar;
    }
    public Timestamp getActStarDate() {
        return actStarDate;
    }
    public void setActStarDate(Timestamp actStarDate) {
        this.actStarDate = actStarDate;
    }
}
