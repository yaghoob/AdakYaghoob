package ir.adaktech.yaghoob;

public class User {

    private Integer id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;

    private String job;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_Name() {
        return first_name;
    }

    public void setFirst_Name(String first_Name) {
        this.first_name = first_Name;
    }

    public String getLast_Name() {
        return last_name;
    }

    public void setLast_Name(String last_Name) {
        this.last_name = last_Name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }


}