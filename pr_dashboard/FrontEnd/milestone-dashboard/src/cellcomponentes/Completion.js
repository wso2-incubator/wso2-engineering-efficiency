import React from "react";
import {ProgressBar} from 'react-bootstrap'

class Completion extends React.Component{
    constructor(props){
        super(props)
    }

    render(){
        let color = "success";
        let percentage = this.props.percentage;
        if(this.props.percentage<33){
            color = "danger";
        } else  if(this.props.percentage <66) {
            color = "warning";
        } else if(this.props.percentage <100) {
          color = "success";
        } else if(this.props.percentage == 100){
            color = "info";
        }else {
            percentage =0;
        }
        return(
            <ProgressBar bsStyle={color} label={percentage} now={percentage}/>
        );
    }
}

export default Completion;