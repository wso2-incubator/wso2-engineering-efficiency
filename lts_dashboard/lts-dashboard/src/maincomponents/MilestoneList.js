import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import Typography from 'material-ui/Typography';
import Paper from 'material-ui/Paper';
import Divider from 'material-ui/Divider';
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import MilestoneExpansion from './milestones/MilestoneExpansion.js'

const styles = theme => ({
    root: {
        width: '100%',
    },


    paper: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
        marginTop: theme.spacing.unit * 3,
        width : 1200
    }),
    appBar : {
        paddingBottom:20
    }
});




class MilestoneList extends React.Component {
    state = {
        expanded: null,
    };


    testData = [1,2,3,4,5];

    render() {
        const { classes } = this.props;

        return (
            <div>
                <Paper className={classes.paper} elevation={4}>
                    <div className={classes.appBar}>
                        <AppBar position="static" color="default">
                            <Toolbar >
                                <Typography type="headline" component="h2">
                                    2.0.1
                                </Typography>
                            </Toolbar>
                        </AppBar>
                    </div>
                    <Divider light />
                    <div className={classes.root}>
                        {this.testData.map((value,index)=>(
                                <MilestoneExpansion key={index}/>
                            ))
                        }
                    </div>
                </Paper>
            </div>
        );
    }
}

MilestoneList.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(MilestoneList);