import React, { useEffect, useState } from 'react';
import { FlatList, StyleSheet, Text, SafeAreaView } from 'react-native';

import ReceiveSharingIntent from 'react-native-receive-sharing-intent';

const App = () => {
    const [files, setFiles] = useState([]);
    
  useEffect(()=>{
    ReceiveSharingIntent.getReceivedFiles(files => {
      setFiles(files)
        console.log(files);
    }, 
    (error) =>{
      console.log(error);
    });
    
    return () => {
      ReceiveSharingIntent.clearReceivedFiles();
    }
  },[]);
  
  return (
    <SafeAreaView style={styles.container}>
    <Text style={{fontSize:28,fontWeight:"bold"}}> Shared Files</Text>
    <FlatList
      data={files}
      renderItem={({item}) => <Text style={styles.item}>{item.fileName ? item.fileName : item.weblink}</Text>}
    />
  </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
   flex: 1,
   paddingTop: 22
  },
  item: {
    padding: 10,
    fontSize: 18,
    height: 44,
  },
})

export default App;
