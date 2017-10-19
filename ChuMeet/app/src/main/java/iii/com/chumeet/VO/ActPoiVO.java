package iii.com.chumeet.VO;

import java.io.Serializable;

/**
 * Created by sonic on 2017/10/14.
 */

public class ActPoiVO implements Serializable {
    private Integer actid;
    private Integer poiid;
    public Integer getActid() {
        return actid;
    }
    public void setActid(Integer actid) {
        this.actid = actid;
    }
    public Integer getPoiid() {
        return poiid;
    }
    public void setPoiid(Integer poiid) {
        this.poiid = poiid;
    }
}
