/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.webscrapinglogic;

/**
 *
 * @author dvo
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import org.jsoup.*;
import org.jsoup.nodes.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONException;

public class WebScraping  {
    /**
    *   Class constructor or initialization method.
    */
    public WebScraping() {
    }
    /**
     * This function web scraps bloomberg for news articles on the ticker
     * and add the title of the article and url to JSONObject
     * @param ticker jse alpha code 
     * @param proxyEnable allows you to use a proxy to reach all the websites
     * @param proxyHost the hostname of the proxy
     * @param proxyPort the port used to access the website via the proxy
     * @return result with all the articles bloomberg for a certain ticker
     */
    public List<Map<String, Object>> moneywebScraping(String ticker, String proxyEnabled, String proxyHost, String proxyPort) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        String url = "https://www.moneyweb.co.za/tools-and-data/click-a-company/?shareCode=" + ticker;
        try {
            URL obj = new URL(url);
            Proxy proxy = null;
            if (proxyEnabled.equals("true") && proxyHost.length() > 0 && proxyPort.length() > 0) {
                InetSocketAddress proxyInet = new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort));
                proxy = new Proxy(Proxy.Type.HTTP, proxyInet);
            }
            HttpsURLConnection con = proxyEnabled.equals("true") ? (HttpsURLConnection) obj.openConnection(proxy)
                    : (HttpsURLConnection) obj.openConnection();
    
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            con.setRequestProperty("Connection", "keep-alive");
            if (con.getResponseCode() < 400) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                int i = 0;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                    i = i + 1;
                }
                in.close();
                String content = response.toString();
                Document parse = Jsoup.parse(content);
                org.jsoup.select.Elements elements = parse.getElementsByClass("col-lg-9 col-md-9 col-sm-8 col-xs-9");
    
                for (Element element : elements) {
                    Document parse1 = Jsoup.parse(element.toString());
                    String dateString = String.join("", parse1.getElementsByClass("byline").eachText());
                    String[] dateList = dateString.replaceAll("\n", "").split(" ");
                    String ArticleDay = dateList[0];
                    String ArticleMonth = dateList[1];
                    String ArticleYear = dateList[2];
                    url = element.getElementsByTag("a").eachAttr("href").toString().replace("[", "").replace("]", "");
                    String title = element.getElementsByTag("a").eachAttr("title").toString().replace("[", "").replace("]",
                            "");
                    
                    title = title.replaceAll("\u2018", "'");
                    title = title.replaceAll("\u2019", "'");
                    title = title.replaceAll("\u2026", "");
                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
                    Date dateOfArticle = formatter.parse(dateString);
                    Map<String, Object> article = new LinkedHashMap<>();
                    article.put("title", title);
                    article.put("url", url);
                    article.put("date", (dateOfArticle));
                    result.add(article);
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }        

        return result;
    }
    /**
     * This function web scraps bloomberg for news articles on the ticker
     * and add the title of the article and url to JSONObject
     * @param ticker jse alpha code 
     * @param proxyEnable allows you to use a proxy to reach all the websites
     * @param proxyHost the hostname of the proxy
     * @param proxyPort the port used to access the website via the proxy
     * @return result with all the articles bloomberg for a certain ticker
     */
    public List<Map<String, Object>> bloombergScraping(String ticker, String proxyEnabled, String proxyHost, String proxyPort) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        
        try {

            String url = "https://www.bloomberg.com/quote/" + ticker + ":SJ";
            URL obj = new URL(url);
            HttpsURLConnection con = null;
                Proxy proxy = null;

                if (proxyEnabled.equals("true") && proxyHost.length() > 0 && proxyPort.length() > 0) {
                    InetSocketAddress proxyInet = new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort));
                    proxy = new Proxy(Proxy.Type.HTTP, proxyInet);
                }

                con = proxyEnabled.equals("true") ? (HttpsURLConnection) obj.openConnection(proxy)
                        : (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
            if (con.getResponseCode() < 400) {
                InputStream is = new BufferedInputStream(con.getInputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {

                    response.append(inputLine);
                }
                String content = response.toString();
                Document parse = Jsoup.parse(content);
                org.jsoup.select.Elements elements = parse.getElementsByClass("container__170fc8e2 noExpand__45259a50");
                int ArticleNumber = 0;
                for (Element element : elements) {
                    String Sentiment = "";
                    Document parse1 = Jsoup.parse(element.toString());
                    String title = String.join("", parse1.getElementsByClass("headline__07dbac92 ").eachText());
                    String dateNow = String.join("", parse1.getElementsByClass("publishedAt__4009bb4f ").eachText());
                    url = element.getElementsByTag("a").eachAttr("href").toString().replace("[", "").replace("]", "");
                    title = title.replaceAll("\u2018", "'");
                    title = title.replaceAll("\u2019", "'");
                    title = title.replaceAll("\u2026", "");
                    SimpleDateFormat formatter = new SimpleDateFormat("MM dd,yyyy");
                    Date date = formatter.parse(dateNow);
                    Map<String, Object> article = new LinkedHashMap<>();
                    article.put("title", title);
                    article.put("url", url);
                    article.put("date", (date));
                    result.add(article);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection Error");
        }

        return result;
    }
    /**
     * This functions start the web scraping for articles and does this for a period of the past 30 days.
     * It runs moneywebScraping and moneywebScraping and searches for articles over the past 30 days.
     * @param ticker jse alpha code
     * @param bloombergEnabled determine if Bloomberg can be scraped 
     * @param proxyEnable allows you to use a proxy to reach all the websites
     * @param proxyHost the hostname of the proxy
     * @param proxyPort the port used to access the website via the proxy
     * @return JSONObject that hold article title as key and URL as value  
     */
    public List<Map<String, Object>> run(String ticker, String bloombergEnabled, String proxyEnabled, String proxyHost, String proxyPort)  {

        List<Map<String, Object>> articles = new ArrayList<Map<String, Object>>();

        try {

            if (bloombergEnabled.equals("true")) {
                articles.addAll(this.bloombergScraping(ticker, proxyEnabled, proxyHost, proxyPort));
            }
            articles.addAll(this.moneywebScraping(ticker, proxyEnabled, proxyHost, proxyPort));

        } catch(Exception ex) {
            ex.printStackTrace();
        }
        // return all the articles after websites were scraped for JSE Ticker 
        return articles;
    }
    //main allow you to test the WebScarping System
    public static void main(String[] args) throws ClassNotFoundException, IOException, JSONException, KeyManagementException, NoSuchAlgorithmException, ParseException {
        //Instant allows the system to check the of webscraping a website like Moneyweb
        Instant start = Instant.now();
        //Create object of TYpe Map to allow the system to transfer the data to variable news
        List<Map<String, Object>> news = new WebScraping().run("APN", "false", "false", "", "");
        //Get the end time of scraping websites and calculates the time from start till now to show the speed of the system
        Duration interval = Duration.between(start, Instant.now());
        //Print the execution time
        System.out.println("Execution time in seconds: " + interval.getSeconds());
        //Print the news List to a string 
        System.out.println(news.toString());
    }
}

