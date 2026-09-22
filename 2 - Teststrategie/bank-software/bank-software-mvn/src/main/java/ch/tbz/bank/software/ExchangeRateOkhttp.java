package ch.tbz.bank.software;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExchangeRateOkhttp {

    public double getExchangeRate(String currencyFrom, String currencyTo) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url("https://api.apilayer.com/exchangerates_data/convert?to="+currencyTo+"&from="+currencyFrom+"&amount=1")
                .addHeader("apikey", "aZA8SRPPWKe8RCu4fLT9dGtgAUfkwVfS")
                .method("GET", null)
                .build();

        try{
            Response response = client.newCall(request).execute();

            Gson gson = new Gson();
            Rate r = gson.fromJson(response.body().string(), Rate.class);
            return r.result;
        } catch (Exception e) {
            System.out.println("! Error bei der Abfrage des Wechselkurses: " + e.getMessage());
            return 0.0;
        }
    }

    public class Rate {
        public double result;
    }

}
