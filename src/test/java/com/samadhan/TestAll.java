package com.samadhan;

import com.samadhan.core.*;
import com.samadhan.module1_system.Service;

public class TestAll {
    public static void main(String[] args) {
        System.out.println("Starting tests...");
        try {
            Service svc = new Service();
            System.out.println("Service initialized. Complaints: " + svc.all.size());

            // Test login
            String res = svc.login("admin", "Admin@123", "admin");
            System.out.println("Admin login: " + res);
            assert res == null : "Admin login failed";

            res = svc.login("customer", "Customer@123", "customer");
            System.out.println("Customer login: " + res);
            assert res == null : "Customer login failed";

            // Test register
            res = svc.register("newUser1", "password123");
            System.out.println("Register: " + res);
            assert res == null : "Register failed";

            // Test suggest
            Issue sug = svc.suggest("my phone screen is cracked and broken");
            System.out.println("Suggest: " + (sug != null ? sug.label : "null"));

            // Test similar
            String[] sim = svc.similar("screen cracked broken");
            System.out.println("Similar: " + sim.length);

            // Test hits
            System.out.println("Hits english: " + svc.hits("phone screen", "phone"));
            System.out.println("Hits typo: " + svc.hits("phone screen", "phome"));
            System.out.println("Hits hindi: " + svc.hits("पार्सल टूटा हुआ मिला", "पार्सल"));

            // Test urgent
            String[] urg = svc.urgent(6);
            System.out.println("Urgent: " + urg.length);

            // Test assign
            int assigned = svc.assign();
            System.out.println("Assigned max-flow: " + assigned + ", bottleneck: " + svc.bottleneck);

            // Test assignMinCost
            String minCost = svc.assignMinCost();
            System.out.println("Assign min cost: " + minCost);

            // Test optimize
            String[] opt = svc.optimize(12);
            System.out.println("Optimize exact: " + opt.length);
            for (String s : opt) System.out.println("  " + s);

            // Test optimizeApprox
            String[] optApp = svc.optimizeApprox(12, 0.5);
            System.out.println("Optimize approx: " + optApp.length);
            for (String s : optApp) System.out.println("  " + s);

            // Test route
            String[] rt = svc.route();
            System.out.println("Route: " + rt.length);
            for (String s : rt) System.out.println("  " + s);

            // Test audit
            String[] aud = svc.audit(5);
            System.out.println("Audit: " + aud.length);

            // Test carrierStats
            int[][] stats = svc.carrierStats();
            System.out.println("Carrier stats: " + stats.length);
            System.out.println("Worst carrier: " + svc.worstCarrier());

            // Test trending
            System.out.println("Trending: " + svc.trending());

            // Test escalation
            String[] esc = svc.escalation();
            System.out.println("Escalation: " + esc.length);
            for (String s : esc) System.out.println("  " + s);

            // Test lab
            String[] lab = svc.lab("days");
            System.out.println("Lab: " + lab.length);
            for (String s : lab) System.out.println("  " + s);

            // Test prime
            System.out.println("Prime 561: " + svc.prime("561"));
            System.out.println("Prime 17: " + svc.prime("17"));

            // Test save and load
            svc.save();
            System.out.println("Saved successfully");
            svc.export();
            System.out.println("Exported successfully");

            System.out.println("ALL TESTS COMPLETED SUCCESSFULLY!");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
