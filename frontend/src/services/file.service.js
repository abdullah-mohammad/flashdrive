import axios from 'axios';
import authHeader from './auth-header';

//const API_URL = 'http://localhost:8080/api/';
const API_URL = 'http://34.117.49.181/api/';

class FileService {
    /* get(id) {
        return http.get(`/items/${id}`);
    } */
    uploadFile(data) {
        return axios.post(API_URL + 'upload', data, { headers: authHeader() });
    }

    getFiles(username) {
        return axios.get(`${API_URL}all/${username}`);
    }

}

export default new FileService();