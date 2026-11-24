package src.Main;

public class SceneData {

    public static String[] info(int n) {
        switch (n) {
            case 1: return new String[] {"ACT I", "Scene 1: The Last Five Minutes", "FRI 4:55 PM", "office", "s1_enter"};
            case 2: return new String[] {"ACT I", "Scene 2: The Catalyst", "FRI 4:58 PM", "office", "s2_enter"};
            case 3: return new String[] {"ACT I", "Scene 3: Corporate Intervention", "FRI 4:59 PM", "office", "s3_enter"};
            case 4: return new String[] {"ACT II", "Scene 4: The Failing Test Suite", "FRI 5:02 PM", "desk", "s4_enter"};
            case 5: return new String[] {"ACT II", "Scene 5: Circular Dependency Meltdown", "FRI 5:05 PM", "desk", "s5_enter"};
            case 6: return new String[] {"ACT II", "Scene 6: The Unencapsulated Leak", "FRI 5:08 PM", "desk", "s6_enter"};
            case 7: return new String[] {"ACT III", "Scene 7: Heap Critical & Null Payloads", "FRI 5:11 PM", "desk", "s7_enter"};
            case 8: return new String[] {"ACT III", "Scene 8: Runtime Polymorphism Climax", "FRI 5:13 PM", "desk", "s8_enter"};
            case 9: return new String[] {"ACT III", "Scene 9: The Verdict", "FRI 5:15 PM", "office", "s9_enter"};
            case 10: return new String[] {"ENDING A", "Clean Architecture", "FRI 5:30 PM", "office", "end_a"};
            case 11: return new String[] {"ENDING B", "The Spaghetti Patch", "FRI 5:30 PM", "office", "end_b"};
            case 12: return new String[] {"GAME OVER", "Fail State", "SIX MONTHS LATER", "cabin", "end_c"};
            default: return null;
        }
    }

