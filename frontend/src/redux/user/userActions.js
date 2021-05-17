import { 
    FETCH_USER_REQUEST, FETCH_USER_SUCCESS, FETCH_USER_FAILURE, 
    LOGIN_USER_REQUEST, LOGIN_USER_SUCCESS, LOGIN_USER_FAILURE 
} from './userActionTypes';
import axios from 'axios';
import authService from '../../services/auth.service';

export const fetchUserRequest = () => {
    return {
        type: FETCH_USER_REQUEST
    }
}

export const fetchUserSuccess = data => {
    return {
        type: FETCH_USER_SUCCESS,
        payload: data
    }
}

export const fetchUserFailure = error => {
    return {
        type: FETCH_USER_FAILURE,
        payload: error
    }
}


/**
 * LOGIN ACTIONS 
 */
export const loginUserRequest = () => {
    return {
        type: LOGIN_USER_REQUEST
    }
}

export const loginUserSuccess = connectedUser => {
    return {
        type: LOGIN_USER_SUCCESS,
        payload: connectedUser
    }
}

export const loginUserFailure = error => {
    return {
        type: LOGIN_USER_FAILURE,
        payload: error
    }
}


/**
 * PURE ACTION FUNCTIONS 
 */

/* export const fetchUser = (dataType,searchTerm="") => {
    return function(dispatch) {
        const url = dataType === PEOPLE ? API_PEOPLE
                    : dataType === PLANETS ? API_PLANETS
                    : API_STARSHIPS
        dispatch(fetchUserRequest())
        axios.get(`${url}?search=${searchTerm}`)
            .then(res => {
                dispatch(fetchUserSuccess(res.data.results))
            })
            .catch(err => {
                dispatch(fetchUserFailure(err.message))
            })
    }
} */

export const loginUser = user => {
    return function(dispatch) {
        dispatch(loginUserRequest())
        return authService.login(user.username, user.password).then(
            res => {
                dispatch(loginUserSuccess(res))
                return Promise.resolve(res);
            },
            error => {
                dispatch(loginUserFailure(error.message))
                return Promise.reject(error);
            }
        );
    }
}