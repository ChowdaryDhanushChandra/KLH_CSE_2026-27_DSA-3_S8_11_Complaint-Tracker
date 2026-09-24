package com.samadhan.core;
public enum Issue {
    DAMAGED("Damaged Product", "#EF4444", new String[]{"Order ID", "Product name"}, new String[]{"Reported", "Photo verified", "Replacement approved", "Replacement shipped"}),
    WRONG("Wrong Item", "#F59E0B", new String[]{"Order ID", "Item received instead"}, new String[]{"Reported", "Return label sent", "Item collected", "Correct item shipped"}),
    LOST("Lost Package", "#8B5CF6", new String[]{"Tracking number", "Last known location"}, new String[]{"Reported", "Courier trace", "Located or declared lost", "Resolved"}),
    DELAYED("Delayed Delivery", "#0EA5E9", new String[]{"Tracking number", "Days late"}, new String[]{"Reported", "Courier contacted", "Rescheduled", "Delivered"}),
    DEFECT("Defective / Warranty", "#22C55E", new String[]{"Serial number", "Purchase date"}, new String[]{"Reported", "Diagnosed", "Repair or replace", "Closed"}),
    REFUND("Refund & Payment", "#F97316", new String[]{"Order ID", "Amount (Rs.)"}, new String[]{"Reported", "Verified", "Refund approved", "Refunded"});
    public final String label, color; public final String[] fields, flow;
    Issue(String l, String c, String[] f, String[] w) { label = l; color = c; fields = f; flow = w; }
}