    public static String[][] actors(int n) {
        switch (n) {
            case 1: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"granger", "granger_stand", "232", "80", "true"}
            };
            case 2: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"lucy", "lucy_stand", "232", "80", "false"}
            };
            case 3: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"lucy", "lucy_stand", "232", "80", "true"},
                {"guiliman", "guiliman_stand", "270", "80", "false"}
            };
            case 4: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"lucy", "lucy_stand", "232", "80", "true"},
                {"haversam", "popup", "0", "0", "false"}
            };
            case 5: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"lucy", "lucy_stand", "232", "80", "true"},
                {"luke", "luke_stand", "20", "80", "false"}
            };
            case 6: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"lucy", "lucy_stand", "232", "80", "true"},
                {"kilver", "popup", "0", "0", "false"},
                {"delumar", "popup", "0", "0", "false"}
            };
            case 7: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"lucy", "lucy_stand", "232", "80", "true"},
                {"farseer", "farseer_stand", "20", "80", "false"},
                {"venus", "venus_stand", "270", "80", "false"}
            };
            case 8: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"lucy", "lucy_stand", "232", "80", "true"},
                {"farseer", "farseer_stand", "20", "80", "true"},
                {"guiliman", "guiliman_stand", "270", "80", "false"}
            };
            case 9: return new String[][] {
                {"alex", "alex_sit", "70", "80", "true"},
                {"farseer", "farseer_stand", "20", "80", "true"},
                {"lucy", "lucy_stand", "192", "80", "true"},
                {"guiliman", "guiliman_stand", "232", "80", "true"},
                {"venus", "venus_stand", "270", "80", "true"}
            };
            case 10: return new String[][] {
                {"alex", "alex_stand", "70", "80", "true"},
                {"lucy", "lucy_stand", "232", "80", "true"}
            };
            case 11: return new String[][] {
                {"alex", "alex_stand", "70", "80", "true"},
                {"venus", "venus_stand", "232", "80", "true"}
            };
            case 12: return new String[][] {
                {"granger", "granger_stool", "30", "80", "true"},
                {"alex", "alex_stool", "70", "80", "true"},
                {"lucy", "lucy_stool", "110", "80", "true"}
            };
            default: return new String[0][0];
        }
    }

    public static String[][] hotspots(int n) {
        switch (n) {
            case 1: return new String[][] {
                {"met_granger", "Granger", "227", "85", "43", "43", "granger", "", "", "true", "DIALOGUE:s1_granger"},
                {"pr_unlocked", "Granger", "227", "85", "43", "43", "granger", "check_maven,check_compiler,check_heap", "Run all three pre-flight checks first (click the monitors).", "true", "DIALOGUE:s1_granger_good"},
                {"s1_exit", "Granger", "227", "85", "43", "43", "granger", "pr1,pr2,pr3", "Clear the three pull requests first.", "true", "DIALOGUE:s1_granger_exit"},
                {"check_maven", "Monitor: Maven dependencies", "110", "75", "30", "26", "", "met_granger", "Talk to Granger first.", "true", "DIALOGUE:s1_maven"},
                {"check_compiler", "Monitor: compiler compliance", "144", "75", "30", "26", "", "met_granger", "Talk to Granger first.", "true", "DIALOGUE:s1_compiler"},
                {"check_heap", "Monitor: JVM heap", "178", "75", "30", "26", "", "met_granger", "Talk to Granger first.", "true", "DIALOGUE:s1_heap"},
                {"pr1", "Pull Request #1", "110", "75", "30", "26", "", "pr_unlocked", "Granger will tell you when to clear the PRs.", "true", "DIALOGUE:s1_pr1"},
                {"pr2", "Pull Request #2", "144", "75", "30", "26", "", "pr_unlocked", "Granger will tell you when to clear the PRs.", "true", "DIALOGUE:s1_pr2"},
                {"pr3", "Pull Request #3", "178", "75", "30", "26", "", "pr_unlocked", "Granger will tell you when to clear the PRs.", "true", "DIALOGUE:s1_pr3"}
            };
            case 2: return new String[][] {
                {"s2_door", "Office door", "6", "30", "28", "76", "", "", "", "true", "DIALOGUE:s2_door"},
                {"s2_lucy", "Lucy", "227", "85", "43", "43", "lucy", "", "", "true", "DIALOGUE:s2_lucy"}
            };
            case 3: return new String[][] {
                {"s3_dash", "Telemetry dashboard", "174", "14", "80", "44", "", "", "", "true", "DIALOGUE:s3_dash"},
                {"s3_guiliman", "Guiliman", "265", "85", "43", "43", "guiliman", "", "", "true", "DIALOGUE:s3_guiliman"}
            };
            case 4: return new String[][] {
                {"s4_lucy", "Lucy", "227", "85", "43", "43", "lucy", "haversam_done", "Haversam is still talking.", "true", "DIALOGUE:s4_lucy"},
                {"s4_diagram", "Class hierarchy diagram", "12", "14", "98", "60", "", "s4_lucy", "Hear Lucy out first.", "true", "DIALOGUE:s4_diagram"}
            };
            case 5: return new String[][] {
                {"s5_aws", "AWS auto-scaling console", "116", "14", "88", "60", "", "luke_talked", "Listen to Luke first.", "true", "DIALOGUE:s5_aws"},
                {"s5_lucy", "Lucy", "227", "85", "43", "43", "lucy", "luke_talked", "Listen to Luke first.", "true", "DIALOGUE:s5_lucy"},
                {"s5_maven", "Maven build", "210", "14", "98", "60", "", "puzzle4_done,s5_aws", "Pause the auto-scaler and extract taxRate first.", "true", "DIALOGUE:s5_done"}
            };
            case 6: return new String[][] {
                {"s6_phone", "Desk phone", "194", "92", "28", "18", "", "phone_ringing", "The phone is not ringing.", "true", "DIALOGUE:s6_kilver"},
                {"s6_lucy", "Lucy", "227", "85", "43", "43", "lucy", "kilver_done", "Answer the phone first.", "true", "DIALOGUE:s6_lucy"},
                {"s6_conn", "ConnectionManager.java", "12", "14", "98", "60", "", "kilver_done", "Answer the phone first.", "true", "DIALOGUE:s6_conn"},
                {"s6_slack", "Slack DMs", "210", "14", "98", "60", "", "kilver_done", "Answer the phone first.", "true", "DIALOGUE:s6_delumar"},
                {"s6_line400", "SuperManager.java line 400", "116", "14", "88", "60", "", "puzzle5_done,puzzle6_done,delumar_done", "Not yet: fix the try-with-resources leak, the Singleton, and read Delumar's DM.", "true", "DIALOGUE:s6_line400"}
            };
            case 7: return new String[][] {
                {"s7_gc", "JVM Profiler: run manual GC", "210", "14", "98", "60", "", "", "", "false", "GC"},
                {"s7_venus", "Venus", "265", "85", "43", "43", "venus", "farseer_done", "Farseer is still talking.", "true", "DIALOGUE:s7_venus"},
                {"s7_dto", "UserDTO.java", "12", "14", "98", "60", "", "venus_done", "Venus is not finished yelling yet.", "true", "DIALOGUE:s7_dto"},
                {"s7_tx", "Transaction.java", "116", "14", "88", "60", "", "puzzle7_done", "Restore the DTO getters first.", "true", "DIALOGUE:s7_end"}
            };
            default: return new String[0][0];
        }
    }

    public static String[][] objectives(int n) {
        switch (n) {
            case 1: return new String[][] {
                {"met_granger", "Click Granger to start the tutorial."},
                {"check_maven,check_compiler,check_heap", "Run the 3 pre-flight checks: click each monitor."},
                {"pr_unlocked", "Click Granger to report back."},
                {"pr1,pr2,pr3", "Clear the 3 pull requests: click each monitor again."},
                {"s1_exit", "Say goodbye to Granger."}
            };
            case 2: return new String[][] {
                {"s2_door", "Leave through the office door."},
                {"s2_lucy", "Hear Lucy out. Click Lucy."}
            };
            case 3: return new String[][] {
                {"s3_guiliman", "Answer Guiliman: click him. (The wall dashboard is worth a look.)"}
            };
            case 4: return new String[][] {
                {"haversam_done", "Listen to Haversam."},
                {"s4_lucy", "Click Lucy for her take."},
                {"s4_diagram", "Open the class hierarchy diagram (left monitor)."}
            };
            case 5: return new String[][] {
                {"puzzle1_done", "Solve Puzzle 1 before the heap hits 95%!"},
                {"luke_talked", "Listen to Luke."},
                {"s5_aws,puzzle4_done", "Pause the auto-scaler (center monitor) and click Lucy to extract taxRate."},
                {"s5_maven", "Re-run the Maven build (right monitor)."}
            };
            case 6: return new String[][] {
                {"puzzle2_done", "Solve Puzzle 2: lock down UserAccount."},
                {"kilver_done", "Answer the ringing desk phone."},
                {"puzzle5_done", "Click Lucy: fix the try-with-resources leak."},
                {"puzzle6_done", "Open ConnectionManager.java (left monitor): make it a Singleton."},
                {"delumar_done", "Check Slack (right monitor): Delumar sent a DM."},
                {"s6_line400", "Scroll to line 400 of SuperManager.java (center monitor)."}
            };
            case 7: return new String[][] {
                {"farseer_done", "Listen to Farseer. (Tip: click the profiler to run GC.)"},
                {"venus_done", "Click Venus to answer her."},
                {"puzzle7_done", "Open UserDTO.java (left monitor): restore the getters."},
                {"s7_tx", "Open Transaction.java (center monitor)."}
            };
            default: return new String[0][0];
        }
    }
}
