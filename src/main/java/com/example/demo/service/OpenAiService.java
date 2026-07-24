package com.example.demo.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



@Service
public class OpenAiService {


    @Value("${openai.api.key}")
    private String apiKey;



    private final ObjectMapper mapper =
            new ObjectMapper();



    public String ask(String question){


        OkHttpClient client =
                new OkHttpClient();



        String json =
        """
        {
          "model":"gpt-4.1-mini",
          "messages":[
            {
              "role":"user",
              "content":"%s"
            }
          ]
        }
        """.formatted(
                question.replace("\"","\\\"")
        );



        RequestBody body =
                RequestBody.create(
                        json,
                        MediaType.parse(
                        "application/json")
                );



        Request request =
                new Request.Builder()

                .url(
                "https://api.openai.com/v1/chat/completions"
                )

                .addHeader(
                "Authorization",
                "Bearer " + apiKey
                )

                .addHeader(
                "Content-Type",
                "application/json"
                )

                .post(body)

                .build();




        try(Response response =
                client.newCall(request).execute()){


            String result =
                    response.body().string();



            System.out.println("OpenAI結果:");
            System.out.println(result);



            JsonNode root =
                    mapper.readTree(result);



            // エラー確認
            if(root.has("error")){

                return "OpenAIエラー\n"
                        + root
                        .path("error")
                        .path("message")
                        .asText();

            }



            JsonNode choices =
                    root.path("choices");



            if(choices.isArray()
                    && choices.size()>0){


                return choices
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();

            }



            return "AI回答を取得できませんでした";


        }catch(Exception e){


            e.printStackTrace();


            return "通信エラー : "
                    + e.getMessage();

        }


    }


}