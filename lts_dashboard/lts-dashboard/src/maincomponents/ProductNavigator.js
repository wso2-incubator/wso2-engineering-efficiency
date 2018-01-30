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
import Input, {InputLabel} from 'material-ui/Input';
import {MenuItem} from 'material-ui/Menu';
import {FormControl} from 'material-ui/Form';
import Select from 'material-ui/Select';
import axios from "axios/index";

const styles = theme => ({
    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    formControl: {
        margin: theme.spacing.unit,
        minWidth: 400,
    },
    selectEmpty: {
        marginTop: theme.spacing.unit * 2,
    },
});

class ProductNavigator extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            product: '',
            productList: []
        };

        this.fetchVersions();

    }

    handleChange = event => {
        this.setState({[event.target.name]: event.target.value},
            () => {
                this.props.setProduct(this.state.product)
            });

    };

    fetchVersions() {
        axios.get('http://10.100.5.173:8080/lts/products'
        ).then(
            (response) => {
                let datat = response.data;
                this.setState(
                    {
                        productList: datat
                    }
                );
            }
        )
    }


    render() {
        const {classes} = this.props;

        return (
            <form className={classes.container} autoComplete="off">
                <FormControl className={classes.formControl}>
                    <InputLabel htmlFor="product-simple">Product</InputLabel>
                    <Select
                        value={this.state.product}
                        onChange={this.handleChange}
                        input={<Input name="product" id="product-simple"/>}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {this.state.productList.map((productName, index) => (
                            <MenuItem key={index} value={productName}>{productName}</MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </form>
        );
    }
}

ProductNavigator.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(ProductNavigator);