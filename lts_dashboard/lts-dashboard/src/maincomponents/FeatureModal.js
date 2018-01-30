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
import Typography from 'material-ui/Typography';
import Modal from 'material-ui/Modal';
import AppBar from "material-ui/es/AppBar/AppBar";
import Toolbar from "material-ui/es/Toolbar/Toolbar";
import Paper from "material-ui/es/Paper/Paper";
import PropTypes from "prop-types";
import {withStyles} from "material-ui/styles/index";
import axios from "axios/index";
import CircularProgress from "material-ui/es/Progress/CircularProgress";
import ExpansionSummary from './milestones/ExpansionSummary.js';
import Dialog from "material-ui/es/Dialog/Dialog";
import Slide from "material-ui/es/transitions/Slide";
import IconButton from "material-ui/es/IconButton/IconButton";
import CloseIcon from 'material-ui-icons/Close';
import purple from 'material-ui/colors/purple';


const styles = theme => ({
    paper: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
        marginTop: theme.spacing.unit * 3,
    }),
    subtitle: {
        marginBottom: 16,
        fontSize: 14,
        color: theme.palette.text.secondary,
    },
    progress: {
        margin: `0 ${theme.spacing.unit * 2}px`,
    },
    heading: {
        paddingRight: 16
    },
    marketing: {
        paddingLeft: 20,
        fontSize: 16,
    }
});

function getModalStyle() {

    return {
        position: 'absolute',
        width: `80%`,
        top: `10%`,
        left: `10%`,
        border: '1px solid #e5e5e5',
        backgroundColor: '#fff',
        boxShadow: '0 5px 15px rgba(0, 0, 0, .5)',

    };
}

function transition(props) {
    return <Slide direction="up" {...props} />;
}

class FeatureModal extends React.Component {
    handleOpen = () => {
        this.setState({open: true});
    };
    handleClose = () => {
        this.setState({open: false});
    };

    constructor(props) {
        super(props);
        this.state = {
            open: false,
            data: {},
            featureData: [],
            progressState: false
        };

    }

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.versionData !== this.props.versionData) {
            this.setState({
                    data: nextProps.data,
                    featureData: [],
                },
                () => (
                    this.fetchMilestoneFeatures(this.createIssueListofMilestone())
                ))
        }
        if (nextProps.open !== this.state.open) {
            this.setState({
                open: true
            })
        }
    }

    // fetch milestone features
    fetchMilestoneFeatures(data) {
        this.setState({
                progressState: true
            }, () => (
                axios.post('http://10.100.5.173:8080/lts/features',
                    data
                ).then(
                    (response) => {
                        let datat = response.data;
                        this.setState({
                            featureData: datat,
                            progressState: false
                        })
                    }
                )
            )
        );
    }




    // create issue url list belong to the milestone
    createIssueListofMilestone() {
        let milestoneIssues = [];
        this.props.issueList.forEach(function (issue) {

            let object = {
                url: issue["url"],
                html_url: issue["html_url"],
                title: issue["issue_title"],
            };
            milestoneIssues.push(object)

        });

        return milestoneIssues;
    }


    generate(array) {
        return array.map((value, index) =>
            <ExpansionSummary key={index} data={value}/>
        );
    }



    render() {
        const {classes} = this.props;
        return (
            <div>
                <Dialog
                    fullScreen
                    transition={transition}
                    open={this.state.open}
                    onClose={this.handleClose}
                >
                    {/*<div style={getModalStyle()}>*/}
                    <div>
                        <div>
                            {/*top titile bar*/}
                            <AppBar position="static" color="primary">
                                <Toolbar>
                                    <IconButton color="inherit" onClick={this.handleClose} aria-label="Close">
                                        <CloseIcon />
                                    </IconButton>
                                    <Typography type="title" color="inherit">
                                        {this.props.versionData["product"] + " : " + this.props.versionData["version"]}
                                    </Typography>
                                    <Typography type="title" color="inherit" className={classes.marketing}>
                                        Marketing Messages
                                    </Typography>
                                    {this.state.progressState && <CircularProgress
                                        style={{ color: purple[500] }}
                                        className={classes.progress}/>}
                                </Toolbar>
                            </AppBar>

                            {/*feature List*/}
                            <Paper className={classes.paper} elevation={4}>
                               <div>
                                   {this.generate(this.state.featureData)}
                               </div>
                            </Paper>
                        </div>

                    </div>
                </Dialog>
            </div>
        );
    }
}

FeatureModal.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(FeatureModal);