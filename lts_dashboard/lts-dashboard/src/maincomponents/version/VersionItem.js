import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import List, { ListItem, ListItemText } from 'material-ui/List';
import Divider from 'material-ui/Divider';
import Collapse from 'material-ui/transitions/Collapse';
import ExpandLess from 'material-ui-icons/ExpandLess';
import ExpandMore from 'material-ui-icons/ExpandMore';

const styles=theme => ( {
    nested: {
        paddingLeft: theme.spacing.unit * 4,
    },
});


class VersionItem extends React.Component {
    constructor(props){
        super(props);
        this.state = {open: false};
    }

    handleClick = () => {
        this.setState({ open: !this.state.open });
    };


    render() {
        const {classes} = this.props;
        return (
            <div>
                <ListItem button onClick={this.handleClick}>
                    <ListItemText inset primary={this.props.versionData["main_version"]}/>
                    {this.state.open ? <ExpandLess/> : <ExpandMore/>}
                </ListItem>
                <Divider/>
                {
                    this.props.versionData["sub_versions"].map((subVersion,index) => (
                        <Collapse key={index} component="li" in={this.state.open} timeout="auto" unmountOnExit>
                            <List disablePadding>
                                <ListItem button className={classes.nested}>
                                    <ListItemText inset primary={subVersion}/>
                                </ListItem>
                            </List>
                            <Divider/>
                        </Collapse>
                    ))
                }
                <Divider/>
            </div>
        );
    }
}

VersionItem.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(VersionItem);