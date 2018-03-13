/*
 * Copyright (c) 2415, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var result =[]
function getDataFromService(){


    $.post("/portal/controllers/apis/Dependency-Updater-Gadget/DependencyUpdateBuildData.jag", {
        action: "getBuild"
    },
    function (data) { 
        productList = JSON.parse(JSON.parse(JSON.parse(data).data).data);
        productAreas = Object.keys(productList);
        addHeader();
        addRows();
    });

}
function showFailedComponents(){
    alert("Clicked");
}
function getStatusImage(i){
    var imageFolderPath = "/portal/store/carbon.super/gadget/dependency-updater/images";
    if(i==0){
        return imageFolderPath+"/Failed.png";
    }
    else if(i==1){
        return imageFolderPath+"/Success.png";
    }
    else if(i==2){
        return imageFolderPath+"/na.png";
    }
    else if(i==3){
        return imageFolderPath+"/na.png";
    }
    else if( i==11){
        return imageFolderPath+"/Stormy.png";
    }
    else if( i==12){
        return imageFolderPath+"/Rainy.png";
    }
    else if( i==13){
        return imageFolderPath+"/Cloudy.png";
    }
    else if( i==14){
        return imageFolderPath+"/PartlyCloudy.png";
    }
    else if( i==15){
        return imageFolderPath+"/Sunny.png";
    }
    

}
function addHeader() {
    var date = [];
    var currentDate = new Date();
    for (var i = 0; i < 7; i++) {
        currentDate.setDate(currentDate.getDate() - 1);
        date.push(new Date(currentDate).toString());
    }
    $("#report").append("" +
        "<tr>" +
        "<th>"+"Product"+"</th>" +
        "<th>"+date[5].substring(3, 10)+"</th>" +
        "<th>"+date[4].substring(3, 10)+"</th>" +
        "<th>"+date[3].substring(3, 10)+"</th>" +
        "<th>"+date[2].substring(3, 10)+"</th>" +
        "<th>"+date[1].substring(3, 10)+"</th>" +
        "<th>"+date[0].substring(3, 10)+"</th>" +
        "<th>"+"Current"+"</th>" +
        "<th>"+"Weekly"+"</th>" +
        "<th>"+"Monthly"+"</th>" +
        "<th></th></tr>");       
}
function addRows(){
    for (index = 0; index < productAreas.length; index++) {
        buildData = productList[productAreas[index]];
        $("#report").append("<tr><td>"+productAreas[index]+"</td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.day6)+" /></td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.day5)+" /></td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.day4)+" /></td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.day3)+" /></td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.day2)+" /></td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.day1)+" /></td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.day0)+" /></td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.weekly)+" /></td>"
        +"<td><img height='25' width='25' src="+getStatusImage(buildData.monthly)+" /></td>"
        +"<td><div class='arrow'></div></td></tr>"
       );

       $("#report").append("<tr class='expandable'><td colspan='11'><div>Currently Failed Components : "+buildData.failed+"</div></td></tr>");

            $(document).ready(function () {
                $("#report tr:odd").addClass("odd");
                $("#report tr:not(.odd)").hide();
                $("#report tr:first-child").show();

                $("#report tr.odd").click(function () {
                    $(this).next("tr").toggle();
                    alert(stringify($(this).next("tr").toggle()));
                    $(this).find(".arrow").toggleClass("up");
                });
        
});
    } 
 
}
getDataFromService();
setTimeout(function(){
    window.location.reload(1);
 }, 60000);







