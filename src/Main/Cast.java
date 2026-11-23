package src.Main;

// Titles shown next to a speaker's name. 
public class Cast {
    public static String role(String s) {
        switch (s.toUpperCase()) {
            case "ALEX": return "Senior Java Architect";
            case "GRANGER": return "Principal SysAdmin";
            case "LUCY": return "Junior Developer";
            case "HAVERSAM": return "QA Lead";
            case "LUKE": return "DevOps Engineer";
            case "KILVER": return "Database Administrator";
            case "DELUMAR": return "Senior Security Auditor";
            case "FARSEER": return "Senior Performance Architect";
            case "VENUS": return "Frontend Lead";
            case "GUILIMAN": return "VP of Engineering";
            default: return "";
        }
    }
}
