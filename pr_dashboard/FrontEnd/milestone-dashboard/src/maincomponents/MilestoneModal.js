import React from 'react';
import MilestoneTable from './MilestoneTable.js'
import { Button ,Modal,} from 'react-bootstrap';
import MilestoneHeader from '../cellcomponentes/MilestoneHeader.js'



class MilestoneModal extends React.Component{
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Modal {...this.props} bsSize="lg" aria-labelledby="contained-modal-title-sm">
                <Modal.Header closeButton>
                    <Modal.Title id="contained-modal-title-lg">
                        <MilestoneHeader data={this.props.milestone}/>
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <MilestoneTable  array={this.props.milestone["issues"]}/>
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={this.props.onHide}>Close</Button>
                </Modal.Footer>
            </Modal>
        );
    }
}

export default MilestoneModal;

