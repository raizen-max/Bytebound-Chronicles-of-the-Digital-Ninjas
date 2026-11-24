package src.Main;

// To add a puzzle: add a case, then "@PUZZLE n" in script/dialogue.txt.
public class Puzzles {
    public int id;
    public String title, file, doneDialogue;
    public boolean gcButton;        // show the "RUN GC" button?
    public String[] instruction, hint, explanation;
    public String[][] code, options;
    public int[] correct;

    public static Puzzles get(int id) {
        Puzzles p = new Puzzles();
        p.id = id;
        switch (id) {
            case 1:
                p.title = "PUZZLE 1: Refactor extends -> implements"; p.file = "CreditCardPayment.java"; p.gcButton = false; p.doneDialogue = "s5_after_p1";
                p.instruction = new String[] {"A credit card is-not-a database socket. Fix the class signature.", "Inject the database dependency through the constructor."};
                p.code = new String[][] {
                    {"public class CreditCardPayment ____ {", "    private DatabaseConnection db;", "}"},
                    {"public class CreditCardPayment implements Payable {", "    private final DatabaseConnection db;", "", "    public CreditCardPayment(____) {", "        this.db = db;", "    }", "}"}
                };
                p.options = new String[][] {
                    {"extends DatabaseConnection", "implements Payable", "extends Payable", "implements DatabaseConnection"},
                    {"new DatabaseConnection()", "DatabaseConnection db", "static DatabaseConnection db", "Payable db"}
                };
                p.correct = new int[] {1, 1};
                p.hint = new String[] {"Payment classes should promise behaviour (an interface), not inherit a JDBC driver.", "The caller hands the dependency in; the class must not create it itself."};
                p.explanation = new String[] {"implements Payable: payment now depends on an abstraction (Liskov Substitution fixed).", "Constructor injection: the dependency is supplied from outside, so it is loosely coupled."};
                break;
            case 2:
                p.title = "PUZZLE 2: Encapsulation & access modifiers"; p.file = "UserAccount.java"; p.gcButton = false; p.doneDialogue = "s6_after_p2";
                p.instruction = new String[] {"An external thread is writing account.balance = -9999.99. Lock the field down.", "Provide controlled read access with a getter.", "Add a negative-value validation check inside setBalance()."};
                p.code = new String[][] {
                    {"public class UserAccount {", "    ____ double balance;", "}"},
                    {"private double balance;", "", "____", "    return balance;", "}"},
                    {"public void setBalance(double balance) {", "    ____ {", "        throw new IllegalArgumentException(\"No negatives\");", "    }", "    this.balance = balance;", "}"}
                };
                p.options = new String[][] {
                    {"public", "private", "static", "protected"},
                    {"public double getBalance() {", "private double getBalance() {", "public static double getBalance() {", "public void getBalance() {"},
                    {"if (balance > 0)", "if (balance < 0)", "if (balance == 0)", "if (this.balance < 0)"}
                };
                p.correct = new int[] {1, 0, 1};
                p.hint = new String[] {"Which modifier hides a field from every other class?", "A getter must be public, non-static, and return the field's type.", "Check the NEW value coming in (the parameter), and reject values below zero."};
                p.explanation = new String[] {"private: only UserAccount itself can touch balance now.", "public double getBalance(): controlled public access to a private field.", "Input validation: the setter now rejects negative balances before storing them."};
                break;
            case 3:
                p.title = "PUZZLE 3: Abstraction & dynamic method dispatch"; p.file = "Transaction.java"; p.gcButton = true; p.doneDialogue = "s8_after_p3";
                p.instruction = new String[] {"Make Transaction abstract so nobody can instantiate a raw one.", "Declare the abstract process() method.", "Mark the override in each concrete subclass.", "Replace the 300-line if-else with a polymorphic loop."};
                p.code = new String[][] {
                    {"public ____ class Transaction {", "}"},
                    {"public abstract class Transaction {", "    ____", "}"},
                    {"public class CardTransaction extends Transaction {", "    ____", "    public void process() { /* charge card */ }", "}"},
                    {"for (Transaction t : queue) {", "    ____", "}"}
                };
                p.options = new String[][] {
                    {"final", "abstract", "static", "private"},
                    {"public abstract void process() { }", "public abstract void process();", "public void process();", "abstract void process() { }"},
                    {"@Overload", "@Override", "@Deprecated", "@FunctionalInterface"},
                    {"if (t instanceof CardTransaction) { ... }", "t.process();", "Transaction.process();", "queue.process();"}
                };
                p.correct = new int[] {1, 1, 1, 1};
                p.hint = new String[] {"Which keyword marks a class that is only a template for subclasses?", "Abstract methods have no body: they end with a semicolon.", "This annotation asks the compiler to verify you really override a parent method.", "Just call the method; Java picks the right subclass version at runtime."};
                p.explanation = new String[] {"abstract class: it defines the contract but cannot be instantiated.", "Subclasses are now forced to supply their own process().", "@Override: the compiler checks the signature matches Transaction.process().", "Dynamic method dispatch: each object runs its own process(). No instanceof chain!"};
                break;
            case 4:
                p.title = "MINI-FIX: Extract taxRate into a constants class"; p.file = "TaxConstants.java"; p.gcButton = false; p.doneDialogue = "s5_tax_done";
                p.instruction = new String[] {"Extract taxRate into a final utility constant class."};
                p.code = new String[][] {
                    {"public final class TaxConstants {", "    ____", "}"}
                };
                p.options = new String[][] {
                    {"public static double taxRate = 0.12;", "public static final double TAX_RATE = 0.12;", "private final double taxRate;", "public double TAX_RATE = 0.12;"}
                };
                p.correct = new int[] {1};
                p.hint = new String[] {"A shared constant is static AND final."};
                p.explanation = new String[] {"static final: one immutable value, so PaymentFactory no longer needs to inherit OrderProcessor."};
                break;
            case 5:
                p.title = "MINI-FIX: try-with-resources"; p.file = "ReportJob.java"; p.gcButton = false; p.doneDialogue = "s6_try_done";
                p.instruction = new String[] {"Close the Scanner and connection streams automatically."};
                p.code = new String[][] {
                    {"____ (Scanner sc = new Scanner(System.in)) {", "    process(sc);", "}"}
                };
                p.options = new String[][] {
                    {"using", "try", "with", "catch"}
                };
                p.correct = new int[] {1};
                p.hint = new String[] {"Java's resource-management statement starts with the same keyword as exception handling."};
                p.explanation = new String[] {"try-with-resources closes the resource automatically, even if an exception is thrown."};
                break;
            case 6:
                p.title = "MINI-FIX: Singleton connection manager"; p.file = "ConnectionManager.java"; p.gcButton = false; p.doneDialogue = "s6_conn_done";
                p.instruction = new String[] {"Enforce a private constructor to make this a proper Singleton."};
                p.code = new String[][] {
                    {"public class ConnectionManager {", "    ____ ConnectionManager() { }", "}"}
                };
                p.options = new String[][] {
                    {"public", "private", "static", "protected"}
                };
                p.correct = new int[] {1};
                p.hint = new String[] {"Nobody outside the class should be able to call new ConnectionManager()."};
                p.explanation = new String[] {"private constructor: the class controls its single instance (no more new connections in loops)."};
                break;
            case 7:
                p.title = "MINI-FIX: Restore the DTO getters"; p.file = "UserDTO.java"; p.gcButton = false; p.doneDialogue = "s7_dto_done";
                p.instruction = new String[] {"Jackson needs public getters to serialize this DTO. Restore one."};
                p.code = new String[][] {
                    {"public class UserDTO {", "    private String name;", "    ____", "}"}
                };
                p.options = new String[][] {
                    {"public void getName() { }", "public String getName() { return name; }", "private String getName() { return name; }", "String name = \"\";"}
                };
                p.correct = new int[] {1};
                p.hint = new String[] {"Private field, public getter that returns the field's type."};
                p.explanation = new String[] {"Encapsulation = private fields with controlled public getters (not hiding everything)."};
                break;
            default:
                return null;
        }
        return p;
    }
}
