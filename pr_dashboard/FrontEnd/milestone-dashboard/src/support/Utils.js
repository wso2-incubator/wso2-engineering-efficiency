import React from "react";
import PRView from "../cellcomponentes/PRView.js"
import CloseLabel from "../cellcomponentes/CloseLabel.js";
import Completion from "../cellcomponentes/Completion.js"



export const Milestone = data => {
    let f_count = data["open-issues"]+data["closed-issues"];
    let completeness =calculateCompleteness(data["closed-issues"],f_count);
    let prdata = calculateOpenPRs(data["issues"]);
    let codeOpenPr = prdata[0];
    let docOpenPr = prdata[1];
    let completion = <Completion percentage={completeness}/>
    return {
        due_date: data["due-date"],
        product_name: data["product-name"],
        version: data["version"],
        milestone: data["name"],
        feature_count: f_count,
        pr_count: codeOpenPr,
        doc_pr_count:docOpenPr,
        completion : completion,
    }
}


export const IssueData = data => {
    let prList = makePRlist(data["pr-list"]);
    let state = <CloseLabel data={data["status"]}/>;
    let titleLink ={
        url : data["url"],
        title : data["title"]
    }
    return {
        feature_name : titleLink,
        state : data["status"],
        code_pr : prList[0],
        doc_pr :prList[1]
    }
}




function calculateOpenPRs(data){
    let issuesLength = data.length;
    let codeOpenPr = 0;
    let docOpenPr = 0;
    for(let i=0;i<issuesLength;i++){
        let prs = data[i]["pr-list"];
        for(let j=0;j<prs.length;j++){
            if(prs[j]["status"]==="open"){
                if(prs[j]["isCodePR"]){
                    codeOpenPr = codeOpenPr + 1;
                }
                else {
                    docOpenPr = docOpenPr + 1;
                }
            }
        }
    }
    return [codeOpenPr,docOpenPr];
}



function makePRlist(data){
    let codePr = [];
    let docPr = [];

    for (let i = 0;i<data.length;i++){
        if(data[i]["isCodePR"]){
            codePr.push(<PRView data={data[i]}/>);
        }
        else {
            docPr.push(<PRView data={data[i]}/>);
        }
    }
    return [codePr,docPr];
}

function calculateCompleteness(closed,all){
    let percentage = 0;
    if(all>0){
        percentage = Math.round((closed/all)*100);
    }
    return percentage;
}


export function isMilestoneCompletedinRange(date,closed,open){
    let completion = calculateCompleteness(closed,closed+open);
    let today = new Date();
    if(date<today && completion<100){
        return true;
    }
    today.setMonth(today.getMonth()+6);
    if(date>today){
        return true;
    }

    return false;
}