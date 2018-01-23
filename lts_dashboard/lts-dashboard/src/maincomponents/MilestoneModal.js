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
import LinearProgress from "material-ui/es/Progress/LinearProgress";
import axios from "axios/index";
import ListItemText from "material-ui/es/List/ListItemText";
import CircularProgress from "material-ui/es/Progress/CircularProgress";
import ListItemIcon from "material-ui/es/List/ListItemIcon";
import StarIcon from 'material-ui-icons/Star';
import ListItem from "material-ui/es/List/ListItem";


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
        },()=>(
            axios.post('http://localhost:8080/lts/milestone',
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
            completion = Math.round(closedIssues / total*100);
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
                    milestoneIssues.push(issue["url"])
                }
            }
        });

        return milestoneIssues;
    }


    generate(array) {
        return array.map((value, index) =>
            <ListItem>
                <ListItemIcon>
                    <StarIcon />
                </ListItemIcon>
                <ListItemText
                    primary={value}
                    key={index}
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
                            <AppBar position="static" color="default">
                                <Toolbar>
                                    <Typography type="title" color="inherit">
                                        {this.props.data["title"]}
                                    </Typography>
                                    {this.state.progressState && <CircularProgress className={classes.progress} />}
                                </Toolbar>
                            </AppBar>


                            {/*detail bar*/}
                            <Paper className={classes.paper} elevation={6}>
                                <Grid container>
                                    <Grid item xs={12} md={6}>
                                        <Typography type="title" className={classes.title}>
                                            Completion
                                        </Typography>
                                        <LinearProgress mode="determinate" value={this.calculateCompletion()}/>
                                        <Typography type="title" className={classes.subtitle}>
                                            Closed issues : {this.props.data["closed_issues"]} from
                                            {parseInt(this.props.data["closed_issues"]) + parseInt(this.props.data["open_issues"])}
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        <Typography type="title" className={classes.title}>
                                            Due on : {this.props.data["due_on"]}
                                        </Typography>
                                    </Grid>
                                </Grid>
                            </Paper>


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