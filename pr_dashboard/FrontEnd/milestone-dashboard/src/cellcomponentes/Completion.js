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

import React from "react";
import {ProgressBar} from 'react-bootstrap'

class Completion extends React.Component {
    constructor(props) {
        super(props)
    }

    render() {
        let color = "success";
        let percentage = this.props.percentage;
        if (this.props.percentage < 33) {
            color = "danger";
        } else if (this.props.percentage < 66) {
            color = "warning";
        } else if (this.props.percentage < 100) {
            color = "success";
        } else if (this.props.percentage == 100) {
            color = "info";
        } else {
            percentage = 0;
        }
        return (
            <ProgressBar bsStyle={color} label={percentage} now={percentage}/>
        );
    }
}

export default Completion;