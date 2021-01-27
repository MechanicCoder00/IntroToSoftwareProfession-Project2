import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Scanner;

/*
Title:                          HW2 “Looking for Fermat’s Last Theorem Near Misses”
Name of file:                   Main.java
List of external files needed:  N/A
List of external files created: N/A
Name of Programmers:            Ted King, Scott Tabaka
Email of Programmers:           tmkbf9@mail.umsl.edu, satk5n@mail.umsl.edu
Course/Section Number:          CMP SCI 4500-001
Date finished/submitted:        2/12/2020
Purpose of program:				This program finds the smallest relative misses of (x^n + y^n) and (z^n). The smallest
								relative miss being last.  This is supposed to confirm Fermat's theorem is correct.
Resources used:                 StackOverflow.com, W3schools.com, Wolfram Alpha Calculator
*/

public class Main {
	private static Scanner sc = new Scanner(System.in); // Scanner variable for user input

	// Prevents BigDecimal from truncating math results.
	private static MathContext mc = new MathContext(100, RoundingMode.HALF_UP);
	// Exclusively used for adding 0 or 1 to the Z value during output
	private static int plusOne = 0;


	private static void printInstructions()
	{
		System.out.println("This program will calculate the smallest relative miss of Fermat's Theorem. " +
				"x^n + y^n = z^n");
		System.out.println("Two input will be needed from the user. (1.Exponent for n.  2.k which is the limit " +
				"for x and y)\n");

	}

	private static int getN(int n) { // Function to get value of n from user

		// Loop to get correct input for n, will loop indefinitely until correct input is given
		while (n < 3 || n > 11) {
			System.out.print("Please enter a integer value for the exponent n. (3-11): ");
			n = sc.nextInt(); // gets user input and stores it in k
		}
		return n;
	}

	private static int getK(int k, int n) { // Function to get value of k from user

		// Array of limit values for k dependent on n(Limited for speed, but can accept much higher numbers)
		int[] kLimit = {3000,2600,2400,2200,2000,1800,1600,1400,1000}; //Array of limit values for k dependent on n

		// Loop to get correct input for k, will loop indefinitely until correct input is given
		while (k < 11 || k > kLimit[n - 3]) {
			System.out.print("Please enter a integer value for k which limits x and y. (11-" + kLimit[n - 3] + "): ");
			k = sc.nextInt(); // gets user input and stores it in k
		}
		return k;
	}

	private static double dbfindXNYN(int x, int y, int n) { // Function to calculate (x^n + y^n)
		return ((Math.pow(x, n)) + (Math.pow(y, n)));
	}

	private static double dbfindZ(double xnyn, int n) { // Function to calculate z from the inverse root of (x^n + y^n)
		double pow = 1 / (double) n;
		return Math.pow(xnyn, pow);
	}

	// Function to calculate A^N and return a BigDecimal
	private static BigDecimal findAPowN(int a, int n) {
		BigDecimal big = new BigDecimal(a);
		return big.pow(n, mc);
	}

