package pl.rpieja.flat.api;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import java.io.IOException;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.rpieja.flat.dto.ChargesDTO;
import pl.rpieja.flat.dto.SessionCheckResponse;

/**
 * Created by radix on 10/29/17.
 */

public class FlatAPI {

    private OkHttpClient client;
    private static final String apiAdress = "https://api.flat.memleak.pl/";
    private static final String sessionCheckUrl = apiAdress + "session/check";
    private static final String createSession = apiAdress + "session/create";
    private static final String getCharges = apiAdress + "charge/";

    //    private static final String apiAdress ="http://j3b.tobiasz.maxmati.pl:8080";

    public FlatAPI(CookieJar cookieJar) {
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    public Boolean login(String username, String password) throws IOException {
        String json = "{\"name\":\"" + username + "\", \"password\": \"" + password + "\"}";

        Request request = new Request.Builder()
                .url(createSession)
                .post(RequestBody.create(APILogIn.JSON, json))
                .build();
        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    public Boolean validateSession() throws IOException {
        Request request = new Request.Builder()
                .url(sessionCheckUrl)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) return false;
        Gson gson = new Gson();
        SessionCheckResponse checkResponse = gson.fromJson(response.body().string(), SessionCheckResponse.class);
        if (checkResponse == null) return false;
        return "ok".equals(checkResponse.error);
    }

    public ChargesDTO getCharges(int month, int year) throws IOException, NoInternetConnectionException{

        String requestUrl = getCharges + Integer.toString(year) + "/" + Integer.toString(month);

        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        Response response=client.newCall(request).execute();
        if (!response.isSuccessful()) throw new NoInternetConnectionException();
        Gson gson=new Gson();
        ChargesDTO charges=gson.fromJson(response.body().string(), ChargesDTO.class);
        if (charges==null) throw new JsonIOException("JSON parsing exception");
        return charges;
    }
}
