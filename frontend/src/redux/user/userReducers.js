import { 
    FETCH_USER_REQUEST, FETCH_USER_SUCCESS, FETCH_USER_FAILURE,
    LOGIN_USER_FAILURE, LOGIN_USER_REQUEST, LOGIN_USER_SUCCESS 
} from './userActionTypes';

const initialState = {
    loading: false,
    users: [],
    currentUser: undefined,
    error: ''
}

export const userReducer = (state=initialState, action) => {
    switch (action.type) {
        case FETCH_USER_REQUEST: return {
            ...state,
            loading: true
        }
        case FETCH_USER_SUCCESS: 
            return {
                loading: false,
                users: action.payload,
                error: ''
            }
        case FETCH_USER_FAILURE: return {
            loading: false,
            users: [],
            error: action.payload
        }

        case LOGIN_USER_REQUEST: return {
            ...state,
            loading: true
        }
        case LOGIN_USER_SUCCESS: 
            return {
                loading: false,
                currentUser: action.payload,
                error: ''
            }
        case LOGIN_USER_FAILURE: return {
            loading: false,
            currentUser: [],
            error: action.payload
        }

        default: return state
    }
}