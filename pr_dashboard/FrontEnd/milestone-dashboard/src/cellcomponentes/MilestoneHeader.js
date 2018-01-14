import React from 'react';
import {Row, Col,Button,Label} from 'react-bootstrap';
import Completion from './Completion'

class MilestoneHeader extends React.Component{
    constructor(props){
        super(props);
    }



    render(){
        let open = this.props.data["open-issues"];
        let closed = this.props.data["closed-issues"];
        let completion = Math.round(closed/(open+closed)*100);
        return(
            <div>
                  <Row>
                      <Col xs={6} md={4}>
                          <a href={this.props.data["url"]} className="center-text">
                              <Button bsStyle="default" bsSize="large" block><h2>{this.props.data["name"]}</h2></Button>
                          </a>
                      </Col>
                      <Col xs={2} md={2} ><div><h4>Due Date</h4><h2> {this.props.data["due-date"]}</h2></div></Col>
                      <Col xs={2} md={2} >
                          <div>
                              <h4>Features</h4>
                          </div>
                          <div>
                              <h2 style={{display:"inline-block"}}>{this.props.data["open-issues"]}</h2>
                              <h5 style={{display:"inline-block", color:"#9494b8"}}>open</h5>
                              <div style={{width:'10px', display:'inline-block'}}></div>
                              <h2 style={{display:"inline-block"}}>{this.props.data["closed-issues"]}</h2>
                              <h5 style={{display:"inline-block", color:"#9494b8"}}>closed</h5>
                          </div>
                      </Col>

                  </Row>
                <Row>
                    <Col xs={6} md={4}>
                        <Completion percentage={completion}/>
                    </Col>

                </Row>
            </div>

        );
    }
}


export default MilestoneHeader