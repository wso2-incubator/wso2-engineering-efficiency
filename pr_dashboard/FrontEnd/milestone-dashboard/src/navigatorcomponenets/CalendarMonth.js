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
import List, { ListItem, ListItemText } from 'material-ui/List';
import Divider from 'material-ui/Divider';
import Collapse from 'material-ui/transitions/Collapse';
import ExpandLess from 'material-ui-icons/ExpandLess';
import ExpandMore from 'material-ui-icons/ExpandMore';
import WeekButtons from './WeekButtons';
import {generateMonthName} from "../support/datepickers";
import Chip from 'material-ui/Chip';

const styles=theme => ( {
    nestedWeeks: {
        paddingLeft: theme.spacing.unit * 5,
    },
    listBackColorNormal:{
        paddingLeft: theme.spacing.unit * 4,
        backgroundColor: "#EAF5CD",
    },
    listBackColorSelected:{
        paddingLeft: theme.spacing.unit * 4,
        backgroundColor: "#D7F1AB"
    },
    chip: {
        backgroundColor: "#f1656c",
    }
});


class CalendarMonth extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            open: false,
            weeks : this.props.monthData,
        };
        this.setCheckDate = this.setCheckDate.bind(this);
    }

    handleClick = () => {
        this.setState({ open: !this.state.open });
    };





    renderWeek(){


        let weeks = this.state.weeks.filter(function (a) {
            return !this[a.weekNumber] && (this[a.weekNumber] = true);
        }, Object.create(null));


        weeks.sort(function(a, b){
            var keyA = new Date(a.weekNumber),
                keyB = new Date(b.weekNumber);
            // Compare the 2 dates
            if(keyA < keyB) return -1;
            if(keyA > keyB) return 1;
            return 0;
        });

        return weeks.map((week,index)=>(
            <WeekButtons
                weekNumber={week["weekNumber"]}
                key={index}
                startDate ={week["startDate"]}
                endDate = {week["endDate"]}
                setDate = {this.setCheckDate}
                danger = {week["danger"]}
            />
        ))
    }


    setCheckDate(sDate,eDate){
        this.props.setDate(sDate,eDate);
    }


    checkDanger(){
        let test = false;
        for(let i=0;i<this.state.weeks.length;i++){
            if(this.state.weeks[i]["danger"]){
                test = true;
                return test;
            }
        }
        return test;
    }

    render() {
        const {classes} = this.props;
        return (
            <div>
                <ListItem button onClick={this.handleClick} className={classes.listBackColorNormal}>
                    <ListItemText inset primary={generateMonthName(this.props.month)}/>
                    {this.checkDanger()?<div><Chip label="In Progress" className={classes.chip}/></div>:<div></div>}
                    {this.state.open ? <ExpandLess/> : <ExpandMore/>}

                </ListItem>
                <Divider/>
                <Collapse component="li" in={this.state.open} timeout="auto" unmountOnExit>
                    <List disablePadding>
                        <ListItem className={classes.nestedWeeks}>
                        {

                            this.renderWeek()
                        }
                        </ListItem>
                        <Divider/>
                    </List>
                </Collapse>
            </div>
        );
    }
}

CalendarMonth.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(CalendarMonth);