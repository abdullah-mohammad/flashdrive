import axios from 'axios';
import authHeader from './auth-header';

//const API_URL = 'http://localhost:8080/api/';
const API_URL = 'http://34.117.49.181/api/';

class UserService {
  getPublicContent() {
    return axios.get(API_URL + 'all');
  }

  getUserBoard() {
    return axios.get(API_URL + 'user', { headers: authHeader() });
  }
}

export default new UserService();