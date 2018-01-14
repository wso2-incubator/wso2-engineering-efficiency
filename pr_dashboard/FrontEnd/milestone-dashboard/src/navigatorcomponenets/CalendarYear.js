import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import List, { ListItem, ListItemText } from 'material-ui/List';
import Divider from 'material-ui/Divider';
import Collapse from 'material-ui/transitions/Collapse';
import ExpandLess from 'material-ui-icons/ExpandLess';
import ExpandMore from 'material-ui-icons/ExpandMore';
import CalendarMonth from './CalendarMonth';
import Chip from 'material-ui/Chip';

const styles=theme => ( {
    nested: {
        paddingLeft: theme.spacing.unit * 4,
    },
    listBackColorNormal:{
        backgroundColor: "#D4EDF4"
    },
    listBackColorSelected:{
        backgroundColor: "#A7E0F0"
    },
    chip: {
        backgroundColor: "#f1656c",
    }

});


class CalendarYear extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            open: false,
            year : this.props.year,
            months: this.getMonths(this.props.yearData),
            monthData: this.props.yearData
        };
        this.setCheckDate = this.setCheckDate.bind(this);

    }

    handleClick = () => {
        this.setState({ open: !this.state.open });
    };




    getMonths(yearData){
        if(Object.keys(yearData).length>0) {
            return Object.keys(yearData);
        }
        else {
            return [];
        }
    }



    setCheckDate(sDate,eDate){
        this.props.setDate(sDate,eDate);
    }


    renderMonths(){
        if(this.state.months.length>0) {
            return this.state.months.map((month,index) => (
                <CalendarMonth
                    month={month}
                    key={index}
                    year = {this.props.year}
                    monthData={this.state.monthData[month]}
                    setDate = {this.setCheckDate}

                />
            ))
        }
    }

    checkDanger(){
        let test = false;
        for(let i=0;i<this.state.months.length;i++){
            let weekData = this.state.monthData[this.state.months[i]];
            for(let i=0;i<weekData.length;i++){
                if(weekData[i]["danger"]){
                    test = true;
                    return test;
                }
            }
        }
        return test;
    }


    render() {
        const {classes} = this.props;
        return (
            <div>
                <ListItem button className={classes.listBackColorNormal} onClick={this.handleClick}>
                    <ListItemText inset primary={this.props.year}/>
                    {this.checkDanger()?<div><Chip label="In Progress" className={classes.chip}/></div>:<div></div>}
                    {this.state.open ? <ExpandLess/> : <ExpandMore/>}
                </ListItem>
                <Divider/>
                <Collapse component="li" in={this.state.open} timeout="auto" unmountOnExit>
                    <List disablePadding>
                        {
                           this.renderMonths()
                        }
                    </List>
                </Collapse>
                <Divider/>
            </div>
        );
    }
}


CalendarYear.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(CalendarYear);