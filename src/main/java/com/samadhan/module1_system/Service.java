package com.samadhan.module1_system;
import com.samadhan.core.*;
import com.samadhan.module2_strings.KMP;
import com.samadhan.module2_strings.AhoCorasick;
import com.samadhan.module2_strings.SuffixArray;
import com.samadhan.module3_dp.BitmaskTSP;
import com.samadhan.module3_dp.EditDistance;
import com.samadhan.module4_flow.Assigner;
import com.samadhan.module4_flow.MinCostFlow;
import com.samadhan.module2_strings.Matchers;
import com.samadhan.module3_dp.TreeDP;
import com.samadhan.module5_np.Knapsack;
import com.samadhan.module6_random.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Facade used by every screen: auth, complaints, and calls into each algorithm folder. */
public class Service {
    public final DynArray<Complaint> all = new DynArray<>();
    final DynArray<String[]> users = new DynArray<>();
    public final String[] officerNames = {"Anita Rao", "Ravi Kumar", "Sunita Devi", "Kiran Reddy", "Mohan Lal", "Priya Nair", "Suresh Babu", "Lakshmi"};
    public final Issue[] skill = {Issue.DAMAGED, Issue.WRONG, Issue.LOST, Issue.DELAYED, Issue.DEFECT, Issue.REFUND, Issue.LOST, Issue.DELAYED};
    public final int[] cap = {3, 3, 2, 3, 2, 3, 2, 2};
    public String user, role; int fails; long lockUntil;
    final java.util.Random rnd = new java.util.Random();
    final AhoCorasick ac = new AhoCorasick();
    public String bottleneck = "";
    public final int[] ox = {10, 80, 50, 20, 70, 40, 90, 30}, oy = {20, 15, 80, 60, 50, 90, 70, 40};
    static final String[] ROLE = {"Head of Customer Care", "Logistics Manager", "Product Quality Manager", "Courier Desk Lead", "Warehouse Lead", "Warranty Lead", "Refunds Lead"};
    static final int[] PAR = {-1, 0, 0, 1, 1, 2, 0}, COST = {0, 4, 6, 2, 3, 5, 8}, OWNER = {4, 4, 3, 3, 5, 6};
    public final String[] carriers = {"SwiftShip", "BlueWing", "QuickPost", "GreenMile"};
    static final java.nio.file.Path FILE = java.nio.file.Path.of("data", "samadhan.tsv");

    public Service() {
        users.add(new String[]{"admin", hash("Admin@123"), "admin"});
        users.add(new String[]{"customer", hash("Customer@123"), "customer"});
        String[][] s = {{"Phone arrived with a cracked screen", "Glass bottle broken inside the box"}, {"Ordered a blue shirt, received a red one", "Received size M instead of L"}, {"Package shows delivered but never arrived", "Parcel lost in transit"}, {"Order is 7 days late", "Tracking has not moved for 5 days"}, {"Blender stopped working after 2 days", "Headphones have no sound on the left side"}, {"Refund not received after return", "Charged twice for one order"}};
        String[][] kw = {{"broken", "cracked", "damaged", "shattered", "dent", "torn", "leak"}, {"wrong", "instead", "different", "mismatch", "size"}, {"lost", "missing", "never arrived", "delivered but", "stolen"}, {"late", "delay", "days", "stuck", "tracking"}, {"stopped", "defective", "faulty", "warranty", "repair", "not working", "no sound"}, {"refund", "charged", "payment", "money", "return", "twice"}};
        for (int i = 0; i < kw.length; i++) for (String w : kw[i]) ac.add(w, i);
        ac.build();
        user = "customer";
        for (int i = 0; i < 12; i++) {
            Issue is = Issue.values()[i / 2]; file(is, s[i / 2][i % 2], "Reported by residents, please check soon.", false);
            Complaint c = all.get(i); c.priority = 1 + (i * 3) % 5; c.hours = 1 + (i * 5) % 6; c.status = i % 3 == 0 ? is.flow.length - 1 : i % 2;
        }
        file(Issue.DAMAGED, "पार्सल टूटा हुआ मिला", "डिब्बा खोला तो सामान टूटा हुआ था", false);
        file(Issue.DAMAGED, "ప్యాకేజీ దెబ్బతిన్నది", "వస్తువు పగిలిపోయి వచ్చింది", false);
        user = null; load();
    }