	private static void findSmallestRelativeMiss(int n, int k, BigDecimal smallestFract) { // Function to find the smallest relative miss
		int x, y; // Variable to store values of x and y
		double xnyn, z, z1; // Variable to store values of (x^n + y^n), and z.
		// double fract; //Variable to store values of relative miss
		BigDecimal xn, yn, zLowActual, zHighActual, bigXNYN;
		BigDecimal miss, fract, zLow, zHigh;

		// Two nested loops for iterating through all combinations of x and y to find the smallest relative miss
		for (x = 10; x <= k; x++) {
			for (y = 10; y <= k; y++) {

				xnyn = dbfindXNYN(x, y, n); // Function call to find value of (x^n + y^n)

				z = dbfindZ(xnyn, n);
				// Function call to find the value of z Calculates X^N
				xn = findAPowN(x, n);
				// Calculates Y^N
				yn = findAPowN(y, n);
				// Calculates X^N + Y^N
				bigXNYN = xn.add(yn, mc);
				// Calculates Z = (X^N + Y^N) ^ (1/N)
				z1 = dbfindZ(bigXNYN.doubleValue(), n);
				// Turns double into integer and stores it in a BigDecimal
				zLowActual = new BigDecimal(Math.floor(z1));
				zHighActual = new BigDecimal(Math.floor(z1) + 1);
				// Finds Z values for Z^N and (Z + 1)^N
				zLow = zLowActual.pow(n, mc);
				zHigh = zHighActual.pow(n, mc);
				// Function call to find the value of the actual miss
				miss = bigFindMiss(xn, yn, zLow, zHigh);
				// Function call to find the value of relative miss
				fract = bigFindFract(miss, xn, yn);
				// Prints values for the smallest relative miss
				if (fract.compareTo(smallestFract) == -1) {
					System.out.println("At n=" + n + " and k=" + k);
					System.out.println("The (x,y,z) of the smallest relative miss is (" + x + ", " + y + ", " + (((int) z) + plusOne) + ")");
					System.out.println("The actual miss was " + miss + " while the relative miss was " + fract + ".");
					smallestFract = fract; // Copies value of the relative miss into smallest relative miss found
					System.out.println();
				}
			}
		}
	}

	// Function to calculate the smallest actual miss
	private static BigDecimal bigFindMiss(BigDecimal bigXN, BigDecimal bigYN, BigDecimal zLow, BigDecimal zHigh) {
		BigDecimal miss, xnyn; // Big Decimal variable to store value of miss and xnyn
		xnyn = bigYN.add(bigXN, mc);	//Calculates xnyn
		BigDecimal var1,var2,var3,var4;	//Variables that can swap places in order to keep miss positive

		if(xnyn.compareTo(zLow) >= 0){	//If xnyn is greater than zLow
			var1 = xnyn;
			var2 = zLow;
		} else {						//If xnyn is less than zLow
			var1 = zLow;
			var2 = xnyn;
		}

		if(xnyn.compareTo(zHigh) <= 0){	//If xnyn is less than zHigh
			var3 = xnyn;
			var4 = zHigh;
		} else {						//If xnyn is greater than zHigh
			var3 = zHigh;
			var4 = xnyn;
		}

		// Determines if zLow or zHigh is closer to x^n + y^n and stores the smallest in "miss"
		if ((var1.subtract(var2)).compareTo(var4.subtract(var3)) <= 0) {
			miss = var1.subtract(var2, mc);
			plusOne = 0;
		} else {
			miss = var4.subtract(var3, mc);
			plusOne = 1;
		}
		return miss;
	}

	// Function to calculate the relative miss fraction using BigDecimal variable to store value of (x^n + y^n)
	private static BigDecimal bigFindFract(BigDecimal miss, BigDecimal bigXN, BigDecimal bigYN) {
		BigDecimal xnyn;
		xnyn = bigYN.add(bigXN, mc);
		return miss.divide(xnyn,60, RoundingMode.HALF_UP); // Calculates the relative miss in BigDecimal and returns
	}

	private static void waitForEnter() // function to wait for the "Enter" key to be pressed
	{
		sc.nextLine(); // Clears the previous "Enter" from earlier input from
		// scanner
		System.out.print("Press Enter key to continue...");
		sc.nextLine(); // Waits for "Enter" to be pressed
	}

	public static void main(String[] args) {

		int n = 0, k = 0; // variable for storing initial values of n and k
		BigDecimal smallestFract = new BigDecimal(1); // variable for storing initial value of the smallest relative miss

		printInstructions();
		 n = getN(n); //Function call to get the value of n from user
		 k = getK(k,n); //Function call to get the value of k from user

		findSmallestRelativeMiss(n, k, smallestFract); // Function call to find the smallest relative miss dependent on n and k using double

		 waitForEnter(); //Function call to wait for "Enter" to be pressed
	}
}
