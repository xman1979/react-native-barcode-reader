# react-native-barcode-reader [![npm version](https://badge.fury.io/js/react-native-barcode-reader.svg)]

A barcode reader for React Native, using google-play-service. 
Suport only android, for iOS you can use react-native-camera.

It is more stable and license friendly comparing with the ones using zxing implementation. 

**NOTE** These docs are for the work in progress v1 release. 

## Known Issues (To be fixed)
1. Need to give [width height], [flex: 1] not working yet.
2. Only works with portrait, lanscape mode doesn't work yet.
3. Barcode type is defined in native returns number, to be converted to the standard name in string.

## Getting started
#### Android
1. `npm install react-native-barcode-reader@https://github.com/maxiaodong97/react-native-barcode-reader.git --save`
2. Open up `android/app/src/main/java/[...]/MainActivity.java
  - Add `import com.maxiadoong97.barcode.*;` to the imports at the top of the file
  - Add `new BarcodeReader()` to the list returned by the `getPackages()` method

3. Append the following lines to `android/settings.gradle`:

	```
include ':RCTBarcodeReaderAndroid'
project(':RCTBarcodeReaderAndroid').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-barcode-reader/RCTBarcodeReaderAndroid')
	```

4. Insert the following lines inside the dependencies block in `android/app/build.gradle`:

	```
    compile project(':RCTBarcodeReaderAndroid')
	```

## Usage

All you need is to `require` the `react-native-barcode-reader` module and then use the
`<BarcodeReaderView/>` tag.

```javascript

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


```

## Properties


#### `onBarCodeRead`

The barcode type is provided in the `data` object.
