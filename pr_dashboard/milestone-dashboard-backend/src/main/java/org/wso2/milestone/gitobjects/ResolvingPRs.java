package org.wso2.milestone.gitobjects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ResolvingPRs {
    private String prId;
    private String prLink;
    private String resolvingIssue;
    private String status;
    private String resolvingRepoName;
    private String title;
    private ArrayList<String> labels;



    public ResolvingPRs(String prId) {
        this.prId = prId;
    }

    public void setPrLink(String prLink) {
        this.prLink = prLink;
    }

    public void setResovingIssue(String resovingIssue) {
        this.resolvingIssue = resovingIssue;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResolvingRepoName(String resolvingRepoName) {
        this.resolvingRepoName = resolvingRepoName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public String getTitle() {
        return title.replace("\"","");
    }

    public String getPrId() {
        return prId;
    }

    public String getPrLink() {
        return prLink.replace("\"","");
    }

    public String getResovingIssue() {
        return resolvingIssue;
    }

    public String getStatus() {
        return status;
    }

    public String getRepoName() {
        return resolvingRepoName;
    }


    /**
     * create josn object of current pr instance
     * @return
     */
    public JsonObject getJsonObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pr-id",this.getPrId());
        jsonObject.addProperty("title",this.getTitle());
        jsonObject.addProperty("pr-link",this.getPrLink());
        jsonObject.addProperty("status",this.getStatus());
        jsonObject.addProperty("isCodePR",this.isCodePR());

        return jsonObject;
    }

    public String getJsonString(){
        return this.getJsonObject().toString();
    }


    /**
     * check whether the pr is a doc pr or code pr
     * @return
     */
    private boolean isCodePR(){
        boolean type = true;
        for(String label: labels){
            label = label.replace("\"","");
            if(label.equals("doc") || label.equals("Doc")){

                type =false;
            }
        }
        return type;
    }

    /**
     * create json array of lables
     * @return
     */
    private JsonArray generateLabelJsonArray(){
        JsonArray jsonArray = new JsonArray();
        for(String label: labels){
            jsonArray.add(label);
        }

        return jsonArray;
    }


}
