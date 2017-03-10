package main;

import java.util.Random;

public class Test {
	public static void main(String args[]){
		Random r1 = new Random(9);
		Random r2 = new Random(9);
		System.out.println(r1.nextDouble()+" ,"+r2.nextDouble());
	}

}
