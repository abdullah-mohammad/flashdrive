import { FETCH_FILE_FAILURE, FETCH_FILE_REQUEST, FETCH_FILE_SUCCESS } from './fileActionTypes';
import authService from '../../services/auth.service';
import fileService from '../../services/file.service';

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
export const uploadFile = file => {
    return function(dispatch) {
        dispatch(fetchFileRequest())
        const bucketName = authService.getCurrentUser().username
        var data = new FormData();
        data.append("username", bucketName.toLowerCase());
        data.append("file", file);
        console.log("HOWER; ", data.username)
        return fileService.uploadFile(data).then(
            res => {
                console.log("RESPONSE: ", res.data)
                dispatch(fetchFileSuccess(res))
                return Promise.resolve(res);
            },
            error => {
                dispatch(fetchFileFailure(error.message))
                return Promise.reject(error);
            }
        );
    }
}