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
import Grid from "material-ui/es/Grid/Grid";
import axios from "axios/index";
import ListItemText from "material-ui/es/List/ListItemText";
import CircularProgress from "material-ui/es/Progress/CircularProgress";
import ListItemIcon from "material-ui/es/List/ListItemIcon";
import StarIcon from 'material-ui-icons/Star';
import ListItem from "material-ui/es/List/ListItem";
import purple from "material-ui/colors/purple";


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

class MilestoneModal extends React.Component {
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
            leftData: ["test 1", "test 2", "test 3"],
            rightData: ["test 1", "test 2", "test 3"],
            progressState: false
        };

    }

    componentWillUpdate(nextProps, nextState) {
        if (nextProps.data !== this.props.data) {
            this.setState({
                    data: nextProps.data,
                    leftData: [],
                    rightData: []
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
                axios.post('http://10.100.5.173:8080/lts/milestone',
                    data
                ).then(
                    (response) => {
                        let datat = response.data;
                        this.makeTwoWayList(datat);
                    }
                )
            )
        );
    }


    // make two way feature List to display
    makeTwoWayList(featureArray) {
        let arraySize = featureArray.length;
        let leftArray = [];
        let rightArray = [];
        for (let i = 0; i < arraySize; i++) {
            if (i % 2 === 0) {
                leftArray.push(featureArray[i])
            }
            else {
                rightArray.push(featureArray[i])
            }
        }

        this.setState({
            leftData: leftArray,
            rightData: rightArray,
            progressState: false
        })
    }

    // calculate the completion of the milestone
    calculateCompletion() {
        let openIssues = parseInt(this.props.data["open_issues"]);
        let closedIssues = parseInt(this.props.data["closed_issues"]);
        let total = openIssues + closedIssues;
        let completion = 0;
        if (total > 0) {
            completion = Math.round(closedIssues / total * 100);
        }

        return completion;
    }

    // create issue url list belong to the milestone
    createIssueListofMilestone() {
        let milestoneIssues = [];
        let milestoneTitle = this.props.data["title"];
        this.props.issueList.forEach(function (issue) {
            if (issue["milestone"] != null) {
                if (issue["milestone"]["title"] === milestoneTitle) {
                    let object = {
                        url: issue["url"],
                        html_url: issue["html_url"],
                        title: issue["issue_title"],
                    };
                    milestoneIssues.push(object)
                }
            }
        });

        return milestoneIssues;
    }


    generate(array) {

        return array.map((value, index) =>
            <ListItem button key={index} onClick={() => window.open(value["html_url"].replace("\"", ""))}>
                <ListItemIcon>
                    <StarIcon/>
                </ListItemIcon>
                <ListItemText
                    primary={value["feature"]}
                    secondary={"From issue :" + value["title"]}
                />
            </ListItem>
        );
    }


    render() {
        const {classes} = this.props;
        return (
            <div>
                <Modal
                    aria-labelledby="simple-modal-title"
                    aria-describedby="simple-modal-description"
                    open={this.state.open}
                    onClose={this.handleClose}
                >
                    <div style={getModalStyle()}>
                        <div>
                            {/*top titile bar*/}
                            <AppBar position="static" color="primary">
                                <Toolbar>
                                    <Typography type="title" color="inherit" className={classes.heading}>
                                        Features of Milestone
                                    </Typography>
                                    <Typography type="title" color="inherit">
                                        <a onClick={() => window.open(this.props.data["html_url"], '_blank')}>
                                            {this.props.data["title"]}
                                        </a>
                                    </Typography>
                                    {this.state.progressState && <CircularProgress
                                        style={{color: purple[500]}}
                                        className={classes.progress}/>}
                                </Toolbar>
                            </AppBar>

                            {/*feature List*/}
                            <Paper className={classes.paper} elevation={4}>
                                <Grid container>
                                    <Grid item xs={12} md={6}>
                                        {
                                            this.generate(this.state.leftData)
                                        }
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        {
                                            this.generate(this.state.rightData)
                                        }
                                    </Grid>
                                </Grid>
                            </Paper>
                        </div>

                    </div>
                </Modal>
            </div>
        );
    }
}

MilestoneModal.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(MilestoneModal);