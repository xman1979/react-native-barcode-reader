'use strict';

import React, {
  PropTypes,
  Component,
  View,
  requireNativeComponent,
} from 'react-native';

export default class BarcodeReaderView extends Component {
  static propTypes = {
    ...View.propTypes,
    onBarCodeRead: PropTypes.func,
    rotation: PropTypes.number,
    scaleX: PropTypes.number,
    scaleY: PropTypes.number,
    translateX: PropTypes.number,
    translateY: PropTypes.number,
  }

  constructor(props) {
    super(props);
    this.onChange = this.onChange.bind(this);
  }

  onChange(event) {
    if (!this.props.onBarCodeRead) {
      return;
    }

    this.props.onBarCodeRead({
      type: event.nativeEvent.type,
      data: event.nativeEvent.data,
    });
  }

  render() {
    return (
      <RCTBarcodeReaderView {...this.props} onChange={this.onChange} />
    );
  }
}
let RCTBarcodeReaderView = requireNativeComponent('RCTBarcodeReaderView', BarcodeReaderView, {
  nativeOnly: {
    onChange: true
  }
});
