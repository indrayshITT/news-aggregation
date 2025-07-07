package com.newsaggregation.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

public class InputUtil {
	
	 public static String readString(Scanner scanner, String prompt) {
	        System.out.print(prompt);
	        String input = scanner.nextLine().trim();
	        return input.isEmpty() ? null : input;
	    }

	    public static int readInt(Scanner scanner, String prompt) {
	        Optional<Integer> value = Optional.empty();
	        int attempts = 3;

	        while (attempts-- > 0 && value.isEmpty()) {
	            System.out.print(prompt);
	            String input = scanner.nextLine().trim();
	            try {
	                value = Optional.of(Integer.parseInt(input));
	            } catch (NumberFormatException e) {
	                System.out.println("Please enter a valid number.");
	            }
	        }

	        return value.orElse(-1);
	    }

	    public static LocalDate readDate(Scanner scanner, String prompt) {
	        Optional<LocalDate> date = Optional.empty();
	        int attempts = 3;
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        while (attempts-- > 0 && date.isEmpty()) {
	            System.out.print(prompt);
	            String input = scanner.nextLine().trim();
	            try {
	                date = Optional.of(LocalDate.parse(input, formatter));
	            } catch (DateTimeParseException e) {
	                System.out.println("Invalid date format. Please enter in YYYY-MM-DD format.");
	            }
	        }

	        return date.orElse(null);
	    }

	    public static boolean readBoolean(Scanner scanner, String prompt) {
	        Optional<Boolean> result = Optional.empty();
	        int attempts = 3;

	        while (attempts-- > 0 && result.isEmpty()) {
	            System.out.print(prompt);
	            String input = scanner.nextLine().trim().toLowerCase();

	            if (input.equals("yes") || input.equals("y")) {
	                result = Optional.of(true);
	            } else if (input.equals("no") || input.equals("n")) {
	                result = Optional.of(false);
	            } else {
	                System.out.println("Please answer with 'yes' or 'no'.");
	            }
	        }

	        return result.orElse(false);
	    }
}
