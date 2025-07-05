package com.newsaggregation.client;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newsaggregation.util.APIService;
import com.newsaggregation.util.InputUtil;

public class ExternalServerClient {
	public static void showMenu(Scanner sc) throws JSONException {
        while (true) {
            System.out.println("\n-- External Server Management --");
            System.out.println("1. View list of external servers and status");
            System.out.println("2. View external server details");
            System.out.println("3. Update external server API key");
            System.out.println("4. Add new category");
            System.out.println("5. Back");
            int choice = InputUtil.readInt(sc, "Choose an option: ");

            switch (choice) {
                case 1 -> viewServerList();
                case 2 -> viewServerDetails(sc);
                case 3 -> updateServer(sc);
                case 4 -> CategoryClient.addCategory(sc);
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void viewServerList() throws JSONException {
        String res = APIService.send("/api/admin/external-servers/list", "GET", null);
        JSONArray servers = new JSONArray(res);
        System.out.println("\nExternal Servers:");
        for (int i = 0; i < servers.length(); i++) {
            JSONObject server = servers.getJSONObject(i);
            System.out.printf("%d. %s - %s - last accessed: %s\n",
                server.getInt("id"),
                server.getString("name"),
                server.getBoolean("active") ? "Active" : "Not Active",
                server.optString("lastAccessed", "Never")
            );
        }
    }

    private static void viewServerDetails(Scanner sc) throws JSONException {
        String res = APIService.send("/api/admin/external-servers/details/", "GET", null);
        JSONArray servers = new JSONArray(res);

        for (int i = 0; i < servers.length(); i++) {
            JSONObject obj = servers.getJSONObject(i);
            System.out.printf("%d. %s - API Key: %s\n",
                obj.getInt("id"),
                obj.getString("name"),
                obj.getString("api_key")
            );
        }
    }

    private static void updateServer(Scanner sc) throws JSONException {
        int id = InputUtil.readInt(sc, "Enter the external server ID: ");
        String newKey = InputUtil.readString(sc, "Enter the updated API key: ");
        sc.nextLine();

        JSONObject payload = new JSONObject();
        payload.put("id", id);
        payload.put("apiKey", newKey);

        String res = APIService.send("/api/admin/external-servers", "POST", payload.toString());
        JSONObject result = new JSONObject(res);

        System.out.println(result.optString("message"));
    }
}
