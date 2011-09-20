
package cashmanager.database;

/**
 *
 * @author Kiko
 */
public class CausalAmount implements Comparable{

    private String causal;
    private double totalAmount;

    public CausalAmount(){
        causal = "";
        totalAmount = 0;
    }
    public CausalAmount(String causal, double totalAmount){
        this.causal = causal;
        this.totalAmount = totalAmount;
    }
    public String getCausal(){
        return causal;
    }
    public void setCausal(String causal){
        this.causal = causal;
    }
    public double getTotalAmount(){
        return totalAmount;
    }
    public void setTotalAmount(double amount){
        totalAmount = amount;
    }
    public int compareTo(Object o){
        CausalAmount tmp = (CausalAmount) o;
        return Double.compare(totalAmount, tmp.getTotalAmount());
    }
    
}//CausalAmount
