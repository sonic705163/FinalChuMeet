package iii.com.chumeet.club;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


public class ClubVO implements Serializable{
    private int clubId;
    private int clubMemId;
    private String clubName;
    private int clubTypeId;
    private String clubContent;
    private byte[] clubPhoto;
    private Timestamp clubStartDate;
    private int clubStatus;
    private double clubLong;
    private double clubLat;



    public int getClubId() {
        return clubId;
    }


    public void setClubId(int clubId) {
        this.clubId = clubId;
    }


    public int getClubMemId() {
        return clubMemId;
    }


    public void setClubMemId(int clubMemId) {
        this.clubMemId = clubMemId;
    }


    public String getClubName() {
        return clubName;
    }


    public void setClubName(String clubName) {
        this.clubName = clubName;
    }


    public int getClubTypeId() {
        return clubTypeId;
    }


    public void setClubTypeId(int clubTypeId) {
        this.clubTypeId = clubTypeId;
    }


    public String getClubContent() {
        return clubContent;
    }


    public void setClubContent(String clubContent) {
        this.clubContent = clubContent;
    }


    public byte[] getClubPhoto() {
        return clubPhoto;
    }


    public void setClubPhoto(byte[] clubPhoto) {
        this.clubPhoto = clubPhoto;
    }


    public String getClubStartDate() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm a").format(clubStartDate);
        return date;
    }


    public void setClubStartDate(Timestamp clubStartDate) {
        this.clubStartDate = clubStartDate;
    }


    public int getClubStatus() {
        return clubStatus;
    }


    public void setClubStatus(int clubStatus) {
        this.clubStatus = clubStatus;
    }


    public double getClubLong() {
        return clubLong;
    }


    public void setClubLong(double clubLong) {
        this.clubLong = clubLong;
    }


    public double getClubLat() {
        return clubLat;
    }


    public void setClubLat(double clubLat) {
        this.clubLat = clubLat;
    }

}
