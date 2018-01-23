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

import React from "react";
import {Milestone} from "../support/Utils";
import matchSorter from 'match-sorter'
// Import React Table
import ReactTable from "react-table";
import MilestoneModal from "./MilestoneModal";
import {getDate} from "../support/datepickers";

class DataTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            data: [],
            pure_data: this.props.pureData,
            modal_data: [],
            start_date: this.props.changeDate.startDate,
            end_date: this.props.changeDate.endDate
        };
        this.hideModal = this.hideModal.bind(this);
        this.setFilterDates = this.setFilterDates.bind(this);


    }

    makeData(data) {
        return data.map(d => {
            return {
                ...Milestone(d)
            };
        });

    }


    setMilestone() {

        this.setState(
            {
                data: this.makeData(this.state.pure_data)
            }
        );
        this.setFilterDates(this.props.changeDate);


    }


    getInitialState() {
        return {show: false};
    }

    showModal() {
        this.setState({show: true});
    }

    hideModal() {
        this.setState({show: false});
    }

    componentDidMount() {

    }


    componentWillUpdate(nextProps, nextState) {
        if (nextProps.pureData != this.state.pure_data) {
            this.state.pure_data = nextProps.pureData;
            this.setMilestone();
        }
        if (nextProps.changeDate != this.props.changeDate) {
            this.setFilterDates(nextProps.changeDate);
        }
    }


    setFilterDates(check_date) {
        this.setState(
            {
                start_date: check_date.startDate,
                end_date: check_date.endDate
            },
            () => (
                this.createFilteredMilestones()
            )
        );

    }


    findSingleMilestoneData(product, milestone) {
        let ite = this.state.pure_data.length;
        let pureFile = this.state.pure_data;
        for (let i = 0; i < ite; i++) {
            if (pureFile[i]["product-name"] === product && pureFile[i]["name"] === milestone) {
                this.setState({modal_data: pureFile[i]});
            }
        }
    }


    createFilteredMilestones() {
        let ite = this.state.pure_data.length;
        let pureFile = this.state.pure_data;
        let changeData = [];
        let count = 0;
        for (let i = 0; i < ite; i++) {
            let due_date = getDate(pureFile[i]["due-date"]);
            due_date.setHours(0, 0, 0, 0);
            if (due_date >= this.state.start_date && due_date <= this.state.end_date) {
                changeData[count] = pureFile[i];
                count++;
            }
        }

        this.setState(
            {
                data: this.makeData(changeData)
            }
        )
    }


    render() {
        const {data} = this.state;
        return (
            <div className="Table-m">
                <ReactTable
                    getTrProps={(state, rowInfo, column, instance) => ({
                        onClick: e => {
                            this.findSingleMilestoneData(rowInfo["original"]["product_name"], rowInfo["original"]["milestone"]);
                            this.showModal();
                        }
                    })}
                    data={data}
                    filterable
                    defaultFilterMethod={(filter, row) =>
                        String(row[filter.id]) === filter.value}


                    columns={[
                        {
                            Header: "Due Date",
                            maxWidth: 150,
                            id: "due_date",
                            accessor: d => d.due_date,
                            filterMethod: (filter, row) => {
                                let cellDate = getDate(row[filter.id]);
                            },
                            filterAll: true
                        },
                        {
                            Header: "Milestone name",
                            id: "milestone",
                            accessor: d => d.milestone,
                            filterMethod: (filter, rows) =>
                                matchSorter(rows, filter.value, {keys: ["milestone"]}),
                            filterAll: true
                        },
                        {
                            Header: "Product",
                            id: "product_name",
                            accessor: d => d.product_name,
                            filterMethod: (filter, rows) =>
                                matchSorter(rows, filter.value, {keys: ["product_name"]}),
                            filterAll: true
                        },
                        {
                            Header: "Version",
                            id: "version",
                            maxWidth: 150,
                            accessor: d => d.version,
                            filterMethod: (filter, rows) =>
                                matchSorter(rows, filter.value, {keys: ["version"]}),
                            filterAll: true
                        },
                        {
                            Header: "Completion",
                            accessor: "completion",
                            id: "completion",
                            sortMethod: (a, b) => {
                                if (a.props.percentage === b.props.percentage) {
                                    return 0;
                                }
                                else if (a.props.percentage > b.props.percentage) {
                                    return 1;
                                }
                                else {
                                    return -1;
                                }
                            },
                            filterMethod: (filter, row) => {
                                if (filter.value <= row[filter.id].props.percentage) {
                                    return true;
                                }
                            }
                        },
                        {
                            Header: "Number of Features",
                            accessor: "feature_count",
                            id: "feature_count",
                            maxWidth: 150,
                            filterMethod: (filter, row) => {
                                if (filter.value <= row[filter.id]) {
                                    return true;
                                }
                            }
                        },
                        {
                            Header: "Open PRs",
                            accessor: "pr_count",
                            id: "pr_count",
                            maxWidth: 120,
                            filterMethod: (filter, row) => {
                                if (filter.value <= row[filter.id]) {
                                    return true;
                                }
                            }
                        },
                        {
                            Header: "Doc Open PRs",
                            accessor: "doc_pr_count",
                            id: "doc_pr_count",
                            maxWidth: 120,
                            filterMethod: (filter, row) => {
                                if (filter.value <= row[filter.id]) {
                                    return true;
                                }
                            }
                        }

                    ]}
                    // defaultPageSize={20}
                    className="-striped -highlight"
                />
                <MilestoneModal  {...this.props}
                                 show={this.state.show}
                                 onHide={this.hideModal}
                                 dialogClassName="custom-modal"
                                 milestone={this.state.modal_data}
                />
            </div>
        );
    }
}


export default DataTable;