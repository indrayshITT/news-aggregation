package com.newsaggregation.util;

import java.util.Scanner;

public class InputUtil {
	public static String readString(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}
