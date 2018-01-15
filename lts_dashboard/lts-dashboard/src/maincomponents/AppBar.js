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
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import Typography from 'material-ui/Typography';
import SimpleModal from './ProductSelectModal';
import {Row,Col} from 'react-bootstrap'


const styles = {
    root: {
        width: '100%',
    },
    header: {
        paddingRight:20
    }
};


function HeaderAppBar(props) {
    const { classes } = props;
    return (
        <div className={classes.root}>
            <AppBar position="static" color="default">
                <Toolbar>
                    <div className={classes.header}>
                        <Typography type="title" color="inherit">
                            API Manager
                        </Typography>
                    </div>
                    <SimpleModal/>
                </Toolbar>
            </AppBar>
            <Row>
                <Col xs={3}>

                </Col>
                <Col xs={9}>
                </Col>
            </Row>

        </div>
    );
}

HeaderAppBar.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(HeaderAppBar);