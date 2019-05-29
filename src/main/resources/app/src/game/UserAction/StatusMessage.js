import React, {Component} from 'react'
import {connect} from 'react-redux'
import PropTypes from 'prop-types'

class StatusMessage extends Component {
  render() {
    return (
      <div id='status-message'>
        {this.props.statusMessage}
      </div>
    )
  }
}

const mapStateToProps = state => {
  return {
    statusMessage: state.statusMessage
  }
}

StatusMessage.propTypes = {
  statusMessage: PropTypes.string
}

export default connect(mapStateToProps)(StatusMessage)