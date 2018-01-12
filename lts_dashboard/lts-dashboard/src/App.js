import React, { Component } from 'react';
import logo from './img/WSO2_Software_Logo.png'
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import './App.css';
import AppBar from './maincomponents/AppBar';
import VersionNavigator from './maincomponents/VersionNavigator';
import MilestoneList from './maincomponents/MilestoneList';

const styles = theme => ({
    blocks : {
        display : 'inline',
        float : 'left',
        padding: 20
    }


});

class App extends Component {
  render() {
    const { classes } = this.props;
    return (
      <div className="App">

        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">LTS Dashboard</h1>
        </header>

        <AppBar/>
        <div style={{height:15}}></div>
        <div >
            <div className={classes.blocks}>
                <VersionNavigator/>
            </div>
            <div className={classes.blocks}>
                <MilestoneList/>
            </div>
        </div>
      </div>
    );
  }
}

App.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default  withStyles(styles)(App);
