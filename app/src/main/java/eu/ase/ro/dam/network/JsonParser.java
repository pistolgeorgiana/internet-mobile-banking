package eu.ase.ro.dam.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.ase.ro.dam.database.model.Account;

public class JsonParser {
    public static Map<String, List<Item>> parseJson(String json) {
        Map<String, List<Item>> items = new HashMap<String, List<Item>>();
        if(json == null) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            List<Item> americanBank = getItemListFromJson(
                    object.getJSONArray("AmericanBank"));
            List<Item> romanianBank = getItemListFromJson(
                    object.getJSONArray("RomanianBank"));
            List<Item> swissBank = getItemListFromJson(
                    object.getJSONArray("SwitzerlandBank"));
            items.put("AmericanBankClients", americanBank);
            items.put("RomanianBankClients", romanianBank);
            items.put("SwissBankClients", swissBank);
            return items;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Item> getItemListFromJson(JSONArray array) throws JSONException {
        if(array == null) {
            return null;
        }
        List<Item> result = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Item item = getItemFromJson(array.getJSONObject(i));
            if(item != null) {
                result.add(item);
            }
        }
        return result;
    }

    private static Item getItemFromJson(JSONObject object) throws JSONException {
        String company = object.getString("company");
        String city = object.getString("city");
        Account account = getAccount(object.getJSONObject("client"));
        return new Item(company, city, account);
    }

    private static Account getAccount(JSONObject object) throws JSONException {
        if(object == null) {
            return null;
        }
        String iban = object.getString("iban");
        String name = object.getString("name");
        Double amount = object.getDouble("balance");
        String currency = object.getString("currency");
        return new Account(iban, name, currency, amount);
    }
}
