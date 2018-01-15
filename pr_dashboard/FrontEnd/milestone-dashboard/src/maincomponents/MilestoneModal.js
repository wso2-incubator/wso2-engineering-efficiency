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
import MilestoneTable from './MilestoneTable.js'
import { Button ,Modal,} from 'react-bootstrap';
import MilestoneHeader from '../cellcomponentes/MilestoneHeader.js'



class MilestoneModal extends React.Component{
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Modal {...this.props} bsSize="lg" aria-labelledby="contained-modal-title-sm">
                <Modal.Header closeButton>
                    <Modal.Title id="contained-modal-title-lg">
                        <MilestoneHeader data={this.props.milestone}/>
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <MilestoneTable  array={this.props.milestone["issues"]}/>
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.props.onHide}>Close</Button>
                </Modal.Footer>
            </Modal>
        );
    }
}

export default MilestoneModal;

