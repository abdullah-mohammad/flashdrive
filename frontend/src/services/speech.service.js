import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/';

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