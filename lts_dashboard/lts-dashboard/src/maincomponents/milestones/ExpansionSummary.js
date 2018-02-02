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
import ExpansionPanel, {ExpansionPanelDetails, ExpansionPanelSummary,} from 'material-ui/ExpansionPanel';
import Typography from 'material-ui/Typography';
import ExpandMoreIcon from 'material-ui-icons/ExpandMore';
import List from "material-ui/es/List/List";
import ListItem from "material-ui/es/List/ListItem";
import ListItemIcon from "material-ui/es/List/ListItemIcon";
import ListItemText from "material-ui/es/List/ListItemText";
import StarIcon from 'material-ui-icons/Star';


const styles = theme => ({
    root: {
        width: '100%',
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
        flexBasis: '30%',
        flexShrink: 0,
        color: theme.palette.primary.main
    },
    secondaryHeading: {
        fontSize: theme.typography.pxToRem(15),
        color: theme.palette.text.secondary,
    },
    featureList: {
        width: '100%',
        maxWidth: 360,
        backgroundColor: theme.palette.background.paper,
    },
    column: {
        flexBasis: '50%',
    },
});

class ExpansionSummary extends React.Component {
    state = {
        expanded: true,
    };

    handleChange = () => {
        this.setState({
            expanded: !this.state.expanded,
        });
    };


    generateFeatures(array) {
        return array.map((value, index) =>
            <ListItem button key={index}>
                <ListItemIcon>
                    <StarIcon/>
                </ListItemIcon>
                <ListItemText
                    inset primary={value}/>
            </ListItem>
        );
    }

    render() {
        const {classes} = this.props;
        const {expanded} = this.state;

        return (
            <ExpansionPanel expanded={expanded}>
                <ExpansionPanelSummary onClick={this.handleChange} expandIcon={<ExpandMoreIcon/>}>
                    <Typography className={classes.heading}>{this.props.data["title"]}</Typography>
                    <Typography className={classes.secondaryHeading}>{this.props.data["html_url"]}</Typography>
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    <div className={classes.column}>
                        <List className={classes.root} dense={true}>
                            {this.generateFeatures(this.props.data["features"])}
                        </List>
                    </div>
                </ExpansionPanelDetails>
                {/*<Divider />*/}
                {/*<Button onClick={() => window.open(this.props.data["html_url"], '_blank')} dense color="secondary">*/}
                {/*Change Version*/}
                {/*</Button>*/}
            </ExpansionPanel>

        );
    }
}

ExpansionSummary.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(ExpansionSummary);