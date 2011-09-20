
package cashmanager.database;

/**
 *
 * @author Kiko
 */
public class IncomeOutcome {

    private double income;
    private double outcome;

    public IncomeOutcome(){

    }
    public IncomeOutcome(double in, double out){
        income = in;
        outcome = out;
    }
    public double getIncome(){
        return income;
    }
    public void setIncome(double in){
        income = in;
    }
    public double getOutcome(){
        return outcome;
    }
    public void setOutcome(double out){
        outcome = out;
    }
}//IncomeOutcome
