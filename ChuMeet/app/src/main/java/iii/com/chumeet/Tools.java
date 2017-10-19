package iii.com.chumeet;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by sonic on 2017/10/12.
 */

public abstract class Tools {


    public static String emailPattern = "^([\\w]+)(([-\\.][\\w]+)?)*@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";



    public static String toFormat(Timestamp today){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年mm月dd日 hh:mm aa");

        String dayStr = formatter.format(today);

        return dayStr;
    }
    public static String toFormat2(Timestamp today){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 \nhh:mm aa");

        String dayStr = formatter.format(today);

        return dayStr;
    }


}
