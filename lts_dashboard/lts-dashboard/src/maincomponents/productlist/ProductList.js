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