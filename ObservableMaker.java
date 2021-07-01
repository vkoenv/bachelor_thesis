import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public class ObservableMaker {
    int sizeo;
    int tmax;
    double a0;
    double a1;
    double b0;
    double b1;
    private static DecimalFormat df = new DecimalFormat("0.00000000");
    /**
     * Constructor for the observable maker classes setting up the variables
     * @param sizeo size of observation space
     * @param tmax size of time interval
     * @param a0 coefficient for p1 = a0*t + b0
     * @param a1 coefficient for p1 = a1*t + b1
     * @param b0 constant for p1 = a0*t + b0
     * @param b1 constant for p1 = a1*t + b1
     */
    public ObservableMaker(int sizeo, int tmax, double a0, double a1, double b0, double b1){
        this.sizeo = sizeo;
        this.tmax = tmax;
        this.a0 = a0;
        this.a1 = a1;
        this.b0 = b0;
        this.b1 = b1;
    }
    /**
     * creates factorial n
     * @param n size of the factorial
     * @return !n
     */
    public BigDecimal factorial(int n){
        if(n>0)
            return new BigDecimal(n).multiply(factorial(n-1));
        else
            return new BigDecimal(1);
    }
    /**
     * Creates a combination for any n and r
     * @param n total number of objects in the set
     * @param r number of choosing objects from the set
     * @return number of combinations
     */
    public BigDecimal combi(int n, int r){
        return factorial(n).divide((factorial(n-r).multiply(factorial(r))));
    }
    /**
     * Generates the transitional probability of a single observation at a specific time given the state of the system.
     * @param k the observation
     * @param s0 the state of the system, true is in-control, false is out-of-control
     * @param t the time
     * @return the transitional probability for observation k at time t and state s0.
     */
    public BigDecimal probability(int k, boolean s0, int t){
        if(s0)
            return combi(sizeo,k).multiply(new BigDecimal(a0*t+b0).pow(k)).multiply(new BigDecimal(1-(a0*t+b0)).pow(sizeo-k));
        else
            return combi(sizeo,k).multiply(new BigDecimal(a1*t+b1).pow(k)).multiply(new BigDecimal(1-(a1*t+b1)).pow(sizeo-k));
    }
    /**
     * Generates a binomial distribution
     * @param s0 the state of the system, true is in-control, false is out-of-control
     * @param t the time
     * @return the transitional probabilities at time t and state s0
     */
    public BigDecimal[] Binom(boolean s0,int t){
        BigDecimal[] bigD = new BigDecimal[sizeo+1];
        for(int k = 0; k<=sizeo; k++) bigD[k] = probability(k, s0, t).round(new MathContext(8, RoundingMode.HALF_EVEN));
        return bigD;
    }
    /**
     * formats the binomial distribution for use in prism
     * @param bd the binomial distribution
     * @return a string for the update part of an observation command
     */
    public String Binomial( BigDecimal[] bd){
        String tussen = "";
        for(int i = 0; i<=sizeo; i++){
            if(i != 0)
                tussen += " + ";
            tussen += df.format(bd[i]) +  ":(o' = " + i + ") & (b' = true)";
        }
        tussen += ";";
        return tussen;
    }
    /**
     * formats the entire observation
     * @return formatted observations for use in prism
     */
    @Override
    public String toString(){
        String tussen = "";
        for(int t = 0; t<=tmax;t++){
            tussen+="//t=" + t;
            tussen += "\n[o] i=0 & t=" + t + "& !b -> " ;
            tussen += Binomial(Binom(true,t));
            tussen += "\n[o] i=1 & t=" + t + "& !b -> " ;
            tussen += Binomial(Binom(false,t));
            tussen += "\n";
        }
        return tussen;
    }
}
