'use strict';

import React, {
  PropTypes,
  requireNativeComponent,
} from 'react-native';

export default class BarcodeReaderView extends Component {
  static propTypes = {
    onBarCodeRead: PropTypes.func,
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
  nativeOnly: {onChange: true}
});
