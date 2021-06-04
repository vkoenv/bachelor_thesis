public class Main {

    public static void main(String[] args) {
        // fill in your value for size of the observation space (sizeo), maximum time (tmax), and the other variables that make up the bionomial coefficient.
        ObservableMaker observableMaker = new ObservableMaker(5, 1, 0.03, 0.03, 0.4, 0.7);
        System.out.println(observableMaker.toString());
    }
}
