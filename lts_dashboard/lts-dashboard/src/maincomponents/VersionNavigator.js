import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import List, { ListItem, ListItemText } from 'material-ui/List';
import Paper from 'material-ui/Paper';
import Typography from 'material-ui/Typography';
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import VersionItem from './version/VersionItem.js';



const styles = theme => ({
    root: {
        width: '100%',
        textAlign: 'left',
        background: theme.palette.background.paper,
    },

    title: {
        marginBottom: 16,
        fontSize: 14,
        color: theme.palette.text.secondary,
    },
    paper: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
        marginTop: theme.spacing.unit * 3,
        width : 400
    }),
    appBar : {
        paddingBottom:20
    }
});



class VersionNavigator extends React.Component {
    constructor(){
        super();
        this.state = {
                'APIM-100': false,
                'APIM-200': false,
                'APIM-300': false,
                'APIM-400': false

            };
    }

    testdata = [
        {
            main_version : "1.0.0",
            main_key : "APIM-100",
            sub_versions : [
                "1.0.1","1.0.2"
            ]
        },
        {
            main_version : "2.0.0",
            main_key: "APIM-200",
            sub_versions : [
                "2.0.1","2.0.2"
            ]
        },
        {
            main_version : "3.0.0",
            main_key: "APIM-300",
            sub_versions : [
                "3.0.1","3.0.2"
            ]
        },
        {
            main_version : "4.0.0",
            main_key: "APIM-400",
            sub_versions : [
                "4.0.1","4.0.2"
            ]
        }
    ]

    handleClick = (version) => {
        let versionState = this.state[version.toString()];
        let obj = {};
        obj[version] = !versionState;
        // this.setState(obj);
    };



    makeStateForClick = (version) => {
        this.setState({
                [version]: false
            }
        );
    };



    render() {
        const { classes } = this.props;
        return (
            <div>
                <Paper className={classes.paper} elevation={4}>
                    <div className={classes.appBar}>
                        <AppBar position="static" color="default">
                            <Toolbar >
                                <Typography type="headline" component="h2">
                                    Versions
                                </Typography>
                            </Toolbar>
                        </AppBar>
                    </div>



                    <List className={classes.root}>
                        <ListItem button>
                            <ListItemText inset primary="To be released" />
                        </ListItem>

                        {
                            this.testdata.map((version)=>(
                               <VersionItem key={version["main_key"]} versionData={version}/>
                            ))
                        }

                    </List>
                </Paper>
            </div>
        );
    }
}



VersionNavigator.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(VersionNavigator);