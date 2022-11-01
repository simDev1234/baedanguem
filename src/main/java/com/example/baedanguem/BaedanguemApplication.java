package com.example.baedanguem;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication
public class BaedanguemApplication {

    public static void main(String[] args) {
        //SpringApplication.run(BaedanguemApplication.class, args);

        // https://jsoup.org/apidocs/
        try {
            Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1667174400&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");
            Document document = connection.get(); // GET 방식으로 요청

            Elements eles = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element ele = eles.get(0);

            Element tbody = ele.children().get(1);

            for (Element e : tbody.children()) {

                String text = e.text();

                if (!text.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = text.split(" ");
                String month = splits[0];
                int day = Integer.parseInt(splits[1].replace(",",""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                System.out.println(year + " " + month + " " + day + " -> " + dividend);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


