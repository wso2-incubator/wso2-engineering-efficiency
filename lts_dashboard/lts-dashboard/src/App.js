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

import React, {Component} from 'react';
import logo from './img/WSO2_Software_Logo.png'
import PropTypes from 'prop-types';
import {withStyles} from 'material-ui/styles';
import './App.css';
import MilestoneList from './maincomponents/MilestoneList';
import MenuAppBar from './maincomponents/HeaderAppBar'
import axios from "axios/index";
import MilestoneModal from "./maincomponents/MilestoneModal.js"
import LinearProgress from "material-ui/es/Progress/LinearProgress";


const styles = {
    blocks: {
        display: 'inline',
        float: 'left',
        padding: 20
    }


};


class App extends Component {

    modalOpen = (milestoneData) => {
        this.setState({
            milestoneData: milestoneData,
            openModal: true
        });
    };

    constructor(props) {
        super(props);
        this.state = {
            issueList: [],
            milestoneData: {},
            openModal: false,
            loadIssue: false
        };

        this.setIssues = this.setIssues.bind(this);
        this.modalOpen = this.modalOpen.bind(this);

    }

    setIssues(productName, versionName) {
        if (productName !== '' || versionName !== '') {
            let productObject = {};
            productObject["product"] = productName;
            productObject["version"] = versionName;

            if (productName !== '') {
                this.setState({
                    loadIssue: true,
                    openModal: false,
                    issueList: []
                }, () => (
                    axios.post('http://localhost:8080/lts/issues',
                        productObject
                    ).then(
                        (response) => {
                            let datat = response.data;
                            this.setState(
                                {
                                    issueList: datat,
                                    loadIssue: false,
                                    openModal: false,
                                }
                            );
                        }
                    )
                ));
            }
        }
    }


    render() {
        const {classes} = this.props;
        return (
            <div className="App">

                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo"/>
                    <h1 className="App-title">LTS Dashboard</h1>
                </header>

                <MenuAppBar
                    productUpdate={this.setProduct}
                    setissues={this.setIssues}
                />
                <div style={{height: 15}}>
                    {this.state.loadIssue && <LinearProgress />}
                </div>

                <div className={classes.blocks}>
                    <MilestoneList
                        issueList={this.state.issueList}
                        modalLauch={this.modalOpen}
                    />
                </div>

                <div>
                    <MilestoneModal
                        data={this.state.milestoneData}
                        open={this.state.openModal}
                        issueList={this.state.issueList}
                    />
                </div>

            </div>
        );
    }
}

App.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(App);
