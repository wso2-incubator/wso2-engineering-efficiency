
import React from 'react';
import {Label,Col,Row} from 'react-bootstrap'

class PRView extends React.Component{
    constructor(props){
        super(props)

    }

    checkColor(state) {
        let color = null;
        if(state==="closed"){
            color = "danger";
        }
        else if (state === "open"){
            color = 'success';
        }
        return color;
    }


    render(){
        let color = this.checkColor(this.props.data["status"]);
        return (
            <div className="pr-view">
                <Row>
                    <Col xs={12} md={8}>
                        <a href={this.props.data["pr-link"]}>
                            <div className="over">
                            {this.props.data["title"]}
                            </div>
                        </a>
                    </Col>
                    <Col xs={6} md={4}>
                        <div className="get_right">
                        <Label bsStyle={color}>
                            {this.props.data["status"]}
                        </Label>
                        </div>
                    </Col>
                </Row>

            </div>
        );
    }
}

export default PRView;

