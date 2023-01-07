package com.example.rest_api_example_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    String ip_address = "http://85.143.223.149";
    String ip_port = "2020";

    private ArrayList<String> postTemplate(String data) throws IOException {

        URL IPAddress = new URL(ip_address + ":" + ip_port + "/TestConnection/");
        HttpURLConnection URLConnection = null;

        try {
            URLConnection = (HttpURLConnection) IPAddress.openConnection();

            URLConnection.setRequestProperty("User-Agent", "rest-api-example-app-1.0.0");
            URLConnection.setRequestProperty("Content-Type", "application/json");
            URLConnection.setRequestMethod("POST");

            URLConnection.setDoOutput(true);

            //Преобразуем входные данные в байт-код
            OutputStream outputStream = URLConnection.getOutputStream();
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);

            //Читаем поток данных от сервера = выполняем запрос к серверу
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            URLConnection.getInputStream(), StandardCharsets.UTF_8
                    ));

            //Обрабатываем запрос от сервера = преобразуем полученный байткод в строку
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            ArrayList<String> returnArray = new ArrayList<>();
            returnArray.add( String.valueOf(URLConnection.getResponseCode()) );
            returnArray.add(response.toString());

            return returnArray;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (URLConnection != null) {
                URLConnection.disconnect();
            }
        }
        return new ArrayList<>();
    }

    private void buttonGetClick(TextView textView1, TextView textView2) throws IOException {
        URL IPAddress = new URL(ip_address + ":" + ip_port + "/TestConnection/");
        HttpURLConnection URLConnection = null;

        try {
        URLConnection = (HttpURLConnection) IPAddress.openConnection();
        URLConnection.setRequestProperty("User-Agent", "rest-api-example-app-1.0.0");
        URLConnection.setRequestProperty("Content-Type", "application/json");
        URLConnection.setRequestMethod("GET");

        String resultCode = String.valueOf(URLConnection.getResponseCode());
        String resultBody = String.valueOf(URLConnection.getResponseMessage());

        this.runOnUiThread(() -> {
            textView1.setText(resultCode);
            textView2.setText(resultBody);
        });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (URLConnection != null) {
                URLConnection.disconnect();
            }
        }
    }

    private void buttonPostClick(TextView textView1, TextView textView2, EditText textEdit) throws IOException {
        String data = String.valueOf(textEdit.getText());

        ArrayList<String> response = postTemplate(data);

        this.runOnUiThread(() -> {
            try {
                textView1.setText(response.get(0));
                textView2.setText(response.get(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Runnable asyncTaskButtonGetClick(TextView textView1, TextView textView2) {
        try {
            buttonGetClick(textView1, textView2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initButtonClicks() {

        Button buttonGetEvent = findViewById(R.id.button_get);
        Button buttonPostEvent = findViewById(R.id.button_post);
        TextView textView1 = findViewById(R.id.text1);
        TextView textView2 = findViewById(R.id.text2);
        EditText textEdit = findViewById(R.id.request_body);

        buttonGetEvent.setOnClickListener(v -> {
            AsyncTask.execute(asyncTaskButtonGetClick(textView1, textView2));
        });

        buttonPostEvent.setOnClickListener(v -> {
            AsyncTask.execute(() -> {
                try {
                    buttonPostClick(textView1, textView2, textEdit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtonClicks();
    }
}