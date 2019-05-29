import React, {Fragment, Component} from 'react'
import {bindActionCreators} from 'redux'
import {connect} from 'react-redux'
import stompClient from './WebSocket'
import Message from './UserAction/Message'
import StatusMessage from './UserAction/StatusMessage'
import Hand from './Hand/Hand'
import Library from './Library/Library'
import Battlefield from './Battlefield/Battlefield'
import Graveyard from './Graveyard/Graveyard'
import TurnPhases from './Turn/TurnPhases'
import PlayerInfo from './PlayerInfo/PlayerInfo'
import UserAction from './UserAction/UserAction'
import MaximizedCard from './Card/MaximizedCard'
import Stack from './Stack/Stack'


class App extends Component {
  componentDidMount() {
    stompClient.init(this.props.receive)
  }

  render() {
    return (
      <Fragment>
        <MaximizedCard/>
        <UserAction/>
        <StatusMessage/>
        <Message/>
        <Hand type='opponent'/>
        <Hand type='player'/>
        <div id="table">
          <Library type='opponent'/>
          <Battlefield type='opponent'/>
          <Graveyard type='opponent'/>
          <PlayerInfo type='opponent'/>
          <TurnPhases/>
          <Stack/>
          <Library type='player'/>
          <Battlefield type='player'/>
          <Graveyard type='player'/>
          <PlayerInfo type='player'/>
        </div>
      </Fragment>
    )
  }
}

const mapStateToProps = state => {
  return {
    message: state.message
  }
}

const mapDispatchToProps = dispatch => {
  return {
    receive: bindActionCreators((event) => event, dispatch)
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
