/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import React, {
  AppRegistry,
  Component,
  StyleSheet,
  Text,
  View,
} from 'react-native';

import BarcodeReaderView from 'react-native-barcode-reader';

class BarcodeReaderExample extends Component {
  constructor(props) {
    super(props);
    this.state = {
      read: false,
      content: '',
    }
  }
  onBarCodeRead(data) {
    console.log(data);
    this.setState({read: true, content: data.data});
  }
  resetToRead() {
    this.setState({read: false, content:''});
  }
  render() {
    let content;
    if (this.state.read) {
      content = (
        <View style={styles.textView}>
          <Text style={styles.text}>{this.state.content}</Text>
          <Text style={styles.text} onPress={this.resetToRead.bind(this)}>Press to try again?</Text>
        </View>
        );
    }
    else {
      content = (
         <BarcodeReaderView onBarCodeRead={this.onBarCodeRead.bind(this)} style={styles.camera}/>
       );
    }

    return  (<View style={styles.container}>{content}</View>);
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  textView: {
    flex: 1,
  },
  text: {
    flex: 1,
    fontSize: 24,
  },
  camera: {
    width: 300,
    height: 300,
  },
});

AppRegistry.registerComponent('BarcodeReaderExample', () => BarcodeReaderExample);
