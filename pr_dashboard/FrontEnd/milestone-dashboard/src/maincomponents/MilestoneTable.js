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
            data : this.makeData(this.props.array)
        })
    }


    render() {
        const { data } = this.state;
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
                            Cell : row => (
                                <a href={row["original"]["feature_name"]["url"]}>
                                    {row["original"]["feature_name"]["title"]}
                                </a>
                            ),
                            filterMethod: (filter, rows) =>
                                matchSorter(rows, filter.value, { keys: ["feature_name.title"] }),
                            filterAll: true
                        },
                        {
                            Header: "State",
                            id : "state",
                            accessor : "state",
                            maxWidth : 100,
                            Cell : row => (
                                <CloseLabel data={row["original"]["state"]}/>
                            ),
                            filterMethod: (filter,rows) =>{
                                if(filter.value==="all"){
                                    return true;
                                }
                                if(filter.value==="open"){
                                    return rows[filter.id]==="open";
                                }
                                else if (filter.value === "closed"){
                                    return rows[filter.id]==="closed";
                                }
                            },
                            Filter: ({ filter, onChange }) =>
                                <select
                                    onChange={event => onChange(event.target.value)}
                                    style={{ width: "100%" }}
                                    value={filter ? filter.value : "all"}
                                >
                                    <option value="all">all</option>
                                    <option value="open">open</option>
                                    <option value="closed">closed</option>
                                </select>
                        },
                        {
                            Header: "Source code related PRs",
                            accessor : "code_pr",
                            filterable: false
                        },
                        {
                            Header: "Doc related PRs",
                            accessor : "doc_pr",
                            filterable : false
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