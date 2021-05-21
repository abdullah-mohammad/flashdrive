import { FETCH_FILE_FAILURE, FETCH_FILE_REQUEST, FETCH_FILE_SUCCESS } from "./fileActionTypes"

const initialState = {
    loading: false,
    files: [
    ],
    error: ''
}

export const fileReducer = (state=initialState, action) => {
    switch (action.type) {
        case FETCH_FILE_REQUEST: return {
            ...state,
            loading: true
        }
        case FETCH_FILE_SUCCESS: 
            return {
                loading: false,
                files: action.payload,
                error: ''
            }
        case FETCH_FILE_FAILURE: return {
            loading: false,
            files: [],
            error: action.payload
        }

        default: return state
    }
}