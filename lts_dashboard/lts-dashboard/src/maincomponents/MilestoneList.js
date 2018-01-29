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
import {withStyles} from 'material-ui/styles';
import Paper from 'material-ui/Paper';
import Divider from 'material-ui/Divider';
import {FilteringState, IntegratedFiltering, IntegratedSorting, SortingState,} from "@devexpress/dx-react-grid";
import {Grid, TableFilterRow, TableHeaderRow} from '@devexpress/dx-react-grid-material-ui'
import MilestoneCheckButton from "./milestones/MilestoneButton.js"
import {
    TableColumnResizing,
    VirtualTable
} from "@devexpress/dx-react-grid-material-ui/dist/dx-react-grid-material-ui.cjs";

const styles = theme => ({
    root: {
        width: '100%',
    },

    paper: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
        marginTop: theme.spacing.unit * 3,
        width: `97%`,
        marginRight: `5%`,
        height: 700
    }),
    appBar: {
        paddingBottom: 20
    }
});



function getColumnWidths() {
    let screenWidth = window.innerWidth;
    let divPaperSize = Math.round(screenWidth / 100 * 92);
    let col1Size = Math.round(divPaperSize / 100 * 60);
    let col2Size = Math.round(divPaperSize / 100 * 20);
    let col3Size = Math.round(divPaperSize / 100 * 20);
    return [col1Size, col2Size, col3Size]
}

class MilestoneList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            issueList: [],
            displayIssueList: [],
            milestoneList : {}
        };

        this.modalOpen = this.modalOpen.bind(this);
    }


    componentWillUpdate(nextProps, nextState) {
        if (nextProps.issueList !== this.props.issueList) {
            this.setState({
                issueList: nextProps.issueList,
                displayIssueList: this.processIssueList(nextProps.issueList),
            })
        }
    }


    modalOpen(data) {
        this.props.modalLauch(data);
    };

    processIssueList(issueList) {
        let displayArray = [];
        let modalOpenUp = this.modalOpen;
        issueList.forEach(function (element) {
                let issueTitle = <a href={element["html_url"]}>{element["issue_title"]}</a>;
                let milestoneDueOn = " N/A";
                let milestoneTitle = " N/A";
                if (element["milestone"] != null) {
                    if (element["milestone"]["due_on"] !== "null") {
                        milestoneDueOn = element["milestone"]["due_on"];
                    }
                    // TODO - make a list of milestones and add that to cell info
                    // TODO - or pass total object and filter it
                    // milestoneTitle = element["milestone"]["title"];
                    milestoneTitle = <MilestoneCheckButton
                        data={element["milestone"]}
                        modalLauch={modalOpenUp}
                    />
                }

                let issue = {
                    title: issueTitle,
                    due_on: milestoneDueOn,
                    milestone: milestoneTitle,

                };
                displayArray.push(issue);
            }
        );

        return displayArray;

    }



    render() {
        const {classes} = this.props;
        let columnSizes = getColumnWidths();

        return (
            <Paper className={classes.paper} elevation={4}>
                <Divider light/>
                <div className={classes.root}>
                    <Grid
                        rows={this.state.displayIssueList}

                        columns={[
                            {name: 'title', title: 'Feature'},
                            {name: 'due_on', title: 'Release Date'},
                            {name: 'milestone', title: 'Feature included milestone'}
                        ]}>

                        <FilteringState defaultFilters={[]}/>
                        <IntegratedFiltering/>

                        <SortingState
                            defaultSorting={[{columnName: 'milestone', direction: 'desc'}]}
                        />
                        <IntegratedSorting/>
                        <VirtualTable height={700}/>
                        <TableColumnResizing defaultColumnWidths={[
                            {columnName: 'title', width: columnSizes[0]},
                            {columnName: 'due_on', width: columnSizes[1]},
                            {columnName: 'milestone', width: columnSizes[2]},
                        ]}/>
                        <TableHeaderRow showSortingControls/>
                        <TableFilterRow/>

                    </Grid>
                </div>
            </Paper>

        );
    }
}

MilestoneList.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(MilestoneList);