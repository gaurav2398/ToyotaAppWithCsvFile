package impel.imhealthy.adminapp.Model;

public class DaySortingModel {

    private String id;
    private String user_id;
    private String fever_c;
    private String fever_f;
    private String oxymetry_spo2;
    private String oxymetry_bpm;
    private String address;
    private String time;
    private String member_name;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFever_c() {
        return fever_c;
    }

    public void setFever_c(String fever_c) {
        this.fever_c = fever_c;
    }

    public String getFever_f() {
        return fever_f;
    }

    public void setFever_f(String fever_f) {
        this.fever_f = fever_f;
    }

    public String getOxymetry_spo2() {
        return oxymetry_spo2;
    }

    public void setOxymetry_spo2(String oxymetry_spo2) {
        this.oxymetry_spo2 = oxymetry_spo2;
    }

    public String getOxymetry_bpm() {
        return oxymetry_bpm;
    }

    public void setOxymetry_bpm(String oxymetry_bpm) {
        this.oxymetry_bpm = oxymetry_bpm;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }
}
