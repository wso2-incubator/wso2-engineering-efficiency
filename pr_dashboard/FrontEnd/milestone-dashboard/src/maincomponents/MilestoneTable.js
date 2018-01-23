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
import ReactTable from "react-table";
import {IssueData} from "../support/Utils";
import matchSorter from "match-sorter";
import CloseLabel from "../cellcomponentes/CloseLabel";


class MilestoneTable extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            data: [],
        };


    }

    makeData(data) {
        return data.map(d => {
            return {
                ...IssueData(d)
            };
        });
    }


    componentDidMount() {
        this.setState({
            data: this.makeData(this.props.array)
        })
    }


    render() {
        const {data} = this.state;
        return (
            <div className="Table-m">
                <ReactTable
                    data={data}
                    filterable
                    defaultFilterMethod={(filter, row) =>
                        String(row[filter.id]) === filter.value}
                    columns={[
                        {
                            Header: "Feature Name",
                            accessor: "feature_name",
                            Cell: row => (
                                <a href={row["original"]["feature_name"]["url"]}>
                                    {row["original"]["feature_name"]["title"]}
                                </a>
                            ),
                            filterMethod: (filter, rows) =>
                                matchSorter(rows, filter.value, {keys: ["feature_name.title"]}),
                            filterAll: true
                        },
                        {
                            Header: "State",
                            id: "state",
                            accessor: "state",
                            maxWidth: 100,
                            Cell: row => (
                                <CloseLabel data={row["original"]["state"]}/>
                            ),
                            filterMethod: (filter, rows) => {
                                if (filter.value === "all") {
                                    return true;
                                }
                                if (filter.value === "open") {
                                    return rows[filter.id] === "open";
                                }
                                else if (filter.value === "closed") {
                                    return rows[filter.id] === "closed";
                                }
                            },
                            Filter: ({filter, onChange}) =>
                                <select
                                    onChange={event => onChange(event.target.value)}
                                    style={{width: "100%"}}
                                    value={filter ? filter.value : "all"}
                                >
                                    <option value="all">all</option>
                                    <option value="open">open</option>
                                    <option value="closed">closed</option>
                                </select>
                        },
                        {
                            Header: "Source code related PRs",
                            accessor: "code_pr",
                            filterable: false
                        },
                        {
                            Header: "Doc related PRs",
                            accessor: "doc_pr",
                            filterable: false
                        }
                    ]}
                    defaultPageSize={20}
                    className="-striped -highlight"
                />
            </div>
        );
    }
}

export default MilestoneTable;