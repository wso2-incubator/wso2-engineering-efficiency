/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

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

