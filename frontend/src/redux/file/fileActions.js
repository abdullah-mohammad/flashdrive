import { FETCH_FILE_FAILURE, FETCH_FILE_REQUEST, FETCH_FILE_SUCCESS } from './fileActionTypes';
import authService from '../../services/auth.service';

export const fetchFileRequest = () => {
    return {
        type: FETCH_FILE_REQUEST
    }
}

export const fetchFileSuccess = data => {
    return {
        type: FETCH_FILE_SUCCESS,
        payload: data
    }
}

export const fetchFileFailure = error => {
    return {
        type: FETCH_FILE_FAILURE,
        payload: error
    }
}

// pure function
/* export const loginFile = user => {
    return function(dispatch) {
        dispatch(loginFileRequest())
        return authService.login(user.username, user.password).then(
            res => {
                dispatch(loginFileSuccess(res))
                return Promise.resolve(res);
            },
            error => {
                dispatch(loginFileFailure(error.message))
                return Promise.reject(error);
            }
        );
    }
} */