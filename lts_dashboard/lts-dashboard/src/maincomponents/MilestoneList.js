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
import Typography from 'material-ui/Typography';
import Paper from 'material-ui/Paper';
import Divider from 'material-ui/Divider';
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import MilestoneExpansion from './milestones/MilestoneExpansion.js'

const styles = theme => ({
    root: {
        width: '100%',
    },


    paper: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
        marginTop: theme.spacing.unit * 3,
        width : 1200
    }),
    appBar : {
        paddingBottom:20
    }
});




class MilestoneList extends React.Component {
    state = {
        expanded: null,
    };


    testData = [1,2,3,4,5];

    render() {
        const { classes } = this.props;

        return (
            <div>
                <Paper className={classes.paper} elevation={4}>
                    <div className={classes.appBar}>
                        <AppBar position="static" color="default">
                            <Toolbar >
                                <Typography type="headline" component="h2">
                                    2.0.1
                                </Typography>
                            </Toolbar>
                        </AppBar>
                    </div>
                    <Divider light />
                    <div className={classes.root}>
                        {this.testData.map((value,index)=>(
                                <MilestoneExpansion key={index}/>
                            ))
                        }
                    </div>
                </Paper>
            </div>
        );
    }
}

MilestoneList.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(MilestoneList);