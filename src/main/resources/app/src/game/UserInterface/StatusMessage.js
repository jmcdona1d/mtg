import get from 'lodash/get'
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
    statusMessage: get(state, 'userInterface.statusMessage', '')
  }
}

StatusMessage.propTypes = {
  statusMessage: PropTypes.string.isRequired
}

export default connect(mapStateToProps)(StatusMessage)