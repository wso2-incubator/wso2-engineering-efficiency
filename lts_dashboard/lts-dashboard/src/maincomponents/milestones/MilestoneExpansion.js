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
import ExpansionPanel, {
    ExpansionPanelDetails,
    ExpansionPanelSummary,
} from 'material-ui/ExpansionPanel';
import Typography from 'material-ui/Typography';
import Button from 'material-ui/Button';
import ExpandMoreIcon from 'material-ui-icons/ExpandMore';

const styles=theme => ( {
    nested: {
        paddingLeft: theme.spacing.unit * 4,
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
        flexBasis: '25%',
        flexShrink: 0,
    },
    secondaryHeading: {
        fontSize: theme.typography.pxToRem(15),
        color: theme.palette.text.secondary,
        flexBasis: '50%'
    },
    buttonEdit : {
        float: 'right'
    },
});


class MilestoneExpansion extends React.Component {
    constructor(props){
        super(props);
        this.state = {open: false};
    }

    handleClick = () => {
        this.setState({ open: !this.state.open });
    };

    editButtonClick = () => {
        alert("button clicked");
    }


    render() {
        const {classes} = this.props;
        return (
            <ExpansionPanel expanded={this.state.open} >
                <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />} onClick={this.handleClick}>
                    <Typography className={classes.heading}>Milestone Name</Typography>
                    <Typography className={classes.secondaryHeading}>
                        Current version tag in git
                    </Typography>
                    <Button color="accent" className={classes.button} onClick={this.editButtonClick}>
                        Edit
                    </Button>
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    <Typography>
                        <ul>
                            <li>Feature 1</li>
                            <li>Feature 2</li>
                            <li>Feature 3</li>
                        </ul>
                    </Typography>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        );
    }
}


MilestoneExpansion.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(MilestoneExpansion);