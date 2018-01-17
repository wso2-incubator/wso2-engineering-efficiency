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
import AppBar from './maincomponents/AppBar';
import VersionNavigator from './maincomponents/VersionNavigator';
import MilestoneList from './maincomponents/MilestoneList';

const styles = theme => ({
    blocks: {
        display: 'inline',
        float: 'left',
        padding: 20
    }


});

class App extends Component {
    render() {
        const {classes} = this.props;
        return (
            <div className="App">

                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo"/>
                    <h1 className="App-title">LTS Dashboard</h1>
                </header>

                <AppBar/>
                <div style={{height: 15}}></div>
                <div>
                    <div className={classes.blocks}>
                        <VersionNavigator/>
                    </div>
                    <div className={classes.blocks}>
                        <MilestoneList/>
                    </div>
                </div>
            </div>
        );
    }
}

App.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(App);
