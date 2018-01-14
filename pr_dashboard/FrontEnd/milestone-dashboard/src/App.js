import React, { Component } from 'react';
import logo from './img/WSO2_Software_Logo.png';
import './App.css';
import DataTable from './maincomponents/DataTable.js';
import WeekNavigator from './maincomponents/WeekNavigator'
import {Row,Col,Button} from 'react-bootstrap'
import 'react-infinite-calendar/styles.css';
import axios from "axios/index";
import {getWeekDates} from "./support/datepickers";

class App extends Component {
    constructor(props){
        super(props);
        let today = new Date();
        let stringDate = today.getFullYear()+"-"+(today.getMonth()+1)+"-"+today.getDate();
        let filterDates =  getWeekDates(stringDate)
        this.state={
            checkDate:{
                startDate:filterDates[0],
                endDate: filterDates[1]
            },
            pure_data:[]
        };
        this.setCheckDate = this.setCheckDate.bind(this);
        this.fetchMilestone();
    }



    setCheckDate(startDate,endDate){
        this.setState(
            {
                checkDate:{
                    startDate : startDate,
                    endDate : endDate
                }
            }
        );

    }



    fetchMilestone(){
        axios.get('http://localhost:8080/service'
        ).then(
            (response) => {
                let datat = response.data;
                this.setState(
                    {
                        pure_data : datat
                    }
                );
            }
        )

    }

  render() {
    return (
      <div className="App">

          {/*app header*/}
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Weekly Release Dashboard</h1>
        </header>
        <div className="start-space"></div>

        <Row>

            <Col lg={2}>

                 {/*<WeekNavigator/>*/}
                 <WeekNavigator
                    pureData={this.state.pure_data}
                    setDate={this.setCheckDate}
                 />

            </Col>

            <Col lg={10}>
              {/*Table*/}
                <DataTable changeDate ={this.state.checkDate} pureData={this.state.pure_data}/>
            </Col>
        </Row>

      </div>
    );
  }
}

export default App;
