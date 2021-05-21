import React, { Component } from 'react'

import Recorder from 'react-mp3-recorder'
import ReactAudioPlayer from 'react-audio-player'

import blobToBuffer from 'blob-to-buffer'

import SpeechService from '../../../services/speech.service'


export default class SpeechToText extends Component {

    state = {
        url: '',
        text:''
      }
    
      render () {
        const {
          url,text
        } = this.state
    
        return (
          <div>
            <div
              style={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                textAlign: 'center',
                minHeight: '100vh'
              }}
            >
              <div>
                <Recorder
                  onRecordingComplete={this._onRecordingComplete}
                  onRecordingError={this._onRecordingError}
                  style={{
                    margin: '0 auto'
                  }}
                />
    
                <p>
                  Click and hold to start recording.
                </p>
    
                {url && (
                  <div>
                    <ReactAudioPlayer
                      src={url}
                      controls
                      style={{
                        minWidth: '500px'
                      }}
                    />
                  </div>
                )}

                {url && (
                  <div className="content-header-wrapper">
                    <div className="actions">
                        <label className="btn btn-success">
                            <button id="my-file-selector" onClick={this._sendAudio} />
                            convert speech to text
                        </label>
                    </div>
                  </div>
                )}

                {text && (
                  <div className="content-header-wrapper">
                    <div className="actions">
                        <label className="btn btn-success">
                            <button id="my-file-selector" onClick={this._sendAudio}>
                            {this.text}
                            </button>
                        </label>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )
      }
    
      _onRecordingComplete = (blob) => {
        blobToBuffer(blob, (err, buffer) => {
          if (err) {
            console.error(err)
            return
          }
    
          console.log('recording', blob)
    
          

          if (this.state.url) {
            window.URL.revokeObjectURL(this.state.url)
          }
    
          this.setState({
            url: window.URL.createObjectURL(blob)
          })
          const formData = new FormData();
          formData.append('file',blob)
          SpeechService.convertSpeechToText(formData).then(
            (res) => {
              console.log(res.data)
              console.log(res.status)
            }
        ).catch(
          (err)=>{
          console.log(err)
        })
        })
      }
    
      _onRecordingError = (err) => {
        console.log('error recording', err)
    
        if (this.state.url) {
          window.URL.revokeObjectURL(this.state.url)
        }
    
        this.setState({ url: null })
      }

      _sendAudio = () => {
        if (this.state.url) {
          
        }
        var str = SpeechService.convertSpeechToText(this.state.url)
        this.text = 'test'
        this.setState({
          text: str
        })
      }
}
