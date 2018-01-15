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


const styles = theme => ({
    root: {
        width: '100%',
        maxWidth: 360,
        background: theme.palette.background.paper,
    },
});

//test data
function getData(){
    return ({
        array :[
            {
                product_name : "API Manager",
                product_id : "APIM"
            },
            {
                product_name : "IoT",
                product_id : "IOT"
            },
            {
                product_name : "Enterprise Integrator",
                product_id : "EPING"
            }
        ]
    });
}



function ProductList(props) {
    const { classes } = props;
    let data = getData()["array"];
    return (
        <List className={classes.root}>
            {
                    data.map( (name,index) => (
                        <ListItem key={name["product_id"]} button>
                            <ListItemText inset primary={name["product_name"]} />
                        </ListItem>
                    ))
            }

        </List>
    );
}




ProductList.propTypes = {
    classes: PropTypes.object.isRequired,
};


export default withStyles(styles)(ProductList);