package org.wso2.milestone.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GitHandler {

    private final static Logger logger = Logger.getLogger(GitHandler.class);
    private String gitToken = null;

    public GitHandler(String gitToken) {
        this.gitToken = gitToken;
    }


    /**
     * Retrive json array of objects from github
     *
     * @param url       - rest endpoint with queries
     * @param mediaType - Accept media type for the request header
     * @return json array of git objects
     */
    private JsonArray getJSONArrayBasic(String url, String mediaType) {
        String responseString;
        JsonElement element;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet requset = new HttpGet(url);
        requset.addHeader("Accept", mediaType);
        requset.addHeader("Authorization", "Bearer " + gitToken);
        HttpResponse response = null;
        JsonArray jsonArray = new JsonArray();
        boolean containsNext = true;

        try {
            response = httpClient.execute(requset);
            logger.info("Request successful for " + url);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            element = new JsonParser().parse(responseString);
            JsonArray firstArray = element.getAsJsonArray();
            jsonArray.addAll(firstArray);
        } catch (NullPointerException e) {
            logger.error("The response with bad request");
        } catch (IOException e) {
            logger.error("Cannot connect to get receive data");
        }

        // handle pagination
        while (containsNext) {
            try {
                if (response.containsHeader("Link")) {
                    Header[] linkHeader = response.getHeaders("Link");
                    Map<String, String> linkMap = this.splitLinkHeader(linkHeader[0].getValue());

                    try {
                        HttpGet requestForNext = new HttpGet(linkMap.get("next"));
                        requestForNext.addHeader("Accept", mediaType);
                        requestForNext.addHeader("Authorization", "Bearer " + gitToken);
                        response = httpClient.execute(requestForNext);

                        String repo_json_next = EntityUtils.toString(response.getEntity(), "UTF-8");
                        JsonElement jelementNext = new JsonParser().parse(repo_json_next);
                        JsonArray jarrNext = jelementNext.getAsJsonArray();

                        for (JsonElement jsonElement : jarrNext) {
                            jsonArray.add(jsonElement.getAsJsonObject());
                        }
                    } catch (IOException e) {
                        logger.info("The request is failed for " + url + " : page =>" + linkMap.get("next"));
                    } catch (NullPointerException e) {
                        logger.info("No data received from http request " + url);
                        containsNext = false;
                    }
                } else {
                    containsNext = false;
                }
            } catch (NullPointerException e) {
                logger.info("The response header may not contain the Link header");
            }
        }

        return jsonArray;
    }


    /**
     * retrive json array of objects from git with default mediatype application/json
     *
     * @param url - rest enpoint with queries
     * @return - json array of git objects
     */
    public JsonArray getJSONArrayFromGit(String url) {
        return this.getJSONArrayBasic(url, "application/json");

    }

    /**
     * retrieve json array of objects from git with custom mediatypes
     *
     * @param url       - rest endpint with queries
     * @param mediaType - custome mediatype
     * @return - json array of git objects
     */
    public JsonArray getJSONArrayFromGit(String url, String mediaType) {
        return this.getJSONArrayBasic(url, mediaType);
    }


    /**
     * return the page changes
     *
     * @param header - the linkHeader of the response
     * @return map containing next page
     */

    private Map<String, String> splitLinkHeader(String header) {
        String[] parts = header.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < parts.length; i++) {
            String[] sections = parts[i].split(";");
            String PaginationUrl = sections[0].replaceFirst("<(.*)>", "$1");
            String urlPagChange = PaginationUrl.trim();
            String name = sections[1].substring(6, sections[1].length() - 1);
            map.put(name, urlPagChange);
        }

        return map;
    }


}
