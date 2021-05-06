import axios from "axios";

//const API_URL = "http://localhost:8080/api/auth/";
const API_URL = "http://34.117.49.181/api/auth/";
//const API_URL = "http://flashdrive-cloud.de/api/auth/";

class AuthService {
  login(username, password) {
    return axios
      .post(API_URL + "signin", {
        username,
        password
      })
      .then(response => {
        if (response.data.accessToken) {
          localStorage.setItem("user", JSON.stringify(response.data));
        }

        return response.data;
      });
  }

  logout() {
    localStorage.removeItem("user");
  }

  register(username, password, email, firstname, lastname, gender, address) {
    return axios.post(API_URL + "signup", {
      username,
      password,
      email,
      firstname,
      lastname,
      gender,
      address
    });
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));;
  }
}

export default new AuthService();