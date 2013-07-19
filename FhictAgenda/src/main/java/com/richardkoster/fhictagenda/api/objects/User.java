package com.richardkoster.fhictagenda.api.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    public String name;
    @SerializedName("student_id")
    public String studentId;
    public String pcn;
    public List<Schedule> schedules;
    public String email;
}
