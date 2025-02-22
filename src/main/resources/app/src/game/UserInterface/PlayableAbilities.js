import React, {Component} from 'react'
import {connect} from 'react-redux'
import {bindActionCreators} from 'redux'
import get from 'lodash/get'
import PropTypes from 'prop-types'

class PossibleAbility extends Component {
  static renderColors(colors) {
    return colors.map((color, index) => <img key={index} src={`/img/symbols/${color}.png`} alt={color} />)
  }

  render() {
    switch (this.props.possibleAbility.abilityType) {
      case 'TAP_ADD_MANA':
        const colors = this.props.possibleAbility.parameters
        return <li onClick={this.props.onClick} title={`TAP: add ${colors}`}><img src='/img/symbols/TAP.png' alt='TAP' />: add {PossibleAbility.renderColors(colors)}</li>
      default:
        return <li/>
    }
  }
}

PossibleAbility.propTypes = {
  possibleAbility: PropTypes.object.isRequired,
  onClick: PropTypes.func.isRequired
}

class PlayableAbilities extends Component {
  constructor(props) {
    super(props)
    this.closePlayableAbilitiesOverlay = this.closePlayableAbilitiesOverlay.bind(this)
    this.playAbility = this.playAbility.bind(this)
  }

  playAbility(index) {
    this.props.playAbilitiesAction(this.props.cardId, index)
  }

  closePlayableAbilitiesOverlay() {
    this.props.closePlayableAbilitiesOverlay()
  }

  render() {
    if (this.props.possibleAbilities.length > 1) {
      return (
        <div id='playable-abilities-overlay' onClick={this.closePlayableAbilitiesOverlay}>
          <div id='playable-abilities' style={{left: this.props.position.x + 'px', top: (this.props.position.y - 150) + 'px'}}>
            <ul>
              { this.props.possibleAbilities.map((possibleAbility, index) =>
                <PossibleAbility key={index} possibleAbility={possibleAbility} onClick={() => this.playAbility(index)} />) }
            </ul>
          </div>
        </div>
      )
    } else {
      return null
    }
  }
}

const closePlayableAbilitiesAction = () => {
  return {
    type: 'CLOSE_PLAYABLE_ABILITIES_CLICK'
  }
}

const playAbilitiesAction = (cardId, index) => {
  return {
    type: 'PLAY_ABILITIES_CLICK',
    cardId: cardId,
    index: index
  }
}

const mapStateToProps = state => {
  return {
    cardId: get(state, 'userInterface.playableAbilities.cardId', -1),
    possibleAbilities: get(state, 'userInterface.playableAbilities.possibleAbilities', []),
    position: get(state, 'userInterface.playableAbilities.position', {x: -1, y: -1})
  }
}

const mapDispatchToProps = dispatch => {
  return {
    closePlayableAbilitiesOverlay: bindActionCreators(closePlayableAbilitiesAction, dispatch),
    playAbilitiesAction: bindActionCreators(playAbilitiesAction, dispatch)
  }
}

PlayableAbilities.propTypes = {
  cardId: PropTypes.number.isRequired,
  possibleAbilities: PropTypes.array.isRequired,
  position: PropTypes.object.isRequired,
}


export default connect(mapStateToProps, mapDispatchToProps)(PlayableAbilities)