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
import Modal from 'material-ui/Modal';
import Button from 'material-ui/Button';
import ModalBar from './productlist/ModalBar';
import ProductList from './productlist/ProductList';


function getModalStyle() {

    return {
        position: 'absolute',
        width: 8 * 50,
        top: `10%`,
        left: `30%`,
        border: '1px solid #e5e5e5',
        backgroundColor: '#fff',
        boxShadow: '0 5px 15px rgba(0, 0, 0, .5)'
    };
}


class SimpleModal extends React.Component {
    state = {
        open: false,
    };
    handleOpen = () => {
        this.setState({open: true});
    };
    handleClose = () => {
        this.setState({open: false});
    };

    constructor(props) {
        super(props);
        this.handleOpen = this.handleOpen.bind(this);
    }

    render() {
        return (
            <div>
                <div>
                    <Button raised onClick={this.handleOpen}>Change Product</Button>
                </div>

                <Modal
                    aria-labelledby="simple-modal-title"
                    aria-describedby="simple-modal-description"
                    open={this.state.open}
                    onClose={this.handleClose}
                >
                    <div style={getModalStyle()}>
                        <ModalBar/>
                        <div style={{height: 15}}></div>
                        <ProductList/>
                    </div>
                </Modal>
            </div>
        );
    }
}

export default SimpleModal;