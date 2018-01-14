import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import Button from 'material-ui/Button';



const styles=theme => ( {
    button: {
        margin: theme.spacing.unit,
    },
    popover: {
        pointerEvents: 'none',
    },
    popperClose: {
        pointerEvents: 'none',
    },
    paper: {
        padding: theme.spacing.unit,
    },
});


class WeekButtons extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            color : this.setBackColor(),
            weekNumber : this.props.weekNumber,
        };
        this.setBackColor();
    }

    handleClick = () => {
        this.props.setDate(this.props.startDate,this.props.endDate);
    };

    setBackColor(){
        if(this.props.danger){
            return "accent"
        }
        return "primary"
    }


    render() {
        const {classes} = this.props;

        return (
            <Button fab mini color={this.state.color} aria-label="add" className={classes.button} onClick={this.handleClick}>
                {this.state.weekNumber}
            </Button>

        );
    }
}

WeekButtons.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(WeekButtons);