import React from "react";
import {Label} from 'react-bootstrap'

class CloseLabel extends React.Component{
    constructor(props){
        super(props)
    }

    checkColor(state) {
        let color = null;
        if(state==="open"){
            color = "success";
        }
        else if (state === "closed"){
            color = "danger";
        }
        return color;
    }

    render(){
        let color = this.checkColor(this.props.data);
        return (
            <Label bsStyle={color}>
                {this.props.data}
            </Label>
        );
    }
}

export default CloseLabel;