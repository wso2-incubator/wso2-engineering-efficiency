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
import List, {ListItem, ListItemText} from 'material-ui/List';
import Divider from 'material-ui/Divider';
import Collapse from 'material-ui/transitions/Collapse';
import ExpandLess from 'material-ui-icons/ExpandLess';
import ExpandMore from 'material-ui-icons/ExpandMore';

const styles = theme => ({
    nested: {
        paddingLeft: theme.spacing.unit * 4,
    },
});


class VersionItem extends React.Component {
    handleClick = () => {
        this.setState({open: !this.state.open});
    };

    constructor(props) {
        super(props);
        this.state = {open: false};
    }

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
                    this.props.versionData["sub_versions"].map((subVersion, index) => (
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