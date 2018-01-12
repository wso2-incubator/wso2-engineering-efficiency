import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import Typography from 'material-ui/Typography';
import SimpleModal from './ProductSelectModal';
import {Row,Col} from 'react-bootstrap'


const styles = {
    root: {
        width: '100%',
    },
    header: {
        paddingRight:20
    }
};


function HeaderAppBar(props) {
    const { classes } = props;
    return (
        <div className={classes.root}>
            <AppBar position="static" color="default">
                <Toolbar>
                    <div className={classes.header}>
                        <Typography type="title" color="inherit">
                            API Manager
                        </Typography>
                    </div>
                    <SimpleModal/>
                </Toolbar>
            </AppBar>
            <Row>
                <Col xs={3}>

                </Col>
                <Col xs={9}>
                </Col>
            </Row>

        </div>
    );
}

HeaderAppBar.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(HeaderAppBar);