import axios from 'axios';
import authHeader from './auth-header';

//const API_URL = 'http://localhost:8080/api/';
const API_URL = 'http://34.117.49.181/api/';

class SpeechService {
  convertSpeechToText(data) {
    return axios.post(API_URL + 'speech',
    data,
    {
      'Content-Type': 'multipart/form-data'
    });
  }
}

export default new SpeechService();