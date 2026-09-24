package com.samadhan.core;
public class Complaint {
    public long id; public Issue issue; public String title, desc, owner;
    public int status, priority, hours, officer = -1, x, y, rating; public boolean anon; public String carrier = "";
    public boolean done() { return status == issue.flow.length - 1; }
    public String statusText() { return issue.flow[status]; }
    public String text() { return title + " " + desc; }
}