    static String hash(String p) {
        try {
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(p.toCharArray(), "samadhan-salt".getBytes(), 10000, 256)).getEncoded();
            StringBuilder sb = new StringBuilder(); for (byte b : h) sb.append(String.format("%02x", b)); return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public String login(String u, String p, String r) {
        long now = System.currentTimeMillis();
        if (now < lockUntil) return "Too many attempts. Try again in " + ((lockUntil - now) / 1000 + 1) + " seconds.";
        if (u.isEmpty() || p.isEmpty()) return "Enter your username and password.";
        String h = hash(p);
        for (int i = 0; i < users.size(); i++) {
            String[] x = users.get(i);
            if (x[0].equals(u) && x[1].equals(h)) {
                if (!x[2].equals(r)) return "This is a " + x[2] + " account. Switch to the " + x[2] + " tab.";
                fails = 0; user = u; role = r; return null;
            }
        }
        if (++fails >= 5) { fails = 0; lockUntil = now + 60000; return "Locked for 60 seconds after 5 failed attempts."; }
        return "Wrong username or password. " + (5 - fails) + " attempts left.";
    }
    public void logout() { user = null; role = null; }

    public long file(Issue is, String t, String d, boolean anon) {
        Complaint c = new Complaint();
        c.id = MillerRabin.nextPrime(100000000L + rnd.nextInt(800000000));
        c.issue = is; c.title = t; c.desc = d; c.owner = user == null ? "customer" : user; c.anon = anon;
        c.priority = is == Issue.LOST || is == Issue.REFUND ? 4 : 1 + rnd.nextInt(3); c.x = rnd.nextInt(100); c.y = rnd.nextInt(100); c.carrier = carriers[rnd.nextInt(carriers.length)]; c.hours = 1 + rnd.nextInt(6);
        all.add(c); return c.id;
    }
    public Complaint find(String s) {
        long v; try { v = Long.parseLong(s); } catch (NumberFormatException e) { return null; }
        for (int i = 0; i < all.size(); i++) if (all.get(i).id == v) return all.get(i);
        return null;
    }
    public String officerName(int i) { return (i >= 0 && i < officerNames.length) ? officerNames[i] : "—"; }

    /** Smart search: KMP exact match, with an edit-distance fallback that tolerates one typo per word. */
    public boolean hits(String hay, String q) {
        if (KMP.count(hay, q) > 0) return true;
        if (q.length() < 4) return false;
        for (String w : hay.toLowerCase().split("\\s+")) if (Math.abs(w.length() - q.length()) <= 1 && EditDistance.dist(w, q.toLowerCase()) <= 1) return true;
        return false;
    }
    public String[] similar(String text) {
        String[] w = text.toLowerCase().split("\\s+"); DynArray<String> out = new DynArray<>();
        for (int i = 0; i < all.size(); i++) {
            Complaint c = all.get(i); int hit = 0;
            for (String x : w) if (x.length() > 3 && hits(c.text(), x)) hit++;
            if (hit >= 2 && !c.anon) out.add("#" + c.id + "  " + c.title + "  (" + hit + " matching words)");
        }
        return arr(out);
    }
    public int assign() {
        int n = 0; int[] ix = new int[all.size()];
        for (int i = 0; i < all.size(); i++) if (!all.get(i).done()) ix[n++] = i;
        int[] ci = new int[n]; for (int k = 0; k < n; k++) ci[k] = all.get(ix[k]).issue.ordinal();
        int[] oi = new int[skill.length]; for (int j = 0; j < oi.length; j++) oi[j] = skill[j].ordinal();
        int[] r = Assigner.assign(ci, oi, cap); int cnt = 0;
        for (int k = 0; k < n; k++) { all.get(ix[k]).officer = r[k]; if (r[k] >= 0) cnt++; }
        int[] miss = new int[Issue.values().length]; for (int k = 0; k < n; k++) if (r[k] < 0) miss[ci[k]]++;
        bottleneck = ""; for (int i = 0; i < miss.length; i++) if (miss[i] > 0) bottleneck += " " + Issue.values()[i].label + ": " + miss[i] + " waiting, needs more officers.";
        return cnt;
    }
    public String[] optimize(int budget) {
        if (budget <= 0) return new String[]{"Enter a positive number of hours."};
        int n = 0; int[] ix = new int[all.size()];
        for (int i = 0; i < all.size(); i++) if (!all.get(i).done()) ix[n++] = i;
        if (n == 0) return new String[]{"No open complaints to optimize."};
        int[] w = new int[n], v = new int[n];
        for (int k = 0; k < n; k++) { w[k] = all.get(ix[k]).hours; v[k] = all.get(ix[k]).priority; }
        boolean[] p = Knapsack.solve(w, v, budget); DynArray<String> out = new DynArray<>(); int tv = 0, th = 0;
        for (int k = 0; k < n; k++) if (p[k]) { Complaint c = all.get(ix[k]); tv += v[k]; th += w[k]; out.add(c.issue.label + " — " + (c.anon ? "[confidential]" : c.title) + "  (" + w[k] + "h, priority " + v[k] + ")"); }
        out.add("Total: " + th + " of " + budget + " hours used, priority value " + tv);
        return arr(out);
    }
    public String[] audit(int k) {
        int[] d = new int[all.size()]; int n = 0;
        for (int i = 0; i < all.size(); i++) if (all.get(i).done()) d[n++] = i;
        int[] r = Reservoir.sample(n, k, rnd); String[] out = new String[r.length];
        for (int j = 0; j < r.length; j++) { Complaint c = all.get(d[r[j]]); out[j] = "#" + c.id + "  " + c.issue.label + " — " + (c.anon ? "[confidential]" : c.title); }
        return out;
    }
    public long newTicket() { return MillerRabin.nextPrime(100000000L + rnd.nextInt(800000000)); }
    public String register(String u, String p) {
        if (u.length() < 3) return "Username needs at least 3 characters.";
        if (p.length() < 6) return "Password needs at least 6 characters.";
        for (int i = 0; i < users.size(); i++) if (users.get(i)[0].equals(u)) return "That username is already taken.";
        users.add(new String[]{u, hash(p), "customer"}); save(); return null;
    }
    /** Aho-Corasick: suggest the category whose keywords appear most often in the text. */
    public Issue suggest(String t) {
        int[] r = ac.count(t, Issue.values().length); int b = 0, bi = -1;
        for (int i = 0; i < r.length; i++) if (r[i] > b) { b = r[i]; bi = i; }
        return bi < 0 ? null : Issue.values()[bi];
    }
    /** Bitmask DP: exact shortest round trip from the warehouse through up to 9 open pickups. */
    public String[] route() {
        int[] ix = new int[9]; int n = 0;
        for (int i = 0; i < all.size() && n < 9; i++) { Complaint c = all.get(i); if (!c.done() && (c.issue == Issue.DAMAGED || c.issue == Issue.WRONG)) ix[n++] = i; }
        if (n == 0) return new String[]{"No open pickups right now."};
        int[] x = new int[n + 1], y = new int[n + 1]; x[0] = 50; y[0] = 50;
        for (int k = 0; k < n; k++) { x[k + 1] = all.get(ix[k]).x; y[k + 1] = all.get(ix[k]).y; }
        int[] o = BitmaskTSP.route(x, y); DynArray<String> out = new DynArray<>(); double tot = 0;
        for (int i = 1; i <= n; i++) { Complaint c = all.get(ix[o[i] - 1]); out.add(i + ". " + c.issue.label + " — " + c.title + "  at (" + c.x + ", " + c.y + ")"); tot += Math.hypot(x[o[i]] - x[o[i - 1]], y[o[i]] - y[o[i - 1]]); }
        tot += Math.hypot(x[o[n]] - 50, y[o[n]] - 50);
        out.add("Round trip from warehouse: " + Math.round(tot) + " km (exact shortest route)"); return arr(out);
    }
    /** Randomized quicksort: open complaints, highest priority first. */
    public String[] urgent(int k) {
        int n = 0; Complaint[] a = new Complaint[all.size()];
        for (int i = 0; i < all.size(); i++) if (!all.get(i).done()) a[n++] = all.get(i);
        QuickSort.sort(a, 0, n - 1); String[] out = new String[Math.min(k, n)];
        for (int i = 0; i < out.length; i++) out[i] = "P" + a[i].priority + "  " + a[i].issue.label + " — " + a[i].title;
        return out;
    }
    /** Min-cost max-flow: same matching as Auto-Assignment, but minimises total officer travel distance. */
    public String assignMinCost() {
        int n = 0; int[] ix = new int[all.size()];
        for (int i = 0; i < all.size(); i++) if (!all.get(i).done()) ix[n++] = i;
        int[] ci = new int[n]; int[][] cost = new int[n][skill.length];
        for (int k = 0; k < n; k++) { Complaint c = all.get(ix[k]); ci[k] = c.issue.ordinal(); for (int j = 0; j < skill.length; j++) cost[k][j] = (int) Math.round(Math.hypot(c.x - ox[j], c.y - oy[j])); }
        int[] oi = new int[skill.length]; for (int j = 0; j < oi.length; j++) oi[j] = skill[j].ordinal();
        int[] r = MinCostFlow.assign(ci, oi, cap, cost); int cnt = 0, tot = 0;
        for (int k = 0; k < n; k++) { all.get(ix[k]).officer = r[k]; if (r[k] >= 0) { cnt++; tot += cost[k][r[k]]; } }
        return cnt + " complaints assigned with the lowest possible total travel: " + tot + " km (min-cost max-flow).";
    }
    /** FPTAS knapsack compared with the exact answer. */
    public String[] optimizeApprox(int budget, double eps) {
        if (budget <= 0) return new String[]{"Enter a positive number of hours."};
        eps = Math.max(eps, 0.05); int n = 0; int[] ix = new int[all.size()];
        for (int i = 0; i < all.size(); i++) if (!all.get(i).done()) ix[n++] = i;
        if (n == 0) return new String[]{"No open complaints to optimize."};
        int[] w = new int[n], v = new int[n]; for (int k = 0; k < n; k++) { w[k] = all.get(ix[k]).hours; v[k] = all.get(ix[k]).priority; }
        boolean[] p = Knapsack.fptas(w, v, budget, eps), ex = Knapsack.solve(w, v, budget); int av = 0, ev = 0; DynArray<String> out = new DynArray<>();
        for (int k = 0; k < n; k++) { if (p[k]) { av += v[k]; Complaint c = all.get(ix[k]); out.add(c.issue.label + " — " + (c.anon ? "[confidential]" : c.title) + "  (" + w[k] + "h, priority " + v[k] + ")"); } if (ex[k]) ev += v[k]; }
        out.add("Approximate value " + av + " vs exact " + ev + ". Guaranteed at least " + Math.round((1 - eps) * 100) + "% of the best.");
        return arr(out);
    }
    /** Tree DP over the escalation hierarchy. */
    public String[] escalation() {
        int n = Issue.values().length; String[] out = new String[n + 2];
        for (int i = 0; i < n; i++) {
            int u = OWNER[i], tot = 0; String path = ROLE[u];
            while (PAR[u] >= 0) { tot += COST[u]; u = PAR[u]; path += " → " + ROLE[u]; }
            out[i] = Issue.values()[i].label + ": " + path + "  (" + tot + " h to reach the top)";
        }
        out[n] = ""; out[n + 1] = "Longest escalation chain in the hierarchy: " + TreeDP.diameter(PAR, COST) + " hours (tree DP).";
        return out;
    }
    /** Algorithm Lab: races four string matchers on all complaint text. */
    public String[] lab(String pat) {
        String p = pat.toLowerCase(); if (p.isEmpty() || p.length() > 50) return new String[]{"Enter a pattern of 1 to 50 characters."};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < all.size(); i++) { Complaint c = all.get(i); if (c.anon) continue; String d = c.desc; int nl = d.indexOf('\n'); if (nl >= 0) d = d.substring(0, nl); sb.append(c.title).append(' ').append(d).append(' '); }
        String t = sb.toString().toLowerCase().repeat(300); String[] names = {"Naive", "KMP", "Z-function", "Rabin-Karp (double hash)"}; String[] out = new String[5];
        out[0] = "Searching " + t.length() + " characters for \"" + pat + "\"";
        for (int i = 0; i < 4; i++) {
            long t0 = System.nanoTime(); long[] r = i == 0 ? Matchers.naive(t, p) : i == 1 ? Matchers.kmp(t, p) : i == 2 ? Matchers.z(t, p) : Matchers.rk(t, p);
            out[i + 1] = String.format("%-26s matches %-8d steps %-10d %d µs", names[i], r[0], r[1], (System.nanoTime() - t0) / 1000);
        }
        return out;
    }
    public String prime(String s) {
        try {
            long v = Long.parseLong(s);
            if (v < 0 || v > 3_000_000_000L) return "Enter a whole number from 0 to 3 billion.";
            if (v < 2) return v + " is neither prime nor composite.";
            return v + (MillerRabin.isPrime(v) ? " is prime" : " is composite") + " (Miller-Rabin, bases 2, 3, 5, 7).";
        } catch (NumberFormatException e) { return "Enter a whole number."; }
    }
    // ---- saving and loading (data/samadhan.tsv) ----
    static String cl(String s) { return s.replace('\t', ' ').replace('\n', ' ').replace('\r', ' '); }
    public void save() {
        try {
            java.nio.file.Files.createDirectories(FILE.getParent()); StringBuilder sb = new StringBuilder();
            for (int i = 0; i < users.size(); i++) sb.append("U\t").append(String.join("\t", users.get(i))).append('\n');
            for (int i = 0; i < all.size(); i++) { Complaint c = all.get(i);
                sb.append(String.join("\t", "C", "" + c.id, "" + c.issue.ordinal(), cl(c.title), cl(c.desc), c.owner, "" + c.status, "" + c.priority, "" + c.hours, "" + c.officer, "" + c.anon, "" + c.x, "" + c.y, "" + c.rating, c.carrier)).append('\n'); }
            java.nio.file.Files.writeString(FILE, sb.toString());
        } catch (Exception e) { }
    }
    void load() {
        if (!java.nio.file.Files.exists(FILE)) return;
        try {
            DynArray<Complaint> ca = new DynArray<>(); DynArray<String[]> ua = new DynArray<>();
            for (String ln : java.nio.file.Files.readAllLines(FILE)) {
                String[] p = ln.split("\t", -1);
                if (p[0].equals("U")) ua.add(new String[]{p[1], p[2], p[3]});
                else if (p[0].equals("C")) {
                    Complaint c = new Complaint(); c.id = Long.parseLong(p[1]); c.issue = Issue.values()[Integer.parseInt(p[2])]; c.title = p[3]; c.desc = p[4]; c.owner = p[5];
                    c.status = Integer.parseInt(p[6]); c.priority = Integer.parseInt(p[7]); c.hours = Integer.parseInt(p[8]); c.officer = Integer.parseInt(p[9]);
                    c.anon = Boolean.parseBoolean(p[10]); c.x = Integer.parseInt(p[11]); c.y = Integer.parseInt(p[12]); c.rating = Integer.parseInt(p[13]); c.carrier = p[14]; ca.add(c);
                }
            }
            if (ua.size() == 0) return;
            all.clear(); users.clear(); for (int i = 0; i < ua.size(); i++) users.add(ua.get(i)); for (int i = 0; i < ca.size(); i++) all.add(ca.get(i));
        } catch (Exception e) { }
    }
    static String q(String s) { return "\"" + s.replace("\"", "\"\"") + "\""; }
    public String export() {
        try {
            java.nio.file.Files.createDirectories(FILE.getParent()); StringBuilder sb = new StringBuilder("Ticket,Courier,Issue,Title,Status,Priority,Officer,Rating\n");
            for (int i = 0; i < all.size(); i++) { Complaint c = all.get(i);
                sb.append(c.id).append(',').append(c.carrier).append(',').append(q(c.issue.label)).append(',').append(q(c.anon ? "[confidential]" : c.title)).append(',').append(q(c.statusText())).append(',').append(c.priority).append(',').append(c.officer < 0 ? "" : q(officerNames[c.officer])).append(',').append(c.rating).append('\n'); }
            java.nio.file.Path p = FILE.resolveSibling("report.csv"); java.nio.file.Files.writeString(p, sb.toString()); return p.toAbsolutePath().toString();
        } catch (Exception e) { return "(could not write report: " + e.getMessage() + ")"; }
    }
    // ---- insights ----
    public int[][] carrierStats() {
        int[][] r = new int[carriers.length][2];
        for (int i = 0; i < all.size(); i++) { Complaint c = all.get(i); for (int j = 0; j < carriers.length; j++) if (carriers[j].equals(c.carrier)) { r[j][0]++; if (c.done()) r[j][1]++; } }
        return r;
    }
    public String worstCarrier() {
        int[][] r = carrierStats(); int b = 0;
        for (int j = 1; j < r.length; j++) if (r[j][0] - r[j][1] > r[b][0] - r[b][1]) b = j;
        return "Most unresolved complaints: " + carriers[b] + " (" + (r[b][0] - r[b][1]) + " open). Raise this with the courier.";
    }
    /** Suffix array + Kasai LCP over all complaint text: finds the longest phrase repeated across complaints. */
    public String trending() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < all.size(); i++) { Complaint c = all.get(i); if (c.anon) continue; String d = c.desc; int nl = d.indexOf('\n'); if (nl >= 0) d = d.substring(0, nl); sb.append(c.title).append(' ').append(d).append('|'); }
        String s = sb.toString().toLowerCase(); if (s.length() < 2) return "";
        int[] sa = SuffixArray.build(s), l = SuffixArray.lcp(s, sa); String best = "";
        for (int i = 1; i < sa.length; i++) {
            if (l[i] < 6) continue; String p = s.substring(sa[i], sa[i] + l[i]); int cut = p.indexOf('|'); if (cut >= 0) p = p.substring(0, cut); p = p.trim();
            if (p.length() > best.length()) best = p;
        }
        if (best.length() < 6) return "Not enough repeated text yet to find a trend.";
        return "Most repeated phrase: \"" + best + "\" (" + KMP.count(s, best) + " places, found with suffix array + LCP).";
    }
    static String[] arr(DynArray<String> d) { String[] a = new String[d.size()]; for (int i = 0; i < a.length; i++) a[i] = d.get(i); return a; }
}
