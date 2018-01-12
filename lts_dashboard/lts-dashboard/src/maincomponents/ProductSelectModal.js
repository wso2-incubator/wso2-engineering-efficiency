import React from 'react';
import Modal from 'material-ui/Modal';
import Button from 'material-ui/Button';
import ModalBar from './productlist/ModalBar';
import ProductList from './productlist/ProductList';



function getModalStyle() {

    return {
        position: 'absolute',
        width: 8 * 50,
        top: `10%`,
        left: `30%`,
        border: '1px solid #e5e5e5',
        backgroundColor: '#fff',
        boxShadow: '0 5px 15px rgba(0, 0, 0, .5)'
    };
}


class SimpleModal extends React.Component {
    constructor(props){
        super(props);
        this.handleOpen = this.handleOpen.bind(this);
    }
    state = {
        open: false,
    };

    handleOpen = () => {
        this.setState({ open: true });
    };

    handleClose = () => {
        this.setState({ open: false });
    };



    render() {
        return (
            <div>
                <div>
                    <Button raised onClick={this.handleOpen}>Change Product</Button>
                </div>

                <Modal
                    aria-labelledby="simple-modal-title"
                    aria-describedby="simple-modal-description"
                    open={this.state.open}
                    onClose={this.handleClose}
                >
                    <div style={getModalStyle()}>
                        <ModalBar/>
                        <div style={{height:15}}></div>
                        <ProductList/>
                    </div>
                </Modal>
            </div>
        );
    }
}

export default SimpleModal;